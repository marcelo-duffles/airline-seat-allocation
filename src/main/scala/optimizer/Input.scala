package optimizer

import scala.io.Source
import scala.util.Try

object Input {

  def problem: Seq[String] => ProblemInput = raw =>
    ProblemInput(dimensions(raw.head), groups(raw.tail))

  private def dimensions: String => AirplaneDimensions = s =>
    AirplaneDimensions(seatsPerRow = s.charAt(0).asDigit, rows = s.charAt(2).asDigit)

  private def groups: Seq[String] => Seq[Group] = _.map(getGroup)

  private def getGroup: String => Group = _.split(" ").toList.map(getPassenger)

  private def getPassenger: String => Passenger = s =>
    Passenger(id = s.filter(_ != 'W').toInt, w = s.endsWith("W"))

  def fromFile: String => Try[Seq[String]] = filename =>
    Try(Source.fromFile(filename).getLines.toList)
}
