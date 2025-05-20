# Saffron Installation Scripts

This directory contains scripts to install Saffron programming language across different operating systems.

## Available Scripts

1. **Universal Installer**
   - `install.sh` - Detects OS and architecture, then runs the appropriate installer

2. **Platform-Specific Installers**
   - `linux.sh` - Installer for Linux and macOS with bundled JRE
   - `windows.ps1` - Windows PowerShell installer with bundled JRE

3. **Helper Script**
   - `help.sh` - Displays one-line installation commands for all platforms

## How to Install

### For Linux/macOS Users

**One-line installation command:**
```bash
curl -sSL https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/saffron.sh | bash
```
or using wget:
```bash
wget -qO- https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/saffron.sh | bash
```

### For Windows Users

**One-line installation command (PowerShell):**
```powershell
Invoke-Expression (New-Object System.Net.WebClient).DownloadString('https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install/windows.ps1')
```

## What the Installer Does

The installer performs these steps:
1. Detects your operating system and architecture
2. Downloads and installs OpenJDK JRE 17 (no need to have Java installed)
3. Downloads the latest Saffron release from GitHub
4. Extracts the package and sets up necessary files
5. Creates launcher scripts that use the bundled JRE
6. Adds Saffron to your PATH environment variable 

## Supported Platforms

- **Windows**: x64
- **macOS**: x64 and ARM64 (Apple Silicon)
- **Linux**: x64 and ARM64

## Test Mode

All installers support a test mode that shows what would happen without making any changes:

- For Unix/macOS: `./linux.sh --whatif`
- For Windows PowerShell: `./windows.ps1 -WhatIf`

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

If you encounter JRE issues:
1. The installer creates a bundled JRE in `$HOME/.saffron/jre`
2. Verify this directory exists and contains Java binaries
3. If needed, reinstall using the installer script 