// @GENERATOR:play-routes-compiler
// @SOURCE:/mnt/d/Documents/PI/server/conf/routes
// @DATE:Fri Nov 30 11:51:18 GMT 2018


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
