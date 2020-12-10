package de.imise.util.swing.component;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * @author ??? < (??.??.2008)
 */
public class MultiContentButton extends JButton {

    @Override
    public void setHorizontalAlignment(final int alignment) {
        int layoutAlignment;
        switch (alignment) {
        case SwingConstants.LEFT:
            layoutAlignment = FlowLayout.LEFT;
            break;
        case SwingConstants.RIGHT:
            layoutAlignment = FlowLayout.RIGHT;
            break;
        case SwingConstants.CENTER:
            layoutAlignment = FlowLayout.CENTER;
            break;
        case SwingConstants.LEADING:
            layoutAlignment = FlowLayout.LEADING;
            break;
        case SwingConstants.TRAILING:
            layoutAlignment = FlowLayout.TRAILING;
            break;
        default:
            layoutAlignment = FlowLayout.CENTER;
            break;
        }
        ((FlowLayout) getLayout()).setAlignment(layoutAlignment);
        super.setHorizontalAlignment(alignment);

    }

    /**
     * @param content
     */
    public MultiContentButton(final Component... content) {
        setLayout(new FlowLayout());
        for (int i = 0; i < content.length; i++) {
            final Component c = content[i];
            c.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {
                    processEvent(e);
                }
                @Override
                public void mouseEntered(final MouseEvent e) {
                    processEvent(e);
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    processEvent(e);
                }

                @Override
                public void mousePressed(final MouseEvent e) {
                    processEvent(e);
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    processEvent(e);
                }

                void processEvent(final MouseEvent e) {
                    e.setSource(MultiContentButton.this);
                    processMouseEvent(e);
                }
            });

            add(c);
        }
    }

}
