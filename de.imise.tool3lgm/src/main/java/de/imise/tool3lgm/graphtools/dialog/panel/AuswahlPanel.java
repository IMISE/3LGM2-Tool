package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EventObject;

import javax.swing.JLabel;

import de.imise.util.NamedObjectContainer;
import de.imise.util.swing.component.AlphabeticalComboBox;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMItemListener;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwpSwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KawbDoksVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbAwpVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.RawbDbsVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Anwendungsprogramm;
import de.imise.tool3lgm.graphtools.elements.node.Datenbanksystem;
import de.imise.tool3lgm.graphtools.elements.node.Dokumentensammlung;
import de.imise.tool3lgm.graphtools.elements.node.Softwareprodukt;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;

/**
 * @author AXS
 */
public class AuswahlPanel extends ElementDialogPanel {
	
	/**
	 * COMMENTME
	 */
	private AlphabeticalComboBox box;

	/**
	 * COMMENTME
	 */
	private JLabel westLabel;

	/**
	 * COMMENTME
	 */
	private Class<? extends ModelElement> searchElementClass;
	
	/**
	 * COMMENTME
	 */
	private NamedObjectContainer<?> createNew = null;
	
	/**
	 * COMMENTME
	 */
	private ItemListener itemListener;

	/**
	 * @param searchElementClass
	 * @param p
	 */
	public AuswahlPanel(Class<? extends ModelElement> searchElementClass, ElementPropertyDialog p) {
		super(p);
		this.searchElementClass = searchElementClass;
		setLayout(new BorderLayout());
		box = new AlphabeticalComboBox();
		//Action erstell und Listener an Panel und Box anhängen
		box.addMouseListener(new LGMMouseListener(null, null, null, getMouseAction(), null));
		
		itemListener = new LGMItemListener(getItemStateChangedAction(this, searchElementClass));
		box.addItemListener(itemListener);
		
		add(box, BorderLayout.CENTER);
		
		//Das WestLabel auf jeden Fall initialisieren, denn es kann von anderen Panels dann hinzugefügt werden
		westLabel = new JLabel();
		westLabel.setText(Tool3lgmConstants.getResString(searchElementClass.getSimpleName()));
		createNew = new NamedObjectContainer<Object>(this, Tool3lgmConstants.getResString("auswahlPanel_neu") + " " + ModelConstants.getDisplayableName(searchElementClass));
		init();
	}

	/**
	 * @param searchElementClass
	 * @param p
	 * @param addLabel
	 */
	public AuswahlPanel(Class<? extends ModelElement> searchElementClass, ElementPropertyDialog p, boolean addLabel) {
		this(searchElementClass, p);
		if (addLabel == false)
			remove(westLabel);
	}

