param(
    [switch]$WhatIf
)

# Installation directory
$installDir = "$env:USERPROFILE\.saffron"
$jreDir = "$installDir\jre"
$jreBinJava = "$jreDir\bin\java.exe"

# Create installation directory if needed
if (-not (Test-Path $installDir)) {
    if ($WhatIf) {
        Write-Host "Would create directory: $installDir"
    } else {
        New-Item -ItemType Directory -Path $installDir | Out-Null
    }
}

# URLs for downloads
$saffronUrl = "https://github.com/senthilvsh/saffron/releases/download/v0.1/saffron.zip"
$jreUrl = "https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/17.0.12+7/openlogic-openjdk-jre-17.0.12+7-windows-x64.zip"

# Function to check if bundled JRE exists and works
function Test-BundledJre {
    if (-not (Test-Path $jreBinJava)) {
        return $false
    }
    
    try {
        $javaVersion = & $jreBinJava -version 2>&1
        return $true
    } catch {
        return $false
    }
}

# Download and install JRE if needed
if (-not (Test-BundledJre)) {
    Write-Host "Bundled JRE not found. Downloading..."
    
    if ($WhatIf) {
        Write-Host "Would download JRE from: $jreUrl"
        Write-Host "Would extract JRE to: $installDir\jre_temp"
    } else {
        # Create temp directory for downloads
        $tempDir = "$installDir\temp"
        New-Item -ItemType Directory -Path $tempDir -Force | Out-Null
        
        # Download JRE
        $jreZipPath = "$tempDir\jre.zip"
        Invoke-WebRequest -Uri $jreUrl -OutFile $jreZipPath
        
        # Extract JRE to temp location first
        Write-Host "Extracting JRE..."
        $jreTempDir = "$tempDir\jre_extract"
        New-Item -ItemType Directory -Path $jreTempDir -Force | Out-Null
        Expand-Archive -Path $jreZipPath -DestinationPath $jreTempDir -Force
        
        # Find the actual JRE directory inside the extracted content
        $extractedJreDir = Get-ChildItem -Path $jreTempDir -Directory | Where-Object { $_.Name -match "jdk|jre" } | Select-Object -First 1
        
        # Create final JRE directory
        if (Test-Path $jreDir) {
            Remove-Item -Path $jreDir -Recurse -Force
        }
        
        # Move extracted JRE to final location
        if ($extractedJreDir) {
            Move-Item -Path $extractedJreDir.FullName -Destination $jreDir
        } else {
            # If we can't find a JRE/JDK directory, just use the whole extracted directory
            Move-Item -Path $jreTempDir -Destination $jreDir
        }
        
        # Clean up
        Remove-Item -Path $jreZipPath -Force
        if (Test-Path $jreTempDir) {
            Remove-Item -Path $jreTempDir -Recurse -Force
        }
    }
}

# Download and install Saffron
Write-Host "Downloading Saffron..."
if ($WhatIf) {
    Write-Host "Would download Saffron from: $saffronUrl"
    Write-Host "Would extract to: $installDir"
} else {
    # Create temp directory for downloads if it doesn't exist
    $tempDir = "$installDir\temp"
    if (-not (Test-Path $tempDir)) {
        New-Item -ItemType Directory -Path $tempDir -Force | Out-Null
    }
    
    # Download Saffron
    $saffronZipPath = "$tempDir\saffron.zip"
    Invoke-WebRequest -Uri $saffronUrl -OutFile $saffronZipPath
    
    # Extract Saffron to temp directory to avoid name collision
    Write-Host "Extracting Saffron..."
    Expand-Archive -Path $saffronZipPath -DestinationPath $tempDir -Force
    
    # Copy JAR file
    Copy-Item -Path "$tempDir\saffron\saffron.jar" -Destination "$installDir\" -Force
    
    # Create launcher script that uses bundled JRE
    $cmdContent = "@echo off`r`n" + 
                  "set JAVA_EXE=""%USERPROFILE%\.saffron\jre\bin\java.exe""`r`n" +
                  "if exist ""%JAVA_EXE%"" (`r`n" +
                  "  ""%JAVA_EXE%"" -jar ""%~dp0saffron.jar"" %*`r`n" +
                  ") else (`r`n" +
                  "  echo Error: Java Runtime not found in .saffron\jre directory.`r`n" +
                  "  echo Please reinstall Saffron.`r`n" +
                  ")"
    Set-Content -Path "$installDir\saffron.cmd" -Value $cmdContent
    
    # Clean up
    Remove-Item -Path $saffronZipPath -Force
    Remove-Item -Path $tempDir -Recurse -Force
}

# Add to PATH if not already there
$userPath = [Environment]::GetEnvironmentVariable("PATH", "User")
if (-not ($userPath -split ";" -contains $installDir)) {
    if ($WhatIf) {
        Write-Host "Would add to PATH: $installDir"
    } else {
        [Environment]::SetEnvironmentVariable("PATH", $userPath + ";" + $installDir, "User")
        Write-Host "Added Saffron to your PATH environment variable."
    }
}

# Success message
Write-Host "Saffron has been installed to $installDir with its own Java Runtime Environment."
Write-Host "You can run Saffron programs using: saffron.cmd 'program.sfr'"
Write-Host ""
Write-Host "For immediate use without restarting, you can run:"
Write-Host "`$env:PATH += ';$installDir'" 