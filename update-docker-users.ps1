Get-ChildItem -Recurse -Filter "Dockerfile" | ForEach-Object {
    $content = Get-Content $_.FullName
    
    # Replace user/group creation commands
    $content = $content -replace 'RUN apt-get update && apt-get install -y wget && \\s+groupadd spring && useradd spring -G spring && \\s+chown -R spring:spring /app', 'RUN apt-get update && apt-get install -y wget && \    groupadd -r spring && useradd -r -g spring spring && \    chown -R spring:spring /app'
    
    Set-Content $_.FullName $content
}
