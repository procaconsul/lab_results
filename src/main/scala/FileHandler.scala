import java.io.{File, PrintWriter}

object FileHandler {

  def processCodesFile(filePath: String): Map[String, (String, String)] = {
    val bufferedSrc = io.Source.fromFile(filePath)
    LabResultsProcessor.generateProfileToCodeDescriptionMap(bufferedSrc.getLines.toList)
  }

  def processResultsFile(filePath: String): List[Record] = {
    val bufferedSrc = io.Source.fromFile(filePath)
    LabResultsProcessor.assembleRecords(bufferedSrc.getLines.toList)
  }

  def processPatientsFile(filePath: String): List[Patient] = {
    val bufferedSrc = io.Source.fromFile(filePath)
    val content = bufferedSrc.getLines.mkString
    LabResultsProcessor.processPatients(content)
  }

  def dumpJSON(filePath: String, jsonContent: String): Unit = {
    val writer = new PrintWriter(new File(filePath))
    writer.write(jsonContent)
    writer.close()
  }

}

