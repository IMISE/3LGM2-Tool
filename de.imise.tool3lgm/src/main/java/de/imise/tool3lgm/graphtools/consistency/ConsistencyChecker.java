package de.imise.tool3lgm.graphtools.consistency;

import java.util.ArrayList;
import java.util.HashSet;

import javax.swing.JTable;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.GraphDocumentAdapter;
import de.imise.tool3lgm.graphtools.consistency.error.CardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.ErrorSolution;
import de.imise.tool3lgm.graphtools.consistency.error.ErrorSolutionLibraryVersion;
import de.imise.tool3lgm.graphtools.consistency.error.MaxCardinalityError;
import de.imise.tool3lgm.graphtools.consistency.error.MinCardinalityError;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.panel.NConnectionPanel;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.path.MetaPath;
import de.imise.tool3lgm.graphtools.path.PathFinder;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

/**
 * Die Klasse prüft die Konsistenz eines Modells. Es werden alle Kardinalitäten
 * überprüft und fehlerhafte Elemente zurück gegeben.
 * 
 * @author AXS created on 06.08.2008
 */
public class ConsistencyChecker extends GraphDocumentAdapter {

	/**
	 * Modell, das überprüft wird.
	 */
	private GDCollection gdcoll;

	/**
	 * Katalog der Lösungen zu den Fehlern
	 */
	private ErrorSolutionLibraryVersion solutionsLibrary;
	
	/**
	 * Die Kardinalitäts und Fehlerdefinitionen für die bei der Prüfung relevanten Kanten. Wenn diese Variable <code>null</code>
	 * ist, werden alle Kanten mit ihren Originalen Kardinalitäten geprüft.
	 */
	private ConsistencyDefinition consistencyDefinition = null;
	
	/**
	 * Erzeugt einen neuen <code>ConsistencyChecker</code> mit initialisierter
	 * <code>ErrorSolutionLibraryVersion</code>. 
	 */
	private ConsistencyChecker(){
		super();
		solutionsLibrary = new ErrorSolutionLibraryVersion();
	}

	/**
	 * Legt einen neuen Consistency-Checker an, der sich als Listener
	 * beim HauptModell der übergebenen Collection registeriert.
	 * 
	 * @param gdcoll
	 */
	public ConsistencyChecker(GDCollection gdcoll) {
		this();
		changeContext(gdcoll);
	}

	/**
	 * Setzt die übergebene Collection als aktuelle Collection
	 * 
	 * @param gscoll
	 */
	public void changeContext(GDCollection gdcoll){
		if (this.gdcoll != gdcoll) {
			if (this.gdcoll != null)
				this.gdcoll.removeGraphDocumentListener(this);
			this.gdcoll = gdcoll;
			if (this.gdcoll != null) {
				gdcoll.addGraphDocumentListener(this);
			}
		}
		updateErrorTable();
	}

	/**
	 * @param consistencyDefinition
	 */
	public void setConsistencyDefinition(ConsistencyDefinition consistencyDefinition) {
		this.consistencyDefinition = consistencyDefinition;
	}

	/**
	 * Löscht alle Elemente komplett, die fehlerhaft sind, deren Fehler man aber
	 * nicht behandeln kann. Darunter fallen alle Fehler, für die eine
	 * Error-Solution mit einem gültigen <code>MetaPath</code> zu einem
	 * verbundenen Element hinterlegt ist hinterlegt ist, das aber nicht
	 * erreichtbar ist, weil auch die Verbindung zu diesem Element fehlt. Somit
	 * kann der Fehler nirgends behoben werden und man kann das Element löschen.
	 * 
	 * In Metamodell 2.7 heißt das: Anwendungsbaustein-Konfigurationen ohne
	 * einen Anwendungsbautein könnte man im Dialog der Aufgaben an der mit der
	 * Anwendungsbaustein-Konfigurationen verbundenen AufOrgKombination beheben.
	 * Wenn aber sowohl die Verbindung zur AufOrgKombination oder deren
	 * Verbindung zu einer Aufgabe fehlt, dann wird diese Konfiguration einfach
	 * gelöscht, da man den Fehler nicht mehr sinnvoll beheben kann. Das gleiche
	 * gilt für physische DV-Baustein-Konfigurationen ohne
	 * Datenverarbeitungsbausteine. Dies kann man im Dialog der
	 * Anwendungsbausteine der Konfiguration beheben. Fehlt aber auch diese
	 * Verbindung, dann kann man die Konfiguration löschen.
	 */
	public static void clearUnfixableErrors(GDCollection gdcoll){
		ConsistencyChecker checker = new ConsistencyChecker();
		checker.gdcoll = gdcoll;
		//dieses Löschen muss man nicht rückgängig machen können -> BulkMode einschalten
		boolean oldBulkMode = checker.gdcoll.isBulkMode();
		checker.gdcoll.setBulkMode(true);
		for (CardinalityError err : checker.getInconsistencies()){
			if (!checker.isSolutionExecuteable(err)){
				ModelElement errorElement = err.getModelElement();
				checker.gdcoll.deleteElement(errorElement, TransactionManager.STANDARD_PID);
			}
		}
		//für alle explizit angegebenen nicht lösbaren Fehler -> lösche die betreffenden Elemente
		/*		for (Pair<Class<? extends ModelElement>, Class<? extends Kante>> pair : checker.solutionsLibrary.getMinCardinalityNoSolutuinErrors()){
			ArrayList<ModelElement> elements = gdcoll.getGraphDocument().getModelItems(pair.getFirstItem(), true);
			for (ModelElement me : elements){
				//System.err.println(me.getName() + "\t" + me.getHashString() + "\t" + Kante.getMinCardinality(me.getClass(), pair.getSecondItem()) + "\t" + me.countConnections(pair.getSecondItem()));
				if (me.countConnections(pair.getSecondItem()) < Kante.getMinCardinality(me.getClass(), pair.getSecondItem()))
					gdcoll.deleteElement(me, TransactionManager.STANDARD_PID);
			}
		}
		 */		
		checker.gdcoll.setBulkMode(oldBulkMode);
	}

