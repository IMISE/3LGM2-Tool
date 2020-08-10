package de.imise.util.swing.component.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * @author Ich (10.08.2020)
 */
public class FlexibleTabPaneTab extends JPanel {

    /**
     * The tabbed pane that uses this as a component
     * to display a tab.
     */
    private final JTabbedPane tabbedPane;

    /**
     * The label that displays the tab title
     */
    private final JLabel tabLabel;

    /**
     * @param tabbedPane
     */
    public FlexibleTabPaneTab(final JTabbedPane tabbedPane) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.tabbedPane = tabbedPane;
        setOpaque(false);

        tabLabel = new JLabel() {
            @Override
            public String getText() {
                int tabIndex = tabbedPane.indexOfTabComponent(FlexibleTabPaneTab.this);
                return tabIndex >= 0 ? tabbedPane.getTitleAt(tabIndex) : null;
            }
        };

        add(tabLabel);
        tabLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JButton closeButton = new CloseButton();
        closeButton.setBorder(BorderFactory.createEmptyBorder());
        add(closeButton);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

    }

    /**
     * @author Ich (10.08.2020)
     */
    private class CloseButton extends JButton {

        int w = -1;
        int h = -1;
        int sizeAndImageInsets = -1;

        /**
         *
         */
        public CloseButton() {
            //If we don't set an irrelevant default preferred size > 0 the paintComponent(g) will never be called
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    int tabIndex = tabbedPane.indexOfTabComponent(FlexibleTabPaneTab.this);
                    if (tabIndex != -1) {
                        tabbedPane.remove(tabIndex);
                    }
                }
            });
        }

        @Override
        public void updateUI() {
            //never update the buttons ui
        }

        //paint the cross
        @Override
        protected void paintComponent(final Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            if (getModel().isPressed()) {
                //shift the image for pressed buttons
                g2.translate(1, 1);
            }
            Dimension labelSize = tabLabel.getSize();
            int labelHeight = labelSize.height;
            if (w != labelHeight || h != labelHeight) {
                setPreferredSize(new Dimension(labelHeight, labelHeight));
                Font tabLabelFont = tabLabel.getFont();
                int tabLabelFontSize = tabLabelFont.getSize();
                w = labelHeight;
                h = labelHeight;
                w = w / 2 * 2;
                h = h / 2 * 2;
                sizeAndImageInsets = h - tabLabelFontSize;
            }
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }

            int x1 = sizeAndImageInsets;
            int y1 = sizeAndImageInsets;
            int x2 = w - sizeAndImageInsets;
            int y2 = h - sizeAndImageInsets;

            g2.drawLine(x1, y1, x2, y2);
            g2.drawLine(x1 + 1, y1, x2, y2 - 1);
            g2.drawLine(x1, y1 + 1, x2 - 1, y2);

            g2.drawLine(x1, y2, x2, y1);
            g2.drawLine(x1, y2 - 1, x2 - 1, y1);
            g2.drawLine(x1 + 1, y2, x2, y1 + 1);
            g2.dispose();

        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        @Override
        public void mouseEntered(final MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}