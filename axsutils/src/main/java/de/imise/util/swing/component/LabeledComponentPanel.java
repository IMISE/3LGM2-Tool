package de.imise.util.swing.component;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Ein Panel mit einem {@link BorderLayout}, welches ein Label vor, über, nach
 * oder unter die Center-Componente setzt.
 *
 * @author AFranz
 * @create 01.07.2010
 */
public class LabeledComponentPanel extends JPanel {

    /**
     * Die Komponete die initial im Center des Borderlayouts dieses Panels
     * gelegt wird.
     */
    private Component centerComponent;

    private final JLabel label;

    /**
     * @param label
     * @param centerComponent
     */
    public LabeledComponentPanel(final String label, final Component centerComponent) {
        this(label, centerComponent, BorderLayout.WEST);
    }

    /**
     * @param label
     * @param centerComponent
     * @param borderLayoutPosition
     */
    public LabeledComponentPanel(final String label, final Component centerComponent, final String borderLayoutPosition) {
        super();
        this.centerComponent = centerComponent;
        this.label = new JLabel(label);
        this.label.setLabelFor(centerComponent);
        add(this.label);
        add(centerComponent);
    }

    /**
     * @return
     */
    public Component getCenterComponent() {
        return centerComponent;
    }

    /**
     * @param centerComponent
     */
    public void setCenterComponent(final Component centerComponent) {
        this.centerComponent = centerComponent;
    }

    @Override
    public synchronized void addFocusListener(final FocusListener l) {
        super.addFocusListener(l);
        label.addFocusListener(l);
        centerComponent.addFocusListener(l);
    }

    @Override
    public synchronized void addMouseListener(final MouseListener l) {
        super.addMouseListener(l);
        label.addMouseListener(l);
        centerComponent.addMouseListener(l);
    }

    @Override
    public synchronized void removeFocusListener(final FocusListener l) {
        super.removeFocusListener(l);
        label.removeFocusListener(l);
        centerComponent.removeFocusListener(l);
    }

    @Override
    public synchronized void removeMouseListener(final MouseListener l) {
        super.removeMouseListener(l);
        label.removeMouseListener(l);
        centerComponent.removeMouseListener(l);
    }

}