	/**
	 * Liefert das Modell, das dieser Checker überprüft.
	 * 
	 * @return überprüftes Modell
	 */
	GDCollection getGDCollection() {
		return gdcoll;
	}

	////////////////////////////////////////////////////
	// Erstellen und Aktualisierern der Fehlertabelle //
	////////////////////////////////////////////////////

	/**
	 * COMMENTME
	 */
	private ConsistencyErrorTableGenerator tableGenerator;

	/**
	 * Liefert einen JTable, in dem alle Inkonsistenzen aufgelistet werden.
	 * 
	 * @return
	 */
	public JTable getErrorTable() {
		if (tableGenerator == null)
			tableGenerator = new ConsistencyErrorTableGenerator(this);
		return tableGenerator.getTable();
	}

	/**
	 * Aktualisiert die Fehlertabelle
	 */
	public void updateErrorTable(){
		if (tableGenerator == null)
			return;
		tableGenerator.updateTable();
	}

	@Override
	public void dataChanged(GraphDocument source) {
		//nicht abgeschlossene Dialogtransaktionen ignorieren -> erst updaten, wenn keine Transaktion mehr
		//offen ist
		if (!gdcoll.getTman().isInTransaction())
			updateErrorTable();
	}

	@Override
	public void elementAdded(GraphDocument source, ElementContainer element) {
		dataChanged(source);
	}

	@Override
	public void elementDeleted(GraphDocument source, ElementContainer element) {
		dataChanged(source);
	}

	/** Gibt wieder, ob Inkonsistenzen im Modell bestehen */
	public boolean hasInconsistencies() {
		return getInconsistencies().size() > 0;
	}

