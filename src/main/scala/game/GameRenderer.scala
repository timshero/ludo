package game

import player._
import token.Implicits.Tokens
import token._

import scala.io.AnsiColor

/**
 * The GameRenderer renders the field and all Tokens to the Commandline.
 */
class GameRenderer {

  private val EMPTY = "   "


  /**
   * Renders the field
   *
   * <2><2>------18]19]20]------<3><3>
   * <2><2>------17](0)21]------<3><3>
   * ------------16](1)22]------------
   * ------------15](2)23]------------
   * 10]11]12]13]14](3)24]25]26]27]28]
   * [9](0)(1)(2)(3)---(3)(2)(1)(0)29]
   * [8][7][6][5][4](3)34]33]32]31]30]
   * ------------[3](2)35]------------
   * ------------[2](1)36]------------
   * <1><1>------[1](0)37]------<4><4>
   * <1><1>------[0]39]38]------<4><4>
   */
  def render(players: List[Player]): Unit = {
    val player1 = players.headOption
    val player2 = players.find(p => p.getStartPosition == Player.OFFSET_START_FACTOR)
    val player3 = players.find(p => p.getStartPosition == Player.OFFSET_START_FACTOR * 2)
    val player4 = players.find(p => p.getStartPosition == Player.OFFSET_START_FACTOR * 3)

    // <2><2>      18]19]20]      <3><3>
    val row0 = List(
      renderJail(player2, 0),
      renderJail(player2, 1),
      EMPTY,
      EMPTY,
      renderPosition(players, 18),
      renderPosition(players, 19),
      renderPosition(players, 20), // start 2
      EMPTY,
      EMPTY,
      renderJail(player3, 0),
      renderJail(player3, 1),
    )

    // <2><2>      17](0)21]      <3><3>
    val row1 = List(
      renderJail(player2, 2),
      renderJail(player2, 3),
      EMPTY,
      EMPTY,
      renderPosition(players, 17),
      renderHome(player3, 0),
      renderPosition(players, 21),
      EMPTY,
      EMPTY,
      renderJail(player3, 2),
      renderJail(player3, 3),
    )

    //             16](1)22]
    val row2 = List(
      EMPTY,
      EMPTY,
      EMPTY,
      EMPTY,
      renderPosition(players, 16),
      renderHome(player3, 1),
      renderPosition(players, 22),
      EMPTY,
      EMPTY,
      EMPTY,
      EMPTY,
    )

    //             15](2)23]
    val row3 = List(
      EMPTY,
      EMPTY,
      EMPTY,
      EMPTY,
      renderPosition(players, 15),
      renderHome(player3, 2),
      renderPosition(players, 23),
      EMPTY,
      EMPTY,
      EMPTY,
      EMPTY,
    )

    // 10]11]12]13]14](3)24]25]26]27]28]
    val row4 = List(
      renderPosition(players, 10), // start 3
      renderPosition(players, 11),
      renderPosition(players, 12),
      renderPosition(players, 13),
      renderPosition(players, 14),
      renderHome(player3, 3),
      renderPosition(players, 24),
      renderPosition(players, 25),
      renderPosition(players, 26),
      renderPosition(players, 27),
      renderPosition(players, 28),
    )

    // [9](0)(1)(2)(3)   (3)(2)(1)(0)29]"
    val row5 = List(
      renderPosition(players, 9),
      renderHome(player2, 0),
      renderHome(player2, 1),
      renderHome(player2, 2),
      renderHome(player2, 3),
      EMPTY,
      renderHome(player4, 3),
      renderHome(player4, 2),
      renderHome(player4, 1),
      renderHome(player4, 0),
      renderPosition(players, 29),
    )

    // [8][7][6][5][4](3)34]33]32]31]30]
    val row6 = List(
      renderPosition(players, 8),
      renderPosition(players, 7),
      renderPosition(players, 6),
      renderPosition(players, 5),
      renderPosition(players, 4),
      renderHome(player1, 3),
      renderPosition(players, 34),
      renderPosition(players, 33),
      renderPosition(players, 32),
      renderPosition(players, 31),
      renderPosition(players, 30), // start 4
    )

    //             [3](2)35]
    val row7 = List(
      EMPTY,
      EMPTY,
      EMPTY,
      EMPTY,
      renderPosition(players, 3),
      renderHome(player1, 2),
      renderPosition(players, 35),
      EMPTY,
      EMPTY,
      EMPTY,
      EMPTY,
    )

    //             [2](1)36]
    val row8 = List(
      EMPTY,
      EMPTY,
      EMPTY,
      EMPTY,
      renderPosition(players, 2),
      renderHome(player1, 1),
      renderPosition(players, 36),
      EMPTY,
      EMPTY,
      EMPTY,
      EMPTY,
    )

    // <1><1>      [1](0)37]      <4><4>
    val row9 = List(
      renderJail(player1, 0),
      renderJail(player1, 1),
      EMPTY,
      EMPTY,
      renderPosition(players, 1),
      renderHome(player1, 0),
      renderPosition(players, 37),
      EMPTY,
      EMPTY,
      renderJail(player4, 0),
      renderJail(player4, 1),
    )

    // <1><1>      [0]39]38]      <4><4>
    val row10 = List(
      renderJail(player1, 2),
      renderJail(player1, 3),
      EMPTY,
      EMPTY,
      renderPosition(players, 0),
      renderPosition(players, 39),
      renderPosition(players, 38),
      EMPTY,
      EMPTY,
      renderJail(player4, 2),
      renderJail(player4, 3),
    )

    print("\u001b[2J")
    List(row0, row1, row2, row3, row4, row5, row6, row7, row8, row9, row10).map(r => r.mkString).foreach(println)
    println()
  }

  private def renderPosition(players: List[Player], position: Int): String = players
    .flatMap(_.tokens.onField)
    .find(_.getNormalizedPosition == position)
    .map(token => colored(token, s"[${token.index + 1}]"))
    .getOrElse(if (position % Player.OFFSET_START_FACTOR == 0) "[A]" else "[ ]")

  private def renderHome(playerOpt: Option[Player], index: Int): String = playerOpt
    .map(player => colored(player, player.tokens.homeTokenAt(index)
      .map(_ => "(x)").getOrElse(indexToLetter(index))))
    .getOrElse(EMPTY)

  private def renderJail(player: Option[Player], index: Int): String = player
    .map(player => colored(player, s"<${if (player.tokens.isJailed(index)) index + 1 else " "}>"))
    .getOrElse(EMPTY)

  private def colored(player: Player, text: String): String = {
    s"${colorToAnsi(player.tokenColor)}$text${AnsiColor.RESET}"
  }

  private def colored(token: Token, text: String): String = {
    s"${colorToAnsi(token.color)}$text${AnsiColor.RESET}"
  }

  private def colorToAnsi(tokenColor: TokenColor): String = tokenColor match {
    case _: TokenColorYellow => AnsiColor.YELLOW
    case _: TokenColorGreen => AnsiColor.GREEN
    case _: TokenColorBlue => AnsiColor.BLUE
    case _: TokenColorRed => AnsiColor.RED
  }

  private def indexToLetter(index: Int): String = index match {
    case 0 => "(a)"
    case 1 => "(b)"
    case 2 => "(c)"
    case 3 => "(d)"
    case _ => throw new RuntimeException(s"Invalid index for home zone $index")
  }
}
