package de.imise.util.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import de.imise.util.swing.component.LimitedSizeScrollTextPane;

/**
 * Dialog zum Eingeben eines Namens und optional einer Farbe
 *
 * @author AXS
 *         created on 21.08.2004
 */
public class NameAndColorInputDialog extends JDialog implements ActionListener {

    /**
     * COMMENTME
     */
    private LimitedSizeScrollTextPane inputArea;

    /**
     * COMMENTME
     */
    private String inputString;

    /**
     * COMMENTME
     */
    private Color inputColor;

    /**
     * COMMENTME
     */
    private JButton ok_button;

    /**
     * COMMENTME
     */
    private JButton cancel_button;

    /**
     * COMMENTME
     */
    private JColorChooser colorChooser = null;

    /**
     * COMMENTME
     */
    private JPanel centerPanel;

    /**
     * COMMENTME
     */
    public static final int DEFAULT_WIDTH = 300;

    /**
     * COMMENTME
     */
    public static final int DEFAULT_HEIGHT = 140;

    /**
     * COMMENTME
     */
    public static final int DEFAULT_INSET = 100;

    /**
     * COMMENTME
     */
    private static final int INVALID_POSITION = -1;

    /**
     * @param owner
     */
    public NameAndColorInputDialog(final JFrame owner) {
        super(owner);
        init();
    }

    /**
     * @param owner
     */
    public NameAndColorInputDialog(final JDialog owner) {
        super(owner);
        init();
    }

    /**
     * Initilaisiert die Komponente
     */
    private void init() {
        setModal(true);
        inputArea = new LimitedSizeScrollTextPane();
        inputArea.addKeyListener(new java.awt.event.KeyListener() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == 10) {//ENTER wurde gedrückt
                    if (e.isControlDown() || e.isAltDown() || e.isShiftDown() || e.isMetaDown() || e.isAltGraphDown()) {
                        ok_button.doClick();
                    }
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {
            }

            @Override
            public void keyTyped(final KeyEvent e) {
            }
        });

        JPanel buttonpanel = new JPanel();

        DialogResourceHandler drh = new DialogResourceHandler(null);
        ok_button = new JButton(drh.getResString("ok"));
        ok_button.addActionListener(this);
        buttonpanel.add(ok_button);
        cancel_button = new JButton(drh.getResString("cancel"));
        cancel_button.addActionListener(this);
        buttonpanel.add(cancel_button);

        getContentPane().setLayout(new BorderLayout());

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(inputArea, BorderLayout.CENTER);
        getContentPane().add(centerPanel, BorderLayout.CENTER);
        getContentPane().add(buttonpanel, BorderLayout.SOUTH);
    }

    /**
     * Zeigt einen Eingabedialog an und liefert eine <code>ArrayList</code> mit den eingegebenen Werten zurück.
     *
     * @param title
     * @param defaultString
     * @param x
     * @param y
     * @param width
     * @param height
     * @param showColorChooser
     */
    public void showDialog(final String title, final String defaultString, final int x, final int y, final int width, final int height, final boolean showColorChooser) {
        setTitle(title);

        inputArea.setText(defaultString);
        inputArea.selectAll();

        if (showColorChooser) {
            if (colorChooser == null) {
                colorChooser = new JColorChooser();
                colorChooser.setColor(10);
            }
            centerPanel.add(colorChooser, BorderLayout.SOUTH);
            inputArea.setPreferredSize(new Dimension(width, height));
            setSize(getPreferredSize());
        } else {
            if (colorChooser != null) {
                centerPanel.remove(colorChooser);
            }
            setSize(width, height);
        }

        if (getOwner() != null) {
            setLocationRelativeTo(getOwner());
        }
        if (x != INVALID_POSITION) {
            setLocation(x, getLocation().y);
        }
        if (y != INVALID_POSITION) {
            setLocation(getLocation().x, y);
        }
        setVisible(true);
    }

    /**
     * Zeigt einen Eingabedialog an und liefert eine <code>ArrayList</code> mit den eingegebenen Werten zurück.
     *
     * @param title
     * @param defaultString
     * @param x
     * @param y
     * @param showColorChooser
     */
    public void showDialog(final String title, final String defaultString, final int x, final int y, final boolean showColorChooser) {
        showDialog(title, defaultString, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, showColorChooser);
    }

    /**
     * Zeigt einen Eingabedialog an und liefert eine <code>ArrayList</code> mit den eingegebenen Werten zurück.
     *
     * @param title
     * @param defaultString
     * @param showColorChooser
     */
    public void showDialog(final String title, final String defaultString, final boolean showColorChooser) {
        showDialog(title, defaultString, INVALID_POSITION, INVALID_POSITION, showColorChooser);
    }

    /**
     * Zeigt einen Eingabedialog an und liefert eine <code>ArrayList</code> mit den eingegebenen Werten zurück.
     *
     * @param title
     * @param defaultString
     * @param x
     * @param y
     */
    public void showDialog(final String title, final String defaultString, final int x, final int y) {
        showDialog(title, defaultString, x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, false);
    }

    /**
     * Zeigt einen Eingabedialog an und liefert eine <code>ArrayList</code> mit den eingegebenen Werten zurück.
     *
     * @param title
     * @param defaultString
     */
    public void showDialog(final String title, final String defaultString) {
        showDialog(title, defaultString, INVALID_POSITION, INVALID_POSITION);
    }

    /**
     * Liefert den eingegebene String (wurde Abbrechen gedrückt, ist dieser <code>null</code>)
     *
     * @return
     */
    public String getInputString() {
        return inputString;
    }

    /**
     * Liefert die ausgewählte Farbe (oder <code>null</code>, wenn keine ausgewählt wurde)
     *
     * @return
     */
    public Color getInputColor() {
        return inputColor;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == ok_button) {
            inputString = inputArea.getText();
            if (colorChooser != null) {
                inputColor = colorChooser.getColor();
            } else {
                inputColor = null;
            }
            dispose();
        }
        if (e.getSource() == cancel_button) {
            inputString = null;
            inputColor = null;
            dispose();
        }
    }

}