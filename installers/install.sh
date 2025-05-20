#!/bin/bash

# Detect OS and show appropriate message
detect_os() {
    if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" || "$OSTYPE" == "win32" ]]; then
        echo "Windows detected."
        echo "This script cannot be used on Windows."
        echo "Please use PowerShell and run the following command instead:"
        echo ""
        echo "Invoke-Expression (New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/windows.ps1')"
        exit 1
    elif [[ "$(uname)" == "Darwin" ]]; then
        echo "macOS detected."
        os="mac"
    else
        echo "Linux detected."
        os="linux"
    fi
}

# Detect architecture
detect_arch() {
    arch="x64"
    if [[ "$(uname -m)" == "arm64" || "$(uname -m)" == "aarch64" ]]; then
        arch="aarch64"
    fi
    echo "Architecture: $arch"
}

# Pass through any arguments (like --whatif)
pass_args=""
if [[ "$1" == "--whatif" ]]; then
    pass_args="--whatif"
fi

# Main execution
detect_os
detect_arch

# Run the Unix installer
echo "Starting Saffron installation for $os ($arch)..."
bash "$(dirname "$0")/linux.sh" $pass_args