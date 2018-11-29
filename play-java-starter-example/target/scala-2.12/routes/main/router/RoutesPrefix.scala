// @GENERATOR:play-routes-compiler
// @SOURCE:/mnt/d/Documents/PI/play-java-starter-example/conf/routes
// @DATE:Thu Nov 29 20:36:05 GMT 2018


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
