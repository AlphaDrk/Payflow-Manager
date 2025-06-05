$services = @(
    "authentificationservice",
    "client-service",
    "config-service",
    "eureka-discoveryservice",
    "factureservice",
    "gatewayservice",
    "produit-service",
    "reglement-service"
)

foreach ($service in $services) {
    Write-Host "Building $service..."
    Set-Location $service
    .\mvnw clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "Error building $service"
        exit 1
    }
    Set-Location ..
}

Write-Host "All services built successfully!"
