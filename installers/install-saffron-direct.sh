#!/bin/bash
# This script provides a universal wget/curl command to install Saffron across all platforms

# Define the installation command
echo "==== Saffron One-Line Installation ===="
echo ""
echo "To install Saffron, copy and paste one of these commands based on your OS and available tools:"
echo ""
echo "=== For Linux/macOS (using curl) ==="
echo "bash -c \"$(curl -fsSL https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install-saffron.sh)\""
echo ""
echo "=== For Linux/macOS (using wget) ==="
echo "bash -c \"$(wget -qO- https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install-saffron.sh)\""
echo ""
echo "=== For Windows (using PowerShell) ==="
echo "powershell -Command \"Invoke-Expression (New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install-saffron-windows.ps1')\""
echo ""
echo "These commands will automatically download and run the appropriate installer for your platform."
echo "All installers require Java 17 or higher to be installed on your system." 