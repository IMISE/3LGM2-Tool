package de.imise.util.swing.component;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.testng.annotations.Test;

public class ParentComponentFinderTest {

    private interface Interface1 {
    }
    private interface Interface2 extends Interface1 {
    }
    private interface Interface3 extends Interface1 {
    }

    private class Comp extends Container implements Interface1 {
    }

    private class Pane extends JPanel implements Interface2 {
    }

    @Test
    public void getParentTest() {
        Component component = mock(Component.class);
        Comp comp = mock(Comp.class);
        Pane pane = mock(Pane.class);
        when(component.getParent()).thenReturn(comp);
        when(comp.getParent()).thenReturn(pane);
        Component parent1 = ParentComponentFinder.getParent(component, Component.class);
        assertEquals(parent1, comp);
        Object parent2 = ParentComponentFinder.getParent(component, Object.class);
        assertEquals(parent2, comp);
        JScrollPane parent3 = ParentComponentFinder.getParent(component, JScrollPane.class);
        assertEquals(parent3, null);
        Pane parent4 = ParentComponentFinder.getParent(component, Pane.class);
        assertEquals(parent4, pane);
        Interface1 parent5 = ParentComponentFinder.getParent(component, Interface1.class);
        assertEquals(parent5, comp);
        Interface2 parent6 = ParentComponentFinder.getParent(component, Interface2.class);
        assertEquals(parent6, pane);
        Interface3 parent7 = ParentComponentFinder.getParent(component, Interface3.class);
        assertEquals(parent7, null);
    }
}
