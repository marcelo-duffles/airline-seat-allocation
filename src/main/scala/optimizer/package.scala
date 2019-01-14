import scala.math.Ordering

package object optimizer {

  case class Passenger(id: Int, w: Boolean) {
    override def toString = if (w) id.toString + "W" else id.toString
  }
  
  type Group = Seq[Passenger]
  case class AirplaneDimensions(seatsPerRow: Int, rows: Int)
  case class ProblemInput(dimensions: AirplaneDimensions, groups: Seq[Group])

  type SeatAllocation = Seq[Seq[Passenger]]

  private def sortBySizeAndPreference: Seq[Group] => Seq[Group] = {
    def compare: (Group, Group) => Boolean = (a, b) =>
      if (a.size != b.size) a.size > b.size else a.filter(_.w).size >= b.filter(_.w).size
    _.sortWith(compare)
  }

  implicit object PassengerOrdering extends Ordering[Passenger] {
    def compare(a: Passenger, b: Passenger): Int = a.id compare b.id
  }

  def seatAllocation: ProblemInput => SeatAllocation = input => {
    type GroupIndex = Int
    type Acc = (SeatAllocation, Seq[Group], Set[Group], GroupIndex)

    def allocateRow: (Acc, Int) => Acc = (acc, rowIndex) => {
      def spaceLeft = (allocation: SeatAllocation, rowIndex: Int) =>
        input.dimensions.seatsPerRow - allocation(rowIndex).size

      def windowPreference: Acc => Group = acc => {
        val (allocation, groups, _, groupIndex) = acc
        val group = groups(groupIndex)
        group.filter(_.w) match {
          case p1 :: p2 :: _ => p1 +: group.filter(e => e != p1 && e != p2) :+ p2
          case p :: Nil      => if (allocation(rowIndex).size == 0) p +: group.filter(_ != p) else group.filter(_ != p) :+ p
          case Nil           => group
        }
      }

      def allocateGroup: PartialFunction[Acc, Acc] = {
        case (allocation, groups, allocated, groupIndex) if spaceLeft(allocation, rowIndex) >= groups(groupIndex).size => {
          val group = windowPreference((allocation, groups, allocated, groupIndex))
          (
            allocation.patch(rowIndex, Seq(allocation(rowIndex) ++ group), 1),
            groups,
            allocated ++ Set(group),
            groupIndex + 1
          )
        }
      }

      def breakDownLastGroup: PartialFunction[Acc, Acc] = {
        case (allocation, groups, allocated, groupIndex) if groups.last == groups(groupIndex) =>
          (
            allocation,
            groups.take(groups.size - 1) ++ sortBySizeAndPreference(groups.takeRight(1).flatten.grouped(1).toSeq),
            allocated,
            groupIndex
          )
      }

      def nextGroup: PartialFunction[Acc, Acc] = {
        case (allocation, groups, allocated, groupIndex) => (allocation, groups, allocated, groupIndex + 1)
      }

      def reset: Acc => Acc = acc => {
        val (allocation, groups, allocated, groupIndex) = acc
        (allocation, groups.map(_.toSet).diff(allocated.map(_.toSet).toList).map(_.toList), allocated, 0)
      }

      def resumeWithUnallocated: PartialFunction[Acc, Acc] = {
        case (allocation, groups, allocated, groupIndex) if groupIndex >= groups.size =>
          reset((allocation, groups, allocated, groupIndex))
      }

      val next: Acc => Acc = resumeWithUnallocated orElse allocateGroup orElse breakDownLastGroup orElse nextGroup
      val endOfRow = spaceLeft(acc._1, rowIndex) == 0

      if (endOfRow) reset(acc) else allocateRow(next(acc), rowIndex)
    }

    def initialAcc: Acc = {
      val initialSeatAllocation = (0 to input.dimensions.rows - 2).foldLeft(Seq(Seq()))((acc, _) => acc ++ Seq(Seq()))
      (initialSeatAllocation, sortBySizeAndPreference(input.groups), Set(), 0)
    }

    val (seatAllocation, _, _, _) = (0 to input.dimensions.rows - 1).foldLeft(initialAcc)(allocateRow)
    seatAllocation
  }

}
