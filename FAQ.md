# Fragen zur Modellierung #
## **1.     Fachliche Ebene** ##
#### ***1.1        Wie finde ich geeignete Objekttypen?*** ####
Zwar ist das Metamodell der fachlichen Ebene sehr einfach, und dem ersten Anschein nach ist es leicht ein geeignetes Modell zu erstellen. Allerdings ist es sehr anspruchsvoll geeignete Objekttypen und Aufgaben zu finden. Die größte Schwierigkeit wird darin liegen zu verstehen, was eigentlich Objekttypen sind. Dabei ist es sehr empfehlenswert sich noch mal die Definition des Begriffs Objekttyp klarzumachen: "...Durch Objekttypen kann beschrieben werden, welcher Art Informationen sind, die zur Bearbeitung einer Aufgabe benötigt werden oder nach Bearbeitung der Aufgabe bereitgestellt werden können." Dies führt zu folgenden Konsequenzen: Soll beispielsweise ein Arztbrief als physisches Ding der Welt modelliert werden, ist dabei zu bedenken, dass dieser auch Informationen über Patient, Fall, Diagnosen ... enthält, die in der Regel als eigene Objekttypen definiert werden. Für eine bestimmte Problemstellung kann es notwendig sein, diesen Zusammenhang (in Form von ist_Teil_von Beziehungen) darzustellen. Erst dadurch gelingt es die Komplexität der Wirklichkeit adäquat darzustellen. Aus oben genannten Gründen empfehlen wir für die Fachliche Ebene Referenzmodelle zu verwenden, soweit sie verfügbar sind, z.B. das fachliche Referenzmodell für ein Archiv.

#### ***1.2        Gibt es eine Möglichkeit die Organisationseinheiten grafisch darzustellen (z.B. Darstellung der Organisationseinheiten mit den dazugehörenden Aufgaben)?*** ####
Derzeit gibt es keine Möglichkeit Organisationseinheiten grafisch darzustellen. Damit soll vermieden werden, dass das graphische Modell 'überladen wird'.

#### ***1.3        Wieso sollen auf der fachlichen Ebene Nachrichtentypen, Dokumententypen und Datensatztypen (Repräsentationsform) festgelegt werden, die doch eher der logischen Werkzeugebene zugehören?*** ####
Nachrichtentypen, Dokumententypen und Datensatztypen gehören zur logischen Werkzeugebene. Im Eigenschaftendialog eines Objekttyps kann lediglich festgelegt werden, wie dieser auf der logischen Werkzeugebene repräsentiert wird. Um die Modellierung zu vereinfachen, ist es in diesem Zusammenhang auch möglich neue Nachrichtentypen, Dokumententypen und Datensatztypen anzulegen, wenn die benötigten Instanzen bisher noch nicht modelliert wurden.

#### ***1.4        Was soll ich mit der Master-DBS beim Objekttyp/Eigenschaftendialog/Allgemein auf der fachlichen Ebene anfangen?*** ####
Einem Objekttyp kann zugeordnet werden, welches Datenbanksystem / welche Dokumentensammlung für diesen Objekttyp führend ist (Beziehung hat_führende(s) im Metamodell). Mit Master-DBS ist im Baukasten das führende Datenbanksystem gemeint, an dieser Stelle ist die Terminologie zwischen 3lgm und 3lgm Baukasten nicht konsistent. 'Führend' heißt in diesem Zusammenhang, dass dieses Datenbanksystem /diese Dokumentensammlung) zu jedem Zeitpunkt die aktuellen Daten enthält. Alle anderen Datenbanksysteme/Dokumentensammlungen, die diesen Objekttyp speichern, müssen über Kommunikation zwischen den entsprechenden Anwendungsbausteinen ihre Datenbestände mit diesem Datenbanksystem / Dokumentensammlung abgleiche, wenn Daten geändert, hinzugefügt oder gelöscht werden, da sonst die Datenintegrität nicht gewährleistet ist. (Bei Datensammlungen ist dies natürlich besonders problematisch!).

## **2.     Logische Werkzeugebene** ###
#### ***2.1        Wie modelliere ich ein Datenbank, die zu keinem Anwendungsbaustein gehört?*** ####
Gemeint ist hier wohl ein Datenbanksystem. Ei Datenbanksystem kann im weitesten Sinne selbst Anwendungsbaustein sein. In diesem Fall muss aber zunächst ein Anwendungsbaustein definiert werden, der sozusagen als Hülle dient. Diesem Anwendungsbaustein wird dann ein Datenbanksystem zugeordnet. Der Anwendungsbaustein erhält Bausteinschnittstellen, über die andere Anwendungsbausteine dann auf die Datenbank zugreifen können.

