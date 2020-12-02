package de.imise.tool3lgm.gui.viewpane.matrix;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Repräsentiert einen Legendeneintrag in der <code>TableToolBar</code>.<br>
 * Diese Einträge werden angezeigt, wenn in der ToolBar eine gültige Auswahl von
 * 2 Klassen vorgenommen wurde, für die die Verbindungsmatrix angezeigt werden
 * soll.
 *
 * @author Thomas Rudert
 */
public class MatrixViewLegendItem extends JPanel {

    /**
     * Farbe der Felder, die dieses Legenden-Item erklärt
     */
    private final Color color;

    /**
     * Beschreibung der Felder, die dieses Legenden-Item erklärt
     */
    private final String description;
    private String descriptionPrefix = null;
    private String descriptionPostfix = null;

    /**
     * Wenn <code>true</code> wird der jeweilige BEschreibungteil Fett
     * dargestellt
     */
    boolean boldDescriptionPrefix = false;
    boolean boldDescription = false;
    boolean boldDescriptionPostfix = false;

    /**
     * @param description
     * @param color
     */
    public MatrixViewLegendItem(final int i, final String description, final Color color) {
        this(null, description, null, color);
    }

    /**
     * @param descriptionPrefix
     * @param description
     * @param descriptionPostfix
     * @param color
     */
    public MatrixViewLegendItem(final String descriptionPrefix, final String description, final String descriptionPostfix, final Color color) {
        this(descriptionPrefix, description, descriptionPostfix, color, false, descriptionPrefix != null && descriptionPostfix != null, false);
    }

    /**
     * @param descriptionPrefix
     * @param description
     * @param descriptionPostfix
     * @param color
     */
    public MatrixViewLegendItem(final String descriptionPrefix, final String description, final String descriptionPostfix, final Color color, final boolean boldDescriptionPrefix, final boolean boldDescription, final boolean boldDescriptionPostfix) {
        this.descriptionPrefix = descriptionPrefix;
        this.description = description;
        this.descriptionPostfix = descriptionPostfix;
        this.color = color;
        this.boldDescriptionPrefix = boldDescriptionPrefix;
        this.boldDescription = boldDescription;
        this.boldDescriptionPostfix = boldDescriptionPostfix;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        Font font = g.getFont();
        Font boldFont = font.deriveFont(Font.BOLD);
        int xStart = 15;
        if (descriptionPrefix != null) {
            if (boldDescriptionPrefix) {
                g.setFont(boldFont);
            } else {
                g.setFont(font);
            }
            String s = descriptionPrefix + " ";
            g.drawString(s, xStart, 9);
            xStart += g.getFontMetrics().getStringBounds(s, g).getWidth();
        }
        if (description != null) {
            if (boldDescription) {
                g.setFont(boldFont);
            } else {
                g.setFont(font);
            }
            String s = description + " ";
            g.drawString(s + " ", xStart, 9);
            xStart += g.getFontMetrics().getStringBounds(s, g).getWidth();
        }
        if (descriptionPostfix != null) {
            if (boldDescriptionPostfix) {
                g.setFont(boldFont);
            } else {
                g.setFont(font);
            }
            String s = descriptionPostfix;
            g.drawString(s + " ", xStart, 9);
        }
        g.setFont(font);
        g.setColor(color);
        g.fillRect(0, 0, 10, 10);
    }

    /**
     * @return
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @return the descriptionPrefix
     */
    public final String getDescriptionPrefix() {
        return descriptionPrefix;
    }

    /**
     * @return the descriptionPostfix
     */
    public final String getDescriptionPostfix() {
        return descriptionPostfix;
    }

}
