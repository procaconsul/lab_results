import net.liftweb.json._


/*
 * Extracts and reorganises data related to
 * patients and test results from their raw form.
 */
object LabResultsProcessor {

  // COLUMN INDICES OF CSV RESULTS
  val HOSP_ID = 0
  val SAMPLE = 1
  val DATE = 2
  val PROFILE_NAME = 3
  val PROFILE_CODE = 4
  val CODE_VAL_START = 5
  val CODE_VAL_END = 30
  val TEST_NAME = 30
  val UNIT = 31
  val LOWER = 32
  val UPPER = 33
  // =============================

  // For lift-json operations
  implicit val formats = DefaultFormats

  def generateCodeValueMap(resCols: List[String]): Map[String, String] = {
    resCols.collect { case res if res != "" =>
      val values = res.split("~", -1)
      (values(0), values(1))
    }.toMap
  }

  // Generates map (key -> (code, description)) [related to labresults-codes]
  def generateCodesTable(lines: List[String]): Map[String, (String, String)] = {
    lines.tail.map { line =>
      val cols = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1).map(_.trim).toList
      (cols(0).trim, (cols(1).trim, cleanStringValue(cols(2).trim)))
    }.toMap
  }

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

  // Remove the \" value used to surround value
  // involving commas in csv file
  def cleanStringValue(sVal: String): String = {
    sVal.replaceAll("\"","")
  }

  // Default value for missing lower/upper entries
  def padNumerical(strNumber: String): String = {
    "0" + strNumber
  }

  def processPatients(jsonPatients: String): List[Patient] = {
    val json = parse(jsonPatients)
    json.extract[List[Patient]]
  }

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

  def produceJSON(formattedResult: FormattedLabResults): String = {
    import net.liftweb.json.Serialization.writePretty
    writePretty(formattedResult)
  }
}


