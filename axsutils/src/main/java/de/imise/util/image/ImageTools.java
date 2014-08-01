package de.imise.util.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import de.imise.util.robot.ScreenRobot;

/**
 * @author AXS
 */
public class ImageTools {
	
	/**
	 * @param bi1
	 * @param bi2
	 * @return true, wenn die übergebenen Bilder in allen Pixeln dieselben RGB-Farbwerte besitzen
	 */
	public static final boolean equals(BufferedImage bi1, BufferedImage bi2){
		int w1 = bi1.getWidth();
		int w2 = bi2.getWidth();
		int h1 = bi1.getHeight();
		int h2 = bi2.getHeight();
		
		if (w1!=w2 || h1!=h2)
			return false;

		//Das hier ist sehr wahrscheinlich nicht richtig
		int[] rgbInt1 = bi1.getRGB(0, 0, w1, h1, null, 0, w1*h1);
		int[] rgbInt2 = bi2.getRGB(0, 0, w2, h2, null, 0, w2*h2);
		
		if (Arrays.equals(rgbInt1, rgbInt2))
			return true;
		return false;
	}
	
	/**
	 * Liefert ein Bild zurück, dass an allen Stellen, an denen mindestens eins der übergebenen
	 * Bilder die übergebenen Farbe hat, ebenfalls diese Farbe gesetzt ist, sowie auch an allen
	 * Stellen, an denen sich die übergebenen Bilder unterscheiden.<br> 
	 * Alle anderen Stellen - also die, an denen alle Bilder die gleiche Farbe haben, die aber nicht
	 * mit der übergebenen Farbe identisch ist, behalten diese in allen Bildern gleiche Farbe.<br>
	 * Alle Bilder müssen gleich groß sein!
	 * @param images
	 * @param color
	 * @return
	 */
	public static final BufferedImage getCommonImage(ArrayList<BufferedImage> images, Color color){
		if (images.size()<2)
			return null;
		BufferedImage targetImage = images.get(0);
		int w = targetImage.getWidth();
		int h = targetImage.getHeight();

		BufferedImage fullDifferenceImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		int colorRGB = color.getRGB();
		for (int x=0; x<w; x++){
			for (int y=0; y<h; y++){
				int targetRGB = images.get(0).getRGB(x, y);
				fullDifferenceImage.setRGB(x, y, targetRGB);
				if (targetRGB==colorRGB)
					continue;
				for (int i=1; i<images.size(); i++){
					int otherRGB = images.get(i).getRGB(x, y);
					if (targetRGB!=otherRGB || otherRGB==colorRGB){
						fullDifferenceImage.setRGB(x, y, colorRGB);
						break;
					}	
				}				
			}
		}
		return fullDifferenceImage;
	}
	