	/**
	 * @return
	 */
	public ArrayList<CardinalityError> getInconsistencies() {
		ArrayList<CardinalityError> returnList = new ArrayList<CardinalityError>();

		if (gdcoll == null)
			return returnList;

		GraphDocument doc = gdcoll.getMainGraphDocument();

		ArrayList<ModelElement> elements = new ArrayList<ModelElement>();

		// Klassen aller Elemente, die im Model vorkommen und wenigstens eine
		// Kantenart besitzen einsammeln
		HashSet<Class<? extends ModelElement>> classes = new HashSet<Class<? extends ModelElement>>();
		ArrayList<ModelElement> allMe = doc.getModelItems(ModelElement.class, true);
		for (ModelElement me : allMe) {
			if (ModelConstants.getEdgeTypes(me.getClass()) != null) {
				elements.add(me);
				classes.add(me.getClass());
			}
		}

		for (ModelElement me : elements) {
			Class<? extends ModelElement> meClass = me.getClass();
			Class<? extends Kante>[] edgeTypes = ModelConstants.getEdgeTypes(meClass);
			for (Class<? extends Kante> edgeClass : edgeTypes) {
				if (consistencyDefinition != null && !consistencyDefinition.contains(edgeClass))
						continue;
				//entweder für die aktuelle Kantenklasse die neu gesetzten Kardinalitäten holen oder die Standardwaerte laden, wenn keine neuen gesetzt wurden
				int minStartCard = consistencyDefinition == null ? Kante.getMinStartToEndCardinality(edgeClass) : consistencyDefinition.getMinStartToEndCardinality(edgeClass);
				int maxStartCard = consistencyDefinition == null ? Kante.getMaxStartToEndCardinality(edgeClass) : consistencyDefinition.getMaxStartToEndCardinality(edgeClass);
				int minEndCard = consistencyDefinition == null ? Kante.getMinEndToStartCardinality(edgeClass) : consistencyDefinition.getMinEndToStartCardinality(edgeClass);
				int maxEndCard = consistencyDefinition == null ? Kante.getMaxEndToStartCardinality(edgeClass) : consistencyDefinition.getMaxEndToStartCardinality(edgeClass);
				boolean meHasStartClass = Kante.isStartClass(edgeClass, me.getClass());
				boolean meHasEndClass = Kante.isEndClass(edgeClass, me.getClass());

				ArrayList<Kante> connections = me.getEdges(edgeClass);
				ArrayList<Kante> meIsStartConnections = new ArrayList<Kante>();
				ArrayList<Kante> meIsEndConnections = new ArrayList<Kante>();
				for (Kante edge : connections) {
					if (edge.isStart(me))
						meIsStartConnections.add(edge);
					else
						meIsEndConnections.add(edge);
				}

				// Bei Teil-Von-Beziehungen oder Beziehungen bei denen meClass
				// sowohl Start- als auch Endklasse sein können
				if (PartOfBeziehung.class.isAssignableFrom(edgeClass)) {
					if (meHasStartClass && meIsStartConnections.size() < minStartCard)
						returnList.add(new MinCardinalityError(me, edgeClass, minEndCard, gdcoll));
					if (meHasStartClass && meIsStartConnections.size() > maxStartCard)
						returnList.add(new MaxCardinalityError(me, edgeClass, meIsStartConnections, maxEndCard, gdcoll));
					if (meHasEndClass && meIsEndConnections.size() < minEndCard)
						returnList.add(new MinCardinalityError(me, edgeClass, minStartCard, gdcoll));
					if (meHasEndClass && meIsEndConnections.size() > maxEndCard)
						returnList.add(new MaxCardinalityError(me, edgeClass, meIsEndConnections, maxStartCard, gdcoll));
				} else if (meHasStartClass && meHasEndClass) {
					int card = minStartCard < minEndCard ? minEndCard : minStartCard;
					if (connections.size() < card)
						returnList.add(new MinCardinalityError(me, edgeClass, card, gdcoll));
					card = maxStartCard < maxEndCard ? maxStartCard : maxEndCard;
					if (connections.size() > card)
						returnList.add(new MaxCardinalityError(me, edgeClass, connections, card, gdcoll));
				} else if (meHasStartClass) {
					if (connections.size() < minStartCard)
						returnList.add(new MinCardinalityError(me, edgeClass, minStartCard, gdcoll));
					if (connections.size() > maxStartCard)
						returnList.add(new MaxCardinalityError(me, edgeClass, connections, maxStartCard, gdcoll));
				} else if (meHasEndClass) {
					if (connections.size() < minEndCard)
						returnList.add(new MinCardinalityError(me, edgeClass, minEndCard, gdcoll));
					if (connections.size() > maxEndCard)
						returnList.add(new MaxCardinalityError(me, edgeClass, connections, maxEndCard, gdcoll));
				} else {
					System.err.println("Die Kante darf gar nicht für dieses Element existieren!");
				}
			}
		}

		return returnList;
	}

