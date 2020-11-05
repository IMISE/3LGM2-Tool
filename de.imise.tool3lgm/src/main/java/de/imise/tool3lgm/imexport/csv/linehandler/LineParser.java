package de.imise.tool3lgm.imexport.csv.linehandler;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Splits a line in its column tokens
 *
 * @author AXS
 * @create 06.10.2014
 */
public class LineParser {

    /** Default delimiter to split the line is TAB */
    public static final String DEFAULT_DELIM = "\t";

    /** An empty string */
    public static final String EMPTY_STRING = "";

    /** The used delimiter to split the line */
    protected final String delim;

    /** List of all tokens of the line including the dlimiter tokens */
    private final List<String> line = new ArrayList<>();

    /** Original line string */
    private String string;

    /**
     * Creates a new line parser with the default delimiter
     */
    protected LineParser() {
        this(DEFAULT_DELIM);
    }

    /**
     * Creates a new line parser with the given delimiter
     *
     * @param delim
     */
    public LineParser(final String delim) {
        this.delim = delim;
    }

    /**
     * Sets the current line to parse
     *
     * @param str line to parse
     */
    public void setLine(final String str) {
        string = str;
        StringTokenizer st = new StringTokenizer(str, delim, true);
        String token = null;
        while (st.hasMoreTokens()) {
            if (token == null) {
                token = st.nextToken();
                if (token.equals(delim)) {
                    line.add(EMPTY_STRING);
                }
            } else {
                token = st.nextToken();
                if (!delim.equals(token)) {
                    token = token.trim();
                }
            }
            line.add(token.replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t"));
        }
    }

    /**
     * @return number of tokens including the delimiter tokens
     */
    public int getTokenCount() {
        return line.size();
    }

    /**
     * @return number of defined columns
     */
    public int getColumnCount() {
        return (line.size() + 1) / 2;
    }

    /**
     * @param index index of the token to be returned
     * @return token at the given index or <code>null</code> if the given index
     *         is greater than the token count
     */
    public String getToken(final int index) {
        return getTokenCount() > index ? line.get(index) : null;
    }

    /**
     * @param index index of the column to be returned
     * @return column at the given index or <code>null</code> if the given index
     *         is greater than the column count
     */
    public String getColumn(final int index) {
        int realIndex = index * 2;
        return getToken(realIndex);
    }

    /**
     * @return original line string
     */
    public String getString() {
        return string;
    }

    /**
     * @return List of all tokens in the line including the delimiter tokens
     */
    public List<String> getLine() {
        return line;
    }

    /**
     * @param startColumn index of the column where the list should start
     * @return list of the values of all columns starting with the column at the
     *         given index
     */
    public List<String> getColumns(final int startColumn) {
        List<String> columns = new ArrayList<>();
        for (int i = startColumn; i < getColumnCount(); i++) {
            columns.add(getColumn(i));
        }
        return columns;
    }

    @Override
    public String toString() {
        return string.replaceAll(delim, " | ");
    }

}