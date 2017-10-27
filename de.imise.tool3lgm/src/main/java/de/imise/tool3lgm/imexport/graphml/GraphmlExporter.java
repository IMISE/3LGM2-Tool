package de.imise.tool3lgm.imexport.graphml;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.util.StringUtils;
import de.imise.util.io.FileHandler;

public class GraphmlExporter {

    private final File dir;

    private final Iterable<Szenario> szenarios;

    /**
     * @param file
     *            Verzeichnis, in das die Graphml-Dateien geschrieben werden sollen
     * @param gdcoll
     *            Modell, dessen Teilmodell-Grafiken exportiert werden sollen
     * @throws XMLStreamException
     * @throws IOException
     */
    public GraphmlExporter(final File file, final GDCollection gdcoll) {
        this(file, gdcoll.getSzenarios());
    }

    /**
     * @param file
     *            Verzeichnis, in das die Graphml-Dateien geschrieben werden sollen
     * @param szenarios
     *            Teilmodelle, deren Grafiken exportiert werden sollen
     * @throws XMLStreamException
     * @throws IOException
     */
    public GraphmlExporter(final File file, final Iterable<Szenario> szenarios) {
        dir = file.isDirectory() ? file : file.getParentFile();
        this.szenarios = szenarios;
    }

    /**
     * @return
     */
    public final boolean writeYEdGraphml() {
        return write(YEdGraphmlWriter.class);
    }

    /**
     * @return
     */
    public final boolean writeYFilesGraphml() {
        return write(YFilesGraphmlWriter.class);
    }

    private boolean write(final Class<? extends GraphmlWriter> writerClass) {
        boolean allOK = true;
        for (Szenario szenario : szenarios) {
            for (int layer : ModelConstants.VISIBLE_LAYERS) {
                try {
                    if (writerClass == YFilesGraphmlWriter.class) {
                        File targetFile = getValidFile(szenario, layer, dir, "_yHTML");
                        new YFilesGraphmlWriter(targetFile, szenario).write(layer);
                    } else if (writerClass == YEdGraphmlWriter.class) {
                        File targetFile = getValidFile(szenario, layer, dir, "_yEd");
                        new YEdGraphmlWriter(targetFile, szenario).write(layer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    allOK = false;
                    continue;
                }
            }
        }
        return allOK;
    }

    private static File getValidFile(final Szenario szenario, final int layer, final File dir, final String fileNameEnd) {
        String fileName = ModelConstants.getVisibleLayerName(layer);
        fileName = StringUtils.getFirstChars(fileName);
        fileName = szenario.getCollection().getName() + "_" + szenario.getTitle() + "_" + fileName + fileNameEnd + ".graphml";
        fileName = FileHandler.removeInvalidFileNameCharacters(fileName);
        return new File(dir, fileName);
    }

}
