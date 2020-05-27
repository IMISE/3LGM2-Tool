package de.imise.tool3lgm.gui.internalframe;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import de.imise.tool3lgm.graphtools.model.GraphDocument;
import de.imise.tool3lgm.gui.GraphViewPane;
import de.imise.tool3lgm.gui.GraphViewPaneFrameComponent;
import de.imise.tool3lgm.gui.LastAndNextViewManager;
import de.imise.tool3lgm.gui.MatrixViewPaneFrameComponent;
import de.imise.tool3lgm.gui.ViewPane;
import de.imise.tool3lgm.gui.ViewPaneFrameComponent;
import de.imise.tool3lgm.gui.ViewPaneFrameComponentListener;
import de.imise.tool3lgm.gui.ViewPaneFrameComponentParent;
import de.imise.tool3lgm.gui.ViewPaneToolbarManager;
import de.imise.tool3lgm.log.Log;

/**
 * @author AXS (08.05.2020)
 */
public class MainFrameDesktopPane extends JDesktopPane implements ComponentListener, ViewPaneFrameComponentParent, InternalFrameListener {

    /** if the desktop size changes the frames will be resized too */
    private int desktopWidth = -1;
    private int desktopHeight = -1;

    /**
     *
     */
    public MainFrameDesktopPane() {
        addComponentListener(this); //resize desktop -> resize frames
    }

    /**
     *
     */
    private final List<ViewPaneFrameComponentListener> viewPaneFrameComponentListeners = new ArrayList<>();

    @Override
    public void addViewPaneFrameComponentListener(final ViewPaneFrameComponentListener listener) {
        if (!viewPaneFrameComponentListeners.contains(listener)) {
            viewPaneFrameComponentListeners.add(listener);
        }
    }

