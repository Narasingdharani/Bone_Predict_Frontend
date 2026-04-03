@echo off
cd /d "%~dp0"
echo Starting the AlveoPredict Website and Unified Backend...
echo --------------------------------------------------------
echo The website will be available at: http://localhost:8000
echo Keep this window open while you are using the app or website.
echo --------------------------------------------------------
python website\app.py
pause
