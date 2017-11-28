import net.liftweb.json._


/*
 * Extracts and reorganises data related to
 * patients and test results from their raw form.
 */
object LabResultsProcessor {

  // COLUMN INDICES OF CSV RESULTS
  private val HOSP_ID = 0
  private val SAMPLE = 1
  private val DATE = 2
  private val PROFILE_NAME = 3
  private val PROFILE_CODE = 4
  private val CODE_VAL_START = 5
  private val CODE_VAL_END = 30
  private val TEST_NAME = 30
  private val UNIT = 31
  private val LOWER = 32
  private val UPPER = 33
  // =============================
  val UNKNOWN_NUMERICAL_PLACEHOLDER = "-1.0"

  // For lift-json operations
  implicit val formats = DefaultFormats

  // Main API -----------------------------------------------------------------
  def generateCodeValueMap(resCols: List[String]): Map[String, String] = {
    resCols.collect { case res if res != "" =>
      val values = res.split("~", -1)
      (values(0), values(1))
    }.toMap
  }

  def buildFormattedLabResults(records: List[Record],
                               codesTable: Map[String, (String, String)],
                               patients: List[Patient]): FormattedLabResults = {
    val byHospId = records.groupBy(_.hospId)
    val patientRecords = patients.map { patient =>
      val relevantRecords = patient.identifiers.flatMap { ident => byHospId.getOrElse(ident, List()) }
      val patientResults = assembleLabResults(relevantRecords, codesTable)
      PatientRecord(patient.id, patient.firstName, patient.lastName, patient.dateOfBirth, patientResults)
    }
    FormattedLabResults(patientRecords)
  }

  // Generates map (key -> (code, description)) [related to labresults-codes]
  // PRE: The first line is a headline
  def generateCodesTable(lines: List[String]): Map[String, (String, String)] = {
    lines.tail.map { line =>
      val cols = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1).map(_.trim).toList
      (cols(0).trim, (cols(1).trim, cleanStringValue(cols(2).trim)))
    }.toMap
  }

  // PRE: The first line is a headline
  def assembleRecords(lines: List[String]): List[Record] = {
    lines.tail.map { line =>
      val cols = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1).map(_.trim).toList
      val testName = cols(TEST_NAME)
      val codeValueMap = generateCodeValueMap(cols.slice(CODE_VAL_START, CODE_VAL_END))
      val formattedDate = DateUtils.dateToISO8601(cols(DATE))
      Record(cols(HOSP_ID), cols(SAMPLE), formattedDate, cleanStringValue(cols(PROFILE_NAME)),
      cleanStringValue(cols(PROFILE_CODE)), codeValueMap.getOrElse(testName, "UNKNOWN_VALUE"), cols(TEST_NAME),
      cols(UNIT), padNumerical(cols(LOWER)).toDouble, padNumerical(cols(UPPER)).toDouble)
    }
  }

  def processPatients(jsonPatients: String): List[Patient] = {
    val json = parse(jsonPatients)
    json.extract[List[Patient]]
  }

  // UTILS ---------------------------------------------------------------------------------------

  // Removing escape slashes from the entries
  private def cleanStringValue(sVal: String): String = {
    sVal.replaceAll("\"","")
  }

  // This is an arbitrary choice dictated by necessity of having
  // a number for the lower and upper values.
  // -1 is adopted as numerical placeholder.
  private def padNumerical(strNumber: String): String = {
    strNumber match {
      case "" => UNKNOWN_NUMERICAL_PLACEHOLDER
      case _  => strNumber
    }
  }

  private def cleanOutput(res: String): String = {
    res.replaceAll(UNKNOWN_NUMERICAL_PLACEHOLDER, "null")
  }

  // HELPERS --------------------------------------------------------------------------------------

  def assembleLabResults(records: List[Record], codesTable: Map[String, (String, String)]): List[LabResult] = {
    val bySampleId = records.groupBy(_.sampleId).toList
    bySampleId.map {
      case (sample, currRecords) =>
        val firstRecord = currRecords.head
        val singleTestResults = assembleSingleTestResults(currRecords, codesTable)
        LabResult(firstRecord.date, Profile(firstRecord.profName, firstRecord.profCode), singleTestResults)
    }
  }

  def assembleSingleTestResults(records: List[Record],
  profileCodesTable: Map[String, (String, String)]): List[SingleTestResult] = {
    records.map { record =>
      val (code, desc) = profileCodesTable.getOrElse(record.testName, ("UNKNOWN_CODE", "UNKNOWN_VALUE"))
      SingleTestResult(code, desc, record.value, record.unit, record.lower, record.upper)
    }
  }


  def produceJSON(formattedResult: FormattedLabResults): String = {
    import net.liftweb.json.Serialization.writePretty
    cleanOutput(writePretty(formattedResult))
  }
}


