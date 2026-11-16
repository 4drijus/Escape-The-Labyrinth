# Escape the Labyrinth

2D online co-op platform game created in Java

## Technology
- Java 21
- Maven
- TCP sockets
- Swing/AWT
- Git/GitHub

## Player movement states

Each `PlayerState` delegates movement commands to an idle, running, rising,
or falling `MovementState`. `PlayerPhysics` still applies gravity and resolves
collisions, then lets the current state react to the result. Horizontal
velocity is independent of the airborne states. Checkpoints retain movement
state, and restoration reconciles it with the current horizontal velocity.

The dependency-free movement test can be run with JDK 21 or newer in
PowerShell from the project root:

```powershell
New-Item -ItemType Directory -Force target\movement-tests | Out-Null
$sources = Get-ChildItem src\main\java -Recurse -Filter *.java
javac --release 21 -d target\movement-tests $sources.FullName src\test\java\lt\escape\labyrinth\server\engine\PlayerMovementStateTest.java
if ($LASTEXITCODE -eq 0) { java -cp target\movement-tests lt.escape.labyrinth.server.engine.PlayerMovementStateTest }
```

This standalone test is not automatically run by `mvn test`.