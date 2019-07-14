@echo off
echo Deleting CVS folders and files under: %1
REM Open Folder specified by parameter.
cd %1
REM Recursive delete command
for /f "tokens=*" %%i in ('dir /b/a/s CVS*') do @rmdir /q /s "%%i"
echo Done!
pause