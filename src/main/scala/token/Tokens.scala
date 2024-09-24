package token

package object Implicits {

  /**
   * Extension of type List[Token], mostly to filter lists of tokens.
   */
  implicit class Tokens(list: List[Token]) {

    def get(index: Int): Option[Token] = list.find(_.index == index)

    def indexes: List[Int] = list.map(_.index)

    def projectPositions(spots: Int): List[Int] = list.map(_.projectBy(spots))

    def onField: List[Token] = list.filter(token => !token.isJailed && !token.isHome)

    def homeTokenAt(index: Int): Option[Token] = list.find(_.projectToHome == index)

    def areAllHome: Boolean = list.forall(_.isHome)

    def nonJailed: List[Token] = list.filter(!_.isJailed)

    def areAnyJailed: Boolean = list.exists(_.isJailed)

    def isJailed(index: Int): Boolean = list.get(index).exists(_.isJailed)

    def areAllJailed: Boolean = list.forall(_.isJailed)

    def getFirstJailed: Option[Token] = list.find(_.isJailed)

  }

}
