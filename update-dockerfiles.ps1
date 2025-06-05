Get-ChildItem -Recurse -Filter "Dockerfile" | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    
    # Replace Alpine-based images with Debian-based ones
    $content = $content -replace 'FROM eclipse-temurin:17-jre-alpine', 'FROM openjdk:17-slim'
    $content = $content -replace 'FROM node:18-alpine', 'FROM node:18'
    $content = $content -replace 'FROM nginx:alpine', 'FROM nginx:stable'
    
    # Replace Alpine commands with Debian equivalents
    $content = $content -replace 'apk add --no-cache wget', 'apt-get update && apt-get install -y wget'
    $content = $content -replace 'addgroup -S spring && adduser -S', 'groupadd spring && useradd'
    
    Set-Content $_.FullName $content
}
