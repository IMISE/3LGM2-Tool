package de.imise.util.swing.dialog;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import de.imise.util.io.FileHandler;
import de.imise.util.swing.component.ParentComponentFinder;

/**
 * Extends the functionality of the {@link JFileChooser} to set FileSystemView
 * and set currentDirectory to userPath in the constructor. If the return value
 * of showDialog, showOpenDialog or showSaveDialog is APPROVE_OPTION, then
 * userPath will be set to currentDirectory. All dialogs initialized with the
 * same <code>pathKey</code> always start in the path last selected in such a
 * dialog. Furthermore, when the save dialog is called and the user confirms
 * with the save button, it is always ensured that the file to be saved exists
 * and may be overwritten. Furthermore: if the current {@link FileFilter} is a
 * {@link FileNameExtensionFilter} while saving and the filename does not have
 * an extension that this {@link FileFilter} accepts, then the first of the
 * extensions that this filter accepts is appended to the file.
 */
public class ExtendedFileChooser extends JFileChooser {

    /**
     * If no directory is specified, an instance of this dialog starts in this
     * directory
     */
    private static final File DEFAULT_PATH = FileSystemView.getFileSystemView().getDefaultDirectory();

    /**
     * Maps from a Key-Object to a path. Depending on which key object was used
     * to start an instance of this class, the last selected path in this map is
     * remembered. The default is the key object <code>null</code>.
     */
    private static final Map<Object, File> KEY_TO_PATH_MAP = new HashMap<>();

    /**
     * Default key for the last path of this dialog, if no other key object was
     * set.
     */
    private Object pathKey = null;

    /** ResourceHandler for all instances */
    private static final DialogResourceHandler drh = new DialogResourceHandler(ExtendedFileChooser.class);

    private String fileName = null;

    /**
     * @param pathKey is used to determine in which path a Chooser with this
     *            pathKey was last opened and restored in this way.
     */
    public ExtendedFileChooser(final Object pathKey) {
        this(pathKey, (File) null);
    }

    /**
     * @param pathKey is used to determine in which path a Chooser with this
     *            pathKey was last opened and restored in this way.
     * @param defaultPath If no path is found for the pathKey yet, the passed
     *            DefaulPath is set. If this is invalid, you will end up in the
     *            user's main directory.
     */
    public ExtendedFileChooser(final Object pathKey, final File defaultPath) {
        this(pathKey, defaultPath, null);
    }

    /**
     * @param pathKey is used to determine in which path a Chooser with this
     *            pathKey was last opened and restored in this way.
     * @param fileName Name of the file that should already be preset
     */
    public ExtendedFileChooser(final Object pathKey, final String fileName) {
        this(pathKey, null, fileName);
    }

    /**
     * @param pathKey is used to determine in which path a Chooser with this
     *            pathKey was last opened and restored in this way.
     * @param defaultPath If no path is found for the pathKey yet, the passed
     *            DefaulPath is set. If this is invalid, you will end up in the
     *            user's main directory.
     * @param fileName Name of the file that should already be preset
     */
    public ExtendedFileChooser(final Object pathKey, final File defaultPath, final String fileName) {
        setPathKey(pathKey);
        setFileSystemView(FileSystemView.getFileSystemView());
        File path = KEY_TO_PATH_MAP.get(pathKey);
        if (path == null && defaultPath != null && defaultPath.isDirectory() && defaultPath.exists()) {
            path = defaultPath;
        }
        this.fileName = fileName;
        setCurrentDirectory(path == null ? DEFAULT_PATH : path);
    }

    /**
     * @param pathKey is used to determine in which path a Chooser with this
     *            pathKey was last opened and restored in this way.
     */
    public void setPathKey(final Object pathKey) {
        this.pathKey = pathKey;
    }

    /**
     * @return the pathKey is used to determine in which path a Chooser with
     *         this pathKey was last opened and restored in this way.
     */
    public Object getPathKey() {
        return pathKey;
    }

    /**
     * @param pathKey is used to determine in which path a Chooser with this
     *            pathKey was last opened and restored in this way.
     * @param path this path or file will be current directory or file for
     *            dialogs with the given pathKey
     */
    public void setPath(final Object pathKey, final File path) {
        setPathKey(pathKey);
        KEY_TO_PATH_MAP.put(pathKey, path);
        setCurrentDirectory(path);
    }

