ECHO ON
COPY /Y store-sdk-standard-release.aar GameMakerPluginRazerSDK\pluginrazersdk\libs
COPY /Y store-sdk-standard-release.aar InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y store-sdk-standard-release.jar InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y store-sdk-standard-release.aar VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y store-sdk-standard-release.jar VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs
PAUSE
