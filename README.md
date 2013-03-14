# Echo

Echo if a bidirectional model transformation tool for QVT Relations (QVT-R) transformation language, based on the Alloy model checker and built over the Eclipse Modeling Framework.

## Running
At the moment Echo is available through a command line executable. The basic synthax is
```sh
java Echo [mode] [qvt] [direction] [metamodel1] [instance1] [metamodel2] [instance2]
```
Direction should be "enforce" or "check". Metamodels should be presented in ECore, while instances should be xmi files conforming to the respective metamodels.
