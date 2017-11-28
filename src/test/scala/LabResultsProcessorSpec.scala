import org.scalatest.{FlatSpec, Matchers}

/*
 * Minimal test suite to assess functional correctness (base of TDD).
 * No nasty edge cases are covered (like malformed values or inconsistent data),
 * as the scope of this task is limited and it is possible to make assumptions about the shape of the data received.
 */
class LabResultsProcessorSpec extends FlatSpec with Matchers {

  "The Lab Results Processor" should "correctly create code-value map from results" in {
    val expected = Map("BILI" -> "1", "ALP" -> "2", "ALT" -> "3.0", "PHOS" -> "12")
    val got      = LabResultsProcessor.generateCodeValueMap(List("BILI~1", "ALP~2", "ALT~3.0", "", "PHOS~12", ""))
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

    val got = LabResultsProcessor.assembleRecords(lines)
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
    val expected = Map("ALB"->("1751-7", "Albumin, Serum"),
      "ALP"->("6768-6","Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma"))

    val got = LabResultsProcessor.generateCodesTable(lines)
    got shouldEqual expected
  }

  it should "generate correct list of patients from json list" in {
    val jsonPatients =
      """
        |[
        |  {
        |    "id": "ccbf8ccb-12b9-4f33-b5a2-cb2d38039d3e",
        |    "identifiers": [
        |      "41915278"
        |    ],
        |    "firstName": "Patient",
        |    "lastName": "Alpha",
        |    "dateOfBirth": "1980-01-10T00:00:00.000Z"
        |  },
        |  {
        |    "id": "9341fce0-5864-4b8a-a850-1ec0689482b3",
        |    "identifiers": [
        |      "41723788"
        |    ],
        |    "firstName": "Patient",
        |    "lastName": "Beta",
        |    "dateOfBirth": "1995-03-30T00:00:00.000Z"
        |  }
        |]
      """.stripMargin

    val expected = List(
      Patient("ccbf8ccb-12b9-4f33-b5a2-cb2d38039d3e", List("41915278"), "Patient", "Alpha", "1980-01-10T00:00:00.000Z"),
      Patient("9341fce0-5864-4b8a-a850-1ec0689482b3", List("41723788"), "Patient", "Beta", "1995-03-30T00:00:00.000Z")
    )

    val got = LabResultsProcessor.processPatients(jsonPatients)
    got shouldEqual expected
  }

  it should "generate correct FormattedLabResults from given records, codes table and patients" in {
    val records = List(
      Record("40681648","41860BONALP~55", "09/08/2014","BONE PROFILE","BON","55","ALP","IU/L",35,104),
      Record("40681648","41860MGMG~0.91", "09/08/2014","MAGNESIUM","MG","0.91","CA","mmol/L",2.2,2.6),
      Record("40681615","41860MGMG~0.91", "09/08/2017","MAGNESIUM","MG","0.91","CA","mmol/L",2.2,2.6)
    )

    val map =  Map("ALP" -> ("6768-6","Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma"),
      "CA"->("17861-6","Calcium, Serum"))

    val patients = List(
      Patient("ccbf8ccb-12b9-4f33-b5a2-cb2d38039d3e", List("40681648"), "Patient", "Alpha", "1980-01-10T00:00:00.000Z"),
      Patient("9341fce0-5864-4b8a-a850-1ec0689482b3", List("40681615"), "Patient", "Beta", "1995-03-30T00:00:00.000Z")
    )

    val expected = FormattedLabResults(List(
      PatientRecord("ccbf8ccb-12b9-4f33-b5a2-cb2d38039d3e", "Patient", "Alpha", "1980-01-10T00:00:00.000Z",
        List(
          LabResult("09/08/2014", Profile("MAGNESIUM","MG"),
            List(SingleTestResult("17861-6","Calcium, Serum", "0.91","mmol/L",2.2,2.6)
            )
          ),
          LabResult("09/08/2014", Profile("BONE PROFILE","BON"),
            List(SingleTestResult("6768-6","Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma",
                "55","IU/L",35,104)
            ))
        )
      ),
      PatientRecord("9341fce0-5864-4b8a-a850-1ec0689482b3", "Patient", "Beta", "1995-03-30T00:00:00.000Z",
        List(
          LabResult("09/08/2017", Profile("MAGNESIUM","MG"),
            List(SingleTestResult("17861-6","Calcium, Serum", "0.91","mmol/L",2.2,2.6)
            )
          )
        ))
    ))

    val got = LabResultsProcessor.buildFormattedLabResults(records, map, patients)
    got shouldEqual expected
  }

