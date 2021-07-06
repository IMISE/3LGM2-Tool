# Static view of the domain layer #

The domain layer of a hospital information system is determined by the functions of the hospital, which are supported by the hospital information system, and the object types that are processed or interpreted in the course of the functions. It thus describes the professional consequences of the company's goals and abstracts the tools used to complete the functions.

*Note: The model of the domain layer is based on the idea that the objects to be processed are all located in the world under consideration. This makes the idea superfluous that on the domain layer object types have to flow between functions or objects between activities. It is only shown in the context of which function something is being processed that may need to be interpreted in the context of which (other) function. This means that the relationships between functions are not modeled and this is ultimately left to the process view. 
The following figure shows the UML notation of the domain layer.*

![metaModelDomain][Fig. 1]

"Fig. 1: The meta model of the domain layer."

[Fig. 1]: https://bitbucket.org/repo/9L6rMz/images/3681716203-metaModelDomain.png 

## Object types ##
Information refers to objects. Objects are physical or virtual things of a hospital (see "part of the perceivable or conceivable universe" [15]), which are represented by data. Objects can be described by characteristics. Objects with the same characteristic types are summarized as an object type; object types are represented by their characteristic types (see "concept" as "unit of thought constituted through abstraction on the basis of properties common to a set of objects" [15]). Object types can therefore be used to describe the type of information that is needed to process a function or that can be provided after the function has been processed. Object types can be refined.

## Function ##
According to [19], p. 147, an (enterprise) function [17] ("enterprise function" [18]) is a target specification for human or machine action. The goal to be achieved by the action coincides with or supports the achievement of a (partial) goal of the company. A function has no defined beginning and no defined end. Functions can be divided into subfunctions.
A function, and thus also a subfunction, can be regarded as a duty, which is more or less mandatory due to the company's objectives.
Acting within the scope of a function is the fulfillment of the target specification and takes place within the scope of activities. An activity of a function is the one-time application or execution of a procedure that is available for the completion of the function.
Activities edit objects by changing the characteristic values. This may require information that is obtained from the characteristics of the processed or other objects. An object type whose objects are processed by the activities of a function is called 'object type of a function to be processed'. An object type whose objects are processed by the activities of a function is called 'object type of a function to be interpreted'.
In the context of this work, we will initially refrain from modeling a sequence of processing objects by the activities of the functions beyond this static model. This is reserved for the extension to a dynamic model. For planning and monitoring in strategic information management, however, it is important to be able to derive for each object type the functions that will be processed or interpreted when the object type is completed and vice versa.

## Organizational unit ##
Functions are performed in specific organizational units. This is particularly important later on when assigning application modules.

## Roles ##
Roles indicate the professional position of an employee. Typical roles in a hospital are senior physician, nursing staff; medical technical assistant etc.
On the professional layer, requirements are formulated for the logical and physical tool layer. For this reason, no semantic quality criteria can be specified for this layer alone. However, reference models can be described on the domain layer, which then only need to be modified for the modeling of a concrete hospital information system. Reference models of the domain layer are for example the catalog of requirements for information processing in hospitals, the Good Electronic Health Record, the Reference Information Model, the communication standards HL7, HISA or the European Electronic Healthcare Record Architecture (EHCRA). Auf der fachlichen Ebene werden Anforderungen an die logische und physische Werkzeugebene formuliert. Aus diesem Grund können allein für diese Ebene keine semantischen Qualitätskriterien angegeben werden. Allerdings können auf der fachlichen Ebene Referenzmodelle beschrieben werden, die dann für die Modellierung eines konkreten Krankenhausinformationssystems lediglich noch modifiziert werden müssen. Referenzmodelle der fachlichen Ebene sind beispielsweise der Anforderungskatalog für die Informationsverarbeitung im Krankenhaus, das Good Electronic Health Record, das Reference Information Model das Kommunikationsstandards HL7, HISA oder die Europäische Electronic Healthcare Record Architecture (EHCRA).
Fig. 2 shows an example of a domain layer, which is to be regarded as a manifestation of the meta-model shown in [Fig. 1](#metaModelDomain). Rectangles represent tasks, ovals stand for object types. An arrow from an object type to a task marks an interpreting access, an arrow from a task to an object type marks an editing access. Refinements are represented as rectangles contained in other rectangles. Typical tasks of a hospital here are hospital management, patient treatment or the management of patient records. The PATIENT TREATMENT task is refined into other tasks. Typical object types are PATIENT, SERVICE REQUIREMENT or CASE.

![domainExample.PNG](https://bitbucket.org/repo/9L6rMz/images/3504194347-domainExample.PNG)
  Fig. 3: Example of a professional level.
The tasks are performed by the persons working in a hospital. They use information processing tools for this purpose. In the following we distinguish between logical tools based on software products or organization charts and physical tools like computer systems or conventional tools.