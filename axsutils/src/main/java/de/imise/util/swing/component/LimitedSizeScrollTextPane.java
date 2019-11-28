/*
 * Created on 02.09.2003
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.imise.util.swing.component;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentListener;
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
    private final int maxLines;

    /**
     * Das eigentliche Textpane
     */
    private final JTextComponent textPane;

    /**
     *
     */
    public LimitedSizeScrollTextPane() {
        this(-1);
    }

    /**
     * @param editable
     */
    public LimitedSizeScrollTextPane(final boolean editable) {
        this(-1, editable);
    }

    /**
     * @param maxLines
     */
    public LimitedSizeScrollTextPane(final int maxLines) {
        this(maxLines, true);
    }

    /**
     * @param maxLines
     * @param editable
     */
    public LimitedSizeScrollTextPane(final int maxLines, final boolean editable) {
        super();
        textPane = new ExtendedTextPane(editable);
        setViewportView(textPane);
        this.maxLines = maxLines;
        //      if (maxLines>1){
        textPane.addCaretListener(e -> {
            setSize(new Dimension(getSize().width, getPreferredSize().height));
            Component comp = getParent();
            if (comp != null) {
                ((JComponent) getParent()).revalidate();
            }
        });
        //      }
    }
    public void setCaretPosition(final int position) {
        textPane.setCaretPosition(position);
        textPane.setSize(getSize());

    }

    public int getCaretPosition() {
        return textPane.getCaretPosition();

    }

    public String getText() {
        return textPane.getText().replaceAll("\r", "");
    }

    public void setText(final String text) {
        textPane.setText(text);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        if (maxLines > 0) {
            if (super.getPreferredSize().height > getMaximumSize().height) {
                return getMaximumSize();
            }
        }
        return super.getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        if (maxLines > 0) {
            return new Dimension(textPane.getMaximumSize().width, (textPane.getFontMetrics(textPane.getFont()).getHeight() + 3) * maxLines + 5);
        }
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
    public void setEditable(final boolean b) {
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

    private final Set<KeyListener> keyListeners = new HashSet<>();

    @Override
    public synchronized void addKeyListener(final KeyListener listener) {
        //jeden Listener nur 1 x hinzufügen
        if (keyListeners.contains(listener)) {
            return;
        }
        textPane.addKeyListener(listener);
        keyListeners.add(listener);
    }

    @Override
    public synchronized void removeKeyListener(final KeyListener listener) {
        textPane.removeKeyListener(listener);
        keyListeners.remove(listener);
    }

    private final Set<DocumentListener> documentListeners = new HashSet<>();

    public void addDocumentListener(final DocumentListener listener) {
        //jeden Listener nur 1 x hinzufügen
        if (documentListeners.contains(listener)) {
            return;
        }
        textPane.getDocument().addDocumentListener(listener);
        documentListeners.add(listener);
    }

    public void removeDocumentListener(final DocumentListener listener) {
        textPane.getDocument().removeDocumentListener(listener);
        documentListeners.remove(listener);
    }

    @Override
    public synchronized void addMouseListener(final MouseListener l) {
        super.addMouseListener(l);
        textPane.addMouseListener(l);
    }

    @Override
    public void setBackground(final Color bg) {
        super.setBackground(bg);
        if (textPane != null) {
            textPane.setBackground(bg);
        }
    }

}