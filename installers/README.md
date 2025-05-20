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
1. Creates a `.saffron` directory in your home folder
2. Downloads and installs Java JRE 17 into the `.saffron/jre` directory
   - **No need to install Java separately!**
3. Downloads the latest Saffron release from GitHub
4. Creates launcher scripts that use the bundled JRE
5. Adds Saffron to your PATH environment variable 
6. Gives instructions for completing installation

## Features

- **No Java Installation Required**: Saffron now comes with its own Java Runtime Environment
- **Self-contained**: All Saffron components are installed in a single directory
- **Won't Interfere**: Doesn't modify any system-wide Java installations
- **Architecture Aware**: Automatically selects the right JRE for your system (x64, ARM64)

## Requirements

- Internet connection to download Saffron and JRE
- Linux/macOS: Bash shell
- Windows: PowerShell 3.0 or higher

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

## Testing Mode

Both installers support a testing mode:

For Windows:
```powershell
.\install-saffron-windows.ps1 -WhatIf
```

For Linux/macOS:
```bash
./install-saffron-unix.sh --whatif
```

This will show what actions would be taken without actually performing them. 