package de.imise.tool3lgm.graphtools.view.tree;

import de.imise.tool3lgm.graphtools.model.LGMChangeListener;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class DynamicTreeGraphDocumentAndInTransactionListener implements LGMChangeListener {

    private final DynamicTree tree;

    private boolean active = true;

    public DynamicTreeGraphDocumentAndInTransactionListener(final DynamicTree tree) {
        this.tree = tree;
        add();
    }

    @Override
    public void dataChanged(final GraphDocument source) {
        if (active) {
            tree.buildTree();
        }
    }

    @Override
    public void elementGraphicsChanged(final ElementContainer element) {
        if (active) {
            //      System.out.println("elementGraphicsChanged");
            tree.refreshTree();
            //      repaint();
        }
    }

    @Override
    public void layoutChanged(final GraphDocument source) {
        if (active) {
            //      System.out.println("layoutChanged");
        }
    }

    @Override
    public void groupOrderChanged(final GraphDocument source) {
        if (active) {
            //      System.out.println("groupOrderChanged");
        }
    }

    @Override
    public void colorsChanged(final GraphDocument source) {
        if (active) {
            //      System.out.println("colorsChanged");
        }
    }

    @Override
    public void selectionChanged(final GraphDocument source) {
        if (active) {
            //      System.out.println("selectionChanged");
            //      long start = System.currentTimeMillis();
            tree.selectObjects();
            //      long end = System.currentTimeMillis();
            //      System.err.println("DynamicTree.selectionChanged()");
            //      System.err.println(end - start);
        }
    }

    @Override
    public void modelOrSzenarioRenamed(final GraphDocument source) {
        if (active) {
            //      System.out.println("modelOrSzenarioRenamed");
            tree.refreshTree();
            //      repaint();
        }
    }

    @Override
    public void activeLayerChanged(final GraphDocument source) {
        if (active) {
            GraphDocument doc = tree.getGraphDocument();
            int layer = doc.getCollection().getActiveLayer();
            tree.selectLayerNode(layer);
        }
    }

    @Override
    public void elementNameChanged(final ElementContainer ec) {
        tree.revalidate();
        tree.repaint();
    }

    @Override
    public void userFieldValueChanged(final UserFieldTarget userFieldTarget) {
        if (active) {
            tree.buildTree();
        }
    }

    public void remove() {
        GraphDocument doc = tree.getGraphDocument();
        doc.removeAllTransactionsListener(this);
    }

    public void add() {
        remove(); //zur Sicherheit erstmal removen
        GraphDocument doc = tree.getGraphDocument();
        doc.addAllTransactionsListener(this);
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

}
