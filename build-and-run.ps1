# Function to display colored status messages
function Write-Status {
    param(
        [string]$Message,
        [string]$Status,
        [string]$Color = 'White'
    )
    Write-Host "$Message" -NoNewline
    Write-Host " [$Status]" -ForegroundColor $Color
}

# Stop all running containers
Write-Host "`nStopping all running containers..." -ForegroundColor Yellow
docker-compose down
Write-Status "Containers stopped" "OK" "Green"

# Clean existing images
Write-Host "`nCleaning existing images..." -ForegroundColor Yellow
docker image prune -f
Write-Status "Images cleaned" "OK" "Green"

# Build all Maven projects
$services = @(
    "config-service",
    "eureka-discoveryservice",
    "gatewayservice",
    "authentificationservice",
    "produit-service",
    "client-service",
    "factureservice",
    "reglement-service"
)

Write-Host "`nBuilding Maven projects..." -ForegroundColor Yellow
foreach ($service in $services) {
    Write-Host "Building $service..." -NoNewline
    Set-Location $service
    
    # Clean and package with Maven, skipping tests
    ./mvnw clean package -DskipTests
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host " [OK]" -ForegroundColor Green
    } else {
        Write-Host " [FAILED]" -ForegroundColor Red
        Write-Host "Build failed for $service. Exiting..."
        exit 1
    }
    
    Set-Location ..
}

# Build frontend (Angular)
Write-Host "`nBuilding frontend (PayFlow_Manager)..." -ForegroundColor Yellow
Set-Location PayFlow_Manager
npm install
npm run build
if ($LASTEXITCODE -eq 0) {
    Write-Host " [OK]" -ForegroundColor Green
} else {
    Write-Host " [FAILED]" -ForegroundColor Red
    Write-Host "Frontend build failed. Exiting..."
    exit 1
}
Set-Location ..

# Build Docker images and start services in order
Write-Host "`nBuilding and starting services..." -ForegroundColor Yellow

# Start MySQL first
Write-Host "Starting MySQL..." -NoNewline
docker-compose up -d mysql
Start-Sleep -Seconds 10
Write-Host " [OK]" -ForegroundColor Green

# Start Config Server
Write-Host "Starting Config Service..." -NoNewline
docker-compose up -d config-service
Start-Sleep -Seconds 15
Write-Host " [OK]" -ForegroundColor Green

# Start Eureka Server
Write-Host "Starting Eureka Service..." -NoNewline
docker-compose up -d eureka-service
Start-Sleep -Seconds 15
Write-Host " [OK]" -ForegroundColor Green

# Start Gateway
Write-Host "Starting Gateway Service..." -NoNewline
docker-compose up -d gateway-service
Start-Sleep -Seconds 10
Write-Host " [OK]" -ForegroundColor Green

# Start remaining services
$remainingServices = @(
    "auth-service",
    "produit-service",
    "client-service",
    "facture-service",
    "reglement-service"
)

foreach ($service in $remainingServices) {
    Write-Host "Starting $service..." -NoNewline
    docker-compose up -d $service
    Start-Sleep -Seconds 15
    Write-Host " [OK]" -ForegroundColor Green
}

# Start frontend container
Write-Host "Starting frontend..." -NoNewline
docker-compose up -d frontend
Start-Sleep -Seconds 10
Write-Host " [OK]" -ForegroundColor Green

# Verify all services are running
Write-Host "`nVerifying service status..." -ForegroundColor Yellow
docker-compose ps

Write-Host "`nAll services should be up and running!" -ForegroundColor Green
Write-Host "You can check the logs of any service using: docker-compose logs [service-name]"
Write-Host "To check Eureka dashboard, visit: http://localhost:8761"
Write-Host "To check Gateway status, visit: http://localhost:8888"
Write-Host "To access the frontend, visit: http://localhost:4200"
