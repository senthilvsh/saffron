# Saffron


## Introduction

Saffron is a simple, type-safe, general-purpose programming language.

The language provides a few basic data types (Number, String and Boolean) and 
simple programming constructs like conditional statements, loops and functions.

It provides [type safety](#type-safety) by making a series of checks before starting execution.

It also comes with a [standard library](#standard-library) of functions that support console I/O,
string manipulation and data conversions.

The [User Guide](https://senthilvsh.github.io/saffron/userguide.html) will walk you through the language features
and standard library functions using example programs.


## Setup

Follow the steps below to setup and run Saffron programs.

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

A variable cannot be redeclared in the same scope
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

## Standard Library <a name="standard-library"></a>

Saffron comes with a standard library of functions that provide support for  
console I/O, string manipulation and data conversion.

### Console

#### writeln

Write the given value to the console, followed by a new-line character at the end.

```
writeln(value: num): void;
writeln(value: str): void;
writeln(value: bool): void;
```

#### write

Write the given value to the console _(no new-line character at the end)_.

```
write(value: num): void;
write(value: str): void;
write(value: bool): void;
```

#### readln

Read a line of text from the console. Returns when the user presses ENTER.

```
readln(): str;
```

### Strings

#### str_length

Get the length of the ___source___ string.

```
str_length(source: str): num
```

#### str_substr

Get a sub-string of the ___source___ string.

The sub-string will be from the ___start___ position till the ___end___ position, inclusive.

The overload without the ___end___ argument will return the sub-string from 
the ___start___ position till the end of the ___source___ string.

Positions are zero-based i.e., the position of the first character is ___0___ 
and the position of the last character is ___length - 1___.

Throws `INDEX_OUT_OF_BOUNDS_EXCEPTION` if the value of ___start___ or ___end___ is outside
the range `[0 to length-1]`.

```
str_substr(source: str, start: num): str
str_substr(source: str, start: num, end: num): str
```

#### str_replace

Replace all occurrences of ___search___ with ___replace___, in ___source___.

This function does not modify the original string, but returns a new one.

```
str_replace(source: str, search: str, replace: str): str
```

#### str_trim

Trim whitespace from the beginning and end of the ___source___ string.

This function does not modify the original string, but returns a new one.

```
str_trim(source: str): str
```

#### str_contains

Find whether the ___source___ string contains the ___search___ string.

```
str_contains(source: str, search: str): bool
```

#### str_startswith

Find whether the ___source___ string starts with the ___search___ string.

```
str_startswith(source: str, search: str): bool
```

#### str_endswith

Find whether the ___source___ string ends with the ___search___ string.

```
str_endswith(source: str, search: str): bool
```

### Data Conversion

#### to_num

Parse the ___source___ string as a number.

Throws `FORMAT_EXCEPTION` if ___source___ is not in a valid number format.

```
to_num(source: str): num
```

#### to_bool

Parse the ___source___ string as a boolean.

Throws `FORMAT_EXCEPTION` if ___source___ is not a valid boolean value.

```
to_bool(source: str): bool
```

#### to_str

Convert the ___source___ value to its string representation.

```
to_str(source: num): str
to_str(source: bool): str
```
