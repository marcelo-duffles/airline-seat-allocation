package optimizer

class InputSpec extends BaseSpec {

  "The function `problem`" when {
    "given the default raw input" should {
      "return the default ProblemInput" in {
        Input.problem(defaultRawInput) shouldBe defaultProblemInput
      }
    }
  }

}
