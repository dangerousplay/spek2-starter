@if "%DEBUG%" == "" @echo off

set CMD_LINE_ARGS=%*
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

"%JAVA_EXE%" -jar kroto\protoc-gen-kroto-plus-0.3.0-jvm8.jar %CMD_LINE_ARGS%