    /**
     * @param doc
     * @return
     */
    @Override
    public GraphViewPane getGraphViewPane(final GraphDocument doc) {
        JInternalFrame[] allFrames = getAllFrames();
        for (JInternalFrame frame : allFrames) {
            if (frame instanceof InternalGraphFrame) {
                InternalGraphFrame graphFrame = (InternalGraphFrame) frame;
                ViewPane viewPane = graphFrame.getViewPane();
                if (viewPane.hasGraphDocument(doc)) {
                    if (viewPane instanceof GraphViewPane) {
                        return (GraphViewPane) viewPane;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<ViewPaneFrameComponent> getViewPaneFrameComponents(final GraphDocument doc) {
        JInternalFrame[] allFrames = getAllFrames();
        List<ViewPaneFrameComponent> viewPaneFrameComponents = new ArrayList<>();
        for (JInternalFrame frame : allFrames) {
            if (frame instanceof ViewPaneFrameComponent) {
                ViewPaneFrameComponent viewPaneFrameComponent = (ViewPaneFrameComponent) frame;
                if (viewPaneFrameComponent.hasGraphDocument(doc)) {
                    viewPaneFrameComponents.add(viewPaneFrameComponent);
                }
            }
        }
        return viewPaneFrameComponents;
    }

    @Override
    public List<ViewPaneFrameComponent> getAllViewPaneFrameComponents() {
        JInternalFrame[] allFrames = getAllFrames();
        ViewPaneFrameComponent[] viewPaneFrameComponents = new ViewPaneFrameComponent[allFrames.length];
        System.arraycopy(allFrames, 0, viewPaneFrameComponents, 0, allFrames.length);
        return Arrays.asList(viewPaneFrameComponents);
    }

    @Override
    public void removeViewPaneFrameComponents(final GraphDocument doc) {
        List<ViewPaneFrameComponent> viewPaneFrameComponents = getViewPaneFrameComponents(doc);
        for (ViewPaneFrameComponent frameComponent : viewPaneFrameComponents) {
            AbstractInternalFrame frame = (AbstractInternalFrame) frameComponent;
            LastAndNextViewManager.removeWindow(frame);
            frame.dispose();
        }
    }

    @Override
    public boolean isSelected(final ViewPaneFrameComponent viewPaneFrameComponent) {
        if (viewPaneFrameComponent instanceof InternalGraphFrame) {
            InternalGraphFrame internalFrame = (InternalGraphFrame) viewPaneFrameComponent;
            return internalFrame.isSelected();
        }
        return false;
    }

    @Override
    public void setSelected(final ViewPaneFrameComponent viewPaneFrameComponent) {
        if (viewPaneFrameComponent instanceof InternalGraphFrame) {
            InternalGraphFrame internalFrame = (InternalGraphFrame) viewPaneFrameComponent;
            internalFrame.setSelected(true);
        }
    }

    //////////////////////////////////////////////
    // resize desktop -> resize internal frames //
    //////////////////////////////////////////////

    /**
     * @return all internal frames with the 0 position and max width of the desktop
     */
    private Iterable<JInternalFrame> getFramesWithMaxSize() {
        Set<JInternalFrame> framesWithMaxSize = new HashSet<>();
        for (JInternalFrame frame : getAllFrames()) {
            Point location = frame.getLocation();
            if (location.x == 0 && location.y == 0) {
                Rectangle frameBounds = frame.getBounds();
                if (frameBounds.width == desktopWidth && frameBounds.height == desktopHeight) {
                    framesWithMaxSize.add(frame);
                }
            }
        }
        return framesWithMaxSize;
    }

    /**
     * Resize all given frames to the maximum with of the desktop
     *
     * @param frames
     */
    private void setFramesToMaxSize(final Iterable<JInternalFrame> frames) {
        for (JInternalFrame frame : frames) {
            Rectangle frameBounds = frame.getBounds();
            frameBounds.width = desktopWidth;
            frameBounds.height = desktopHeight;
            frame.setBounds(frameBounds);
        }
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        Object source = e.getSource();
        if (source == this) {
            if (desktopWidth != -1) {
                Iterable<JInternalFrame> framesWithMaxSize = getFramesWithMaxSize();
                desktopWidth = getWidth();
                desktopHeight = getHeight();
                setFramesToMaxSize(framesWithMaxSize);
            } else {
                desktopWidth = getWidth();
                desktopHeight = getHeight();
            }
        }
    }

    @Override
    public void componentMoved(final ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentShown(final ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentHidden(final ComponentEvent e) {
        //do nothing
    }

    private void addView(final ViewPaneFrameComponent viewPaneFrameComponent) {
        if (viewPaneFrameComponent instanceof AbstractInternalFrame) {
            AbstractInternalFrame frame = (AbstractInternalFrame) viewPaneFrameComponent;
            frame.addInternalFrameListener(this);
            Rectangle bounds = getBounds();
            frame.setBounds(bounds);
            super.add(frame);
            frame.setLocation(0, 0);
            frame.setVisible(true);
            setSelectedFrame(frame);
        }
    }

    /**
     * @param doc
     * @return
     */
    @Override
    public GraphViewPaneFrameComponent createGraphView(final GraphDocument doc) {
        InternalGraphFrame frame = new InternalGraphFrame(doc);
        frame.addInternalFrameListener(this);
        addView(frame);
        return frame;
    }

    @Override
    public MatrixViewPaneFrameComponent createMatrixView(final GraphDocument doc, final int titleIndex, final ViewPaneToolbarManager viewPaneToolbarManager) {
        MatrixViewInternalFrame matrixView = new MatrixViewInternalFrame(doc, viewPaneToolbarManager, titleIndex);
        matrixView.addInternalFrameListener(this);
        addView(matrixView);
        return matrixView;
    }

    @Override
    public void internalFrameOpened(final InternalFrameEvent e) {
        // nothing to do
    }

    @Override
    public void internalFrameClosing(final InternalFrameEvent e) {
        for (ViewPaneFrameComponentListener l : viewPaneFrameComponentListeners) {
            AbstractInternalFrame source = (AbstractInternalFrame) e.getSource();
            l.viewClosing(source);
        }
    }

    @Override
    public void internalFrameClosed(final InternalFrameEvent e) {
        for (ViewPaneFrameComponentListener l : viewPaneFrameComponentListeners) {
            AbstractInternalFrame source = (AbstractInternalFrame) e.getSource();
            l.viewClosed(source);
        }
    }

    @Override
    public void internalFrameIconified(final InternalFrameEvent e) {
        // nothing to do
    }

    @Override
    public void internalFrameDeiconified(final InternalFrameEvent e) {
        // nothing to do
    }

    @Override
    public void internalFrameActivated(final InternalFrameEvent e) {
        for (ViewPaneFrameComponentListener l : viewPaneFrameComponentListeners) {
            AbstractInternalFrame source = (AbstractInternalFrame) e.getSource();
            l.viewActivated(source);
        }
    }

    @Override
    public void internalFrameDeactivated(final InternalFrameEvent e) {
        for (ViewPaneFrameComponentListener l : viewPaneFrameComponentListeners) {
            AbstractInternalFrame source = (AbstractInternalFrame) e.getSource();
            l.viewDeactivated(source);
        }
    }

    /**
     * ordnet alle InternalFrames neu an (überlappt)
     */
    public void reorderFramesWithOverlap() {
        JInternalFrame[] frames = getAllFrames();
        Rectangle rect = getVisibleRect();
        double height = rect.getHeight();
        double width = rect.getWidth();
        int xOffset = 10, yOffset = 10;
        int openFrameCount = 0;
        for (int n = frames.length; n > 0; n--) {
            ++openFrameCount;
            double count = openFrameCount;
            if (height - yOffset * count < 50) {
                count = (height - 50) / yOffset;
            }
            frames[n - 1].setSize((int) width - xOffset * (int) count, (int) height - yOffset * (int) count);
            frames[n - 1].setLocation(xOffset * (int) count, yOffset * (int) count);
        }
        try {
            frames[frames.length - 1].setMaximum(false);
        } catch (java.beans.PropertyVetoException evt) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), evt);
        }
    }

    /**
     * ordnet alle InternalFrames neu an (nebeneinander)
     */
    public void reorderFramesSideBySide() {
        JInternalFrame[] frames = getAllFrames();
        try {
            frames[frames.length - 1].setMaximum(false);
        } catch (java.beans.PropertyVetoException evt) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), evt);
        }
        Rectangle rect = getVisibleRect();
        double height = rect.getHeight();
        double width = rect.getWidth();
        int spalten, zeilen;
        double hilfe = Math.sqrt(frames.length);
        if ((int) hilfe * (int) hilfe == frames.length) {
            zeilen = (int) hilfe;
            spalten = (int) hilfe;
        } else {
            zeilen = (int) hilfe + 1;
            spalten = (int) hilfe;
        }
        int count = 0;
        for (int m = 0; m < zeilen - 1; m++) {
            for (int n = 0; n < spalten; n++) {
                frames[count].setBounds(0 + n * (int) width / spalten, 0 + m * (int) height / zeilen, (int) width / spalten, (int) height / zeilen);
                count++;
            }
        }
        int rest = frames.length - count;
        for (int k = count; k < frames.length; k++) {
            frames[k].setBounds(0 + (k - count) * (int) width / rest, (int) height / zeilen * (zeilen - 1), (int) width / rest, (int) height / zeilen);
        }
    }

}
