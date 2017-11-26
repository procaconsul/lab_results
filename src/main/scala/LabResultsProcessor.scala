
case class UnprocessedLabResult(hospId: String, date: String, profName: String, profCode: String,
                                values: Map[String, String], testName: String, unit: String,
                                upper: Double, lower: Double)

object LabResultsProcessor {

  def extractValues(resCols: List[String]): Map[String, String] = {
    resCols.map { res =>
      val values = res.split("~")
      (values(0), values(1))
    }.toMap
  }


}