  it should "generate correct and well-formed json file from formatted lab result" in {
    val formattedResults = FormattedLabResults(List(
      PatientRecord("ccbf8ccb-12b9-4f33-b5a2-cb2d38039d3e", "Patient", "Alpha", "1980-01-10T00:00:00.000Z",
        List(
          LabResult("09/08/2014", Profile("MAGNESIUM","MG"),
            List(SingleTestResult("17861-6","Calcium, Serum", "0.91","mmol/L",2.2,2.6)
            )
          ),
          LabResult("09/08/2014", Profile("BONE PROFILE","BON"),
            List(SingleTestResult("6768-6","Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma",
              "55","IU/L",35,104)
            ))
        )
      ),
      PatientRecord("9341fce0-5864-4b8a-a850-1ec0689482b3", "Patient", "Beta", "1995-03-30T00:00:00.000Z",
        List(
          LabResult("09/08/2017", Profile("MAGNESIUM","MG"),
            List(SingleTestResult("17861-6","Calcium, Serum", "0.91","mmol/L",2.2,2.6)
            )
          )
        ))
    ))

    val expected =
      """{
        |  "patients":[
        |    {
        |      "id":"ccbf8ccb-12b9-4f33-b5a2-cb2d38039d3e",
        |      "firstName":"Patient",
        |      "lastName":"Alpha",
        |      "dob":"1980-01-10T00:00:00.000Z",
        |      "lab_results":[
        |        {
        |          "timestamp":"09/08/2014",
        |          "profile":{
        |            "name":"MAGNESIUM",
        |            "code":"MG"
        |          },
        |          "panel":[
        |            {
        |              "code":"17861-6",
        |              "label":"Calcium, Serum",
        |              "value":"0.91",
        |              "unit":"mmol/L",
        |              "lower":2.2,
        |              "upper":2.6
        |            }
        |          ]
        |        },
        |        {
        |          "timestamp":"09/08/2014",
        |          "profile":{
        |            "name":"BONE PROFILE",
        |            "code":"BON"
        |          },
        |          "panel":[
        |            {
        |              "code":"6768-6",
        |              "label":"Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma",
        |              "value":"55",
        |              "unit":"IU/L",
        |              "lower":35.0,
        |              "upper":104.0
        |            }
        |          ]
        |        }
        |      ]
        |    },
        |    {
        |      "id":"9341fce0-5864-4b8a-a850-1ec0689482b3",
        |      "firstName":"Patient",
        |      "lastName":"Beta",
        |      "dob":"1995-03-30T00:00:00.000Z",
        |      "lab_results":[
        |        {
        |          "timestamp":"09/08/2017",
        |          "profile":{
        |            "name":"MAGNESIUM",
        |            "code":"MG"
        |          },
        |          "panel":[
        |            {
        |              "code":"17861-6",
        |              "label":"Calcium, Serum",
        |              "value":"0.91",
        |              "unit":"mmol/L",
        |              "lower":2.2,
        |              "upper":2.6
        |            }
        |          ]
        |        }
        |      ]
        |    }
        |  ]
        |}""".stripMargin
    val got = LabResultsProcessor.produceJSON(formattedResults)
    got shouldEqual expected
  }
}
