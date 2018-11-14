package de.imise.tool3lgm.graphtools.matrixview;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Repräsentiert einen Legendeneintrag in der <code>TableToolBar</code>.<br>
 * Diese Einträge werden angezeigt, wenn in der ToolBar eine gültige Auswahl von 2 Klassen vorgenommen wurde, für die die Verbindungsmatrix angezeigt werden soll.
 * 
 * @author Thomas Rudert
 */
public class TableToolBarLegendItem extends JPanel {

    /**
     * Farbe der Felder, die dieses Legenden-Item erklärt
     */
    private final Color color;

    /**
     * Beschreibung der Felder, die dieses Legenden-Item erklärt
     */
    private final String description;

    /**
     * @param description
     * @param color
     */
    public TableToolBarLegendItem(final String description, final Color color) {
        this.description = description;
        this.color = color;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.drawString(description, 15, 9);
        g.setColor(color);
        g.fillRect(0, 0, 10, 10);
    }

    /**
     * @return
     */
    public Color getColor() {
        return color;
    }

}
