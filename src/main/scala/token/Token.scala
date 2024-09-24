package token

import game.Game

/**
 * A TokenColor identifies the Tokens belonging to a Player.
 */
sealed trait TokenColor

case class TokenColorGreen() extends TokenColor {
  override def toString: String = "Green"
}

case class TokenColorBlue() extends TokenColor {
  override def toString: String = "Blue"
}

case class TokenColorRed() extends TokenColor {
  override def toString: String = "Red"
}

case class TokenColorYellow() extends TokenColor {
  override def toString: String = "Yellow"
}

/**
 * A Token belongs to a Player and is identified by a TokenColor and index. The Token starts at the given offset of the
 * Player and has a position on the field.
 *
 * @param index    Unique index of this Token in the Player's token list
 * @param color    Color identifying to which Player this token belongs
 * @param offset   Offset on the field where this Token starts
 * @param position Current position of this Token on the field, in the jail or home
 */
class Token(
             val index: Int,
             val color: TokenColor,
             val offset: Int,
             private var position: (Int) = -1
           ) {

  def moveTo(newPosition: Int): Unit = position = newPosition

  def isJailed: Boolean = position == -1

  def isHome: Boolean = projectToHome > -1

  def getNormalizedPosition: Int = projectBy(0)

  def projectToHome: Int = position - offset - Game.SHARED_LENGTH

  def projectBy(spots: Int): Int = (position + spots) % Game.SHARED_LENGTH

  def getPosition: Int = position

  override def toString: String = s"Token (${index + 1}, $position)"

}
