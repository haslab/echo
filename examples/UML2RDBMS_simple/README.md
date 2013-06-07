### UML2RDBMS (simplified)
The UML2RDBMS transformation is a simplified version of the classical object-relational mapping, between class diagrams and database schemes, used as a running example in the QVT standard.

#### Overview
Every persistent Class on the UML class diagram is matched to a Table in the relational database scheme, with a Column for every attribute, including those inherited. 

#### Meta-models
* [UML.ecore](UML.ecore) for UML class diagrams (without associations);
* [RDBMS.ecore](RDBMS.ecore) for relational database schemes (without keys).

#### History
This example has been used to illustrate the *Implementing QVT-R Bidirectional Model Transformations Using Alloy* paper as well as in the Echo demo video.
