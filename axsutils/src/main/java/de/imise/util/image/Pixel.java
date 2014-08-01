package de.imise.util.image;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import de.imise.util.robot.ScreenRobot;

/**
 * Erweitert die Klasse <code>Point</code> um Farbinformationen.
 * @author AXS
 */
public class Pixel extends Point {

	/**
	 * Farbe dieses Pixels
	 */
	protected int rgb;

	/**
	 * Minimaler Rotwert, der noch als gleich zu der korrekten Farbe dieses Pixels anerkannt werden soll
	 */
	protected int minR;

	/**
	 * Maximaler Rotwert, der noch als gleich zu der korrekten Farbe dieses Pixels anerkannt werden soll
	 */
	protected int maxR;
	
	/**
	 * Minimaler Grünwert, der noch als gleich zu der korrekten Farbe dieses Pixels anerkannt werden soll
	 */
	protected int minG;

	/**
	 * Maximaler Grünwert, der noch als gleich zu der korrekten Farbe dieses Pixels anerkannt werden soll
	 */
	protected int maxG;
	/**
	 * Minimaler Blauwert, der noch als gleich zu der korrekten Farbe dieses Pixels anerkannt werden soll
	 */
	protected int minB;

	/**
	 * Maximaler Blauwert, der noch als gleich zu der korrekten Farbe dieses Pixels anerkannt werden soll
	 */
	protected int maxB;
	/**
	 * <code>false</code> bedeutet, der Pixel soll genau die Farbe <code>rgb</code> haben, <code>true</code>
	 * bedeutet, er soll genau diese Farbe nicht haben. 
	 */
	protected boolean invers;
	
	/**
	 * Definiert einen Pixel und dessen Farbe.
	 * @param x
	 * @param y
	 * @param color
	 */
	public Pixel(int x, int y, Color color) {
		this(x, y, color, false);
	}

	/**
	 * Definiert einen Pixel und dessen Farbe.
	 * @param point
	 * @param color
	 */
	public Pixel(Point point, Color color) {
		this(point, color, false);
	}

	/**
	 * @param point
	 * @param color
	 * @param tolerance
	 */
	public Pixel(Point point, Color color, int tolerance) {
		this(point.x, point.y, color, tolerance, false);
	}

	/**
	 * Definiert einen Pixel und dessen Farbe.
	 * @param point
	 * @param color
	 * @param invers
	 */
	public Pixel(Point point, Color color, boolean invers) {
		this(point.x, point.y, color, invers);
	}
	
	/**
	 * Definiert einen Pixel und dessen Farbe.
	 * @param x
	 * @param y
	 * @param color
	 * @param invers
	 */
	public Pixel(int x, int y, Color color, boolean invers) {
		this(x, y, color, 0, invers);
	}

	/**
	 * Definiert einen Pixel, dessen Farbe, ob er invers ist und wie stark ein
	 * Vergleichspixel in den Farbwerten rot, grün und blau abweichen darf.<br>
	 * @param x
	 * @param y
	 * @param rgb
	 * @param tolerance
	 */
	public Pixel(int x, int y, int rgb, int tolerance) {
		this(x, y, new Color(rgb), tolerance, false);
	}

	
	/**
	 * Definiert einen Pixel, dessen Farbe und wie stark ein
	 * Vergleichspixel in den Farbwerten rot, grün und blau abweichen darf.<br>
	 * @param x
	 * @param y
	 * @param color
	 * @param tolerance
	 */
	public Pixel(int x, int y, Color color, int tolerance) {
		this(x, y, color, tolerance, false);
	}

	/**
	 * Definiert einen Pixel, dessen Farbe, ob er invers ist und wie stark ein
	 * Vergleichspixel in den Farbwerten rot, grün und blau abweichen darf.<br>
	 * @param x
	 * @param y
	 * @param color
	 * @param tolerance
	 */
	public Pixel(int x, int y, Color color, int tolerance, boolean invers) {
		super(x, y);
		this.invers = invers;
		rgb = color.getRGB();
		setTolerance(tolerance);
	}

	
	/**
	 * Liefert den RGB-Wert der Farbe dieses Pixels
	 * @return
	 */
	public int getRGB(){
		return rgb;
	}
	
	/**
	 * @return
	 */
	public boolean isPixelOnScreen(){
		return isPixelOnScreen(0, 0);
	}
	
	/**
	 * @param offset
	 * @return
	 */
	public boolean isPixelOnScreen(Point offset){
		return isPixelOnScreen(offset.x, offset.y);
	}

	/**
	 * @param offset
	 * @return
	 */
	public boolean isPixelOnScreen(int offsetX, int offsetY){
		return similar(ScreenRobot.getScreenColor(x+offsetX, y+offsetY));
	}

	/**
	 * @param image
	 * @return
	 */
	public boolean isPixelInImage(BufferedImage image){
		return isPixelInImage(image, 0, 0);
	}

	/**
	 * Liefert <code>true</code>, wenn dieser Pixel in den Toleranzen mit dem übergebenen Punkt
	 * im übergebenen Bild übereinstimmt.
	 * @param image
	 * @return
	 */
	public boolean isPixelInImage(BufferedImage image, int offsetX, int offsetY){
		return similar(new Color(image.getRGB(x+offsetX, y+offsetY)));
	}

	/**
	 * Liefert <code>true</code>, wenn die Farbe des Pixels in den Toleranzen mit der übergebenen
	 * Farbe übereinstimmt.
	 * @param color
	 * @return
	 */
	public boolean similar(Color color){
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		if (invers)
			return (r<minR && g<minG && b<minB) || (r>maxR && g>maxG && b>maxB);
		return (r>=minR && g>=minG && b>=minB) && (r<=maxR && g<=maxG && b<=maxB);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof Pixel))
			return false;
		return ((Pixel)obj).getRGB() == getRGB();
	}

	/**
	 * @return
	 */
	public boolean isInvers() {
		return invers;
	}
	
	/**
	 * @param tolerance
	 */
	public void setTolerance(int tolerance){
		Color color = new Color(rgb);
		int r = color.getRed()-tolerance;
		if (r<0)r=0;
		int g = color.getGreen()-tolerance;
		if (g<0)g=0;
		int b = color.getBlue()-tolerance;
		if (b<0)b=0;
		minR = r;
		minG = g;
		minB = b;

		r = color.getRed()+tolerance;
		if (r>255)r=255;
		g = color.getGreen()+tolerance;
		if (g>255)g=255;
		b = color.getBlue()+tolerance;
		if (b>255)b=255;
		maxR = r;
		maxG = g;
		maxB = b;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.setLength(sb.length()-1);
		sb.append(", ");
		sb.append(new Color(rgb));
		sb.append(", invers=");
		sb.append(invers);
		sb.append("]");
		return sb.toString();
	}

}
