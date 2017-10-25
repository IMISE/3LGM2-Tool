package de.imise.tool3lgm.imexport.graphml;

import java.io.File;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;
import de.imise.tool3lgm.graphtools.model.GDCollection;
import de.imise.tool3lgm.graphtools.model.Szenario;
import de.imise.util.StringUtils;

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

    private boolean write(final Class<? extends GraphmlWriter> writerClass) {
        int i = 1;
        boolean allOK = true;
        for (Szenario szenario : szenarios) {
            File targetFile = getValidFile(szenario, i++, dir);
            GraphmlWriter writer = null;
            try {
                if (writerClass == YFilesGraphmlWriter.class) {
                    writer = new YFilesGraphmlWriter(targetFile, szenario);
                } else if (writerClass == YEdGraphmlWriter.class) {
                    writer = new YEdGraphmlWriter(targetFile, szenario);
                }
                writer.write(ModelConstants.DOMAIN_LAYER);
            } catch (Exception e) {
                e.printStackTrace();
                allOK = false;
                continue;
            }
        }
        return allOK;
    }

    private static File getValidFile(final Szenario szenario, final int index, final File dir) {
        String name = szenario.getTitle();
        if (!StringUtils.isNullOrEmptyOrBlank(name)) {
            name = Tool3lgmConstants.getResString("submodel") + "_" + index;
        }
        name += ".graphml";
        return new File(dir, name);
    }

}
