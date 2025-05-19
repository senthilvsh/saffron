# Saffron Installation Scripts

This directory contains scripts to install Saffron programming language across different operating systems.

## Available Scripts

1. **Universal Installers**
   - `install-saffron.sh` - The main installer that works on both Linux and macOS
   - `install-saffron-unix.sh` - Identical to install-saffron.sh (alternative name)

2. **Windows Installer**
   - `install-saffron-windows.ps1` - Windows PowerShell installation script

3. **Helper Script**
   - `install-saffron-direct.sh` - Displays one-line installation commands for all platforms

## How to Install

### For Linux/macOS Users

**One-line installation command:**
```bash
curl -sSL https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install-saffron.sh | bash
```
or using wget:
```bash
wget -qO- https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install-saffron.sh | bash
```

### For Windows Users

**One-line installation command (PowerShell):**
```powershell
Invoke-Expression (New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install-saffron-windows.ps1')
```

## What the Installer Does

The installer performs these steps:
1. Checks if Java 17 or higher is installed
2. Downloads the latest Saffron release from GitHub
3. Extracts the package and sets up necessary files
4. Makes sure scripts are executable
5. Adds Saffron to your PATH environment variable 
6. Gives instructions for completing installation

## Requirements

- Java 17 or higher must be installed
- Linux/macOS: Bash shell
- Windows: PowerShell 3.0 or higher
- Internet connection to download Saffron

## After Installation

After installation, you can run Saffron programs using:
```bash
saffron "your-program.sfr"
```

## Troubleshooting

If the `saffron` command is not found after installation:
1. Run the source command shown by the installer to update your PATH
2. Restart your terminal session
3. If problems persist, add the following to your shell profile manually:
   ```bash
   export PATH="$PATH:$HOME/.saffron"
   ``` 