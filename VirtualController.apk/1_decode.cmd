call init.cmd

rm -r -f %PROJNAME%
"%JDK7%\bin\java.exe" -jar %APKTOOL% decode %PROJNAME%.apk
if NOT "%NO_PAUSE%"=="true" PAUSE
