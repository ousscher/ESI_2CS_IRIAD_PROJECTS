@echo off
call run-main-container.bat
timeout /t 3
start cmd /k "run-container1.bat"
start cmd /k "run-container2.bat"