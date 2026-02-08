@echo off
chcp 65001 > nul
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8
title FinanceTracker v1.0.0
java -Dfile.encoding=UTF-8 -jar FinanceTracker.jar
pause