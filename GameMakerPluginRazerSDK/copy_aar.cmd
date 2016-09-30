SET JDK7=c:\NVPACK\jdk1.7.0_71
CD ..
COPY /Y pluginrazersdk-release.aar InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y pluginrazersdk-release.aar VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs

COPY /Y store-sdk-standard-release.aar InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y store-sdk-standard-release.aar VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs

IF EXIST classes.jar DEL classes.jar
"%JDK7%\bin\jar.exe" -xvf pluginrazersdk-release.aar classes.jar
IF EXIST pluginrazersdk-release.jar DEL pluginrazersdk-release.jar
RENAME classes.jar pluginrazersdk-release.jar

COPY /Y pluginrazersdk-release.jar InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y pluginrazersdk-release.jar VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs

IF EXIST classes.jar DEL classes.jar
"%JDK7%\bin\jar.exe" -xvf store-sdk-standard-release.aar classes.jar
IF EXIST store-sdk-standard-release.jar DEL store-sdk-standard-release.jar
RENAME classes.jar store-sdk-standard-release.jar

COPY /Y store-sdk-standard-release.jar InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y store-sdk-standard-release.jar VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs

PAUSE
