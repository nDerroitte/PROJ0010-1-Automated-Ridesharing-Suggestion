#include "AppDelegate.h"
#include "GeneratedPluginRegistrant.h"

// Add the import for the GeofencingPlugin.
#import <geofencing/GeofencingPlugin.h>

void registerPlugins(NSObject<FlutterPluginRegistry>* registry) {
  [GeneratedPluginRegistrant registerWithRegistry:registry];
}

@implementation AppDelegate

- (BOOL)application:(UIApplication *)application
    didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
  // Register the plugins with the AppDelegate
  registerPlugins(self);
  // Set registerPlugins as a callback within GeofencingPlugin. This allows
  // for the Geofencing plugin to register the plugins with the background
  // FlutterEngine instance created to handle events. If this step is skipped,
  // other plugins will not work in the geofencing callbacks!
  [GeofencingPlugin setPluginRegistrantCallback:registerPlugins];

  // Override point for customization after application launch.
  return [super application:application didFinishLaunchingWithOptions:launchOptions];
}

@end