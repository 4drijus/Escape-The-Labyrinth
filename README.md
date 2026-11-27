# Escape the Labyrinth

2D online co-op platform game created in Java

## Technology
- Java 21
- Maven
- TCP sockets
- Swing/AWT
- Git/GitHub

## Player movement states

`Player` checks each movement command against its current `MovementState` and
executes commands allowed by that state. The concrete states only define which
commands are allowed. A shared `StateManager` selects the next state from the
player's grounded status and velocity after commands and physics updates.
`PlayerPhysics` still applies gravity and resolves collisions. Checkpoints
retain movement data, and restoration recalculates the movement state.

The dependency-free movement test can be run with JDK 21 or newer in
PowerShell from the project root:

```powershell
New-Item -ItemType Directory -Force target\movement-tests | Out-Null
$sources = Get-ChildItem src\main\java -Recurse -Filter *.java
javac --release 21 -d target\movement-tests $sources.FullName src\test\java\lt\escape\labyrinth\server\engine\PlayerMovementStateTest.java
if ($LASTEXITCODE -eq 0) { java -cp target\movement-tests lt.escape.labyrinth.server.engine.PlayerMovementStateTest }
```

This standalone test is not automatically run by `mvn test`.