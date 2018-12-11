
### java-c-process-interact

Just a small test project to get the following done:

* Java calls a C program,
* which can be cross-compiled,
* sends commands to it in a portable way,
* while getting its output at the same time,
* and all from a single Gradle buildfile.

#### Run it (Linux)

```
./gradlew clean run
```

You can call the main class with the native binary as first argument, if you
want to eschew Gradle:

```
$JAVA_HOME/bin/java -cp build/classes/main \
  org.example.javacall.CCallTest build/exe/main/linux/main
```

Astonishingly enough, on my (ArchLinux) machine this even works with the
Windows binary, since Java (or my system) seems to call Wine automatically:

```
$JAVA_HOME/bin/java -cp build/classes/main \
  org.example.javacall.CCallTest build/exe/main/windows/main.exe
```

#### Run it (Windows)
You need a JDK (8+) and [MinGW-w64](https://mingw-w64.org/doku.php/download) installed. Then have the environment variables JAVA_HOME point to the JDK path and have PATH include the path to mingw64/bin.

If those two environment variable values are present, then you can simply, from a terminal in the project directory, run
```
gradlew.bat clean run
```

#### Output
The result should look like this:
```
C:   1
C:   2
C:   3
C:   4
C:   5
C:   6
J: Sending pause
C: Paused
C: ...
C: ...
J: Sending resume
C: Resumed
C:   7
C:   8
J: Closing stdin stream
C: Parent finished
```

#### License
[![License
](https://img.shields.io/github/license/grandchild/java-c-process-interact.svg)
](https://creativecommons.org/publicdomain/zero/1.0/)

You may use this code without attribution, that is without mentioning where
it's from or who wrote it. I would actually prefer if you didn't mention me.
You may even claim it's your own.
