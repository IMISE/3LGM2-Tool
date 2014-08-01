package de.imise.tool3lgm.graphtools.view.container;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.elements.Doppelkante;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.Knickpunkt;
import de.imise.tool3lgm.graphtools.elements.Knoten;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.PartOfBeziehung;
import de.imise.tool3lgm.graphtools.elements.edge.KommBeziehung;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.graphtools.view.graph.KnotenRenderer;
import de.imise.tool3lgm.log.Log;

/**
 * @author N.N
 * @create Long time ago
 */
public class EdgeContainer extends ElementContainer {
	
	/**
	 * COMMENTME
	 */
	final static float dash1[] = { 10.0f };
	
	/**
	 * COMMENTME
	 */
	final static BasicStroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

	/**
	 * COMMENTME
	 */
	protected int startx = 0, starty = 0, endx = 0, endy = 0;
	
	/**
	 * COMMENTME
	 */
	public Polygon p1 = new Polygon(), p2 = new Polygon();
	
	/**
	 * COMMENTME
	 */
	protected boolean over_lapping = false;
	
	/**
	 * COMMENTME
	 */
	protected double rad1 = 0, rad2 = 0;

	/**
	 * COMMENTME
	 */
	protected ArrayList<BendpointContainer> knickpunkte = new ArrayList<BendpointContainer>(1);

	/**
	 * 
	 */
	public EdgeContainer() {
		super();
	}

	/**
	 * @param neu
	 * @param gd
	 */
	public EdgeContainer(Kante neu, GraphDocument gd) {
		super(neu, gd);
		computeBorderPoints();
	}

	/**
	 * @param neu
	 * @param l
	 * @param gd
	 */
	public EdgeContainer(Kante neu, GraphElementLayout l, GraphDocument gd) {
		super(neu, l, gd);
		computeBorderPoints();
	}

	/**
	 * @param alt
	 * @param gd
	 */
	public EdgeContainer(EdgeContainer alt, GraphDocument gd) {
		super(alt, gd);
		computeBorderPoints();
	}

	/* (non-Javadoc)
	 * @see tool3lgm.graphtools.view.container.ElementContainer#clone(boolean, tool3lgm.graphtools.GraphDocument)
	 */
	@Override
	public ElementContainer clone(boolean cloneModelElement, GraphDocument _doc) {
		EdgeContainer retVal;
		try {
			retVal = (EdgeContainer) super.clone(cloneModelElement, _doc);
		} catch (Exception e) {
			Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
			return null;
		}
		retVal.startx = startx;
		retVal.starty = starty;
		retVal.endx = endx;
		retVal.endy = endy;
		retVal.over_lapping = over_lapping;
		retVal.p1 = new Polygon(p1.xpoints, p1.ypoints, 3);
		retVal.p2 = new Polygon(p2.xpoints, p2.ypoints, 3);
		retVal.getBendpointContainerList().clear();
		if (_doc instanceof Szenario) {
			for (int i = 0; i < knickpunkte.size(); i++) {
				BendpointContainer knC = knickpunkte.get(i);
				BendpointContainer kp = (BendpointContainer) knC.clone(true, _doc);
				kp.getElement().addEdge((Kante) me);
				retVal.setKnickpunkt(kp, i);
			}
		}
		return retVal;
	}

	/**
	 * @param _k1
	 * @param _k2
	 * @param gd
	 */
	public void setKnots(Knoten _k1, Knoten _k2, GraphDocument gd) {
		getEdge().setKnots(_k1, _k2);
	}

