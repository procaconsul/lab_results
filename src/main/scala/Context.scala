import com.typesafe.config.{Config, ConfigFactory}

// Wrapper around configuration in resources/application.conf
class Context(config: Config) {

  config.checkValid(ConfigFactory.defaultReference(), "lab_results")

  def this() {
    this(ConfigFactory.load())
  }

  val resultsFilePath = config.getString("lab_results.results_path")
  val codesFilePath = config.getString("lab_results.res_codes_path")
  val patientsPath = config.getString("lab_results.patients_path")
  val outputPath = config.getString("lab_results.output_path")
}
