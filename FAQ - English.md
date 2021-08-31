[TOC]

---------------------------

# **1. FAQ - Modelling** #
---------------------------
## **1.1     Domain Layer** ##
#### ***1.1.1        How do I find suitable object types?*** ####
Although the metamodel of the domain layer is very simple, and at first glance it seems easy to create a suitable model. However, it is very demanding to find suitable object types and functions. The biggest difficulty will be to understand what object types actually are. It is highly recommended to remember the definition of the term object type: "...Object types can be used to describe the type of information that is needed to complete a function or can be provided after the function has been completed." This leads to the following consequences: If, for example, a doctor's letter is to be modeled as a physical thing in the world, it must be taken into account that it also contains information about the patient, case, diagnoses ... which are usually defined as separate object types. For a particular problem it may be necessary to represent this relationship (in the form of is_part_of_relationships). Only then is it possible to adequately represent the complexity of reality. For the reasons mentioned above, we recommend to use reference models for the domain layer as far as they are available, e.g. the domain layer reference model for an archive.

#### ***1.1.2        Is there a possibility to represent the organizational units graphically (e.g. presentation of the organizational units with the corresponding tasks)?*** ####
Currently there is no possibility to display organizational units graphically. This is to avoid that the graphical model is 'overloaded'.

#### ***1.1.3        Why should message types, document types and data record types (form of representation) be defined on the domain layer, which belong more to the logical tool layer?*** ####
Message types, document types and record types belong to the logical tool layer. In the properties dialog of an object type you can only define how it is represented on the logical tool layer. To simplify modeling, it is also possible to create new message types, document types and record types in this context, if the required instances of another level have not yet been modeled.

#### ***1.1.4        What should I do with the Master DBS in the Object Type/Properties Dialog/General on the domain layer?*** ####
You can assign to an object type which database system / document collection is master for this object type (relationship has_as_master in the meta model). Master' in this context means that data may only be added, changed and deleted in this database system / document collection. All other database systems / document collections that also store this object type must use communication between the corresponding application modules to compare their data with this database system / document collection when data is changed, added or deleted, otherwise data integrity cannot be guaranteed. (This is of course particularly problematic with document collections!). If both application modules are computer-based, event message types are communicated, if one of the application modules is paper-based, event document types are communicated.

---------------------------
## **1.2     Logical Tool Layer** ###
#### ***1.2.1        How do I model a database that does not belong to any application component?*** ####
What is meant here is probably a database system. A database system can be an application component in the broadest sense. In this case, however, an application component must first be defined, which serves as a shell, so to speak. A database system is then assigned to this application component. The application component is provided with block interfaces via which other application components can then access the database system.


#### ***1.2.2        Is it possible to create several database systems for one application component??*** ####
According to our meta model, there is no possibility of assigning several database systems to one application component. In our experience, this is not necessary, since the application components known to us always have only one database system. However, it may well be possible for one application component to access several database systems. In this case, the database systems and the application components that own them (see also FAQ 5.) must be modeled first. The access must then be modeled via component interfaces and communication relationships.

#### ***1.2.3        Are there modeling guidelines on the logical tool level for paper-based application components?*** ####
Yes, especially for communication links between (computer-based) application components and (non-computer-based) organizational units.

In this communication, the data is converted accordingly. 

* During communication from application components to organizational units, digitally available data is put into physical form. This is the case, for example, when data is displayed on the screen, printed on the printer, listened to in the form of acoustic signals (music, text), or stored on a DVD, and this data is then further processed by humans within the organizational units.

* When organizational units communicate to application components, physically existing data is brought into digital form. This is the case, for example, when a document that exists on paper is
typed, a (recorded) dictation is written, or a CD is manually inserted into a reader so that the data on it can be
so that the data on it can be read by the application system.

When modeling messages, it should not be assumed at this point that the messages are already in the appropriate form.

