package de.imise.util.swing.component;

import static de.imise.util.swing.component.TriStateCheckBox.SelectionState.FULL;
import static de.imise.util.swing.component.TriStateCheckBox.SelectionState.HALF;
import static de.imise.util.swing.component.TriStateCheckBox.SelectionState.NOT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.UIManager;

import de.imise.util.image.ImageTools;

/**
 * Quelle:
 * https://stackoverflow.com/questions/2701817/java-swing-jcheckbox-with-3-states-full-selected-partially-selected-and-dese
 *
 * @author AXS (21.04.2021)
 */
public class TriStateCheckBox extends JCheckBox implements Icon {

    /**
     * Enum for the thre possible selction states
     *
     * @author AXS (22.04.2021)
     */
    public static enum SelectionState {
        NOT,
        HALF,
        FULL;
    }

    /** The checkbox background image in the current look and feel */
    final static Icon icon = UIManager.getIcon("CheckBox.icon");

    /**  */
    final static boolean MIDasSELECTED = true; //consider mid-state as selected ?

    private final Icon[] icons = new Icon[6];

    /**
     * If <code>true</code> all 3 states will be set in rotation if clicked. If
     * <code>false</code> an initial medium state will be set to full selected
     * and then every click only switches between full and not selected.
     */
    private final boolean rotateState;

    /**
     * Creates a checkbox with {@link #rotateState} <code>== false</code>
     */
    public TriStateCheckBox() {
        this(false);
    }

    /**
     * @param rotateState If <code>true</code> all 3 states will be set in
     *            rotation if clicked. If <code>false</code> an initial medium
     *            state will be set to full selected and then every click only
     *            switches between full and not selected.
     */
    public TriStateCheckBox(final boolean rotateState) {
        this("", rotateState);
    }

    /**
     * Creates a checkbox with {@link #rotateState} <code>== false</code>.
     *
     * @param text
     */
    public TriStateCheckBox(final String text) {
        this(text, false);
    }

    /**
     * @param text
     * @param rotateState If <code>true</code> all 3 states will be set in
     *            rotation if clicked. If <code>false</code> an initial medium
     *            state will be set to full selected and then every click only
     *            switches between full and not selected.
     */
    public TriStateCheckBox(final String text, final boolean rotateState) {
        this(text, SelectionState.NOT, rotateState);
    }

    /**
     * Creates a checkbox with {@link #rotateState} <code>== false</code>.
     *
     * @param text
     * @param selectionState
     * @param rotateState If <code>true</code> all 3 states will be set in
     *            rotation if clicked. If <code>false</code> an initial medium
     *            state will be set to full selected and then every click only
     *            switches between full and not selected.
     */
    public TriStateCheckBox(final String text, final SelectionState selectionState) {
        this(text, selectionState, false);
    }

    /**
     * @param text
     * @param selectionState
     * @param rotateState If <code>true</code> all 3 states will be set in
     *            rotation if clicked. If <code>false</code> an initial medium
     *            state will be set to full selected and then every click only
     *            switches between full and not selected.
     */
    public TriStateCheckBox(final String text, final SelectionState selectionState, final boolean rotateState) {
        // tri-state checkbox has 3 selection states: 0 unselected 1 mid-state
        // selection 2 fully selected
        super(text, selectionState != SelectionState.NOT);

        switch (selectionState) {
        case FULL:
            setSelected(true);
        case HALF:
        case NOT:
            putClientProperty("SelectionState", selectionState);
            break;
        default:
            throw new IllegalArgumentException();
        }
        ActionListener stateListener = createStateListener();
        addActionListener(stateListener);
        setIcon(this);
        this.rotateState = rotateState;
    }

