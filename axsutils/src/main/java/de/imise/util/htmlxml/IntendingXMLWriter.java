package de.imise.util.htmlxml;

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
 * Allgemeiner XML-Writer der eingerückte XML-Dateien schreibt und bei Bedarf zippen kann.
 *
 * @author AXS (11.08.2017)
 */
public class IntendingXMLWriter {

    private final XMLStreamWriter writer;

    private OutputStream outStream;

    private ZipOutputStream zipOutStream;

    /**
     * Legt einen neuen XML-Writer an, der alle Einträge einrückt. Wird ein nicht leerer oder nicht null zipEntryName
     * angegeben, dann wird die Datei zusätzlich in eine zip-Datei gepackt und der innere Dateiname ist der zipEntryName.
     *
     * @param file Name der zu schreibenden Datei
     * @param zipEntryName ist dieser String nicht leer oder null wird die entstehende XML-Datei in eine ZIP-Datei gepackt und die innen liegende
     *            Datei hat den Namen dieses Strings
     * @throws XMLStreamException
     * @throws IOException
     */
    protected IntendingXMLWriter(final File file, final String zipEntryName) throws XMLStreamException, IOException {
        outStream = new FileOutputStream(file);
        if (!Strings.isNullOrEmpty(zipEntryName)) {
            zipOutStream = getOpenZipOutputStream(outStream, zipEntryName);
            outStream = zipOutStream;
        }
        XMLStreamWriter tmpwriter = XMLOutputFactory.newInstance().createXMLStreamWriter(outStream, "UTF-8");
        writer = new IndentingXMLStreamWriter(tmpwriter);
    }

    protected final void finish() throws XMLStreamException, IOException {
        writer.writeEndDocument();
        writer.flush();
        writer.close();
        if (zipOutStream != null) {
            zipOutStream.finish(); //schließt auch den Entry
        }
        outStream.close();
    }

    private ZipOutputStream getOpenZipOutputStream(final OutputStream baseStream, final String zipEntryName) throws IOException {
        ZipOutputStream zipStream = new ZipOutputStream(baseStream);
        zipStream.setMethod(ZipOutputStream.DEFLATED);
        zipStream.setLevel(9);
        ZipEntry entry = new ZipEntry(zipEntryName);
        entry.setMethod(ZipEntry.DEFLATED);
        zipStream.putNextEntry(entry);
        return zipStream;
    }

    protected final void writeStartDocument(final String encoding, final String version) throws XMLStreamException {
        writer.writeStartDocument(encoding, version);
    }

    protected final void writeStartElement(final String element, final String... attributes) throws XMLStreamException {
        writer.writeStartElement(element);
        writeAttributes(attributes);
    }

    protected void writeAttributes(final String... attributes) throws XMLStreamException {
        for (int i = 0; i < attributes.length; i += 2) {
            //übergebene Attribute, bei denen der Value leer ist, werden nicht geschrieben
            writeAttribute(attributes[i], attributes[i + 1]);
        }
    }

    protected final void writeEndElement() throws XMLStreamException {
        writer.writeEndElement();
    }

    protected final void writeEmptyElement(final String element, final String... attributes) throws XMLStreamException {
        writer.writeEmptyElement(element);
        writeAttributes(attributes);
    }

    protected final void writeElement(final String element, final String text) throws XMLStreamException {
        writer.writeStartElement(element);
        writer.writeCharacters(text);
        writer.writeEndElement();
    }

    protected final void writeElement(final String element, final boolean bool) throws XMLStreamException {
        writeElement(element, String.valueOf(bool));
    }

    protected final void writeElement(final String element, final int intValue) throws XMLStreamException {
        writeElement(element, String.valueOf(intValue));
    }

    protected final void writeElement(final String element, final double doubleValue) throws XMLStreamException {
        writeElement(element, String.valueOf(doubleValue));
    }

    protected final void writeAttribute(final Object attribute, final Object text) throws XMLStreamException {
        if (text != null) {
            writer.writeAttribute(attribute.toString(), text.toString());
        }
    }

    protected final void writeAttribute(final String attribute, final int intValue) throws XMLStreamException {
        writeAttribute(attribute, String.valueOf(intValue));
    }

    protected final void writeCharacters(final String text) throws XMLStreamException {
        writer.writeCharacters(text);
    }

    protected final void writeCDATA(final String text) throws XMLStreamException {
        writer.writeCData(text);
    }

    protected final void writeComment(final String comment) throws XMLStreamException {
        writer.writeComment(comment);
    }

    protected final String getValidString(final String s) {
        return s == null ? "" : s;
    }

}
