package de.imise.util.pair;

import java.awt.Graphics;

/**
 * Einfach 2 Punktkoordinaten
 * 
 * @author AXS
 */
public class PointPair {

	/**
	 * COMMENTME
	 */
	public int x1, y1, x2, y2;
	
	/**
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public PointPair(int x1, int y1, int x2, int y2) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	/**
	 * @param pointPair
	 */
	public PointPair(PointPair pointPair) {
		super();
		this.x1 = pointPair.x1;
		this.y1 = pointPair.y1;
		this.x2 = pointPair.x2;
		this.y2 = pointPair.y2;
	}

	
	/**
	 * @return
	 */
	public int getX1() {
		return x1;
	}

	/**
	 * @param x1
	 */
	public void setX1(int x1) {
		this.x1 = x1;
	}

	/**
	 * @return
	 */
	public int getY1() {
		return y1;
	}

	/**
	 * @param y1
	 */
	public void setY1(int y1) {
		this.y1 = y1;
	}

	/**
	 * @return
	 */
	public int getX2() {
		return x2;
	}

	/**
	 * @param x2
	 */
	public void setX2(int x2) {
		this.x2 = x2;
	}

	/**
	 * @return
	 */
	public int getY2() {
		return y2;
	}

	/**
	 * @param y2
	 */
	public void setY2(int y2) {
		this.y2 = y2;
	}

	/**
	 * @param source
	 */
	public void setValues(PointPair source) {
		x1 = source.x1;
		y1 = source.y1;
		x2 = source.x2;
		y2 = source.y2;
	}
	
	/**
	 * @return
	 * 		Absolute (alsways positve) difference between x1 and x2.
	 * 
	 */
	public int getWidth() {
		int width = x2 - x1;
		if (width < 0)
			width *= -1;
		return width;
	}
	
	/**
	 * @return
	 * 		Absolute (alsways positve) difference between y1 and y2.
	 * 
	 */
	public int getHeight() {
		int height = y2 - y1;
		if (height < 0)
			height *= -1;
		return height;
	}
	
	/**
	 * Malt mit den aktuellen Zeichenenstellungen von <code>g</code> ein Rechteck auf <code>g</code>.
	 * @param g
	 */
	public void drawRect(Graphics g) {
		g.drawLine(x1, y1, x1, y2);
		g.drawLine(x1, y2, x2, y2);
		g.drawLine(x2, y2, x2, y1);
		g.drawLine(x2, y1, x1, y1);
	}

	@Override
	public String toString() {
		return "PointPair [x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2=" + y2 + "]";
	}
	
	
}
