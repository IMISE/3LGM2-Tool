/*
 * Created on 02.09.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import de.imise.util.swing.component.text.ExtendedTextPane;
/**
 * Wird maxLines >0 angegeben, dann wird das TextPane nie größer als diese Zeilenanzahl 
 * sondern es erscheint ein ScrollPane. Bei maxLines kleiner 1 vergrößert sich das Pane
 * beliebig, ohne dass ein ScrollPane erscheint. 
 */
public class LimitedSizeScrollTextPane extends JScrollPane {

	/**
	 * Maximale Zeilenanzahl, auf die sich die Komponente vergrößert, bevor das ScrollPane angezeigt wird.
	 */
	private int maxLines;
	
	/**
	 * Das eigentliche Textpane
	 */
	private JTextComponent textPane;

	/**
	 * 
	 */
	public LimitedSizeScrollTextPane() {
		this(-1);
	}

	/**
	 * @param maxLines
	 */
	public LimitedSizeScrollTextPane(int maxLines) {
		super();
		textPane = new ExtendedTextPane();
		this.setViewportView(textPane);
		this.maxLines = maxLines;
//		if (maxLines>1){
			textPane.addCaretListener(new CaretListener() {
				@Override
				public void caretUpdate(CaretEvent e) {
					setSize(new Dimension(getSize().width, getPreferredSize().height));
					Component comp = getParent();
					if (comp != null)
						((JComponent) getParent()).revalidate();
				}
			});
//		}
	}

	public void setCaretPosition(int position) {
		textPane.setCaretPosition(position);
		textPane.setSize(getSize());

	}

	public int getCaretPosition() {
		return textPane.getCaretPosition();

	}

	public String getText() {
		return textPane.getText().replaceAll("\r", "");
	}

	public void setText(String text) {
		textPane.setText(text);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getMinimumSize()
	 */
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		if (maxLines > 0) {
			if (super.getPreferredSize().height > getMaximumSize().height) {
				return getMaximumSize();
			}
		}
		return super.getPreferredSize();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getMaximumSize()
	 */
	@Override
	public Dimension getMaximumSize() {
		if (maxLines > 0)
			return new Dimension(textPane.getMaximumSize().width, (textPane.getFontMetrics(textPane.getFont()).getHeight() + 3) * maxLines + 5);
		return super.getMaximumSize();
	}

	/**
	 * @return
	 */
	public JTextComponent getTextPane() {
		return (JTextComponent) getViewport().getView();
	}


	/**
	 * @param b
	 */
	public void setEditable(boolean b) {
		textPane.setEditable(b);
	}

	/**
	 * 
	 */
	public void selectAll() {
		textPane.selectAll();
	}

	/**
	 * @return
	 */
	public String getSelectedText() {
		return textPane.getSelectedText();
	}

	/**
	 * @return
	 */
	public int getSelectionStart() {
		return textPane.getSelectionStart();
	}

	/**
	 * @return
	 */
	public int getSelectionEnd() {
		return textPane.getSelectionEnd();
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#addKeyListener(java.awt.event.KeyListener)
	 */
	@Override
	public synchronized void addKeyListener(final KeyListener listener) {
		textPane.addKeyListener(listener);
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#removeKeyListener(java.awt.event.KeyListener)
	 */
	@Override
	public synchronized void removeKeyListener(final KeyListener listener) {
		textPane.removeKeyListener(listener);
	}

}