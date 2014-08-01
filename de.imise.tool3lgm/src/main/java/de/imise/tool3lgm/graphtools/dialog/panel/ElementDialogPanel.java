/*
 * Created on 08.01.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMComponentListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMWindowListener;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.tree.TreeRenderer;


/**
 * @author Thomas
 * 
 * Ist ein TestPanel zur Verallgemeinerung abgeleiteter Panels
 *
 */
public abstract class ElementDialogPanel extends JPanel  {

	/**
	 * GraphDocument, für das das Panel geöffnet wurde. Über das Panel neu angelegte Elemente
	 * sind automatisch in diesem GraphDocument enthalten.
	 */
	protected GraphDocument doc;
	/**
	 * Das Hauptdokument des Modells. In den Dialogen werden immer alle Verbindungen angezeigt, die
	 * in diesem Dokument vorkommen.
	 */
	protected GraphDocument mainDoc;
	/**
	 * Der Dialog in dem sich dieses Panel befindet
	 */
	protected ElementPropertyDialog dialog;
	/**
	 * Liste mit allen Elementen, die im Panel selektiert sind. Da ein Panel mehrere Bäume
	 * enthalten kann, wird sich in dieser Liste sozusagen die Gesamtselektion gemerkt.
	 */
	protected List<ElementContainer> highlight = new ArrayList<ElementContainer>(0);
	
	/**
	 * COMMENTME
	 */
	protected List<Object> specialInfoOwner = new ArrayList<Object>(0);

	/**
	 * COMMENTME
	 */
	protected boolean alreadyInitialized = false ;
	
	/**
	 * COMMENTME
	 */
	protected EventObject lastSelEvent = null;
	
	/**
	 * Legt fest, ob auch die rechte Seite des Panels angezeigt werden soll.
	 */
	protected boolean rightSideVisible = true;
	
	/**
	 * Der Renderer der Bäume
	 */
	protected TreeRenderer treeRenderer;
	
	/**
	 * COMMENTME
	 */
	private int correctingSelectionCount = 0;
	
	/**
	 * COMMENTME
	 */
	private LGMWindowListener windowListener;
	
	/**
	 * COMMENTME
	 */
	private LGMComponentListener componentListener;
	
	/**
	 * COMMENTME
	 */
	protected JButton viewButton;
	
	/**
	 * COMMENTME
	 */
	protected LGMAction showAllAction;
	
	/**
	 * COMMENTME
	 */
	protected LGMAction showPartlyAction;
	
	/**
	 * COMMENTME
	 */
	private LGMAction windowClosedAction;
	
	/**
	 * COMMENTME
	 */
	private LGMAction componentShownAction;
	
	/** ****************************************************************************** */

	/**
	 * TODO: die Konstruktoren braucht man sicher nicht mehr, wenn alle Panels mit Kanten oder Pfaden initialisiert werden
	 * 
	 * @param dialog Dialog, der dieses Panel enthält
	 */
	public ElementDialogPanel(ElementPropertyDialog dialog) {
		this(dialog, (String)null);
	}

	
	/**
	 * @param dialog Dialog, der dieses Panel enthält
	 * @param name
	 */
	public ElementDialogPanel(ElementPropertyDialog dialog, String name) {
		super();
		internalInit(dialog, name);
	}
	/**
	 * @param dialog
	 * @param name
	 */
	private void internalInit(ElementPropertyDialog dialog, String name){
		setName(name);
		
		doc = dialog.getGraphDocument();
		mainDoc = doc.getCollection().getMainGraphDocument();
		this.dialog = dialog;
		treeRenderer = new TreeRenderer(doc);
		setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 0));
		highlight = new ArrayList<ElementContainer>();
		
		windowClosedAction = LGMActionLibrary.getWindowClosedAction(this);
		componentShownAction = LGMActionLibrary.getComponentShownAction(this);
		
		windowListener = new LGMWindowListener(null, windowClosedAction, null, null, null, null, null);
		componentListener = new LGMComponentListener(null, null, null, componentShownAction);
		
		dialog.addWindowListener(windowListener);
		addComponentListener(componentListener);
		
//		 Aktionen für den button setzen
		showPartlyAction = getShowPartlyAction(this);
		viewButton = new JButton();
		showAllAction = getShowAllAction(this);
		viewButton.setAction(showAllAction);
	}
	
	
	/**
	 * Initialisert das Panel
	 */
	protected void init() {
		viewButton.setAction(showAllAction);
		//System.out.println("Ich bin: " + this.getClass().getName());
	}
	
	protected void showFullDialog() {
		viewButton.setAction(showPartlyAction);
	}
	
//	----------------------------------------------------------------------------------------------------------------------------------


//	----------------------------------------------------------------------------------------------------------------------------------

	public void update() {
		init();
		if (rightSideVisible) 
			showFullDialog();
	}

	public void commit() {	
	}
	
