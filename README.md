# Echo

*Echo* is a tool for model repair and transformation based on the [Alloy](http://alloy.mit.edu) model finder, with support for bidirectional model transformations with the [QVT Relations](http://www.omg.org/spec/QVT/1.1/) (QVT-R) transformation language.
It is able to both check and recover through minimal updates both intra- and inter-model consistency, and is built over the Eclipse Modeling Framework (EMF).

## Features

For an overview of Echo's features please watch this [video](https://vimeo.com/67716977).

Current features include

* Model visualization;
* Model generation;
* Conformance check;
* Model repair;
* Inter-model consistency check;
* Inter-model consistency repair;

* Implements the checking semantics from the QVT-R standard;
* Implements the principle of least-change, returning all instances closest to the original;
* Support for both "enforce" and "checkonly" modes;
* Support for OCL constraints over the models;
* Support for instance conformance testing.

*Check mode* verifies if two models are consistent according to the given QVT-R specification.

*Enforce mode* updates the target instance to one of the closest consistent states. Echo presents all possible instances as Alloy models, which are translated back to xmi once the usar chooses the desired one. To measure the distance between instances, Alloy models are seen as graphs, and the graph edit distance is calculated (which counts node and edge deletions and creations).

For more information about how the tool is implemented please read the paper [Implementing QVT-R Bidirectional Model Transformations Using Alloy](http://www3.di.uminho.pt/~mac/Publications/fase13.pdf), recently accepted for publication at [FASE'13](http://www.etaps.org/2013/fase13).

## Installing

Echo is available as both an Eclipse plug-in or as a command-line aplication.

### Eclipse plugin

### Command-line

* Checkout the latest stable version (v0.1) from the git repository:

```
git clone https://github.com/haslab/echo.git
cd echo
git checkout v0.1
```
* Compile the java source files into an executable jar by running `make.sh`:

```
./make.sh
```
This will create the `echo.jar` file in the project's root directory.

## Running

### Eclipse plugin

The best way to get started is to watch this [video](https://vimeo.com/67716977).

### Command-line

At the moment, Echo is available through an executable jar. The basic syntax is
```sh
java -jar echo.jar -check -q <qvtr> -m <models>... -i <instances>...
java -jar echo.jar -enforce <direction> -q <qvtr> -m <models>... -i <instances>...
```
for checkonly and enforce mode respectively. Metamodels should be presented in ECore, while instances should be xmi files conforming to the respective metamodels and presented in the order defined by the QVT-R transformation.

Additional options include:
```
-d, --delta <nat>           maximum delta between the original and the new generated instances
-o, --nooverwrite           do not overwrite the original instance xmi with the newly generated
-t, --conformance           test if instances conform to the models before applying qvt
```

Echo can also simply be run to check if the instances conform to the models as:
```sh
java -jar -t -m <models>... -i <instances>...
```

## Examples

Folder [examples](examples) contains QVT-R implementations of some typical bidirectional transformations. Files `enforce` and `check` are example commands that perform consistency checks and enforcement executions, respectively.

## Contributors
* [Alcino Cunha] (http://di.uminho.pt/~mac)
* Tiago Guimarães 
* [Nuno Macedo] (http://alfa.di.uminho.pt/~nfmmacedo)

The contributors are members of the *High-Assurance Software Laboratory* ([HASLab](haslab.di.uminho.pt)) at University of Minho.
