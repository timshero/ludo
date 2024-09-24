package player

import token._

sealed trait PlayerType

case class HumanPlayerType() extends PlayerType

case class ComputerPlayerType(strategy: MovingStrategy) extends PlayerType


/**
 * Creates a Player given a PlayerType. It can be a maximum of four Players be created.
 */
object PlayerFactory {
  private var offset = -1

  private val colors = Map.apply(
    0 -> TokenColorGreen(),
    1 -> TokenColorBlue(),
    2 -> TokenColorRed(),
    3 -> TokenColorYellow()
  )

  def create(playerType: PlayerType): Player = {
    offset += 1

    if (offset > 3) throw new RuntimeException("Cannot create more then 4 Players for this game.")

    playerType match {
      case HumanPlayerType() => new HumanPlayer(offset, nextColor(offset))
      case ComputerPlayerType(strategy) => new ComputerPlayer(offset, nextColor(offset), strategy)
    }
  }

  private def nextColor(index: Int): TokenColor = colors.getOrElse(index,
    throw new RuntimeException("Cannot create more then 4 colors."))

}
