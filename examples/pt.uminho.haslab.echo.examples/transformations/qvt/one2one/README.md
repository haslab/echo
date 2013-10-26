### One-to-one (bijection)
The [one2one.qvtr](one2one.qvtr) transformation denotes a bijection between two very simple models.

#### Overview
This transformation is part of a series of toy transformations designed to test the expressibility of *Echo*. Metamodels simply consist of sets of uninterpreted *A* and *B* elements, respectively. QVT-R is not designed to naturally handle one-to-one relations, QVT-R specifications typically denoting some-to-some relations. In order to attain a bijection in this example, since elements are uninterpreted, we must resort to non-common QVT-R specifications, in particular forcing the number of *A* and *B* elements to be the same. This may be troublesome in *Echo* if the bitwidth is not sufficient to accommodate the cardinality of the sets.

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
<li>[some-to-some](../some2some) (surjective and entire)</li>
<li>[some-to-one](../some2one) (surjection)</li>
<li>one-to-set (injective and surjective)</li>
<li>one-to-lone (injective abstraction)</li>
<li>[one-to-some](../one2some) (surjective representation)</li>
<li>[one-to-one](../one2one) (bijection)</li>
</ul></td>
<td>
<img src="../../../metamodels/multiplicities/images/taxonomy.png" alt="Relation taxonomy" width="100px">
</td></tr></table>

#### Meta-models
| [A_base.ecore](../../../metamodels/multiplicities/A_base.ecore) for empty *A*s | [B_base.ecore](../../../metamodels/multiplicities/B_base.ecore) for empty *B*s |
| --- | --- | --- |
| <img src="../../../metamodels/multiplicities/images/A_base.png" alt="A metamodel" width="100px"> | <img src="../../../metamodels/multiplicities/images/B_base.png" alt="B metamodel" width="100px"> |

#### Models
| [A_base_example.xmi](../../../models/multiplicities/A_base_example.xmi) | [B_base_example.xmi](../../../models/multiplicities/B_base_example.xmi) |
| --- | --- | --- |
| <img src="../../../models/multiplicities/images/A_base_example.png" alt="HSM model" width="150px" align="middle"/> | <img src="../../../models/multiplicities/images/B_base_example.png" alt="B model" width="200px" align="middle"/> |

#### History
* The taxonomy of these examples is inspired by taxonomy for binary relation from the *Data Transformation by Calculation* tutorial by J. N. Oliveira.