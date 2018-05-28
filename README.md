# Echo

*Echo* is a tool for model repair and transformation built over the [Alloy](http://alloy.mit.edu) model finder, with support for bidirectional model transformations.
It is able to both check and recover, through minimal updates, both intra- and inter-model consistency, and is deployed over the Eclipse Modeling Framework (EMF).

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

For more information about how the tool is implemented and its semantics, please consult the paper [Implementing QVT-R Bidirectional Model Transformations Using Alloy](https://nmacedo.github.io/pubs/FASE13.pdf). For an overview of Echo's features please watch this [video](https://vimeo.com/67716977).

([read more](https://github.com/haslab/echo/wiki/Overview))

## Installing

*Echo* is deployed over the Eclipse (Kepler) Modeling Tools. The following steps assume a fresh installation of Eclipse. 

* Download Eclipse [Modeling Tools (Kepler Service Release 2)](http://www.eclipse.org/downloads/packages/eclipse-modeling-tools/keplersr2);
* Download and install in Eclipse (through the **Help > Install New Software...**) menu the following archives in the given order:
  + [Eclipse OCL 5.0](http://www.eclipse.org/modeling/mdt/downloads/?project=ocl) of the Model Development Tools (MDT) project;
  + [Eclipse QVTd 0.11](http://www.eclipse.org/mmt/downloads/?project=qvtd) of the Model to Model Transformation (MMT) project;
  + [Eclipse ATL 3.5](http://www.eclipse.org/mmt/downloads/?project=atl) of the Model to Model Transformation (MMT) project;
  + [Echo 0.3](http://haslab.github.io/echo/downloads/echo-0.3.1.zip).

For Mac OS users, versions of Java earlier than 7up40 have an issue with the bridge between AWT and SWT that does not allow the embedding of the Alloy Visualizer in Eclipse. This is fixed in posterior releases.
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

([read more](https://github.com/haslab/echo/wiki/Install))

## Running

The best way to get started with Echo is to follow this [tutorial](https://github.com/haslab/echo/wiki/Tutorial) or to watch this [video](https://vimeo.com/67716977).

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

Folder [examples](http://github.com/haslab/echo/tree/master/examples) contains ECore meta-models and QVT-R implementations of some typical bidirectional transformations, as well some example XMI model instances. Alternatively, download this [archive](http://haslab.github.io/echo/downloads/echo-0.3.0_examples.zip) containing the same examples.

<!--Files `enforce` and `check` are example commands that perform consistency checks and enforcement executions, respectively.-->

([read more](https://github.com/haslab/echo/wiki/Examples))

## Publications

* N. Macedo and A. Cunha. [*Least-change bidirectional model transformation with QVT-R and ATL*](https://nmacedo.github.io/pubs/SoSyM16.pdf). Software and Systems Modeling. Springer, 2016
* N. Macedo, T. Guimarães and A. Cunha. [*Model repair and transformation with Echo*](https://nmacedo.github.io/pubs/ASE13.pdf). In the proceedings of the 28th IEEE/ACM International Conference on Automated Software Engineering (ASE'13). IEEE, 2013.
* N. Macedo and A. Cunha. [*Implementing QVT-R Bidirectional Model Transformations Using Alloy*](https://nmacedo.github.io/pubs/FASE13.pdf). In the proceedings of the 16th International Conference on Fundamental Approaches to Software Engineering (FASE'13). LNCS 7793. Springer, 2013.

([read more](https://github.com/haslab/echo/wiki/Publications))

## Contributors
* [Alcino Cunha](http://di.uminho.pt/~mac)
* Tiago Guimarães 
* [Nuno Macedo](http://nmacedo.github.io/)

The contributors are members of the *High-Assurance Software Laboratory* ([HASLab](haslab.di.uminho.pt)) at University of Minho, and have developed this work under the [FATBIT](fatbit.di.uminho.pt) project, funded by the ERDF through the programme COMPETE and by the Portuguese Government through FCT (Foundation for Science and Technology), project reference FCOMP-01-0124-FEDER-020532.

<img src="http://haslab.github.io/echo/images/Logo_Compete.jpg" alt="COMPETE" height="100px"/><img src="http://haslab.github.io/echo/images/Logo_QREN.jpg" alt="QREN" height="100px"/><img src="http://haslab.github.io/echo/images/Logo_UE.jpg" alt="UE" height="100px"/><img src="http://haslab.github.io/echo/images/Logo_FCT.jpg" alt="FCT" height="100px"/>
