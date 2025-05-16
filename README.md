# Saffron


## Introduction

Saffron is a simple, type-safe, interpreted, general-purpose programming language.


## Requirements

Saffron is written in Java. You need Java 17 (or higher) installed on your system
to run Saffron programs.


## Standard Library

Saffron provides a minimal set of functions as part of its standard library.

### writeln

Write the given value to the console, followed by a new-line character at the end.

```
writeln(value:num):void;
writeln(value:str):void;
writeln(value:bool):void;
```

### write

Write the given value to the console _(no new-line character at the end)_.

```
write(value:num):void;
write(value:str):void;
write(value:bool):void;
```

### readln

Read a line of text from the console. Returns when the user presses ENTER.

```
readln():str;
```
