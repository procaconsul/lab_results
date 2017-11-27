
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

  def generateProfCodeToValueMap(resCols: List[String]): Map[String, String] = {
    resCols.collect { case res if res != "" =>
      val values = res.split("~")
      (values(0), values(1))
    }.toMap
  }

  def processResultCodes(filePath: String): Map[String, (String, String)] = {
    val bufferedSrc = io.Source.fromFile(filePath)
    generateProfileToCodeDescriptionMap(bufferedSrc.getLines.toList)
  }

  // Assumes presence of header
  def generateProfileToCodeDescriptionMap(lines: List[String]): Map[String, (String, String)] = {
    lines.tail.map { line =>
      val cols = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)").map(_.trim).toList
      (cols(0).trim, (cols(1).trim, cols(2).trim))
    }.toMap
  }

  // Assumes presence of header
  def processLines(lines: List[String]): List[Record] = {
    lines.tail.map { line =>
      val cols = line.split(",").map(_.trim).toList
      val testName = cols(TEST_NAME)
      val codeValueMap = generateProfCodeToValueMap(cols.slice(CODE_VAL_START, CODE_VAL_END))
      val formattedDate = FormatUtils.dateToISO8601(cols(DATE))
      Record(cols(HOSP_ID), cols(SAMPLE), formattedDate, cols(PROFILE_NAME), cols(PROFILE_CODE),
        codeValueMap.getOrElse(testName, "UNKNOWN_VALUE"), cols(TEST_NAME), cols(UNIT),
        cols(LOWER).toDouble, cols(UPPER).toDouble)
    }
  }

  // Main API function for LabResultsProcessor
  def processResultsCSV(filePath: String): List[Record] = {
    val bufferedSrc = io.Source.fromFile(filePath)
    processLines(bufferedSrc.getLines.toList)
  }

  def assembleLabResults(records: List[Record], profileCodesTable: Map[String, (String, String)]): List[LabResult] = {
    val bySampleId = records.groupBy(_.sampleId).toList
    bySampleId.map {
      case (sample, currRecords) => {
        val firstRecord = currRecords.head
        val singleTestResults = assembleSingleTestResults(currRecords, profileCodesTable)
        LabResult(firstRecord.date, firstRecord.profName, firstRecord.profCode, singleTestResults)
      }
    }
  }

  def assembleSingleTestResults(records: List[Record],
                                profileCodesTable: Map[String, (String, String)]): List[SingleTestResult] = {
    records.map { record =>
      val (code, desc) = profileCodesTable.getOrElse(record.testName, ("UNKNOWN_CODE", "UNKNOWN_VALUE"))
      SingleTestResult(code, desc, record.value, record.unit, record.lower, record.upper)
    }
  }
}