    /**
     * @param path
     */
    public void setPath(final File path) {
        setPath(pathKey, path);
    }

    @Override
    public int showDialog(final Component parent, final String approveButtonText) throws HeadlessException {
        if (fileName != null && !fileName.trim().isEmpty()) {
            setSelectedFile(new File(fileName));
        }
        int returnValue = super.showDialog(parent, approveButtonText);
        setPath(pathKey, getCurrentDirectory());
        return returnValue;
    }

    /**
     * Sets the FileFilter of this dialog. The first FileFilter from the array
     * is set as active.
     *
     * @param showAllFileFilter
     * @param fileFilters
     */
    public void setFileFilters(final boolean showAllFileFilter, final FileNameExtensionFilter... fileFilters) {
        setAcceptAllFileFilterUsed(showAllFileFilter);
        if (fileFilters != null) {
            for (FileNameExtensionFilter fileFilter : fileFilters) {
                addChoosableFileFilter(fileFilter);
            }
            setFileFilter(fileFilters[0]);
        }
    }

    /**
     * @param parent
     * @param showAllFileFilter
     * @param fileFilters
     * @return
     */
    public int showOpenDialog(final Component parent, final boolean showAllFileFilter, final FileNameExtensionFilter... fileFilters) {
        setFileFilters(showAllFileFilter, fileFilters);
        return super.showOpenDialog(parent);
    }

    /**
     * @param parent
     * @param showAllFileFilter
     * @param fileFilters
     * @return
     */
    public int showSaveDialog(final Component parent, final boolean showAllFileFilter, final FileNameExtensionFilter... fileFilters) {
        return showSaveDialog(parent, null, showAllFileFilter, null, fileFilters);
    }

    /**
     * @param parentComponent
     * @param title
     * @param showAllFileFilter
     * @param selectedFilter
     * @param fileFilters
     * @return
     */
    public int showSaveDialog(final Object parentComponent, final String title, final boolean showAllFileFilter, final FileNameExtensionFilter selectedFilter, final FileNameExtensionFilter... fileFilters) {
        setDialogType(SAVE_DIALOG);
        if (title != null) {
            setDialogTitle(title);
        }
        setFileFilters(showAllFileFilter, fileFilters);

        // if there was a previously selected file type set the selection to it
        if (selectedFilter != null) {
            setFileFilter(selectedFilter);
        }

        boolean correctFileName = false;
        int returnValue = ERROR_OPTION;
        // the dialog is repeated until a writable file is selected or Cancel is pressed
        while (!correctFileName && returnValue != CANCEL_OPTION) {
            Component parent = ParentComponentFinder.getFrameOrDialog(parentComponent);
            returnValue = showDialog(parent, null);

            // if not OK pressed -> out
            if (returnValue != APPROVE_OPTION) {
                return returnValue;
            }
            setPath(pathKey, getCurrentDirectory());
            // get chooesed file
            File selectedFile = getSelectedFile();

            // Check if there are invalid characters in the name
            correctFileName = false;
            try {
                correctFileName = selectedFile.getCanonicalPath().endsWith(selectedFile.getName());
            } catch (IOException e) {
            }
            if (!correctFileName) {
                MultipleOptionPane.showConfirmDialog(parent, drh.getResString("MESSAGE_SAVE_ERROR"), drh.getResString("MESSAGE_INVALID_CHARS"), MultipleOptionPane.DEFAULT_OPTION, MultipleOptionPane.ERROR_MESSAGE);
                continue;
            }

            // if the specified file does not exist yet check if an extension should be appended
            FileFilter fileFilter = getFileFilter();
            if (fileFilter instanceof FileNameExtensionFilter) {
                String[] extensions = ((FileNameExtensionFilter) fileFilter).getExtensions();
                // if the specified filename has no extension, but a valid extension exists in the FileFilter
                if (extensions.length > 0) {
                    String newSelectedFileName = getSelectedFile().getPath();
                    boolean extensionFound = false;
                    // if the filename has none of the valid extensions ALWAYS append the first file extension of the FileFilter
                    for (String extension : extensions) {
                        if (newSelectedFileName.endsWith(extension)) {
                            extensionFound = true;
                            break;
                        }
                    }
                    if (!extensionFound) {
                        setSelectedFile(new File(newSelectedFileName.concat(".").concat(extensions[0])));
                        selectedFile = getSelectedFile();
                    }
                }
            }

            // if the specified file already exists
            if (selectedFile.exists()) {
                // if writable -> ask whether to save over it
                if (!selectedFile.canWrite()) {
                    correctFileName = false;
                    showSaveErrorMessage(parent);
                    continue;
                }
                switch (JOptionPane.showConfirmDialog(parent, drh.getResString("MESSAGE_OVERWRITE_1") + selectedFile.getName() + drh.getResString("MESSAGE_OVERWRITE_2"))) {
                case JOptionPane.YES_OPTION:
                    return APPROVE_OPTION;
                case JOptionPane.NO_OPTION:
                    correctFileName = false;
                    continue;
                case JOptionPane.CANCEL_OPTION:
                    return CANCEL_OPTION;
                }
                // if not writable -> repeat error and dialog for file selection
            }

            // if the new file cannot be created or is not writable for some reason
            if (!FileHandler.guaranteeWriteableFile(selectedFile)) {
                correctFileName = false;
                MultipleOptionPane.showConfirmDialog(parent, drh.getResString("MESSAGE_SAVE_ERROR"), drh.getResString("MESSAGE_CANT_WRITE"), MultipleOptionPane.DEFAULT_OPTION, MultipleOptionPane.ERROR_MESSAGE);
                continue;
            }
        }
        return returnValue;
    }