	/**
	 * @return
	 */
	public JLabel getWestLabel() {
		return westLabel;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.ElementDialogPanel#init()
	 */
	@Override
	protected void init() {		
		super.init();
		doc.start_transaction(dialog.getTransactionID(), false);
		box.removeItemListener(itemListener);
		box.removeAllItems();
		box.addItem("");
		box.addItem(createNew);
		box.addSeparator(false);
		ArrayList<ElementContainer> connect = mainDoc.getElementContainer(searchElementClass);
		ModelElement modelElement = getModelElement();
		if (modelElement instanceof Anwendungsbaustein) {
			if (Datenbanksystem.class.isAssignableFrom(searchElementClass) || Dokumentensammlung.class.isAssignableFrom(searchElementClass)) {
				connect = ((Knoten) modelElement).getConnectedContainer(searchElementClass, mainDoc);
			} else if (Softwareprodukt.class.isAssignableFrom(searchElementClass)) {
				for (int m = 0; m < connect.size(); m++)
					box.addItem(connect.get(m));
			
				connect = new ArrayList<ElementContainer>();
				ArrayList<ElementContainer> awp = ((Knoten) modelElement).getConnectedContainer(Anwendungsprogramm.class, mainDoc);
				if (awp.size() > 0)
					connect = ((NodeContainer) awp.get(0)).getKnoten().getConnectedContainer(searchElementClass, mainDoc);
			}
		}else {
			for (int m = 0; m < connect.size(); m++)
				box.addItem(connect.get(m));
			connect = ((Knoten) modelElement).getConnectedContainer(searchElementClass, mainDoc);
		}
	
		for (int m = 0; m < connect.size(); m++) {
			NodeContainer kc = (NodeContainer) connect.get(m);
			box.removeItem(kc); 
			box.addItem(kc);
		}
		if (connect.size() > 0)
			box.setSelectedItem(connect.get(0));
		doc.finish_transaction(dialog.getTransactionID(), false);
		box.addItemListener(itemListener);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.ElementDialogPanel#showFullDialog()
	 */
	@Override
	protected void showFullDialog() {
		super.showFullDialog();
	}
	
	/**
	 * @return
	 */
	public AlphabeticalComboBox getBox() {
		return this.box;
	}
	
	/**
	 * @param occp
	 * @param elementClass
	 * @return
	 */
	private static final LGMAction getItemStateChangedAction(AuswahlPanel occp, Class<? extends ModelElement> elementClass) {
		final GraphDocument mainDoc = occp.getGraphDocument();
		final GDCollection gdcoll = mainDoc.getCollection();
		final Class<? extends ModelElement> searchElementClass = elementClass;
		final ElementPropertyDialog dialog = occp.getDialog();
		final AuswahlPanel panel = occp;
		final ModelElement modelElement = occp.getModelElement();

		return new LGMAction() {
			@Override
			public void execute(EventObject eo) {
				if (!(eo instanceof ItemEvent))
					return;
				ItemEvent e = (ItemEvent) eo;
				Object selected = e.getItem();
				mainDoc.start_transaction(dialog.getTransactionID());
				
				//vor jedem select gibt es ein Deselect, wenn erst etwas selektiert war -> alte Verbindung trennen
				if (e.getStateChange() == ItemEvent.DESELECTED) {
					if (selected instanceof NodeContainer) {
						if (Softwareprodukt.class.isAssignableFrom(searchElementClass)) {
							Softwareprodukt swp = (Softwareprodukt) ((NodeContainer) selected).getElement();
							for (ModelElement awp : modelElement.getConnectedElements(Anwendungsprogramm.class))
								gdcoll.unlink(awp, swp, dialog.getTransactionID());
						} else {
							NodeContainer knot = (NodeContainer) selected;
							gdcoll.unlink(modelElement, knot.getElement(), dialog.getTransactionID());
						}
						modelElement.getContainer(mainDoc).refreshText();
						mainDoc.finish_transaction(dialog.getTransactionID());
						return;
					}
				}
				
				//Neues Eley
				if (selected == panel.createNew) {
					if (Softwareprodukt.class.isAssignableFrom(searchElementClass)) {
						NodeContainer nc = mainDoc.createKnotenWithContainer(Softwareprodukt.class, dialog.getTransactionID());
						if (nc != null) {
							ModelElement swp = nc.getElement();
							ElementContainer awp = null;
							ArrayList<ElementContainer> awpl = modelElement.getConnectedContainer(Anwendungsprogramm.class, mainDoc.getCollection().getMainGraphDocument());
							if (awpl.size() > 0)
								awp = awpl.get(0);
							if (awp == null) {
								boolean old_mode = gdcoll.isInteractiveMode();
								gdcoll.setInteractiveMode(false);
								GraphDocument.createAddicted(mainDoc.getCollection().getSelectedDoc(), modelElement, RawbAwpVerbindung.class, Anwendungsprogramm.class, modelElement.getClearName() + "_" + swp.getName(), dialog.getTransactionID());
								gdcoll.setInteractiveMode(old_mode);
								awp = mainDoc.getLastCreated();
							}
							gdcoll.link(AwpSwpVerbindung.class, awp.getElement(), swp, dialog.getTransactionID());
						}
					} else if (Datenbanksystem.class.isAssignableFrom(searchElementClass)) {
						GraphDocument.createAddicted(mainDoc.getCollection().getSelectedDoc(), modelElement, RawbDbsVerbindung.class, Datenbanksystem.class, dialog.getTransactionID());
					} else if (Dokumentensammlung.class.isAssignableFrom(searchElementClass)) {
						GraphDocument.createAddicted(mainDoc.getCollection().getSelectedDoc(), modelElement, KawbDoksVerbindung.class, Dokumentensammlung.class, dialog.getTransactionID());
					} else {
						mainDoc.createKnotenWithContainer(searchElementClass, dialog.getTransactionID());
						NodeContainer nc = mainDoc.getLastCreated();
						if (nc != null)
							gdcoll.link(modelElement, nc.getElement(), dialog.getTransactionID());
					}
				} else if (selected instanceof NodeContainer) {
					if (Softwareprodukt.class.isAssignableFrom(searchElementClass)) {
						Softwareprodukt swp = (Softwareprodukt) ((NodeContainer) selected).getElement();

						NodeContainer awp = null;
						ArrayList<ElementContainer> awpl = modelElement.getConnectedContainer(Anwendungsprogramm.class, mainDoc);
						if (awpl.size() > 0)
							awp = (NodeContainer) awpl.get(0);

						if (awp == null) {
							boolean old_mode = gdcoll.isInteractiveMode();
							gdcoll.setInteractiveMode(false);
							// mainDoc.createAWPforABS(modelElement.
							// getHashString(), modelElement.getClearName()
							// + "_" + swp.getName(),
							// dialog.getTransactionID());
							GraphDocument.createAddicted(mainDoc.getCollection().getSelectedDoc(), modelElement, RawbAwpVerbindung.class, Anwendungsprogramm.class, modelElement.getClearName() + "_" + swp.getName(), dialog.getTransactionID());
							gdcoll.setInteractiveMode(old_mode);
							awp = (mainDoc.getLastCreated());
						}
						gdcoll.link(AwpSwpVerbindung.class, awp.getElement(), swp, dialog.getTransactionID());
					} else {
						NodeContainer knot = (NodeContainer) selected;
						gdcoll.link(modelElement, knot.getElement(), dialog.getTransactionID());
					}
				} 
					
				modelElement.getContainer(mainDoc).refreshText();
				mainDoc.finish_transaction(dialog.getTransactionID());
				mainDoc.distributeEvent(GraphDocument.DATA_CHANGED, dialog.getTransactionID());
				panel.showFullDialog(false);
			}
		};
	}


	/**
	 * Methode liefert eine <code>LGMAction</code> zurück, die auf
	 * Mouse-Aktionen in Panels reagiert.
	 * 
	 * @param edp
	 */
	private final LGMAction getMouseAction() {
		final AuswahlPanel panel = this;
		final GraphDocument doc = panel.getGraphDocument();
		final ElementPropertyDialog dialog = panel.getDialog();
		return new LGMAction() {
			@Override
			public void execute(EventObject eo) {
				MouseEvent e = (MouseEvent) eo;
				if (Tool3lgmConstants.isPopupTrigger(e)) {
					Object item = panel.getBox().getSelectedItem();
					if ((item != null) && (item instanceof NodeContainer)) {
						NodeContainer knot = (NodeContainer) item;
						doc.select(knot, dialog.getTransactionID());
						Tool3lgm.getContextGenerator().getTreeKnotContextMenu().show(e.getComponent(), e.getX() + 3, e.getY() + 3);
					}
				}
			}
		};
	}





}