package de.imise.tool3lgm.graphtools.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GDCollection;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.dialog.panel.DescripPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogHeaderPanel;
import de.imise.tool3lgm.graphtools.dialog.panel.ElementDialogPanel;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.undoredo.InTransactionListener;
import de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.PropertyDialogUserFieldPanel;
import de.imise.tool3lgm.graphtools.view.container.ElementContainer;
import de.imise.util.swing.component.LimitedSizeScrollTextPane;
import de.imise.util.swing.component.TabbedPane;
import de.imise.util.swing.component.text.ExtendedTextPane;

/**
 * Eigenschaftsdialog für Modellelemnte, also Knoten und Kanten.<br>
 * 
 * @author N.N., AXS
 */
public class ElementPropertyDialog extends PropertyDialog implements ActionListener, InTransactionListener {

    /**
     * COMMENTME
     */
    private LimitedSizeScrollTextPane nameTextPane;

    /**
     * COMMENTME
     */
    private ExtendedTextPane descripPane;

    /**
     * COMMENTME
     */
    private String oldname, olddescrip;

    /**
     * COMMENTME
     */
    private final ModelElement modelElement;

    /**
     * COMMENTME
     */
    // private ExtendedTextPane nameLabel;
    private final ElementDialogHeaderPanel headerPanel;

    /**
     * COMMENTME
     */
    static int lastWidth = -1;

    /**
     * COMMENTME
     */
    static int lastHeight = -1;

    /**
     * @param modelElement
     * @param gdcoll
     */
    public ElementPropertyDialog(final ModelElement modelElement, final GDCollection gdcoll) {

        super(gdcoll);

        setTitle(Tool3lgmConstants.getResString("eigensch_dial"));

        getContentPane().setLayout(new BorderLayout());

        this.modelElement = modelElement;

        JPanel up = new JPanel(new GridLayout(1, 1));
        tab = new TabbedPane();
        tab.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        /*
         * nameLabel = new ExtendedTextPane(); nameLabel.setEditable(false);
         * nameLabel.setBackground(up.getBackground()); up.add(nameLabel);
         */
        headerPanel = new ElementDialogHeaderPanel(this);
        up.add(headerPanel);
        update();

        tab.addTab(Tool3lgmConstants.getResString("general"), new DescripPanel(this));
        // tab.addTab(Tool3lgmConstants.getResString("general"), new GeneralPanel(this));

        // wenn es mind ein Userfield für diese Klasse gibt -> zeige das USerFieldPanel
        if (doc.getCollection().getUserFieldDefinitions().hasUserFields(modelElement.getClass())) {
            tab.addTab(Tool3lgmConstants.getResString("userfields"), new PropertyDialogUserFieldPanel(this));
        }

        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new BorderLayout());

        JPanel bp = new JPanel();
        okButton.addActionListener(this);
        bp.add(okButton);
        cancelButton.addActionListener(this);
        bp.add(cancelButton);
        applyButton = new JButton(Tool3lgmConstants.getResString("apply"));
        applyButton.addActionListener(this);
        bp.add(applyButton);
        if (helpButton != null) {
            bp.add(helpButton);
        }

        buttonpanel.add(bp, BorderLayout.EAST);

        getContentPane().add(up, BorderLayout.NORTH);
        getContentPane().add(tab, BorderLayout.CENTER);
        getContentPane().add(buttonpanel, BorderLayout.SOUTH);

        pack();
        int xx = de.imise.tool3lgm.Tool3lgm.tool.getX() + 100;
        int yy = de.imise.tool3lgm.Tool3lgm.tool.getY() + 100;
        if (lastWidth == -1) {
            // lastWidth = this.getWidth();
            // lastHeight = this.getHeight();
            setLastWidth(600);
            setLastHeight(500);
        } else {
            for (int i = 0; i < ModelConstants.dialogs.size(); i++) {
                ElementPropertyDialog pd = ModelConstants.dialogs.get(i);
                if (pd.getLocation().x == xx && pd.getLocation().y == yy) {
                    xx += 20;
                    yy += 20;
                    i = -1;
                }
            }
            if (Toolkit.getDefaultToolkit().getScreenSize().width - xx < 150 || Toolkit.getDefaultToolkit().getScreenSize().height - yy < 150) {
                xx = de.imise.tool3lgm.Tool3lgm.tool.getX() + 100;
                yy = de.imise.tool3lgm.Tool3lgm.tool.getY() + 100;
            }
        }

        setLocation(xx, yy);
        setSize(lastWidth, lastHeight);

