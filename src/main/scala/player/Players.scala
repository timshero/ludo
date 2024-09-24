package player

import token.Implicits.Tokens
import token.Token

package object Implicits {

  implicit class Players(list: List[Player]) {
    def findTokenAtProjection(player: Player, position: Int): List[(Player, Token)] = list
      .filter(_.tokenColor != player.tokenColor)
      .flatMap(current => current.tokens.onField.map((current, _)))
      .filter(tuple => tuple._2.getNormalizedPosition == position)

    def findTokenPositionThatCanBeCaptured(player: Player, position: Int): List[Int] = list
      .findTokenAtProjection(player, position)
      .map(_._2.getNormalizedPosition)

    def findCapturableTokenAtPosition(player: Player, position: Int): Option[(Player, Token)] = list
      .findTokenAtProjection(player, position)
      .headOption
  }

}
