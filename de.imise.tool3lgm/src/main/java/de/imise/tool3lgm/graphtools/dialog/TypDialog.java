package de.imise.tool3lgm.graphtools.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgm;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.RechAnwendungsbaustein;

public class TypDialog extends JDialog implements ActionListener, KeyListener {
	
	/**
	 * COMMENTME
	 */
	protected JCheckBox box1, box2;
	
	/**
	 * COMMENTME
	 */
	protected Class<? extends ModelElement> inputValue;
	
	/**
	 * COMMENTME
	 */
	protected TypDialog dialog;
	
	/**
	 * COMMENTME
	 */
	protected JButton button;

	/**
	 * 
	 */
	public TypDialog() {
		super();
		addKeyListener(this);
	}

	/**
	 * @param t
	 * @param question
	 * @param object
	 * @return
	 */
	public Class<? extends ModelElement> showDialog(String t, String question, String object) {
		dialog = new TypDialog();
		JDialog aktdia = dialog.createDialog(t, question, object);
		if (Tool3lgm.tool != null)
			aktdia.setLocation(Tool3lgm.tool.getX() + 100, Tool3lgm.tool.getY() + 100);
		aktdia.setVisible(true);

		return dialog.getInputValue();
	}

	/**
	 * @param titel
	 * @param frage
	 * @param obj
	 * @return
	 */
	protected JDialog createDialog(String titel, String frage, String obj) {
		setTitle(titel);
		setSize(250, 150);
		setModal(true);
		getContentPane().setLayout(new BorderLayout());
		JPanel panel = new JPanel();
		JLabel text = new JLabel(frage);
		panel.add(text);
		JPanel wahl = new JPanel();
		box1 = new JCheckBox(Tool3lgmConstants.getResString("rechunt"));
		box1.setSelected(true);
		box2 = new JCheckBox(Tool3lgmConstants.getResString("konv"));
		ButtonGroup bg = new ButtonGroup();
		bg.add(box1);
		bg.add(box2);
		wahl.add(box1);
		wahl.add(box2);

		JPanel buttonpanel = new JPanel();
		button = new JButton(Tool3lgmConstants.getResString("ok"));
		button.addActionListener(this);
		buttonpanel.add(button);
		// b.addActionListener(this);
		// buttonpanel.add(b);

		getContentPane().add(panel, BorderLayout.NORTH);
		getContentPane().add(wahl, BorderLayout.CENTER);
		getContentPane().add(buttonpanel, BorderLayout.SOUTH);

		return this;
	}

	/**
	 * @return
	 */
	public Class<? extends ModelElement> getInputValue() {
		return inputValue;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals(Tool3lgmConstants.getResString("ok"))) {
			if (box1.isSelected())
				inputValue = RechAnwendungsbaustein.class;
			if (box2.isSelected())
				inputValue = KonAnwendungsbaustein.class;
			JButton but = (JButton) e.getSource();
			Component parent = but;
			do {
				parent = parent.getParent();
			} while (!(parent instanceof JDialog));
			JDialog d = (JDialog) parent;
			d.dispose();
		}
		if (str.equals(Tool3lgmConstants.getResString("cancel"))) {
			inputValue = null;
			JButton but = (JButton) e.getSource();
			Component parent = but;
			do {
				parent = parent.getParent();
			} while (!(parent instanceof JDialog));
			JDialog d = (JDialog) parent;
			d.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == 10) {
			if (box1.isSelected())
				inputValue = RechAnwendungsbaustein.class;
			if (box2.isSelected())
				inputValue = KonAnwendungsbaustein.class;
			Component parent = button;
			do {
				parent = parent.getParent();
			} while (!(parent instanceof JDialog));
			JDialog d = (JDialog) parent;
			d.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}
}
