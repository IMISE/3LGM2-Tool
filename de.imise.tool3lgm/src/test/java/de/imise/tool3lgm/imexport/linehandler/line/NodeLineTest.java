package de.imise.tool3lgm.imexport.linehandler.line;

import static org.testng.Assert.assertEquals;

import java.util.List;

import mockit.Mock;
import mockit.MockUp;

import org.testng.annotations.Test;

import de.imise.tool3lgm.graphtools.elements.ModelElement;

public class NodeLineTest {

    @Test
    public void NodeLine() {
        String name = "NAME";
        String description = "DESCRIPTION";
        String hash = "HASH";
        List<String> userFields = null;
        int row = 0;
        NodeLine nodeLine = new NodeLine(null, name, description, hash, userFields, row);
        assertEquals(nodeLine.getElementClass(), null);
        assertEquals(nodeLine.getHeaderLine(), null);
        assertEquals(nodeLine.getName(), name);
        assertEquals(nodeLine.getDescription(), description);
        assertEquals(nodeLine.getHash(), hash);
        assertEquals(nodeLine.getUserFields(), null);
        assertEquals(nodeLine.getRow(), row);
    }

    @Test
    public void NodeLine2() {

        new MockUp<NodeHeaderLine>() {
            @Mock
            public void $init(final Class<? extends ModelElement> elementClass, final String elementType, final String nameHeader, final String descriptionHeader, final String hashHeader, final List<String> userFieldNames, final int row) {

            }

        };

        String name = "NAME";
        String description = "DESCRIPTION";
        String hash = "HASH";
        List<String> userFields = null;
        int row = 0;

        NodeLine nodeLine = new NodeLine(null, name, description, hash, userFields, row);
        assertEquals(nodeLine.getHeaderLine(), null);
        assertEquals(nodeLine.getName(), name);
        assertEquals(nodeLine.getDescription(), description);
        assertEquals(nodeLine.getHash(), hash);
        assertEquals(nodeLine.getUserFields(), null);
        assertEquals(nodeLine.getRow(), row);
    }

}
