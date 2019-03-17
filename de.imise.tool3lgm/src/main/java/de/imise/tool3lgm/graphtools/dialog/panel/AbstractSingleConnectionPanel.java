package de.imise.tool3lgm.graphtools.dialog.panel;

import java.awt.Component;
import java.awt.event.MouseListener;

import de.imise.tool3lgm.graphtools.dialog.ElementPropertyDialog;
import de.imise.tool3lgm.graphtools.dialog.action.LGMAction;
import de.imise.tool3lgm.graphtools.dialog.action.LGMMouseListener;
import de.imise.tool3lgm.graphtools.path.meta.SimpleMetaPath;

/**
 * Abtsractes Panel für Verbindungen mit einer Maximalen Verbindungsanzahl von 1.
 *
 * @author astruebi
 * @created 05/2017
 */
public abstract class AbstractSingleConnectionPanel extends AbstractPathConnectionPanel {

    private final MouseListener mouseListener = createMouseListener();

    public AbstractSingleConnectionPanel(final ElementPropertyDialog dialog, final SimpleMetaPath simpleMetaPath) {
        this(dialog, false, simpleMetaPath);
    }

    public AbstractSingleConnectionPanel(final ElementPropertyDialog dialog, final boolean labelLastEdgeName, final SimpleMetaPath simpleMetaPath) {
        super(dialog, labelLastEdgeName, simpleMetaPath);
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
        // Das unten auskommentierte hatte ich (AXS) mal gebaut, damit auf Comboboxen auch das Kontextmenü funktioniert. Das klappt aber auf dem MAC gar nicht
        // und es reicht der untere Aufruf völlig -> Testen ob das auch auf Windows so geht und wenn ja, dann das auskommentierte Löschen. Das muss aber mal
        // nötig gewesen sein, sonst hätte ich das nicht geschrieben. Evtl. auch Änderung durch neue Java-Version!?
        //        if (component instanceof JComboBox<?>) {
        //            JComboBox<?> box = (JComboBox<?>) component;
        //            //box.getEditor().getEditorComponent().addMouseListener(mouseListener); // funktioniert nicht!!!
        //            Component c[] = box.getComponents();
        //            for (int i = 0; i < c.length; i++) {
        //                // add event listener to all of the child components
        //                MouseListener[] mouseListeners = c[i].getMouseListeners();
        //                if (!CollectionUtils.arrayContains(mouseListeners, mouseListener)) {
        //                    c[i].addMouseListener(mouseListener);
        //                }
        //            }
        //        } else {
        component.addMouseListener(mouseListener);
        //        }
    }

}
