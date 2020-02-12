package de.imise.util.swing;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Collections;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * @author AXS (12.02.2020)
 */
/**
 * A subclass of Graphics2D that returns the correct FontMetrics but does not
 * actually paint anything.
 *
 * @see <a
 *      href="http://stackoverflow.com/questions/16227877/how-to-update-a-jcomponent-with-html-without-flickering">How
 *      to update a JComponent with HTML without flickering?</a>
 */
public class NoopGraphics extends Graphics2D {
    private Font font;
    private Color color = Color.BLACK;
    private final Rectangle clip;
    private Stroke stroke;
    private Paint paint;
    private Color background;
    private AffineTransform transform = new AffineTransform();
    private final RenderingHints renderingHints = new RenderingHints(Collections.<Key, Object> emptyMap());
    private Composite composite = AlphaComposite.SrcOver;
    private final boolean isAntiAliased;
    private final boolean usesFractionalMetrics;
    private final GraphicsConfiguration graphicsConfiguration;

    public static GraphicsConfiguration getDefaultScreenGraphicsConfiguration() {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
        GraphicsConfiguration graphicsConfiguration = graphicsDevice.getDefaultConfiguration();
        return graphicsConfiguration;
    }
    public NoopGraphics(final int x, final int y, final int width, final int height) {
        this(x, y, width, height, getDefaultScreenGraphicsConfiguration(), false, false);
    }
    public NoopGraphics(final int x, final int y, final int width, final int height, final GraphicsConfiguration graphicsConfiguration, final boolean isAntiAliased, final boolean usesFractionalMetrics) {
        this(new Rectangle(x, y, width, height), graphicsConfiguration, isAntiAliased, usesFractionalMetrics);
    }
    public NoopGraphics(final Rectangle clip, final GraphicsConfiguration graphicsConfiguration, final boolean isAntiAliased, final boolean usesFractionalMetrics) {
        this.graphicsConfiguration = graphicsConfiguration;
        this.isAntiAliased = isAntiAliased;
        this.usesFractionalMetrics = usesFractionalMetrics;
        this.clip = clip;
    }
    public static NoopGraphics createNoopGraphics() {
        NoopGraphics noopGraphics = new NoopGraphics(0, 0, 1, 1);
        noopGraphics.setFont(new JLabel().getFont());
        return noopGraphics;
    }
    @Override
    public void setXORMode(final Color c1) {
    }
    @Override
    public void setPaintMode() {
    }
    @Override
    public Font getFont() {
        return font;
    }
    @Override
    public void setFont(final Font font) {
        this.font = font;
    }
    @Override
    public Color getColor() {
        return color;
    }
    @Override
    public void setColor(final Color c) {
        color = c;
    }
    @Override
    public void setClip(final int x, final int y, final int width, final int height) {
    }
    @Override
    public void setClip(final Shape clip) {
        this.clip.setRect(clip.getBounds());
    }
    @Override
    public FontMetrics getFontMetrics(final Font f) {
        // http://stackoverflow.com/questions/2753514/java-friendlier-way-to-get-an-instance-of-fontmetrics
        return new Canvas(graphicsConfiguration).getFontMetrics(f);
    }
    @Override
    public Rectangle getClipBounds() {
        return clip.getBounds();
    }
    @Override
    public Shape getClip() {
        return clip;
    }
    @Override
    public void fillRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
    }
    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
    }
    @Override
    public void fillPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
    }
    @Override
    public void fillOval(final int x, final int y, final int width, final int height) {
    }
    @Override
    public void fillArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
    }
    @Override
    public void drawRoundRect(final int x, final int y, final int width, final int height, final int arcWidth, final int arcHeight) {
    }
    @Override
    public void drawPolyline(final int[] xPoints, final int[] yPoints, final int nPoints) {
    }
    @Override
    public void drawPolygon(final int[] xPoints, final int[] yPoints, final int nPoints) {
    }
    @Override
    public void drawOval(final int x, final int y, final int width, final int height) {
    }
    @Override
    public void drawLine(final int x1, final int y1, final int x2, final int y2) {
    }
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final Color bgcolor, final ImageObserver observer) {
        return true;
    }
    @Override
    public boolean drawImage(final Image img, final int dx1, final int dy1, final int dx2, final int dy2, final int sx1, final int sy1, final int sx2, final int sy2, final ImageObserver observer) {
        return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer);
    }
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final Color bgcolor, final ImageObserver observer) {
        return false;
    }
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final int width, final int height, final ImageObserver observer) {
        return drawImage(img, x, y, width, height, null, observer);
    }
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final Color bgcolor, final ImageObserver observer) {
        return false;
    }
    @Override
    public boolean drawImage(final Image img, final int x, final int y, final ImageObserver observer) {
        return drawImage(img, x, y, null, observer);
    }
    @Override
    public void drawArc(final int x, final int y, final int width, final int height, final int startAngle, final int arcAngle) {
    }
    @Override
    public void dispose() {
    }
    @Override
    public Graphics create() {
        return this;
    }
    @Override
    public void copyArea(final int x, final int y, final int width, final int height, final int dx, final int dy) {
    }
    @Override
    public void clipRect(final int x, final int y, final int width, final int height) {
        SwingUtilities.computeIntersection(x, y, width, height, clip);
    }
    @Override
    public void clearRect(final int x, final int y, final int width, final int height) {
    }
    @Override
    public void translate(final double tx, final double ty) {
        getTransform().translate(tx, ty);
    }
    @Override
    public void translate(final int x, final int y) {
        translate((double) x, (double) y);
    }
    @Override
    public void transform(final AffineTransform Tx) {
        getTransform().concatenate(Tx);
    }
    @Override
    public void shear(final double shx, final double shy) {
        getTransform().shear(shx, shy);
    }
    @Override
    public void scale(final double sx, final double sy) {
        getTransform().scale(sx, sy);
    }
    @Override
    public void setTransform(final AffineTransform Tx) {
        transform = Tx;
    }
    @Override
    public void setStroke(final Stroke s) {
        stroke = s;
    }
    @Override
    public void setRenderingHints(final Map<?, ?> hints) {
        renderingHints.clear();
        renderingHints.putAll(hints);
    }
    @Override
    public void setRenderingHint(final Key hintKey, final Object hintValue) {
        renderingHints.put(hintKey, hintValue);
    }
    @Override
    public void setPaint(final Paint paint) {
        this.paint = paint;
    }
    @Override
    public void setComposite(final Composite comp) {
        composite = comp;
    }
    @Override
    public void setBackground(final Color color) {
        background = color;
    }
    @Override
    public void rotate(final double theta, final double x, final double y) {
        getTransform().rotate(theta, x, y);
    }
    @Override
    public void rotate(final double theta) {
        getTransform().rotate(theta);
    }
    @Override
    public boolean hit(final Rectangle rect, final Shape s, final boolean onStroke) {
        return false;
    }
    @Override
    public AffineTransform getTransform() {
        return transform;
    }
    @Override
    public Stroke getStroke() {
        return stroke;
    }
    @Override
    public RenderingHints getRenderingHints() {
        return renderingHints;
    }
    @Override
    public Object getRenderingHint(final Key hintKey) {
        return renderingHints.get(hintKey);
    }
    @Override
    public Paint getPaint() {
        return paint;
    }
    @Override
    public FontRenderContext getFontRenderContext() {
        return new FontRenderContext(transform, isAntiAliased, usesFractionalMetrics);
    }
    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return graphicsConfiguration;
    }
    @Override
    public Composite getComposite() {
        return composite;
    }
    @Override
    public Color getBackground() {
        return background;
    }
    @Override
    public void fill(final Shape s) {
    }
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final float x, final float y) {
    }
    @Override
    public void drawString(final AttributedCharacterIterator iterator, final int x, final int y) {
        drawString(iterator, (float) x, (float) y);
    }
    @Override
    public void drawString(final String str, final float x, final float y) {
        drawString(new AttributedString(str).getIterator(), x, y);
    }
    @Override
    public void drawString(final String str, final int x, final int y) {
        drawString(str, (float) x, (float) y);
    }
    @Override
    public void drawRenderedImage(final RenderedImage img, final AffineTransform xform) {
    }
    @Override
    public void drawRenderableImage(final RenderableImage img, final AffineTransform xform) {
    }
    @Override
    public void drawImage(final BufferedImage img, final BufferedImageOp op, final int x, final int y) {
    }
    @Override
    public boolean drawImage(final Image img, final AffineTransform xform, final ImageObserver obs) {
        return false;
    }
    @Override
    public void drawGlyphVector(final GlyphVector g, final float x, final float y) {
    }
    @Override
    public void draw(final Shape s) {
    }
    @Override
    public void clip(final Shape s) {
    }
    @Override
    public void addRenderingHints(final Map<?, ?> hints) {
        renderingHints.putAll(hints);
    }
}