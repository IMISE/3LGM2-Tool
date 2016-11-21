/*
 * Created on 02.12.2003
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package de.imise.tool3lgm.tools;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author thomas
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class LGMInputStream extends FileInputStream {

    /**
     * @param file
     * @throws FileNotFoundException
     */
    public LGMInputStream(final RandomAccessFile file) throws FileNotFoundException, IOException {
        super(file.getFD());
    }

    /**
     * @param fd
     */
    public LGMInputStream(final FileDescriptor fd) {
        super(fd);
    }

    /**
     * @param file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public LGMInputStream(final File file) throws FileNotFoundException, IOException {
        super(file);
    }

    @Override
    public void close() {
        //nothing to do
    }

    /**
     * @throws IOException
     */
    public void forceClose() throws IOException {
        super.close();
    }
}
