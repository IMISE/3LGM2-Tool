package de.imise.util.swing;

import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import de.imise.util.Alphabetical;
import de.imise.util.Sys;

/**
 * Creates for every font a JTextPane and renders a sample text
 *
 * @author Ich (06.05.2021)
 */
public class FontTest {

    private static void setLookAndFeel() {
        try {
            if (System.getProperty("os.name").startsWith("Windows")) {
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            } else {
                //auf dem Mac gibt es mit dem Apple-Glas-Look-And-Feel totale Probleme, wenn man viele InternalFrames
                //verwendet, was unser Tool tut. Viele beginnt hier bereits bei ca. 5. Da beginnt es schlimm zu werden
                //und ab 10 friert das Tool immer mal für ne Minute ein.
                // javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (Exception ex) {
            System.exit(-1);
        }
    }

    public static void main(final String[] args) {

        setLookAndFeel();

        //these fonts are the intersection between Windows and Mac
        String windowsAndAppleFonts[] = {
                "Arial",
                "Arial Black",
                "Arial Narrow",
                "Arial Rounded MT Bold",
                "Baskerville Old Face",
                "Bauhaus 93",
                "Bell MT",
                "Bernard MT Condensed",
                "Book Antiqua",
                "Bookman Old Style",
                "Britannic Bold",
                "Brush Script MT",
                "Calisto MT",
                "Century",
                "Century Gothic",
                "Century Schoolbook",
                "Colonna MT",
                "Comic Sans MS",
                "Cooper Black",
                "Copperplate Gothic Bold",
                "Copperplate Gothic Light",
                "Courier New",
                "Curlz MT",
                "Dialog",
                "DialogInput",
                "Edwardian Script ITC",
                "Engravers MT",
                "Footlight MT Light",
                "Garamond",
                "Georgia",
                "Gill Sans Ultra Bold",
                "Gloucester MT Extra Condensed",
                "Goudy Old Style",
                "Haettenschweiler",
                "Harrington",
                "Impact",
                "Imprint MT Shadow",
                "Lucida Bright",
                "Lucida Calligraphy",
                "Lucida Fax",
                "Lucida Handwriting",
                "Lucida Sans",
                "Lucida Sans Typewriter",
                "Matura MT Script Capitals",
                "Mistral",
                "Modern No. 20",
                "Monospaced",
                "Monotype Corsiva",
                "MS Gothic",
                "MS PGothic",
                "MT Extra",
                "Onyx",
                "Papyrus",
                "Perpetua Titling MT",
                "Playbill",
                "Rockwell",
                "Rockwell Extra Bold",
                "SansSerif",
                "Serif",
                "SimSun",
                "Stencil",
                "Symbol",
                "Tahoma",
                "Times New Roman",
                "Trebuchet MS",
                "Verdana",
                "Webdings",
                "Wide Latin",
                "Wingdings",
                "Wingdings 2",
                "Wingdings 3",
        };
        String systemFonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        List<String> appleFontNames = Arrays.asList(windowsAndAppleFonts);
        for (int i = 0; i < appleFontNames.size(); i++) {
            appleFontNames.set(i, appleFontNames.get(i).trim());
        }
        List<String> windowsFontNames = Arrays.asList(systemFonts);

        Font font = UIManager.getFont("Label.font");
        String fontfamily = font.getFamily();
        Sys.out(fontfamily);

        Set<String> fontsSet = windowsFontNames.stream().distinct().filter(appleFontNames::contains).collect(Collectors.toSet());
        List<String> fonts = new ArrayList<>(fontsSet);
        Alphabetical.sort(fonts);

        for (int i = 0; i < fonts.size(); i++) {
            System.out.println("\"" + fonts.get(i) + "\",");
        }

        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new GridLayout(fonts.size(), 1));
        Container contentPane = frame.getContentPane();
        contentPane.add(new JScrollPane(panel));

        for (int i = 0; i < fonts.size(); i++) {
            String fontName = fonts.get(i);
            String text = "<html><body style=\"font-family: " + fontName + ";font-size:" + "1.0em" + ";\">" + "Dies ist ein Label  "
                    + "<a href=\"http://www.google.com/finance?q=NYSE:C\">C</a>, <a href=\"http://www.google.com/finance?q=NASDAQ:MSFT\">MSFT</a>   " + fontName + "</body></html>";

            JLabel label = new JLabel(text);
            panel.add(label);
        }

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

}
