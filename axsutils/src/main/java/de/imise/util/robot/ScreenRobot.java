package de.imise.util.robot;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import de.imise.util.image.Pixel;

public class ScreenRobot {

    private static final Random random = new Random();

    private static int standardDelay = 30;

    /**
     * Eine <code>Robot</code>-Instanz für alle.
     */
    public static Robot robot;
    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param x
     * @param y
     */
    public static final void setMouse(final int x, final int y) {
        robot.mouseMove(x, y);
    }
    /**
     * @param p
     */
    public static final void setMouse(final Point p) {
        robot.mouseMove(p.x, p.y);
    }

    public static final void click() {
        robot.mousePress(InputEvent.BUTTON1_MASK);
        delay(10);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }

    public static final void doubleClick() {
        click();
        delay(10);
        click();
    }

    /**
     * Setzt die Maus mit einer zufälligen Abweichung im Bereich <code>tolerance</code> an die
     * angegebene Position.<br>
     * Die <code>tolerance</code> gibt die Anzahl der Pixel an, um die der Punkt, an den die Maus
     * dann tatsächlich bewegt wird, in positiver sowie negativer X- und Y-Richtung abweichen kann.
     *
     * @param p
     * @param tolerance
     */
    public static final void setMouse(final Point p, int tolerance) {
        tolerance = random.nextInt(tolerance);
        int factor = random.nextInt(2);
        if (factor == 0) {
            factor = -1;
        }
        robot.mouseMove(p.x + tolerance * factor, p.y + tolerance * factor);
    }

    /**
     * Rechteck, Maße des gesamten Bildschirms
     */
    private static Rectangle fullScreenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    /**
     * Bestimmt das Rechteck neu, das den ganzen Bildschirm darstellt
     */
    public static final void refreshFullScreenRect() {
        fullScreenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    }

    /**
     * Liefert einen Screenshot des gesamten Bildschirms
     *
     * @return
     */
    public static final BufferedImage getScreenShot() {
        return robot.createScreenCapture(fullScreenRect);
    }

    /**
     * Liefert einen Screenshot eines Ausschnittes des gesamten Bildschirms, der
     * druch das übergebene <code>Rectangle</code> festgelegt ist.
     *
     * @return
     */
    public static final BufferedImage getScreenShot(final Rectangle r) {
        return robot.createScreenCapture(r);
    }

    /**
     * Liefert einen Screenshot der übergebenen Ausmaße.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param store
     * @return
     */
    public static final BufferedImage getScreenShot(final int x, final int y, final int w, final int h) {
        return getScreenShot(new Rectangle(x, y, w, h));
    }

    /**
     * Liefert die Farbe des Pixels an der angegebenen Stelle auf dem Bildschirm
     *
     * @param p
     * @return
     */
    public static final Color getScreenColor(final Pixel p) {
        return getScreenColor(p.x, p.y);
    }

    /**
     * Liefert die Farbe des Punktes an der angegebenen Stelle auf dem Bildschirm
     *
     * @param p
     * @return
     */
    public static final Color getScreenColor(final Point p) {
        return getScreenColor(p.x, p.y);
    }

    /**
     * Liefert die Farbe an der angegebenen Stelle auf dem Bildschirm
     *
     * @param x
     * @param y
     * @return
     */
    public static final Color getScreenColor(final int x, final int y) {
        return robot.getPixelColor(x, y);
    }
    /**
     * @return the robot
     */
    public static final Robot getRobot() {
        return robot;
    }

    /**
     * @param mask
     *            KeyEvent id
     */
    public static void pressKey(final int mask) {
        robot.keyPress(mask);
        delay();
        robot.keyRelease(mask);
        delay();
    }

    /**
     * @param mask1
     *            KeyEvent id
     * @param mask2
     *            KeyEvent id
     */
    public static void pressKey(final int mask1, final int mask2) {
        robot.keyPress(mask1);
        delay();
        pressKey(mask2);
        robot.keyRelease(mask1);
        delay();
    }

