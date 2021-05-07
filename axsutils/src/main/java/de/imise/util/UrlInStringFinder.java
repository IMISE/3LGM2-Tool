package de.imise.util;

import static de.imise.util.collections.CollectionUtils.arrayContains;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;

import com.google.common.base.Strings;

/**
 * @author AXS (05.05.2021)
 */
public class UrlInStringFinder {

    /**
     * @author AXS (05.05.2021)
     */
    public class UrlFinderResult {

        public String original;

        public String url;

        public File file;

        public int startIndexInOriginal;

        public int endIndexInOriginal;

        @Override
        public String toString() {
            return "original=" + original + "\nurl=" + url + "\nfile=" + file + "\nstartIndexInOriginal=" + startIndexInOriginal + "\nendIndexInOriginal=" + endIndexInOriginal;
        }

        /**
         * @return
         */
        public boolean hasUrlOrFile() {
            return !Strings.isNullOrEmpty(url) || file != null;
        }

    }

    /**  */
    private static final char[] removeStartChars = {
            ' ', '\"', '\'', '(', '[', '{'
    };

    /**  */
    private static final char[] removeEndChars = {
            ' ', '\"', '\'', '.', ',', ';', ')', ']', '}'
    };

    /**  */
    private UrlValidator validator = null;

    /**
     * @param s
     * @return
     */
    public UrlFinderResult getResult(final String s) {
        UrlFinderResult result = trim(s);
        result.file = parseFile(result.url);
        if (result.file == null) {
            result.url = parseUrl(result.url);
        }
        return result;
    }

    /**
     * @param s
     * @return
     */
    private String parseUrl(final String s) {
        if (validator == null) {
            validator = new UrlValidator();
        }
        if (validator.isValid(s)) {
            return s;
        }
        String fullUrl = "http://" + s;
        if (validator.isValid(fullUrl)) {
            return s;
        }
        return null;
    }

    /**
     * @param s
     * @return
     */
    public static File parseFile(final String s) {
        //try file absolut
        File file = new File(s);
        if (file.canRead()) {
            return file;
        }
        //try file relative
        file = new File(ApplicationManager.getApplicationDir(), s);
        if (file.canRead()) {
            return file;
        }
        return null;
    }

    /**
     * @param s
     * @return
     */
    private UrlFinderResult trim(final String s) {
        UrlFinderResult result = new UrlFinderResult();
        result.original = s;
        result.endIndexInOriginal = s.length();
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() != 0) {
            char firstChar = sb.charAt(0);
            if (!arrayContains(removeStartChars, firstChar)) {
                break;
            }
            sb.deleteCharAt(0);
            result.startIndexInOriginal++;
        }
        while (sb.length() != 0) {
            int lastIndex = sb.length() - 1;
            char lastChar = sb.charAt(lastIndex);
            if (!arrayContains(removeEndChars, lastChar)) {
                break;
            }
            sb.deleteCharAt(lastIndex);
            result.endIndexInOriginal--; //temporarily store the number of deleted chares here
        }
        result.url = sb == null ? s : sb.toString(); //here it is only the possible url (or file)
        return result;
    }

    /**
     * @param text
     * @return
     */
    public List<UrlFinderResult> getResults(final String text) {
        if (text.startsWith("\"")) {
            Sys.err1(text);
        }
        List<UrlFinderResult> results = new ArrayList<>();
        int textLength = text.length();
        int startIndex = -1;
        int endIndex = -1;
        for (int i = 0; i < textLength; i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c) || i + 1 == textLength) {
                if (startIndex < 0) {
                    continue;
                }
                endIndex = i + 1 == textLength ? textLength : i;
                String token = text.substring(startIndex, endIndex);
                UrlFinderResult result = getResult(token);
                if (result.hasUrlOrFile()) {
                    result.original = text;
                    result.startIndexInOriginal = startIndex;
                    result.endIndexInOriginal = startIndex + result.url.length();
                    results.add(result);
                }
                startIndex = -1;
                endIndex = -1;
                continue;

            } else if (startIndex >= 0) {
                continue;
            }
            startIndex = i + 1;
        }
        return results;
    }

}