#### ***2.2        Besteht die Möglichkeit für einen Anwendungsbaustein mehrere Datenbank­systeme zu erstellen?*** ####
Nach unserem Metamodell gibt es keine Möglichkeit, einem Anwendungsbaustein mehrere Daten­banken zuzuordnen. Dies ist aus unserer Erfahrung heraus nicht notwendig, da die uns bekannten Anwendungsbausteine auch immer nur ein Datenbanksystem besitzen. Allerdings kann es durchaus möglich sein, dass ein Anwendungsbaustein auf mehrere Datenbanksysteme zugreift. In diesem Fall sind zuerst die Datenbanksysteme und die sie besitzenden Anwendungsbausteine (siehe auch FAQ 5.) zu modellieren. Der Zugriff muss dann über Bausteinschnittstellen und Kommunikations­beziehungen modelliert werden.

#### ***2.3        Gibt es Modellierungsrichtlinien auf der logischen Werkzeugebenebene für nicht rechnerbasierte Anwendungsbausteine?*** ####
#### ***2.4        Wann ist eine Schnittstelle Benutzungsschnittstelle und wann Bausteinschnittstelle?*** ####
Eine Schnittstelle ist immer dann eine Bausteinschnittstelle, wenn damit eine Kommunikationsbeziehung zu einem anderen Anwendungs­baustein modelliert werden soll. Eine Benutzungsschnittstelle bezieht sich auf die Mensch-Maschine-Interaktion und betrachtet mehr softwareergonomische Aspekte.

#### ***2.5        Warum wird im Objekttyp/Eigenschaftendialog der Nachrichtentyp mit dem Objekttyp verbunden? Gehört der Nachrichtentyp nicht eher mit dem Datensatztyp bzw. dem Dokumententyp verbunden? (z.B. Nachrichtentyp „ADT-Meldung“ – Datensatztyp „Patientenstammdaten“ anstelle von Objekt „Patient“)*** ####
Nach unserem Metamodell gibt es drei Möglichkeiten, wie ein Objekttyp auf der logischen Werkzeugebene repräsentiert werden kann: als Nachrichtentyp, als Datensatztyp oder als Dokumententyp. Die Repräsentation als Datensatztyp bezieht sich auf die Speicherung in einem Datenbanksystem. Die Repräsentation als Nachrichtentyp bezieht sich auf die Kommunikation zwischen zwei rechnerbasierten Anwendungsbausteine. Die Repräsentation als Dokumententyp bezieht sich auf die Speicherung in einer Dokumentensammlung und die Kommunikation zwischen zwei Anwendungsbausteinen, von denen mindestens einer papierbasiert ist.

#### ***2.6    Kann ich unter dem Master-DBS das Datenbankverwaltungssystem verstehen, wie es im Modellbrowser benannt ist?*** ####
Nein. Siehe auch ‎1.4.

#### ***2.7    Wann ist es notwendig, nicht rechnerunterstützte Bausteinschnittstellen zu modellieren? (z.B. wenn zunächst etwas auf Papier dokumentiert und später mit einen rbAB erfasst wird – Leistungsanordnung)*** ####
Typisches Beispiel für die Datenkommunikation zwischen einem rechnerbasierten und einem papierbasierten Anwendungsbaustein ist das Auftreten von Medienbrüchen. Will man dieses modellieren/sehen, so sind für die Schnittstellen folgende Fragen zu stellen:

* Werden Daten im papierbasierten Bereich erfasst, die anschließend manuell in ein rechnerbasiertes Anwendungssystem eingegeben werden?
* Werden Daten aus einem rechnerbasierten Anwendungssystem ausgedruckt und auf Papier weiter verwendet?

Tipp: Da im 3LGM²-Baukasten in der Regel keine Benutzer modelliert werden, wird die Benutzung des rechnerbasierten Anwendungsbausteins nicht explizit modelliert. Wird also ein Papier ausgedruckt, gelesen und dann vernichtet, wird das in der Regel nicht modelliert. Das Lesen wäre auch am Bildschirm möglich gewesen. Soll dieser Fall modelliert werden, um z.B. den Verdacht des unnötig hohen Papieraufkommens zu bekräftigen, kann man einen papierbasierten Anwendungsbaustein „Betrieb“ modellieren mit einer Dokumentensammlung „Papierkorb“.

#### ***2.8    Nach meinem Verständnis gibt es keine Schnittstellen, die sowohl Sende- als auch Empfangsschnittstelle sein können. Trotzdem lässt das Meta-Modell und der Baukasten dies zu. Warum?*** ####
Unser Metamodell lässt es zu, dass ein und dieselbe Schnittstelle sowohl Sende- als auch Empfangsschnittstelle sein kann. Dies ist zunächst nicht einsichtig, da realistische Schnittstellen i. a. entweder Sende- oder Empfangsschnittstelle sind. Allerdings ist zu bedenken, dass es wünschenswert sein wird, abstrakter modellieren zu wollen. Möchte man beispielsweise lediglich darstellen, dass zwei Anwendungsbausteine in beide Richtungen miteinander kommunizieren, wird man für jeden AB eine Schnittstelle definieren und diese in beide Richtungen verbinden. Das Meta-Modell lässt hier bewusst Möglichkeiten offen, um es dem Modellierer zu ermöglichen sein IS unter verschiedenen Zielsetzungen und Abstraktionsgraden zu modellieren.

