package de.imise.util.image;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class UniquePixelExtractor {

	public UniquePixelExtractor() {
/*		
		ArrayList <BufferedImage> al = ImageTools.getAllImages(IMConstants.IMAGE_DIRECTORY+"Digit", "png", 0, 10);
		System.out.println(getUniquePixelSet(al, 0, new Color(255, 255, 255)));
		System.out.println(getUniquePixelSet(al, 1, new Color(255, 255, 255)));
		System.out.println(getUniquePixelSet(al, 2, new Color(255, 255, 255)));
		System.out.println(getUniquePixelSet(al, 3, new Color(255, 255, 255)));
		System.out.println(getUniquePixelSet(al, 4, new Color(255, 255, 255)));
		System.out.println(getUniquePixelSet(al, 5, new Color(255, 255, 255)));
		System.out.println(getUniquePixelSet(al, 6, new Color(255, 255, 255)));
		System.out.println(getUniquePixelSet(al, 7, new Color(255, 255, 255)));
		System.out.println(getUniquePixelSet(al, 8, new Color(255, 255, 255)));
		System.out.println(getUniquePixelSet(al, 9, new Color(255, 255, 255)));
*/		
	}

	/**
	 * @param targetImage
	 * @param images
	 * @param pixelColor
	 * @return
	 */
	public static final ArrayList<Pixel> getUniquePixelSet(BufferedImage targetImage, ArrayList<BufferedImage> images, Color pixelColor){
		return getUniquePixelSet(targetImage, images, pixelColor, null);
	}

	/**
	 * @param images
	 * @param targetImageIndex
	 * @param pixelColor
	 * @param ignoreColor
	 * @return
	 */
	public static final ArrayList<Pixel> getUniquePixelSet(BufferedImage targetImage, ArrayList<BufferedImage> images, Color pixelColor, Color ignoreColor){
		//Rückgabeliste anlegen
		ArrayList<Pixel> uniquePixels = new ArrayList<Pixel>(images.size());
		
		//für das Bild an Position targetImageIndex in der Liste images ein Feld mit Differentpixeln anlegen, die
		//entweder genau die übergebene Pixelfarbe haben, sie nicht haben oder, wenn der Pixel im aktuellen Bild
		//die Farbe ignoreColor besitzt, null ist
		DifferencePixel[][] d = DifferencePixel.getDifferencePixels(targetImage, images, pixelColor, ignoreColor);
		int w = d.length;
		int h = d[0].length;
		
		//Variable, die einen Pixel enthalten wird, der sich von den meisten Bildern unterscheidet, von
		//denen sich alle bisher gefundenen Pixel nicht unterschieden haben
		DifferencePixel bestPixel;
		//Liste aller Bilder von denen sich der bestPixel unterscheidet, von denen sich die bisher gefundenen Pixel
		//nicht unterschieden haben
		ArrayList <BufferedImage> bestPixelNewImages = null;
		//Liste aller Bilder, von denen sich die gesamte bisher gefundene Pixelmenge unterscheidet
		ArrayList<BufferedImage> allDifferentImages = new ArrayList<BufferedImage>(images.size());
		//boolean, der sich merkt, ob schon ein nicht-inverser Pixel im Rückgabeset steckt (das muss sein)
		boolean onlyInversPixelInSet = true;
		//solange nicht alle Pixel zur Gesamtmenge der gefundenen Pixel hinzugefügt wurden
		while(uniquePixels.size()<images.size()-1){
			//setze den "besten" neuen Pixel null
			bestPixel = null;
			//für jede Koordinate des Bildes 
			for (int x=0; x<w; x++){
				for (int y=0; y<h; y++){
					//wenn der entsprechende Differenzpixel schon in der Gesamtliste ist -> weiter
					if (d[x][y]==null || uniquePixels.contains(d[x][y]))
						continue;
					//
					ArrayList <BufferedImage> newDifferentImages = d[x][y].getNewDifferenceImages(allDifferentImages);
					//wenn die Lösung grade gefunden wurde
					if (allDifferentImages.size()+newDifferentImages.size()+1==images.size()){
						uniquePixels.add(d[x][y]);
						//wenn bisher kein nichtinverser Pixel mit der pixelColor im Rückgabeset ist, dann
						//wird der erstbeste hinzugefügt
						if (d[x][y].isInvers() && onlyInversPixelInSet){
							Point p = ImageTools.getMostLeftUpPixelPosition(targetImage, pixelColor.getRGB());
							uniquePixels.add(new Pixel(p, pixelColor));
						}
						return uniquePixels;
					}
					if (bestPixel==null){
						bestPixel = d[x][y];
						bestPixelNewImages = newDifferentImages;
					}else{
						if(newDifferentImages.size()>bestPixelNewImages.size()){
							bestPixel = d[x][y];
							bestPixelNewImages = bestPixel.getNewDifferenceImages(allDifferentImages);
						}
					}
				}
			}
			//keine eindeutige Lösung 
			if (bestPixel==null || bestPixelNewImages.size()==0)
				return null;
			uniquePixels.add(bestPixel);
			if (!bestPixel.isInvers())
				onlyInversPixelInSet=false;
			allDifferentImages.addAll(bestPixelNewImages);
		}
		//wenn bisher kein nichtinverser Pixel mit der pixelColor im Rückgabeset ist, dann
		//wird der erstbeste hinzugefügt
		if (onlyInversPixelInSet){
			Point p = ImageTools.getMostLeftUpPixelPosition(targetImage, pixelColor.getRGB());
			uniquePixels.add(new Pixel(p, pixelColor));
		}
		return uniquePixels;
	}
	
/*	
	private static final DifferencePixel[][] _getDifferencePixels(ArrayList<BufferedImage> images, int targetImageIndex, Color pixelColor, Color ignoreColor){
		BufferedImage targetImage = images.get(targetImageIndex);
		int w = targetImage.getWidth();
		int h = targetImage.getHeight();
		
		DifferencePixel[][] returnArray = new DifferencePixel[w][h];
		
		int colorRGB = pixelColor.getRGB();
		for (int x=0; x<w; x++){
			for (int y=0; y<h; y++){
				DifferencePixel differencePixel;
				if (ignoreColor!=null && targetImage.getRGB(x, y)==ignoreColor.getRGB())
					continue;
				if (targetImage.getRGB(x, y)==colorRGB)
					differencePixel = new DifferencePixel(x, y, pixelColor, false);
				else
					differencePixel = new DifferencePixel(x, y, pixelColor, true);
				for (int i=0; i<images.size(); i++){
					if (i==targetImageIndex)
						continue;
					differencePixel.add(images.get(i));
				}
				returnArray[x][y]=differencePixel;
			}
		}
		return returnArray;
	}
*/	
	
	/**
	 * Pixel, der für übergebene Bilder entscheiden kann, ob dieses Bild den Pixel enthält.<br>
	 * Ein Pixel besteht aus einem Punkt und einer Farbe.<br>
	 * Ist <code>invers</code> nicht <code>true</code>, dann wird ein übergebenes Bild bei
	 * <code>add(BufferedImage)</code> zur Liste der Differenzbilder hinzugefügt, wenn es an diesem
	 * Punkt den Pixel nicht enthält.
	 */
	private static class DifferencePixel extends Pixel{

		/**
		 * Liste, in die alle Bilder eingefügt werden, deren Farbwert in dem Bildpunkt, den dieser Pixel 
		 * beschreibt bei <code>invers==true</code> nicht mit dem Farbwert dieses Pixel übereinstimmt
		 * oder bei <code>invers==false</code> mit dem Farbwert dieses Pixel übereinstimmt.
		 */
		private ArrayList <BufferedImage> differenceImages;
		
		/**
		 * Liste, in die alle Bilder eingefügt werden, deren Farbwert in dem Bildpunkt, den dieser Pixel 
		 * beschreibt bei <code>invers==true</code> mit dem Farbwert dieses Pixel übereinstimmt
		 * oder bei <code>invers==false</code> nicht mit dem Farbwert dieses Pixel übereinstimmt.
		 */
		private ArrayList <BufferedImage> equalsImages;
		
		/**
		 * @param x
		 * @param y
		 * @param color
		 * @param targetImage
		 * @param images
		 */
		private DifferencePixel(int x, int y, Color color, BufferedImage targetImage, ArrayList<BufferedImage> images) {
			super(x, y, color);
			differenceImages = new ArrayList<BufferedImage>(5);
			equalsImages = new ArrayList<BufferedImage>(5);
			if (targetImage.getRGB(x, y)!=color.getRGB())
				invers=true;
			for (int i=0; i<images.size(); i++){
				BufferedImage image = images.get(i);
				if (image==targetImage)
					continue;
				differenceImages.remove(image);
				equalsImages.remove(image);
				if (isPixelInImage(image))
					equalsImages.add(image);
				else
					differenceImages.add(image);
			}
			
		}

		/**
		 * @param alreadyKnownImages
		 * @return
		 */
		public ArrayList <BufferedImage>getNewDifferenceImages(ArrayList<BufferedImage> alreadyKnownImages){
			if (differenceImages==null)
				return null;
			ArrayList <BufferedImage>newImages = new ArrayList<BufferedImage>(differenceImages.size());
			for (int i=0; i<differenceImages.size(); i++){
				BufferedImage image = differenceImages.get(i);
				if (!alreadyKnownImages.contains(image))
					newImages.add(image);
			}
			return newImages;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(super.toString());
			sb.setLength(sb.length()-1);
			if (differenceImages==null){
				sb.append(", UNDEFINED]");
				return sb.toString();
			}
			sb.append(", diffCount=");
			sb.append(differenceImages.size());
			sb.append(", equalCount=");
			sb.append(equalsImages.size());
			sb.append("]");
			return sb.toString();
		}
		

		/**
		 * Liefer ein DifferencePixel-Array für das Zielbild.<br>
		 * Alle Pixel im Zielbild mit der ignoreColor sind im Array <code>null</code>. Alle anderen sind normale
		 * Differenzpixel.
		 * @param targetImage
		 * @param images
		 * @param pixelColor
		 * @param ignoreColor
		 * @return
		 */
		public static final DifferencePixel[][] getDifferencePixels(BufferedImage targetImage, ArrayList<BufferedImage> images, Color pixelColor, Color ignoreColor){
			int w = targetImage.getWidth();
			int h = targetImage.getHeight();
			DifferencePixel[][] returnArray = new DifferencePixel[w][h];
			for (int x=0; x<w; x++){
				for (int y=0; y<h; y++){
					DifferencePixel differencePixel;
					if (ignoreColor!=null){
						if (targetImage.getRGB(x, y)==ignoreColor.getRGB())
							continue;
						boolean ignore = false;
						for (int i=0; i<images.size(); i++){
							if (images.get(i).getRGB(x, y)==ignoreColor.getRGB()){
								ignore = true;
								break;
							}
						}
						if (ignore)
							continue;
					}
					differencePixel = new DifferencePixel(x, y, pixelColor, targetImage, images);
					returnArray[x][y]=differencePixel;
				}
			}
			return returnArray;
			
		}

		
		
	}
	
}
