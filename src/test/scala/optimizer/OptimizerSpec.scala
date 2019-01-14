package optimizer

class OptimizerSpec extends BaseSpec {

  "The function `seatAllocation`" when {
    "given the default ProblemInput" should {
      "return the default SeatAllocation" in {
        seatAllocation(defaultProblemInput) shouldBe defaultSeatAllocation
      }
    }

    "given 4 groups of 3 passengers for a 5x3 airplane" should {
      def problemInput = ProblemInput(
        AirplaneDimensions(5, 3),
        Seq(
          Seq(Passenger(1, false), Passenger(2, true), Passenger(3, false)),
          Seq(Passenger(4, false)),
          Seq(Passenger(5, true)),
          Seq(Passenger(6, true), Passenger(7, true), Passenger(8, true)),
          Seq(Passenger(9, true)),
          Seq(Passenger(10, true), Passenger(11, false), Passenger(12, false)),
          Seq(Passenger(13, true), Passenger(14, true), Passenger(15, true)),
        )
      )
      "split one of the groups" in {
        seatAllocation(problemInput) shouldBe Seq(
          Seq(Passenger(13, true), Passenger(15, true), Passenger(14, true), Passenger(9, true), Passenger(5, true)),
          Seq(Passenger(6, true), Passenger(8, true), Passenger(7, true), Passenger(4, false), Passenger(2, true)),
          Seq(Passenger(10,true), Passenger(11,false), Passenger(12,false), Passenger(3,false), Passenger(1,false))
        )
      }
    }
  }
}
