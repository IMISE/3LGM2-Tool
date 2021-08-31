[TOC]

---------------------------

# **1. Fragen zur Modellierung** #
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
No. See also [1.‎1.4](#markdown-header-114-what-should-i-do-with-the-Master-DBS-in-the-Object-TypeProperties-DialogGeneral-on-the-domain-layer)

#### ***1.2.7    When is it necessary to model non-computer supported application component interfaces? (e.g. if something is first documented on paper and later captured with a computer-based application component - Service arrangement)*** ####
A typical example of data communication between a computer-based and a paper-based application component is the occurrence of media breaks. If one wants to model/see this, the following questions have to be asked for the interfaces:

* Is data captured in the paper-based area, which is then manually entered into a computer-based application system?
* Is data from a computer-based application system printed out and reused on paper?

Tip: Since users are not usually modeled in the 3LGM² toolbox, the use of the computer-based application module is not explicitly modeled. So if a paper is printed, read and then destroyed, it is usually not modeled. Reading would also have been possible on screen. If this case is to be modeled, e.g. to confirm the suspicion of an unnecessarily high amount of paper, a paper-based application component "Operation" can be modeled with a document collection "Recycle Bin".

#### ***1.2.8    In my understanding, there are no interfaces that can be both send and receive interfaces. Nevertheless, the meta-model and the modelling tool allow this. Why?*** ####
Our meta model allows one and the same interface to be both a sending and receiving interface. This is not obvious at first, since realistic interfaces are generally either send or receive interfaces. However, it should be considered that it will be desirable to model more abstractly. For example, if you only want to show that two application components communicate with each other in both directions, you will define an interface for each application component and connect them in both directions. The meta-model deliberately leaves open possibilities here, in order to enable the modeler to model his IS under different objectives and degrees of abstraction.

#### ***1.2.9    Wie kann ich auf der logischen Ebene meinem Datenbanksystem seine Master-DBS zuordnen?*** ####
Das geht nicht. Siehe auch [1.‎1.4](#markdown-header-114-what-should-i-do-with-the-Master-DBS-in-the-Object-TypeProperties-DialogGeneral-on-the-domain-layer)

#### ***1.2.10    Beim Datensatztyp wird ETNT nicht angeboten, sondern nur beim Dokumententyp. Wie kann ich das dann bei zwei rechnerbasierten Anwendungsbausteinen modellieren?*** ####
Das ist so gewollt. Dahinter steckt folgendes: Wenn wir uns im rein rechnerbasierten Bereich befinden, erfolgt  die Kommunikation Ausschließlich über Nachrichtentypen, eine ETNT-Kombination ist daher beim Datensatztyp gar nicht notwendig. Der Datensatztyp repräsentiert nur wie Objekttypen gespeichert werden. Im papier-basierten Bereich dagegen wird die Repräsentationsform Dokumententyp sowohl für die Kommunikation als auch für die Speicherung von Objekttypen verwendet. Daher benötigen wir auch eine ETNT-Kombination (, die eigentlich ETDT-Kombination heißen müsste).

#### ***1.2.11    Im Eigenschaftendialog für Softwareprodukte im Modellbrowser werden die Softwareprodukte Aufgaben und nicht Anwendungsbausteinen zugeordnet. Ist das richtig?*** ####
Das ist so richtig. Im Metamodell entspricht das der Beziehung 'kann_unterstützen'. Damit ist man in der Lage eine Zusatzinformation zum Softwareprodukt selbst zu modellieren. Nicht alle Aufgaben, die ein Softwareprodukt unterstützen kann, werden nach der Installation auch tatsächlich unterstützt. Dies hängt beispielsweise von der Parametrierung ab. Dadurch ist es theoretisch möglich, festzustellen, ob nicht weitere Aufgaben von einem auf einem bestimmten Softwareprodukt basierenden Anwendungsbaustein unterstützt werden sollten, beispielsweise um den Grad der Heterogenität zu senken.

#### ***1.2.12    Warum werden Anwendungsprogramme Softwareprodukten und Anwendungs­bausteinen zugeordnet? Ich dachte  Anwendungsprogramme sind adaptierte Softwareprodukte (spez. Parametereinstellungen,...)*** ####
Ein Anwendungsbaustein wird gesteuert durch eine Anwendungsprogramm (Beziehung wird_gesteuert_durch im Metamodell.). Ein Anwendungsprogramm ist ein adaptiertes Softwareprodukt (Beziehung basiert_auf im Metamodell). Der Einfachheit halber kann man nun im Eigenschaftendialog eines Anwendungsbausteins direkt ein Softwareprodukt zuordnen, diese wird aber tatsächlich dem Anwendungsprogramm dieses Anwendungsbausteins zugeordnet.

Allerdings ist die Modellierung dieser Beziehungen ziemlich wenig intuitiv und dazu auch noch fehlerhaft. So kann man beispielsweise im Eigenschaftendialog eines Anwendungsbausteins mehrere Softwareprodukte zuordnen, ohne dass man das im Eigenschaftendialog sieht.

---------------------------
## **1.3     Physische Werkzeugebene** ##
#### ***1.3.1    Warum heißt das Subnetz nicht einfach Netz? Warum gibt es ein Subnetz wenn es kein Netz gibt?*** ####
Dazu gibt es keine ausgefeilte Begründung. Die Betrachtung von Netzen/Subnetzen macht in den meisten Fällen nur Sinn im Zusammenhang mit Subnetzen. Die Information, dass alle Physischen DV-Bausteine eines Modells zu einem einzigen Netz gehören, hat oft keinen besonderen Wert.

---------------------------
## **1.4     Inter-Ebenen-Beziehungen** ##
#### ***1.4.1    Warum sind die Anwendungsbausteinkonfigurationen an Organisationseinheiten gebunden?*** ####
Häufig ist es so, dass es von der Organisationseinheit abhängt, welche Anwendungsbaustein­konfiguration welche Aufgabe unterstützt. Um darstellen zu können, in welchen Organisationseinheiten welche Anwendungsbausteinkonfigurationen genutzt werden, wurde diese Beziehung zwischen den drei Klassen Aufgabe, Organisationseinheit und Anwendungsbausteinkonfiguration eingeführt.

#### ***1.4.2    Gibt es eine Möglichkeit, DB-Verwaltungssysteme zu DV-Baustein-Konfigurationen zuzuordnen?*** ####
Das Metamodell sieht das nicht vor, und der Baukasten (dementsprechend) auch nicht. Eine Zuordnung findet nur indirekt statt, über das Datenbanksystem und den Anwendungsbaustein. Vielleicht können Sie uns ein kurzes Beispiel beschreiben, das die Anpassung motivieren könnte. Evtl. finden wir auch eine alternative Modellierungsmöglichkeit, die sich mit den vorhandenen Mitteln umsetzen lässt.

---------------------------
## **1.5     Allgemeines** ##
#### ***1.5.1    Was bedeutet die Ist_Teil_von Beziehung zwischen Objekttypen?*** ####
Diese ist_Teil_von Beziehung ist im Sinne einer Aggregation zu verstehen. Die sich hieraus ergebenden Konsequenzen für Beziehungen zwischen Modellkomponenten werden am Beispiel der Beziehung zwischen der Klasse Aufgabe und Objekttyp im folgenden verdeutlicht:

![istTeilVon01.PNG](https://bitbucket.org/repo/9L6rMz/images/1072977069-istTeilVon01.PNG)

1. Alle Teilaufgaben von PATIENTENAUFNAHME greifen interpretierend auf den Objekttyp FALL zu.
2. Die Teilaufgabe ADMINISTRATIVE PATIENTENAUFNAHME greift bearbeitend auf alle Teil-Objekttypen zu.
3. Die Teilaufgabe ÄRZTLICHE PATIENTENAUFNAHME greift bearbeitend auf den Objekttyp DIAGNOSE zu.

#### ***1.5.2    Wie wirken sich die Ist-Teil_von Beziehungen beim Vergröbern und Verfeinern aus?*** ####
![istTeilVon02.PNG](https://bitbucket.org/repo/9L6rMz/images/3419780724-istTeilVon02.PNG)

Beim Vergröbern einer Instanz werden die Beziehungen der untergeordneten Instanzen an die übergeordnete Instanz weitergegeben. In der Darstellung wird unterschieden, ob lediglich Teile berührt sind, oder die Instanz als Ganzes. Im Beispiel ist dies durch farblich unterschiedliche Pfeile ausgedrückt. 

Schwarzer Pfeil: Interpretiert / bearbeitet alle Teil-Objekttypen des Objekttyps PATIENT

Roter Pfeil: Interpretiert / Bearbeitet Teile des Objekttyps PATIENT. Welche dies sind ist in der vergröberten Darstellung nicht relevant.

Für das Modellieren ist es wichtig, Beziehungen auf der feinst-möglichen Stufe zu modellieren, da diese Beziehungen dann richtig auf die übergeordneten Instanzen weitergegeben werden. Wird auf einer sehr groben Stufe modelliert greiftdie Annahme, dass die Beziehungen auch für alle Teile einer Instanz gelten.


#### ***1.5.3    Was bedeutet die Ist_Teil_von Beziehung zwischen Anwendungsbausteinen?*** ####
Alle Anwendungsbausteine, die einem anderen Anwendungsbaustein durch ist_Teil_von Beziehungen untergeordnet sind, können miteinander beliebig kommunizieren. Die Modellierung der Kommunikation zwischen diesen Anwendungsbausteinen ist daher nicht notwendig. Es wird dringend empfohlen - auch im Sinne der Modellkonsistenz und möglicher Analysen - Kommunikation zwischen diesen Anwendungsbausteinen nicht zu modellieren, auch wenn dies derzeit im 3LGM2 und im 3LGM2 Baukasten zulässig ist. 

Ist es notwendig die Kommunikation zwischen diesen Anwendungsbausteinen zu modellieren, ist das ein Indiz dafür die entsprechenden Ist-Teil_von Beziehungen aufzulösen und ggf. den übergeordneten Anwendungsbaustein aus dem entsprechenden Teilmodell zu entfernen.

---------------------------

# **2. Fragen zur Bedienung des 3LGM²-Baukastens** #

---------------------------
## **2.1     Fachliche Ebene** ##

---------------------------
## **2.2     Logische Werkzeugebene** ##
#### ***2.2.1    Ist DBS im Eigenschaftendialog vom DB-Verwaltungssystem das Datenbanksystem?*** ####
Ja. (zum besseren Verständnis sollte hier der Baukasten angepasst werden!)

#### ***2.2.2    Werden Anwendungsprogramme automatisch benannt und wenn ja nach welchen Regeln?*** ####
Anwendungsprogramme werden nicht automatisch benannt und auch nicht automatisch erzeugt. Erst durch Eingabe einer Bezeichnung für das Anwendungsprogramm im Eigenschaftendialog des Anwendungs­bausteins wird ein Anwendungsprogramm erzeugt.

#### ***2.2.3    Warum kann man auf der logischen Werkzeugebene nicht für alle Elemente im Modellbrowser neue Instanzen anlegen?*** ####
Es gibt Modellelemente im 3LGM2, die zu einem anderen Modellelement (meist ist dies auf der logischen Werkzeugebene der Anwendungsbaustein), in einer Abhängigkeitsbeziehung stehen, d. h. Instanzen dieser Modellelemente dürfen nur existieren, wenn der dazugehörige Anwendungsbaustein existiert Aus diesem Grund macht es keinen Sinn für diese Modellelemente Instanzen im Modellbrowser anlegen zu können.

---------------------------
## **2.3     Physische Werkzeugebene** ##
#### ***2.3.1    Warum gibt es keine grafische Repräsentationsform für Netztypen?*** ####
Es wurde noch kein geeignetes Visualisierungskonzept für Netztypen gefunden.

---------------------------
## **2.4     Inter-Ebenen-Beziehungen** ##
#### ***2.4.1    Wie bildet man zu einer Aufgabe die Anwendungsbausteinkonfiguration?*** ####
Über den Reiter Anwendungsbausteinkonfiguration im Eigenschaftendialog der Aufgabe. Anwendungsbausteinkonfigurationen beziehen sich immer auf eine Organisationseinheit (OE). Daher muss im linken Teil des Fensters zunächst eine OE ausgewählt werden. Danach kann meine eine Menge von Anwendungs­bausteinen ( = Anwendungsbausteinkonfiguration) im rechten Teil des Fensters auswählen und über die Pfeiltasten der ausgewählten OE zuordnen. Sind der Aufgabe noch keine OEs zugeordnet muss dies auf dem Reiter zunächst nachgeholt werden.

(Tipp: Will man die Organisationseinheiten nicht betrachten, aber trotzdem Anwendungs­bausteinkonfigurationen anlegen, so kann man sich beispielsweise eine Organisationseinheit 'Krankenhaus' anlegen und diese allen Aufgaben (als eine Art Dummy) zuordnen).

---------------------------
## **2.5     Allgemeines** ##
#### ***2.5.1    Gibt es eine Möglichkeit, die Zeichenfläche zu vergrößern? Bei größeren Modellen könnte das durchaus notwendig werden.*** ####
Um die Zeichenfläche zu vergrößern können Sie unten im Baukasten den Slider "Ebenengröße" verwenden.

#### ***2.5.2    Was für einen Sinn ergibt es, wenn man im Kontextmenü Elemente anlegen kann, die andere Elemente näher beschreiben (z.B. Softwareprodukt ...) und grafisch nicht dargestellt werden, sondern nur im Modellbrowser aufgelistet werden, wo sich ein Anfänger schwer tut diese später zuzuordnen?*** ####
Die im Modellbrowser angelegten Instanzen eines Modellelementes werden nicht nur im Modellbrowser angezeigt, sondern stehen für die weitere Modellierung in den Eigenschaftendialogen zur Verfügung. Die Möglichkeit, Instanzen über das Kontextmenü des Modellbrowsers zu erzeugen, erweitert die Modellierungsstrategien. Es ist durchaus vorstellbar, dass sich ein Modellierer beispielsweise entschließt, zunächst alle ihm bekannten in seiner Organisation verwendeten Softwareprodukte zu erfassen, damit ihm diese anschließend bei der Modellierung der Anwendungsbausteine zur Verfügung stehen.

#### ***2.5.3    Welche Inhalte verstecken sich im Kontextmenü „Internes (Verifizieren, interaktiv...)“?*** ####
Die im Kontextmenü unter Internes zu findenden Befehle sind nur im Zusammenhang mit der Entwicklung des 3lgm Baukasten von Bedeutung und werden daher für die Anwendung des Baukastens nicht benötigt.