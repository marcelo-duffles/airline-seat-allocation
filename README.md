# airline-seat-allocation
This is a simple console program in Scala that optimizes the airplane seat allocation by maximizing the number of passengers that have had their preferences satisfied. The preferences can be:
* groups of passengers prefer to seat together on the same row.
* some passengers prefer window seats.

The program takes an input file as a command-line argument, for instance:
```
4 4
1W 2 3
4 5 6 7
8
9 10 11W
12W
13 14
15 16
```

The first line specifies the dimensions of the plane. The first digit is the number of seats in a row and the second digit is the number of rows on the plane.
Each subsequent line describes a group of passengers. For example, the first line of travelers describes a group of three where the first passenger has a preference for a window seat. Each number uniquely identifies the passenger on the flight.


## Running one example
You can run the app to calculate the seat allocation for the above example:

```
$ sbt "run inputfile"
[...]
[info] Running optimizer.App inputfile
4 5 6 7
11W 9 10 12W
1W 2 3 8
15 16 13 14
[success] Total time: 8 s, completed Jan 14, 2019 10:00:58 PM
```

You can see that all the passengers had their preferences satisfied.


## Solution
Let `n` be the number of seats in the airplane. The total number of permutations of `n` passengers in `n` seats is `n!`, so an algorithm that relies only on exhaustive search on this `n`-dimensional space would have time complexity of `O(n!)`. In contrast, the implemented algorithm has time complexity of `O(n)`, orders of magnitude lower. This problem could also be solved with off-the-shelf optimization algorithms, but we preferred a heuristic more effective for this particular case. Our heuristic relies on the following observations:
* In the vast majority of the sub-spaces within the `n`-dimensional space the objective function (i.e. maximizing the number of passengers that had their preferences satisfied) is constant.
* The problem space is highly symmetrical, by construction. For instance, imagine that each seat correspond to an integer number, then observe the symmetry among the points. In a `2`-dimensional space, the valid points are `(1,0)` and `(0,1)`, which are at the intersection of the line `y = 1 - x` with the `y` and `x` axis. In a `3`-dimensional space, the valid points are located at the center of the external edges of a cube. And so on and so forth. 
* A random selection of the possible points is not needed, since the construction of the problem gives us a method of selecting points that will increase the objective function without the need of computing it. For example, when selecting the value for a subsequent dimension, the we certainly know that the value that corresponds to another passenger in the same group is better than a value that corresponds to any other passenger. In fact, it's not hard to construct a dimensionality reduction technique based on the above fact. For example, consider a `2x2` airplane and for passengers `a, b, c, d`, where `a` and `b` are part of a group and `c` and `d` are traveling alone. Due to the above reasons, without loss of generality, we can arbitrarily select the passenger `a` for the top left position. Then, the problem reduces to determine whether `b`, `c` or `d` are in the top right position, next to `a`. If `b` is selected, then the objective function is maximized (100% of passengers have their preferences satisfied). Otherwise, the passenger satisfaction will be 50%. Therefore, the problem of exploring the `3`-dimensional space of possible values for `b, c, d` is reduced to an `1`-dimensional variable that represents the three available choices for the neighbor of `a`.

Our heuristic works as follows:
* Sort the input groups by size in descending order.
* Allocate seats for each group.
  * If a group doesn't fit in a row, skip it and try it in a later row.
  * If all groups have been visited (including the single-passenger ones), break down the smallest one.

We should also note that we focused on maximizing preference for groups of passengers and the window-seat allocation still has room for improvement. For example, in our test case `when given 4 groups of 3 passengers for a 5x3 airplane`, passenger 9 could have been placed in passenger 2 position.


## Testing

You can run unit tests as below:

```
$ sbt test
[...]
[info] InputSpec:
[info] The function `problem`
[info]   when given the default raw input
[info]   - should return the default ProblemInput
[info] OptimizerSpec:
[info] The function `seatAllocation`
[info]   when given the default ProblemInput
[info]   - should return the default SeatAllocation
[info]   when given 4 groups of 3 passengers for a 5x3 airplane
[info]   - should split one of the groups
[info] Run completed in 412 milliseconds.
[info] Total number of tests run: 3
[info] Suites: completed 3, aborted 0
[info] Tests: succeeded 3, failed 0, canceled 0, ignored 0, pending 0
[info] All tests passed.
[success] Total time: 8 s, completed Jan 14, 2019 10:03:59 PM
```
