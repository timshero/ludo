package player

import game.GameContext
import token.Implicits.Tokens
import token.{Token, TokenColor}

import scala.io.StdIn

/**
 * A HumanPlayer provides input via the Commandline to interact with the game.
 *
 * @param offset     Offset of the start position of the field
 * @param tokenColor Color identifying the Player
 */
class HumanPlayer(offset: Int, tokenColor: TokenColor) extends Player(offset, tokenColor) {

  override def choseTokenForTurn(context: GameContext): Token = {
    try {
      val index = StdIn.readInt() - 1

      if (tokens.indexes.contains(index)) tokens(index)
      else throw new RuntimeException("Input is not a valid index.")
    } catch {
      case _: NumberFormatException => throw new RuntimeException("Input is not a number.")
      case e: Exception => throw new RuntimeException(e.getMessage)
    }
  }

}
