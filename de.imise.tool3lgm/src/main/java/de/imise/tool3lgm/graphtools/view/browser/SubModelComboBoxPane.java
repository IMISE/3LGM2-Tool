/**
 * 
 */
package de.imise.tool3lgm.graphtools.view.browser;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JScrollPane;

import de.imise.util.swing.component.AlphabeticalComboBox;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;

/**
 * @author Ich
 *
 */
public class SubModelComboBoxPane extends SubModelsBrowser implements ItemListener {
	
	/**
	 * Combobox, in der das aktuelle Teilmodell ausgewählt werden kann
	 */
	private AlphabeticalComboBox submodelBox;
	
	/**
	 * @param gdcoll
	 */
	public SubModelComboBoxPane(GDCollection gdcoll) {
	    super(gdcoll);
	    JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.addMouseListener(this);
		scrollPane.getHorizontalScrollBar().addMouseListener(this);
		scrollPane.getVerticalScrollBar().addMouseListener(this);
		submodelBox = new AlphabeticalComboBox();
		submodelBox.addItemListener(this);
		submodelBox.addMouseListener(this);
	    add(submodelBox, BorderLayout.NORTH);
	    add(scrollPane, BorderLayout.CENTER);
    }

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#addGraphDocument(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void addGraphDocument(GraphDocument doc) {
		submodelBox.addItem(doc);
		//das erste doc ist immer das Gesamtmodell -> erstes Doc in eigene Liste packen (also einen
		//Separator nach dem ersten einfügen), damit es immer oben steht (egal wie es heißt) und nur
		//die Elemente darunter (alle Szenarios) sortiert werden
		if (submodelBox.getItemCount()==1)
			submodelBox.addSeparator(false);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#getCollection()
	 */
	@Override
	public GDCollection getCollection() {
		return gdcoll;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#getDocCount()
	 */
	@Override
	public int getDocCount() {
		int count = 0;
		for (int i=0; i<submodelBox.getItemCount(); i++) {
			if (submodelBox.getObjectAt(i) instanceof GraphDocument)
				count++;
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#getSelectedDoc()
	 */
	@Override
	public GraphDocument getSelectedDoc() {
		Object o = submodelBox.getSelectedObject();
		if (o instanceof GraphDocument)
			return (GraphDocument)o;
		return null;
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#removeGraphDocument(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void removeGraphDocument(GraphDocument doc) {
		submodelBox.removeItem(doc);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#setSelectedDoc(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void setSelectedDoc(GraphDocument doc) {
		submodelBox.setSelectedObject(doc);
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#updateTitle(tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public void updateTitle(GraphDocument doc) {
		submodelBox.resort();
	}

	/* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
    	update();
    }

    /* (non-Javadoc)
     * @see tool3lgm.graphtools.view.browser.SubModelsBrowser#update()
     */
    @Override
    public void update() {
    	GraphDocument mySelDoc = getSelectedDoc();
    	//das muss extra abgefragt werden und darf nicht einfach mit der unteren if-Abfrage zusammen erledigt werden,
    	//beim Starten des Baukastens der Baum sonst gar kein Doc hat, solange man nicht wenigstens 1 Mal das Teilmodell
    	//wechselt
    	if (tree.getGraphDocument() != mySelDoc)
			tree.setGraphDocument(mySelDoc);
    	if (mySelDoc != Tool3lgm.tool.getSelectedDoc())
    		Tool3lgm.tool.setSelectedDoc(mySelDoc, true);
    }
    

}
