$images = @(
    "openjdk:17-slim",
    "mysql:8.0",
    "node:18",
    "nginx:alpine"
)

foreach ($image in $images) {
    Write-Host "Pulling $image..."
    docker pull $image
}

Write-Host "All images pulled successfully!"
