package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.GDCollectionChangeListener.GDCollectionChangeType.ELEMENT_GRAPHICS_CHANGED;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.graphtools.ElementsNameBuilder;
import de.imise.tool3lgm.graphtools.dialog.tools.EasyDialogAccess;
import de.imise.tool3lgm.graphtools.metamodel.GraphViewDefinition;
import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.ElementsLayoutDefinition;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.log.Log;

/**
 * Der LayoutEdior für die Konfiguration von Form, Farbe und Schriftwart der Elemetklassen
 * zuständig.
 */
public class LayoutEditor extends JDialog implements ActionListener {

    private final JScrollPane jsp;
    private final JPanel flaeche;
    private final NodeContainer[] nodeContainers;
    private final JButton[] form_trigger, farbe_trigger, font_trigger;
    private final JPopupMenu farbe_menu, form_menu, font_menu;
    private JMenuItem[] farbe;
    private final JMenuItem[] form;
    private final JMenuItem[] name;
    private final ElementsLayoutDefinition my_mapping;
    private final Insets insets;
    private int wieviele = 0, offset, c, counter, akt_x, akt_y, aktuelles = -1;
    private final JButton uebernehmen, abbrechen, beenden;
    private final GraphDocument doc, mydoc;
    private final int x_abstand = 160, y_abstand = 140;

    /**
     * Initialisiert den Grafikdialog.
     *
     * @param f
     * @param document
     */
    public LayoutEditor(final JFrame f, final GraphDocument doc) {
        super(f);
        setLocationByPlatform(true);
        this.doc = doc;
        GDCollection gdcoll = doc.getCollection();
        gdcoll = new GDCollection(gdcoll.getMetaModelContext());
        mydoc = new LGMGraphDocument(gdcoll);
        my_mapping = mydoc.getMapping();
        my_mapping.adapt(doc.getMapping());
        setTitle(getResString("layout_edit"));

        MetaModel metaModel = gdcoll.getMetaModel();
        GraphViewDefinition graphViewDefinition = metaModel.getGraphViewDefinition();

        wieviele = graphViewDefinition.getMetaModelSpecificPaintableNodes().size();

        insets = new Insets(0, 0, 0, 0);
        nodeContainers = new NodeContainer[wieviele];
        form_trigger = new JButton[wieviele];
        farbe_trigger = new JButton[wieviele];
        font_trigger = new JButton[wieviele];

        beenden = new JButton(getResString("ok"));
        beenden.addActionListener(this);
        beenden.setMargin(insets);
        beenden.setPreferredSize(new Dimension(100, 30));

        abbrechen = new JButton(getResString("cancel"));
        abbrechen.addActionListener(this);
        abbrechen.setMargin(insets);
        abbrechen.setPreferredSize(new Dimension(100, 30));

        uebernehmen = new JButton(getResString("apply"));
        uebernehmen.addActionListener(this);
        uebernehmen.setMargin(insets);
        uebernehmen.setPreferredSize(new Dimension(100, 30));

        flaeche = new JPanel() {
            @Override
            public void paintComponent(final Graphics g) {
                super.paintComponent(g);
                mypaint((Graphics2D) g);
            }
        };

        farbe_menu = new JPopupMenu(getResString("le_farbe"));
        form_menu = new JPopupMenu(getResString("le_form"));
        font_menu = new JPopupMenu(getResString("le_schriftart"));

        farbe = new JMenuItem[GraphElementLayout.COLORS.length];

        farbe = new JMenuItem[GraphElementLayout.COLORS.length];
        for (c = 0; c < GraphElementLayout.COLORS.length; c++) {
            farbe[c] = new JMenuItem(GraphElementLayout.COLOR_NAMES[c]);
            farbe[c].setActionCommand("farbe " + c);
            farbe[c].setBackground(GraphElementLayout.COLORS[c]);
            farbe[c].addActionListener(this);
            farbe_menu.add(farbe[c]);
        }

        GraphElementLayout.SHAPE[] shapes = GraphElementLayout.SHAPE.values();
        form = new JMenuItem[shapes.length];
        for (c = 0; c < shapes.length; c++) {
            form[c] = new JMenuItem(getResString(shapes[c].toString()));
            form[c].setActionCommand("form " + shapes[c]);
            form[c].addActionListener(this);
            form_menu.add(form[c]);
        }

        name = new JMenuItem[GraphElementLayout.FONT_NAMES.length];
        for (c = 0; c < GraphElementLayout.FONT_NAMES.length; c++) {
            name[c] = new JMenuItem(GraphElementLayout.FONT_NAMES[c]);
            name[c].setActionCommand("font " + c);
            name[c].addActionListener(this);
            font_menu.add(name[c]);
        }

        flaeche.setLayout(null);

        offset = 0;
        counter = 0;
        int maxInRow = 0;
        List<Class<? extends ModelElement>> metaModelSpecificPaintableNodes = graphViewDefinition.getMetaModelSpecificPaintableNodes();
        for (int l = 0; l < ModelConstants.VISIBLE_LAYERS.length; l++) {
            int currentLayer = ModelConstants.VISIBLE_LAYERS[l];
            for (int c = 0; c < metaModelSpecificPaintableNodes.size(); c++) {
                Class<? extends ModelElement> paintbaleClass = metaModelSpecificPaintableNodes.get(c);
                if (MetaModel.isAbstract(paintbaleClass)) {
                    continue;
                }
                // nur für Node kann man das Layout im Moment festlegen -> Kanten auslassen
                if (!Node.class.isAssignableFrom(paintbaleClass)) {
                    continue;
                }
                if (metaModel.layerFor(paintbaleClass) != currentLayer) {
                    continue;
                }
                if (c > maxInRow) {
                    maxInRow = c;
                }
                int index = counter + offset;
                NodeContainer kc = new NodeContainer((Node) metaModel.createElement(paintbaleClass, true), mydoc);
                nodeContainers[index] = kc;
                ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
                kc.getNode().setName(elementsNameBuilder.getDisplayableName(paintbaleClass));
                kc.setCoordinates(akt_x + 90, akt_y + 50, 100, 60);
                kc.setFont(mydoc.getMapping().getStandardFont(kc));

                form_trigger[index] = new JButton(getResString("le_form"));
                form_trigger[index].setMargin(insets);
                form_trigger[index].setSize(50, 20);
                form_trigger[index].addActionListener(this);
                form_trigger[index].setLocation(akt_x + 40, akt_y + 95);
                flaeche.add(form_trigger[index]);

                farbe_trigger[index] = new JButton(getResString("le_farbe"));
                farbe_trigger[index].setMargin(insets);
                farbe_trigger[index].setSize(50, 20);
                farbe_trigger[index].addActionListener(this);
                farbe_trigger[index].setLocation(akt_x + 90, akt_y + 95);
                flaeche.add(farbe_trigger[index]);

                font_trigger[index] = new JButton(getResString("le_schrift"));
                font_trigger[index].setMargin(insets);
                font_trigger[index].setSize(50, 20);
                font_trigger[index].addActionListener(this);
                font_trigger[index].setLocation(akt_x + 140, akt_y + 95);
                flaeche.add(font_trigger[index]);

                akt_x += x_abstand;
                counter++;
            }

            offset += counter;
            counter = 0;
            akt_x = 0;
            akt_y += y_abstand;
        }
        akt_y -= y_abstand;

        flaeche.setPreferredSize(new Dimension(maxInRow * x_abstand + 30, akt_y + 90));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(350, 30));
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(beenden);
        buttonPanel.add(abbrechen);
        buttonPanel.add(uebernehmen);

