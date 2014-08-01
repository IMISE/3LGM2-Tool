/*
 * Created on 30.10.2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package de.imise.tool3lgm.graphtools.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.userproperties.UserProperties;

/**
 * @author Thomas Rudert
 *
 * Dialog for setting the properties for drawing graphics
 */
public class GraphicPropertyDialog extends JDialog {

	/** Bitpattern for Rendering-Hints (standard value: all bits are set to zero
	 * bit0: ANTIALIASING
	 * bit1: ALPHA_INTERPOLATION
	 * bit2: COLOR_RENDERING
	 * bit3: RENDERING
	 * bit4: DITHERING
	 * bit5: FRACTIONALMETRICS
	 * bit6: INTERPOLATION
	 * bit7: TEXT_ANTIALIASING
	 */
	
	private JCheckBox[] checkBoxArray = new JCheckBox[8];
	private String[] checkBoxText = {"Antialiasing", "Alpha-Interpolation", "Color-Rendering", "Rendering", "Dithering",
			"Fractionalmetrics", "Interpolation", "Text-Antialiasing"};
	
	public GraphicPropertyDialog(Frame owner) {
		super(owner, Tool3lgmConstants.getResString("graphicProperty"), true);

		setLocationRelativeTo(owner);
		
		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.Y_AXIS));
		
		int renderingHints = UserProperties.getRenderingHints();
		
		for (int i=0; i < checkBoxArray.length; i++) {
			checkBoxArray[i] = new JCheckBox(checkBoxText[i]);
			checkBoxArray[i].setSelected(((renderingHints >> i) & 1) == 1);
			checkBoxPanel.add(checkBoxArray[i]);
		}
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(checkBoxPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		
		JButton okButton = new JButton(Tool3lgmConstants.getResString("ok"));
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					setProperties();
					dispose();
			}
		});

		JButton applyButton = new JButton(Tool3lgmConstants.getResString("apply"));
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setProperties();
			}
		});
		
		JButton cancelButton = new JButton(Tool3lgmConstants.getResString("cancel"));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(applyButton);
		buttonPanel.add(cancelButton);
		
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
	}
	
	private void setProperties() {
		int bitPattern = 0;
		for (int i=0; i<checkBoxArray.length; i++)
			bitPattern |= checkBoxArray[i].isSelected() ? 1 << i : 0;

		UserProperties.setRenderingHints(bitPattern);
		getParent().repaint();
	}
}
