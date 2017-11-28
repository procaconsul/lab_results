import java.io.{File, PrintWriter}

object FileHandler {

  def bufferSource(filePath: String): Iterator[String] = {
    io.Source.fromFile(filePath).getLines
  }

  def dumpJSON(filePath: String, jsonContent: String): Unit = {
    val writer = new PrintWriter(new File(filePath))
    writer.write(jsonContent)
    writer.close()
  }

}

