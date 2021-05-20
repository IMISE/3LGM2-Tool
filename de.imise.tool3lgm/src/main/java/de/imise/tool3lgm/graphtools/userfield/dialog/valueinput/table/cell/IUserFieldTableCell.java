package de.imise.tool3lgm.graphtools.userfield.dialog.valueinput.table.cell;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import de.imise.tool3lgm.graphtools.userfield.definition.type.UserField;

/**
 * Repräsentiert genau eine Zelle eines UserFieldTables.<br>
 * Ist die Editor- und die Renderer-Komponente für eine Zelle.
 *
 * @author fstephan
 */
public interface IUserFieldTableCell extends TableCellEditor, TableCellRenderer {

    /**
     * Zeichen, dass im Editor als Dezimal-Trennzeichnen angezeigt wird. Sorgt
     * dafür, dass <code>Strings</code> korrekt auf <code>Number</code> geparsed
     * werden können.
     */
    char EDITOR_DECIMAL_SEPARATOR = '.';

    /**
     * {@link UserField#EMPTY_STRING} wird im Renderer durch <code>""</code>
     * ersetzt
     */
    String RENDERER_EMPTY_STRING = "";

    /**
     * {@link UserField#EMPTY_STRING} wird im Editor durch <code>""</code>
     * ersetzt
     */
    String EDITOR_EMPTY_STRING = "";

    /** Renderer stellt Text rechtsbündig dar */
    int HORIZONTAL_ALIGNMENT_RIGHT = SwingConstants.RIGHT;

    /** Editor stellt Text linksbündig dar */
    int HORIZONTAL_ALIGNMENT_LEFT = SwingConstants.LEFT;

    /** Initiale Zellhöhe */
    int PREFERRED_CELL_HEIGHT = 20;

    /** Minimale Zellhöhe */
    int MIN_CELL_HEIGHT = 15;

    /** Initiale Zellbreite */
    int PREFERRED_CELL_WIDTH = 150;

    /** Minimal Zellbreite */
    int MIN_CELL_WIDTH = 75;

    /** Maximale Zellbreite */
    int MAX_CELL_WIDTH = 200;

    /** Initiale Zellgröße */
    Dimension PREFERRED_SIZE = new Dimension(PREFERRED_CELL_WIDTH, PREFERRED_CELL_HEIGHT);

    /** Hintergrundfarbe nicht selektierter Zellen */
    Color DEFAULT_BACKROUND_COLOR = Color.WHITE;

    /** Hintergrundfarbe selektierter Zellen */
    Color SELECTION_BACKROUND_COLOR = new Color(0, 30, 180, 85);

    /** Textfarbe der Anchor-Zelle */
    Color ANCHOR_FONT_COLOR = Color.BLACK;

    /** Umrandung der Anchor-Zelle */
    Border ANCHOR_BORDER = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.WHITE, 1), BorderFactory.createLineBorder(Color.BLACK, 1));

    /** Hintergrundfarbe deaktivierter Zellen */
    Color DEACTIVATED_CELL_BACKROUND_COLOR = Color.LIGHT_GRAY;

    /** Gibt die Renderer-Komponente dieser Zelle wieder */
    DefaultTableCellRenderer getRenderer();

    /** Gibt die Editor-Komponente dieser Zelle wieder */
    DefaultCellEditor getEditor();

    /** Gibt den angezeigten Wert der Zelle als {@link String} zurück */
    String getCellRendererValue();

}
