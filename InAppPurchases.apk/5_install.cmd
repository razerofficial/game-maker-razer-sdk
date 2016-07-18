CALL init.cmd
adb install -r %PROJNAME%Aligned.apk
if NOT "%NO_PAUSE%"=="true" PAUSE
