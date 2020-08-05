package de.imise.util.swing.component.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

/**
 * Ein TabPane, bei dem man die Tabs per Maus-Drag in ihrer Reihenfolge ändern kann.
 * Außerdem hat es eine Funktion, über die man einfach per boolean umstellen kann, ob die
 * Tabs in einer Zeile dargestellt werden sollen oder gewrapped werden, wenn die Zeile
 * nicht mehr ausreicht.
 * http://forum.java.sun.com/thread.jsp?thread=263180&forum=57&message=2281801<br>
 * XTabbedPane<br>
 * funktioniert nicht mit SCROLL_TAB_LAYOUT
 * 
 * @author dsmalley (Dave)
 */
public class TabbedPane extends JTabbedPane {

    public TabbedPane() {
        this(TOP);
    }

    public TabbedPane(final int tabPlacement) {
        this(tabPlacement, WRAP_TAB_LAYOUT);
    }

    public TabbedPane(final int tabPlacement, final int tabLayoutPolicy) {
        super(tabPlacement, tabLayoutPolicy);
        mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private final MouseInputListener mouseHandler;
    private Cursor defaultCursor, handCursor;

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

    private Cursor getDefaultCursor() {
        if (defaultCursor == null) {
            defaultCursor = Cursor.getDefaultCursor();
        }
        return defaultCursor;
    }

    private Cursor getHandCursor() {
        if (handCursor == null) {
            handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
        return handCursor;
    }

    private int getTabIndex(final int x, final int y) {
        return getUI().tabForCoordinate(this, x, y);
    }

    private void maybeSetDefaultCursor() {
        Cursor cursor = getDefaultCursor();
        if (getCursor() != cursor) {
            setCursor(cursor);
        }
    }

    private void maybeSetHandCursor() {
        Cursor cursor = getHandCursor();
        if (getCursor() != cursor) {
            setCursor(cursor);
        }
    }

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