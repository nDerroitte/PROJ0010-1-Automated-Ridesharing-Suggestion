// @GENERATOR:play-routes-compiler
// @SOURCE:/home/minh/PI/server/conf/routes
// @DATE:Mon Feb 25 18:11:39 CET 2019


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}
