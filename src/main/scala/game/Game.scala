package game

import player.Implicits.Players
import player._
import token.Implicits.Tokens
import token.Token

import scala.annotation.tailrec
import scala.io.StdIn

/**
 * Shared game constants.
 */
object Game {
  val SHARED_LENGTH = 40

  val HOME_ZONE_LENGTH = 4

  val MAX_LENGTH: Int = SHARED_LENGTH + HOME_ZONE_LENGTH
}

/**
 * The Game is a controller, executing every turn and assigning the current Player. Depending on the result of a
 * Player's turn the Game executes different actions.
 *
 * @param players  The participants of this game
 * @param renderer The renderer to display the game
 */
class Game(
            private val players: List[Player],
            private val renderer: GameRenderer
          ) {

  private var totalTurns = 0

  def run(): Unit = getWinner match {
    case Some(winner) => endGame(winner)
    case None => makeTurnAndRender()
  }

  private def makeTurnAndRender(): Unit = {
    renderer.render(players)
    startNextTurn()
    run()
  }

  private def startNextTurn(): Unit = {
    val player = players(totalTurns % players.length)
    val round = Math.ceil(totalTurns / players.length).toInt

    StdIn.readLine(s"Round $round - Turn of $player");
    makePlayerTurn(player)
    totalTurns += 1
  }

  @tailrec
  private def makePlayerTurn(player: Player): Unit = {
    val event = player.makeTurn()

    StdIn.readLine(event.toString)

    event match {
      case PlayerMoveEvent(token, spots) => movePlayerToken(player, token, spots)
      case PlayerMoveOutOfJailEvent(token) => moveOutOfJail(player, token)
      case PlayerCannotMoveOutOfJailEvent(_) => makePlayerTurn(player)
      case PlayerRequestTokenPositionEvent(tokens, spots) => moveTokenByInput(player, tokens, spots)
      case PlayerCannotMoveEvent() =>
    }
  }

  private def endGame(winner: Player): Unit = {
    renderer.render(players)
    println(s"${winner} has won the game!")
  }

  private def movePlayerToken(player: Player, token: Token, spots: Int): Unit = {
    players
      .findCapturableTokenAtPosition(player, token.projectBy(spots))
      .foreach(tuple => {
        val (defeatedPlayer, defeatedToken) = tuple

        println(s"$player defeated Token of $defeatedPlayer. $defeatedToken is moved to jail.")
        defeatedPlayer.moveTokenTo(defeatedToken.index, -1)
      })

    player.moveTokenTo(token.index, token.getPosition + spots)
  }

  private def moveOutOfJail(player: Player, token: Token): Unit = {
    movePlayerToken(player, token, player.getStartPosition + 1)
    makePlayerTurn(player)
  }

  @tailrec
  private def moveTokenByInput(player: Player, tokens: List[Token], spots: Int): Unit = {
    val tokenPositions = tokens.indexes.map(_ + 1)

    println(s"Which Token should be moved? [${tokenPositions.mkString(", ")}]")

    try {
      val context = GameContext(tokens, players, spots)
      val token = player.choseTokenForTurn(context)

      println(s"$player moves $token")
      movePlayerToken(player, token, spots)
    } catch {
      case e: Exception =>
        println(e.getMessage)
        moveTokenByInput(player, tokens, spots)
    }
  }

  private def getWinner: Option[Player] = players.find(_.tokens.areAllHome)

}