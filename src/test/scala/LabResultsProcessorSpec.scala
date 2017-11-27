import org.scalatest.{FlatSpec, Matchers}

class LabResultsProcessorSpec extends FlatSpec with Matchers {

  "The Lab Results Processor" should "correctly create code-value map from results" in {
    val expected = Map("BILI" -> "1", "ALP" -> "2", "ALT" -> "3.0", "PHOS" -> "12")
    val got      = LabResultsProcessor.generateProfCodeToValueMap(List("BILI~1", "ALP~2", "ALT~3.0", "PHOS~12"))
    got shouldEqual expected
  }

  it should "correctly process well-formed lines" in {
    val lines = List("Header Line",
      "40681648,41860BONALP~55,09/08/2014,BONE PROFILE,BON,,," +
        ",ALP~2.34,PHOS~1.29,,,,,,,,,,,,,,,,,,,,,ALP,IU/L,35,104",
      "40681615,41860BONALP~55,09/08/2017,MAGNESIUM,MG,,," +
        "CA~2.18,,,,,,,,,,,,,,,,,,,,,,,CA,mmol/L,2.2,2.6"
    )
    val expected = List(
      Record("40681648","41860BONALP~55", "2014-08-09T00:00:00","BONE PROFILE","BON","2.34","ALP","IU/L",35,104),
      Record("40681615","41860BONALP~55", "2017-08-09T00:00:00","MAGNESIUM","MG","2.18","CA","mmol/L",2.2,2.6)
    )

    val got = LabResultsProcessor.processLines(lines)
    got shouldEqual expected
  }

  it should "correctly assemble single test results" in {
    val records = List(
      Record("40681648","41860BONALP~55", "09/08/2014","BONE PROFILE","BON","2.34","ALP","IU/L",35,104),
      Record("40681615","41860BONALP~55", "09/08/2017","MAGNESIUM","MG","2.18","CA","mmol/L",2.2,2.6)
    )
    val map = Map("ALP" -> ("6768-6","Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma"),
                  "CA"->("17861-6","Calcium, Serum"))

    val expected = List(
      SingleTestResult("6768-6", "Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma","2.34","IU/L",35,104),
      SingleTestResult("17861-6", "Calcium, Serum","2.18","mmol/L",2.2,2.6)
    )

    val got = LabResultsProcessor.assembleSingleTestResults(records,map)
    got shouldEqual expected
  }

  it should "correctly generate code-description map from well-formed lines" in {
    val lines = List("key,code,description",
      "ALB,1751-7,\"Albumin, Serum\"",
      "ALP,6768-6,Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma")
    val expected = Map("ALB"->("1751-7", "\"Albumin, Serum\""),
      "ALP"->("6768-6","Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma"))

    val got = LabResultsProcessor.generateProfileToCodeDescriptionMap(lines)
    got shouldEqual expected
  }
}
