@echo off
set BASE=http://localhost:7979/api/v1

echo === GET /config/status ===
curl -s -X GET %BASE%/config/status
echo.
echo.

echo === GET /config/business-details ===
curl -s -X GET %BASE%/config/business-details
echo.
echo.

echo === POST /config/setup ===
curl -s -X POST %BASE%/config/setup ^
  -H "Content-Type: application/json" ^
  -d "{\"businessName\":\"Test\",\"username\":\"admin\",\"pin\":\"1234\",\"brandColor\":\"#FF6B35\"}"
echo.
echo.

pause
