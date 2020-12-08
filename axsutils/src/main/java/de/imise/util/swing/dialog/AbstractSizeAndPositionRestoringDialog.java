package de.imise.util.swing.dialog;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

/**
 * Dialog, der seine Größe immer werder herstellen kann, wenn er neu geöffnet
 * wird.
 *
 * @author Ich
 * @create 12.09.2015
 */
public abstract class AbstractSizeAndPositionRestoringDialog extends JDialog {

    private static final Map<Class<? extends AbstractSizeAndPositionRestoringDialog>, Rectangle> DIALOG_CLASS_TO_SIZE_AND_POSITION_MAP = new HashMap<>();

    private static final List<AbstractSizeAndPositionRestoringDialog> openDialogs = new ArrayList<>();

    private boolean initialized = false;

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
        //add ComponentListener after restoring the old or default position
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(final ComponentEvent e) {
                if (initialized) {
                    storeSizeAndPosition();
                }
            }

            @Override
            public void componentResized(final ComponentEvent e) {
                if (initialized) {
                    storeSizeAndPosition();
                }
            }

        });

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(final WindowEvent e) {
                //unchecked cast is ok here
                AbstractSizeAndPositionRestoringDialog dialog = (AbstractSizeAndPositionRestoringDialog) e.getSource();
                openDialogs.add(dialog);

            }
            @Override
            public void windowClosed(final WindowEvent e) {
                //unchecked cas is ok here
                AbstractSizeAndPositionRestoringDialog dialog = (AbstractSizeAndPositionRestoringDialog) e.getSource();
                openDialogs.remove(dialog);
            }
        });
        restoreSizeAndPosition();
    }

    /**
     * @return the default size of this dialog
     */
    public Dimension getDefaultSize() {
        return null;
    }

    /**
     * @return the default position of this dialog. A <code>null</code> value
     *         sets the dialog in the center of its owner.
     */
    public Point getDefaultPosition() {
        return null;
    }

    /**
     * @return the offset of x and y
     */
    public int getNextDialogPositionOffset() {
        return 0;
    }

    /**
     *
     */
    private void storeSizeAndPosition() {
        Point location = getLocation();
        Dimension size = getSize();
        Rectangle sizeAndPosition = new Rectangle(location.x, location.y, size.width, size.height);
        DIALOG_CLASS_TO_SIZE_AND_POSITION_MAP.put(getClass(), sizeAndPosition);
    }

    /**
     *
     */
    protected void restoreSizeAndPosition() {
        Rectangle sizeAndPosition = DIALOG_CLASS_TO_SIZE_AND_POSITION_MAP.get(getClass());
        if (sizeAndPosition == null) {
            Dimension defaultSize = getDefaultSize();
            if (defaultSize != null) {
                setSize(defaultSize);
            } else {
                pack();
            }
            Point defaultPosition = getDefaultPosition();
            if (defaultPosition != null) {
                Window owner = getOwner();
                Point ownerLocation = owner == null ? new Point(0, 0) : owner.getLocation();
                setLocation(ownerLocation.x + defaultPosition.x, ownerLocation.y + defaultPosition.y);
            } else {
                setLocationRelativeTo(getOwner());
            }
        } else {
            setSize(sizeAndPosition.width, sizeAndPosition.height);
            setLocation(sizeAndPosition.x, sizeAndPosition.y);
        }
        int nextDialogPositionOffset = getNextDialogPositionOffset();
        if (nextDialogPositionOffset != 0) {
            Point location = getLocation();
            for (int i = 0; i < openDialogs.size(); i++) {
                JDialog dialog = openDialogs.get(i);
                Point otherLocation = dialog.getLocation();
                if (location.x == otherLocation.x && location.y == otherLocation.y) {
                    location.x += 20;
                    location.y += 20;
                    i = -1;
                }
            }
            setLocation(location);
        }
        initialized = true;
    }

    protected Dimension getLastSize() {
        Rectangle sizeAndPosition = DIALOG_CLASS_TO_SIZE_AND_POSITION_MAP.get(getClass());
        return new Dimension(sizeAndPosition.width, sizeAndPosition.height);
    }

}
