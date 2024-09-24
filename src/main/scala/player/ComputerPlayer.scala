package player

import game.GameContext
import player.Implicits.Players
import token.Implicits.Tokens
import token.{Token, TokenColor}

import scala.util.Random

/**
 * A ComputerPlayer uses a given algorithm to make decision in his turn.
 *
 * @param offset     Offset of the start position of the field
 * @param tokenColor Color identifying the Player
 * @param strategy   The strategy by which this Bot decides how to move the Tokens
 */
class ComputerPlayer(
                      offset: Int,
                      tokenColor: TokenColor,
                      private val strategy: MovingStrategy)
  extends Player(offset, tokenColor) {

  override def choseTokenForTurn(context: GameContext): Token =
    strategy.choseTokenForTurn(this, context)

  override def toString: String = super.toString + s"[$strategy]"

}

object MovingStrategy {
  private val strategies = List(
    AlwaysMoveFirstStrategy,
    AlwaysMoveLastStrategy,
    RandomMoveStrategy,
    MoveTokenNextToHome,
    BeatTokensFirstStrategy
  )

  def getRandomStrategy: MovingStrategy = {
    strategies(new Random().nextInt(strategies.length))()
  }
}

/**
 * A ComputerPlayer decides with a MovingStrategy which Token to move, if there are more than one Tokens movable.
 */
sealed trait MovingStrategy {
  def choseTokenForTurn(player: Player, context: GameContext): Token

  override def toString: String = getClass.getSimpleName

}

/**
 * Moves the first Token.
 */
case class AlwaysMoveFirstStrategy() extends MovingStrategy {
  override def choseTokenForTurn(player: Player, context: GameContext): Token = context.tokens.head
}

/**
 * Moves the last Token.
 */
case class AlwaysMoveLastStrategy() extends MovingStrategy {
  override def choseTokenForTurn(player: Player, context: GameContext): Token = context.tokens.last
}

/**
 * Moves a random Token.
 */
case class RandomMoveStrategy() extends MovingStrategy {
  override def choseTokenForTurn(player: Player, context: GameContext): Token =
    context.tokens(new Random().nextInt(context.tokens.length))

}

/**
 * Moves the Token which is the closest to the home zone.
 */
case class MoveTokenNextToHome() extends MovingStrategy {
  override def choseTokenForTurn(player: Player, context: GameContext): Token =
    context.tokens.reduce((a, b) => if (a.getNormalizedPosition > b.getNormalizedPosition) a else b)
}

/**
 * Moves the first Token that can beat the Token of another Player. If not Such token is found, the
 * MoveTokenNextToHome-Strategy is used as a default.
 */
case class BeatTokensFirstStrategy() extends MovingStrategy {

  // We can extend the strategy by picking the token, if multiple can be beaten, that is closest to it's home zone
  override def choseTokenForTurn(player: Player, context: GameContext): Token = context.tokens
    .projectPositions(context.spots)
    .flatMap(context.players.findTokenPositionThatCanBeCaptured(player, _))
    .flatMap(pos => context.tokens.filter(_.projectBy(context.spots) == pos))
    .headOption
    .getOrElse(MoveTokenNextToHome().choseTokenForTurn(player, context))

}

// human strategy:
// 1. beat other tokens
// 2. move tokens from start position
// 3. chase tokens in range
// 4. move token next to home