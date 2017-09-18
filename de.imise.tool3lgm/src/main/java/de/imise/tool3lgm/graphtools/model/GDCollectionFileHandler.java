package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.xml.ToolXMLParser.isParseAbleFileVersion;
import static de.imise.tool3lgm.xml.ToolXMLParser.isXMLFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;
import java.util.zip.DataFormatException;
import java.util.zip.ZipInputStream;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import de.imise.tool3lgm.Static;
import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.Tool3lgmConstants.FileFilterType;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.gui.AbstractInternalFrame;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.tools.LGMInputStream;
import de.imise.tool3lgm.userproperties.UserProperties;
import de.imise.tool3lgm.xml.LGMVersionException;
import de.imise.tool3lgm.xml.ToolXMLParser;
import de.imise.tool3lgm.xml.ToolXMLWriter;
import de.imise.tool3lgm.xml.XMLVersionException;
import de.imise.util.io.FileHandler;
import de.imise.util.swing.dialog.ExtendedFileChooser;

public class GDCollectionFileHandler {

    /** {@link GDCollection}, die dieser FileHanlder speichert */
    private final GDCollection gdcoll;

    /** Information für Dateiversion (wird bei jedem Aufruf von getFileVersion() um eins erhoeht) */
    private int fileVersion = 0;

    /** the file to load collection from or to save collection in */
    private RandomAccessFile randomAccessFile;

    private File file;

    private FileLock lock;

    /** flag, whether file for this collection is only opened for reading */
    private final boolean isReadOnly = false;

    /** flag, whether the filesystem of the file for this collection supports locking */
    private boolean lockSupported = false;

    /** flag, whether collection will be saved in compressed zip-file or not */
    private boolean isZipFile = true;

    public GDCollectionFileHandler(final GDCollection gdcoll) {
        this.gdcoll = gdcoll;
        createName();
    }

    /**
     * gibt String mit Versionsdaten der Datei zurück<br>
     * setzt sich zusammen aus fileVersion_Benutzername_currentTimeMillis()
     *
     * @return String mit Versionsdaten der Datei
     */
    public String getFileVersion() {
        return (++fileVersion) + "_" + System.getProperty("user.name") + "_" + System.currentTimeMillis();
    }

    /**
     * setzt die int-Variable mit der Dateiversion
     *
     * @param String der dim Aufbau dem Rueckgabe-String der Methode getFileVersion() gleicht
     */
    public void setFileVersion(final String string) {
        try {
            fileVersion = Integer.parseInt(string.substring(0, string.indexOf('_')));
        } catch (Exception exp) {
            //          Log.show(Log.ERROR, Tool3lgmConstants.getErrorString("FehlerAllgemein"), exp);
        }
    }

    /**
     * set the file modelElement for this collection
     *
     * @param _file the new File for this collection
     * @return boolean with false, if file is shared (--> readOnly) otherwise true
     * @author Thomas Rudert
     */
    public boolean setFile(final File _file) throws IOException {
        if (file != null && file.equals(_file)) {
            return true;
        }

        if (randomAccessFile != null) {
            if (lock != null) {
                lock.release();
            }
            randomAccessFile.close();
            file = null;
        }

        lockSupported = Tool3lgmConstants.lockSupportedByFileSystem(_file);

        RandomAccessFile raf = null;
        boolean copiedToUserDir = false;
        try {
            raf = new RandomAccessFile(_file, "rw");
        } catch (IOException e) {
            File writableFile = new File(Tool3lgmConstants.USER_HOME_DIR_NAME + "/3LGM2Tool", _file.getName());
            FileHandler.copyFile(_file, writableFile);
            setFile(writableFile);
            copiedToUserDir = true;
        }
        if (copiedToUserDir) {
            return true;
        }
        if (lockSupported) {
            lock = raf.getChannel().tryLock(0, Long.MAX_VALUE, true);
            if (lock == null) {
                return false;
            }
        }

        file = _file;
        randomAccessFile = raf;

        createName();

        return !isReadOnly;
    }

