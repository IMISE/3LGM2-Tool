package de.imise.tool3lgm.graphtools.model;

import static de.imise.tool3lgm.Tool3lgmConstants.getResString;
import static de.imise.tool3lgm.log.Log.ERROR;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import de.imise.tool3lgm.log.Log;

/**
 * @author AXS (23 Mar 2019)
 */
public class GDCollectionIconTable extends HashMap<String, byte[]> {

    /**
     * COMMENTME
     */
    private int iconCounter = 0;

    /**
     * @param iconPath
     * @return
     */
    public final String loadIcon(final File iconPath) {
        String iconKey = null;
        try {
            RandomAccessFile imf = new RandomAccessFile(iconPath, "r");
            byte[] img = new byte[(int) imf.length()];
            imf.read(img);
            iconKey = keyOf(img);
            if (iconKey == null) {
                iconKey = "IMG_" + new Date().getTime() + iconCounter++ + ".gif";
                put(iconKey, img);
            }
            imf.close();
        } catch (Exception e) {
            Log.show(ERROR, getResString("FehlerAllgemein"), e);
        }
        return iconKey;
    }

    /**
     * @param entry
     * @return
     */
    private final String keyOf(final byte[] entry) {
        for (String key : keySet()) {
            if (Arrays.equals(get(key), entry)) {
                return key;
            }
        }
        return null;
    }

    /**
     * @param iconID
     * @return
     */
    public ImageIcon getIcon(final String iconID) {
        ImageIcon icon = null;
        try {
            byte[] imageData = get(iconID);
            icon = new ImageIcon(imageData);
        } catch (Exception ex) {
            Log.show(Log.ERROR, getResString("FehlerAllgemein"), ex);
            JOptionPane.showMessageDialog(null, getResString("icon_kaputt"), getResString("fehler"), JOptionPane.ERROR_MESSAGE);
        }
        return icon;
    }
}
