package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.model.LGMChangeListener.LGMChangeType.ELEMENT_GRAPHICS_CHANGED;

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

import de.imise.tool3lgm.MetaModelContext;
import de.imise.tool3lgm.Tool3lgmModelType;
import de.imise.tool3lgm.Tool3lgmModelType.ModelCategory;
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
import de.imise.tool3lgm.graphtools.view.graph.DefaultElementsLayoutDefinition;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.LayoutColor;
import de.imise.tool3lgm.graphtools.view.graph.Shape;
import de.imise.tool3lgm.log.Log;

/**
 * Der LayoutEdior für die Konfiguration von Form, Farbe und Schriftwart der
 * Elemetklassen zuständig.
 */
public class LayoutEditor extends JDialog implements ActionListener {

    private final JScrollPane scrollPane;
    private final JPanel panel;
    private final NodeContainer[] nodeContainers;
    private final JButton[] buttonShape, buttonColor, buttonFont;
    private final JPopupMenu menuColor, menuShape, menuFont;
    private final JMenuItem[] menuItemColor;
    private final JMenuItem[] menuItemShape;
    private final JMenuItem[] menuItemFont;
    private final DefaultElementsLayoutDefinition myDefaultElementsLayout;
    private final Insets insets;
    private int paintableNodesCount = 0;
    int offset;
    int c;
    int counter;
    int currX;
    int currY;
    int selectedElementTypeIndex = -1;
    private final JButton buttonApply;
    private final JButton buttonOK;
    private final JButton buttonCancel;
    private final GraphDocument doc, mydoc;
    private final int offsetX = 160;
    private final int offsetY = 140;

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
        MetaModelContext metaModelContext = gdcoll.getMetaModelContext();
        Tool3lgmModelType modelType = new Tool3lgmModelType(metaModelContext, ModelCategory.REGULAR);
        gdcoll = new GDCollection(modelType);
        mydoc = new LGMGraphDocument(gdcoll);
        myDefaultElementsLayout = mydoc.getDefaultElementsLayout();
        myDefaultElementsLayout.adapt(doc.getDefaultElementsLayout());
        setTitle(getResString("layout_edit"));

        MetaModel metaModel = gdcoll.getMetaModel();
        GraphViewDefinition graphViewDefinition = metaModel.getGraphViewDefinition();

        paintableNodesCount = graphViewDefinition.getMetaModelSpecificPaintableNodes().size();

        insets = new Insets(0, 0, 0, 0);
        nodeContainers = new NodeContainer[paintableNodesCount];
        buttonShape = new JButton[paintableNodesCount];
        buttonColor = new JButton[paintableNodesCount];
        buttonFont = new JButton[paintableNodesCount];

        buttonOK = new JButton(getResString("ok"));
        buttonOK.addActionListener(this);
        buttonOK.setMargin(insets);
        buttonOK.setPreferredSize(new Dimension(100, 30));

        buttonCancel = new JButton(getResString("cancel"));
        buttonCancel.addActionListener(this);
        buttonCancel.setMargin(insets);
        buttonCancel.setPreferredSize(new Dimension(100, 30));

        buttonApply = new JButton(getResString("apply"));
        buttonApply.addActionListener(this);
        buttonApply.setMargin(insets);
        buttonApply.setPreferredSize(new Dimension(100, 30));

        panel = new JPanel() {
            @Override
            public void paintComponent(final Graphics g) {
                super.paintComponent(g);
                mypaint((Graphics2D) g);
            }
        };

        menuColor = new JPopupMenu(getResString("le_farbe"));
        menuShape = new JPopupMenu(getResString("le_form"));
        menuFont = new JPopupMenu(getResString("le_schriftart"));

        LayoutColor[] layoutColors = LayoutColor.values();
        menuItemColor = new JMenuItem[layoutColors.length];
        for (c = 0; c < layoutColors.length; c++) {
            LayoutColor layoutColor = layoutColors[c];
            String colorName = layoutColor.toString();
            menuItemColor[c] = new JMenuItem(colorName);
            menuItemColor[c].setActionCommand("farbe " + c);
            Color color = layoutColor.awtColor();
            menuItemColor[c].setBackground(color);
            menuItemColor[c].addActionListener(this);
            menuColor.add(menuItemColor[c]);
        }

        Shape[] shapes = Shape.values();
        menuItemShape = new JMenuItem[shapes.length];
        for (c = 0; c < shapes.length; c++) {
            menuItemShape[c] = new JMenuItem(getResString(shapes[c].toString()));
            menuItemShape[c].setActionCommand("form " + shapes[c]);
            menuItemShape[c].addActionListener(this);
            menuShape.add(menuItemShape[c]);
        }

        menuItemFont = new JMenuItem[GraphElementLayout.FONT_NAMES.length];
        for (c = 0; c < GraphElementLayout.FONT_NAMES.length; c++) {
            menuItemFont[c] = new JMenuItem(GraphElementLayout.FONT_NAMES[c]);
            menuItemFont[c].setActionCommand("font " + c);
            menuItemFont[c].addActionListener(this);
            menuFont.add(menuItemFont[c]);
        }

        panel.setLayout(null);

