package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import de.imise.util.resource.SimpleResourceIconSource;

/**
 * @author AXS (19.05.2020)
 */
public class CenterableScrollPane extends JScrollPane {

    /**
     * COMMENTME
     */
    private JButton centerViewButton;

    /**
     *
     */
    public CenterableScrollPane() {
        init();
    }

    /**
     * @param view
     */
    public CenterableScrollPane(final Component view) {
        super(view);
        init();
    }

    /**
     * @param vsbPolicy
     * @param hsbPolicy
     */
    public CenterableScrollPane(final int vsbPolicy, final int hsbPolicy) {
        super(vsbPolicy, hsbPolicy);
        init();
    }

    /**
     * @param view
     * @param vsbPolicy
     * @param hsbPolicy
     */
    public CenterableScrollPane(final Component view, final int vsbPolicy, final int hsbPolicy) {
        super(view, vsbPolicy, hsbPolicy);
        init();
    }

    private void init() {
        JViewport viewport = getViewport();
        viewport.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        //viewport.setScrollMode(JViewport.BLIT_SCROLL_MODE );
        //viewport.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        horizontalScrollBar.setUnitIncrement(10); //better default
        verticalScrollBar.setUnitIncrement(10); //better default
        ImageIcon centerIcon = SimpleResourceIconSource.getIcon(getClass(), "zent");
        centerViewButton = new JButton(centerIcon);
        centerViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                JViewport viewport = getViewport();
                Rectangle vp = viewport.getViewRect();
                Component view = viewport.getView();
                int vw = view.getWidth();
                int vh = view.getHeight();
                double vpw = vp.getWidth();
                double vph = vp.getHeight();
                int x = (int) (vw - vpw) / 2;
                int y = (int) (vh - vph) / 2;
                Point center = new Point(x, y);
                viewport.setViewPosition(center);
            }
        });
        setCorner(JScrollPane.LOWER_RIGHT_CORNER, centerViewButton);
    }

}
