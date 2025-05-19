# Check for Java 17+
try {
    $javaVersion = (& java -version 2>&1 | Select-String -Pattern 'version "([^"]+)"' | ForEach-Object { $_.Matches.Groups[1].Value })
    if (-not $javaVersion) {
        Write-Host "Java not found. Please install Java 17 or higher."
        exit 1
    }
    
    $majorVersion = [int]($javaVersion.Split('.')[0])
    if ($majorVersion -lt 17) {
        Write-Host "Java version $javaVersion detected. Saffron requires Java 17 or higher."
        exit 1
    }
    
    Write-Host "Java $javaVersion detected."
} catch {
    Write-Host "Java not found. Please install Java 17 or higher."
    exit 1
}

# Create installation directory
$installDir = "$env:USERPROFILE\.saffron"
if (-not (Test-Path $installDir)) {
    New-Item -ItemType Directory -Path $installDir | Out-Null
}

# Download latest release
Write-Host "Downloading Saffron..."
$downloadUrl = "https://github.com/senthilvsh/saffron/releases/download/v0.1/saffron.zip"
Invoke-WebRequest -Uri $downloadUrl -OutFile "$installDir\saffron.zip"

# Extract the ZIP file
Write-Host "Extracting..."
Expand-Archive -Path "$installDir\saffron.zip" -DestinationPath "$installDir\temp" -Force
Remove-Item -Path "$installDir\saffron.zip"

# Fix nested directory structure
Write-Host "Installing Saffron..."

# Copy JAR file and cmd script
Copy-Item -Path "$installDir\temp\saffron\saffron.jar" -Destination "$installDir\" -Force
Copy-Item -Path "$installDir\temp\saffron\saffron.cmd" -Destination "$installDir\" -Force

# Create the main launcher script (avoiding name collision)
# For Windows, we use saffron.cmd, so we don't need the shell script named 'saffron'

# Clean up
Remove-Item -Path "$installDir\temp" -Recurse -Force

# Add to PATH
$userPath = [Environment]::GetEnvironmentVariable("PATH", "User")
if (-not $userPath.Contains($installDir)) {
    [Environment]::SetEnvironmentVariable("PATH", $userPath + ";" + $installDir, "User")
    Write-Host "Added Saffron to your PATH environment variable."
}

Write-Host "Saffron has been installed to $installDir"
Write-Host "Please restart your PowerShell window to use Saffron."
Write-Host "Then you can run Saffron programs using: saffron.cmd 'program.sfr'"
Write-Host ""
Write-Host "For immediate use without restarting, you can run:"
Write-Host "`$env:PATH += ';$installDir'" 