//	----------------------------------------------------------------------------------------------------------------------------------


//	----------------------------------------------------------------------------------------------------------------------------------
	protected void add(Container con, Component c, GridBagConstraints gbc, int x, int y, int w, int h) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		con.add(c, gbc);
	}

//	----------------------------------------------------------------------------------------------------------------------------------

	protected void expandTree(JTree tree) {
		for (int n = 0; n < tree.getRowCount(); n++)
			tree.expandRow(n);
	}
	
//	----------------------------------------------------------------------------------------------------------------------------------

	public void removeHighLightsAndSpecialInfos(){
		removeHighLights();
		removeSpecialInfos();
	}
	
	public void removeHighLights(){
		for (int b = 0; b < highlight.size(); b++) {
			ElementContainer ec = highlight.get(b);
			if (ec!=null)
				ec.setHighLight(false);
		}
		highlight.clear();
	}

	public void removeSpecialInfos(){
		for (int i = 0; i < specialInfoOwner.size(); i++) {
//TODO:AXS: das hier geht für LGMProcessSteps jetzt auf jeden Fall schief, weil sie keine ElementContainer mehr sind
			
			ElementContainer ec = (ElementContainer) specialInfoOwner.get(i);
			if (ec!=null) {
//				ec.deleteSpecialInfoFromMyTargets();
			}
		}
		specialInfoOwner.clear();
	}
	
//	----------------------------------------------------------------------------------------------------------------------------------
	
	/**
	 * @return doc
	 */
	public GraphDocument getGraphDocument(){
		return doc;
	}
	
	/**
	 * 
	 */
	public void clearHighlight(){
		highlight.clear();
	}
	
	/**
	 * @param ec
	 */
	public void addHighlight(ElementContainer ec){
		highlight.add(ec);
	}
	
	public void setHighlightVector(List<ElementContainer> list) {
		highlight = list;
	}
	
	/**
	 * @return dialog
	 */
	public ElementPropertyDialog getDialog() {
		return dialog;
	}
	
	/**
	 * @return correctingSelectionCount
	 */
	public int getCorrectingSelectionCount() {
		return correctingSelectionCount;
	}
	
	/**
	 * @return lastSelEvent
	 */
	public EventObject getLastSelEvent() {
		return this.lastSelEvent;
	}
	
	public void setLastSelEvent(EventObject eo) {
		this.lastSelEvent = eo;
	}
	
	public void setCorrectingSelectionCount(int correctedCount) {
		this.correctingSelectionCount = correctedCount;
	}
	
	/**
	 * @return rightSideVisible
	 */
	public boolean getRightSideVisible() {
		return rightSideVisible;
	}
	
	public void setRightSideVisible(boolean rightSideVisible)  {
		this.rightSideVisible = rightSideVisible;
	}
	
	/**
	 * @return alreadyInitialized
	 */
	public boolean isAlreadyInitialized() {
		return alreadyInitialized;
	}
	
	
	public void setAlreadyInitialized(boolean b) {
		alreadyInitialized = b;
	}
	
	/**
	 * @return modelElement
	 */
	public ModelElement getModelElement() {
		return dialog.getModelElement();
	}
	
//-------------------------------------------------------------------------------- -/	
	public void showFullDialog(boolean b) {
		
		if (b == true) {
			rightSideVisible = true;
			showFullDialog();
		}
		else {
			rightSideVisible = false;
			init();
		}
	}
	
	/**
	 * Methode liefert eine <code>LGMAction</code> zurück, die das gesamte
	 * Panel anzeigen lässt.
	 * 
	 * Diese <code>LGMAction</code> sollte an den "viewButton" eines Panels
	 * angefügt werden, falls der Inhalt des Panels nur teilweise zu sehen ist.
	 * 
	 * @param edp
	 */
	public static final LGMAction getShowAllAction(ElementDialogPanel edp) {
		final ElementDialogPanel panel = edp;
		return new LGMAction("", Tool3lgmConstants.getIcon("zu.gif")) {
			@Override
			public void execute(EventObject e) {
				panel.showFullDialog(true);
			}
		};
	}

	/**
	 * Methode liefert eine <code>LGMAction</code> zurück, die nur einen Teil
	 * des Panels anzeigen lässt.
	 * 
	 * Diese <code>LGMAction</code> sollte an den "viewButton" eines Panels
	 * angefügt werden, falls der Inhalt des Panels vollständig zu sehen ist.
	 * 
	 * @param edp
	 */
	public static final LGMAction getShowPartlyAction(ElementDialogPanel edp) {
		final ElementDialogPanel panel = edp;
		return new LGMAction("", Tool3lgmConstants.getIcon("auf.gif")) {
			@Override
			public void execute(EventObject e) {
				panel.showFullDialog(false);
			}
		};
	}


	
}
