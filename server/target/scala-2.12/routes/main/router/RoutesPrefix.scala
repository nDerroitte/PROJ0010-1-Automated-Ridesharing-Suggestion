// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/cedri_000/PI/server/conf/routes
// @DATE:Tue Feb 19 12:48:04 CET 2019


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