#### ***2.9    Wie kann ich auf der logischen Ebene meinem Datenbanksystem seine Master-DBS zuordnen?*** ####
Das geht nicht. Siehe auch ‎1.4.

#### ***2.10    Beim Datensatztyp wird ETNT nicht angeboten, sondern nur beim Dokumententyp. Wie kann ich das dann bei zwei rechnerbasierten Anwendungsbausteinen modellieren?*** ####
Das ist so gewollt. Dahinter steckt folgendes: Wenn wir uns im rein rechnerbasierten Bereich befinden, erfolgt  die Kommunikation Ausschließlich über Nachrichtentypen, eine ETNT-Kombination ist daher beim Datensatztyp gar nicht notwendig. Der Datensatztyp repräsentiert nur wie Objekttypen gespeichert werden. Im paper-basierten Bereich dagegen wird die Repräsentationsform Dokumententyp sowohl für die Kommunikation als auch für die Speicherung von Objekttypen verwendet. Daher benötigen wir auch eine ETNT-Kombination (, die eigentlich ETDT-Kombination heißen müsste).

#### ***2.11    Im Eigenschaftendialog für Softwareprodukte im Modellbrowser werden die Softwareprodukte Aufgaben und nicht Anwendungsbausteinen zugeordnet. Ist das richtig?*** ####
Das ist so richtig. Im Metamodell entspricht das der Beziehung 'kann_unterstützen'. Damit ist man in der Lage eine Zusatzinformation zum Softwareprodukt selbst zu modellieren. Nicht alle Aufgaben, die ein Softwareprodukt unterstützen kann, werden nach der Installation auch tatsächlich unterstützt. Dies hängt beispielsweise von der Parametrierung ab. Dadurch ist es theoretisch möglich, festzustellen, ob nicht weitere Aufgaben von einem auf einem bestimmten Softwareprodukt basierenden Anwendungsbaustein unterstützt werden sollten, beispielsweise um den Grad der Heterogenität zu senken.

#### ***2.12    Warum werden Anwendungsprogramme Softwareprodukten und Anwendungs­bausteinen zugeordnet? Ich dachte  Anwendungsprogramme sind adaptierte Softwareprodukte (spez. Parametereinstellungen,...)*** ####
Ein Anwendungsbaustein wird gesteuert durch eine Anwendungsprogramm (Beziehung wird_gesteuert_durch im Metamodell.). Ein Anwendungsprogramm ist ein adaptiertes Softwareprodukt (Beziehung basiert_auf im Metamodell). Der Einfachheit halber kann man nun im Eigenschaftendialog eines Anwendungsbausteins direkt ein Softwareprodukt zuordnen, diese wird aber tatsächlich dem Anwendungsprogramm dieses Anwendungsbausteins zugeordnet.

Allerdings ist die Modellierung dieser Beziehungen ziemlich wenig intuitiv und dazu auch noch fehlerhaft. So kann man beispielsweise im Eigenschaftendialog eines Anwendungsbausteins mehrere Softwareprodukte zuordnen, ohne das man das im Eigenschaftendialog sieht.

## 3.     Physische Werkzeugebene ##
#### ***3.1    Warum heißt das Subnetz nicht einfach Netz? Warum gibt es ein Subnetz wenn es kein Netz gibt?*** ####
Dazu gibt es keine ausgefeilte Begründung. Die Betrachtung von Netzen/Subnetzen macht in den meisten Fällen nur Sinn im Zusammenhang mit Subnetzen. Die Information, dass alle Physischen DV-Bausteine eines Modells zu einem einzigen Netz gehören, hat oft keinen besonderen Wert.

## **4.     Inter-Ebenen-Beziehungen** ##
#### ***4.1    Warum sind die Anwendungsbausteinkonfigurationen an Organisationseinheiten gebunden?*** ####
Häufig ist es so, dass es von der Organisationseinheit abhängt, welche Anwendungsbaustein­konfiguration welche Aufgabe unterstützt. Um darzustellen zu können, in welchen Organisationseinheiten welche Anwendungsbausteinkonfigurationen genutzt werden, wurde diese Beziehung zwischen den drei Klassen Aufgabe, Organisationseinheit und Anwendungsbausteinkonfiguration eingeführt.

#### ***4.2    Gibt es eine Möglichkeit, DB-Verwaltungssysteme zu DV-Baustein-Konfigurationen zuzuordnen?*** ####
Das Metamodell sieht das nicht vor, und der Baukasten (dementsprechend) auch nicht. Eine Zuordnung findet nur indirekt statt, über das Datenbanksystem und den Anwendungsbaustein. Vielleicht können Sie uns ein kurzes Beispiel beschreiben, das die Anpassung motivieren könnte. Evtl. finden wir auch eine alternative Modellierungsmöglichkeit, die sich mit den vorhandenen Mitteln umsetzen lässt.

## **5.     Allgemeines** ##
#### ***5.1    Was bedeuten die ist_Teil_von-Beziehungen bei den Aufgaben, Objekttypen, Organisationseinheiten, Anwendungsbausteinen und physischen Datenverarbeitungsbausteinen?*** ####