        jsp = new JScrollPane(flaeche);
        jsp.setPreferredSize(new Dimension(790, akt_y + 90 + 50));

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(jsp);

        getContentPane().add(buttonPanel);
        pack();
        setModal(true);
        setVisible(true);
    }

    /**
     * @param z
     */
    private void setAktuelles(final int z) {
        aktuelles = z;
    }

    /**
     *
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == abbrechen) {
            my_mapping.adapt(doc.getMapping());
            dispose();
            return;
        }
        if (e.getSource() == uebernehmen) {
            doc.adaptMapping(my_mapping);
            doc.distributeEvent(ELEMENT_GRAPHICS_CHANGED);
            return;
        }
        if (e.getSource() == beenden) {
            doc.adaptMapping(my_mapping);
            doc.distributeEvent(ELEMENT_GRAPHICS_CHANGED);
            dispose();
            // setVisible(false);
            return;
        }

        for (c = 0; c < offset; c++) {
            if (e.getSource() == farbe_trigger[c]) {
                setAktuelles(c);

                Color oldColor = my_mapping.getStandardBackGroundColor(nodeContainers[aktuelles]);
                Color newColor = JColorChooser.showDialog(new JFrame(), getResString("farbe_ausw"), oldColor);

                if (newColor == null) {
                    return;
                }
                my_mapping.setStandardBackGroundColor(nodeContainers[aktuelles].getNode().getClass(), newColor);
                flaeche.repaint();
            }
            if (e.getSource() == form_trigger[c]) {
                setAktuelles(c);
                form_menu.show(flaeche, nodeContainers[c].getX() - 50, nodeContainers[c].getY() + 45);
            }
            if (e.getSource() == font_trigger[c]) {
                setAktuelles(c);
                Font font = EasyDialogAccess.getFontByChooser(this, nodeContainers[aktuelles].getFont());
                if (font != null) {
                    my_mapping.setStandardFont(nodeContainers[aktuelles].getNode().getClass(), font);
                }
                flaeche.repaint();
            }
        }

        if (e.getActionCommand().startsWith("form ")) {
            try {
                my_mapping.setStandardForm(nodeContainers[aktuelles].getNode().getClass(), GraphElementLayout.SHAPE.valueOf(e.getActionCommand().substring("form ".length())));
                flaeche.repaint();
            } catch (Exception ne) {
                Log.show(Log.ERROR, getResString("Fehler beim Form setzen."), ne);
            }
        }
    }

    /**
     * @param g
     */
    public void mypaint(final Graphics2D g) {
        for (int c = 0; c < wieviele; c++) {
            if (nodeContainers[c] == null) {
                continue;
            }
            nodeContainers[c].setFont(my_mapping.getStandardFont(nodeContainers[c]));
            nodeContainers[c].refreshText();
            nodeContainers[c].paint(g);
        }
    }

}
