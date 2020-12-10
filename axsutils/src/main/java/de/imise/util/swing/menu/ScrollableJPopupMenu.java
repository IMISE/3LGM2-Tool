package de.imise.util.swing.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Nicht das hier nehmen, sondern den {@link MenuScroller}!
 *
 * @author Ich
 */
public class ScrollableJPopupMenu extends JPopupMenu implements ChangeListener, MouseWheelListener {

    /**
     *
     */
    private JMenuItem selectedItem = null;

    /**
     *
     */
    public ScrollableJPopupMenu() {
        super();
        //		addMouseWheelListener(this);
    }

    /**
     * @param label
     */
    public ScrollableJPopupMenu(final String label) {
        super(label);
    }

    @Override
    public void show(final Component invoker, final int x, final int y) {
        super.show(invoker, x, y);
    }

    @Override
    public JMenuItem add(final Action a) {
        JMenuItem item = super.add(a);
        item.addChangeListener(this);
        return item;
    }

    @Override
    public JMenuItem add(final JMenuItem menuItem) {
        JMenuItem item = super.add(menuItem);
        item.addChangeListener(this);

        return item;
    }

    @Override
    public JMenuItem add(final String s) {
        JMenuItem item = super.add(s);
        item.addChangeListener(this);
        return item;
    }

    private ScrollThread scrollThread = null;

    @Override
    public void stateChanged(final ChangeEvent e) {
        //		System.err.println("stateChanged " + e);
        if (e.getSource() == selectedItem) {
            return;
        }
        selectedItem = (JMenuItem) e.getSource();
        if (getYShift() != 0) {
            if (scrollThread == null) {
                scrollThread = new ScrollThread();
                scrollThread.start();
            }
        }
    }

    private JMenuItem nextItem = null;

    /**
     * Liefert den Pixel-Wert, um den das Popupmenü verschoben werden müsste,
     * damit man den nicht sichtbaren Eintrag dirkt über oder unter dem
     * selektierten Eintrag sehen kann. Wenn beide angrenzenden Menüitems
     * vollständig sichtbar sind, kommt 0 zurück.
     *
     * @return
     */
    private int getYShift() {
        if (getSize().height <= Toolkit.getDefaultToolkit().getScreenSize().height) {
            return 0;
        }
        int i = getComponentIndex(selectedItem);

        nextItem = null;
        if (i > 0) {
            // prüfen, ob das aktuell selektierte Item ganz oben das letzte sichtbare Item ist und darüber noch mind ein
            // weiteres kommt, das in den schtbaren Bereich gescrollt werden müsste
            for (int j = i - 1; j >= 0; j--) {
                // Separatoren übergehen
                Component subcomponent = getComponent(j);
                if (!(subcomponent instanceof JMenuItem)) {
                    continue;
                }
                nextItem = (JMenuItem) subcomponent;
                // wenn das vorherige Item nicht vollständig sichtbar ist
                int y = nextItem.getY();
                Rectangle bounds = getBounds();
                int pos = y + bounds.y;
                if (pos < 0) {
                    y = selectedItem.getY();
                    y -= nextItem.getY();
                    return y;
                }
                break;
            }
        }
        int componentCount = getComponentCount();
        if (i < componentCount) {
            // prüfen, ob das aktuell selektierte Item ganz unten das letzte sichtbare Item ist und danach noch mind ein
            // weiteres kommt
            for (int j = i + 1; j < componentCount; j++) {
                // Separatoren übergehen
                Component component = getComponent(j);
                if (!(component instanceof JMenuItem)) {
                    continue;
                }
                nextItem = (JMenuItem) component;
                // wenn das nächste Item nicht vollständig sichtbar ist
                int y = nextItem.getY();
                int h = nextItem.getHeight();
                Rectangle bounds = getBounds();
                int pos = y + h + bounds.y;
                Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
                Dimension screenSize = defaultToolkit.getScreenSize();
                if (pos > screenSize.height) {
                    return screenSize.height - pos;
                }
                break;
            }
        }
        return 0;
    }

    /**
     * Scrollt ein Menü solange nach oben oder unten bis er angehalten wird oder
     * es nichts mehr zu scrollen gibt.
     */
    private class ScrollThread extends Thread {

        /**
         *
         */
        public ScrollThread() {
            super();
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = 0;
            int yshift = getYShift();
            while (yshift != 0) {
                i++;
                setBounds(getBounds().x, getBounds().y + yshift, getBounds().width, getBounds().height);
                selectedItem.setArmed(false);
                //hierdurch wird stateChanged() ausgeloest -> selectedItem ist das alte nextItem -> selectedItem gleich wieder
                //deselektieren, damit bei schenellen Mausbewegungen das letzte oder erste Element nicht einfach selektiert bleibt
                nextItem.setArmed(true);
                selectedItem.setArmed(false);
                int sleep = 100 - i * 5;
                if (sleep < 0) {
                    sleep = 0;
                }
                try {
                    sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                yshift = getYShift();
                selectedItem.setArmed(false);
            }
            scrollThread = null;
        }

    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.
     * MouseWheelEvent)
     */
    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        //	    System.err.println("mouseWheelMoved " + e);
    }

}
