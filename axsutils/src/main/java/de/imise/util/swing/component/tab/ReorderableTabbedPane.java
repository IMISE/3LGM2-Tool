package de.imise.util.swing.component.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.event.MouseInputAdapter;

/**
 * Ein TabPane, bei dem man die Tabs per Maus-Drag in ihrer Reihenfolge ändern
 * kann. Außerdem hat es eine Funktion, über die man einfach per boolean
 * umstellen kann, ob die Tabs in einer Zeile dargestellt werden sollen oder
 * gewrapped werden, wenn die Zeile nicht mehr ausreicht.
 * http://forum.java.sun.com/thread.jsp?thread=263180&forum=57&message=2281801<br>
 * XTabbedPane<br>
 * funktioniert nicht mit SCROLL_TAB_LAYOUT
 *
 * @author dsmalley (Dave)
 */
public class ReorderableTabbedPane extends FlexibleTabPane {

    /**
     *
     */
    private Cursor defaultCursor;

    /**
     *
     */
    private Cursor handCursor;

    /**
     *
     */
    public ReorderableTabbedPane() {
        this(TOP);
    }

    /**
     * @param tabPlacement
     */
    public ReorderableTabbedPane(final int tabPlacement) {
        this(tabPlacement, WRAP_TAB_LAYOUT);
    }

    /**
     * @param tabPlacement
     * @param tabLayoutPolicy
     */
    public ReorderableTabbedPane(final int tabPlacement, final int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
        initMouseHandler();
    }

    /**
     * @param withCloseButtons
     */
    public ReorderableTabbedPane(final boolean withCloseButtons) {
        super(withCloseButtons);
        initMouseHandler();
    }

    /**
     * @param activeTabForegroundColor
     * @param activeTabFontStyle
     * @param withCloseButtons
     */
    public ReorderableTabbedPane(final Color activeTabForegroundColor, final int activeTabFontStyle, final boolean withCloseButtons) {
        super(activeTabForegroundColor, activeTabFontStyle, withCloseButtons);
        initMouseHandler();
    }

    /**
     * @param activeTabForegroundColor
     * @param activeTabFontStyle
     */
    public ReorderableTabbedPane(final Color activeTabForegroundColor, final int activeTabFontStyle) {
        super(activeTabForegroundColor, activeTabFontStyle);
        initMouseHandler();
    }

    /**
     * @param tabPlacement
     * @param activeTabForegroundColor
     * @param activeTabFontStyle
     * @param withCloseButtons
     */
    public ReorderableTabbedPane(final int tabPlacement, final Color activeTabForegroundColor, final int activeTabFontStyle, final boolean withCloseButtons) {
        super(tabPlacement, activeTabForegroundColor, activeTabFontStyle, withCloseButtons);
        initMouseHandler();
    }

    /**
     * @param tabPlacement
     * @param tabLayoutPolicy
     * @param activeTabForegroundColor
     * @param activeTabFontStyle
     * @param withCloseButtons
     */
    public ReorderableTabbedPane(final int tabPlacement, final int tabLayoutPolicy, final Color activeTabForegroundColor, final int activeTabFontStyle, final boolean withCloseButtons) {
        super(tabPlacement, tabLayoutPolicy, activeTabForegroundColor, activeTabFontStyle, withCloseButtons);
        initMouseHandler();
    }

    /**
     *
     */
    private void initMouseHandler() {
        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    /**
     * @param dragIndex
     * @param tabIndex
     */
    private void dragTab(final int dragIndex, final int tabIndex) {
        String title = getTitleAt(dragIndex);
        Icon icon = getIconAt(dragIndex);
        Component component = getComponentAt(dragIndex);
        String toolTipText = getToolTipTextAt(dragIndex);
        Color background = getBackgroundAt(dragIndex);
        Color foreground = getForegroundAt(dragIndex);
        Icon disabledIcon = getDisabledIconAt(dragIndex);
        int mnemonic = getMnemonicAt(dragIndex);
        int displayedMnemonicIndex = getDisplayedMnemonicIndexAt(dragIndex);
        boolean enabled = isEnabledAt(dragIndex);
        remove(dragIndex);
        insertTab(title, icon, component, toolTipText, tabIndex);
        setBackgroundAt(tabIndex, background);
        setForegroundAt(tabIndex, foreground);
        setDisabledIconAt(tabIndex, disabledIcon);
        setMnemonicAt(tabIndex, mnemonic);
        setDisplayedMnemonicIndexAt(tabIndex, displayedMnemonicIndex);
        setEnabledAt(tabIndex, enabled);
    }

    /**
     * @return
     */
    private Cursor getDefaultCursor() {
        if (defaultCursor == null) {
            defaultCursor = Cursor.getDefaultCursor();
        }
        return defaultCursor;
    }

    /**
     * @return
     */
    private Cursor getHandCursor() {
        if (handCursor == null) {
            handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
        return handCursor;
    }

    /**
     * @param x
     * @param y
     * @return
     */
    private int getTabIndex(final int x, final int y) {
        return getUI().tabForCoordinate(this, x, y);
    }

    /**
     *
     */
    private void maybeSetDefaultCursor() {
        Cursor cursor = getDefaultCursor();
        if (getCursor() != cursor) {
            setCursor(cursor);
        }
    }

    /**
     *
     */
    private void maybeSetHandCursor() {
        Cursor cursor = getHandCursor();
        if (getCursor() != cursor) {
            setCursor(cursor);
        }
    }

    /**
     * @author N.N. (??.??.20??)
     */
    class MouseHandler extends MouseInputAdapter {

        private int dragIndex = -1;

        @Override
        public void mouseDragged(final MouseEvent e) {
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            //			if (!e.isPopupTrigger() && e.getButton() == MouseEvent.BUTTON1) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                int tabIndex = getTabIndex(e.getX(), e.getY());
                if (tabIndex != -1) {
                    dragIndex = tabIndex;
                    maybeSetHandCursor();
                }
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            //			if (!e.isPopupTrigger() && e.getButton() == MouseEvent.BUTTON1) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (dragIndex != -1) {
                    int tabIndex = getTabIndex(e.getX(), e.getY());
                    if (tabIndex != -1 && tabIndex != dragIndex) {
                        dragTab(dragIndex, tabIndex);
                        setSelectedIndex(tabIndex);
                    }
                    dragIndex = -1;
                }
            }
            maybeSetDefaultCursor();
        }

    }

    /**
     * @param b
     */
    public void setTabsInOneLineLayout(final boolean b) {
        if (b) {
            setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        } else {
            setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        }
    }

    @Override
    public void fireStateChanged() {
        super.fireStateChanged();
    }

}