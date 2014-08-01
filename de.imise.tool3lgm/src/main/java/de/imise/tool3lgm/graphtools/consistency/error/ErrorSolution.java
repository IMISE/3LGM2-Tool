package de.imise.tool3lgm.graphtools.consistency.error;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.path.MetaPath;

public class ErrorSolution {

	/**
	 * Elementklasse, zu der dieser Fehler gehört.
	 */
	private Class<? extends ModelElement> targetClass;
	
	/**
	 * Assoziationsklasse, deren Anzahl für Elemente der <code>targetClass</code> nicht
	 * korrekt sein kann.
	 */
	private Class<? extends Kante> edgeClass;
	
	/**
	 * Pfad ausgehend vom targetElement des Fehlers hin zu den Element(en), in dessen
	 * Eigenschaftsdialog man den Fehler anzeigen oder beheben kann. In der Regel wird
	 * das nur ein Element sein, aber theoretisch können es beliebig viele sein. 
	 * 
	 * Wenn dieser Pfad <code>null</code> ist, dann wird davon ausgegangen, dass das
	 * angegebene Panel im Eigeschaftsdialog des Elementes mit der <code>targetClass</code>
	 * selbst enthalten ist.  
	 * 
	 * Diesen speziellen MetaPath braucht man nur zu setzen, wenn der Fehler bei einem 
	 * Element auftritt, das keinen eigenen Eigenschaftsdialog hat, wie zum Beipsiel 
	 * <code>AWBKonfiguration</code>. Sie werden im Eigenschaftsdialog von Aufgaben 
	 * angezeigt und zusammengesetzt.
	 */
	private MetaPath pathToPropertyDialogElement;
	
	/**
	 * Klasse des Panels, in dem man den Fehler anzeigen bzw. beheben kann. Die Kombination
	 * aus <code>panelClass</code> und <code>panelName</code> sollte eindeutig sein.
	 */
	private Class<? extends ElementDialogPanel> panelClass;
	
	/**
	 * Name des Panels, in dem man den Fehler anzeigen bzw. beheben kann. Die Kombination
	 * aus <code>panelClass</code> und <code>panelName</code> sollte eindeutig sein.
	 */
	private String panelName;
	
	/**
	 * @param targetClass
	 * @param edgeClass
	 * @param pathToPropertyDialogElement
	 * @param panelClass
	 * @param panelNameResKey
	 */
	public ErrorSolution(Class<? extends ModelElement> targetClass, Class<? extends Kante> edgeClass, MetaPath pathToPropertyDialogElement, Class<? extends ElementDialogPanel> panelClass, String panelNameResKey){
		super();
		this.targetClass = targetClass;
		this.edgeClass = edgeClass;
		this.pathToPropertyDialogElement = pathToPropertyDialogElement;
		this.panelClass = panelClass;
		this.panelName = Tool3lgmConstants.getResString(panelNameResKey);
	}
	
    /**
     * @param targetClass
     * @param edgeClass
     * @param panelClass
     * @param panelNameResKey
     * @param edgeClassToPanelElement
     * /
    public ErrorSolution(Class<? extends ModelElement> targetClass, Class<? extends Kante> edgeClass, Class<? extends ElementDialogPanel> panelClass, String panelNameResKey, Class<? extends Kante> edgeClassToPanelElement, int i){
		this(targetClass, edgeClass, (MetaPath)null, panelClass, panelNameResKey);
		if (edgeClassToPanelElement!=null)
			try {
				pathToPropertyDialogElement = new MetaPath(targetClass, Kante.getOther(edgeClassToPanelElement, targetClass), edgeClassToPanelElement);
	        } catch (Exception e) {
		        e.printStackTrace();
	        }
	}

    /**
     * 
     * 
     * @param targetClass
     * @param edgeClass
     * @param panelClass
     * @param panelNameResKey
     */
	public ErrorSolution(Class<? extends ModelElement> targetClass, Class<? extends Kante> edgeClass, Class<? extends ElementDialogPanel> panelClass, String panelNameResKey){
		this(targetClass, edgeClass, (MetaPath)null, panelClass, panelNameResKey);
	}

	/**
     * @return the targetClass
     */
    public Class<? extends ModelElement> getTargetClass() {
    	return targetClass;
    }

	/**
     * @return the edgeClass
     */
    public Class<? extends Kante> getEdgeClass() {
    	return edgeClass;
    }

	/**
     * @return the pathToPropertyDialogElement
     */
    public MetaPath getPathToPropertyDialogElement() {
    	return pathToPropertyDialogElement;
    }

	/**
     * @return the panelClass
     */
    public Class<? extends ElementDialogPanel> getPanelClass() {
    	return panelClass;
    }

	/**
     * @return the panelName
     */
    public String getPanelName() {
    	return panelName;
    }

    
}
