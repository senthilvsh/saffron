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
$jreUrl = "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.8%2B7/OpenJDK17U-jre_x64_windows_hotspot_17.0.8_7.zip"

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
        Write-Host "Downloading JRE from: $jreUrl"
        Invoke-WebRequest -Uri $jreUrl -OutFile $jreZipPath
        
        # Check if download was successful
        if ((Get-Item $jreZipPath).Length -eq 0) {
            Write-Host "Error: Failed to download JRE. The downloaded file is empty."
            exit 1
        }
        
        # Extract JRE to temp location first
        Write-Host "Extracting JRE..."
        $jreTempDir = "$tempDir\jre_extract"
        New-Item -ItemType Directory -Path $jreTempDir -Force | Out-Null
        
        try {
            Expand-Archive -Path $jreZipPath -DestinationPath $jreTempDir -Force
        } catch {
            Write-Host "Error extracting JRE archive: $_"
            exit 1
        }
        
        # Debug output
        Write-Host "Contents of extract directory:"
        Get-ChildItem -Path $jreTempDir | Format-Table
        
        # Find the actual JRE directory inside the extracted content
        $extractedJreDir = $null
        
        # First try direct subdirectories
        $extractedJreDir = Get-ChildItem -Path $jreTempDir -Directory | 
                           Where-Object { $_.Name -match "jdk|jre|OpenJDK" } | 
                           Select-Object -First 1
                           
        # If no bin directory in the direct match, look for nested structure
        if ($extractedJreDir -and -not (Test-Path "$($extractedJreDir.FullName)\bin")) {
            Write-Host "JRE directory found but doesn't contain a bin directory."
            Write-Host "Looking for nested JRE structure..."
            
            # Try to find a nested JRE directory
            $nestedJre = Get-ChildItem -Path $extractedJreDir.FullName -Directory | 
                         Where-Object { $_.Name -match "jre|jdk|OpenJDK" } |
                         Select-Object -First 1
                         
            if ($nestedJre -and (Test-Path "$($nestedJre.FullName)\bin")) {
                Write-Host "Found nested JRE at: $($nestedJre.FullName)"
                $extractedJreDir = $nestedJre
            } else {
                # Try to find any directory with bin\java.exe
                $javaExePath = Get-ChildItem -Path $extractedJreDir.FullName -Recurse -Filter "java.exe" |
                              Where-Object { $_.DirectoryName -match "\\bin$" } |
                              Select-Object -First 1
                
                if ($javaExePath) {
                    $binDir = Split-Path $javaExePath.FullName -Parent
                    $nestedJreDir = Split-Path $binDir -Parent
                    Write-Host "Found nested JRE via bin\java.exe at: $nestedJreDir"
                    $extractedJreDir = Get-Item $nestedJreDir
                }
            }
        }
        
        # If still not found, look for java.exe recursively
        if (-not $extractedJreDir -or -not (Test-Path "$($extractedJreDir.FullName)\bin")) {
            $javaExePath = Get-ChildItem -Path $jreTempDir -Recurse -Filter "java.exe" |
                          Where-Object { $_.DirectoryName -match "\\bin$" } |
                          Select-Object -First 1
            
            if ($javaExePath) {
                $binDir = Split-Path $javaExePath.FullName -Parent
                $jreDirPath = Split-Path $binDir -Parent
                Write-Host "Found JRE via bin\java.exe at: $jreDirPath"
                $extractedJreDir = Get-Item $jreDirPath
            }
        }
        
        if (-not $extractedJreDir) {
            Write-Host "Error: Could not find JRE directory in the extracted content."
            Write-Host "Contents of extract directory:"
            Get-ChildItem -Path $jreTempDir -Recurse | Format-Table
            exit 1
        }
        
        Write-Host "Found JRE at: $($extractedJreDir.FullName)"
        
        # Final validation - ensure bin directory exists
        if (-not (Test-Path "$($extractedJreDir.FullName)\bin")) {
            Write-Host "Error: Cannot find a valid JRE structure with bin directory."
            Write-Host "Contents of the found directory:"
            Get-ChildItem -Path $extractedJreDir.FullName | Format-Table
            exit 1
        }
        
        # Create final JRE directory
        if (Test-Path $jreDir) {
            Remove-Item -Path $jreDir -Recurse -Force
        }
        
        # Move extracted JRE to final location
        Move-Item -Path $extractedJreDir.FullName -Destination $jreDir
        
        # Verify bin directory exists
        if (-not (Test-Path "$jreDir\bin")) {
            Write-Host "Error: bin directory not found in extracted JRE."
            Write-Host "Contents of JRE directory:"
            Get-ChildItem -Path $jreDir | Format-Table
            exit 1
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