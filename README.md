# Saffron


## Introduction

Saffron is a simple, type-safe, interpreted, general-purpose programming language.

The language provides three basic data types (Number, String and Boolean) and basic
programming constructs like conditional statements, loops and functions.

It provides [type safety](#type_safety) by making a series of checks before starting execution.

It also comes with a standard library of functions that support console I/O,
string manipulation and data conversions.


## Setup

Saffron is written in Java. You need Java 17 (or higher) installed on your system
to run Saffron programs.

1. Download ___saffron.zip___ from the latest release in the [Releases](https://github.com/senthilvsh/saffron/releases) page.

2. Extract the ZIP file. The extracted contents will have the following structure.
    ```
    saffron/
    ├─ saffron.jar
    ├─ saffron
    ├─ saffron.cmd

3. Add the path of the ___saffron___ folder to the PATH environment variable
   so that `saffron` and `saffron.cmd` are available on the PATH.

### Run a Saffron program <a name="run_saffron_program"></a>

To run a Saffron program called __my-program.sfr__, use the following command.

```shell
saffron "my-program.sfr"
```

## User Guide

This section will walk you through the language features and standard library functions
using example programs. You can copy the code in each example below and run it as described in the
[Run a Saffron program](#run_saffron_program) section above.

### 1. Hello World

```
writeln("Saffron is awesome!");
```

The above program writes a string to the console. The `writeln()` function is a standard library function
that prints a given value to the console.

### 2. Variables

```
var message:str;

message = "Saffron is awesome!";

writeln(message);
```

Variables are declared using the `var` keyword. The type of the variable is specified immediately
following the variable name, separated by the `:` symbol.

Variables can also be initialized at the same time they are declared.

```
var message:str = "Saffron is awesome!";
```

Saffron supports three data types:

- __Number__ - is an 8-byte double precision floating point value.

- __String__ - is a sequence of Unicode characters.

- __Boolean__ - is either `true` or `false`.

```
var age:num = 30;
writeln(age);

var is_found:bool = false;
writeln(is_found);
```

### 3. Console I/O

```
write("Enter your name: ");

var user:str = readln();

writeln("Namaste " + user + "!");
```

The `readln()` function reads one line of text from the console. Also note the use
of `write` instead of `writeln`. The former will NOT add a new-line character at the end.

### 4. Parse string as a number

```
write("Enter a number: ");

var str_val:str = readln();

var num_val:num = to_num(str_val);

writeln("I doubled your number: " + num_val * 2);

```

The `to_num()` function is used to convert a string to a number.

### 5. Conditional statements

```
write("Enter first number: ");
var n1:num = to_num(readln());

write("Enter second number: ");
var n2:num = to_num(readln());

if (n1 > n2) {
    writeln("First number is greater than second number");
} else if (n1 < n2) {
    writeln("First number is less than second number");
} else {
    writeln("First number is equal to second number");
}
```

The `if` keyword is used to execute a block of statements if a condition is true.
The `else` keyword is used to execute a block of statements if the above condition is false.
You can also chain if-else statements to check a series of conditions as shown above.

### 6. Loops

```
var i:num = 1;

while (i <= 10) {
    writeln(i);
    i = i + 1;
} 
```

The above program uses the `while` loop to print numbers from 1 to 10.

Aside from the loop condition, you can control the execution of loops using
the `break` and `continue` statements.

```
var i:num = 1;

while (i <= 10) {
    if (i % 2 == 0) {
        i = i + 1;
        continue;
    }
    writeln(i);
    i = i + 1;
}
```

In the above program, we check whether the current value of `i` is an even number
and skip the current iteration using the `continue` statement. Similarly, a `break` statement 
can be used to exit a loop.

### 7. Functions

```
fun greet(user: str): str {
    return "Namaste " + user + "!";
}

write("Enter your name: ");

var user:str = readln();

writeln(greet(user));
```

In the above program, we define a function called ___greet___ which takes a string parameter and
returns a greeting.

A function is defined using the `fun` keyword, followed by the name of the function. 
The name of the function must be followed by a pair of open and close brackets `()`.

The function can optionally have arguments which are declared similar to variable declarations,
but without the `var` keyword. These arguments must be specified inside the open and close brackets.

The return type of the function is specified immediately after the closing bracket `)`,
separated by the `:` symbol. Functions that do not return any value must have the `void` return type.

After the return type, the body of the function must be speficied in the form of a statement block.

```
fun print_something(msg: str): void {
    writeln(msg);
}

print_something("Saffron is awesome!");
```

Note that __void__ is not a proper data type i.e, it cannot be used in variable declarations.
It is used only in the case of functions that do not return any value.


## Type Safety <a name="type_safety"></a>

Saffron is a type-safe language. Before running a program, it is validated to find all type-related errors.

All variables must be declared before being used.
```
// var a:num

a = 10; // error
```

A variable cannot be redeclared in the same scope
```
var a:num = 10;
var a:num = 20;    // error, 'a' is already declared in this scope

fun test():void {
    var a:num = 20;    // OK, because a function introduces a new scope
}
```

All variables must have a type specified.
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

All functions must declare a return type.
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

## Standard Library

The Saffron Standard Library provides a minimal set of functions to perform 
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

Throws an exception if ___source___ is not in a valid number format.

```
to_num(source: str): num
```

#### to_bool

Parse the ___source___ string as a boolean.

Throws an exception if ___source___ is not a valid boolean value.

```
to_bool(source: str): bool
```

#### to_str

Convert the ___source___ value to its string representation.

```
to_str(source: num): str
to_str(source: bool): str
```
