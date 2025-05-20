#!/bin/bash

# Detect the operating system
if [[ "$OSTYPE" == "linux-gnu"* ]]; then
    OS="linux"
elif [[ "$OSTYPE" == "darwin"* ]]; then
    OS="mac"
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" || "$OSTYPE" == "win32" ]]; then
    OS="windows"
else
    echo "Unsupported operating system: $OSTYPE"
    exit 1
fi

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Download the appropriate installer based on OS
echo "Detected operating system: $OS"
echo "Downloading Saffron installer for $OS..."

BASE_URL="https://raw.githubusercontent.com/senthilvsh/saffron/master/installers"
INSTALLER_URL="$BASE_URL/install-saffron-$OS"
if [[ "$OS" == "windows" ]]; then
    INSTALLER_URL="${INSTALLER_URL}.ps1"
    # For Windows, download the PowerShell script
    if command_exists curl; then
        curl -sSL "$INSTALLER_URL" -o install-saffron-windows.ps1
        echo "Downloaded Windows installer. Please run the PowerShell script:"
        echo "powershell -ExecutionPolicy Bypass -File install-saffron-windows.ps1"
    elif command_exists wget; then
        wget -q "$INSTALLER_URL" -O install-saffron-windows.ps1
        echo "Downloaded Windows installer. Please run the PowerShell script:"
        echo "powershell -ExecutionPolicy Bypass -File install-saffron-windows.ps1"
    else
        echo "Error: Neither curl nor wget is installed. Please install one of them and try again."
        exit 1
    fi
else
    # For Unix systems, use the Unix installer script
    if command_exists curl; then
        curl -sSL "$BASE_URL/install-saffron-unix.sh" -o "install-saffron-unix.sh"
        chmod +x "install-saffron-unix.sh"
        echo "Running installer for $OS..."
        ./install-saffron-unix.sh
    elif command_exists wget; then
        wget -q "$BASE_URL/install-saffron-unix.sh" -O "install-saffron-unix.sh"
        chmod +x "install-saffron-unix.sh"
        echo "Running installer for $OS..."
        ./install-saffron-unix.sh
    else
        echo "Error: Neither curl nor wget is installed. Please install one of them and try again."
        exit 1
    fi
fi 