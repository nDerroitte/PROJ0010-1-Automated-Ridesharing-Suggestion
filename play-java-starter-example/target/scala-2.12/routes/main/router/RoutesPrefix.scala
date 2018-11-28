// @GENERATOR:play-routes-compiler
// @SOURCE:/mnt/c/Users/mieux/Documents/PI/play-java-starter-example/conf/routes
// @DATE:Wed Nov 28 13:23:49 GMT 2018


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
