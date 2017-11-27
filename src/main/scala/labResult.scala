

case class FormattedLabResults(patients: List[PatientRecord])
case class PatientRecord(id: String, firstName: String, lastName: String, dob: String, lab_results: List[LabResult])
case class Patient(id: String, identifiers: List[String], firstName: String, lastName: String, dateOfBirth: String)
case class LabResult(timestamp: String, profile: Profile, panel: List[SingleTestResult])
case class Profile(name: String, code: String)
case class Record(hospId: String, sampleId: String, date: String, profName: String, profCode: String,
value: String, testName: String, unit: String, lower: Double, upper: Double)
case class SingleTestResult(code: String, label: String, value: String, unit: String, lower: Double, upper: Double)
