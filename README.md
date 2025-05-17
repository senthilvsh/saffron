# Saffron


## Introduction

Saffron is a simple, type-safe, interpreted, general-purpose programming language.


## Requirements

Saffron is written in Java. You need Java 17 (or higher) installed on your system
to run Saffron programs.


## Standard Library

The Saffron Standard Library provides a minimal set of functions to perform console IO and string manipulation.

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

The overload without the ___end___ argument will return the sub-string from the ___start___ position till the end of the ___source___ string.

Positions are zero-based i.e., the position of the first character is ___0___ and the position of the last character is ___length - 1___.

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
