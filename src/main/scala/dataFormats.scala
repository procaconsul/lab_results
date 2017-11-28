
/*
 * Collection of all the intermediate formats adopted during the execution.
 * The execution involves the following transformations:
 *  raw data from file =>
 *  records, patients =>
 *  labResult(s) =>
 *  patientRecord(s) =>
 *  formattedLabResult
 * The convenience of having a formattedLabResults (which is a simple wrapper for a list of patientRecords)
 * arises with the lift-json library: we can serialize it to a json object in one step!
 */
case class Patient(id: String, identifiers: List[String], firstName: String, lastName: String, dateOfBirth: String)
case class Record(hospId: String, sampleId: String, date: String, profName: String, profCode: String,
                  value: String, testName: String, unit: String, lower: Double, upper: Double)

case class LabResult(timestamp: String, profile: Profile, panel: List[SingleTestResult])
case class SingleTestResult(code: String, label: String, value: String, unit: String, lower: Double, upper: Double)
case class Profile(name: String, code: String)

case class PatientRecord(id: String, firstName: String, lastName: String, dob: String, lab_results: List[LabResult])
case class FormattedLabResults(patients: List[PatientRecord])


