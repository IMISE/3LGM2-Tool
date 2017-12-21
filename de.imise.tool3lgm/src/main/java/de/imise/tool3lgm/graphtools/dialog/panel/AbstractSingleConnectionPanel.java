package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Component;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.util.collections.CollectionUtils;

/**
 * Abtsractes Panel für Verbindungen mit einer Maximalen Verbindungsanzahl von 1.
 *
 * @author astruebi
 * @created 05/2017
 */
public abstract class AbstractSingleConnectionPanel extends AbstractPathConnectionPanel {

    private final MouseListener mouseListener = createMouseListener();

    public AbstractSingleConnectionPanel(final ElementPropertyDialog dialog, final Class<? extends Edge>... edgeClasses) {
        this(dialog, false, edgeClasses);
    }

    public AbstractSingleConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final Class<? extends Edge>... edgeClasses) {
        super(dialog, labelLastEdgeName, edgeClasses);
        addMouseActions(westLabel);
    }

    private MouseListener createMouseListener() {
        LGMAction mouseAction = getMouseClickedAction();
        MouseListener mouseListener = new LGMMouseListener(null, null, null, mouseAction, null);
        return mouseListener;
    }

    /**
     * Fügt der übergebenen Komponente die Doppelklick-Öffne-Eigenschaftsdialog-des-selektierten-Elementes-Action
     * hinzu und die Rechte-Maustastae-Öffnet-KontextMenü-Action.
     *
     * @param component
     */
    protected void addMouseActions(final Component component) {
        if (component instanceof JComboBox<?>) {
            JComboBox<?> box = (JComboBox<?>) component;
            //box.getEditor().getEditorComponent().addMouseListener(mouseListener); // funktioniert nicht!!!
            Component c[] = box.getComponents();
            for (int i = 0; i < c.length; i++) {
                // add event listener to all of the child components
                MouseListener[] mouseListeners = c[i].getMouseListeners();
                if (!CollectionUtils.arrayContains(mouseListeners, mouseListener)) {
                    c[i].addMouseListener(mouseListener);
                }
            }
        } else {
            component.addMouseListener(mouseListener);
        }
    }

}
