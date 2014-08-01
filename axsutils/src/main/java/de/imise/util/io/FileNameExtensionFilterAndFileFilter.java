package de.imise.util.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Ein {@link FileFilter} und {@link FilenameFilter}, der intern einen {@link FileNameExtensionFilter}
 * hält
 * 
 * @author AXS
 * @create 06.07.2012
 */
public class FileNameExtensionFilterAndFileFilter implements FileFilter {

	/** Der eigentliche FileNameExtensionFilter */
	private FileNameExtensionFilter fileNameExtensionFilter;

	/**
     * Creates a {@code FileNameExtensionFilterAndFileFilter} with the specified
     * description and file name extensions. The returned {@code
     * FileNameExtensionFilterAndFileFilter} will accept all directories and any
     * file with a file name extension contained in {@code extensions}.
     *
     * @param description textual description for the filter, may be
     *                    {@code null}
     * @param extensions the accepted file name extensions
     * @throws IllegalArgumentException if extensions is {@code null}, empty,
     *         contains {@code null}, or contains an empty string
     * @see #accept
     * @see javax.swing.filechooser.FileNameExtensionFilter
     */
    public FileNameExtensionFilterAndFileFilter(String description, String... extensions) {
    	this(new FileNameExtensionFilter(description, extensions));
	}

    /**
     * @param fileNameExtensionFilter
     */
    public FileNameExtensionFilterAndFileFilter(FileNameExtensionFilter fileNameExtensionFilter) {
    	super();
    	this.fileNameExtensionFilter = fileNameExtensionFilter;
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
	public final void setFileNameExtensionFilter(FileNameExtensionFilter fileNameExtensionFilter) {
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
	public boolean equals(Object obj) {
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

	/* (non-Javadoc)
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File pathname) {
		return fileNameExtensionFilter.accept(pathname);
	}

}
