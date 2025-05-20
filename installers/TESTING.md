# Testing Saffron Installers

This document explains how to test the Saffron installation scripts before releasing them.

## Automated Testing

We provide a comprehensive test script that checks:
1. Shell script syntax
2. Functionality in test mode
3. OS and architecture detection

To run the automated tests:

```bash
# From the project root
bash installers/test-installers.sh
```

The test script will:
- Check syntax of all shell scripts
- Run the installers in test mode (`--whatif` or `-WhatIf`)
- Verify the installers correctly detect OS and architecture
- Check for expected behavior in test output

## Manual Testing

### Unix (Linux/macOS)

Test in dry-run mode:

```bash
# From the project root
./installers/install-saffron-unix.sh --whatif
```

Test for real (creates ~/.saffron directory and installs Saffron):

```bash
# From the project root
./installers/install-saffron-unix.sh
```

### Windows (PowerShell)

Test in dry-run mode:

```powershell
# From the project root
./installers/install-saffron-windows.ps1 -WhatIf
```

Test for real (creates ~/.saffron directory and installs Saffron):

```powershell
# From the project root
./installers/install-saffron-windows.ps1
```

## Testing on Different Architectures

For thorough testing, try to test on:
- x86-64 Linux
- ARM64 Linux (if available)
- Intel macOS
- Apple Silicon macOS
- Windows 10/11

## Common Issues and Solutions

### Permission Issues
If you encounter permission issues:
```bash
chmod +x installers/*.sh
```

### PATH Issues
If Saffron isn't in PATH after installation:
- Check the shell profile files (.zshrc, .bashrc, etc.)
- Ensure the PATH export line was added
- Run the source command shown by the installer

### JRE Issues
If the bundled JRE doesn't work:
- Check that the download URL is correct for the architecture
- Verify the extraction process worked correctly
- Try to manually execute the java binary in `.saffron/jre/bin` 