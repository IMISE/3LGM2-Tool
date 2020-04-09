package de.imise.util.io;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Dieser Stream schließt sich nicht, wenn close() aufgerufen wird. Dadurch bleibt er offen und kann zum Speichern genutzt werden. Er wird nur
 * geschlossen, wenn man die spezielle Funktion forceClose() aufruft. Das Ganze dient dazu, den Stream solange offen zu halten, wie das zugehörige
 * RandomAccessFile geöffnet ist. Externen XML-Einlesebibliotheken würden nach dem Öffnen bzw. Einlesen einer Datei den Stream schließen und das
 * Speichern geht nicht mehr.
 *
 * @author thomas (02.12.2003)
 */
public class StayOpenFileInputStream extends FileInputStream {

    /**
     * @param fileDescriptor
     */
    public StayOpenFileInputStream(final FileDescriptor fileDescriptor) {
        super(fileDescriptor);
    }

    @Override
    public void close() throws IOException {
        //do not close!
    }

    /**
     * Den Stream tatsächlich schließen.
     *
     * @throws IOException
     */
    public void forceClose() throws IOException {
        super.close();
    }

}
