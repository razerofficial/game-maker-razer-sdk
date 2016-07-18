SET JDK7=c:\NVPACK\jdk1.7.0_71
CALL gradlew clean build
COPY /Y pluginrazersdk\build\outputs\aar\pluginrazersdk-release.aar ..\InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y pluginrazersdk\build\outputs\aar\pluginrazersdk-release.aar ..\VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs

COPY /Y pluginrazersdk\libs\store-sdk-standard-release.aar ..\InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y pluginrazersdk\libs\store-sdk-standard-release.aar ..\VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs

CD pluginrazersdk\build\outputs\aar
IF EXIST classes.jar DEL classes.jar
"%JDK7%\bin\jar.exe" -xvf pluginrazersdk-release.aar classes.jar
IF EXIST pluginrazersdk-release.jar DEL pluginrazersdk-release.jar
RENAME classes.jar pluginrazersdk-release.jar
CD ..\..\..\..\
COPY /Y pluginrazersdk\build\outputs\aar\pluginrazersdk-release.jar ..\InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y pluginrazersdk\build\outputs\aar\pluginrazersdk-release.jar ..\VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs
DEL pluginrazersdk\build\outputs\aar\pluginrazersdk-release.aar

CD pluginrazersdk\libs
IF EXIST classes.jar DEL classes.jar
"%JDK7%\bin\jar.exe" -xvf store-sdk-standard-release.aar classes.jar
IF EXIST store-sdk-standard-release.jar DEL store-sdk-standard-release.jar
RENAME classes.jar store-sdk-standard-release.jar
CD ..\..\
COPY /Y pluginrazersdk\libs\store-sdk-standard-release.jar ..\InAppPurchases.gmx\extensions\RazerSDK\AndroidSource\libs
COPY /Y pluginrazersdk\libs\store-sdk-standard-release.jar ..\VirtualController.gmx\extensions\RazerSDK\AndroidSource\libs
DEL pluginrazersdk\libs\store-sdk-standard-release.jar

PAUSE
