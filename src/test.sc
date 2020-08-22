import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

val start = "20170929-230000"

val formatter = DateTimeFormat.forPattern("yyyyMMdd-HHmmss")
val dt = formatter.parseDateTime(start)
val startDate = dt.plusDays(1)
