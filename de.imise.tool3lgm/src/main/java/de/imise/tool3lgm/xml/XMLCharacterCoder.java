/*
 * Created on 01.12.2003
 */
package de.imise.tool3lgm.xml;

/**
 * TODO:AXS: prüfen, ob das über vordefinierte CharSets geht
 * 
 * @author Thomas Rudert
 */
public abstract class XMLCharacterCoder {

    public static String encodeString(final String input) {
        if (input == null) {
            return "";
        }
        StringBuilder buffer = new StringBuilder(input.length() * 2);
        for (int i = 0; i < input.length(); i++) {
            if (isValidCharacter(input.charAt(i))) {
                buffer.append(input.charAt(i));
            } else {
                buffer.append("&#x" + getHexCode(input.charAt(i)) + ";");
            }
        }

        return buffer.toString();
    }

    private static boolean isValidCharacter(final char character) {
        if (character >= '0' && character <= '9') {
            return true;
        }
        if (character >= 'a' && character <= 'z') {
            return true;
        }
        if (character >= 'A' && character <= 'Z') {
            return true;
        }

        switch (character) {
        case '_':
            return true;
        case '-':
            return true;
        case '+':
            return true;
        case ' ':
            return true;
        case '.':
            return true;
        }
        return false;
    }

    public static String decodeString(final String input) {
        if (input == null) {
            return "";
        }
        String[] splited = input.split("&#x");
        StringBuilder result = new StringBuilder();
        int index;
        for (int i = 0; i < splited.length; i++) {
            index = splited[i].indexOf(';');
            if (index == -1) {
                result.append(splited[i]);
            } else {
                result.append(getCharacter(splited[i].substring(0, index)) + splited[i].substring(index + 1));
            }
        }
        return result.toString();
    }

    public static String getHexCode(final char character) {
        String string = Integer.toHexString(character);
        switch (string.length()) {
        case 1:
            return "000" + string;
        case 2:
            return "00" + string;
        case 3:
            return "0" + string;
        case 4:
            return string;
        default:
            return "0000";
        }
    }

    public static char getCharacter(final String hexCode) {
        if (hexCode == null) {
            return '\u0000';
        }
        return (char) Integer.parseInt(hexCode, 16);
    }
}
