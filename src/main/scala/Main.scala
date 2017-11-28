import FileHandler._

object Main extends App {

  val context: Context = new Context()

  val records      = LabResultsProcessor.assembleRecords(bufferSource(context.resultsFilePath).toList)
  val codesTable   = LabResultsProcessor.generateCodesTable(bufferSource(context.codesFilePath).toList)
  val patients     = LabResultsProcessor.processPatients(bufferSource(context.patientsPath).mkString)
  val formattedRes = LabResultsProcessor.buildFormattedLabResults(records, codesTable, patients)
  val jsonOut      = LabResultsProcessor.produceJSON(formattedRes)
  dumpJSON(context.outputPath, jsonOut)
}