	/**
	 * Gibt ein Bild zurück, in dem an allen Stellen, an denen sich die übergebenen Bilder unterscheiden, die
	 * übergebene Farbe gesetzt ist und an allen anderen Stellen die Farbe, die dort in den Bildern vorkommt.<br>
	 * Alle Bilder in der übergebenen Liste müssen gleich groß sein.
	 * @param images
	 * @param differenceColor
	 * @return
	 */
	public static final BufferedImage getDifferenceImage(ArrayList<BufferedImage> images, Color differenceColor){
		BufferedImage firstImage = images.get(0);
		int w = firstImage.getWidth();
		int h = firstImage.getHeight();
		int differenceColorRGB = differenceColor.getRGB();
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int x=0; x<w; x++){
			for (int y=0; y<h; y++){
				int firstImageRGB = firstImage.getRGB(x, y);
				boolean differentFound = false;
				for (int i=1; i<images.size(); i++){
					if (images.get(i).getRGB(x, y)!=firstImageRGB){
						differentFound = true;
						break;
					}
				}
				if (differentFound)
					image.setRGB(x, y, differenceColorRGB);
				else
					image.setRGB(x, y, firstImageRGB);
			}
		}
		return image;
	}	
	
	
	
	/**
	 * Übersetzt das übergebene Bild in Pixel. Alle Pixel, die im Bild nicht die übergebene Farbe
	 * haben, sind invers. Alle, die diese Farbe haben, sind nicht invers.
	 * @param image
	 * @param color
	 * @return
	 */
	public static final Pixel[][] getPixels(BufferedImage image, Color color){
		int w = image.getWidth();
		int h = image.getHeight();
		Pixel[][] pixels = new Pixel[w][h];
		int rgb = color.getRGB();
		for (int x=0; x<w; x++){
			for (int y=0; y<h; y++){
				if (image.getRGB(x,y)==rgb)
					pixels[x][y]=new Pixel(x, y, color, false);
				else
					pixels[x][y]=new Pixel(x, y, color, true);
			}
		}
		return pixels;
	}
	
	/**
	 * Liefert ein <code>Image</code> zurück, bei dem alle Punkte, die im übergebenen Bild die
	 * übergebene Farbe <code>fgColor</code> besitzen, auch im Ergebnisbild diese Farbe haben
	 * und alle anderen die Farbe <code>bgColor</code>.
	 * 
	 * @param source
	 * @param fgColor
	 * @param bgColor
	 * @return
	 */
	public static BufferedImage getMonochromImage(BufferedImage source, Color fgColor, Color bgColor){
		int maxX = source.getWidth();
		int maxY = source.getHeight();
		int fgRGB = fgColor.getRGB();
		int bgRGB = bgColor.getRGB();
		BufferedImage image = new BufferedImage(maxX, maxY, BufferedImage.TYPE_INT_RGB);
		for (int x=0; x<maxX; x++){
			for (int y=0; y<maxY; y++){
				if (source.getRGB(x, y)==fgRGB)
					image.setRGB(x, y, fgRGB);
				else 
					image.setRGB(x, y, bgRGB);
			}
		}
		return image;
	}
	
	
	
	/**
	 * Liefert aus dem Bild an Index <code>targetImageIndex</code> in der übergebnenen Bilderliste
	 * einen Bildpunkt, der in keinem der anderen übergebenen Bilder bei diesen Koordinaten denselben
	 * Farbwert besitzt.<br>
	 * Wird kein eindeutiger Punkt gefunden, kommt <code>null</code> zurück.<br>
	 * Alle Bilder müssen gleich groß sein!<br>
	 * @param images
	 * @param targetImageIndex
	 * @return
	 */
	public static final Point getUniquePoint(ArrayList<BufferedImage> images, int targetImageIndex){
		if (images.size()<1)
			return null;
		if (images.size()==1)
			return new Point(0,0);
		
		BufferedImage targetImage = images.get(targetImageIndex);
		int w = targetImage.getWidth();
		int h = targetImage.getHeight();

		for (int x=0; x<w; x++){
			for (int y=0; y<h; y++){
				int targetRGB = targetImage.getRGB(x,y);
				for (int i=0; i<images.size(); i++){
					if (i==targetImageIndex)
						continue;
					BufferedImage image = images.get(i);
					if (image.getRGB(x, y) == targetRGB){
						targetRGB = -1;
						break;
					}
				}
				if (targetRGB != -1)
					return new Point(x,y);
			}
		}
		return null;
	}

	
	/**
	 * Liefert ein Bild, in dem alle Pixel, die nur in dem Bild an der Stelle <code>targteImageIndex</code>
	 * in der übergebenen Bildliste einen für diese Pixelposition in allen anderen Bildern der Liste eindeutigen
	 * Farbwert besitzen weiß sind und alle nicht einmaligen Pxel schwarz.
	 * @param images
	 * @param targetImageIndex
	 * @return
	 */
	public static final BufferedImage getUniquePixelsImage(ArrayList<BufferedImage> images, int targetImageIndex){
		if (images.size()<1)
			return null;
		if (images.size()==1)
			return images.get(0);
		
		BufferedImage targetImage = images.get(targetImageIndex);
		int w = targetImage.getWidth();
		int h = targetImage.getHeight();

		int blackRGB = Color.black.getRGB();
		int whiteRGB = Color.white.getRGB();
		
		BufferedImage fullDifferenceImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		for (int x=0; x<w; x++){
			for (int y=0; y<h; y++){
				int targetRGB = targetImage.getRGB(x,y);
				boolean equalsRGB = false;
				for (int i=0; i<images.size(); i++){
					if (i==targetImageIndex)
						continue;
					BufferedImage image = images.get(i);
					if (image.getRGB(x, y) == targetRGB){
						equalsRGB = true;
						break;
					}
				}
				if (equalsRGB)
					fullDifferenceImage.setRGB(x,y,blackRGB);
				else
					fullDifferenceImage.setRGB(x,y, whiteRGB);
			}
		}
		return fullDifferenceImage;
	}
	
	/**
	 * Liefert das Bild am angegebenen Pfad oder <code>null</code>, wenn es nicht existierte.
	 * @param filePath
	 * @return
	 */
	public static BufferedImage getImage(String filePath){
		try{
			File imageFile = new File(filePath);
			if (imageFile.exists()) 
				return ImageIO.read(imageFile);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Speichert das Bild am angegebenen Pfad.
	 * @param filePath
	 * @return <tt>true</tt>, wenn das speichern geklappt hat, sonst false
	 */
	public static boolean writeImage(BufferedImage image, String filePath){
		try{
			File imageFile = new File(filePath);
	    	ImageIO.write(image , "png", imageFile);
	    	return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	
	
	/**
	 * Liefert alle Bilddateien, deren Pfadname und Dateiname mit <code>fullPathPrefix</code> beginnt,
	 * gefolgt von einem Index und der übergebenen Datei-Extension. Der Index im Namen der Bilder muss
	 * eine Zahl von <code>startIndex</code> bi <code>stopIndex</code>-1 sein.
	 * Es werden alle Bilder geladen, die existieren.
	 * 
	 * @param fullPathPrefix
	 * @param fileExtension
	 * @param startIndex
	 * @param stopIndex
	 * @return
	 */
	public static final ArrayList<BufferedImage> getAllImages(String fullPathPrefix, String fileExtension, int startIndex, int stopIndex){
		if (startIndex > stopIndex){
			int dummy = startIndex;
			startIndex = stopIndex;
			stopIndex = dummy;
		}
		
		if (fileExtension!=null && fileExtension!="" && !fileExtension.startsWith("."))
			fileExtension = "." + fileExtension;
		
		StringBuilder pathBuilder = new StringBuilder(fullPathPrefix);
		ArrayList <BufferedImage> returnList = new ArrayList<BufferedImage>(stopIndex-startIndex);
		for (; startIndex<stopIndex; startIndex++){
			pathBuilder.setLength(fullPathPrefix.length());
			pathBuilder.append(startIndex);
			if (fileExtension!=null)
				pathBuilder.append(fileExtension);
			BufferedImage image = getImage(pathBuilder.toString());
			if (image!=null)
				returnList.add(image);
		}
		return returnList;
	}
	

	/**
	 * Liefert einen Screenshot der übergebenen Ausmaße und speichert diesen, wenn gewünscht, im 
	 * Standardbildpfad.
	 * @param r
	 * @return
	 */
	public static final BufferedImage saveScreenShot(Rectangle r, String fileName){
		System.err.println(fileName);
		BufferedImage image = null;
		try{
			image = ScreenRobot.getScreenShot(r);
			ImageIO.write(image , "png", new File(fileName));
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return image;
	}
	
	/**
	 * Liefert den im übergebenen <code>Rectangle</code> befindlichen Bildausschnitt des Screenshots
	 * mit dem übergebenen Index und speichert diesen Bildausschnitt in eine Datei, wenn ein nicht-leerer
	 * Dateinamensprefix <code>partImageFileName</code> angegeben wurde.
	 * @param sourceFileName
	 * @param targetFileName
	 * @param partRect
	 * @return
	 */
	public static final BufferedImage getPartOfImageFile(String sourceFileName, String targetFileName, Rectangle partRect){
		if (targetFileName == null || targetFileName.trim().equals(""))
			return null;
		BufferedImage image = getImage(sourceFileName);
		if (image == null)
			return null;
		image = image.getSubimage(partRect.x, partRect.y, partRect.width, partRect.height);
		writeImage(image, targetFileName);
		return image;
	}
	
	/**
	 * Liefert die Position des <code>partImage</code> in dem Bild <code>fullImage</code>.<br>
	 * Es kommt <code>null</code> zurück, wenn das Teilbild nicht gefunden wurde.
	 * @param fullImage
	 * @param partImage
	 * @return
	 */
	public static final Point getPosition(BufferedImage fullImage, BufferedImage partImage){
		int partImageMaxX = partImage.getWidth();
		int partImageMaxY = partImage.getHeight();
		int maxX = fullImage.getWidth()- partImageMaxX - 1;
		int maxY = fullImage.getHeight() - partImageMaxY - 1;
		if (maxX<0 || maxY<0)
			return null;
		boolean wrongPixelFound = false;
		for (int x=0; x<maxX; x++){
			for (int y=0; y<maxY; y++){
				for (int px=0; px<partImageMaxX && !wrongPixelFound; px++){
					for (int py=0; py<partImageMaxY && !wrongPixelFound; py++){
						if (partImage.getRGB(px, py) != fullImage.getRGB(x+px, y+py))
							wrongPixelFound = true;
					}
				}
				if (!wrongPixelFound)
					return new Point(x,y);
				wrongPixelFound = false;
			}
		}
		return null;
	}

	/**
	 * Liefert die Position des <code>rgbArray</code>s in dem Bild <code>fullImage</code>.<br>
	 * Es kommt <code>null</code> zurück, wenn das <code>rgbArray</code> nicht gefunden wurde.
	 * @param fullImage
	 * @param rgbArray
	 * @return
	 */
	public static final Point getPosition(BufferedImage fullImage, int [][]rgbArray){
		int partImageMaxX = rgbArray.length;
		int partImageMaxY = rgbArray[0].length;

		int maxX = fullImage.getWidth()- partImageMaxX - 1;
		int maxY = fullImage.getHeight() - partImageMaxY - 1;
		if (maxX<0 || maxY<0)
			return null;
		boolean wrongPixelFound = false;
		for (int x=0; x<maxX; x++){
			for (int y=0; y<maxY; y++){
				for (int px=0; px<partImageMaxX && !wrongPixelFound; px++){
					for (int py=0; py<partImageMaxY && !wrongPixelFound; py++){
						if (rgbArray[px][py] != fullImage.getRGB(x+px, y+py))
							wrongPixelFound = true;
					}
				}
				if (!wrongPixelFound)
					return new Point(x,y);
				wrongPixelFound = false;
			}
		}
		return null;
	}

	/**
	 * Sucht die Position eines monochromen <tt>Pixel</tt>-Arrays in dem ubergebenen Bild.<br>
	 * Die Koordinaten der <tt>Pixel</tt> werden durch die Ausmaße des Arrays bestimmt, welches
	 * auch die <tt>invers</tt>-Eigenschaft des jeweiligen <tt>Pixel</tt>s fest. Die übergebene
	 * Farbe ist die Farbe, die die <tt>Pixel</tt> haben sollen bzw. nicht haben sollen.<br> 
	 * Es kommt <code>null</code> zurück, wenn die <tt>Pixel</tt> nicht gefunden wurden.
	 * @param fullImage
	 * @param rgbArray
	 * @return
	 */
	public static final Point getPosition(BufferedImage fullImage, boolean[][] pixelInversDefinition, Color color){
		int partImageMaxX = pixelInversDefinition.length;
		int partImageMaxY = pixelInversDefinition[0].length;
		int maxX = fullImage.getWidth()- partImageMaxX - 1;
		int maxY = fullImage.getHeight() - partImageMaxY - 1;
		if (maxX<0 || maxY<0)
			return null;
		boolean wrongPixelFound = false;
		int rgb = color.getRGB();
		for (int x=0; x<maxX; x++){
			for (int y=0; y<maxY; y++){
				for (int px=0; px<partImageMaxX && !wrongPixelFound; px++){
					for (int py=0; py<partImageMaxY && !wrongPixelFound; py++){
						if (pixelInversDefinition[px][py]){
							if (rgb == fullImage.getRGB(x+px, y+py))
								wrongPixelFound = true;
						}else{
							if (rgb != fullImage.getRGB(x+px, y+py))
								wrongPixelFound = true;
						}
					}
				}
				if (!wrongPixelFound)
					return new Point(x,y);
				wrongPixelFound = false;
			}
		}
		return null;
	}
	
	
	
	/**
	 * Macht einen Screenshot und liefert den am weitesten links oben stehenden Bildpunkt mit dem 
	 * angegebenen RGB-Wert oder <code>null</code>, wenn kein solcher Bildpunkt existiert.
	 * @param rgb
	 * @return
	 */
	public static final Point getMostLeftUpPixelPosition(int rgb){
		return getMostLeftUpPixelPosition(ScreenRobot.getScreenShot(), rgb);
	}

	/**
	 * Liefert vom übergebenen Bild den am weitesten links oben stehenden Bildpunkt mit dem angegebenen 
	 * RGB-Wert oder <code>null</code>, wenn kein solcher Bildpunkt existiert.
	 * @param image
	 * @param rgb
	 * @return
	 */
	public static final Point getMostLeftUpPixelPosition(BufferedImage image, int rgb){
		int maxX = image.getWidth();
		int maxY = image.getHeight();
		for (int x=0; x<maxX; x++){
			for (int y=0; y<maxY; y++){
				if (image.getRGB(x, y)==rgb)
					return new Point(x, y);
			}
		}
		return null;
	}
	
	/**
	 * Macht einen Screenshot und liefert den am weitesten rechts unten stehenden Bildpunkt mit dem 
	 * angegebenen RGB-Wert oder <code>null</code>, wenn kein solcher Bildpunkt existiert.
	 * @param rgb
	 * @return
	 */
	public static final Point getMostRigthDownPixelPosition(int rgb){
		return getMostRigthDownPixelPosition(ScreenRobot.getScreenShot(), rgb);
	}

	/**
	 * Liefert vom üvbergebenen Bild den am weitesten rechts unten stehenden Bildpunkt mit dem angegebenen
	 * RGB-Wert oder <code>null</code>, wenn kein solcher Bildpunkt existiert.
	 * @param image
	 * @param rgb
	 * @return
	 */
	public static final Point getMostRigthDownPixelPosition(BufferedImage image, int rgb){
		for (int x=image.getWidth()-1; x>0; x--){
			for (int y=image.getHeight()-1; y>0; y--){
				if (image.getRGB(x, y)==rgb)
					return new Point(x, y);
			}
		}
		return null;
	}
	
	
	
	
	
	/**
	 * Gibt das übergebene Pixel-Array als Java-Code aus.
	 * @param pixels
	 * @param fieldname
	 * @return
	 */
	public static final String getPixelsAsJavaCode(Pixel[][] pixels, String fieldName){
		StringBuilder sb = new StringBuilder("\tpublic static final Pixel[][] ");
		sb.append(fieldName);
		sb.append(" = {");
		int w = pixels.length;
		int h = pixels[0].length;
		for (int x=0; x<w; x++){
			sb.append("\r\n\t\t{");
			for (int y=0; y<h; y++){
				Color c = new Color(pixels[x][y].getRGB());
				sb.append("new Pixel(");
				sb.append(x);
				sb.append(", ");
				sb.append(y);
				sb.append(", new Color(");
				int[] rgb = {c.getRed(), c.getGreen(), c.getBlue()};
				for (int i=0; i<rgb.length; i++){
					String s = new String(""+rgb[i]);
					for (int t=0; t<3-s.length(); t++)
						sb.append(' ');
					sb.append(s);
					if (i+1<rgb.length)
						sb.append(", ");
				}
				sb.append("), ");
				sb.append(pixels[x][y].invers);
				sb.append("), ");
			}
			sb.setLength(sb.length()-2);
			sb.append("},");
		}
		sb.setLength(sb.length()-1);
		sb.append("};");
	    return sb.toString();
	}

	
	public static final String getSingleColorPixelArrayAsJavaCode(Pixel[][] pixels, Color color, String fieldName){
		StringBuilder sb = new StringBuilder("\tstatic final Color ");
		sb.append(fieldName);
		sb.append("_COLOR = new Color(");
		sb.append(color.getRed());
		sb.append(", ");
		sb.append(color.getGreen());
		sb.append(", ");
		sb.append(color.getBlue());
		sb.append(");\r\n\tstatic final boolean[][] ");
		sb.append(fieldName);
		sb.append(" = {");

		int w = pixels.length;
		int h = pixels[0].length;
		for (int x=0; x<w; x++){
			sb.append("\r\n\t\t{");
			for (int y=0; y<h; y++){
				if (pixels[x][y].invers)
					sb.append(" true, ");
				else
					sb.append("false, ");
			}
			sb.setLength(sb.length()-2);
			sb.append("},");
		}
		sb.setLength(sb.length()-1);
		sb.append("\r\n\t};");
		return sb.toString();
	}
	
	
	/**
	 * Zu nutzenden Skalierungsvariante, wenn man ein Bild immer und immer wieder skalieren will.
	 * 
	 * http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
	 * 
     * Convenience method that returns a scaled instance of the
     * provided {@code BufferedImage}.
     *
     * @param img the original image to be scaled
     * @param targetWidth the desired width of the scaled instance,
     *    in pixels
     * @param targetHeight the desired height of the scaled instance,
     *    in pixels
     * @param hint one of the rendering hints that corresponds to
     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
     * @param higherQuality if true, this method will use a multi-step
     *    scaling technique that provides higher quality than the usual
     *    one-step technique (only useful in downscaling cases, where
     *    {@code targetWidth} or {@code targetHeight} is
     *    smaller than the original dimensions, and generally only when
     *    the {@code BILINEAR} hint is specified)
     * @return a scaled version of the original {@code BufferedImage}
     */
	public static final BufferedImage getScaledInstance(BufferedImage img, int targetWidth, int targetHeight, Object hint, boolean higherQuality) {
		int type = (img.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}
			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}
	
}
