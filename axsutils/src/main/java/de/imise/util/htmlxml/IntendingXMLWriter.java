package de.imise.util.htmlxml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.common.base.Strings;

import javanet.staxutils.IndentingXMLStreamWriter;

/**
 * Allgemeiner XML-Writer der eingerückte XML-Dateien schreibt und bei Bedarf
 * zippen kann.
 *
 * @author AXS (11.08.2017)
 */
public class IntendingXMLWriter {

    /**
     * Der eigentliche Writer, der hier nur gekapselt wird, um ein paar
     * convenience-Funktionen zu haben.
     */
    private final XMLStreamWriter writer;

    /**
     * Der OutStream in den der XMLStreamWriter schreibt. Schreibt man Chars
     * über den XMLStreamWriter, dann werden sie immer escaped. Schreibt man
     * direkt in den outStream, wird jedes Zeichen so geschrieben, wie man es
     * übergibt, also unescaped.
     *
     * @see #writeCharactersUnescaped(String)
     */
    private OutputStream outStream;

    /**
     * Soll die geschriebene XML-Datei gezipped werden, dann wird dieser Stream
     * initialisiert und der OutputStream outStream oben ist dasselbe Objekt.
     * Wenn nicht gezippt werden soll bleibt diese Variable hier
     * <code>null</code> und der OutputStream outStream bleibt ein normaler
     * FileOutputStream, der ungezippte Daten rausschreibt.
     */
    private ZipOutputStream zipOutStream;

