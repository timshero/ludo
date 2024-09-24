import game.{Game, GameRenderer}
import player._

/**
 * Starts the Game with a given number of Players. Executes the game loop until a Player has won the game.
 */
object Main extends App {

  private val playerTypes: List[PlayerType] = List(
    HumanPlayerType(),
    ComputerPlayerType(MovingStrategy.getRandomStrategy),
    ComputerPlayerType(MovingStrategy.getRandomStrategy),
    ComputerPlayerType(BeatTokensFirstStrategy()),
  )

  private val players = playerTypes.map(PlayerFactory.create)
  private val renderer = new GameRenderer
  private val game = new Game(players, renderer)

  println("Starting the game, players: ")
  players.foreach(println)
  println()

  game.run()

}