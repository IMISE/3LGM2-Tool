package de.imise.tool3lgm.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.tool3lgm.graphtools.view.graph.InputGraphArea;

/**
 * Erzeugt InternalFrame für 3lgm mit bestimmter Größe und und Lage. Es werden freie Stellen gesucht und eingfügt.
 */
public class InternalGraphFrame extends AbstractInternalFrame implements ActionListener {

    /** Anzahl der jemals geöffneten Teilmodelle */
    static int docCount = 0;

    /**
     * COMMENTME
     */
    private final InputGraphArea area;

    /**
     * COMMENTME
     */
    private final JButton but;

    /**
     * @param pane
     * @param inputGraphArea
     * @param doc
     */
    public InternalGraphFrame(final JDesktopPane pane, final InputGraphArea inputGraphArea, final LGMGraphDocument doc) {
        super(doc, "");

        // Diese Reihenfolge ist wichtig! Im Konstruktor von Werkzeugleiste
        // braucht man das Area, um die Regler bei neuen Modellen auf den dann
        // dargestellten Wert zu setzen
        doc.setFrame(this);
        area = inputGraphArea;

        if (!(doc instanceof Szenario)) {
            docCount++;
        }

        updateTitle();

        setClosable(false);

        JScrollPane sp = getScrollPane();
        sp.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        //		sp.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE );
        //		sp.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        sp.setViewportView(inputGraphArea);
        sp.getHorizontalScrollBar().setUnitIncrement(10);
        sp.getVerticalScrollBar().setUnitIncrement(10);
        but = new JButton(Tool3lgmConstants.getIcon("zent.gif"));
        but.setActionCommand("z");
        getScrollPane().setCorner(JScrollPane.LOWER_RIGHT_CORNER, but);
        but.addActionListener(this);

    }

    /**
     * @return
     */
    public InputGraphArea getInputGraphArea() {
        return area;
    }

    /**
     * implementiert ActionListener zum selbständigen zentrieren der Frames
     *
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        String str = e.getActionCommand();
        if (str == "z") {
            center();
        }
    }

    /**
     *
     */
    public void center() {
        JViewport view = getScrollPane().getViewport();
        Rectangle vp = view.getViewRect();
        int x = (int) (area.getWidth() - vp.getWidth()) / 2;
        int y = (int) (area.getHeight() - vp.getHeight()) / 2;
        view.setViewPosition(new Point(x, y));
    }

    @Override
    public void dispose() {
        super.dispose();
        area.dispose();
    }

    /**
     * Setzt den Titel des Frames
     */
    public void updateTitle() {
        GDCollection gdcoll = getCollection();
        String gdcollName = gdcoll.getName();
        String docName = getGraphDocument().getTitle();
        MetaModelContext metaModelContext = gdcoll.getMetaModelContext();
        String metaModelDisplayName = metaModelContext.getMetaModelDisplayName();
        super.setTitle(gdcollName + " - " + docName + "   (" + metaModelDisplayName + ")");
    }

    @Override
    public void dataChanged(final GraphDocument source) {
        area.revalidateRepaint();
    }

    @Override
    public void elementGraphicsChanged(final GraphDocument source, final ElementContainer element) {
        area.revalidateRepaint(element);
    }

    @Override
    public void layoutChanged(final GraphDocument source) {
        area.layoutChanged();
    }

    @Override
    public void groupOrderChanged(final GraphDocument source) {
        area.revalidateRepaint();
    }

    @Override
    public void activeLayerChanged(final GraphDocument source) {
        area.revalidateRepaint();
    }

    @Override
    public void colorsChanged(final GraphDocument source) {
        area.repaint();
    }

    @Override
    public void selectionChanged(final GraphDocument source) {
        area.repaint();
    }

    @Override
    public void userFieldValueChanged(final ElementContainer ec) {
        area.repaint();
    }

    /**
     * @return
     */
    public LGMGraphDocument getSzenario() {
        return getGraphDocument();
    }

    @Override
    public void elementNameChanged(final ElementContainer ec) {
        refreshElementContainer(ec);
    }

    private void refreshElementContainer(final ElementContainer ec) {
        GraphDocument ecDoc = ec.getGraphDocument();
        GraphDocument doc = getGraphDocument();
        ElementContainer thisEc = ecDoc == doc ? ec : ec.getElement().getContainer(doc);
        ec.refreshText();
        elementGraphicsChanged(getGraphDocument(), thisEc);
        revalidate();
        repaint();
    }

}
