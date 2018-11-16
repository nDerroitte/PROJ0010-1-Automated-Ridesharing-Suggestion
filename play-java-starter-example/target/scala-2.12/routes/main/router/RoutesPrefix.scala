// @GENERATOR:play-routes-compiler
// @SOURCE:/home/minh/PI/play-java-starter-example/conf/routes
// @DATE:Fri Nov 16 16:28:02 CET 2018


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
