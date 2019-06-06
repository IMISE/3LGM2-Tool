/*
 * Created on 06.02.2008
 */
package de.imise.tool3lgm.graphtools.userfield.dialog.definition.panel;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_SUM;
import static de.imise.tool3lgm.graphtools.userfield.UserField.ACCOUNTING_FUNCTION_TWSUM;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import de.imise.tool3lgm.graphtools.metamodel.MetaModel;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.SubordinationEdge;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.userfield.UserFieldTarget;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * Panel mit dem für Kennzahlen ausgewählt werden kann, wie sie innerhalb einer Hierarchie - also elementartintern verrechnet werden können. Dieses
 * Panel wird Moment nicht benutzt, da die interne Verrechnung in Hierarchien jetzt (erstmal) durch die Definition der Formeln erledigt wird.
 *
 * @author AXS
 */
public class InternalAccountingPanel extends AbstractInputPanel implements ActionListener {

    private final JRadioButton noAccountingBut = new JRadioButton(getResString("non_accounting"));

    private final JRadioButton sumAccountingBut = new JRadioButton(UserField.getDisplayableFunctionName(ACCOUNTING_FUNCTION_SUM));

    private final JRadioButton twsumAccountingBut = new JRadioButton(UserField.getDisplayableFunctionName(ACCOUNTING_FUNCTION_TWSUM));

    private final AlphabeticalComboBox dirComboBox = new AlphabeticalComboBox();

    private final AlphabeticalComboBox vgComboBox = new AlphabeticalComboBox();

    /**
     * "Gleichverteilt" in der gewählten Loacle. Wird angezeigt, wenn als Verteilungsgewicht bei einer Verrechnung über die Teilwertsumme kein
     * spezielles Verteilungsgewicht genutzt werden soll.
     */
    private static final String UNIFORMLY_DISTRIBUTED = getResString("uniformly_distributed");