        addComponentListener(new ComponentListener() {
            @Override
            public void componentHidden(final ComponentEvent e) {
            }

            @Override
            public void componentMoved(final ComponentEvent e) {
                dialogPositionOrSizeChanged();
            }

            @Override
            public void componentResized(final ComponentEvent e) {
                dialogPositionOrSizeChanged();
            }

            @Override
            public void componentShown(final ComponentEvent e) {
            }
        });

    }

    /**
	 * 
	 */
    public void showDialog() {
        oldname = nameTextPane.getText();
        olddescrip = descripPane.getText();
        doc.start_transaction(getTransactionID());
        doc.addInTransactionListener(this);
        setVisible(true);
    }

    /**
     * @return
     */
    public ModelElement getModelElement() {
        return modelElement;
    }

    /**
     * @param textPane
     */
    public void setName(final LimitedSizeScrollTextPane textPane) {
        nameTextPane = textPane;
    }

    /**
     * @param descrip
     */
    public void setDescrip(final ExtendedTextPane descrip) {
        descripPane = descrip;
    }

    /**
	 * 
	 */
    private void commit() {
        // TODO: diese Aufrufe sollten bei Gelegenheit in der Methode commit von DescriptionPanel

        if (descripPane != null && nameTextPane != null) {
            String newName = nameTextPane.getText();
            // nur wenn der Name explizit geändert wurde, dann auch den Namen in einer Transaktion
            // ändern
            if (newName != null && !oldname.equals(newName)) {
                doc.setName(modelElement, newName, getTransactionID());
                // wenn der Name gleich gebleiben ist, kann aber trotzdem der HTML-Name in der
                // Grafik sich geändert haben,
                // wenn in dem Dialog ein Element verknüpft wurde, das auch im Namen in der Grafik
                // angezeigt wird -> einfach
                // ohne Transaktion in jedem Fall mal setName() mit dem alten Namen für das Element
                // aufrufen
            } else {
                modelElement.setName(oldname);
            }
            String newDescrip = descripPane.getText();
            if (newDescrip != null && !olddescrip.equals(newDescrip)) {
                doc.setDescription(modelElement.getHashString(), GraphDocument.getParseSaveString(newDescrip), getTransactionID());
            }
        }

        modelElement.refreshText();

        for (int m = 0; m < tab.getTabCount(); m++) {
            Component comp = tab.getComponentAt(m);
            if (comp instanceof ElementDialogPanel) {
                ((ElementDialogPanel) tab.getComponentAt(m)).commit();
            }
        }
        for (ElementPropertyDialog pd : ModelConstants.getDialogs()) {
            TabbedPane tp = pd.tab;
            // this wird in update klargemacht...
            if (pd != this) {
                for (int m = 0; m < tp.getTabCount(); m++) {
                    if (tp.getComponentAt(m) instanceof ElementDialogPanel) {
                        ((ElementDialogPanel) tp.getComponentAt(m)).update();
                    }
                }
            }
        }
    }

    /**
     * @param doUpdate
     */
    public void commit(final boolean doUpdate) {
        // System.out.println("ElementPropertyDialog commit "+ doUpdate);
        commit();
        if (doUpdate) {
            update();
        } else {
            ((ElementDialogPanel) tab.getSelectedComponent()).update();
        }
        doc.finish_transaction(getTransactionID());
        doc.distributeEvent(GraphDocument.DATA_CHANGED, getTransactionID());
        oldname = nameTextPane.getText();
        olddescrip = descripPane.getText();
        doc.start_transaction(createNewTransactionID());
    }

    public void cancel() {
        doc.finish_transaction(getTransactionID());
        doc.undo(getTransactionID());
        close();
    }

    private void close() {
        ModelConstants.removeDialog(modelElement);
        doc.finish_transaction(getTransactionID());
        doc.removeInTransactionListener(this);
        dispose();
    }

    /**
     * COMMENTME
     */
    boolean closing = false;

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == okButton) {
            closing = true;
            commit(false);
            close();
            closing = false;
        } else if (e.getSource() == cancelButton) {
            closing = true;
            cancel();
            closing = false;
        } else if (e.getSource() == applyButton) {
            commit(true);
        }
        doc.select(modelElement.getContainer(doc), getTransactionID());
        doc.distributeEvent(GraphDocument.SELECTION_CHANGED, getTransactionID());
    }

    @Override
    protected void processWindowEvent(final WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
    }

    // InTransactionListener Begin
    // ###################################################################################

    @Override
    public void dataChanged(final GraphDocument source, final int pid) {
        if (Tool3lgm.DEBUG) {
            System.err.println(getClass().getSimpleName() + "dataChanged() " + modelElement + " " + source + " " + pid);
        }
        update();
    }

    @Override
    public void elementAdded(final GraphDocument source, final ElementContainer element) {
        update();
    }

    @Override
    public void elementDeleted(final GraphDocument source, final ElementContainer element) {
        update();
    }

    @Override
    public void elementGraphicsChanged(final GraphDocument source, final ElementContainer element) {
    }

    // InTransactionListener Ende
    // ####################################################################################

    /**
	 * 
	 */
    private void dialogPositionOrSizeChanged() {
        lastWidth = getWidth();
        lastHeight = getHeight();
    }

    /**
	 * 
	 */
    public void update() {

        if (closing) {
            return;
        }

        // String tmname = "";
        // boolean isKnoten = Knoten.class.isAssignableFrom(modelElement.getClass());
        // if (isKnoten) {
        // GraphDocument vdoc = doc.getCollection().getGraphDocumentCoded(((Knoten)
        // modelElement).getAssociatedDoc());
        // tmname = (vdoc != null ? vdoc.getTitle() : "----------");
        // }
        // nameLabel.setText(ModelConstants.getDisplayableName(modelElement) + "\n" +
        // Tool3lgmConstants.getResString("bez") + ":\t\t" + modelElement.getClearName() +
        // "\nID:\t\t" + modelElement.getHashString()
        // + (isKnoten ? "\n" + Tool3lgmConstants.getResString("verkn_teilmodell") + ":\t" + tmname
        // : ""));

        headerPanel.update();
        for (int i = 0; i < tab.getTabCount(); i++) {
            Component c = tab.getComponentAt(i);
            if (c instanceof ElementDialogPanel) {
                ((ElementDialogPanel) c).update();
            }
        }
    }

    /**
     * @param lastHeight The lastHeight to set.
     */
    public static void setLastHeight(final int lastHeight) {
        ElementPropertyDialog.lastHeight = lastHeight;
    }

    /**
     * @param lastWidth The lastWidth to set.
     */
    public static void setLastWidth(final int lastWidth) {
        ElementPropertyDialog.lastWidth = lastWidth;
    }
}