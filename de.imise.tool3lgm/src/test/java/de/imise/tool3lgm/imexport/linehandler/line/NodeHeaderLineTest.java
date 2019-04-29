package de.imise.tool3lgm.imexport.linehandler.line;

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import de.imise.tool3lgm.graphtools.metamodel.elements.ModelElement;
import de.imise.tool3lgm.graphtools.metamodel.elements.Node;

public class NodeHeaderLineTest {

    @Test
    public void NodeHeaderLine() {
        Class<? extends ModelElement> elementClass = Node.class;
        String elementType = "Aufgabe oder irgend was anderes";
        String nameHeader = "irrelevantString1";
        String descriptionHeader = "irrelevantString2";
        String hashHeader = "irrelevantString3";
        List<String> userFieldNames = new ArrayList<>();
        userFieldNames.add("userFieldName");
        userFieldNames.add("userFieldName");
        userFieldNames.add("userFieldName2");
        int row = 1;
        NodeHeaderLine nodeHeaderLine = new NodeHeaderLine(elementClass, elementType, nameHeader, descriptionHeader, hashHeader, userFieldNames, row);
        assertEquals(nodeHeaderLine.getElementClass(), elementClass);
        assertEquals(nodeHeaderLine.getName(), nameHeader);
        assertEquals(nodeHeaderLine.getDescription(), descriptionHeader);
        assertEquals(nodeHeaderLine.getHash(), hashHeader);
        assertEquals(nodeHeaderLine.getUserFields(), userFieldNames);
        assertEquals(nodeHeaderLine.getRow(), row);

    }
}
