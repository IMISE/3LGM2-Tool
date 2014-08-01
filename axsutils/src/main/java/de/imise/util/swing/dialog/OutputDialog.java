package de.imise.util.swing.dialog;

import java.awt.Frame;
import java.util.Arrays;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import de.imise.util.swing.component.text.ExtendedTextArea;

/**
 * Einfacher Dialog zur Ausgabe von Textnachrichten
 * 
 * @author AXS
 * @created 25.10.2007
 */
public class OutputDialog extends JDialog {

	/**
	 * Das Textfeld, in denen die Ausgaben dargestellt werden.
	 */
	private ExtendedTextArea outputTextField = new ExtendedTextArea();

	/**
	 * Das Scrollpane für das Ausgabetextfeld
	 */
	private JScrollPane outputScrollPane = new JScrollPane(outputTextField);

	/**
	 * @param owner
	 * @param title
	 */
	public OutputDialog(Frame owner, String title) {
		this(owner, title, false);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 */
	public OutputDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		outputTextField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		getContentPane().add(outputScrollPane);
		pack();
		setSize(700, 500);
	}

	/**
	 * @param text
	 */
	public void append(String text) {
		outputTextField.append(text);
		JScrollBar scrollBar = outputScrollPane.getHorizontalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

	/**
	 * @param text
	 */
	public void appendln(String text) {
		if (text != null)
			append(text);
		append("\n");
	}

	/**
	 */
	public void appendln() {
		append("\n");
	}

	/**
	 * Appends the <code>text</code> and underlines it.
	 * 
	 * @param text
	 * @param underline
	 */
	public void appendln(String text, boolean underline) {
		if (!underline)
			appendln(text);
		if (text == null) {
			append("\n");
			return;
		}
		StringTokenizer st = new StringTokenizer(text, "\n");
		while (st.hasMoreTokens()) {
			String tok = st.nextToken();
			append(text);
			append("\n");
			char[] cArray = new char[tok.length()];
			Arrays.fill(cArray, '-');
			append(new String(cArray));
			append("\n");
		}
	}

	/**
	 * @param text
	 */
	public void set(String text) {
		outputTextField.setText(text);
		JScrollBar scrollBar = outputScrollPane.getHorizontalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
	}

}
