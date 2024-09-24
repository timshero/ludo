package player

import game.{Game, GameContext}
import token.Implicits.Tokens
import token.{Token, TokenColor}

object Player {

  val MAX_SPOTS = 6

  val OFFSET_START_FACTOR = 10

}

/**
 * A Player owns four tokens which all start in the jail. Each turn the player rolls the dice and depending on the roll
 * and the tokens position different moves can be executed.
 *
 * @param offset     Offset of the start position of the field
 * @param tokenColor Color identifying the Player
 */
abstract class Player(val offset: Int, val tokenColor: TokenColor) {

  // implicitly known in the state
  implicit val player: Player = this

  private var state: PlayerState = new AllTokensJailed(1)

  val tokens: List[Token] = List(
    new Token(0, tokenColor, getStartPosition),
    new Token(1, tokenColor, getStartPosition),
    new Token(2, tokenColor, getStartPosition),
    new Token(3, tokenColor, getStartPosition)
  )

  def makeTurn(): PlayerTurnEvent = {
    val (event, nextState) = state.makeTurn

    state = nextState
    event
  }

  def moveTokenTo(tokenIndex: Int, position: Int): Unit = tokens
    .get(tokenIndex)
    .map(_.moveTo(position))
    .getOrElse(new RuntimeException("Invalid Token position: " + tokenIndex))

  def getStartPosition: Int = offset * Player.OFFSET_START_FACTOR

  def canMoveOutOfJail(spots: Int): Boolean = spots == Player.MAX_SPOTS &&
    tokens.forall(_.getPosition != getStartPosition) &&
    tokens.areAnyJailed

  def getMovableTokens(spots: Int): List[Token] = tokens.nonJailed.filter(candidate => {
    val newPosition = candidate.getPosition + spots
    val otherTokens = tokens.nonJailed.filter(_.index != candidate.index)

    newPosition < Game.MAX_LENGTH + getStartPosition && otherTokens.forall(_.getPosition != newPosition)
  })

  def choseTokenForTurn(context: GameContext): Token

  override def toString: String = s"Player $tokenColor"

}
