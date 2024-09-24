package player

import token.Implicits.Tokens

import scala.util.Random

/**
 * A player can be in one of two states:
 * a) AllTokensAreJailed: all tokens are in the jail
 * b) SomeTokensAreFree: at least on Token is free
 *
 * Depending on the state a different logic for the turn is implemented.
 */
trait PlayerState {
  private val random = new Random

  def makeTurn()(implicit player: Player): (PlayerTurnEvent, PlayerState)

  protected def roll: Int = random.nextInt(6) + 1

}

/**
 * When all tokens are jailed, a player can roll up to three times, until he rolls a 6. If a 6 is rolled, the first
 * Token ist set to the start position on the field, otherwise the player cannot move this turn.
 */
class AllTokensJailed(val tries: Int) extends PlayerState {
  private val MAX_TRIES = 3

  override def makeTurn()(implicit player: Player): (PlayerTurnEvent, PlayerState) = {
    implicit val spots: Int = roll

    if (!player.canMoveOutOfJail(spots) && tries == MAX_TRIES) (PlayerCannotMoveEvent(), new AllTokensJailed(1))
    else if (!player.canMoveOutOfJail(spots) && tries < MAX_TRIES) (PlayerCannotMoveOutOfJailEvent(tries), new AllTokensJailed(tries + 1))
    else (PlayerMoveOutOfJailEvent(player.tokens.getFirstJailed.get), new SomeTokensFree)
  }
}

/**
 * If some Tokens are free the player rolls the dice. Depending on the situation, a different event is generated:
 * a) PlayerMoveOutOfJailEvent: the player rolls a 6 and the start position is not blocked by his tokens
 * b) PlayerCannotMoveEvent: the player cannot move any Token with the given spots
 * c) PlayerMoveEvent: Exactly one Token can be moved
 * d) PlayerRequestTokenPositionEvent: At least two tokens can be moved
 */
class SomeTokensFree extends PlayerState {
  override def makeTurn()(implicit player: Player): (PlayerTurnEvent, PlayerState) = {
    if (player.tokens.areAllJailed) {
      return new AllTokensJailed(1).makeTurn()
    }

    implicit val spots: Int = roll

    if (player.canMoveOutOfJail(spots)) {
      (PlayerMoveOutOfJailEvent(player.tokens.getFirstJailed.get), this)
    } else player.getMovableTokens(spots) match {
      case Nil => (PlayerCannotMoveEvent(), this)
      case head :: Nil => (PlayerMoveEvent(head), this)
      case tail => (PlayerRequestTokenPositionEvent(tail), this)
    }
  }
}