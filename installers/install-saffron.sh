#!/bin/bash

# Check for Java 17+
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ -z "$java_version" ]]; then
    echo "Java not found. Please install Java 17 or higher."
    exit 1
fi

major_version=$(echo $java_version | cut -d'.' -f1)
if [[ "$major_version" -lt 17 ]]; then
    echo "Java version $java_version detected. Saffron requires Java 17 or higher."
    exit 1
fi

echo "Java $java_version detected."

# Create a clean installation directory
install_dir="$HOME/.saffron"
if [ -d "$install_dir" ]; then
    echo "Removing existing installation..."
    rm -rf "$install_dir"
fi
mkdir -p "$install_dir"

# Download latest release
echo "Downloading Saffron..."
download_url="https://github.com/senthilvsh/saffron/releases/download/v0.1/saffron.zip"

if command -v curl >/dev/null 2>&1; then
    curl -L -s "$download_url" -o "/tmp/saffron.zip"
elif command -v wget >/dev/null 2>&1; then
    wget -q "$download_url" -O "/tmp/saffron.zip"
else
    echo "Error: Neither curl nor wget is installed. Please install one of them and try again."
    exit 1
fi

# Set up a temporary directory for extraction
tmp_dir="/tmp/saffron-extract"
rm -rf "$tmp_dir"
mkdir -p "$tmp_dir"

# Extract the ZIP file
echo "Extracting..."
unzip -q -o "/tmp/saffron.zip" -d "$tmp_dir"
rm -f "/tmp/saffron.zip"

# Copy files to installation directory
echo "Installing Saffron..."
cp -f "$tmp_dir/saffron/saffron.jar" "$install_dir/"
cp -f "$tmp_dir/saffron/saffron.cmd" "$install_dir/"

# Create a shell script launcher
echo '#!/bin/sh' > "$install_dir/saffron"
echo 'java -jar "$(dirname "$0")/saffron.jar" "$@"' >> "$install_dir/saffron"
chmod +x "$install_dir/saffron"

# Clean up
rm -rf "$tmp_dir"

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
        echo "export PATH=\"\$PATH:$install_dir\"" >> "$profile_file"
        echo "Updated $profile_file"
    fi
    source_cmd="source $profile_file"
else
    echo "No shell profile found. You'll need to manually add the following to your shell profile:"
    echo "export PATH=\"\$PATH:$install_dir\""
    source_cmd="export PATH=\"\$PATH:$install_dir\""
fi

echo "Saffron has been installed to $install_dir"
echo "To complete installation, run:"
echo "$source_cmd"
echo ""
echo "Then you can run Saffron programs using:"
echo "saffron \"program.sfr\""
echo ""
echo "Testing installation..."
export PATH="$PATH:$install_dir"
if command -v saffron >/dev/null 2>&1; then
    echo "Success! Saffron is now available in your current shell."
else
    echo "Warning: 'saffron' command not found in PATH. Please run the source command above."
fi 