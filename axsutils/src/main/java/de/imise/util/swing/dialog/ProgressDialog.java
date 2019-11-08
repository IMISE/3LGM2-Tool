package de.imise.util.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * @author AXS
 * @created 11.09.2003
 */
public class ProgressDialog extends JDialog {

    /**
     * COMMENTME
     */
    private JLabel statusLabel;

    /**
     * COMMENTME
     */
    private final JPanel progressPanel = new JPanel();

    /**
     * COMMENTME
     */
    private final int barWidth;

    private int barValue = 0;

    private int barHeight = 0;

    /**
     * COMMENTME
     */
    private ProgressThread pdthread;

    /**
     * COMMENTME
     */
    private final Window owner;

    /**
     * COMMENTME
     */
    private boolean run = true;

    /**
     * COMMENTME
     */
    private final static int BAR_WIDTH = 60;

    /**
     * COMMENTME
     */
    private final static int BAR_HEIGHT = 17;

    /**
     * Thread dessen Progress der Dialog anzeigt.<br>
     * Wenn der <code>null</code> bleibt, bezieht er sich auf den Haupt-Thread. Sonst schließt sich
     * der ProgressDialog, wenn der <code>observedThread</code> nicht mehr läuft.
     */
    private Thread observedThread = null;

    ////////////////////////////////////////
    // Konstruktoren für Frames als Owner //
    ////////////////////////////////////////

    /**
     * @param owner
     * @param title
     * @param showStatusLabel
     * @param barWidth
     * @param barHeight
     */
    public ProgressDialog(final Frame owner, final String title, final boolean showStatusLabel, final int barWidth, final int barHeight) {
        super(owner, title, false);
        this.barWidth = barWidth;
        this.owner = owner;
        init(barHeight, showStatusLabel);
    }

    /**
     * @param owner
     * @param title
     * @param showStatusLabel
     * @param barWidth
     * @param barHeight
     * @param observedThread
     */
    public ProgressDialog(final Frame owner, final String title, final boolean showStatusLabel, final int barWidth, final int barHeight, final Thread observedThread) {
        this(owner, title, showStatusLabel, barWidth, barHeight);
        this.observedThread = observedThread;
    }

    /**
     * @param owner
     * @param title
     * @param showStatusLabel
     */
    public ProgressDialog(final Frame owner, final String title, final boolean showStatusLabel) {
        this(owner, title, showStatusLabel, BAR_WIDTH, BAR_HEIGHT);
    }

    /**
     * @param owner
     * @param title
     * @param showStatusLabel
     * @param observedThread
     */
    public ProgressDialog(final Frame owner, final String title, final boolean showStatusLabel, final Thread observedThread) {
        this(owner, title, showStatusLabel, BAR_WIDTH, BAR_HEIGHT, observedThread);
    }

    /**
     * @param owner
     * @param title
     */
    public ProgressDialog(final Frame owner, final String title) {
        this(owner, title, true, BAR_WIDTH, BAR_HEIGHT);
    }

    /**
     * @param owner
     */
    public ProgressDialog(final Frame owner) {
        this(owner, true);
    }

    /**
     * @param owner
     * @param showStatusLabel
     */
    public ProgressDialog(final Frame owner, final boolean showStatusLabel) {
        this(owner, new DialogResourceHandler(ProgressDialog.class).getResString("pleaseWait"), showStatusLabel, BAR_WIDTH, BAR_HEIGHT);
    }

    /**
     * @param owner
     * @param showStatusLabel
     */
    public ProgressDialog(final Dialog owner, final boolean showStatusLabel) {
        this(owner, new DialogResourceHandler(ProgressDialog.class).getResString("pleaseWait"), showStatusLabel, BAR_WIDTH, BAR_HEIGHT);
    }

    /////////////////////////////////////////
    // Konstruktoren für Dialoge als Owner //
    /////////////////////////////////////////

    /**
     * @param owner
     * @param title
     * @param showStatusLabel
     * @param barWidth
     * @param barHeight
     */
    public ProgressDialog(final Dialog owner, final String title, final boolean showStatusLabel, final int barWidth, final int barHeight) {
        super(owner, title, false);
        this.barWidth = barWidth;
        this.owner = owner;
        init(barHeight, showStatusLabel);
    }

    /**
     * @param owner
     * @param title
     * @param showStatusLabel
     */
    public ProgressDialog(final Dialog owner, final String title, final boolean showStatusLabel) {
        this(owner, title, showStatusLabel, BAR_WIDTH, BAR_HEIGHT);
    }