    /**
     * @param mask1
     *            KeyEvent id
     * @param mask2
     *            KeyEvent id
     * @param mask3
     *            KeyEvent id
     */
    public static void pressKey(final int mask1, final int mask2, final int mask3) {
        robot.keyPress(mask1);
        delay();
        pressKey(mask2, mask3);
        robot.keyRelease(mask1);
        delay();
    }

    /**
     * @param ms
     * @see java.awt.Robot#delay(int)
     */
    public static final void delay(final int ms) {
        robot.delay(ms);
    }

    /**
     * @param ms
     * @see java.awt.Robot#delay(int)
     */
    public static final void delay() {
        delay(standardDelay);
    }
    /**
     * @return the standardDelay
     */
    public static final int getStandardDelay() {
        return standardDelay;
    }
    /**
     * @param standardDelay the standardDelay to set
     */
    public static final void setStandardDelay(final int standardDelay) {
        ScreenRobot.standardDelay = standardDelay;
    }

    public static void type(final CharSequence characters) {
        int length = characters.length();
        for (int i = 0; i < length; i++) {
            char character = characters.charAt(i);
            type(character);
        }
    }

    public static void type(final char character) {
        switch (character) {
        case 'a':
            doType(KeyEvent.VK_A);
            break;
        case 'b':
            doType(KeyEvent.VK_B);
            break;
        case 'c':
            doType(KeyEvent.VK_C);
            break;
        case 'd':
            doType(KeyEvent.VK_D);
            break;
        case 'e':
            doType(KeyEvent.VK_E);
            break;
        case 'f':
            doType(KeyEvent.VK_F);
            break;
        case 'g':
            doType(KeyEvent.VK_G);
            break;
        case 'h':
            doType(KeyEvent.VK_H);
            break;
        case 'i':
            doType(KeyEvent.VK_I);
            break;
        case 'j':
            doType(KeyEvent.VK_J);
            break;
        case 'k':
            doType(KeyEvent.VK_K);
            break;
        case 'l':
            doType(KeyEvent.VK_L);
            break;
        case 'm':
            doType(KeyEvent.VK_M);
            break;
        case 'n':
            doType(KeyEvent.VK_N);
            break;
        case 'o':
            doType(KeyEvent.VK_O);
            break;
        case 'p':
            doType(KeyEvent.VK_P);
            break;
        case 'q':
            doType(KeyEvent.VK_Q);
            break;
        case 'r':
            doType(KeyEvent.VK_R);
            break;
        case 's':
            doType(KeyEvent.VK_S);
            break;
        case 't':
            doType(KeyEvent.VK_T);
            break;
        case 'u':
            doType(KeyEvent.VK_U);
            break;
        case 'v':
            doType(KeyEvent.VK_V);
            break;
        case 'w':
            doType(KeyEvent.VK_W);
            break;
        case 'x':
            doType(KeyEvent.VK_X);
            break;
        case 'y':
            doType(KeyEvent.VK_Y);
            break;
        case 'z':
            doType(KeyEvent.VK_Z);
            break;
        case 'A':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A);
            break;
        case 'B':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B);
            break;
        case 'C':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C);
            break;
        case 'D':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D);
            break;
        case 'E':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E);
            break;
        case 'F':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F);
            break;
        case 'G':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G);
            break;
        case 'H':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H);
            break;
        case 'I':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I);
            break;
        case 'J':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J);
            break;
        case 'K':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K);
            break;
        case 'L':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L);
            break;
        case 'M':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M);
            break;
        case 'N':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N);
            break;
        case 'O':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O);
            break;
        case 'P':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P);
            break;
        case 'Q':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q);
            break;
        case 'R':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R);
            break;
        case 'S':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S);
            break;
        case 'T':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T);
            break;
        case 'U':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U);
            break;
        case 'V':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V);
            break;
        case 'W':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W);
            break;
        case 'X':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X);
            break;
        case 'Y':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y);
            break;
        case 'Z':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z);
            break;
        case '`':
            doType(KeyEvent.VK_BACK_QUOTE);
            break;
        case '0':
            doType(KeyEvent.VK_0);
            break;
        case '1':
            doType(KeyEvent.VK_1);
            break;
        case '2':
            doType(KeyEvent.VK_2);
            break;
        case '3':
            doType(KeyEvent.VK_3);
            break;
        case '4':
            doType(KeyEvent.VK_4);
            break;
        case '5':
            doType(KeyEvent.VK_5);
            break;
        case '6':
            doType(KeyEvent.VK_6);
            break;
        case '7':
            doType(KeyEvent.VK_7);
            break;
        case '8':
            doType(KeyEvent.VK_8);
            break;
        case '9':
            doType(KeyEvent.VK_9);
            break;
        case '-':
            doType(KeyEvent.VK_MINUS);
            break;
        case '=':
            doType(KeyEvent.VK_ALT, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD1);
            //			doType(KeyEvent.VK_EQUALS);
            break;
        case '~':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_QUOTE);
            break;
        case '!':
            doType(KeyEvent.VK_EXCLAMATION_MARK);
            break;
        case '@':
            doType(KeyEvent.VK_AT);
            break;
        case '#':
            doType(KeyEvent.VK_ALT, KeyEvent.VK_NUMPAD3, KeyEvent.VK_NUMPAD5);
            //			doType(KeyEvent.VK_NUMBER_SIGN);
            break;
        case '$':
            doType(KeyEvent.VK_DOLLAR);
            break;
        case '%':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_5);
            break;
        case '^':
            doType(KeyEvent.VK_CIRCUMFLEX);
            break;
        case '&':
            doType(KeyEvent.VK_AMPERSAND);
            break;
        case '*':
            doType(KeyEvent.VK_ASTERISK);
            break;
        case '(':
            doType(KeyEvent.VK_LEFT_PARENTHESIS);
            break;
        case ')':
            doType(KeyEvent.VK_RIGHT_PARENTHESIS);
            break;
        case '_':
            doType(KeyEvent.VK_UNDERSCORE);
            break;
        case '+':
            doType(KeyEvent.VK_PLUS);
            break;
        case '\t':
            doType(KeyEvent.VK_TAB);
            break;
        case '\n':
            doType(KeyEvent.VK_ENTER);
            break;
        case '[':
            doType(KeyEvent.VK_OPEN_BRACKET);
            break;
        case ']':
            doType(KeyEvent.VK_CLOSE_BRACKET);
            break;
        case '\\':
            doType(KeyEvent.VK_BACK_SLASH);
            break;
        case '{':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_OPEN_BRACKET);
            break;
        case '}':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_CLOSE_BRACKET);
            break;
        case '|':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_BACK_SLASH);
            break;
        case ';':
            doType(KeyEvent.VK_SEMICOLON);
            break;
        case ':':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_PERIOD);
            //			doType(KeyEvent.VK_COLON);
            break;
        case '\'':
            doType(KeyEvent.VK_QUOTE);
            break;
        case '"':
            doType(KeyEvent.VK_QUOTEDBL);
            break;
        case ',':
            doType(KeyEvent.VK_COMMA);
            break;
        case '<':
            doType(KeyEvent.VK_LESS);
            break;
        case '.':
            doType(KeyEvent.VK_PERIOD);
            break;
        case '>':
            doType(KeyEvent.VK_GREATER);
            break;
        case '/':
            doType(KeyEvent.VK_SHIFT, KeyEvent.VK_7);
            //			doType(KeyEvent.VK_SLASH);
            break;
        case '?':
            doType(KeyEvent.VK_ALT, KeyEvent.VK_NUMPAD6, KeyEvent.VK_NUMPAD3);
            //			doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH);
            break;
        case ' ':
            doType(KeyEvent.VK_SPACE);
            break;
        default:
            throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    private static void doType(final int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    private static void doType(final int[] keyCodes, final int offset, final int length) {
        if (length == 0) {
            return;
        }
        robot.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        robot.keyRelease(keyCodes[offset]);
    }

}
