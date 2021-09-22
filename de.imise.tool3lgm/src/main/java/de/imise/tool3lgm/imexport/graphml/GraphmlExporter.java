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
     * @param file Verzeichnis, in das die Graphml-Dateien geschrieben werden
     *            sollen
     * @param gdcoll Modell, dessen Teilmodell-Grafiken exportiert werden sollen
     * @throws XMLStreamException
     * @throws IOException
     */
    public GraphmlExporter(final File file, final GDCollection gdcoll) {
        this(file, gdcoll.getSzenarios());
    }

    /**
     * @param file Verzeichnis, in das die Graphml-Dateien geschrieben werden
     *            sollen
     * @param szenarios Teilmodelle, deren Grafiken exportiert werden sollen
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
        //im Moment ist der boolean egal, weil der YedGrapgmlWriter immer nur eine einzelne Ebene rausschreiben kann
        return write(YEdGraphmlWriter.class, true);
    }

    /**
     * @return
     */
    public final boolean writeYFilesGraphml() {
        return write(YFilesGraphmlWriter.class, true);
    }

    private boolean write(final Class<? extends GraphmlWriter> writerClass, final boolean oneModelFile) {
        boolean allOK = true;
        for (Szenario szenario : szenarios) {
            if (oneModelFile && writerClass == YFilesGraphmlWriter.class) {
                try {
                    File targetFile = getValidFile(szenario, -1, dir, "yHTML");
                    //Aufruf der Funktion, mit der alle Ebenen in ein Modell geschrieben werden
                    new YFilesGraphmlWriter(targetFile, szenario, -1).write();
                } catch (Exception e) {
                    e.printStackTrace();
                    allOK = false;
                    continue;
                }
            } else {
                for (int layer : ModelConstants.VISIBLE_LAYERS) {
                    try {
                        if (writerClass == YFilesGraphmlWriter.class) {
                            File targetFile = getValidFile(szenario, layer, dir, "yHTML");
                            //Aufruf der Funktion, mit der eine Ebenen in ein Modell geschrieben wird
                            new YFilesGraphmlWriter(targetFile, szenario, layer).write();
                        } else if (writerClass == YEdGraphmlWriter.class) {
                            //Aufruf der Funktion, mit der eine Ebenen in ein Modell geschrieben wird
                            File targetFile = getValidFile(szenario, layer, dir, "yEd");
                            new YEdGraphmlWriter(targetFile, szenario, layer).write();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        allOK = false;
                        continue;
                    }
                }
            }
        }
        return allOK;
    }

    private static File getValidFile(final Szenario szenario, final int layer, final File dir, final String fileNameEnd) {
        String layerName = layer < 0 ? "" : StringUtils.getFirstChars(ModelConstants.getVisibleLayerName(layer)) + "_";
        String fileName = szenario.getCollection().getName() + "_" + szenario.getName() + "_" + layerName + fileNameEnd + ".graphml";
        fileName = FileHandler.removeInvalidFileNameCharacters(fileName);
        return new File(dir, fileName);
    }

}