    /**
     * @param definitions
     * @param hierarchieEdgeClass
     */
    public InternalAccountingPanel(final UserFieldDefinitions definitions, final UserField userField) {
        super(new GridBagLayout());

        //alle Objekte des Panels initialisieren
        ButtonGroup functionButtonGroup = new ButtonGroup();
        functionButtonGroup.add(noAccountingBut);
        functionButtonGroup.add(sumAccountingBut);
        functionButtonGroup.add(twsumAccountingBut);

        JPanel functionButtonPanel = new JPanel();
        functionButtonPanel.add(noAccountingBut);
        functionButtonPanel.add(sumAccountingBut);
        functionButtonPanel.add(twsumAccountingBut);

        dirComboBox.addItem(UserField.DIRECTION_FROM_PART_TO_WHOLE, getResString("part_to_whole"));
        dirComboBox.addItem(UserField.DIRECTION_FROM_WHOLE_TO_PART, getResString("whole_to_part"));

        Class<? extends UserFieldTarget> userFieldTargetClass = userField.getTargetClass();
        if (ModelElement.class.isAssignableFrom(userFieldTargetClass)) {
            Class<? extends ModelElement> elementClass = userFieldTargetClass.asSubclass(ModelElement.class);
            MetaModel metaModel = definitions.getMetaModel();
            for (Class<? extends Edge> edgeClass : metaModel.getEdgeTypes(elementClass)) {
                if (SubordinationEdge.class.isAssignableFrom(edgeClass)) {
                    vgComboBox.addItem(UNIFORMLY_DISTRIBUTED);
                    vgComboBox.addSeparator(false);
                    for (UserField uf : definitions.getUserFields(edgeClass)) {
                        vgComboBox.addItem(uf.getHashCode(), uf.toString());
                    }
                }
            }
        }
        //allen notwendigen Komponenten den ActionListener anheften
        noAccountingBut.addActionListener(this);
        sumAccountingBut.addActionListener(this);
        twsumAccountingBut.addActionListener(this);

        //Das Panel zusammenbauen
        setBorder(BorderFactory.createTitledBorder(getResString("internal_accounting")));

        GridBagConstraints constraints = new GridBagConstraints(0, 0, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0);

        add(functionButtonPanel, constraints);
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        add(new JLabel(getResString("direction")), constraints);
        constraints.insets.left = 10;
        constraints.gridx++;
        add(dirComboBox, constraints);

        //userField.ge

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.insets.left = 0;
        add(new JLabel(getResString("weighting")), constraints);
        constraints.insets.left = 10;
        constraints.gridx++;
        add(vgComboBox, constraints);

        //falls im UserField bereits eine Verrechnungsfunktion, eine Richtung und ein Verteilungsgewicht
        //hinterlegt sind, müssen diese in den Komponenten ausgewählt werden
        //als erstes die Funktion setzen
        //		if (userField.getInternalAccountingFunction()==UserField.INTERNAL_ACCOUNTING_FUNCTION_SUM)
        //			sumAccountingBut.setSelected(true);
        //		else if (userField.getInternalAccountingFunction()==UserField.INTERNAL_ACCOUNTING_FUNCTION_TWSUM)
        //			twsumAccountingBut.setSelected(true);
        //		else
        //			noAccountingBut.setSelected(true);
        //		//die Verrechnungsrichtung setzen
        //		if (userField.getInternalAccountingDirection()==UserField.DIRECTION_FROM_PART_TO_WHOLE)
        //			dirComboBox.setSelectedObject(UserField.DIRECTION_FROM_PART_TO_WHOLE);
        //		else if (userField.getInternalAccountingDirection()==UserField.DIRECTION_FROM_WHOLE_TO_PART)
        //			dirComboBox.setSelectedObject(UserField.DIRECTION_FROM_WHOLE_TO_PART);
        //		//das bei der Verrechnung zu nutzende Verteilungsgewicht setzen
        //		if (userField.getInternalAccountingWeightUserFieldHash()!=null)
        //			vgComboBox.setSelectedObject(userField.getInternalAccountingWeightUserFieldHash());

        initSelection();
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == noAccountingBut) {
            dirComboBox.setSelectedItem(null);
            vgComboBox.setSelectedItem(null);
            dirComboBox.setEnabled(false);
            vgComboBox.setEnabled(false);
        } else if (e.getSource() == twsumAccountingBut) {
            dirComboBox.setEnabled(true);
            vgComboBox.setEnabled(true);
            vgComboBox.setSelectedItem(UNIFORMLY_DISTRIBUTED);
        } else {
            dirComboBox.setEnabled(true);
            dirComboBox.setSelectedIndex(0);
            vgComboBox.setEnabled(false);
            vgComboBox.setSelectedItem(null);
        }
    }

    private void initSelection() {
        //die Funktion des UserFields selektieren
        //	/*	String elementToSelect = userField.getInternalAccountingFunction();
        //		if (UserField.INTERNAL_ACCOUNTING_FUNCTION_SUM.equals(elementToSelect)){
        //			sumAccountingBut.doClick();
        //		}else if (UserField.INTERNAL_ACCOUNTING_FUNCTION_TWSUM.equals(elementToSelect)){
        //			twsumAccountingBut.doClick();
        //		}else{
        //			noAccountingBut.doClick();
        //		}
        //
        //		//die Richtung des UserFields selektieren
        //		dirComboBox.setSelectedObject(userField.getInternalAccountingDirection());
        //
        //		//das Verteilungsgewicht der internen Verrechnung selektieren (nur bei Teilwertsumme möglich)
        //		if (twsumAccountingBut.isSelected()) {
        //			String weightHash = userField.getInternalAccountingWeightUserFieldHash();
        //			if (vgComboBox.setSelectedObject(weightHash)<0)
        //				vgComboBox.setSelectedObject(UNIFORMLY_DISTRIBUTED);
        //			else
        //				vgComboBox.setSelectedObject(weightHash);
        //		}
        //		*/
    }

    //	/**
    //	 * Liefert einen der folgenden Strings: <br>
    //	 * <ul>
    //	 * <li><code>UserFiled.INTERNAL_ACCOUNTING_FUNCTION_SUM</code>, wenn über die interne Verrechnung über eine Summe ausgewählt ist</li>
    //	 * <li><code>UserFiled.INTERNAL_ACCOUNTING_FUNCTION_TWSUM</code>, wenn über die interne Verrechnung über eine Teilwertsumme ausgewählt ist</li>
    //	 * <li><code>null</code>, wenn nicht verrechnet werden soll</li>
    //	 * </ul>
    //	 * @return
    //	 * 		String der Funktion über die intern verrechnet werden soll
    //	 * /
    //	public final String getSelectedInternalFuntionName(){
    //		if (sumAccountingBut.isSelected())
    //			return UserField.INTERNAL_ACCOUNTING_FUNCTION_SUM;
    //		if (twsumAccountingBut.isSelected())
    //			return UserField.INTERNAL_ACCOUNTING_FUNCTION_TWSUM;
    //		return null;
    //	}
    //*/

    /**
     * Liefert je nach Auswahl in der Richtungs-ComboBox die Strings <code>UserField.DIRECTION_FROM_PART_TO_WHOLE</code>,
     * <code>UserField.DIRECTION_FROM_WHOLE_TO_PART</code> oder <code>null</code>.
     *
     * @return Richtung in der eine verrechnet werden soll
     */
    public final String getSelectedInternalAccountingDirection() {
        Object selectedDir = dirComboBox.getSelectedObject();
        if (selectedDir == null) {
            return null;
        }
        return selectedDir.toString();
    }

    /**
     * Liefert den Hash-String des selektierten Verteilungsgwichtes für die interne Verrechnung.
     *
     * @return
     */
    public String getSelectedInternalAccountingWeigthHash() {
        Object vg = vgComboBox.getSelectedObject();
        if (vg != null && !vg.equals(UNIFORMLY_DISTRIBUTED)) {
            return vg.toString();
        }
        return null;
    }

    @Override
    public void cancel() {
    }

    @Override
    public void commit() {

        //         String selectedFuntion = getSelectedInternalFuntionName(); if (UserField.INTERNAL_ACCOUNTING_FUNCTION_SUM.equals(selectedFuntion)){
        //         userField.setInternalAccountingFunction(UserField.INTERNAL_ACCOUNTING_FUNCTION_SUM);
        //         userField.setInternalAccountingDirection(getSelectedInternalAccountingDirection());
        //         userField.setInternalAccountingWeightUserFieldHash(null); }else if (UserField.INTERNAL_ACCOUNTING_FUNCTION_TWSUM.equals(selectedFuntion)){
        //         userField.setInternalAccountingFunction(UserField.INTERNAL_ACCOUNTING_FUNCTION_TWSUM);
        //         userField.setInternalAccountingDirection(getSelectedInternalAccountingDirection());
        //         userField.setInternalAccountingWeightUserFieldHash(getSelectedInternalAccountingWeigthHash()); }else {
        //         userField.setInternalAccountingFunction(null); userField.setInternalAccountingDirection(null);
        //         userField.setInternalAccountingWeightUserFieldHash(null); }

    }

}
