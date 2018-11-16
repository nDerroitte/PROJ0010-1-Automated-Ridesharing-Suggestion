// @GENERATOR:play-routes-compiler
// @SOURCE:/mnt/d/Documents/PI/play-java-starter-example/conf/routes
// @DATE:Wed Nov 14 17:11:34 GMT 2018

import play.api.mvc.Call


import _root_.controllers.Assets.Asset
import _root_.play.libs.F

// @LINE:6
package controllers {

  // @LINE:24
  class ReverseSignIn(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:24
    def sign_in(user:String, password:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "sign_in" + play.core.routing.queryString(List(Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("user", user)), Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("password", password)))))
    }
  
  }

  // @LINE:18
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:18
    def versioned(file:Asset): Call = {
      implicit lazy val _rrc = new play.core.routing.ReverseRouteContext(Map(("path", "/public"))); _rrc
      Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[play.api.mvc.PathBindable[Asset]].unbind("file", file))
    }
  
  }

  // @LINE:33
  class ReverseStoreData(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:33
    def store_data(user:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "store_data" + play.core.routing.queryString(List(Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("user", user)))))
    }
  
  }

  // @LINE:21
  class ReverseSignUp(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:21
    def sign_up(user:String, password:String, email:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "sign_up" + play.core.routing.queryString(List(Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("user", user)), Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("password", password)), Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("email", email)))))
    }
  
  }

  // @LINE:27
  class ReverseForgottenPassword(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:27
    def forgotten_password(user:String, email:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "forgotten_password" + play.core.routing.queryString(List(Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("user", user)), Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("email", email)))))
    }
  
  }

  // @LINE:12
  class ReverseCountController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:12
    def count(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "count")
    }
  
  }

  // @LINE:6
  class ReverseHomeController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def write(): Call = {
      
      Call("POST", _prefix)
    }
  
    // @LINE:6
    def index(): Call = {
      
      Call("GET", _prefix)
    }
  
  }

  // @LINE:15
  class ReverseAsyncController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:15
    def message(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "message")
    }
  
  }

  // @LINE:30
  class ReverseRemoveUser(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:30
    def remove_user(user:String, password:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "remove_user" + play.core.routing.queryString(List(Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("user", user)), Some(implicitly[play.api.mvc.QueryStringBindable[String]].unbind("password", password)))))
    }
  
  }


}
