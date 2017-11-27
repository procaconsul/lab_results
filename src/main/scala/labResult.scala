
case class LabResult(timestamp: String, profileName: String, profileCode: String, testResults: List[SingleTestResult])
case class Record(hospId: String, sampleId: String, date: String, profName: String, profCode: String,
                                value: String, testName: String, unit: String, lower: Double, upper: Double)
case class SingleTestResult(code: String, label: String, value: String, unit: String, lower: Double, upper: Double)
