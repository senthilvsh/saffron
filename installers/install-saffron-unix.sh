#!/bin/bash

# Parse arguments
whatif=false
if [[ "$1" == "--whatif" ]]; then
    whatif=true
    echo "Running in test mode (--whatif)"
fi

# Installation directory
install_dir="$HOME/.saffron"
jre_dir="$install_dir/jre"
jre_bin_java="$jre_dir/bin/java"

# Detect OS and architecture
os="linux"
arch="x64"

if [[ "$(uname)" == "Darwin" ]]; then
    os="mac"
    if [[ "$(uname -m)" == "arm64" ]]; then
        arch="aarch64"
    fi
elif [[ "$(uname -m)" == "aarch64" || "$(uname -m)" == "arm64" ]]; then
    arch="aarch64"
fi

# Create installation directory if needed
if [ ! -d "$install_dir" ]; then
    if [ "$whatif" = true ]; then
        echo "Would create directory: $install_dir"
    else
        mkdir -p "$install_dir"
    fi
fi

# URLs for downloads
saffron_url="https://github.com/senthilvsh/saffron/releases/download/v0.1/saffron.zip"

# Set JRE download URL based on OS and architecture
jre_url=""
if [[ "$os" == "mac" ]]; then
    if [[ "$arch" == "aarch64" ]]; then
        jre_url="https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/17.0.12+7/openlogic-openjdk-jre-17.0.12+7-mac-aarch64.tar.gz"
    else
        jre_url="https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/17.0.12+7/openlogic-openjdk-jre-17.0.12+7-mac-x64.tar.gz"
    fi
else # linux
    if [[ "$arch" == "aarch64" ]]; then
        jre_url="https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/17.0.12+7/openlogic-openjdk-jre-17.0.12+7-linux-aarch64.tar.gz"
    else
        jre_url="https://builds.openlogic.com/downloadJDK/openlogic-openjdk-jre/17.0.12+7/openlogic-openjdk-jre-17.0.12+7-linux-x64.tar.gz"
    fi
fi

echo "Installing Saffron for $os ($arch)"

# Function to check if bundled JRE exists and works
test_bundled_jre() {
    if [ ! -f "$jre_bin_java" ]; then
        return 1
    fi
    
    "$jre_bin_java" -version >/dev/null 2>&1
    return $?
}

# Download and install JRE if needed
if ! test_bundled_jre; then
    echo "Bundled JRE not found. Downloading..."
    
    if [ "$whatif" = true ]; then
        echo "Would download JRE from: $jre_url"
        echo "Would extract JRE to: $jre_dir"
    else
        # Create temp directory for downloads
        temp_dir="$install_dir/temp"
        mkdir -p "$temp_dir"
        
        # Download JRE
        jre_archive="$temp_dir/jre.tar.gz"
        if command -v curl >/dev/null 2>&1; then
            curl -L -s "$jre_url" -o "$jre_archive"
        elif command -v wget >/dev/null 2>&1; then
            wget -q "$jre_url" -O "$jre_archive"
        else
            echo "Error: Neither curl nor wget is installed. Please install one of them and try again."
            exit 1
        fi
        
        # Extract JRE
        echo "Extracting JRE..."
        jre_temp_dir="$temp_dir/jre_extract"
        mkdir -p "$jre_temp_dir"
        
        # Extract archive
        tar -xzf "$jre_archive" -C "$jre_temp_dir"
        
        # Find the JRE directory in the extracted content
        extracted_jre_dir=$(find "$jre_temp_dir" -type d -name "jdk*" -o -name "jre*" | head -1)
        
        # Create final JRE directory
        if [ -d "$jre_dir" ]; then
            rm -rf "$jre_dir"
        fi
        
        # Move extracted JRE to final location
        if [ -n "$extracted_jre_dir" ]; then
            mv "$extracted_jre_dir" "$jre_dir"
        else
            # If we can't find a JRE/JDK directory, use the whole extracted directory
            mv "$jre_temp_dir" "$jre_dir"
        fi
        
        # Make JRE binaries executable
        chmod +x "$jre_dir/bin/"*
        
        # Clean up
        rm -f "$jre_archive"
        rm -rf "$jre_temp_dir"
    fi
fi

# Download and install Saffron
echo "Downloading Saffron..."
if [ "$whatif" = true ]; then
    echo "Would download Saffron from: $saffron_url"
    echo "Would extract to: $install_dir"
else
    # Create temp directory for downloads
    temp_dir="$install_dir/temp"
    mkdir -p "$temp_dir"
    
    # Download Saffron
    saffron_zip="$temp_dir/saffron.zip"
    if command -v curl >/dev/null 2>&1; then
        curl -L -s "$saffron_url" -o "$saffron_zip"
    elif command -v wget >/dev/null 2>&1; then
        wget -q "$saffron_url" -O "$saffron_zip"
    fi
    
    # Set up a temporary directory for extraction
    extract_dir="$temp_dir/saffron-extract"
    mkdir -p "$extract_dir"
    
    # Extract the ZIP file
    echo "Extracting Saffron..."
    if command -v unzip >/dev/null 2>&1; then
        unzip -q -o "$saffron_zip" -d "$extract_dir"
    else
        echo "Error: unzip is not installed. Please install it and try again."
        exit 1
    fi
    
    # Copy JAR file to installation directory
    cp -f "$extract_dir/saffron/saffron.jar" "$install_dir/"
    
    # Create a shell script launcher that uses the bundled JRE
    cat > "$install_dir/saffron" << EOF
#!/bin/sh
JRE_BIN="\$(dirname "\$0")/jre/bin/java"
if [ -x "\$JRE_BIN" ]; then
    "\$JRE_BIN" -jar "\$(dirname "\$0")/saffron.jar" "\$@"
else
    echo "Error: Java Runtime not found in .saffron/jre directory."
    echo "Please reinstall Saffron."
    exit 1
fi
EOF
    chmod +x "$install_dir/saffron"
    
    # Clean up
    rm -f "$saffron_zip"
    rm -rf "$extract_dir"
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

# Add to PATH in profile if not already there
if [ -n "$profile_file" ]; then
    if ! grep -q "export PATH=.*$install_dir" "$profile_file"; then
        if [ "$whatif" = true ]; then
            echo "Would add to PATH by updating: $profile_file"
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

# Success message
echo "Saffron has been installed to $install_dir with its own Java Runtime Environment."
echo "You can run Saffron programs using: saffron \"program.sfr\""
echo ""
echo "To use Saffron immediately, run:"
echo "$source_cmd"

# Set PATH for current shell if not in whatif mode
if [ "$whatif" = false ]; then
    export PATH="$PATH:$install_dir"
    if command -v saffron >/dev/null 2>&1; then
        echo "Success! Saffron is now available in your current shell."
    else
        echo "Warning: 'saffron' command not found in PATH. Please run the source command above."
    fi
fi 