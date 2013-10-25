### UML2RDBMS
The [uml2rdbms_keys.qvtr](uml2rdbms_keys.qvtr) transformation is a simplified version of the classical object-relational mapping, between class diagrams and database schemes, used as a running example in the QVT standard.

#### Overview
Every persistent Class on the UML class diagram is matched to a Table in the relational database scheme, with a Column for every Attribute, including those inherited from super-classes. Associations between classes are mapped to foreign keys.

#### Meta-models
| [UML.ecore](../../../metamodels/uml2rdbms_keys/UML.ecore) for UML class diagrams | [RDBMS.ecore](../../../metamodels/uml2rdbms_keys/RDBMS.ecore) for relational database schemes |
| --- | --- | --- |
| <img src="../../../metamodels/uml2rdbms_keys/images/UML_metamodel.png" alt="UML metamodel" width="400px"> | <img src="../../../metamodels/uml2rdbms_keys/images/RDB_metamodel.png" alt="RDBMS metamodel" width="350px"> |

#### Models
| [UML_Company.xmi](../../../models/uml2rdbms_keys/UML_Company.xmi) | [RDB_Company.xmi](../../../models/uml2rdbms_keys/RDB_Company.xmi) |
| --- | --- | --- |
| <img src="../../../models/uml2rdbms_keys/images/UML_Company.png" alt="UML company" width="350px" align="middle"/> | <img src="../../../models/uml2rdbms_keys/images/RDB_Company.png" alt="RDB company" width="350px" align="middle"/> |


#### History
* This example is a simplified version of the example illustrating the *MOF 2.0 Query/View/Transformation Specification*. 
* This example has been used to illustrate the *Model repair and transformation with Echo* paper, by N. Macedo, T. Guimarães and A. Cunha.
