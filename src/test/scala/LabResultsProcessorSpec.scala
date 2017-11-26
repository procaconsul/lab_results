import org.scalatest.{FlatSpec, Matchers}

class LabResultsProcessorSpec extends FlatSpec with Matchers {

  "The Lab Results Processor" should "correctly create code-value map from results" in {
    val expected = Map("BILI" -> "1", "ALP" -> "2", "ALT" -> "3.0", "PHOS" -> "12")
    val got      = LabResultsProcessor.extractValues(List("BILI~1", "ALP~2", "ALT~3.0", "PHOS~12"))
    got shouldEqual expected
  }
}