	/**
	 * @return
	 */
	public Doppelkante getEdge() {
		return (me instanceof Doppelkante) ? ((Doppelkante) me) : (null);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getX()
	 */
	@Override
	public int getX() {
		return Math.min(startx, endx) - 10;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getY()
	 */
	@Override
	public int getY() {
		return Math.min(starty, endy) - 10;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getWidth()
	 */
	@Override
	public int getWidth() {
		return Math.abs(endx - startx) + 20;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getHeight()
	 */
	@Override
	public int getHeight() {
		return Math.abs(endy - starty) + 20;
	}

	// Komplett aus Kante
	/**
	 * @return
	 */
	public final int getStartX() {
		return startx;
	}

	/**
	 * @return
	 */
	public final int getStartY() {
		return starty;
	}

	/**
	 * @return
	 */
	public final int getEndX() {
		return endx;
	}

	/**
	 * @return
	 */
	public final int getEndY() {
		return endy;
	}

	// Komplett aus Kante
	/**
	 * 
	 */
	public void computeBorderPoints() {
// String h = getHashString();
// if (h.equals("KAN_1061988438094_1409") || h.equals("KAN_1061988438094_1480") || h.equals("KAN_1061988438094_1426"))
// System.err.println("jetzte");
		/*
		 * if (ModelConstants.layerFor(getEdge().getClass()) == 4) System.err.println("EdgeContainer -> computeBorderPoints(): " + getGraphDocument()); for (ElementContainer c :
		 * getEdge().getContainerTable().values()) { EdgeContainer ec = (EdgeContainer) c; System.err.println(ec.getGraphDocument() + " " + ec.getHashString() + " " + ec.knickpunkte + " " +
		 * ec.getEdge() + " " + ModelConstants.getFullBackwardMetaAssociationName(ec.getEdge().getClass()) + " " + ec.hashCode()); }
		 */

		ElementContainer kc1 = null;
		ElementContainer kc2 = null;
		try {
			Kante k;
			if ((k = getEdge()) != null) {
				if (k.getStart() != null)
					kc1 = k.getStart().getContainer(doc);
				if (k.getEnd() != null)
					kc2 = k.getEnd().getContainer(doc);
			}
		} catch (NullPointerException e) {
			Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), e);
		}
		if ((kc1 == null) || (kc2 == null))
			return;

		// TODO:Aus irgend einem Grund sind beim Import von Teilmodellen hier
		// null-Elemente in der Liste knickpunkte
		// daher hat AXS hier mal das Löschen eingefügt. Eigentlich sollte das
		// aber nicht nötig sein, weil das nur sie Symptome abstellt
		for (int i = knickpunkte.size() - 1; i >= 0; i--)
			if (knickpunkte.get(i) == null)
				knickpunkte.remove(i);

		if (knickpunkte.size() > 0) {
			int i = knickpunkte.size() - 1;
			int left_x = kc1.getX();
			int left_y = kc1.getY();
			int right_x = knickpunkte.get(0).getX();
			int right_y = knickpunkte.get(0).getY();

			int middle_x = right_x, middle_y = right_y;

			while (((Math.abs(left_x - right_x) > 1) || (Math.abs(left_y - right_y) > 1))) {
				middle_x = (left_x + right_x) / 2;
				middle_y = (left_y + right_y) / 2;
				if (KnotenRenderer.isInside(kc1, middle_x, middle_y)) {
					left_x = middle_x;
					left_y = middle_y;
				} else {
					right_x = middle_x;
					right_y = middle_y;
				}
			}
			startx = middle_x;
			starty = middle_y;

			left_x = kc2.getX();
			left_y = kc2.getY();
			right_x = knickpunkte.get(i).getX();
			right_y = knickpunkte.get(i).getY();

			middle_x = left_x;
			middle_y = left_y;

			while (((Math.abs(left_x - right_x) > 1) || (Math.abs(left_y - right_y) > 1))) {
				middle_x = (left_x + right_x) / 2;
				middle_y = (left_y + right_y) / 2;
				if (KnotenRenderer.isInside(kc2, middle_x, middle_y)) {
					left_x = middle_x;
					left_y = middle_y;
				} else {
					right_x = middle_x;
					right_y = middle_y;
				}
			}
			endx = middle_x;
			endy = middle_y;
		} else {
			int left_x = kc1.getX();
			int left_y = kc1.getY();
			int right_x = kc2.getX();
			int right_y = kc2.getY();

			int middle_x = left_x, middle_y = left_y;

			while (((Math.abs(left_x - right_x) > 1) || (Math.abs(left_y - right_y) > 1))) {
				middle_x = (left_x + right_x) / 2;
				middle_y = (left_y + right_y) / 2;
				if (KnotenRenderer.isInside(kc1, middle_x, middle_y)) {
					left_x = middle_x;
					left_y = middle_y;
				} else {
					right_x = middle_x;
					right_y = middle_y;
				}
			}
			startx = middle_x;
			starty = middle_y;

			left_x = kc2.getX();
			left_y = kc2.getY();
			right_x = kc1.getX();
			right_y = kc1.getY();

			while (((Math.abs(left_x - right_x) > 1) || (Math.abs(left_y - right_y) > 1))) {
				middle_x = (left_x + right_x) / 2;
				middle_y = (left_y + right_y) / 2;
				if (KnotenRenderer.isInside(kc2, middle_x, middle_y)) {
					left_x = middle_x;
					left_y = middle_y;
				} else {
					right_x = middle_x;
					right_y = middle_y;
				}
			}
			endx = middle_x;
			endy = middle_y;
		}

		int lstartx = 0;
		int lstarty = 0;
		int lendx = 0;
		int lendy = 0;
		if (knickpunkte.size() > 0) {
			int i = knickpunkte.size() - 1;
			lstartx = knickpunkte.get(i).getX();
			lstarty = knickpunkte.get(i).getY();
			rad2 = (Math.atan2((endy - lstarty), (endx - lstartx)) + (Math.PI / 2));
			lendx = knickpunkte.get(0).getX();
			lendy = knickpunkte.get(0).getY();
			rad1 = (Math.atan2((lendy - starty), (lendx - startx)) + (Math.PI / 2));
		} else {
			rad2 = (Math.atan2((endy - starty), (endx - startx)) + (Math.PI / 2));
			rad1 = rad2;
		}

		if ((KnotenRenderer.isInside(kc1, endx, endy)) || (KnotenRenderer.isInside(kc2, startx, starty)))
			over_lapping = true;
		else
			over_lapping = false;

		p1.reset();
		p2.reset();
		p1.addPoint(endx - 4, endy + 12);
		p1.addPoint(endx, endy);
		p1.addPoint(endx + 4, endy + 12);
		p2.addPoint(startx + 4, starty - 12);
		p2.addPoint(startx, starty);
		p2.addPoint(startx - 4, starty - 12);
	}

	/**
	 * @return
	 */
	public final boolean isOverLapping() {
		return over_lapping;
	}

	/**
	 * COMMENTME
	 */
	int x_shift = 0, y_shift = 0;

	/**
	 * @param x
	 * @param y
	 */
	public void setShift(int x, int y) {
		x_shift = x;
		y_shift = y;
	}

	/**
	 * @param kc1
	 * @param kc2
	 */
	private void createSurrogateContainers(ElementContainer kc1, ElementContainer kc2) {
		if (paintingSurrogates)
			return;
		if ((kc1 == null) || (kc2 == null) || (containerParent == null))
			return;

		for (ElementContainer startC : kc1.getSurrogateContainer()) {
			for (ElementContainer endC : kc2.getSurrogateContainer()) {
				if (startC == endC)
					continue;
				Kante tmpKante = (Kante) me.clone();
				tmpKante.setKnots(startC.getElement(), endC.getElement(), false);
				EdgeContainer tmpC = new EdgeContainer(tmpKante, doc);
				tmpC.setColor(Color.gray);
				((LayerContainer) containerParent).addTmpEdgeContainer(tmpC);
			}
		}
	}

	/**
	 * Toleranz in Pixeln, mit der man neben die Kante klicken darf, wenn man sie per Mausklick auswählen will oder mit der entschieden wird, an welcher Stelle ein neuer Knickpunkt hinzugefügt werden
	 * muss.
	 */
	public static final int TOLERANCE = 4;

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isInside(int x, int y) {
		if (!isVisible())
			return false;
		return getKnickpunktInsertIndex(x, y) >= 0;
	}

	/**
	 * Liefert den Index, an dem in die Liste <code>knickpunkte</code> ein neuer Knickpunkt eingefügt werden würde, wenn der die übergebenen Koordinaten besitzt.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public int getKnickpunktInsertIndex(int x, int y) {
		return getKnickpunktInsertIndex(x, y, TOLERANCE);
	}

	/**
	 * Liefert den Index, an dem in die Liste <code>knickpunkte</code> ein neuer Knickpunkt eingefügt werden würde, wenn der die übergebenen Koordinaten besitzt.
	 * 
	 * @param x
	 * @param y
	 * @param tolerance
	 * @return
	 */
	private int getKnickpunktInsertIndex(int x, int y, int tolerance) {
		int index = -1;
		int startx = getStartX();
		int starty = getStartY();
		int numKKnots = knickpunkte.size();
		for (int i = 0; i <= numKKnots; i++) {
			int endx = 0, endy = 0;
			if (i == numKKnots) {
				endx = getEndX();
				endy = getEndY();
			} else {
				endx = knickpunkte.get(i).getX();
				endy = knickpunkte.get(i).getY();
			}
			if (((int) Line2D.ptSegDist(startx, starty, endx, endy, x, y)) <= tolerance) {
				index = i;
				break;
			}
			startx = endx;
			starty = endy;
		}
		return index;
	}

	/**
	 * Setzt einen Knickpunkt an der richtigen Position in die Liste der Knickpunkte ein. Wird als Index -1 übergeben, dann wird der Index anhand der Koordinaten berechnet.
	 * 
	 * @param kp
	 * @param index
	 */
	public void setKnickpunkt(BendpointContainer kp, int index) {
		if (index == -1)
			index = 0;
		while (index >= knickpunkte.size()) {
			knickpunkte.add(null);
		}
// System.err.println("AXS_AXSsetKnickpunkt " + getGraphDocument());
		knickpunkte.set(index, kp);
		kp.getKnickpunktKnoten().setOwner(this);
	}

	/**
	 * Fügt einen Knickpunkt an der richtigen Position in die Liste der Knickpunkte ein. Wird als Index -1 übergeben, dann wird der Index anhand der Koordinaten berechnet.
	 * 
	 * @param kp
	 * @param index
	 */
	public void addKnickpunkt(BendpointContainer kp, int index) {
		if (index < 0)
			index = getKnickpunktInsertIndex(kp.layout.x, kp.layout.y);
		if (index < 0)
			index = 0;
		knickpunkte.add(index, kp);
		kp.getKnickpunktKnoten().setOwner(this);
	}

	/**
	 * @param kp
	 */
	public void removeKnickpunkt(Knickpunkt kp) {
		knickpunkte.remove(kp.getContainer(doc));
	}

	/**
	 * @return
	 */
	public ArrayList<BendpointContainer> getBendpointContainerList() {
		return knickpunkte;
	}

	/**
	 * @param kn
	 * @return
	 */
	public int getIndexOfKnickpunkt(Knickpunkt kn) {
		for (int i = 0; i < knickpunkte.size(); i++) {
			if (knickpunkte.get(i).getKnickpunktKnoten() == kn)
				return i;
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Component#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return isVisible(false);
	}

	/**
	 * @param createSurrogates
	 * @return
	 */
	public boolean isVisible(boolean createSurrogates) {
		ModelElement me1 = getEdge().getStart();
		ModelElement me2 = getEdge().getEnd();
		if ((me1 == null) || (me2 == null))
			return false;

		ElementContainer kc1 = me1.getContainer(doc);
		ElementContainer kc2 = me2.getContainer(doc);
		if ((kc1 == null) || (kc2 == null) || (kc1.isUnpaintable()) || (kc2.isUnpaintable()) || (kc1.getElement().layerFor() != kc2.getElement().layerFor())) {
			return false;
		}
		if ((!kc1.isVisible()) || (!kc2.isVisible())) {
			if ((!(me instanceof KommBeziehung)) && createSurrogates)
				createSurrogateContainers(kc1, kc2);
			return false;
		}

		if (isOverLapping())
			return false;

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		if (!isVisible(true)) {
			return;
		}

		Graphics2D gc = (Graphics2D) g;

		Color elem_col = getColor();
		if (elem_col == null)
			elem_col = Color.black;
		g.setColor(elem_col);

		Stroke s = gc.getStroke();

		boolean isResult = doc.isAnalysisResult(this);
		boolean isHighLight = isHighLight();

		int startx = 0, starty = 0, endx = 0, endy = 0;
		startx = getStartX();
		starty = getStartY();

		int numKKnots = knickpunkte.size();
		for (int i = 0; i <= numKKnots; i++) {
			if (i == numKKnots) {
				endx = getEndX();
				endy = getEndY();
			} else {
				endx = knickpunkte.get(i).getX();
				endy = knickpunkte.get(i).getY();
			}

			boolean fatFrame = false;
			if (isResult || isHighLight) {
				fatFrame = true;
				gc.setStroke(fatStroke);
			}

			if ((!paintingSurrogates) && (isDashed())) {
				Stroke str = gc.getStroke();
				gc.setStroke(dashedStroke);
				g.drawLine(startx, starty, endx, endy);
				gc.setStroke(str);
			} else
				g.drawLine(startx, starty, endx, endy);

			if (i == 0) {
				Doppelkante dlk = getEdge();
				if ((dlk.getDirection() == Doppelkante.BACKWARD) || (dlk.getDirection() == Doppelkante.DOUBLE)) {
					gc.rotate(rad1, startx, starty);
// try {
					g.drawPolygon(p2);
					g.fillPolygon(p2);
					/*
					 * System.err.println("1.) " + i + " von " + numKKnots); System.err.println(p2 + " " + CollectionUtils.toString(p2.xpoints) + " " + CollectionUtils.toString(p2.ypoints));
					 * System.err.println(ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); } catch (Exception e) {
					 * System.err.println("1.) " + i + " von " + numKKnots + " ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"); System.err.println(p2 + " " +
					 * CollectionUtils.toString(p2.xpoints) + " " + CollectionUtils.toString(p2.ypoints)); System.err.println(getHashString() + " " +
					 * ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); }
					 */gc.rotate(-rad1, startx, starty);
				}
			}
			if (i == numKKnots) {
				Doppelkante dlk = getEdge();
				if ((dlk.getDirection() == Doppelkante.FORWARD) || (dlk.getDirection() == Doppelkante.DOUBLE)) {
					gc.rotate(rad2, endx, endy);
// try {
					g.drawPolygon(p1);
					g.fillPolygon(p1);
					/*
					 * System.err.println("2.) " + i + " von " + numKKnots); System.err.println(p1 + " " + CollectionUtils.toString(p1.xpoints) + " " + CollectionUtils.toString(p1.ypoints));
					 * System.err.println(ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); } catch (Exception e) {
					 * System.err.println("2.) " + i + " von " + numKKnots + " #######################################################################################"); System.err.println(p1 + " " +
					 * CollectionUtils.toString(p1.xpoints) + " " + CollectionUtils.toString(p1.ypoints)); System.err.println(getHashString() + " " +
					 * ModelConstants.getFullBackwardMetaAssociationName(dlk.getClass()) + ": start=" + dlk.getStart() + " -> end=" + dlk.getEnd()); }
					 */gc.rotate(-rad2, endx, endy);
				}
			}

			if (fatFrame)
				gc.setStroke(s);

			if (isSelected()) {
				g.setColor(Color.black);

				g.drawRect(startx - 5, starty - 5, 10, 10);
				g.fillRect(startx - 5, starty - 5, 10, 10);

				g.drawRect(endx - 5, endy - 5, 10, 10);
				g.fillRect(endx - 5, endy - 5, 10, 10);
			}

			// northLabel wird bei Kanten ins Zentrum verschoben
			if (northLabel != null) {
				// System.out.println(northLabel.getText() + "\n startx=" +
				// startx + " endx=" + endx + " starty=" + starty + "
				// endy="+endy + "
				// northLabel.height="+northLabel.getPreferredSize().height + "
				// northLabel.width="+northLabel.getPreferredSize().width);
				int dx = (startx + endx) / 2 - northLabel.getPreferredSize().width;
				int dy = (starty + endy) / 2 - northLabel.getPreferredSize().height;
				g.translate(dx, dy);
				northLabel.paint(g);
				g.translate(-dx, -dy);
			}
			if (southLabel != null) {
				int dx = (startx + endx) / 2;// +
				// southLabel.getPreferredSize().width;
				int dy = (starty + endy) / 2 - southLabel.getPreferredSize().height;
				g.translate(dx, dy);
				southLabel.paint(g);
				g.translate(-dx, -dy);
			}
			/*
			 * Die 2 hier sind nicht getestet und sehr wahrscheinlich völlig Quatsch (die Koordinaten) if (eastLabel != null) { int dx = startx; g.translate(dx, ym); eastLabel.paint(g);
			 * g.translate(-dx, -ym); } if (westLabel != null) { int dx = xm - westLabel.getPreferredSize().width - 1; g.translate(dx, ym); westLabel.paint(g); g.translate(-dx, -ym); }
			 */

			startx = endx;
			starty = endy;
		}
	}

	/**
	 * @return
	 */
	public boolean isDashed() {
		return me instanceof PartOfBeziehung;
	}

	/*
	 * (non-Javadoc)
	 * @see tool3lgm.graphtools.view.container.ElementContainer#refreshText()
	 */
	@Override
	public final void refreshText() {
		// hier passiert nichts
	}

	/**
	 * @author Thomas Rudert
	 * @param preString
	 *            der Tag wird mit diesen String eingerueckt
	 * @return String der vollstaendige XML-Tag zu diesem Objekt
	 * @see de.imise.tool3lgm.graphtools.view.container.ElementContainer#toXMLString()
	 */
	@Override
	public String toXMLString() {
		return "<container hash=\"" + me.getHashString() + "\" />";
	}

}