    /**
     * @return
     */
    private ActionListener createStateListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                TriStateCheckBox checkBox = TriStateCheckBox.this;
                SelectionState selectionState = checkBox.getSelectionState();
                if (rotateState) {
                    selectionState = selectionState == FULL ? NOT : selectionState == HALF ? FULL : HALF;
                } else {
                    selectionState = selectionState == FULL ? NOT : FULL;
                }
                checkBox.putClientProperty("SelectionState", selectionState);
                checkBox.removeActionListener(this);
                checkBox.doClick();
                checkBox.addActionListener(this);
            }
        };
    }

    @Override
    public boolean isSelected() {
        SelectionState selectionState = getSelectionState();
        return MIDasSELECTED && selectionState != NOT;
    }

    /**
     * @return
     */
    public SelectionState getSelectionState() {
        Object selectionState = getClientProperty("SelectionState");
        return selectionState != null ? (SelectionState) selectionState : super.isSelected() ? FULL : NOT;
    }

    /**
     * @param sel
     */
    public void setSelectionState(final SelectionState selectionState) {
        putClientProperty("SelectionState", selectionState);
        fireStateChanged();
        revalidate();
        repaint();
    }

    /**
     * Sets the state depending on the booleans :)
     *
     * @param oneEnabled
     * @param oneDisabled
     */
    public void setSelectionState(final boolean oneEnabled, final boolean oneDisabled) {
        if (!oneEnabled) {
            setSelectionState(NOT);
        } else if (!oneDisabled) {
            setSelectionState(FULL);
        } else {
            setSelectionState(HALF);
        }
    }

    ////////////////////////////
    // Icon  create, set, get //
    ////////////////////////////

    @Override
    public void paintIcon(final Component c, final Graphics g, final int x, final int y) {
        if (icons[0] == null) {
            createIcons();
        }

        int iconIndex = getSelectionState().ordinal() + (isEnabled() ? 0 : 3);
        icons[iconIndex].paintIcon(c, g, x, y);
    }

    @Override
    public int getIconWidth() {
        return icon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return icon.getIconHeight();
    }

    /**
     * @param source
     * @return
     */
    private BufferedImage getSubImage(final BufferedImage source) {
        int w = source.getWidth();
        int h = source.getHeight();
        int realW = getIconWidth();
        int realH = getIconHeight();
        int diffW1 = (w - realW) / 2;
        int diffH1 = (h - realH) / 2;
        int diffW2 = (w - realW) / 2 + (w - realW) % 2;
        int diffH2 = (h - realH) / 2 + (h - realH) % 2;
        return source.getSubimage(diffW1, diffH1, source.getWidth() - diffW2, source.getHeight() - diffH2);
    }

    /**
     * @param background
     * @return
     */
    private void createIcons() {
        JCheckBox cb = new JCheckBox();

        //unchecked - enabled
        BufferedImage unchecked = getSubImage(ImageTools.createImage(cb));
        icons[0] = new ImageIcon(unchecked);

        //full checked - enabled
        cb.setSelected(true);
        BufferedImage checked = getSubImage(ImageTools.createImage(cb));
        icons[2] = new ImageIcon(checked);

        //medium checked - enabled
        cb.setSelected(false);
        Color boxColor = getBoxColor(checked, unchecked);
        BufferedImage mediumChecked = getSubImage(ImageTools.createImage(cb));
        drawRect(mediumChecked, boxColor);
        icons[1] = new ImageIcon(mediumChecked);

        //unchecked - disabled
        cb.setEnabled(false);
        unchecked = getSubImage(ImageTools.createImage(cb));
        icons[3] = new ImageIcon(unchecked);

        //full checked - disabled
        cb.setSelected(true);
        checked = getSubImage(ImageTools.createImage(cb));
        icons[5] = new ImageIcon(checked);

        //medium checked - disabled
        cb.setSelected(false);
        boxColor = getBoxColor(checked, unchecked);
        mediumChecked = getSubImage(ImageTools.createImage(cb));
        drawRect(mediumChecked, boxColor);
        icons[4] = new ImageIcon(mediumChecked);

    }

    private Color getBoxColor(final BufferedImage image1, final BufferedImage image2) {
        Color boxColor = Color.black;
        for (int x = 0; x < image1.getWidth(); x++) {
            for (int y = 0; y < image1.getHeight(); y++) {
                try {
                    int checkedRGB = image1.getRGB(x, y);
                    int uncheckedRGB = image2.getRGB(x, y);
                    if (checkedRGB != uncheckedRGB) {
                        boxColor = new Color(checkedRGB);
                        break;
                    }
                } catch (Exception e) {
                    //Sys.err1(e.getMessage());
                }
            }
        }
        return boxColor;
    }

    private void drawRect(final BufferedImage image, final Color c) {
        Graphics g = image.getGraphics();

        int w = getIconWidth();
        int h = getIconHeight();
        g.setColor(c);
        int rectSize = 4;
        g.fillRect(rectSize, rectSize, w - rectSize * 2, h - rectSize * 2);

    }

    //////////
    // Test //
    //////////

    private static void setLookAndFeel() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            } else {
                //auf dem Mac gibt es mit dem Apple-Glas-Look-And-Feel totale Probleme, wenn man viele InternalFrames
                //verwendet, was unser Tool tut. Viele beginnt hier bereits bei ca. 5. Da beginnt es schlimm zu werden
                //und ab 10 friert das Tool immer mal für ne Minute ein.
                // javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception ex) {
            System.exit(-1);
        }
    }

    public static void _main(final String[] args) {
        setLookAndFeel();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.setLayout(new GridLayout(12, 1));
        TriStateCheckBox cb1 = new TriStateCheckBox("CheckBox 1", NOT);
        TriStateCheckBox cb2 = new TriStateCheckBox("CheckBox 2", HALF);
        TriStateCheckBox cb3 = new TriStateCheckBox("CheckBox 3", FULL);
        TriStateCheckBox cb4 = new TriStateCheckBox("CheckBox 1", NOT);
        TriStateCheckBox cb5 = new TriStateCheckBox("CheckBox 2", HALF);
        TriStateCheckBox cb6 = new TriStateCheckBox("CheckBox 3", FULL);
        TriStateCheckBox cb7 = new TriStateCheckBox("CheckBox 1", NOT, true);
        TriStateCheckBox cb8 = new TriStateCheckBox("CheckBox 2", HALF, true);
        TriStateCheckBox cb9 = new TriStateCheckBox("CheckBox 3", FULL, true);
        TriStateCheckBox cb10 = new TriStateCheckBox("CheckBox 1", NOT, true);
        TriStateCheckBox cb11 = new TriStateCheckBox("CheckBox 2", HALF, true);
        TriStateCheckBox cb12 = new TriStateCheckBox("CheckBox 3", FULL, true);

        cb4.setEnabled(false);
        cb5.setEnabled(false);
        cb6.setEnabled(false);
        cb10.setEnabled(false);
        cb11.setEnabled(false);
        cb12.setEnabled(false);

        pane.add(cb1);
        pane.add(cb2);
        pane.add(cb3);
        pane.add(cb4);
        pane.add(cb5);
        pane.add(cb6);
        pane.add(cb7);
        pane.add(cb8);
        pane.add(cb9);
        pane.add(cb10);
        pane.add(cb11);
        pane.add(cb12);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

}