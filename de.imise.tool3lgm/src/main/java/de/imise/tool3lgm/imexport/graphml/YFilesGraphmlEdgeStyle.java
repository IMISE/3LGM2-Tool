package de.imise.tool3lgm.imexport.graphml;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelInstance;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge;
import de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState;
import de.imise.tool3lgm.graphtools.metamodel.elements.Edge;
import de.imise.tool3lgm.graphtools.view.container.EdgeContainer;

public class YFilesGraphmlEdgeStyle {

    //    <yjs:PolylineEdgeStyle smoothingLength="0" sourceArrow="TRIANGLE" targetArrow="TRIANGLE">
    //        <yjs:PolylineEdgeStyle.stroke>
    //            <yjs:Stroke fill="BLACK" dashStyle="Dash" thickness="1"/>
    //        </yjs:PolylineEdgeStyle.stroke>
    //    </yjs:PolylineEdgeStyle>

    public String smoothingLength;

    public String sourceArrow;

    public String targetArrow;

    public String strokeFill = null;

    public String strokeDashStyle = null;

    public String strokeThickness = null;

    boolean isDashed = false;

    public YFilesGraphmlEdgeStyle(final EdgeContainer ec) {
        Edge edge = ec.getEdge();
        Class<? extends Edge> edgeClass = edge.getClass();
        MetaModelInstance metaModel = edge.getMetaModel();
        ConnectionState direction = metaModel.isDirectedEdge(edgeClass) ? MetaModelInstance.isDoubleMeaningEdge(edgeClass) ? ((DoubleMeaningEdge) edge).getConnectionState() : ConnectionState.FORWARD : ConnectionState.DOUBLE;
        sourceArrow = direction == ConnectionState.DOUBLE || direction == ConnectionState.BACKWARD ? "TRIANGLE" : null;
        targetArrow = direction == ConnectionState.DOUBLE || direction == ConnectionState.FORWARD ? "TRIANGLE" : null;
        smoothingLength = "0";
        isDashed = MetaModelInstance.isHasPartEdge(edge.getClass());
        if (isDashed) {
            strokeFill = "BLACK";
            strokeDashStyle = "Dash";
            strokeThickness = "1";
        }
    }

    public static YFilesGraphmlEdgeStyle createLabelStyle(final EdgeContainer ec) {
        return new YFilesGraphmlEdgeStyle(ec);
    }

    private static final StringBuilder sb = new StringBuilder();

    public final String getEdgeStyleKey() {
        sb.setLength(0);
        if (smoothingLength != null) {
            sb.append(smoothingLength);
        }
        sb.append(sourceArrow);
        sb.append(targetArrow);
        if (strokeFill != null) {
            sb.append(strokeFill);
        }
        if (strokeDashStyle != null) {
            sb.append(strokeDashStyle);
        }
        if (strokeThickness != null) {
            sb.append(strokeThickness);
        }
        return sb.toString();
    }

}
