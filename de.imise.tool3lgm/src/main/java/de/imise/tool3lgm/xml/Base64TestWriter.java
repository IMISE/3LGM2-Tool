package de.imise.tool3lgm.xml;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import de.imise.util.image.ImageTools;

public class Base64TestWriter {

    public static final int LINE_LENGTH = 76;

    public static final String LINE_END = "&#13;";

    public static final String IMAGE_PATH = "/Applications/3LGM2Tool3.3.9/Symbols/symb_pc.gif";

    public static void main(final String[] args) {
        File file = new File(IMAGE_PATH);
        byte[] imgData = loadImageData(file);

        ImageIcon icon = new ImageIcon(imgData);

        BufferedImage bufferedImageFromIcon = ImageTools.toBufferedImage(icon);

        BufferedImage bufferedImageLoaded = loadBufferedImage(file);

        printBufferedImage(bufferedImageFromIcon);
        printBufferedImage(bufferedImageLoaded);

        //        System.err.println("Native Data");
        //        System.err.println(new String(imgData));
        //        System.err.println();
        //        System.err.println("tlgmBase64 Data");
        //        System.err.println(Base64.encode(imgData));
        //        System.err.println();
        //        System.err.println("apache Data");
        //        System.err.println(new String(new org.apache.commons.codec.binary.Base64().encode(imgData)));
        //        printBufferedImage(file);
    }

    private static void printBufferedImage(final BufferedImage img) {
        System.err.println();
        System.err.println("BufferedImage");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String encodedImage = "nix";
        try {
            ImageIO.write(img, "png", baos);
            baos.flush();
            System.err.println(baos.toByteArray().length);
            encodedImage = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());
            baos.close(); // should be inside a finally block
        } catch (IOException e) {
            e.printStackTrace();
        }
        printLines(encodedImage);
    }

    private static void printLines(final String s) {
        System.err.println(s);
        System.err.println(s.length());
        for (int i = 0; i * LINE_LENGTH < s.length(); i++) {
            String line = getLine(s, i);
            System.err.println(line);
        }
    }

    private static String getLine(final String s, final int line) {
        int start = line * LINE_LENGTH;
        int fullLength = s.length();
        if (start >= fullLength) {
            return null;
        }
        int end = start + LINE_LENGTH;
        String sub = s.substring(start, end < fullLength ? end : fullLength);
        sub += LINE_END;
        return sub;
    }

    /**
     * @param iconPath
     * @return
     */
    public static final byte[] loadImageData(final File file) {
        byte[] img = new byte[0];
        try {
            RandomAccessFile imf = new RandomAccessFile(file, "r");
            img = new byte[(int) imf.length()];
            imf.read(img);
            imf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }

    private static BufferedImage loadBufferedImage(final File file) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("Datei nicht lesbar!");
        }
        return bi;
    }

}
