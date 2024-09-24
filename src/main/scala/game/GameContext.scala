package game

import player.Player
import token.Token

/**
 * Context that holds information about the current state of the game.
 *
 * @param tokens  Tokens relevant for the computation the context is used in
 * @param players Participants of the Game
 * @param spots   Rolled spots from the current active Player
 */
case class GameContext(
                        tokens: List[Token],
                        players: List[Player],
                        spots: Int
                      )
