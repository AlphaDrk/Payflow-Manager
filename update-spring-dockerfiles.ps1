$services = @(
    "client-service",
    "reglement-service",
    "produit-service",
    "factureservice",
    "authentificationservice",
    "gatewayservice"
)

$oldUserCreation = 'RUN apt-get update && apt-get install -y wget && \
    groupadd spring && useradd spring -G spring && \
    chown -R spring:spring /app'

$newUserCreation = 'RUN apt-get update && apt-get install -y wget && \
    groupadd -r spring && useradd -r -g spring spring && \
    chown -R spring:spring /app'

foreach ($service in $services) {
    $dockerfilePath = Join-Path -Path $PSScriptRoot -ChildPath "$service\Dockerfile"
    if (Test-Path $dockerfilePath) {
        Write-Host "Updating $service Dockerfile..."
        $content = Get-Content $dockerfilePath -Raw
        $updatedContent = $content -replace [regex]::Escape($oldUserCreation), $newUserCreation
        Set-Content -Path $dockerfilePath -Value $updatedContent
    }
}

Write-Host "All Spring service Dockerfiles have been updated."
