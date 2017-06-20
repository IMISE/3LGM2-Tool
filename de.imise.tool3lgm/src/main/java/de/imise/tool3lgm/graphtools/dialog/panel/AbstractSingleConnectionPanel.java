package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Component;
import java.awt.event.MouseListener;

import javax.swing.JComboBox;

import org.apache.commons.lang3.ArrayUtils;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMActionLibrary;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.elements.Kante;

/**
 * Abtsractes Panel für Verbindungen mit einer Maximalen Verbindungsanzahl von 1.
 *
 * @author astruebi
 * @created 05/2017
 */
public abstract class AbstractSingleConnectionPanel extends AbstractPathConnectionPanel {

    private final MouseListener mouseListener = createMouseListener();

    public AbstractSingleConnectionPanel(final ElementPropertyDialog dialog, final Class<? extends Kante>... edgeClasses) {
        this(dialog, false, edgeClasses);
    }

    public AbstractSingleConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final Class<? extends Kante>... edgeClasses) {
        super(dialog, labelLastEdgeName, edgeClasses);
        addMouseActions(westLabel);
    }

    private MouseListener createMouseListener() {
        LGMAction mouseAction = LGMActionLibrary.getMouseAction(this);
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
                if (!ArrayUtils.contains(mouseListeners, mouseListener)) {
                    c[i].addMouseListener(mouseListener);
                }
            }
        } else {
            component.addMouseListener(mouseListener);
        }
    }

    /**
     * Liefert das selektiert Object. Wenn hier ein ModelElement oder ein ElementContainer zurück
     * kommt, dann wird damit die mouseAction ausgeführt.
     *
     * @return
     */
    public abstract Object getSelection();

}
