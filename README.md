## SysML2PetriNet

Transform SysML v2 activity-like models (.sysml) into Petri Nets and export PNML. The pipeline performs 2 transformations:

- Model-to-Model (M2M): SysML v2 → in-memory Petri Net model (Java classes in `nl.utwente.sysml2petrinet.PetriNet`)
- Model-to-Text (M2T):
  - Indirect: Petri Net → EMF XMI → Acceleo generates PNML
  - Direct: Petri Net → PNML writer (no intermediate XMI)

The project includes sample models and JUnit tests that validate generated PNML against the PTNet RNG schema.

### Key capabilities
- Parse SysML v2 models using the official Xtext-based SysML toolchain
- Build an intermediate Petri Net model (Places, Transitions, Arcs)
- Export PNML (2009 grammar) with optional ISO-style `<text>` vs legacy `<value>` inscription/marking
- Validate PNML against `ptnet.pntd.xml` in tests

## Repository structure

```text
SysML2PetriNet/
├─ pom.xml
├─ README.md
├─ src/
│  ├─ main/
│  │  ├─ java/nl/utwente/sysml2petrinet/
│  │  │  ├─ Main.java                      # Simple entry point using example model
│  │  │  ├─ SysML2PetriNet.java            # Orchestrates end-to-end transformation
│  │  │  ├─ SysMLProcessor.java            # Loads SysML libraries and parses input
│  │  │  ├─ m2m/Transformer.java           # SysML → PetriNet (in-memory)
│  │  │  ├─ m2t_direct/TransformerPNML.java# Direct PetriNet → PNML writer
│  │  │  ├─ m2t/src/.../GeneratePetriNet.java
│  │  │  └─ PetriNet/                      # In-memory Petri Net model
│  │  │     ├─ Arc.java, Node.java, Place.java, Transition.java, PetriNet.java
│  │  └─ resources/
│  │     ├─ model/                         # Sample .sysml inputs and output folder
│  │     ├─ acceleo/                       # Embedded Acceleo/EMF runtime JARs
│  │     ├─ sysml/                         # Embedded SysML v2 toolchain JARs
│  │     ├─ sysml.library/                 # SysML libraries loaded at runtime
│  │     └─ log4j2.xml                     # Console logging config
│  └─ test/
│     ├─ java/.../SysML2PetriNetTest.java  # End-to-end + validation tests
│     └─ resources/                        # PTNet RNG schema and includes
└─ target/                                 # Maven build output
```

## Requirements
- JDK 21
- Maven 3.9+
- Windows, macOS, or Linux

All third-party runtime JARs are vendored under `src/main/resources/**` and referenced with Maven `system` scope.

## Build and test

```bash
mvn -q clean test
```

What this does:
- Compiles the project
- Runs JUnit tests that transform sample models and validate PNML against `src/test/resources/ptnet.pntd.xml`

If you only want to build without running tests:

```bash
mvn -q clean package -DskipTests
```

## Run (from the command line)

Use the provided `Main` class with Maven Exec Plugin (downloaded automatically when invoked):

```bash
mvn -q exec:java -Dexec.mainClass=nl.utwente.sysml2petrinet.Main
```



## Logging
Console logging is configured via `src/main/resources/log4j2.xml` (root level `debug`). Adjust as needed.

## Using your own models
1. Place your `.sysml` file anywhere (e.g., under `src/main/resources/model/`).
2. Run either programmatic call or the `Main` class as shown above.
3. Find PNML in your chosen output directory.

