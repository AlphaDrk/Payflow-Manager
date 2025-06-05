$services = @(
    "client-service",
    "reglement-service",
    "produit-service",
    "factureservice",
    "authentificationservice",
    "gatewayservice",
    "eureka-discoveryservice"
)

$oldEntrypoint = 'ENTRYPOINT exec java $JAVA_OPTS -jar app.jar'
$newEntrypoint = 'ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]'

foreach ($service in $services) {
    $dockerfilePath = Join-Path -Path $PSScriptRoot -ChildPath "$service\Dockerfile"
    if (Test-Path $dockerfilePath) {
        Write-Host "Updating $service Dockerfile..."
        $content = Get-Content $dockerfilePath -Raw
        if ($content -match [regex]::Escape($oldEntrypoint)) {
            $updatedContent = $content -replace [regex]::Escape($oldEntrypoint), $newEntrypoint
            Set-Content -Path $dockerfilePath -Value $updatedContent
            Write-Host "Updated ENTRYPOINT format in $service"
        } else {
            Write-Host "No changes needed for $service"
        }
    }
}

Write-Host "All Spring service Dockerfiles have been updated."
