package de.imise.util.swing.component.tab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

import de.imise.util.resource.SimpleResourceBundleSourceAdapter;

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
     *
     */
    private boolean isRollover;

    /**
     * @param tabbedPane
     * @param activeColor
     */
    public FlexibleTabPaneTab(final JTabbedPane tabbedPane, final Color activeForegroundColor) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.tabbedPane = tabbedPane;
        setOpaque(false);

        tabLabel = new JLabel() {
            @Override
            public String getText() {
                int tabIndex = getTabIndex();
                return tabIndex >= 0 ? tabbedPane.getTitleAt(tabIndex) : null;
            }

            @Override
            public Color getForeground() {
                int tabIndex = getTabIndex();
                int selectedIndex = getSelectedTabIndex();
                return tabIndex == selectedIndex ? activeForegroundColor : super.getForeground();
            }

            @Override
            public String getToolTipText() {
                return FlexibleTabPaneTab.this.getToolTipText();
            }

        };

        //we must set an irrelevant  dummy tooltip to enable the
        //showing if tooltips on the label. Without that the
        //tabLabel.getTooltipText() is never called.
        tabLabel.setToolTipText("Dummy Tooltip");

        add(tabLabel);
        tabLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JButton closeButton = new CloseButton();
        add(closeButton);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        addMouseListener(mouseListener);
        tabLabel.addMouseListener(mouseListener);
        closeButton.addMouseListener(mouseListener);
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
            SimpleResourceBundleSourceAdapter resourceHandler = new SimpleResourceBundleSourceAdapter(FlexibleTabPaneTab.class);
            String tooltip = resourceHandler.getResString("close_tab");
            setToolTipText(tooltip);
            //the BasicButtonUI enables rollover and mouse clicked events
            setUI(new BasicButtonUI());
            //transparent button -> buttons backgound == tab background
            setContentAreaFilled(false);
            setFocusable(false);
            setRolloverEnabled(true);
            setBorderPainted(false);
            //close tab when clicking the button
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    int tabIndex = getTabIndex();
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
                //after resetting the preferred size we must
                //revalidate and repaint the whole tab to
                //ensure the buttons first paint has the
                //correct size data
                revalidate();
                repaint();
                return;
            }
            if (isRollover || isSelectedTab()) {
                if (getModel().isPressed()) {
                    //shift the image for pressed buttons
                    g2.translate(1, 1);
                }
                if (getModel().isRollover()) {
                    //this isRollover() is only for the button. The global
                    //variable isRollover is for the whole tab inclusive the
                    //border, label and button.
                    g2.setColor(Color.MAGENTA);
                } else {
                    g2.setColor(Color.BLACK);
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

    }

    @Override
    public String getToolTipText() {
        int tabIndex = getTabIndex();
        return tabIndex >= 0 ? tabbedPane.getToolTipTextAt(tabIndex) : null;
    }

    /**
     * @return
     */
    private final int getTabIndex() {
        int tabIndex = tabbedPane.indexOfTabComponent(FlexibleTabPaneTab.this);
        return tabIndex;
    }

    /**
     * @return
     */
    private final int getSelectedTabIndex() {
        int selectedTabIndex = tabbedPane.getSelectedIndex();
        return selectedTabIndex;
    }

    /**
     * @return
     */
    private boolean isSelectedTab() {
        int tabIndex = getTabIndex();
        int selectedTabIndex = getSelectedTabIndex();
        return tabIndex == selectedTabIndex;
    }

    /**
     * @param index
     */
    private void setSelectedTabIndex(final int index) {
        tabbedPane.setSelectedIndex(index);
    }

    /**
     *
     */
    private final MouseListener mouseListener = new MouseListener() {

        @Override
        public void mouseReleased(final MouseEvent e) {
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            //if we add this mouseListener to this Jpanel component
            //the default behaviour of selecting the clicked tab
            //is deactivated. This fixes it.
            int tabIndex = getTabIndex();
            setSelectedTabIndex(tabIndex);
        }

        @Override
        public void mouseClicked(final MouseEvent e) {
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            isRollover = true;
            revalidate();
            repaint();
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            isRollover = false;
            revalidate();
            repaint();
        }
    };
}