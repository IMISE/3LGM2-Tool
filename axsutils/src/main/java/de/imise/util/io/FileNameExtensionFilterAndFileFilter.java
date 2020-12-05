package de.imise.util.io;

import java.io.File;
import java.io.FileFilter;

import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Ein {@link FileFilter}, der sich genauso verhält wie ein
 * {@link FileNameExtensionFilter}, bei dem man aber optional das Akzeptieren
 * von Verzeichnissen abschalten kann, die der {@link FileNameExtensionFilter}
 * in jedem Fall akzeptiert. Da {@link FileNameExtensionFilter} leider final
 * ist, kann man ihn nicht direkt als Oberklasse nehmen.
 * 
 * @author AXS
 * @create 06.07.2012
 */
public class FileNameExtensionFilterAndFileFilter implements FileFilter {

    /** Der eigentliche FileNameExtensionFilter */
    private FileNameExtensionFilter fileNameExtensionFilter;

    /**
     * Wenn <code>true</code> werden auch Verzeichnisse zurück gegeben, sonst
     * nur Dateien
     */
    private final boolean acceptDirectories;

    /**
     * Creates a {@code FileNameExtensionFilterAndFileFilter} with the specified
     * description and file name extensions. The returned
     * {@code FileNameExtensionFilterAndFileFilter} will accept all directories
     * and any file with a file name extension contained in {@code extensions}.
     * 
     * @param description textual description for the filter, may be
     *            {@code null}
     * @param extensions the accepted file name extensions
     * @throws IllegalArgumentException if extensions is {@code null}, empty,
     *             contains {@code null}, or contains an empty string
     * @see #accept
     * @see javax.swing.filechooser.FileNameExtensionFilter
     */
    public FileNameExtensionFilterAndFileFilter(final String description, final String... extensions) {
        this(new FileNameExtensionFilter(description, extensions));
    }

    /**
     * @param fileNameExtensionFilter
     */
    public FileNameExtensionFilterAndFileFilter(final FileNameExtensionFilter fileNameExtensionFilter) {
        this(fileNameExtensionFilter, true);
    }

    /**
     * @param fileNameExtensionFilter
     * @param acceptDirectories
     */
    public FileNameExtensionFilterAndFileFilter(final FileNameExtensionFilter fileNameExtensionFilter, final boolean acceptDirectories) {
        super();
        this.fileNameExtensionFilter = fileNameExtensionFilter;
        this.acceptDirectories = acceptDirectories;
    }

    /**
     * @return the fileNameExtensionFilter
     */
    public final FileNameExtensionFilter getFileNameExtensionFilter() {
        return fileNameExtensionFilter;
    }

    /**
     * @param fileNameExtensionFilter the fileNameExtensionFilter to set
     */
    public final void setFileNameExtensionFilter(final FileNameExtensionFilter fileNameExtensionFilter) {
        this.fileNameExtensionFilter = fileNameExtensionFilter;
    }

    /**
     * @return
     * @see FileNameExtensionFilter#hashCode();
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public int hashCode() {
        return fileNameExtensionFilter.hashCode();
    }

    /**
     * @param obj
     * @return
     * @see FileNameExtensionFilter#equals(Object);
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return fileNameExtensionFilter.equals(obj);
    }

    /**
     * @return
     * @see javax.swing.filechooser.FileNameExtensionFilter#getDescription()
     */
    public String getDescription() {
        return fileNameExtensionFilter.getDescription();
    }

    /**
     * @return
     * @see javax.swing.filechooser.FileNameExtensionFilter#getExtensions()
     */
    public String[] getExtensions() {
        return fileNameExtensionFilter.getExtensions();
    }

    /**
     * @return
     * @see javax.swing.filechooser.FileNameExtensionFilter#toString()
     */
    @Override
    public String toString() {
        return fileNameExtensionFilter.toString();
    }

    @Override
    public boolean accept(final File f) {
        if (!acceptDirectories && f != null && f.isDirectory()) {
            return false;
        }
        return fileNameExtensionFilter.accept(f);
    }
}
