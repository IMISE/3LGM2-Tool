package de.imise.tool3lgm.graphtools.metamodel.elements;

import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.BACKWARD;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.DOUBLE;
import static de.imise.tool3lgm.graphtools.metamodel.elements.DoubleMeaningEdge.ConnectionState.FORWARD;

import de.imise.tool3lgm.graphtools.metamodel.MetaModelInstance;

/**
 * Oberklasse für alle Kantenklassen mit doppelter Bedeutung. Also Kanten, die 2 Assoziationen in einer zwischen 2 Klassen sind.
 *
 * @author AXS (25.09.2017)
 */
public abstract class DoubleMeaningEdge extends Edge {

    public enum ConnectionState {
        FORWARD,
        BACKWARD,
        DOUBLE
    }

    private ConnectionState connectionState = FORWARD; //null als Wert ist ausgeschlossen!

    @Override
    public final boolean putXMLFieldString(final String field, final String value) {
        if (field.equals("state")) {
            for (ConnectionState connectionState : ConnectionState.values()) {
                if (connectionState.name().equals(value)) {
                    this.connectionState = connectionState;
                    return true;
                }
            }
            return false;
        }
        return super.putXMLFieldString(field, value);
    }

    //Diese Funktion kam ehemals aus SubordinationEdge

    //    @Override
    //    public final void setStartToEndConnectionState(final Direction startToEndConnectionState) {
    //        ModelElement start = k1;
    //        ModelElement end = k2;
    //        switch (startToEndConnectionState) {
    //        case DOUBLE:
    //        case FORWARD:
    //            break;
    //        case BACKWARD:
    //            if (Edge.isStartClass(getClass(), k2.getClass()) && Edge.isEndClass(getClass(), k1.getClass())) {
    //                ModelElement temp = k1;
    //                k1 = k2;
    //                k2 = temp;
    //                super.setStartToEndConnectionState(DOUBLE);
    //            }
    //            break;
    //        }
    //        if (isInCircle()) {
    //            k1 = start;
    //            k2 = end;
    //        }
    //        return;
    //    }

    /**
     * @return
     */
    public ConnectionState getConnectionState() {
        return connectionState;
    }

    /**
     * @param connectionState
     */
    public void setConnectionState(final ConnectionState connectionState) {
        if (connectionState != null) {
            this.connectionState = connectionState;
        }
    }

    public final String getConnectionStateName() {
        return connectionState.name();
    }

    @Override
    public final boolean isDirecting(final ModelElement me1, final ModelElement me2) {
        switch (connectionState) {
        case DOUBLE:
            return isConnecting(me1, me2);
        case FORWARD:
            return isDirectingForward(me1, me2);
        case BACKWARD:
            return isDirectingForward(me2, me1);
        default:
            return false;
        }
    }

    /**
     * Prüft die Validität der Kanten und stellt sie wenn möglich her. Die Prüfung betrifft die Art der Kantenelemente
     *
     * @return <code>true</code>, wenn die Edge vollständig richtig ist
     */
    @Override
    public boolean checkValidity() {
        boolean startClassOk = false, endClassOk = false;
        boolean switchStart = false, switchEnd = false;
        if (startElement != null && endElement != null) {
            Class<? extends ModelElement> clazz = startElement.getClass();
            //prüfen, ob das StartElement von einer der Startklassen ist
            if (!isStartClass(clazz)) {
                //wenn nicht
                switchStart = isEndClass(clazz);
            } else {
                startClassOk = true;
            }
            clazz = endElement.getClass();
            //prüfen, ob das EndElement von einer der Endklassen ist
            if (!isEndClass(clazz)) {
                //wenn nicht
                switchEnd = isStartClass(clazz);
            } else {
                endClassOk = true;
            }
        }
        boolean switchClasses = false;
        //wenn sich die Konsitenz herstellen lässt indem man beide Elemente vertaucht -> vertauschen
        if (switchStart && switchEnd) {
            switchClasses = true;
        } else if (switchStart) {
            if (!isStartClass(endElement.getClass())) {
                return false;
            }
            switchClasses = true;
        } else if (switchEnd) {
            if (!isEndClass(startElement.getClass())) {
                return false;
            }
            switchClasses = true;
        }
        //bei allen Kanten, bei denen die Richtung egal ist, wird sie immer auf DOUBLE gesetzt (das macht die GDCollection in link auch!)
        MetaModelInstance metaModel = getMetaModel();
        if (!metaModel.isDirectedEdge(getClass())) {
            connectionState = DOUBLE;
        }
        if (switchClasses) {
            ModelElement dummy = startElement;
            startElement = endElement;
            endElement = dummy;
            if (connectionState == FORWARD) {
                connectionState = BACKWARD;
            } else if (connectionState == BACKWARD) {
                connectionState = FORWARD;
            }
            return true;
        }
        //Es musste nichts vertauscht werden -> hier kommt nur true zurück, wenn die Klassen
        //der Start- und Endelemente mit den Metaklassen übereinstimmen.
        return startClassOk && endClassOk;
    }

}