#!/bin/bash

# Installation directory
install_dir="$HOME/.saffron"
jre_dir="$install_dir/jre"
jre_bin_java="$jre_dir/bin/java"

# Check if running in WhatIf mode
WHAT_IF=false
if [ "$1" == "--whatif" ]; then
    WHAT_IF=true
fi

# Detect platform and set JRE download URL
if [[ "$OSTYPE" == "darwin"* ]]; then
    OS="mac"
    jre_url="https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/17.0.12+7/openlogic-openjdk-jre-17.0.12+7-mac-x64.zip"
else
    OS="linux"
    # Detect architecture
    ARCH=$(uname -m)
    if [[ "$ARCH" == "x86_64" ]]; then
        jre_url="https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/17.0.12+7/openlogic-openjdk-jre-17.0.12+7-linux-x64.zip"
    elif [[ "$ARCH" == "aarch64" || "$ARCH" == "arm64" ]]; then
        jre_url="https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/17.0.12+7/openlogic-openjdk-jre-17.0.12+7-linux-aarch64.zip"
    else
        echo "Unsupported architecture: $ARCH"
        exit 1
    fi
fi

# Saffron download URL
saffron_url="https://github.com/senthilvsh/saffron/releases/download/v0.1/saffron.zip"

# Create a clean installation directory
if [ -d "$install_dir" ]; then
    if $WHAT_IF; then
        echo "Would remove existing installation directory: $install_dir"
    else
        echo "Removing existing installation..."
        rm -rf "$install_dir"
    fi
fi

if $WHAT_IF; then
    echo "Would create directory: $install_dir"
else
    mkdir -p "$install_dir"
fi

# Function to check if bundled JRE exists and works
function test_bundled_jre {
    if [ ! -f "$jre_bin_java" ]; then
        return 1
    fi
    
    if "$jre_bin_java" -version >/dev/null 2>&1; then
        return 0
    else
        return 1
    fi
}

# Download and install JRE if needed
if ! test_bundled_jre; then
    echo "Bundled JRE not found. Downloading..."
    
    if $WHAT_IF; then
        echo "Would download JRE from: $jre_url"
        echo "Would extract JRE to: $jre_dir"
    else
        # Create temp directory for downloads
        temp_dir="$install_dir/temp"
        mkdir -p "$temp_dir"
        
        # Download JRE
        if command -v curl >/dev/null 2>&1; then
            curl -L -s "$jre_url" -o "$temp_dir/jre.zip"
        elif command -v wget >/dev/null 2>&1; then
            wget -q "$jre_url" -O "$temp_dir/jre.zip"
        else
            echo "Error: Neither curl nor wget is installed. Please install one of them and try again."
            exit 1
        fi
        
        # Extract JRE
        echo "Extracting JRE..."
        jre_temp_dir="$temp_dir/jre_extract"
        mkdir -p "$jre_temp_dir"
        unzip -q -o "$temp_dir/jre.zip" -d "$jre_temp_dir"
        
        # Find and move the JRE directory
        # The JRE zip might contain a parent directory
        if [ -d "$jre_dir" ]; then
            rm -rf "$jre_dir"
        fi
        
        # Try to find directories matching jdk or jre pattern
        jre_extracted_dir=$(find "$jre_temp_dir" -maxdepth 1 -type d -name "*jdk*" -o -name "*jre*" | grep -v "^$jre_temp_dir$" | head -1)
        
        if [ -n "$jre_extracted_dir" ]; then
            # Found a JRE/JDK directory, move it to final location
            mv "$jre_extracted_dir" "$jre_dir"
        else
            # If we can't find a specific directory, move the entire content
            mkdir -p "$jre_dir"
            mv "$jre_temp_dir"/* "$jre_dir"
        fi
        
        # Make Java executable
        chmod +x "$jre_dir/bin/java"
        
        # Clean up
        rm -f "$temp_dir/jre.zip"
        rm -rf "$jre_temp_dir"
    fi
fi

# Download and install Saffron
echo "Downloading Saffron..."
if $WHAT_IF; then
    echo "Would download Saffron from: $saffron_url"
    echo "Would extract to: $install_dir"
else
    # Create temp directory for downloads
    temp_dir="$install_dir/temp"
    mkdir -p "$temp_dir"
    
    # Download Saffron
    if command -v curl >/dev/null 2>&1; then
        curl -L -s "$saffron_url" -o "$temp_dir/saffron.zip"
    elif command -v wget >/dev/null 2>&1; then
        wget -q "$saffron_url" -O "$temp_dir/saffron.zip"
    else
        echo "Error: Neither curl nor wget is installed. Please install one of them and try again."
        exit 1
    fi
    
    # Extract Saffron
    echo "Extracting Saffron..."
    unzip -q -o "$temp_dir/saffron.zip" -d "$temp_dir"
    
    # Copy JAR file
    cp "$temp_dir/saffron/saffron.jar" "$install_dir/"
    
    # Create launcher script that uses bundled JRE
    cat > "$install_dir/saffron" <<EOF
#!/bin/sh
JAVA_EXE="\$HOME/.saffron/jre/bin/java"
if [ -x "\$JAVA_EXE" ]; then
  "\$JAVA_EXE" -jar "\$(dirname "\$0")/saffron.jar" "\$@"
else
  echo "Error: Java Runtime not found in .saffron/jre directory."
  echo "Please reinstall Saffron."
  exit 1
fi
EOF
    
    # Make script executable
    chmod +x "$install_dir/saffron"
    
    # Clean up
    rm -f "$temp_dir/saffron.zip"
    rm -rf "$temp_dir"
fi

# Detect shell profile file
profile_file=""
if [ -f "$HOME/.zshrc" ]; then
    profile_file="$HOME/.zshrc"
elif [ -f "$HOME/.bash_profile" ]; then
    profile_file="$HOME/.bash_profile"
elif [ -f "$HOME/.bashrc" ]; then
    profile_file="$HOME/.bashrc"
elif [ -f "$HOME/.profile" ]; then
    profile_file="$HOME/.profile"
fi

# Add to PATH in profile
if [ -n "$profile_file" ]; then
    if ! grep -q "export PATH=.*saffron" "$profile_file"; then
        if $WHAT_IF; then
            echo "Would add to PATH in: $profile_file"
        else
            echo "export PATH=\"\$PATH:$install_dir\"" >> "$profile_file"
            echo "Updated $profile_file"
        fi
    fi
    source_cmd="source $profile_file"
else
    echo "No shell profile found. You'll need to manually add the following to your shell profile:"
    echo "export PATH=\"\$PATH:$install_dir\""
    source_cmd="export PATH=\"\$PATH:$install_dir\""
fi

echo "Saffron has been installed to $install_dir with its own Java Runtime Environment."
echo "To complete installation, run:"
echo "$source_cmd"
echo ""
echo "Then you can run Saffron programs using:"
echo "saffron \"program.sfr\""
echo ""
echo "For immediate use without restarting, you can run:"
echo "export PATH=\"\$PATH:$install_dir\"" 