import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

object FormatUtils {

  def dateToISO8601(date: String, inFormat: String = "dd/MM/yyyy"): String = {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(inFormat)
    val ld = LocalDate.parse(date, formatter)
    val ldt = LocalDateTime.of(ld, LocalDateTime.MIN.toLocalTime)
    DateTimeFormatter.ISO_DATE_TIME.format(ldt)
  }
}
