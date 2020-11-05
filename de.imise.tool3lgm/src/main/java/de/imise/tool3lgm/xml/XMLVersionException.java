/*
 * Created on 19.01.2004
 */
package de.imise.tool3lgm.xml;

/**
 * @author thomas
 */
public class XMLVersionException extends Exception {

    /**
     *
     */
    public XMLVersionException() {
        super();
    }

    /**
     * @param message
     */
    public XMLVersionException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public XMLVersionException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public XMLVersionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
