package de.imise.util.swing.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;

/**
 * Dialog, der seine Größe immer werder herstellen kann, wenn er neu geöffnet wird.
 *
 * @author Ich
 * @create 12.09.2015
 */
public abstract class AbstractSizeAndPositionRestoringDialog extends JDialog {

    private static final Map<Class<? extends AbstractSizeAndPositionRestoringDialog>, Rectangle> DIALOG_CLASS_TO_SIZE_AND_POSITION_MAP = new HashMap<>();

    boolean initialized = false;

    public AbstractSizeAndPositionRestoringDialog() {
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Frame owner) {
        super(owner);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Dialog owner) {
        super(owner);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Window owner) {
        super(owner);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Frame owner, final boolean modal) {
        super(owner, modal);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Frame owner, final String title) {
        super(owner, title);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Dialog owner, final boolean modal) {
        super(owner, modal);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Dialog owner, final String title) {
        super(owner, title);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Window owner, final ModalityType modalityType) {
        super(owner, modalityType);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Window owner, final String title) {
        super(owner, title);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Frame owner, final String title, final boolean modal) {
        super(owner, title, modal);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Dialog owner, final String title, final boolean modal) {
        super(owner, title, modal);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Window owner, final String title, final ModalityType modalityType) {
        super(owner, title, modalityType);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Frame owner, final String title, final boolean modal, final GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Dialog owner, final String title, final boolean modal, final GraphicsConfiguration gc) {
        super(owner, title, modal, gc);
        init();
    }

    public AbstractSizeAndPositionRestoringDialog(final Window owner, final String title, final ModalityType modalityType, final GraphicsConfiguration gc) {
        super(owner, title, modalityType, gc);
        init();
    }

    private void init() {
        if (!initialized) {

            addComponentListener(new ComponentListener() {
                @Override
                public void componentHidden(final ComponentEvent e) {
                }

                @Override
                public void componentMoved(final ComponentEvent e) {
                    storeSizeAndPosition();
                }

                @Override
                public void componentResized(final ComponentEvent e) {
                    storeSizeAndPosition();
                }

                @Override
                public void componentShown(final ComponentEvent e) {
                }
            });

            restoreSizeAndPosition();
            initialized = true;
        }
    }

    /**
     * Liefert die Default-Größe des Dialogs
     *
     * @return
     */
    public abstract Dimension getDefaultSize();

    private void storeSizeAndPosition() {
        Point location = getLocation();
        Dimension size = getSize();
        Rectangle sizeAndPosition = new Rectangle(location.x, location.y, size.width, size.height);
        DIALOG_CLASS_TO_SIZE_AND_POSITION_MAP.put(getClass(), sizeAndPosition);
    }

    private void restoreSizeAndPosition() {
        Rectangle sizeAndPosition = DIALOG_CLASS_TO_SIZE_AND_POSITION_MAP.get(getClass());
        if (sizeAndPosition == null) {
            Dimension defaultSize = getDefaultSize();
            if (defaultSize != null) {
                setSize(defaultSize);
            }
            setLocationByPlatform(true);
        } else {
            setSize(sizeAndPosition.width, sizeAndPosition.height);
            setLocation(sizeAndPosition.x, sizeAndPosition.y);
        }
    }

    protected Dimension getLastSize() {
        Rectangle sizeAndPosition = DIALOG_CLASS_TO_SIZE_AND_POSITION_MAP.get(getClass());
        return new Dimension(sizeAndPosition.width, sizeAndPosition.height);
    }

}
