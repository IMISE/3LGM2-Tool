/*
 * Created on 30.10.2007
 */
package de.imise.tool3lgm.graphtools.dialog;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.graphtools.model.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.help.Help;
import de.imise.util.swing.SwingUtils;
import de.imise.util.swing.dialog.AbstractSizeAndPositionRestoringDialog;

/**
 * Allgemeiner Eigenschaftendialog.<br>
 * In dieser Form kann er als Eigenschaftendialog für ein Modell genutzt werden. Der
 * Eigenschaftendialog von Modellelementen ist eine spezielle Unterklasse dieser Dialogklasse.
 *
 * @author AXS
 * @created 30.10.2007
 */
public abstract class AbstractPropertyDialog extends AbstractSizeAndPositionRestoringDialog implements WindowListener {

    /**
     * Ok-Knopf für die Dialoge
     */
    protected JButton okButton;

    /**
     * Abbrechen-Knopf für die Dialoge
     */
    protected JButton cancelButton;

    /**
     * Übernehmen-Knopf für die Dialoge
     */
    protected JButton applyButton;

    /**
     * Hilfe-Knopf für die Dialoge
     */
    protected JButton helpButton;

    /** Modell in dessen Kontext der Dialog steht */
    protected GDCollection gdcoll;

    /** Hauptmodell der GDCollection */
    protected LGMGraphDocument doc;

    /** ID des Dialoges mit der alle Transaktionen durchgeführt werden */
    protected int transactionID;

    /**
     * Panel, das <code>applyButton</code>,<code>okButton</code> und <code>cancelButton</code> beinhaltet
     */
    protected JPanel controlPanel;

    /**
     * @param graphDocument Document in dessen Kontext der Dialog steht
     */
    public AbstractPropertyDialog(final GDCollection gdcoll) {
        this(Static.getMainFrame(), gdcoll);
    }

    /**
     * @param owner
     * @param gdcoll Modell, in dessen Kontext der Dialog steht
     */
    public AbstractPropertyDialog(final Frame owner, final GDCollection gdcoll) {
        super(owner, "", false);
        init(gdcoll);
    }

    /**
     * @param owner
     * @param graphDocument Document in dessen Kontext der Dialog steht
     */
    public AbstractPropertyDialog(final Dialog owner, final GDCollection gdcoll) {
        super(owner, "", owner instanceof AbstractPropertyDialog);
        init(gdcoll);
    }

    /**
     * @param owner
     */
    public AbstractPropertyDialog(final AbstractPropertyDialog owner) {
        this(owner, owner.gdcoll);
    }

    /**
     * @param gdcoll
     */
    private void init(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
        addWindowListener(this);
        doc = gdcoll.getMainGraphDocument();
        okButton = new JButton(getResString("ok"));
        cancelButton = new JButton(getResString("cancel"));
        applyButton = new JButton(getResString("apply"));
        SwingUtils.setSamePreferredSize(okButton, cancelButton, applyButton);
        String helpID = Help.getHelp().getHelpID(this);
        if (helpID != null && helpID.length() > 0) {
            helpButton = new JButton(getResString("hilfeButtonText"));
            helpButton.setMnemonic('H');
            Help.getHelp().enableHelpOnButton(helpButton, helpID);
        }
        createNewTransactionID();
    }

    /**
     * @return Returns the transactionID.
     */
    public final int getTransactionID() {
        return transactionID;
    }

    /**
     * @return
     */
    protected final int createNewTransactionID() {
        Window owner = getOwner();
        transactionID = owner instanceof AbstractPropertyDialog ? ((AbstractPropertyDialog) owner).transactionID : GraphDocument.createTransactionId();
        return transactionID;
    }

    /**
     * @return <code>doc</code>
     */
    public final LGMGraphDocument getGraphDocument() {
        return doc;
    }

    /**
     * @return
     */
    public final UserFieldDefinitions getUserFieldDefinitions() {
        return gdcoll.getUserFieldDefinitions();
    }

    /**
     * Performs a click on the Ok-Button
     */
    public final void performOK() {
        okButton.doClick();
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    // WindowListener -> bei Aktivierung im Contextgenerator auf das GraphDocument des Dialoges
    // wechseln //
    // /////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void windowActivated(final WindowEvent e) {
        Tool3lgm.getContextGenerator().changeContext(doc);
    }

    @Override
    public void windowOpened(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
    }

    @Override
    public void windowClosed(final WindowEvent e) {
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

}
