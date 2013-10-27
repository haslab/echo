### One-to-some (surjective representation)
The [one2some.qvtr](one2some.qvtr) transformation denotes a surjective representation between two very simple models.

#### Overview
This transformation is part of a series of toy transformations designed to test the expressibility of *Echo*. Metamodel *A* simply consist of sets of uninterpreted *A* elements, while in metamodel *B*, *B* elements contain an extra association of type *Y*, making *B* larger than *A*. QVT-R is not designed to naturally handle one-to-one relations, QVT-R specifications typically denoting some-to-some relations. In order to attain a representation in this example, since elements are uninterpreted, we must resort to non-common QVT-R specifications, in particular forcing the number of *A* and *B* elements to be the same. This may be troublesome in *Echo* if the bitwidth is not sufficient to accommodate the cardinality of the sets.

More in this series:
<table><tr><td><ul>
<li>set-to-set (relation)</li>
<li>set-to-lone (simple)</li>
<li>set-to-some (entire)</li>
<li>set-to-one (function)</li>
<li>lone-to-set (injective)</li>
<li>lone-to-lone (simple and injective)</li>
<li>lone-to-some (representation)</li>
<li>lone-to-one (injection) </li>
<li>some-to-set (surjective)</li>
<li>some-to-lone (abstraction)</li>
<li><a href="../some2some/">some2some</a> (surjective and entire)</li>
<li><a href="../some2one/">some2one</a> (surjection)</li>
<li>one-to-set (injective and surjective)</li>
<li>one-to-lone (injective abstraction)</li>
<li><a href="../one2some/">one2some</a> (surjective representation)</li>
<li><a href="../one2one">one2one</a> (bijection)</li>
</ul></td>
<td>
<img src="../../../metamodels/multiplicities/images/taxonomy.png" alt="Relation taxonomy" width="500px">
</td></tr></table>

#### Meta-models
| [A_base.ecore](../../../metamodels/multiplicities/A_base.ecore) for empty *A*s | [B_extended.ecore](../../../metamodels/multiplicities/B_extended.ecore) for extended *B*s |
| --- | --- | --- |
| <img src="../../../metamodels/multiplicities/images/A_base.png" alt="A metamodel" width="100px"> | <img src="../../../metamodels/multiplicities/images/B_extended.png" alt="B metamodel" width="250px"> |

#### Models
| [A_base_example.xmi](../../../models/multiplicities/A_base_example.xmi) | [B_extended_example.xmi](../../../models/multiplicities/B_extended_example.xmi) |
| --- | --- | --- |
| <img src="../../../models/multiplicities/images/A_base_example.png" alt="HSM model" width="150px" align="middle"/> | <img src="../../../models/multiplicities/images/B_extended_example.png" alt="B model" width="200px" align="middle"/> |

#### History
* The taxonomy of these examples is inspired by taxonomy for binary relation from the *Data Transformation by Calculation* tutorial by J. N. Oliveira.
