# Saffron


## Introduction

Saffron is a simple, type-safe, general-purpose programming language.

The language provides a few basic data types (Number, String and Boolean) and 
simple programming constructs like conditional statements, loops and functions.

It provides [type safety](#type-safety) by making a series of checks before starting execution.

It also comes with a [standard library](https://senthilvsh.github.io/saffron/stdlib.html) of functions 
that support console I/O, string manipulation and data conversions.

The [user guide](https://senthilvsh.github.io/saffron/userguide.html) will walk you through the language features
and standard library functions using example programs.


## Setup

Follow the steps below to install and run Saffron programs.

### 1. Install

#### Automatic Installation (Recommended)

Saffron comes with a bundled Java Runtime Environment - no separate Java installation required!

**For Linux/macOS:**

Using __curl__:
```bash
curl -sSL \
    https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install.sh | bash
```

...or using __wget__:
```bash
wget -qO- \
    https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/install.sh | bash
```

**For Windows (PowerShell):**
```powershell
Invoke-Expression `
    (New-Object System.Net.WebClient).DownloadString( `
    'https://raw.githubusercontent.com/senthilvsh/saffron/master/installers/windows.ps1')
```

The installer will:
1. Detect your operating system and architecture
2. Download and install OpenJDK JRE 17
3. Download and setup Saffron
4. Add Saffron to your PATH

#### Manual Installation

If you prefer manual installation:

1. Download ___saffron.zip___ from the latest release in the [Releases](https://github.com/senthilvsh/saffron/releases) page.
2. Extract the ZIP file. The extracted contents will have the following structure.
    ```
    saffron/
    ├─ saffron.jar
    ├─ saffron
    ├─ saffron.cmd
    ```
3. Ensure you have Java 17 or higher installed and available on your PATH.
4. Add the path of the ___saffron___ folder to the PATH environment variable.

### 2. Run a Saffron program <a name="run-saffron-program"></a>

To run a Saffron program called __my-program.sfr__, use the following command.

```shell
saffron "my-program.sfr"
```

### 3. Uninstall

To uninstall Saffron:

1. Delete the __.saffron__ directory from your user home folder.
2. Remove the path of the above directory from the PATH environment variable.


## Type Safety <a name="type-safety"></a>

Saffron is a type-safe language. It makes a number of validations before running a program, to find type-related
and other errors.

All variables must be declared before being used.
```
// var a:num

a = 10; // error
```

A variable cannot be re-declared in the same scope
```
var a:num = 10;
var a:num = 20;    // error, 'a' is already declared in this scope

fun test(): void {
    var a:num = 20;    // OK, because a function introduces a new scope
}
```

All variables must specify their type.
```
var a = 10; // error
```

A value assigned to a variable must be of the same type.
```
var a:num = 10;        // OK
var b:num = "abcd";    // error
var c:str = 10 * 20;   // error
```

In an expression, the type of operands must match the types allowed by the operators.
```
var a:num = 12 * "abcd"; // error, cannot multiply number and string
var b:str = -"abcd";     // error, unary - not allowed for string
```

All functions must specify a return type.
```
fun test() {    // error, missing return type
}
```

Function must return values matching its return type.
```
fun test(): str {
    return 10;    // error, type of returned value doesn't match the return type
}
```

Type of each argument passed to a function must match the type of the declared argument
```
fun add(a: num, b: num): num {
    return a + b;
}

var res:num = add("1", 2);    // error, type of 1st argument doesn't match the declared type
```
