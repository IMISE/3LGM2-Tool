package de.imise.tool3lgm.graphtools.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.dialog.tools.EasyDialogAccess;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.GraphViewConstants;
import de.imise.tool3lgm.graphtools.view.graph.Mapping;
import de.imise.tool3lgm.log.Log;

/**
 * Der LayoutEdior für die Konfiguration von Form, Farbe und Schriftwart der Elemetklassen
 * zuständig.
 */
public class LayoutEditor extends JDialog implements ActionListener {

    private final JScrollPane jsp;
    private final JPanel flaeche;
    private final NodeContainer[] knoten;
    private final JButton[] form_trigger, farbe_trigger, font_trigger;
    private final JPopupMenu farbe_menu, form_menu, font_menu;
    private JMenuItem[] farbe;
    private final JMenuItem[] form;
    private final JMenuItem[] name;
    private final Mapping my_mapping;
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
    @SuppressWarnings("unchecked")
    public LayoutEditor(final JFrame f, final GraphDocument document) {
        super(f);
        setLocationByPlatform(true);
        doc = document;
        mydoc = new LGMGraphDocument(null);
        my_mapping = mydoc.getMapping();
        my_mapping.adapt(doc.getMapping());
        setTitle(Tool3lgmConstants.getResString("layout_edit"));

        wieviele = ModelConstants.ALL_NODES_SET.size() - GraphViewConstants.getUnpaintableCount() + 4; // Sicherheit
                                                                                                       // geht
                                                                                                       // vor!

        insets = new Insets(0, 0, 0, 0);
        knoten = new NodeContainer[wieviele];
        form_trigger = new JButton[wieviele];
        farbe_trigger = new JButton[wieviele];
        font_trigger = new JButton[wieviele];

        beenden = new JButton(Tool3lgmConstants.getResString("ok"));
        beenden.addActionListener(this);
        beenden.setMargin(insets);
        beenden.setPreferredSize(new Dimension(100, 30));

        abbrechen = new JButton(Tool3lgmConstants.getResString("cancel"));
        abbrechen.addActionListener(this);
        abbrechen.setMargin(insets);
        abbrechen.setPreferredSize(new Dimension(100, 30));

        uebernehmen = new JButton(Tool3lgmConstants.getResString("apply"));
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

        farbe_menu = new JPopupMenu(Tool3lgmConstants.getResString("le_farbe"));
        form_menu = new JPopupMenu(Tool3lgmConstants.getResString("le_form"));
        font_menu = new JPopupMenu(Tool3lgmConstants.getResString("le_schriftart"));

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
            form[c] = new JMenuItem(Tool3lgmConstants.getResString(shapes[c].toString()));
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
        for (c = 0; c < ModelConstants.ALL_DOMAIN_LAYER_NODES.length; c++) {
            if (ModelConstants.isAbstract(ModelConstants.ALL_DOMAIN_LAYER_NODES[c])) {
                continue;
            }
            if (GraphViewConstants.isUnpaintable(ModelConstants.ALL_DOMAIN_LAYER_NODES[c])) {
                continue;
            }
            NodeContainer kc = new NodeContainer((Knoten) ModelConstants.createElement(ModelConstants.ALL_DOMAIN_LAYER_NODES[c], true), mydoc);
            knoten[counter + offset] = kc;
            kc.getKnoten().setName(ModelConstants.getDisplayableName(ModelConstants.ALL_DOMAIN_LAYER_NODES[c]));
            kc.setCoordinates(akt_x + 90, akt_y + 50, 100, 60);
            kc.setFont(mydoc.getMapping().getStandardFont(kc.getElement()));

            form_trigger[counter + offset] = new JButton(Tool3lgmConstants.getResString("le_form"));
            form_trigger[counter + offset].setMargin(insets);
            form_trigger[counter + offset].setSize(50, 20);
            form_trigger[counter + offset].addActionListener(this);
            form_trigger[counter + offset].setLocation(akt_x + 40, akt_y + 95);
            flaeche.add(form_trigger[counter + offset]);

            farbe_trigger[counter + offset] = new JButton(Tool3lgmConstants.getResString("le_farbe"));
            farbe_trigger[counter + offset].setMargin(insets);
            farbe_trigger[counter + offset].setSize(50, 20);
            farbe_trigger[counter + offset].addActionListener(this);
            farbe_trigger[counter + offset].setLocation(akt_x + 90, akt_y + 95);
            flaeche.add(farbe_trigger[counter + offset]);

            font_trigger[counter + offset] = new JButton(Tool3lgmConstants.getResString("le_schrift"));
            font_trigger[counter + offset].setMargin(insets);
            font_trigger[counter + offset].setSize(50, 20);
            font_trigger[counter + offset].addActionListener(this);
            font_trigger[counter + offset].setLocation(akt_x + 140, akt_y + 95);
            flaeche.add(font_trigger[counter + offset]);

            akt_x += x_abstand;
            if (counter % 8 == 7) {
                akt_y += y_abstand;
            }
            counter++;
        }

        offset += counter;
        counter = 0;
        akt_x = 0;
        akt_y += y_abstand;
        for (c = 0; c < ModelConstants.ALL_LOGICAL_LAYER_NODES.length; c++) {
            if (ModelConstants.isAbstract(ModelConstants.ALL_LOGICAL_LAYER_NODES[c])) {
                continue;
            }
            if (GraphViewConstants.isUnpaintable(ModelConstants.ALL_LOGICAL_LAYER_NODES[c])) {
                continue;
            }
            // nur für Knoten kann man das Layout im Moment festlegen -> Kanten
            // auslassen
            if (!Knoten.class.isAssignableFrom(ModelConstants.ALL_LOGICAL_LAYER_NODES[c])) {
                continue;
            }
            NodeContainer kc = new NodeContainer((Knoten) ModelConstants.createElement(ModelConstants.ALL_LOGICAL_LAYER_NODES[c], true), mydoc);
            knoten[counter + offset] = kc;
            kc.setCoordinates(akt_x + 90, akt_y + 50, 100, 60);
            kc.getKnoten().setName(ModelConstants.getDisplayableName(ModelConstants.ALL_LOGICAL_LAYER_NODES[c]));
            kc.setFont(mydoc.getMapping().getStandardFont(kc.getElement()));

            form_trigger[counter + offset] = new JButton(Tool3lgmConstants.getResString("le_form"));
            form_trigger[counter + offset].setMargin(insets);
            form_trigger[counter + offset].setSize(50, 20);
            form_trigger[counter + offset].addActionListener(this);
            form_trigger[counter + offset].setLocation(akt_x + 40, akt_y + 95);
            flaeche.add(form_trigger[counter + offset]);

            farbe_trigger[counter + offset] = new JButton(Tool3lgmConstants.getResString("le_farbe"));
            farbe_trigger[counter + offset].setMargin(insets);
            farbe_trigger[counter + offset].setSize(50, 20);
            farbe_trigger[counter + offset].addActionListener(this);
            farbe_trigger[counter + offset].setLocation(akt_x + 90, akt_y + 95);
            flaeche.add(farbe_trigger[counter + offset]);

            font_trigger[counter + offset] = new JButton(Tool3lgmConstants.getResString("le_schrift"));
            font_trigger[counter + offset].setMargin(insets);
            font_trigger[counter + offset].setSize(50, 20);
            font_trigger[counter + offset].addActionListener(this);
            font_trigger[counter + offset].setLocation(akt_x + 140, akt_y + 95);
            flaeche.add(font_trigger[counter + offset]);

            akt_x += x_abstand;
            if (counter % 8 == 7) {
                akt_y += y_abstand;
            }
            counter++;
        }

        if (counter % 8 != 0) {
            akt_y += y_abstand;
        }
        offset += counter;
        counter = 0;
        akt_x = 0;
        for (c = 0; c < ModelConstants.ALL_PHYSICAL_LAYER_NODES.length; c++) {
            if (ModelConstants.isAbstract(ModelConstants.ALL_PHYSICAL_LAYER_NODES[c])) {
                continue;
            }
            if (GraphViewConstants.isUnpaintable(ModelConstants.ALL_PHYSICAL_LAYER_NODES[c])) {
                continue;
            }
            NodeContainer kc = new NodeContainer((Knoten) ModelConstants.createElement(ModelConstants.ALL_PHYSICAL_LAYER_NODES[c], true), mydoc);
            knoten[counter + offset] = kc;
            kc.setCoordinates(akt_x + 90, akt_y + 50, 100, 60);
            kc.getKnoten().setName(ModelConstants.getDisplayableName(ModelConstants.ALL_PHYSICAL_LAYER_NODES[c]));
            kc.setFont(mydoc.getMapping().getStandardFont(kc.getElement()));

            form_trigger[counter + offset] = new JButton(Tool3lgmConstants.getResString("le_form"));
            form_trigger[counter + offset].setMargin(insets);
            form_trigger[counter + offset].setSize(50, 20);
            form_trigger[counter + offset].addActionListener(this);
            form_trigger[counter + offset].setLocation(akt_x + 40, akt_y + 95);
            flaeche.add(form_trigger[counter + offset]);

            farbe_trigger[counter + offset] = new JButton(Tool3lgmConstants.getResString("le_farbe"));
            farbe_trigger[counter + offset].setMargin(insets);
            farbe_trigger[counter + offset].setSize(50, 20);
            farbe_trigger[counter + offset].addActionListener(this);
            farbe_trigger[counter + offset].setLocation(akt_x + 90, akt_y + 95);
            flaeche.add(farbe_trigger[counter + offset]);

            font_trigger[counter + offset] = new JButton(Tool3lgmConstants.getResString("le_schrift"));
            font_trigger[counter + offset].setMargin(insets);
            font_trigger[counter + offset].setSize(50, 20);
            font_trigger[counter + offset].addActionListener(this);
            font_trigger[counter + offset].setLocation(akt_x + 140, akt_y + 95);
            flaeche.add(font_trigger[counter + offset]);

            akt_x += x_abstand;
            if (counter % 8 == 7) {
                akt_y += y_abstand;
            }
            counter++;
        }
        offset += counter;

        flaeche.setPreferredSize(new Dimension(8 * x_abstand + 30, akt_y + 90));

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
            doc.distributeEvent(GraphDocument.ELEMENT_GRAPHICS_CHANGED);
            return;
        }
        if (e.getSource() == beenden) {
            doc.adaptMapping(my_mapping);
            doc.distributeEvent(GraphDocument.ELEMENT_GRAPHICS_CHANGED);
            dispose();
            // setVisible(false);
            return;
        }

