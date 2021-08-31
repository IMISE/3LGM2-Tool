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
## **1.2     Logische Werkzeugebene** ###
#### ***1.2.1        Wie modelliere ich ein Datenbank, die zu keinem Anwendungsbaustein gehört?*** ####
Gemeint ist hier wohl ein Datenbanksystem. Ein Datenbanksystem kann im weitesten Sinne selbst ein Anwendungsbaustein sein. In diesem Fall muss aber zunächst ein Anwendungsbaustein definiert werden, der sozusagen als Hülle dient. Diesem Anwendungsbaustein wird dann ein Datenbanksystem zugeordnet. Der Anwendungsbaustein erhält Bausteinschnittstellen, über die andere Anwendungsbausteine dann auf die Datenbank zugreifen können.

#### ***1.2.2        Besteht die Möglichkeit für einen Anwendungsbaustein mehrere Datenbank­systeme zu erstellen?*** ####
Nach unserem Metamodell gibt es keine Möglichkeit, einem Anwendungsbaustein mehrere Daten­banken zuzuordnen. Dies ist aus unserer Erfahrung heraus nicht notwendig, da die uns bekannten Anwendungsbausteine auch immer nur ein Datenbanksystem besitzen. Allerdings kann es durchaus möglich sein, dass ein Anwendungsbaustein auf mehrere Datenbanksysteme zugreift. In diesem Fall sind zuerst die Datenbanksysteme und die sie besitzenden Anwendungsbausteine (siehe auch FAQ [1.2.1](#markdown-header-121-wie-modelliere-ich-ein-datenbank-die-zu-keinem-anwendungsbaustein-gehort)) zu modellieren. Der Zugriff muss dann über Bausteinschnittstellen und Kommunikations­beziehungen modelliert werden.

#### ***1.2.3        Gibt es Modellierungsrichtlinien auf der logischen Werkzeugebenebene für nicht rechnerbasierte Anwendungsbausteine?*** ####
Ja insbesondere bei Kommunikationsverbindungen zwischen (rechnerbasierte) Anwendungsbausteine und (nicht rechnerbasierte) Organisationssysteme.

In dieser Kommunikation werden die Daten entsprechend umgewandelt. 

* Bei der Kommunikation von Anwendungsbausteinen zu Organisationssysteme werden digital vorhandene Daten in physischer Form gebracht. Dies ist zum Beispiel dann der Fall, wenn Daten auf dem Bildschirm angezeigt, auf dem Drucker ausgedruckt, in Form akustischer Signale abgehört (Musik, Text) oder auf einer DVD gespeichert werden und diese Daten anschließend von Menschen innerhalb des
Organisationssystems weiterverarbeitet werden.

* Bei der Kommunikation von Organisationssysteme zu Anwendungsbausteinen werden physisch vorhandene Daten in digitaler Form gebracht. Dies ist zum Beispiel dann der Fall, wenn ein auf Papier vorhandenes
Dokument eingetippt, ein (aufgezeichnetes) Diktat geschrieben oder eine CD von Hand in ein Lesegerät
eingelegt wird, damit die darauf enthaltenen Daten von dem Anwendungssystem gelesen werden können.

Bei der Modellierung der Nachrichten sollte an dieser Stelle nicht davon ausgegangen werden, dass die Nachrichten bereits in der entsprechenden Form liegen.

Es empfiehlt sich vielmehr, zunächst einen Kommunikationsstandard zu modellieren, der Ein- bzw. Ausgaben in Anwendungssysteme beschreibt (z. B. „Eingaben in Anwendungssysteme“, „Ausgaben aus Anwendungssystemen“ oder auch „Ein-/Ausgaben in/aus Anwendungssystemen“); es können bei Bedarf auch Kommunikationsstandards separat für jedes betroffene Anwendungssystem modelliert werden (z. B. „Eingaben in Anwendungssystem XYZ“, „Ausgaben aus Anwendungssystem ABC“ oder auch „Ein-/Ausgaben in/aus Anwendungssystem EFG“). Diesen Kommunikationsstandards sollten dann Nachrichtentypen zugeordnet werden, die entweder recht grob beschreiben, dass eine bestimmte Menge von Objekttypen ein- bzw. ausgegeben werden oder aber auch detaillierter beschreiben, in welcher Form das erfolgt (z. B. „Bildschirmeingabe von Patientenstamm- und Patientenfalldaten“, „Audiowiedergabe eines Befunddiktats“, Ein-/Ausgabe“).

#### ***1.2.4        Wann ist eine Schnittstelle Benutzungsschnittstelle und wann Bausteinschnittstelle?*** ####
Eine Schnittstelle ist immer dann eine Bausteinschnittstelle, wenn damit eine Kommunikationsbeziehung zu einem anderen Anwendungs­baustein modelliert werden soll. Eine Benutzungsschnittstelle bezieht sich auf die Mensch-Maschine-Interaktion und betrachtet mehr softwareergonomische Aspekte.

#### ***1.2.5        Warum wird im Objekttyp/Eigenschaftendialog der Nachrichtentyp mit dem Objekttyp verbunden? Gehört der Nachrichtentyp nicht eher mit dem Datensatztyp bzw. dem Dokumententyp verbunden? (z.B. Nachrichtentyp „ADT-Meldung“ – Datensatztyp „Patientenstammdaten“ anstelle von Objekt „Patient“)*** ####
Nach unserem Metamodell gibt es drei Möglichkeiten, wie ein Objekttyp auf der logischen Werkzeugebene repräsentiert werden kann: als Nachrichtentyp, als Datensatztyp oder als Dokumententyp. Die Repräsentation als Datensatztyp bezieht sich auf die Speicherung in einem Datenbanksystem. Die Repräsentation als Nachrichtentyp bezieht sich auf die Kommunikation zwischen zwei rechnerbasierten Anwendungsbausteine. Die Repräsentation als Dokumententyp bezieht sich auf die Speicherung in einer Dokumentensammlung und die Kommunikation zwischen zwei Anwendungsbausteinen, von denen mindestens einer papierbasiert ist.

#### ***1.2.6    Kann ich unter dem Master-DBS das Datenbankverwaltungssystem verstehen, wie es im Modellbrowser benannt ist?*** ####
Nein. Siehe auch [1.‎1.4](#markdown-header-114-was-soll-ich-mit-der-master-dbs-beim-objekttypeigenschaftendialogallgemein-auf-der-fachlichen-ebene-anfangen) 

#### ***1.2.7    Wann ist es notwendig, nicht rechnerunterstützte Bausteinschnittstellen zu modellieren? (z.B. wenn zunächst etwas auf Papier dokumentiert und später mit einen rbAB erfasst wird – Leistungsanordnung)*** ####
Typisches Beispiel für die Datenkommunikation zwischen einem rechnerbasierten und einem papierbasierten Anwendungsbaustein ist das Auftreten von Medienbrüchen. Will man dieses modellieren/sehen, so sind für die Schnittstellen folgende Fragen zu stellen:

* Werden Daten im papierbasierten Bereich erfasst, die anschließend manuell in ein rechnerbasiertes Anwendungssystem eingegeben werden?
* Werden Daten aus einem rechnerbasierten Anwendungssystem ausgedruckt und auf Papier weiter verwendet?

Tipp: Da im 3LGM²-Baukasten in der Regel keine Benutzer modelliert werden, wird die Benutzung des rechnerbasierten Anwendungsbausteins nicht explizit modelliert. Wird also ein Papier ausgedruckt, gelesen und dann vernichtet, wird das in der Regel nicht modelliert. Das Lesen wäre auch am Bildschirm möglich gewesen. Soll dieser Fall modelliert werden, um z.B. den Verdacht des unnötig hohen Papieraufkommens zu bekräftigen, kann man einen papierbasierten Anwendungsbaustein „Betrieb“ modellieren mit einer Dokumentensammlung „Papierkorb“.

#### ***1.2.8    Nach meinem Verständnis gibt es keine Schnittstellen, die sowohl Sende- als auch Empfangsschnittstelle sein können. Trotzdem lässt das Meta-Modell und der Baukasten dies zu. Warum?*** ####
Unser Metamodell lässt es zu, dass ein und dieselbe Schnittstelle sowohl Sende- als auch Empfangsschnittstelle sein kann. Dies ist zunächst nicht einsichtig, da realistische Schnittstellen i. A. entweder Sende- oder Empfangsschnittstelle sind. Allerdings ist zu bedenken, dass es wünschenswert sein wird, abstrakter modellieren zu wollen. Möchte man beispielsweise lediglich darstellen, dass zwei Anwendungsbausteine in beide Richtungen miteinander kommunizieren, wird man für jeden AB eine Schnittstelle definieren und diese in beide Richtungen verbinden. Das Meta-Modell lässt hier bewusst Möglichkeiten offen, um es dem Modellierer zu ermöglichen sein IS unter verschiedenen Zielsetzungen und Abstraktionsgraden zu modellieren.

#### ***1.2.9    Wie kann ich auf der logischen Ebene meinem Datenbanksystem seine Master-DBS zuordnen?*** ####
Das geht nicht. Siehe auch [1.‎1.4](#markdown-header-114-was-soll-ich-mit-der-master-dbs-beim-objekttypeigenschaftendialogallgemein-auf-der-fachlichen-ebene-anfangen) 

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