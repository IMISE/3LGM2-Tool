package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JLabel;

import de.imise.util.swing.component.AlphabeticalComboBox;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.ObjLogspVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.Datenbanksystem;
import de.imise.tool3lgm.graphtools.elements.node.Dokumentensammlung;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class MasterDBSPanel extends ElementDialogPanel implements ItemListener {

	AlphabeticalComboBox box;
	boolean initializing = false;
	JLabel westLabel;

	public MasterDBSPanel(ElementPropertyDialog p) {
		super(p);

		setLayout(new BorderLayout());
		box = new AlphabeticalComboBox();
		box.addItemListener(this);
		add(box, BorderLayout.CENTER);
		westLabel = new JLabel(Tool3lgmConstants.getResString("masterdbs"));
		// add(westLabel,BorderLayout.WEST);

		init();
	}

	public MasterDBSPanel(ElementPropertyDialog p, boolean addWestLabel) {
		this(p);
		if (!addWestLabel)
			remove(westLabel);
	}

	public JLabel getWestLabel() {
		return westLabel;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.ElementDialogPanel#init()
	 */
	@Override
	protected void init() {
		initializing = true;

		box.removeAllItems();
		box.addItem(" ");
		ArrayList<ElementContainer> connect = mainDoc.getElementContainer(Datenbanksystem.class);
		connect.addAll(mainDoc.getElementContainer(Dokumentensammlung.class));
		for (int m = 0; m < connect.size(); m++)
			box.addItem(connect.get(m));
		box.setSelectedItem(null);
		connect = getModelElement().getConnectedContainer(Datenbanksystem.class, mainDoc);
		connect.addAll(getModelElement().getConnectedContainer(Dokumentensammlung.class, mainDoc));
		if (connect.size() > 0)
			box.setSelectedItem(connect.get(0));

		initializing = false;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.dialog.panel.ElementDialogPanel#showFullDialog()
	 */
	@Override
	protected void showFullDialog() {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (initializing)
			return;

		Object str = e.getItem();
		if (str instanceof String)
			return;
		ElementContainer ec = (ElementContainer) str;
		ModelElement me = ec.getElement();
		if (e.getStateChange() == ItemEvent.SELECTED) {
			doc.start_transaction(dialog.getTransactionID());
			if (me.getClass() == Datenbanksystem.class || me.getClass() == Dokumentensammlung.class) {
				doc.getCollection().link(ObjLogspVerbindung.class, getModelElement(), me, dialog.getTransactionID());
			}
			doc.finish_transaction(dialog.getTransactionID());
			doc.distributeEvent(GraphDocument.DATA_CHANGED, dialog.getTransactionID());
		}
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			doc.start_transaction(dialog.getTransactionID());
			if (me.getClass() == Datenbanksystem.class || me.getClass() == Dokumentensammlung.class) {
				for (int n = 0; n < me.getEdgesCount(); n++) {
					Kante trace = me.getEdge(n);
					if (getModelElement().hasConnection(trace)) {
						doc.getCollection().deleteElement(trace, doc, dialog.getTransactionID());
						ec.setHighLight(false);
					}
				}
			}
			doc.finish_transaction(dialog.getTransactionID());
		}
	}
}
