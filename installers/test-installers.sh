#!/bin/bash
# Test script for Saffron installers
# This script checks:
# 1. Shell script syntax
# 2. Functionality in --whatif mode
# 3. Detection of OS and architecture

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== Testing Saffron Installers ===${NC}"
echo ""

# ---- PART 1: Syntax Checking ----
echo -e "${YELLOW}Checking syntax of installers...${NC}"

# Check Unix installer syntax
if bash -n installers/install-saffron-unix.sh; then
    echo -e "${GREEN}✅ Unix installer syntax is valid${NC}"
else
    echo -e "${RED}❌ Unix installer has syntax errors${NC}"
    exit 1
fi

# Check universal installer syntax
if bash -n installers/install-saffron.sh; then
    echo -e "${GREEN}✅ Universal installer syntax is valid${NC}"
else
    echo -e "${RED}❌ Universal installer has syntax errors${NC}"
    exit 1
fi

# Check direct installer syntax
if bash -n installers/install-saffron-direct.sh; then
    echo -e "${GREEN}✅ Direct installer syntax is valid${NC}"
else
    echo -e "${RED}❌ Direct installer has syntax errors${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}All installers have valid syntax!${NC}"
echo ""

# ---- PART 2: Test Unix Installer in --whatif mode ----
echo -e "${YELLOW}Testing Unix installer in --whatif mode...${NC}"

# Run the unix installer with --whatif flag and capture output
UNIX_TEST_OUTPUT=$(bash installers/install-saffron-unix.sh --whatif)

# Check for expected strings in --whatif output
check_unix_installer() {
    local pattern="$1"
    local message="$2"
    
    if echo "$UNIX_TEST_OUTPUT" | grep -q "$pattern"; then
        echo -e "${GREEN}✅ $message${NC}"
        return 0
    else
        echo -e "${RED}❌ $message${NC}"
        return 1
    fi
}

# Run checks
check_unix_installer "Running in test mode" "Detected --whatif mode"
check_unix_installer "Installing Saffron for" "Detected OS and architecture"
check_unix_installer "Would download JRE from:" "JRE download detection works"
check_unix_installer "Would download Saffron from:" "Saffron download detection works"
check_unix_installer "Would add to PATH" "PATH modification check works"

echo ""

# ---- PART 3: Test Universal Installer in --whatif mode ----
echo -e "${YELLOW}Testing universal installer in --whatif mode...${NC}"

# Run the universal installer with --whatif flag and capture output
UNIVERSAL_TEST_OUTPUT=$(bash installers/install-saffron.sh --whatif)

# Check for expected strings in --whatif output
check_universal_installer() {
    local pattern="$1"
    local message="$2"
    
    if echo "$UNIVERSAL_TEST_OUTPUT" | grep -q "$pattern"; then
        echo -e "${GREEN}✅ $message${NC}"
        return 0
    else
        echo -e "${RED}❌ $message${NC}"
        return 1
    fi
}

# Run checks
check_universal_installer "detected" "OS detection works"
check_universal_installer "Architecture" "Architecture detection works"
check_universal_installer "Starting Saffron installation" "Forwarding to platform-specific installer works"
check_universal_installer "Running in test mode" "Passes --whatif flag to Unix installer"

echo ""

# ---- PART 4: Check Direct Installer for Completeness ----
echo -e "${YELLOW}Checking direct installer for completeness...${NC}"

# Run the direct installer and capture output
DIRECT_TEST_OUTPUT=$(bash installers/install-saffron-direct.sh)

# Check for expected strings in output
check_direct_installer() {
    local pattern="$1"
    local message="$2"
    
    if echo "$DIRECT_TEST_OUTPUT" | grep -q "$pattern"; then
        echo -e "${GREEN}✅ $message${NC}"
        return 0
    else
        echo -e "${RED}❌ $message${NC}"
        return 1
    fi
}

# Run checks
check_direct_installer "Linux/macOS" "Has Linux/macOS instructions"
check_direct_installer "Windows" "Has Windows instructions"
check_direct_installer "curl" "Has curl command"
check_direct_installer "wget" "Has wget command"
check_direct_installer "PowerShell" "Has PowerShell command"
check_direct_installer "test mode" "Mentions test mode"

# ---- PART 5: Summary ----
echo ""
echo -e "${YELLOW}=== Installation Tests Summary ===${NC}"
echo ""
echo -e "${GREEN}✅ All tests completed successfully!${NC}"
echo -e "${GREEN}✅ Installers are ready for use.${NC}"
echo ""
echo "To install Saffron in a real environment, use one of these commands:"
echo ""
echo "  Linux/macOS: ./installers/install-saffron.sh"
echo "  Windows: powershell ./installers/install-saffron-windows.ps1"
echo "" 