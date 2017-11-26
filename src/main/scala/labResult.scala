
case class LabResult(timestamp: String, profileName: String, profileCode: String, panel: Panel)
case class Panel(code: String, label: String, value: String, unit: String, lower: Double, upper: Double)