    /**
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     * @return
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     *
     */
    public void close() {
        try {
            if (lock != null) {
                lock.release();
            }
            if (randomAccessFile != null) {
                randomAccessFile.close();
            }
        } catch (Exception exp) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), exp);
        }
    }

    /**
    *
    */
    private void createName() {
        int counter = 0;
        String newName = createName(counter);

        int collectionsCount = Static.getCollectionCount();
        if (collectionsCount < 0) {
            return;
        }

        for (int index = 0; index < collectionsCount; index++) {
            GDCollection temp = Static.getCollection(index);
            if (temp.equals(gdcoll)) {
                continue;
            }
            if (!temp.getName().equals(newName)) {
                continue;
            }
            index = 0;
            newName = createName(++counter);
        }

        String name = newName + (isReadOnly ? " " + getResString("text_readOnly") : "");
        gdcoll.setName(name);
        for (AbstractInternalFrame f : Static.getAllFrames()) {
            if (f.getCollection().equals(gdcoll)) {
                f.setTitle(name);
                Static.getTool().getModelBrowserPanel().updateTitle(gdcoll);
            }
        }
    }

    /**
     * @param counter
     * @return
     */
    private String createName(final int counter) {
        if (getFile() == null) {
            return "<" + getResString("unbenannt") + (counter > 0 ? " #" + counter : "") + ">";
        }
        return getFile().getName() + (isReadOnly ? " <" + getResString("text_readOnly") + ">" : "") + (counter > 0 ? " #" + counter : "");
    }

    /**
     * @return
     * @throws Exception
     */
    public boolean loadFromRAF() throws Exception {
        return loadFromRAF(null);
    }

    /**
     * Load collection from file which is specified in field file
     *
     * @return true if reading was successful
     * @throws Exception; throws all exceptions happen during reading
     * @author Thomas Rudert
     */
    public boolean loadFromRAF(final File file) throws Exception {
        Static.getTool().setCursor(Tool3lgmConstants.getWaitCursor());

        RandomAccessFile randomAccessFile;
        if (file != null) {
            randomAccessFile = new RandomAccessFile(file, "rw");
        } else {
            randomAccessFile = this.randomAccessFile;
        }
        gdcoll.setBulkMode(true);
        boolean readingSuccessful = false;
        try {
            randomAccessFile.seek(0);
            String line = randomAccessFile.readLine();
            if (line != null) {
                LGMInputStream fis = new LGMInputStream(randomAccessFile.getFD());
                if (line.startsWith("<!--ziped Tool3lgmFile-->")) {
                    readingSuccessful = loadZipFile(fis);
                    if (readingSuccessful) {
                        isZipFile = true;
                    }
                } else if (line.startsWith("PK")) {
                    fis.getChannel().position(0);
                    readingSuccessful = loadZipFile(fis);
                    if (readingSuccessful) {
                        isZipFile = true;
                    }
                } else {
                    fis.getChannel().position(0);
                    readingSuccessful = loadFromFileInputStream(fis);
                    if (readingSuccessful) {
                        isZipFile = false;
                    }
                }
                fis.close();
            } else {
                randomAccessFile.close();
                throw new IOException("Could not read file...");
            }
        } catch (Exception e) {
            gdcoll.setBulkMode(false);
            if (file != null) {
                randomAccessFile.close();
            }
            Log.show(Log.FATAL, getResString("FehlerAllgemein") + e, e);
        }

        if (file != null) {
            randomAccessFile.close();
        }
        gdcoll.setBulkMode(false);
        Static.getTool().setCursor(Tool3lgmConstants.getNormalCursor());
        return readingSuccessful;

    }

    /**
     * load collection from packed zipFile
     *
     * @param fileStream the FileInputStream to the file which will be read
     * @return true, if reading was successful
     * @throws IOException if something wrong with the FileInputStream or the zip-format
     * @author Thomas Rudert
     */
    public boolean loadZipFile(final InputStream fileStream) throws IOException {
        ZipInputStream zipStream = new ZipInputStream(fileStream) {

            @Override
            public void close() {
            }
        };
        zipStream.getNextEntry();
        boolean retVal = loadXMLFile(zipStream, false);
        //      zipStream.close();
        return retVal;
    }

    /**
     * load collection from (not packed) file
     *
     * @param fileStream the FileInputStream to the File to load
     * @return true, if reading was successful
     * @throws IOException
     * @throws LGMVersionException, if file-version is not readable
     * @throws XMLVersionException, if xml-version is not readable
     * @throws FileNotFoundException
     * @throws DataFormatException
     * @author Thomas Rudert
     */
    private boolean loadFromFileInputStream(final FileInputStream fileStream) throws IOException, LGMVersionException, XMLVersionException, FileNotFoundException {
        if (!isXMLFile(fileStream) || !isParseAbleFileVersion(fileStream)) {
            throw new LGMVersionException(getResString("to_old_file_format"));
        }
        fileStream.getChannel().position(0);
        return loadXMLFile(fileStream, false);
    }

    /**
     * load collection from xml-source
     *
     * @param inputStream an InputStream to the xml-source
     * @return true, if reading was successful
     * @author Thomas Rudert
     */
    public boolean loadXMLFile(final InputStream inputStream, final boolean paste) {
        try {
            ToolXMLParser parser = new ToolXMLParser(gdcoll, inputStream, paste);
            parser.parseDocument();
        } catch (Exception exp) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein") + exp, exp);
            return false;
        }
        UserFieldDefinitions userFieldDefinitions = gdcoll.getUserFieldDefinitions();
        userFieldDefinitions.hasCrossReferences();
        return true;
    }

    /**
     * @return
     */
    public boolean chooseFile() {
        ExtendedFileChooser fileChooser = new ExtendedFileChooser(null, UserProperties.getWorkingDirectory());
        FileNameExtensionFilter lgmZippedFileFiler = Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LGM3_ZIP);
        FileNameExtensionFilter lgmUnzippedFileFiler = Tool3lgmConstants.getFileNameExtensionFilter(FileFilterType.LGM3_UNZIPPED);
        fileChooser.setFileFilters(false, lgmZippedFileFiler, lgmUnzippedFileFiler);
        fileChooser.setFileFilter(isZipFile ? lgmZippedFileFiler : lgmUnzippedFileFiler);
        if (getFile() != null) {
            fileChooser.setSelectedFile(getFile());
        }
        if (fileChooser.showSaveDialog(Static.getMainFrame()) != ExtendedFileChooser.APPROVE_OPTION) {
            return false;
        }
        File pfad = fileChooser.getSelectedFile();

        try {
            setFile(pfad);
        } catch (IOException exp) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein"), exp);
            exp.printStackTrace();
            return false;
        }
        isZipFile = fileChooser.getFileFilter() == lgmZippedFileFiler;
        return true;
    }

    /**
     * save collection to file<br/>
     * if isReadOnly is true do nothing and return false<br/>
     * if isZipFile write content into compressed file<br/>
     * create temporary file for writing and if all actions are completed successfully overwrites original file
     *
     * @return boolean with true, if and only if filewriting was successful
     * @author Thomas Rudert
     * @throws IOException
     * @throws FactoryConfigurationError
     * @throws XMLStreamException
     */
    public boolean saveToFile() {
        try {
            if (isReadOnly || file == null) {
                if (!chooseFile()) {
                    return false;
                }
            }
            File tempFile = new File(file.getParentFile(), ".tempTool3lgmSaveFile");
            tempFile.delete();
            tempFile.deleteOnExit();
            if (!tempFile.createNewFile()) {
                return false;
            }
            String zipEntryName = isZipFile ? ToolXMLWriter.getZipEntryName(file) : null;
            if (!ToolXMLWriter.write(gdcoll, tempFile, zipEntryName)) {
                return false;
            }
            UserProperties.setWorkingDirectory(file);
            copyTempToDestinationFile(tempFile, randomAccessFile, lockSupported, lock);
        } catch (Exception e) {
            Log.show(Log.FATAL, getResString("FehlerAllgemein") + "\n" + e, e);
            return false;
        }
        return true;
    }

    private static void copyTempToDestinationFile(final File tempFile, final RandomAccessFile randomAccessFile, final boolean lockSupported, final FileLock lock) throws IOException {
        if (tempFile.length() <= 0) {
            throw new IOException("Empty file!");
        }

        @SuppressWarnings("resource")
        //der wird geclosed in forceClose()
        LGMInputStream tmpIStream = new LGMInputStream(tempFile);
        randomAccessFile.seek(0);
        randomAccessFile.setLength(0);

        long l = tempFile.length();
        int length = new Long(l).intValue();
        byte[] data = new byte[length];
        tmpIStream.read(data);
        ByteBuffer byteBuf = ByteBuffer.wrap(data);
        //TW
        if (lockSupported && lock != null) {
            randomAccessFile.getChannel().write(byteBuf);
        } else {
            randomAccessFile.write(data);
        }
        tmpIStream.forceClose();

        tempFile.delete();
    }

}
