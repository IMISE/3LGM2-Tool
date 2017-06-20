package de.imise.tool3lgm.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Die Statusbar der Anwendung.
 * Hier können nach Belieben bei Ereignissen Informationen ausgegeben werden.
 * 
 * @author AXS
 */
public class StatusBar extends JPanel {

    /**
     * Label, welches den aktuell belegten Speicher anzeigt.
     */
    MemoryLabel usedMemoryLabel = new MemoryLabel();

    /**
     * Erzeugt eine neue Statusbar
     */
    public StatusBar() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());
        add(new MemoryLabel(), BorderLayout.EAST);
    }

    /**
     * Label, welches die Speicherwerte formatiert anzeigt.
     * 
     * @author AXS
     */
    private class MemoryLabel extends JLabel {

        /**
         * <code>StringBuilder</code>, in dem immer der neue <code>String</code> zusammengebaut wird.
         */
        StringBuilder sb = new StringBuilder();

        /**
         * Die <code>Runtime</code> der Anwendung. Wenn man sie sich hier merkt,
         * muss sie nicht bei jedem updaten des Lables neu geholt werden.
         */
        Runtime runtime = Runtime.getRuntime();

        /**
         * Die Speicherwerte des Systems kommen in Bytes zurück. Bei Teilung durch
         * diesem Faktor werden daraus Kilobytes.
         */
        long factor = 1024l;

        /**
         * Zeit in Millisekunden, die zwischen 2 Updates der Speicherwerte vergehen soll.
         */
        long delay = 1000;

        /**
         * Erzeugt eine neue Instanz eines Labels zur Anzeige des freien Speichers
         * und startet einen Tread, der alle nach einer Periode von <code>delay</code> Millisekunden die Anzeige aktualisiert.
         */
        public MemoryLabel() {
            super();
            new LabelUpdater().start();
        }

        /**
         * Fragt von der Runtime die aktuellen Speicherwerte ab und setzt sie als Labeltext.
         */
        public void updateMemoryValues() {
            sb.setLength(0);
            sb.append("free: ");
            sb.append(runtime.freeMemory() / factor);
            sb.append("kB / total: ");
            sb.append(runtime.totalMemory() / factor);
            sb.append("kB / max: ");
            sb.append(runtime.maxMemory() / factor);
            sb.append("kB ");
            setText(sb.toString());
        }

        /**
         * Ein Thread, der das MemoryLabel nach dem Intervall von <code>delay</code> Millisekunden updated.
         * 
         * @author AXS
         */
        private class LabelUpdater extends Thread {

            /**
             * Erzeugt eine neuen Update-Thread mit der niedrigsten Priorität.
             */
            public LabelUpdater() {
                super();
                setPriority(Thread.MIN_PRIORITY);
            }

            /*
             * (Kein Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
                while (true) {
                    updateMemoryValues();
                    try {
                        sleep(delay);
                    } catch (Exception e) {
                    }
                }
            }
        }

    }
}