        for (c = 0; c < offset; c++) {
            if (e.getSource() == farbe_trigger[c]) {
                setAktuelles(c);

                Color oldColor = my_mapping.getStandardBackGroundColor(knoten[aktuelles].getKnoten());
                Color newColor = JColorChooser.showDialog(new JFrame(), Tool3lgmConstants.getResString("farbe_ausw"), oldColor);

                if (newColor == null) {
                    return;
                }
                my_mapping.setStandardBackGroundColor(knoten[aktuelles].getKnoten().getClass(), newColor);
                flaeche.repaint();
            }
            if (e.getSource() == form_trigger[c]) {
                setAktuelles(c);
                form_menu.show(flaeche, knoten[c].getX() - 50, knoten[c].getY() + 45);
            }
            if (e.getSource() == font_trigger[c]) {
                setAktuelles(c);
                Font font = EasyDialogAccess.getFontByChooser(this, knoten[aktuelles].getFont());
                if (font != null) {
                    my_mapping.setStandardFont(knoten[aktuelles].getKnoten().getClass(), font);
                }
                flaeche.repaint();
            }
        }

        if (e.getActionCommand().startsWith("form ")) {
            try {
                my_mapping.setStandardForm(knoten[aktuelles].getKnoten().getClass(), GraphElementLayout.SHAPE.valueOf(e.getActionCommand().substring("form ".length())));
                flaeche.repaint();
            } catch (Exception ne) {
                Log.show(Log.ERROR, Tool3lgmConstants.getErrString("Fehler beim Form setzen."), ne);
            }
        }
    }

    /**
     * @param g
     */
    public void mypaint(final Graphics2D g) {
        for (int c = 0; c < wieviele; c++) {
            if (knoten[c] == null) {
                continue;
            }
            knoten[c].setFont(my_mapping.getStandardFont(knoten[c].getElement()));
            knoten[c].refreshText();
            knoten[c].paint(g);
        }
    }

}
