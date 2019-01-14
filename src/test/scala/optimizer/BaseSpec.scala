package optimizer

import org.scalatest.{ Matchers, WordSpec }

class BaseSpec extends WordSpec with Matchers {

  val defaultRawInput = Seq("4 4", "1W 2 3", "4 5 6 7", "8", "9 10 11W", "12W", "13 14", "15 16")

  val defaultProblemInput = ProblemInput(
    AirplaneDimensions(4, 4),
    Seq(
      Seq(Passenger(1, true), Passenger(2, false), Passenger(3, false)),
      Seq(Passenger(4, false), Passenger(5, false), Passenger(6, false), Passenger(7, false)),
      Seq(Passenger(8, false)),
      Seq(Passenger(9, false), Passenger(10, false), Passenger(11, true)),
      Seq(Passenger(12, true)),
      Seq(Passenger(13, false), Passenger(14, false)),
      Seq(Passenger(15, false), Passenger(16, false))
    )
  )

  val defaultSeatAllocation = Seq(
    Seq(Passenger(4, false), Passenger(5, false), Passenger(6, false), Passenger(7, false)),
    Seq(Passenger(11, true), Passenger(9, false), Passenger(10, false), Passenger(12, true)),
    Seq(Passenger(1, true), Passenger(2, false), Passenger(3, false), Passenger(8, false)),
    Seq(Passenger(15, false), Passenger(16, false), Passenger(13, false), Passenger(14, false))
  )

}