	/**
	 * Prüft, ob die Kardinalitäten eines Elementes eingehalten sind. Die
	 * erstebeste Inkonsistenz, die gefunden wird, kann in einem Dialog
	 * angezeigt werden. Wird eine Inkonsistenz gefunden, kommt
	 * <code>false</code> zurück.
	 * 
	 * @param me
	 * @param showDialog
	 * @return
	 * /
	public static boolean _isConsistentMaxCardinality(ModelElement me, GDCollection gdcoll, boolean showDialog) {
		for (Kante edge1 : me.getEdges()) {
			int maxCard = (edge1.isStart(me) ? edge1.getMaxStartToEndCardinality() : edge1.getMaxEndToStartCardinality());
			me.getEdges(edge1.getClass());
			ArrayList<ModelElement> connected = me.getConnectedElements(edge1.getClass(), gdcoll.getGraphDocument());
			if (maxCard < connected.size()) {
				if (!showDialog)
					return false;
				StringBuilder sb = new StringBuilder("Das Element (!)");
				sb.append(" ");
				sb.append(ModelConstants.getDisplayableName(me.getClass()));
				sb.append(":\n\"");
				sb.append(me.getClearName());
				sb.append("\"\n ");
				sb.append("verletzt die Konsistenz des Modells. Es dürfte(n) maximal(!)");
				sb.append(" ");
				sb.append(maxCard);
				sb.append(" ");
				sb.append("Verbindung(en) zu folgenden Elementen bestehen:(!)");
				sb.append("\n");
				for (ModelElement conMe : connected) {
					sb.append(conMe.getClearName());
					sb.append("\n");
				}
				JOptionPane.showMessageDialog(Tool3lgm.tool, sb.toString(), "Inkonsistenz(!)", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
		}
		return true;
	}

	/**
	 * Diese Funktion hat folgende Rückgabewerte:<br />
	 * <ol>
	 * <li>Wenn für den Fehler eine <code>ErrorSolution</code> gefunden wird, die einen
	 * gültigen <code>MetaPath</code> beschreibt, über den ausgehend vom Element des 
	 * übergebenen Fehlers verbundene Elemente gefunden werden, dann kommen genau diese
	 * verbundenen Elemente zurück.</li>
	 * <li>Wenn die gleichen Vorbedingungen gelten, wie eben, aber keine verbundenen Elemente
	 * gefunden werden, dann kommt <code>null</code> zurück.</li>
	 * <li>Wenn für den Fehler eine <code>ErrorSolution</code> gefunden wird, diese aber keinen 
	 * <code>MetaPath</code> enthält, so kommt eine Liste mit dem ModelElement des Fehlers als
	 * einzigem Element zurück</li>
	 * <li>Wenn für den Fehler keine <code>ErrorSolution</code> gefunden wurde, kommt eine
	 * leere Liste zurück</li>
	 * </ol>
	 * 
	 * Zusäztlich dazu wird auch <code>null</code> zurück gegeben, wenn der übergebene
	 * Fehler selbst  <code>null</code> ist. Das kann man aber vorher ausschließen, so 
	 * dass die eindeutige Unterscheidung der einzelnen Fehlerarten möglich ist.
	 * 
	 * @param error
	 * @return
	 */
	private HashSet<ModelElement> getSolutionPropertyDialogElement(CardinalityError error) {
		if (error == null)
			return null;
		ErrorSolution es = solutionsLibrary.getSolution(error);
		if (es == null)
			return new HashSet<ModelElement>();
		MetaPath pathToDialogElement = es.getPathToPropertyDialogElement();
		GDCollection gdcoll = error.getGdcoll();
		// GraphDocument mainDoc = gdcoll.getGraphDocument();
		ModelElement me = error.getModelElement();
		if (pathToDialogElement != null) {
			HashSet<ModelElement> connected = PathFinder.getDirectConnectedElements(me, pathToDialogElement, gdcoll);
			if (connected.size() == 0)
				return null;
			return connected;
		}
		HashSet<ModelElement> al = new HashSet<ModelElement>(1);
		al.add(error.getModelElement());
		return al;
	}

	/**
	 * Liefert <code>true</code>, wenn es für diesen Fehler eine ausführbare
	 * Lösung gibt, sonst <code>false</code>.
	 * 
	 * @param error
	 * @return
	 */
	public boolean isSolutionExecuteable(CardinalityError error) {
		return getSolutionPropertyDialogElement(error) != null;
	}

	/**
	 * @param error
	 * @return
	 */
	public void execSolution(CardinalityError error) {
		ErrorSolution es = solutionsLibrary.getSolution(error);
		//'es' ist null, wenn für den Fehler keine Solution hinterlegt wurde. Das gilt nur
		//für Fehler, für die im Eigenschaftsdialog des Elementes dann ein zusätzliches
		//OneToNUndirectedConnectionPanel angezeigt werden soll, in dem man den Fehler beheben kann
		if (es==null){
			ElementPropertyDialog dialog = error.getModelElement().getPropertyDialog(gdcoll);
			Class<? extends ModelElement> otherClass = Kante.getOther(error.getEdgeClass(), error.getModelElement().getClass());
			NConnectionPanel tp = new NConnectionPanel(otherClass, dialog, error instanceof MinCardinalityError, true);
			String errorTabName = Tool3lgmConstants.getResString("error_error_dialog_tab") + " " + ModelConstants.getDisplayableName(otherClass);
			dialog.addTab(errorTabName, Tool3lgmConstants.getIcon("error.gif"), tp);
			dialog.selectTab(errorTabName, tp.getClass());
			dialog.showDialog();
		}else{
			HashSet<ModelElement> solutionPropertyDialogElement = getSolutionPropertyDialogElement(error);
			if (solutionPropertyDialogElement == null || solutionPropertyDialogElement.size()==0)
				return;
			for (ModelElement connected : solutionPropertyDialogElement) {
				ElementPropertyDialog dialog = connected.getPropertyDialog(gdcoll);
				dialog.selectTab(es.getPanelName(), es.getPanelClass());
				dialog.showDialog();
			}
		}
	}

}
