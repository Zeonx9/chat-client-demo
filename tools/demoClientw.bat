@echo off
set JLINK_VM_OPTIONS=
set DIR=%~dp0
start "" "%DIR%\javaw" %JLINK_VM_OPTIONS% -m com.ade.chatclient/com.ade.chatclient.ClientApplication %* && exit 0
