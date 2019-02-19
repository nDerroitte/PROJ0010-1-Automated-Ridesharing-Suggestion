// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/cedri_000/PI/server/conf/routes
// @DATE:Tue Feb 19 12:48:04 CET 2019

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseSignIn SignIn = new controllers.ReverseSignIn(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseStoreData StoreData = new controllers.ReverseStoreData(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseSignUp SignUp = new controllers.ReverseSignUp(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseForgottenPassword ForgottenPassword = new controllers.ReverseForgottenPassword(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseCountController CountController = new controllers.ReverseCountController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseHomeController HomeController = new controllers.ReverseHomeController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAsyncController AsyncController = new controllers.ReverseAsyncController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseRemoveUser RemoveUser = new controllers.ReverseRemoveUser(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseSignIn SignIn = new controllers.javascript.ReverseSignIn(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseStoreData StoreData = new controllers.javascript.ReverseStoreData(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseSignUp SignUp = new controllers.javascript.ReverseSignUp(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseForgottenPassword ForgottenPassword = new controllers.javascript.ReverseForgottenPassword(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseCountController CountController = new controllers.javascript.ReverseCountController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseHomeController HomeController = new controllers.javascript.ReverseHomeController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAsyncController AsyncController = new controllers.javascript.ReverseAsyncController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseRemoveUser RemoveUser = new controllers.javascript.ReverseRemoveUser(RoutesPrefix.byNamePrefix());
  }

}
