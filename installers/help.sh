#!/bin/bash
# This script provides universal one-line commands to install Saffron across all platforms

# Define the installation command
echo "==== Saffron One-Line Installation ===="
echo ""
echo "To install Saffron, copy and paste one of these commands based on your OS and available tools:"
echo ""
echo "=== For Linux/macOS (using curl) ==="
echo "bash -c \"$(curl -fsSL https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install.sh)\""
echo ""
echo "=== For Linux/macOS (using wget) ==="
echo "bash -c \"$(wget -qO- https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install.sh)\""
echo ""
echo "=== For Windows (using PowerShell) ==="
echo "powershell -Command \"Invoke-Expression (New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/windows.ps1')\""
echo ""
echo "These commands will automatically download and install Saffron with a bundled Java Runtime Environment."
echo "No Java installation is required - the installer will set up everything for you."
echo ""
echo "To run in test mode (no changes made):"
echo "- Linux/macOS: Add '--whatif' to the end of the command"
echo "- Windows: Add '-WhatIf' to the end of the command" 