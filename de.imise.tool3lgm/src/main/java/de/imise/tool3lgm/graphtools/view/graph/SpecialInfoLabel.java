/*
 * Created on 18.12.2003
 */
package de.imise.tool3lgm.graphtools.view.graph;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;

import de.imise.tool3lgm.graphtools.view.container.AdditionalLabelTextGenerator;
import de.imise.util.HTMLConverter;

/**
 * @author AXS
 */
public class SpecialInfoLabel extends JLabel {

    /**
     * COMMENTME
     */
    private final ObjectAndText ownerAndText;

    /**
     * @param xmlText
     * @param horizontalAlignment
     */
    public SpecialInfoLabel(final AdditionalLabelTextGenerator specialInfoOwner, final String info, final boolean addInNewLine) {
        ownerAndText = new ObjectAndText();
        add(specialInfoOwner, info, addInNewLine);
    }

    /**
     * @param owner
     * @param xmlText
     */
    public void addSpecialInfoParent(final AdditionalLabelTextGenerator owner, final String text) {
        ownerAndText.add(owner, text);
        buildInfo();
    }

    /**
     * @param specialInfoOwner
     * @param info
     */
    public void add(final AdditionalLabelTextGenerator specialInfoOwner, final String info) {
        add(specialInfoOwner, info, false);
    }

    /**
     * @param specialInfoOwner
     * @param info
     * @param addInNewLine
     */
    public void add(final AdditionalLabelTextGenerator specialInfoOwner, final String info, final boolean addInNewLine) {
        StringBuilder text2Insert = null;
        GraphElementLayout layout = specialInfoOwner.getLayout();
        if (layout == null || layout.getFont() == null) {
            if (addInNewLine) {
                text2Insert = new StringBuilder("<BR />");
                text2Insert.append(info);
            } else {
                text2Insert = new StringBuilder(info);
            }
            addSpecialInfoParent(specialInfoOwner, text2Insert.toString());
            return;
        }
        if (addInNewLine) {
            text2Insert = new StringBuilder("<BR><FONT face=\"");
        } else {
            text2Insert = new StringBuilder("<FONT face=\"");
        }
        String fontName = null;
        int fontSize = -1;
        Color fontColor = null;
        int fontStyle = -1;
        fontName = layout.getFont().getName();
        if (fontName == null) {
            fontName = GraphElementLayout.STANDARD_FONT_NAME;
        }
        fontSize = layout.getFont().getSize();
        if (fontSize == -1) {
            fontSize = GraphElementLayout.STANDARD_FONT_SIZE;
        }
        fontSize /= 3;
        fontColor = layout.bg_color;
        if (fontColor == null) {
            fontColor = GraphElementLayout.STANDARD_FONT_COLOR;
        }
        fontStyle = layout.getFont().getStyle();
        if (fontStyle == -1) {
            fontStyle = GraphElementLayout.STANDARD_FONT_STYLE;
        }

        text2Insert.append(fontName);
        text2Insert.append("\" size=\"" + fontSize + "\" color=\"");
        HTMLConverter.appendHTMLColor(text2Insert, fontColor);
        switch (fontStyle) {
        case Font.PLAIN:
            text2Insert.append("\">");
            text2Insert.append(info);
            text2Insert.append("</FONT>");
            break;
        case Font.BOLD:
            text2Insert.append("\"><b>");
            text2Insert.append(info);
            text2Insert.append("</b></FONT>");
            break;
        case Font.ITALIC:
            text2Insert.append("\"><i>");
            text2Insert.append(info);
            text2Insert.append("</i></FONT>");
        }
        addSpecialInfoParent(specialInfoOwner, text2Insert.toString());
    }

    /**
     * @param owner
     */
    public void removeSpecialInfoParent(final Object owner) {
        if (owner == null) {
            ownerAndText.clear();
            setText(null);
        }
        if (ownerAndText.removeEvery(owner)) {
            buildInfo();
        }
    }

    /**
     *
     */
    private void buildInfo() {
        StringBuilder sb = new StringBuilder("<HTML><BODY>");
        for (int i = 0; i < ownerAndText.getSize(); i++) {
            sb.append(ownerAndText.getString(i));
        }
        sb.append("</HTML></BODY>");
        setText(sb.toString());
        setSize(getPreferredSize());
        //		System.out.println("Ein Label mit dem Text " + this.getText() + " wurde gebuildet :)");
    }

    /**
     * @return
     */
    public int getSpecialInfoOwnerAndTextSize() {
        return ownerAndText.getSize();
    }

    /**
     * @author AXS
     */
    private class ObjectAndText {

        /**
         * COMMENTME
         */
        private final ArrayList<Object> objects;

        /**
         * COMMENTME
         */
        private final ArrayList<String> strings;

        /**
         *
         */
        public ObjectAndText() {
            objects = new ArrayList<Object>();
            strings = new ArrayList<String>();
        }

        /**
         * @param o
         * @param s
         */
        public void add(final Object o, final String s) {
            objects.add(o);
            strings.add(s);
        }

        /**
         * @param o
         * @return
         */
        public boolean removeEvery(final Object o) {
            int size = objects.size();
            for (int i = size - 1; i >= 0; i--) {
                if (o == objects.get(i)) {
                    objects.remove(i);
                    strings.remove(i);
                }
            }
            if (size != objects.size()) {
                return true;
            }
            return false;
        }

        /**
         *
         */
        public void clear() {
            objects.clear();
            strings.clear();
        }

        /**
         * @return
         */
        public int getSize() {
            return objects.size();
        }

        /**
         * @param index
         * @return
         */
        public Object getString(final int index) {
            return strings.get(index);
        }

    }

}
