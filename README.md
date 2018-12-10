
### java-c-process-interact

Just a small test project to get the following done:

* Java calls a C program,
* which can be cross-compiled,
* sends commands to it in a portable way,
* while getting its output at the same time,
* and all from a single Gradle buildfile.

#### Run it

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


#### License
[![License
](https://img.shields.io/github/license/grandchild/java-c-process-interact.svg)
](https://creativecommons.org/publicdomain/zero/1.0/)

You may use this code without attribution, that is without mentioning where
it's from or who wrote it. I would actually prefer if you didn't mention me.
You may even claim it's your own.
