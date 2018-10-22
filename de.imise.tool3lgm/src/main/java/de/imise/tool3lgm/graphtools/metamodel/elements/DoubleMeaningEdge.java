package de.imise.tool3lgm.graphtools.metamodel.elements;

import de.imise.tool3lgm.graphtools.metamodel.ModelConstants;

/**
 * Oberklasse für alle Kantenklassen mit doppelter Bedeutung. Also Kanten, die 2 Assoziationen in einer zwischen 2 Klassen sind.
 *
 * @author AXS (25.09.2017)
 */
public abstract class DoubleMeaningEdge extends Edge {

    public enum MeaningState {
        FORWARD,
        BACKWARD,
        DOUBLE,
    }

    private MeaningState meaningState = MeaningState.FORWARD;

    @Override
    public final boolean putXMLFieldString(final String field, final String value) {
        if (field.equals("state")) {
            for (MeaningState meaningState : MeaningState.values()) {
                if (meaningState.name().equals(value)) {
                    this.meaningState = meaningState;
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
    public MeaningState getMeaningState() {
        return meaningState;
    }

    /**
     * @param meaningState
     */
    public void setMeaningState(final MeaningState meaningState) {
        this.meaningState = meaningState;
    }

    public final String getMeaningStateName() {
        return meaningState.name();
    }

    @Override
    public final boolean isDirecting(final ModelElement _k1, final ModelElement _k2) {
        switch (meaningState) {
        case DOUBLE:
            return isConnecting(_k1, _k2);
        case FORWARD:
            return isDirectingForward(_k1, _k2);
        case BACKWARD:
            return isDirectingForward(_k2, _k1);
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
        if (k1 != null && k2 != null) {
            Class<? extends ModelElement> clazz = k1.getClass();
            //prüfen, ob das StartElement von einer der Startklassen ist
            if (!isStartClass(clazz)) {
                //wenn nicht
                switchStart = isEndClass(clazz);
            } else {
                startClassOk = true;
            }
            clazz = k2.getClass();
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
            if (!isStartClass(k2.getClass())) {
                return false;
            }
            switchClasses = true;
        } else if (switchEnd) {
            if (!isEndClass(k1.getClass())) {
                return false;
            }
            switchClasses = true;
        }
        //bei allen Kanten, bei denen die Richtung egal ist, wird sie immer auf DOUBLE gesetzt (das macht die GDCollection in link auch!)
        if (!ModelConstants.isDirectedEdge(getClass())) {
            meaningState = MeaningState.DOUBLE;
        }
        if (switchClasses) {
            ModelElement dummy = k1;
            k1 = k2;
            k2 = dummy;
            if (meaningState == MeaningState.FORWARD) {
                meaningState = MeaningState.BACKWARD;
            } else if (meaningState == MeaningState.BACKWARD) {
                meaningState = MeaningState.FORWARD;
            }
            return true;
        }
        //Es musste nichts vertauscht werden -> hier kommt nur true zurück, wenn die Klassen
        //der Start- und Endelemente mit den Metaklassen übereinstimmen.
        return startClassOk && endClassOk;
    }

}