        offset = 0;
        counter = 0;
        int maxInRow = 0;
        List<Class<? extends ModelElement>> metaModelSpecificPaintableNodes = graphViewDefinition.getMetaModelSpecificPaintableNodes();
        for (int l = 0; l < ModelConstants.VISIBLE_LAYERS.length; l++) {
            int currentLayer = ModelConstants.VISIBLE_LAYERS[l];
            for (int c = 0; c < metaModelSpecificPaintableNodes.size(); c++) {
                Class<? extends ModelElement> paintableClass = metaModelSpecificPaintableNodes.get(c);
                if (MetaModel.isAbstract(paintableClass)) {
                    continue;
                }
                // nur für Node kann man das Layout im Moment festlegen -> Kanten auslassen
                if (!Node.class.isAssignableFrom(paintableClass)) {
                    continue;
                }
                if (metaModel.layerFor(paintableClass) != currentLayer) {
                    continue;
                }
                Class<? extends Node> paintableNodeClass = paintableClass.asSubclass(Node.class);

                if (c > maxInRow) {
                    maxInRow = c;
                }
                int index = counter + offset;
                Node node = metaModel.createElement(paintableNodeClass, true);
                NodeContainer kc = new NodeContainer(node, mydoc);
                nodeContainers[index] = kc;
                ElementsNameBuilder elementsNameBuilder = metaModel.getElementsNameBuilder();
                String name = elementsNameBuilder.getDisplayableName(paintableClass);
                node.setName(name);
                kc.setCoordinates(currX + 90, currY + 50, 100, 60);
                kc.setFont(mydoc.getDefaultElementsLayout().getStandardFont(kc));

                buttonShape[index] = new JButton(getResString("le_form"));
                buttonShape[index].setMargin(insets);
                buttonShape[index].setSize(50, 20);
                buttonShape[index].addActionListener(this);
                buttonShape[index].setLocation(currX + 40, currY + 95);
                panel.add(buttonShape[index]);

                buttonColor[index] = new JButton(getResString("le_farbe"));
                buttonColor[index].setMargin(insets);
                buttonColor[index].setSize(50, 20);
                buttonColor[index].addActionListener(this);
                buttonColor[index].setLocation(currX + 90, currY + 95);
                panel.add(buttonColor[index]);

                buttonFont[index] = new JButton(getResString("le_schrift"));
                buttonFont[index].setMargin(insets);
                buttonFont[index].setSize(50, 20);
                buttonFont[index].addActionListener(this);
                buttonFont[index].setLocation(currX + 140, currY + 95);
                panel.add(buttonFont[index]);

                currX += offsetX;
                counter++;
            }

            offset += counter;
            counter = 0;
            currX = 0;
            currY += offsetY;
        }
        currY -= offsetY;

        panel.setPreferredSize(new Dimension(maxInRow * offsetX + 30, currY + 90));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(350, 30));
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(buttonOK);
        buttonPanel.add(buttonCancel);
        buttonPanel.add(buttonApply);

        scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(790, currY + 90 + 50));

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(scrollPane);

        getContentPane().add(buttonPanel);
        pack();
        setModal(true);
        setVisible(true);
    }

    /**
     *
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == buttonCancel) {
            myDefaultElementsLayout.adapt(doc.getDefaultElementsLayout());
            dispose();
            return;
        }
        if (e.getSource() == buttonApply) {
            doc.adaptDefaultElementsLayout(myDefaultElementsLayout);
            doc.distributeEvent(ELEMENT_GRAPHICS_CHANGED);
            return;
        }
        if (e.getSource() == buttonOK) {
            doc.adaptDefaultElementsLayout(myDefaultElementsLayout);
            doc.distributeEvent(ELEMENT_GRAPHICS_CHANGED);
            dispose();
            // setVisible(false);
            return;
        }

        for (c = 0; c < offset; c++) {
            if (e.getSource() == buttonColor[c]) {
                selectedElementTypeIndex = c;

                Color oldColor = myDefaultElementsLayout.getStandardBackGroundColor(nodeContainers[selectedElementTypeIndex]);
                Color newColor = JColorChooser.showDialog(new JFrame(), getResString("farbe_ausw"), oldColor);

                if (newColor == null) {
                    return;
                }
                myDefaultElementsLayout.setStandardBackGroundColor(nodeContainers[selectedElementTypeIndex].getNode().getClass(), newColor);
                panel.repaint();
            }
            if (e.getSource() == buttonShape[c]) {
                selectedElementTypeIndex = c;
                menuShape.show(panel, nodeContainers[c].getX() - 50, nodeContainers[c].getY() + 45);
            }
            if (e.getSource() == buttonFont[c]) {
                selectedElementTypeIndex = c;
                Font font = EasyDialogAccess.getFontByChooser(this, nodeContainers[selectedElementTypeIndex].getFont());
                if (font != null) {
                    myDefaultElementsLayout.setStandardFont(nodeContainers[selectedElementTypeIndex].getNode().getClass(), font);
                }
                panel.repaint();
            }
        }

        String commandPrefix = "form ";
        String actionCommand = e.getActionCommand();
        if (actionCommand.startsWith(commandPrefix)) {
            try {
                NodeContainer nc = nodeContainers[selectedElementTypeIndex];
                Node node = nc.getNode();
                Class<? extends Node> elementClass = node.getClass();
                int commandPrefixLength = commandPrefix.length();
                String shapeName = actionCommand.substring(commandPrefixLength);
                Shape shape = Shape.valueOf(shapeName);
                myDefaultElementsLayout.setStandardForm(elementClass, shape);
                panel.repaint();
            } catch (Exception ne) {
                Log.show(Log.ERROR, getResString("Fehler beim Form setzen."), ne);
            }
        }
    }

    /**
     * @param g
     */
    public void mypaint(final Graphics2D g) {
        for (int c = 0; c < paintableNodesCount; c++) {
            if (nodeContainers[c] == null) {
                continue;
            }
            nodeContainers[c].setFont(myDefaultElementsLayout.getStandardFont(nodeContainers[c]));
            nodeContainers[c].refreshText();
            nodeContainers[c].paint(g);
        }
    }

}
