### HSM2NHSM
The [HSM2NHSM.qvt](HSM2NHSM.qvt) transformation specifies the collapse/expansion of state diagrams

#### Overview
Every top State on the expanded state diagram is matched to a State in the collapsed state diagram, with Transitions being pushed up from children States to the top States.

#### Meta-models
* [HSM.ecore](HSM.ecore) for expanded (hierarchical) state diagrams;
* [NHSM.ecore](NHSM.ecore) for collapsed (non-hierarchical) state diagrams.

#### History
This example is based on the running example from the paper *JTL: a bidirectional and change propagating transformation language"* by Antonio Cicchetti, Davide Di Ruscio, Romina Eramo and Alfonso Pierantonio.