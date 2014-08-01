package de.imise.util.image;

import java.awt.image.BufferedImage;

public class PixelImage {

	/**
	 * Array der eigentlichen Bilddaten
	 */
	private Pixel[][] data;
	
	/**
	 * Variablen für die Höhe und Breite des Bildes
	 */
	private int w, h;
	
	/**
	 * 
	 * @param image
	 * @param tolerance
	 */
	public PixelImage(BufferedImage image, int tolerance){
		super();
		w = image.getWidth();
		h = image.getHeight();
		data = new Pixel[w][h];
		for (int x=0; x<w; x++)
			for (int y=0; y<h; y++)
				data[x][y] = new Pixel(x, y, image.getRGB(x, y), tolerance);
	}
	
	/**
	 * Liefert die Höhe des Bildes
	 * @return
	 */
	public int getHeight() {
		return h;
	}

	/**
	 * Liefert die Breite des Bildes
	 * @return
	 */
	public int getWidth() {
		return w;
	}

	/**
	 * Liefert <code>true</code>, wenn die Bilder gleich groß sind und kein Pixel um mehr als 
	 * die angegebene tolerance von denen des übergebenen Bildes abweicht.
	 * @param image
	 * @return
	 * @see Pixel#isPixelInImage(BufferedImage)
	 */
	public boolean similar(BufferedImage image){
		int w1 = image.getWidth();
		int h1 = image.getHeight();

		if (w1!=w || h1!=h)
			return false;
		
		for (int x=0; x<w; x++)
			for (int y=0; y<h; y++)
				if (!data[x][y].isPixelInImage(image))
					return false;
		return true;
	}
	
}
