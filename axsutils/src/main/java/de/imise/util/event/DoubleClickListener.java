package de.imise.util.event;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;

public class DoubleClickListener extends MouseAdapter {

    private final JButton button;

    public DoubleClickListener(final Action action) {
        this(new JButton(action));
    }

    public DoubleClickListener(final JButton button) {
        this.button = button;
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        if (e.getClickCount() > 1) {
            Object source = e.getSource();
            if (source instanceof JList) {
                JList<?> list = (JList<?>) source;
                if (list.getSelectedIndex() >= 0 && list.getSelectedIndex() == list.locationToIndex(e.getPoint())) {
                    button.doClick();
                }
            } else {
                button.doClick();
            }
        }
    }

}