Instead, it is advisable to first model a communication standard that describes inputs or outputs to application systems (e.g., "Inputs to application systems", "Outputs from application systems" or also "Inputs/outputs to/from application systems"); if necessary, communication standards can also be modeled separately for each application system concerned (e.g., "Inputs to application system XYZ", "Outputs from application system ABC" or also "Inputs/outputs to/from application system EFG"). These communication standards should then be assigned message types which either describe quite roughly that a certain set of object types are input or output or also describe in more detail the form in which this takes place (e.g. "Screen input of patient master and patient case data", "Audio playback of a findings dictation", input/output").


#### ***1.2.4        When is an interface a user interface and when is it a component interface?*** ####
An interface is always a component interface when it is used to model a communication relationship to another application component. A user interface refers to the human-machine interaction and considers more software-ergonomic aspects.

#### ***1.2.5        Why is the message type linked to the object type in the object type/property dialog? Doesn't the message type belong to the record type or document type (e.g. message type "ADT message" - record type "Patient master data" instead of object "Patient")*** ####
According to our metamodel, there are three ways in which an object type can be represented at the logical tool layer: as a message type, as a record type or as a document type. The representation as record type refers to the storage in a database system. The representation as message type refers to the communication between two computer-based application modules. The representation as document type refers to the storage in a document collection and the communication between two application components, of which at least one is paper-based.

#### ***1.2.6    Can I understand the master DBS as the database management system as it is named in the model browser?*** ####
No. See also [1.‎1.4](#markdown-header-114-what-should-i-do-with-the-master-dbs-in-the-object-typeproperties-dialoggeneral-on-the-domain-layer)

#### ***1.2.7    When is it necessary to model non-computer supported application component interfaces? (e.g. if something is first documented on paper and later captured with a computer-based application component - Service arrangement)*** ####
A typical example of data communication between a computer-based and a paper-based application component is the occurrence of media breaks. If one wants to model/see this, the following questions have to be asked for the interfaces:

* Is data captured in the paper-based area, which is then manually entered into a computer-based application system?
* Is data from a computer-based application system printed out and reused on paper?

Tip: Since users are not usually modeled in the 3LGM² toolbox, the use of the computer-based application module is not explicitly modeled. So if a paper is printed, read and then destroyed, it is usually not modeled. Reading would also have been possible on screen. If this case is to be modeled, e.g. to confirm the suspicion of an unnecessarily high amount of paper, a paper-based application component "Operation" can be modeled with a document collection "Recycle Bin".

#### ***1.2.8    In my understanding, there are no interfaces that can be both send and receive interfaces. Nevertheless, the meta-model and the modelling tool allow this. Why?*** ####
Our meta model allows one and the same interface to be both a sending and receiving interface. This is not obvious at first, since realistic interfaces are generally either send or receive interfaces. However, it should be considered that it will be desirable to model more abstractly. For example, if you only want to show that two application components communicate with each other in both directions, you will define an interface for each application component and connect them in both directions. The meta-model deliberately leaves open possibilities here, in order to enable the modeler to model his IS under different objectives and degrees of abstraction.

#### ***1.2.9    How can I assign its master DBS on the logical layer to my database system?*** ####
This is not possible. See also [1.‎1.4](#markdown-header-114-what-should-i-do-with-the-master-dbs-in-the-object-typeproperties-dialoggeneral-on-the-domain-layer)

#### ***1.2.10    ETMT is not offered for the record type, only for the document type. How can I then model this for two computer-based application components?*** ####
This is intended. Behind it is the following: If we are in the purely computer-based area, the communication takes place exclusively via message types, an ETMT combination is therefore not necessary for the data record type. The data record type only represents how object types are stored. In the paper-based area, on the other hand, the representation form document type is used both for communication and for storing object types. Therefore, we also need an ETMT combination ( which should actually be called ETDT combination ).

#### ***1.2.11    In the properties dialog for software products (in the model browser), the software products are assigned to functions and not to application components. Is this correct?*** ####
That is correct. In the metamodel this corresponds to the relationship 'can_support'. This enables you to model additional information about the software product itself. Not all tasks that a software product can support are actually supported after installation. This depends for example on the parameterization. Thus it is theoretically possible to determine whether additional tasks should not be supported by an application component based on a certain software product, for example to reduce the degree of heterogeneity.

#### ***1.2.12   Why are application programs assigned to software products and application components? I thought application programs are adapted software products (esp. parameter settings,...)*** ####
An application component is controlled by an application program (relationship is_controlled_by in the metamodel.). An application program is an adapted software product (relationship is_based_on in the metamodel). For the sake of simplicity, one can now directly assign a software product in the properties dialog of an application component, but this is actually assigned to the application program of this application component.

However, the modeling of these relations is rather unintuitive and in addition also faulty. For example, you can assign several software products in the properties dialog of an application component without seeing this in the properties dialog.

---------------------------
## **1.3     Physical Tool Layer** ##
#### ***1.3.1    Why is the subnet not simply called net? Why is there a subnet if there is no net?*** ####
There is no sophisticated rationale for this. The consideration of nets/subnets makes sense in most cases only in connection with subnets. The information that all physical data processiong components of a model belong to a single network often has no special value.

---------------------------
## **1.4     Inter-Layer Relationships** ##
#### ***1.4.1    Why are the application component configurations bound to organizational units?*** ####
It is often the case that it depends on the organizational unit which application component configuration supports which function. In order to be able to represent in which organizational units which application component configurations are used, this relationship between the three classes task, organizational unit and application component configuration was introduced.

#### ***1.4.2    Is there a possibility to assign DB management systems to data processing component configurations?*** ####
The meta model does not provide for this, nor does the modelling tool (accordingly). An assignment takes place only indirectly, via the database system and the application component. Perhaps you can give us a short example that could motivate the adaptation. We might also find an alternative modelling possibility that can be implemented with the available resources.

---------------------------
## **1.5     General** ##
#### ***1.5.1 What does the is_part_of relationship between object types mean?*** ####
This is_part_of relationship is to be understood in the sense of an aggregation. The resulting consequences for relationships between model components are illustrated in the following using the example of the relationship between the class Function and Object Type:

![istTeilVon01.PNG](https://bitbucket.org/repo/9L6rMz/images/1072977069-istTeilVon01.PNG)

1 All subtasks of PATIENT CALL access the object type CASE interpretatively.
2. the subtask ADMINISTRATIVE PATIENT CALL accesses all subtask object types in an editing manner.
The subtask MEDICAL PATIENT CALL accesses the object type DIAGNOSIS.


#### ***1.5.2    How do the actual_part_of relationships affect coarsening and refining?*** ####
![istTeilVon02.PNG](https://bitbucket.org/repo/9L6rMz/images/3419780724-istTeilVon02.PNG)

When coarsening an instance, the relationships of the subordinate instances are passed on to the parent instance. In the representation, a distinction is made between whether only parts are touched or the instance as a whole. In the example this is expressed by arrows of different colors. 

Black arrow: Interprets / processes all part object types of the object type PATIENT

Red arrow: Interprets / processes parts of the object type PATIENT. Which these are is not relevant in the coarsened representation.

For modeling, it is important to model relationships at the finest possible level, because these relationships are then correctly passed on to the higher-level instances. If modeling is done on a very coarse level, the assumption that the relationships also apply to all parts of an instance is valid.


#### ***1.5.3 What does the is_part_of relationship between application components mean?*** ####
All application components, which are subordinated to another application component by is_part_of relations, can communicate with each other arbitrarily. Therefore, modeling the communication between these application components is not necessary. It is strongly recommended - also for the sake of model consistency and possible analyses - not to model communication between these application components, even if this is currently allowed in 3LGM² and the 3LGM² modelling tool. 

If it is necessary to model communication between these application components, this is an indication to resolve the corresponding actual part_of relationships and, if necessary, to remove the parent application component from the corresponding submodel.

---------------------------

# **2. FAQ - 3LGM²-Tool** #

---------------------------
## **2.1     Domain Layer** ##

---------------------------
## **2.2     Logical Tool Layer** ##
#### ***2.2.1    Is DBS in the properties dialog of the DB Administration System the database system?*** ####
Yes. (for a better understanding the modelling tool should be adapted here!)

#### ***2.2.2    Are application programs named automatically and if so, according to which rules?*** ####
Application programs are created automatically and also named automatically when you select a software product in the properties dialog of an application component. The selected software product is not directly assigned to the application component, but to its application program. If an application program does not yet exist, it is created and automatically named. The name currently consists of the name of the application component, to which the character string 'Application program' is appended. Otherwise, an application program is only created by entering a name for the application program in the properties dialog of the application component.

#### ***2.2.3    Why is it not possible to create new instances for all elements in the Model Browser at the logical tool layer?*** ####
There are model elements in the 3LGM², which are in a dependency relationship to another model element (mostly this is the application component on the logical tool layer), i.e. instances of these model elements may only exist, if the associated application component exists For this reason it does not make sense to be able to create instances for these model elements in the model browser.
---------------------------
## **2.3     Physical Tool Layer** ##
#### ***2.3.1    Why is there no graphical representation form for network types?*** ####
Network types are used to describe subnets in more detail and are therefore to be understood as properties of subnets. However, since different subnets can be of the same network type, it is useful to keep a catalog of network types. Therefore, the corresponding model element exists in the meta model. A visualization of individual net types using special symbols or icons did not seem helpful to us.

---------------------------
## **2.4     Inter-Ebenen-Beziehungen** ##
#### ***2.4.1    How do you create the application component configuration for a function?*** ####
Via the tab Application component Configuration in the properties dialog of the function. Application component configurations always refer to an organizational unit (OU). Therefore, an OU must first be selected in the left part of the window. Then a set of application components ( = Application Component Configuration) can be selected in the right part of the window and assigned to the selected OU using the arrow keys. If no OUs are assigned to the function yet, this must be done on the corresponding tab. 

*(Tip: If you do not want to view the organizational units, but still want to create application component configurations, you can, for example, create an organizational unit 'hospital' and assign it to all functions (as a kind of dummy).)*

---------------------------
## **2.5     General** ##
#### ***2.5.1    How do you establish the connection between two graphically represented model elements?*** ####
You click the first model element with the left mouse button and the second model element with the right mouse button. With the second click a context menu opens automatically, with which you can model the relationship.

#### ***2.5.2    What sense does it make if you can create elements in the context menu that describe other elements in more detail (e.g. software product ...) and are not displayed graphically, but are only listed in the model browser, where a beginner would have difficulty assigning them later?*** ####
The instances of a model element created in the Model Browser are not only displayed in the Model Browser, but are also available for further modeling in the properties dialogs. The possibility to create instances via the context menu of the model browser extends the modeling strategies. It is quite conceivable, for example, that a modeler may decide to first capture all software products known to him that are used in his organization, so that these are subsequently available to him when modeling the application components.