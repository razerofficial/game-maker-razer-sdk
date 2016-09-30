SET JDK7=c:\NVPACK\jdk1.7.0_71
CALL gradlew clean build
COPY /Y pluginrazersdk\libs\store-sdk-standard-release.aar ..
COPY /Y pluginrazersdk\build\outputs\aar\pluginrazersdk-release.aar ..
CALL copy_aar.cmd
