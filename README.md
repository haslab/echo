# Echo

*Echo* is a tool for model repair and transformation based on the [Alloy](http://alloy.mit.edu) model finder, with support for bidirectional model transformations.
It is able to both check and recover, through minimal updates, both intra- and inter-model consistency, and is built over the Eclipse Modeling Framework (EMF).

## Features

Echo is meta-model independent, being able to process any meta-model specified in ECore and its respective instances in XMI. Additional constraints, as well as operations, are specified by embedding OCL in annotations, as prescribed by EMF. Inter-model consistency is specified by the [QVT Relations](http://www.omg.org/spec/QVT/1.1/) (QVT-R) transformation language.

Over these constraints and models, Echo possesses the following features.

<dl>
  <dt>Model visualization</dt>
  <dd>Models are presented using the Alloy visualizer. For better readability, an Alloy theme is automatically inferred from the meta-model, although an user-defined theme can also be provided if desired.</dd>

  <dt>Model generation</dt>
  <dd>Given a meta-model and user-specified size, Echo can generate a new model conformant with the metamodel. Additional constraints can also be specified to generate instances with a parametrized shape.</dd>

  <dt>Consistency check</dt>
  <dd>Given a model, Echo can check if it conforms to the respective meta-model.</dd>

  <dt>Model repair</dt>
  <dd>Given a model that does not conform to its meta-model, Echo can find a minimal repair that produces a consistent model.</dd>

  <dt>Inter-model consistency check</dt>
  <dd>Given a QVT-R transformation and two models that are supposed to be consistent via it, Echo can check if such is the case. The checking semantics follows exactly the specified in the QVT standard.</dd>

  <dt>Inter-model consistency repair</dt>
  <dd>QVT-R specifications are interpreted as bidirectional transformations, thus, given inconsistent models, Echo is able to repair either one to recover consistency.</dd>

  <dt>Inter-model generation</dt>
  <dd>Given a QVT-R transformation and an existing model, Echo can generate the minimal model consistent with existing model by the QVT-R transformation.</dd>
</dl>

For all generation and repair procedures, Echo presents *all* valid solutions, allowing the user to select the desired one.

Repair procedures are always *minimal*, in the sense that the resulting consistent model is as close as possible to the original inconsistent one. The user is able to choose how to measure this distance: either through *graph edit distance*, a meta-model independent metric that sees models as graphs and counts modifications of edges and nodes, or through an *operation-based distance*, that counts the number of applications of user-defined operations required to obtain the new model.

For more information about how the tool is implemented please consult the paper [Implementing QVT-R Bidirectional Model Transformations Using Alloy](http://www3.di.uminho.pt/~mac/Publications/fase13.pdf), accepted for publication at [FASE'13](http://www.etaps.org/2013/fase13). For an overview of Echo's features please watch this [video](https://vimeo.com/67716977).


## Installing

Echo is deployed over the Eclipse Modeling Tools 4.3 (Kepler) (including the QVTd component, which, being in incubation phase, is not included in the standard package). The following steps assume a fresh instalation of Eclipse. If Eclipse Modeling Tools 4.3 is already installed, step 3 will suffice.

1. Download the [Eclipse Modeling Tools 4.3 (Kepler)](http://www.eclipse.org/downloads/packages/eclipse-modeling-tools/keplerrc2);
2. Install the  QVT Declarative (QVTd) 0.10.0RC2 component in Eclipse (Help > Install New Software...) by downloading by downloading the [archive](http://www.eclipse.org/mmt/downloads/?showAll=1&hlbuild=S201305311516&project=qvtd#S201305311516) or through the [update site](http://download.eclipse.org/mmt/qvtd/updates/milestones);
3. Install the  Echo 0.2.0 plugin in Eclipse (Help > Install New Software...) by downloading the [archive](http://haslab.github.io/echo/downloads/echo-0.2.0.zip) or through the [update site](http://haslab.github.io/echo/updates/).

For Mac OS users, the current version of Java 7u21 has an issue with the bridge between AWT and SWT that does not allow the embedding of the Alloy Visualizer in Eclipse. This is fixed in the early access release of [Java 7u40](https://jdk7.java.net/download.html), being required to run Echo in Mac OS.

<!---
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
-->
## Running

The best way to get started with Echo is to watch this [video](https://vimeo.com/67716977).

<!---
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
-->
## Examples

Folder [examples](http://github.com/haslab/echo/tree/master/examples) contains ECore meta-models and QVT-R implementations of some typical bidirectional transformations, as well some example XMI model instances.

<!--Files `enforce` and `check` are example commands that perform consistency checks and enforcement executions, respectively.-->

## Publications
* N. Macedo, T. Guimarães and A. Cunha. *Model repair and transformation with Echo*. Submitted. 2013.
* N. Macedo and A. Cunha. [*Implementing QVT-R Bidirectional Model Transformations Using Alloy*](http://www3.di.uminho.pt/~mac/Publications/fase13.pdf). In the proceedings of the 16th International Conference on Fundamental Approaches to Software Engineering (FASE'13). LNCS 7793. Springer, 2013.

## Contributors
* [Alcino Cunha] (http://di.uminho.pt/~mac)
* Tiago Guimarães 
* [Nuno Macedo] (http://di.uminho.pt/~nfmmacedo)

The contributors are members of the *High-Assurance Software Laboratory* ([HASLab](haslab.di.uminho.pt)) at University of Minho, and have developed this work under the [FATBiT](fatbit.di.uminho.pt) project, funded by the ERDF through the programme COMPETE and by the Portuguese Government through FCT (Foundation for Science and Technology), project reference FCOMP-01-0124-FEDER-020532.

<img src="http://haslab.github.io/echo/images/Logo_Compete.jpg" alt="COMPETE" height="100px"/>
<img src="http://haslab.github.io/echo/images/Logo_QREN.jpg" alt="QREN" height="100px"/>
<img src="http://haslab.github.io/echo/images/Logo_UE.jpg" alt="UE" height="100px"/>
<img src="http://haslab.github.io/echo/images/Logo_FCT.jpg" alt="FCT" height="100px"/>
