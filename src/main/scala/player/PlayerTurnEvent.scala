package player

import token.Token

import scala.collection.mutable.ListBuffer

/**
 * A PlayerTurnEvent represents the result of a Player rolling the dice with the given constellation of his tokens.
 *
 * @param player The Player from which this event originated
 */
sealed abstract class PlayerTurnEvent(implicit player: Player, val spots: Int) {
  protected val messages: ListBuffer[String] = ListBuffer(s"$player rolled $spots spots.")

  override def toString: String = messages.mkString("\n") + "\n"
}

/**
 * Represents the a Token that is set to the field from the jail.
 *
 * @param token  The token to set on the field
 * @param player The Player from which this event originated
 */
case class PlayerMoveOutOfJailEvent(token: Token)(implicit player: Player, spots: Int) extends PlayerTurnEvent {
  messages += s"$player moves $token out of the jail."
}

case class PlayerCannotMoveOutOfJailEvent(tries: Int)(implicit player: Player, spots: Int) extends PlayerTurnEvent {
  messages += s"$player rolls again, because all Tokens are jailed."
}

/**
 * Represents that a Player cannot move given the current circumstances.
 *
 * @param player The Player from which this event originated
 */
case class PlayerCannotMoveEvent()(implicit player: Player, spots: Int) extends PlayerTurnEvent {
  messages += s"$player has no option to move this turn."
}

object PlayerMoveEvent {
  def unapply(in: PlayerMoveEvent): Option[(Token, Int)] = Some((in.token, in.spots))
}

/**
 * Represents the move of excatly one Token of the Player.
 *
 * @param token  The token to move
 * @param spots  The amount of spots to move the token
 * @param player The Player from which this event originated
 */
case class PlayerMoveEvent(token: Token)(implicit player: Player, spots: Int) extends PlayerTurnEvent {
  messages += s"$player moves $token by $spots spots."
}

object PlayerRequestTokenPositionEvent {
  def unapply(in: PlayerRequestTokenPositionEvent): Option[(List[Token], Int)] = Some((in.tokens, in.spots))
}

/**
 * Represents that the player has the possibility to move several tokens.
 *
 * @param tokens The movable Tokens
 * @param spots  The spots to move the chosen Token
 * @param player The Player from which this event originated
 */
case class PlayerRequestTokenPositionEvent(tokens: List[Token])(implicit player: Player, spots: Int) extends PlayerTurnEvent {
  messages += s"$player has multiple options to move this turn: ${tokens.mkString(", ")}"
}