    /**
     * @param owner
     * @param title
     */
    public ProgressDialog(final Dialog owner, final String title) {
        this(owner, title, true, BAR_WIDTH, BAR_HEIGHT);
    }

    /**
     * @param barHeight
     * @param showStatusLabel
     */
    private void init(final int barHeight, final boolean showStatusLabel) {
        if (this.barHeight <= 0 || barHeight > 0) {
            this.barHeight = barHeight;
        }
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        getContentPane().setLayout(new BorderLayout());

        statusLabel = new JLabel();
        if (showStatusLabel) {
            statusLabel.setPreferredSize(new Dimension(getWidth(), 50));
            getContentPane().add(statusLabel, BorderLayout.CENTER);
        }

        JPanel borderPanel = new JPanel();
        borderPanel.setBorder(BorderFactory.createEtchedBorder());

        borderPanel.setPreferredSize(new Dimension(getPreferredSize().width, barHeight));
        progressPanel.setPreferredSize(new Dimension(borderPanel.getPreferredSize().width, barHeight + borderPanel.getBorder().getBorderInsets(borderPanel).left * 2));

        borderPanel.setLayout(new BorderLayout());
        borderPanel.add(progressPanel, BorderLayout.CENTER);

        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        getContentPane().add(borderPanel, BorderLayout.SOUTH);

        Dimension frameSize;
        if (showStatusLabel) {
            frameSize = new Dimension(300, owner.getInsets().top + barHeight + statusLabel.getPreferredSize().height);
        } else {
            frameSize = new Dimension(300, owner.getInsets().top + barHeight + borderPanel.getBorder().getBorderInsets(borderPanel).top * 2);
        }
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension ownerSize = owner.getSize();
        int x = owner.getLocation().x + (ownerSize.width - frameSize.width) / 2;
        int y = owner.getLocation().y + (ownerSize.height - frameSize.height) / 2;
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x + frameSize.width > screenSize.width) {
            x = screenSize.width - frameSize.width;
        }
        if (y + frameSize.height > screenSize.height) {
            y = screenSize.height - frameSize.height;
        }

        setSize(frameSize);
        setLocation(x, y);

        pdthread = new ProgressThread(this);
        setVisible(true);
        pdthread.start();
    }

    /**
     * @param text
     */
    public void setStatusLabelText(final String text) {
        statusLabel.setText(" " + text);
        update(getGraphics());
    }

    /*
     * (non-Javadoc)
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {
        run = false;
        try {
            pdthread.join();
        } catch (InterruptedException ie) {
        }
        super.dispose();
        owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        try {
            finalize();
        } catch (Throwable t) {
        }
    }

    /**
     * Wird vom Thread aufgerufen.<br>
     * Der Dialog darf hierbei nicht auf ein ableben des Threads warten.
     */
    private void disposeInternal() {
        super.dispose();
    }

    /**
     * @author AXS
     * @created on 20.08.2007
     */
    private class ProgressThread extends Thread {

        private final ProgressDialog pd;
        private Image barImage;

        private final int STEPWIDTH = 6;

        public ProgressThread(final ProgressDialog pd) {
            this.pd = pd;
        }

        @Override
        public void run() {
            Graphics g;
            barValue -= barWidth;
            // setPriority(3); //testen, welcher Wert

            barImage = createImage(STEPWIDTH + barWidth, progressPanel.getSize().height);
            g = barImage.getGraphics();
            g.setColor(Color.BLUE);
            g.fillRect(STEPWIDTH, 0, barWidth, progressPanel.getSize().height);

            g = progressPanel.getGraphics();

            Cursor ownerCursor = owner.getCursor();
            owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            pd.setCursor(owner.getCursor());

            while (run) {
                if (barValue >= progressPanel.getWidth()) {
                    barValue = 0 - barWidth;
                } else {
                    barValue += STEPWIDTH;
                }
                try {
                    g.drawImage(barImage, barValue, 0, pd);
                } catch (NullPointerException ne) {
                }
                try {
                    sleep(40);
                } catch (InterruptedException ie) {
                }
                if (pd.observedThread != null && !pd.observedThread.isAlive()) {
                    run = false;
                    pd.disposeInternal();
                    owner.setCursor(ownerCursor);
                }
            }
        }
    }

}