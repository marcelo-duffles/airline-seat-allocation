package optimizer

import scala.util.{ Failure, Success, Try }

object App extends App {

  val problemInput = for {
    filename <- filenameFromArgs(args)
    rawInput <- Input.fromFile(filename)
  } yield Input.problem(rawInput)

  problemInput.map(seatAllocation).map(prettify).map(print)

  def errorHandler: PartialFunction[Throwable, Unit] = {
    case error => {
      Console.err.println("Exiting due to error: " + error)
      sys.exit(1)
    }
  }

  def filenameFromArgs: Array[String] => Try[String] = _.toList match {
    case filename :: Nil => Success(filename)
    case _               => Failure(new IllegalArgumentException("Usage: sbt \"run <filename>\""))
  }

  val defaultSeatAllocation = Seq(
    Seq(Passenger(4, false), Passenger(5, false), Passenger(6, false), Passenger(7, false)),
    Seq(Passenger(11, true), Passenger(9, false), Passenger(10, false), Passenger(12, true)),
    Seq(Passenger(1, true), Passenger(2, false), Passenger(3, false), Passenger(8, false)),
    Seq(Passenger(15, false), Passenger(16, false), Passenger(13, false), Passenger(14, false))
  )

  def prettify: SeatAllocation => String = allocation =>
    allocation.foldLeft("")((s, l) => s + l.mkString(" ") + "\n")

}