    @Override
    public int showSaveDialog(final Component parent) throws HeadlessException {
        return showSaveDialog(parent, (String) null, isAcceptAllFileFilterUsed(), null, (FileNameExtensionFilter[]) null);
    }

    /**
     * Displays a message dialog that the file could not be saved.
     *
     * @param parent
     */
    public static final void showSaveErrorMessage(final Component parent) {
        MultipleOptionPane.showConfirmDialog(parent, drh.getResString("MESSAGE_SAVE_ERROR"), drh.getResString("MESSAGE_CANT_WRITE"), MultipleOptionPane.DEFAULT_OPTION, MultipleOptionPane.ERROR_MESSAGE);
    }

    /**
     * Returns the URL string of the passed File object or <code>null</code> if
     * it could not be converted to a valid URL.
     *
     * @param file
     * @return
     */
    public static String getUrl(final File file) {
        String url = null;
        if (file != null) {
            try {
                URI uri = file.toURI();
                URL url2 = uri.toURL();
                url = url2.toString();
            } catch (MalformedURLException e) {
            }
        }
        return url;
    }

    /**
     * @return
     */
    public static final File chooseFile() {
        return chooseFile(null);
    }

    /**
     * @return
     */
    public static final String chooseFileUrl() {
        return getUrl(chooseFile(null));
    }

    /**
     * @param pathKey
     * @return
     */
    public static final File chooseFile(final Object pathKey) {
        return chooseFile(null, pathKey);
    }

    /**
     * @param parent
     * @param pathKey
     * @return
     */
    public static final File chooseFile(final Component parent, final Object pathKey) {
        return chooseFile(parent, null, pathKey);
    }

    /**
     * @param parent
     * @param filter
     * @param pathKey
     * @return
     */
    public static final File chooseFile(final Component parent, final FileNameExtensionFilter filter, final Object pathKey) {
        return chooseFile(parent, filter, pathKey, false);
    }

    /**
     * @param parent
     * @param filter
     * @param pathKey
     * @param multiSelectionEnabled
     * @return
     */
    public static final File chooseFile(final Component parent, final FileNameExtensionFilter filter, final Object pathKey, final boolean multiSelectionEnabled) {
        ExtendedFileChooser chooser = new ExtendedFileChooser(pathKey);
        chooser.setMultiSelectionEnabled(multiSelectionEnabled);
        if (filter != null) {
            chooser.setFileFilters(true, filter);
        }
        File fileToOpen = null;
        if (chooser.showOpenDialog(parent) == ExtendedFileChooser.APPROVE_OPTION) {
            fileToOpen = chooser.getSelectedFile();
        }
        return fileToOpen;
    }

}
