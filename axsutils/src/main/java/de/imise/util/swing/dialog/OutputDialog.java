package de.imise.util.swing.dialog;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
public class OutputDialog extends JDialog implements WindowListener {

    /**
     * Das Textfeld, in denen die Ausgaben dargestellt werden.
     */
    private final ExtendedTextArea outputTextField = new ExtendedTextArea();

    /**
     * Das Scrollpane für das Ausgabetextfeld
     */
    private final JScrollPane outputScrollPane = new JScrollPane(outputTextField);

    /**
     * @param owner
     * @param title
     */
    public OutputDialog(final Frame owner, final String title) {
        this(owner, title, false);
    }

    /**
     * @param owner
     * @param title
     * @param modal
     */
    public OutputDialog(final Frame owner, final String title, final boolean modal) {
        super(owner, title, modal);
        addWindowListener(this);
        outputTextField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        getContentPane().add(outputScrollPane);
        pack();
        setSize(700, 500);
    }

    /**
     * @param text
     */
    public void append(final String text) {
        outputTextField.append(text);
        JScrollBar scrollBar = outputScrollPane.getHorizontalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
        revalidate();
        repaint();
    }

    /**
     * @param text
     */
    public void appendln(final Object text, final Object... additionalText) {
        appendlnInternal(text);
        if (additionalText != null) {
            for (Object o : additionalText) {
                appendlnInternal(o);
            }
        }
    }

    /**
     * @param text
     */
    public void appendln() {
        appendlnInternal("");
    }

    /**
     * @param text
     */
    private void appendlnInternal(final Object text) {
        if (text != null) {
            if (text instanceof Throwable) {
                Throwable t = (Throwable) text;
                append(t.getMessage());
                append("\n");
                for (StackTraceElement traceElem : t.getStackTrace()) {
                    append(traceElem.toString());
                    append("\n");
                }
            } else {
                append(text.toString());
                append("\n");
            }
        } else {
            append("\n");
        }
    }

    /**
     * Appends the <code>text</code> and underlines it.
     *
     * @param text
     * @param underline
     */
    public void appendln(final String text, final boolean underline) {
        if (!underline) {
            appendln(text);
        }
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
    public void set(final String text) {
        outputTextField.setText(text);
        JScrollBar scrollBar = outputScrollPane.getHorizontalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
    }

    @Override
    public void windowOpened(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
    }

    @Override
    public void windowClosed(final WindowEvent e) {
        outputTextField.setText("");
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowActivated(final WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
    }

}