    /**
     * Legt einen neuen XML-Writer an, der alle Einträge einrückt. Wird ein
     * nicht leerer oder nicht null zipEntryName angegeben, dann wird die Datei
     * zusätzlich in eine zip-Datei gepackt und der innere Dateiname ist der
     * zipEntryName.
     *
     * @param file Name der zu schreibenden Datei
     * @param zipEntryName ist dieser String nicht leer oder <code>null</code>,
     *            wird die entstehende XML-Datei in eine Zip-Datei gepackt und
     *            die innen liegende Datei hat den Namen dieses Strings.
     * @throws XMLStreamException
     * @throws IOException
     */
    protected IntendingXMLWriter(final File file, final String zipEntryName) throws XMLStreamException, IOException {
        outStream = new FileOutputStream(file);
        outStream = new BufferedOutputStream(outStream, 1024 * 1024); // 1 MB Buffer
        if (!Strings.isNullOrEmpty(zipEntryName)) {
            zipOutStream = getOpenZipOutputStream(outStream, zipEntryName);
            outStream = zipOutStream;
        }
        XMLStreamWriter tmpwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outStream, "UTF-8");
        writer = new IndentingXMLStreamWriter(tmpwriter);
    }

    /**
     * Muss am Ende des Schreibens aufgerufen werden. Beendet das Dokument und
     * schließt alle Streams.
     *
     * @throws XMLStreamException
     * @throws IOException
     */
    protected final void finish() throws XMLStreamException, IOException {
        writer.writeEndDocument();
        writer.flush();
        writer.close();
        if (zipOutStream != null) {
            zipOutStream.finish(); //schließt auch den Entry, so dass die Zip-Datei gültig wird
        }
        outStream.close();
    }

    /**
     * Initialisiert einen neuen ZipOutputStream.
     *
     * @param baseStream OutputStream der gezippt werden soll, der also vom
     *            erzeugten ZipOutputStream gekapselt wird.
     * @param zipEntryName Name des Zip-Entrages (also Ordner) in der Zip-Datei.
     *            Alle Zip-Dateien, die dieser Writer schreibt, haben immer nur
     *            einen Eintrag.
     * @return ZipOutputStream um den übergebenen OutputStream
     * @throws IOException
     */
    private ZipOutputStream getOpenZipOutputStream(final OutputStream baseStream, final String zipEntryName) throws IOException {
        ZipOutputStream zipStream = new ZipOutputStream(baseStream);
        zipStream.setMethod(ZipOutputStream.DEFLATED);
        zipStream.setLevel(9);
        ZipEntry entry = new ZipEntry(zipEntryName);
        entry.setMethod(ZipEntry.DEFLATED);
        zipStream.putNextEntry(entry);
        return zipStream;
    }

    /**
     * Schreibt den XML-Dokumenten-Start. Z.B. "<?xml version="1.0"
     * encoding="UTF-8"?>" mit dem Parameter Version "1.0" und Encoding "UTF-8"
     *
     * @param encoding Encoding der XML-Datei. Üblicherweise ist das "UTF-8"
     * @param version Version der XML-Datei. Üblicherweise ist das "1.0"
     * @throws XMLStreamException
     * @see {@link XMLStreamWriter#writeStartDocument(String, String)}
     */
    protected final void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        writer.writeStartDocument(encoding, version);
    }

    /**
     * Schreibt den XML-Dokumenten-Start. Z.B. "<?xml version="1.0"
     * encoding="UTF-8" standalone="yes"?>" mit dem Parameter Version "1.0",
     * Encoding "UTF-8" und standalone "true". Der Standalone-Parameter ist in
     * der Regel irrelevant. Der Default ist <code>false</code>, also man kann
     * den Standalone-Parameter auch weglassen und gleich über die Funktion
     * {@link #writeStartDocument(String, String)} gehen.
     *
     * @param encoding Encoding der XML-Datei. Üblicherweise ist das "UTF-8"
     * @param version Version der XML-Datei. Üblicherweise ist das "1.0"
     * @param standalone Wenn weiterverabeitende Parser diesen
     *            Validierungsparamter brauchen, kann man ihn schreiben, wenn er
     *            <code>true</code> sein muss.
     * @throws XMLStreamException
     * @throws IOException
     */
    protected final void writeStartDocument(final String encoding, final String version, final boolean standalone) throws XMLStreamException, IOException {
        String standAlone = standalone ? "yes" : "no";
        writeCharactersUnescaped("<?xml version=\"" + version + "\" encoding=\"" + encoding + "\" standalone=\"" + standAlone + "\"?>\n");
    }

    /**
     * Schreibt ein öffnendes Tag mit den angegebenen Attributen. Die Attribute
     * müssen immer abwechselnd als Attribut-Name und Attribut-Value übergeben
     * werden. Eine ungerade Anzahl an Attributen führt zu einer Exception.<br>
     * Z.B.:
     * <ol>
     * <li>Übergibt man als element "person" und keine Attribute, wird in die
     * XMl-Datei "&ltperson&gt" geschrieben.</li>
     * <li>Übergibt man als element "person" und als Attribute ["id", "1234"],
     * wird in die XMl-Datei "&ltperson id="1234"&gt" geschrieben.</li>
     * </ol>
     *
     * @param element Name des zu schreibenden Tags
     * @param attributes Attribute-Names und Attribute-Values, die dem Tag
     *            angehängt werden sollen
     * @throws XMLStreamException
     * @see {@link XMLStreamWriter#writeStartElement(String)}
     */
    protected final void writeStartElement(final String element, final String... attributes) throws XMLStreamException {
        writer.writeStartElement(element);
        writeAttributes(attributes);
    }

    /**
     * Hängt dem letzten geöffneten Tag die übergebenen Attribute an. Die
     * Attribute müssen immer abwechselnd als Attribut-Name und Attribut-Value
     * übergeben werden. Eine ungerade Anzahl an Attributen führt zu einer
     * Exception.<br>
     * Z.B.: ist das zuletzt über die Funktion
     * {@link #writeStartElement(String, String...)} geöffnete Tag "person" und
     * übergibt man hier als Attribute ["id", "1234"], wird in die XMl-Datei
     * "&ltperson id="1234"&gt" geschrieben. Weitere Aufrufe dieser Funtkion
     * hängen weitere Attribute an das zuletzt geöffnete Tag an.
     *
     * @param attributes Attribute-Names und Attribute-Values, die dem Tag
     *            angehängt werden sollen
     * @throws XMLStreamException
     * @see {@link #writeAttribute(Object, Object)}
     */
    protected void writeAttributes(final String... attributes) throws XMLStreamException {
        for (int i = 0; i < attributes.length; i += 2) {
            //übergebene Attribute, bei denen der Value null ist, werden nicht geschrieben.
            writeAttribute(attributes[i], attributes[i + 1]);
        }
    }

    /**
     * Schließt das zuletzt geöffnete Tag. Delegate für
     * {@link XMLStreamWriter#writeEndElement()}
     *
     * @throws XMLStreamException
     * @see {@link XMLStreamWriter#writeEndElement()}
     */
    protected final void writeEndElement() throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * Schreibt ein Tag, das keine weiteren Unter-Tags haben kann, da es sofort
     * geschlossen wird. Falls Attribute übergeben werden, dann werden diese
     * angehängt.
     *
     * @param element Name des zu schreibenden Tags
     * @param attributes Attribute-Names und Attribute-Values, die dem Tag
     *            angehängt werden sollen
     * @throws XMLStreamException
     * @see {@link XMLStreamWriter#writeEmptyElement(String)}
     * @see #writeAttributes(String...)
     */
    protected final void writeEmptyElement(final String element, final String... attributes) throws XMLStreamException {
        writer.writeEmptyElement(element);
        writeAttributes(attributes);
    }

    /**
     * Schreibt ein öffnendes und schließendes Tag und dazwischen den
     * übergebenen Text. Z.B. element ist "person" und text ist "Diese Person
     * ist komisch!" ergibt in der XML-Datei "&ltperson&gtDiese Person ist
     * komisch!&lt/person&gt".
     *
     * @param element Name des Tags
     * @param text Text zwischen dem öffnenden und dem schließenden Tag
     * @throws XMLStreamException
     */
    protected final void writeElement(final String element, final String text) throws XMLStreamException {
        writer.writeStartElement(element);
        writer.writeCharacters(text);
        writer.writeEndElement();
    }

    /**
     * Writes an opening and closing tag and in between the passed boolean.
     *
     * @param element Name des Tags
     * @param bool Boolean, dessen String-Value zwischen das öffnende und
     *            schließende Tag geschrieben wird (<code>true</code> oder
     *            <code>false</code>)
     * @throws XMLStreamException
     */
    protected final void writeElement(final String element, final boolean bool) throws XMLStreamException {
        writeElement(element, String.valueOf(bool));
    }

    /**
     * Writes an opening and closing tag and in between the passed boolean. But
     * the boolean is only written if it is <code>false</code>.
     *
     * @param element Name des Tags
     * @param bool Boolean, dessen String-Value zwischen das öffnende und
     *            schließende Tag geschrieben wird (<code>false</code>)
     * @throws XMLStreamException
     */
    protected final void writeElementIfFalse(final String element, final boolean bool) throws XMLStreamException {
        if (!bool) {
            writeElement(element, bool);
        }
    }

    /**
     * Writes an opening and closing tag and in between the passed boolean. But
     * the boolean is only written if it is <code>true</code>.
     *
     * @param element Name des Tags
     * @param bool Boolean, dessen String-Value zwischen das öffnende und
     *            schließende Tag geschrieben wird (<code>true</code>)
     * @throws XMLStreamException
     */
    protected final void writeElementIfTrue(final String element, final boolean bool) throws XMLStreamException {
        if (bool) {
            writeElement(element, bool);
        }
    }

    /**
     * Schreibt ein öffnendes und schließendes Tag und dazwischen den
     * übergebenen Integer. Z.B. element ist "komisch" und intValue ist
     * <code>1</code> ergibt in der XML-Datei "&ltkomisch&gt1&lt/komisch&gt".
     *
     * @param element Name des Tags
     * @param intValue Integer, dessen String-Value zwischen das öffnende und
     *            schließende Tag geschrieben wird
     * @throws XMLStreamException
     */
    protected final void writeElement(final String element, final int intValue) throws XMLStreamException {
        writeElement(element, String.valueOf(intValue));
    }

    /**
     * Schreibt ein öffnendes und schließendes Tag und dazwischen den
     * übergebenen Double. Z.B. element ist "komisch" und doubleValue ist
     * <code>1.0</code> ergibt in der XML-Datei
     * "&ltkomisch&gt1.0&lt/komisch&gt".
     *
     * @param element Name des Tags
     * @param doubleValue Double, dessen String-Value zwischen das öffnende und
     *            schließende Tag geschrieben wird
     * @throws XMLStreamException
     */
    protected final void writeElement(final String element, final double doubleValue) throws XMLStreamException {
        writeElement(element, String.valueOf(doubleValue));
    }

    /**
     * Schreibt das übergebene Attribut mit dem übergebenen Text an das zuletzt
     * die Funktion {@link #writeStartElement(String, String...)} geöffnete Tag.
     * Ist der text aber <code>null</code>, wird nichts geschrieben.
     *
     * @param attribute Name des Attibutes
     * @param text Wert des Attributes
     * @throws XMLStreamException
     * @see {@link XMLStreamWriter#writeAttribute(String, String)}
     */
    protected final void writeAttribute(final Object attribute, final Object text) throws XMLStreamException {
        if (text != null) {
            writer.writeAttribute(attribute.toString(), text.toString());
        }
    }

    /**
     * Schreibt das übergebene Attribut mit dem übergebenen Integer als Text an
     * das zuletzt die Funktion {@link #writeStartElement(String, String...)}
     * geöffnete Tag.
     *
     * @param attribute Name des Attibutes
     * @param text Wert des Attributes
     * @throws XMLStreamException
     * @see {@link XMLStreamWriter#writeAttribute(String, String)}
     */
    protected final void writeAttribute(final String attribute, final int intValue) throws XMLStreamException {
        writeAttribute(attribute, String.valueOf(intValue));
    }

    /**
     * Delegate für {@link XMLStreamWriter#writeCharacters(String)}
     *
     * @param text
     * @throws XMLStreamException
     * @see {@link XMLStreamWriter#writeCharacters(String)}
     */
    protected final void writeCharacters(final String text) throws XMLStreamException {
        writer.writeCharacters(text);
    }

    /**
     * Hängt der XML-Datei den übergebenen Text an. Dieser Text wird in keiner
     * Weise escaped. Es wird einfach so rausgeschrieben, wie es übergeben wird.
     *
     * @param text Text der so wie er übergeben wird an die Datei angehängt wird
     * @throws IOException
     */
    protected final void writeCharactersUnescaped(final String text) throws IOException {
        outStream.write(text.getBytes());
    }

    /**
     * Schreibt eine CDATA-Section. Im Unterschied zu
     * {@link #writeCharactersUnescaped(String)} wird hier
     * <![CDATA[...]]]]><![CDATA[>...]]> um den Text geschrieben.
     *
     * @param text
     * @throws XMLStreamException
     */
    protected final void writeCDATA(final String text) throws XMLStreamException {
        writer.writeCData(text);
    }

    /**
     * Schreibt einen XML-Kommentar. Delegate für
     * {@link XMLStreamWriter#writeComment(String)}.
     *
     * @param comment
     * @throws XMLStreamException
     * @see {@link XMLStreamWriter#writeComment(String)}
     */
    protected final void writeComment(final String comment) throws XMLStreamException {
        writer.writeComment(comment);
    }

    /**
     * Ersetzt den übergebenen String durch den leeren String, wenn er
     * <code>null</code> ist. Sonst kommt der String selnst zurück.
     *
     * @param s String der zurück kommt, wenn er nicht <code>null</code> ist.
     *            Wenn er <code>null</code> ist, dann kommt ein leerer String
     *            zurück.
     * @return den bergebenen String, wenn er nicht <code>null</code> ist. Wenn
     *         er <code>null</code> ist, dann kommt ein leerer String zurück
     */
    protected final String getValidString(final String s) {
        return s == null ? "" : s;
    }

}
