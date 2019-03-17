package de.imise.tool3lgm.metamodel.tlgm_service.dialog.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.panel.AbstractPathConnectionPanel;
import de.imise.tool3lgm.graphtools.path.MetaPathFunctions;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;
import de.imise.tool3lgm.graphtools.path.meta.UnionMetaPath;
import de.imise.tool3lgm.graphtools.path.pathmodel.PathResultTreeModel;

/**
 * @author AXS (11 Mar 2019)
 */
public class ConnectedElementsTablePanel extends AbstractPathConnectionPanel {

    /** Die MetaPfade zu anderen Elementen in einem UnionMetaPath */
    protected UnionMetaPath metaPaths;

    /** Die eigentliche Tabelle */
    private ConnectedElementsTable table;

    /** Definition der Spalten der Tabelle */
    protected final ConnectedElementsTableColumnsDefinition columnsDefinition;

    /** Panel für Buttons Hinzufügen + Entfernen */
    private JPanel buttonpanel;

    /**
     * @param dialog
     * @param columnsDefinition
     * @param simpleMetaPaths
     */
    public ConnectedElementsTablePanel(final ElementPropertyDialog dialog, final boolean editable, final ConnectedElementsTableColumnsDefinition columnsDefinition, final SimpleMetaPath... simpleMetaPaths) {
        super(dialog, simpleMetaPaths[0]); // den muss es geben!
        metaPaths = new UnionMetaPath(simpleMetaPaths);
        this.columnsDefinition = columnsDefinition;
        internalInit(editable);
    }

    private void internalInit(final boolean editable) {
        table = new ConnectedElementsTable((SimpleMetaPath) metaPath, columnsDefinition, editable, dialog.getTransactionID());
        JScrollPane scrollPane = new JScrollPane(table);
        ConnectedElementsTableMouseListener.addTo(table, table);

        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        JButton addButton = new JButton(getCreateNewElementAction());
        JButton removeButton = new JButton(getDisconnectAction());

        if (editable) {
            constraints.anchor = GridBagConstraints.CENTER;
            buttonpanel = new JPanel();
            buttonpanel.setLayout(new GridLayout(1, 2));
            buttonpanel.add(removeButton);
            buttonpanel.add(addButton);
            add(this, buttonpanel, constraints, 0, 2, 3, 1);
        }

        // add(this, viewButton, constraints, 2, 3, 1, 1);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.anchor = GridBagConstraints.WEST;
        //        add(this, null, constraints, 0, 0, 1, 1);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 100;
        constraints.weighty = 100;
        add(this, scrollPane, constraints, 0, 1, 3, 1);

        update();
    }

    /**
    *
    */
    public final LGMAction getCreateNewElementAction() {
        return new LGMAction(getResString("addButtonText")) {
            @Override
            public void execute(final EventObject eo) {
                //aus MultipleCompositionPanel
                //               int pid = getTransactionID();
                //               GraphDocument selectedDoc = doc.getCollection().getSelectedDoc();
                //               ModelElement me = getModelElement();
                //               ElementContainer ec = me.getContainer(mainDoc);
                //               doc.select(ec, pid);
                //               GraphDocument.createAddicted(selectedDoc, me, getLastEdgeClassInPath().asSubclass(CompositionEdge.class), searchElementClass, pid);
                //               doc.select(ec, pid);
            }
        };
    }

    /**
     * Methode liefert eine <code>LGMAction</code> zurück, die das Verschieben von Elementen aus dem
     * <code>srcTree</code> in den <code>targetTree</code> realisiert. Diese <code>LGMAction</code>
     * sollte an die "removeButtons" der Panels angefügt werden.
     */
    public final LGMAction getDisconnectAction() {
        LGMAction returnAction = new LGMAction("", Tool3lgmConstants.getIcon("arrow_right2.gif")) {
            @Override
            public void execute(final EventObject e) {
                //aus MultipleCompositionPanel
                //               TreePath[] selpaths = tree.getSelectionPaths();
                //               if (selpaths != null) {
                //                   for (int n = 0; n < selpaths.length; n++) {
                //                       // if(lomodel.getChildCount(loroot)>0) return;
                //                       LGMTreeNode node = (LGMTreeNode) selpaths[n].getLastPathComponent();
                //                       ElementContainer knot = (ElementContainer) node.getUserObject();
                //
                //                       ModelElement topLevelModelElement;
                //                       topLevelModelElement = getModelElement();
                //                       GDCollection gdcoll = getGraphDocument().getCollection();
                //                       gdcoll.unlink(topLevelModelElement, knot.getElement(), getLastEdgeClassInPath(), getTransactionID());
                //                   }
                //               }
            }
        };
        returnAction.putValue("Name", getResString("delete"));
        returnAction.putValue("SmallIcon", null);

        return returnAction;
    }

    @Override
    protected Object getSelection(final MouseEvent e) {
        return null;
    }

    @Override
    public void update() {
        PathResultTreeModel resultTree = MetaPathFunctions.getResultTree(getModelElement(), metaPaths);
        table.setData(resultTree);
    }

}
