package de.imise.tool3lgm.graphtools.view.tree;

import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMChangeListener;
import de.imise.tool3lgm.graphtools.userfield.definition.UserFieldTarget;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;

public class ModelBrowserTreeLGMChangeListener implements LGMChangeListener {

    private final ModelBrowserTree tree;

    private boolean active = true;

    public ModelBrowserTreeLGMChangeListener(final ModelBrowserTree tree) {
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
    public void activeLayerChanged(final GraphDocument source) {
        if (active) {
            GraphDocument doc = tree.getGraphDocument();
            if (doc == source) {
                int layer = doc.getCollection().getActiveLayer();
                tree.selectLayerNode(layer);
            }
        }
    }

    @Override
    public void selectedSzenarioChanged(final GraphDocument source) {
        if (active) {
            tree.updateSelectedDoc();
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
    public void modelOrSzenarioNameChanged(final GraphDocument source) {
        if (active) {
            //      System.out.println("modelOrSzenarioRenamed");
            tree.refreshTree();
            //      repaint();
        }
    }

    @Override
    public void modelDescriptionChanged(final GraphDocument source) {
        if (active) {
            //      System.out.println("modelDescriptionChanged");
        }
    }

    @Override
    public void szenarioAdded(final GraphDocument source) {
        if (active) {
            //      System.out.println("szenarioAdded");
        }
    }

    @Override
    public void szenarioRemoved(final GraphDocument source) {
        if (active) {
            //      System.out.println("szenarioRemoved");
        }
    }

    public void remove() {
        GraphDocument doc = tree.getGraphDocument();
        GDCollection gdcoll = doc.getCollection();
        gdcoll.removeAllTransactionsListener(this);
    }

    public void add() {
        remove(); //zur Sicherheit erstmal removen
        GraphDocument doc = tree.getGraphDocument();
        GDCollection gdcoll = doc.getCollection();
        gdcoll.addAllTransactionsListener(this);
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return getClass().getName() + " " + tree.getGraphDocument() + " " + hashCode();
    }

}
