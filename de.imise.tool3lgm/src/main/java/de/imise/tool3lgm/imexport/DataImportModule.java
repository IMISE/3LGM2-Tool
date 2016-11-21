/*
 * Created on 21.04.2005
 */
package de.imise.tool3lgm.imexport;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import de.imise.tool3lgm.Tool3lgmConstants;
import de.imise.tool3lgm.graphtools.GraphDocument;
import de.imise.tool3lgm.graphtools.LGMGraphDocument;
import de.imise.tool3lgm.graphtools.Szenario;
import de.imise.tool3lgm.graphtools.elements.Kante;
import de.imise.tool3lgm.graphtools.elements.ModelConstants;
import de.imise.tool3lgm.graphtools.elements.ModelElement;
import de.imise.tool3lgm.graphtools.elements.edge.AwbKommssVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntDotVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntEtVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.EtntNatVerbindung;
import de.imise.tool3lgm.graphtools.elements.edge.KommBeziehung;
import de.imise.tool3lgm.graphtools.elements.edge.KommbezEtntVerbindung;
import de.imise.tool3lgm.graphtools.elements.node.Bausteinschnittstelle;
import de.imise.tool3lgm.graphtools.elements.node.Dokumententyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisDokumentenTyp;
import de.imise.tool3lgm.graphtools.elements.node.EreignisNachrichtenTyp;
import de.imise.tool3lgm.graphtools.elements.node.Ereignistyp;
import de.imise.tool3lgm.graphtools.elements.node.EtntEtdtKombination;
import de.imise.tool3lgm.graphtools.elements.node.KonAnwendungsbaustein;
import de.imise.tool3lgm.graphtools.elements.node.Nachrichtentyp;
import de.imise.tool3lgm.graphtools.undoredo.TransactionManager;
import de.imise.tool3lgm.graphtools.userfield.UserField;
import de.imise.tool3lgm.graphtools.userfield.UserFieldDefinitions;
import de.imise.tool3lgm.graphtools.view.container.NodeContainer;
import de.imise.tool3lgm.graphtools.view.graph.GraphElementLayout;
import de.imise.tool3lgm.log.Log;
import de.imise.tool3lgm.xml.ToolXMLParser;
import de.imise.tool3lgm.xml.XMLCharacterCoder;
import de.imise.util.swing.component.AlphabeticalComboBox;

/**
 * @author thomas
 */
public class DataImportModule extends JDialog implements ActionListener {

    //TODO: der Import funktioniert nicht richtig - es wird immer ein Element "Name" angelegt, weil wohl die Namenszeile mit als Element eingelesen wird.

    AlphabeticalComboBox typListe;
    LGMGraphDocument doc;
    File importFile;
    JButton importBut, endeBut;

    @SuppressWarnings("unchecked")
    public DataImportModule(final Frame owner) {
        super(owner);

        setTitle(Tool3lgmConstants.getResString("importd"));

        JPanel panel = new JPanel();
        panel.setSize(250, 250);

        GridBagLayout gbl = new GridBagLayout();
        panel.setLayout(gbl);
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.weightx = 0;
        constraints.weighty = 0;
        JPanel text = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(250, 100);
            }
        };
        text.setLayout(new GridLayout(4, 2));
        text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel label = new JLabel(Tool3lgmConstants.getResString("kntyp_s"));
        text.add(label);

        typListe = new AlphabeticalComboBox();
        for (Class<? extends ModelElement> c : ModelConstants.IMPORTABLE_NODES) {
            typListe.addItem(c, ModelConstants.getDisplayableName(c));
        }

        text.add(typListe);
        add(panel, text, constraints, 0, 0, 1, 1);
        importBut = new JButton(Tool3lgmConstants.getResString("importButtonText"));
        importBut.addActionListener(this);
        text.add(importBut);
        endeBut = new JButton(Tool3lgmConstants.getResString("exit"));
        endeBut.addActionListener(this);
        text.add(endeBut);
        add(panel, text, constraints, 0, 0, 1, 1);

        getContentPane().add(panel);
        pack();
    }

    public void showDialog(final LGMGraphDocument d, final File importFile) {
        doc = d;
        this.importFile = importFile;
        setVisible(true);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        if (e.getSource() == importBut) {
            if (JOptionPane.showConfirmDialog(null, Tool3lgmConstants.getResString("importfrage"), Tool3lgmConstants.getResString("tool3lgm"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                importData();
            }
            dispose();
        }
        if (e.getSource() == endeBut) {
            dispose();
        }
    }

    /**
     * @param con
     * @param c
     * @param gbc
     * @param x
     * @param y
     * @param w
     * @param h
     */
    private static void add(final Container con, final Component c, final GridBagConstraints gbc, final int x, final int y, final int w, final int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        con.add(c, gbc);
    }

    private void importData() {
        int x = -480;
        int y = -310;

        ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>(5000);

        BufferedReader reader = null;
        try {
            FileInputStream istream = new FileInputStream(importFile);
            reader = new BufferedReader(new InputStreamReader(istream));

            while (reader.ready()) {
                String line = reader.readLine();
                line = line.replaceAll("\\\\n", "\n").replaceAll("\\\\t", "\t");
                StringTokenizer tokenizer = new StringTokenizer(line, "\t");
                ArrayList<String> tokens = new ArrayList<String>(10);
                while (tokenizer.hasMoreTokens()) {
                    tokens.add(tokenizer.nextToken());
                }
                data.add(tokens);
            }
            reader.close();
        } catch (Exception ex) {
            Log.show(Log.ERROR, Tool3lgmConstants.getErrString("FehlerAllgemein"), ex);
            return;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        UserFieldDefinitions definitions = new UserFieldDefinitions(doc.getCollection());
        ArrayList<String> userFieldHashStrings = new ArrayList<String>(8);
        int nameIndex = -1;
        int descrIndex = -1;
        int hashFieldIndex = -1;
        int superHashFieldIndex = -1;
        ArrayList<Integer> extIDPoss = new ArrayList<Integer>();
        ArrayList<String> extIDKeys = new ArrayList<String>();

        int sendBssIndex = -1;
        int recvBssIndex = -1;
        int msgTypIndex = -1;
        int evtTypIndex = -1;
        int sendAwbIndex = -1;
        int recvAwbIndex = -1;

        Class<? extends ModelElement> elementClass = ((Class<?>) typListe.getSelectedObject()).asSubclass(ModelElement.class);

        boolean unique = ModelConstants.isUnique(elementClass);

        ArrayList<String> tokens = data.get(0);
        for (int i = 0; i < tokens.size(); i++) {
            String name = tokens.get(i);
            if (name.equalsIgnoreCase("Name")) {
                nameIndex = i;
                continue;
            }
            if (name.equalsIgnoreCase("Beschreibung") || name.equalsIgnoreCase("Description") || name.equalsIgnoreCase("Beschreibung (Description)")) {
                descrIndex = i;
                continue;
            }
            if (name.equalsIgnoreCase("HashString")) {
                hashFieldIndex = i;
                continue;
            }
            if (name.equalsIgnoreCase("SuperHashString")) {
                superHashFieldIndex = i;
                continue;
            }
            if (name.toLowerCase().startsWith("extid")) {
                extIDPoss.add(new Integer(i));
                extIDKeys.add(name);
                continue;
            }

            if (elementClass == KommBeziehung.class) {
                if (name.equalsIgnoreCase("SendBSS")) {
                    sendBssIndex = i;
                    continue;
                }
                if (name.equalsIgnoreCase("RecvBSS") || name.equalsIgnoreCase("EmpfBSS")) {
                    recvBssIndex = i;
                    continue;
                }
                if (name.equalsIgnoreCase("SendAWB")) {
                    sendAwbIndex = i;
                    continue;
                }
                if (name.equalsIgnoreCase("RecvAWB") || name.equalsIgnoreCase("EmpfAWB")) {
                    recvAwbIndex = i;
                    continue;
                }
                if (name.equalsIgnoreCase("MsgTyp") || name.equalsIgnoreCase("NachTyp") || name.equalsIgnoreCase("Nachrichtentyp") || name.equalsIgnoreCase("Message type")) {
                    msgTypIndex = i;
                    continue;
                }
                if (name.equalsIgnoreCase("EvtTyp") || name.equalsIgnoreCase("ErgnTyp") || name.equalsIgnoreCase("Ereignistyp") || name.equalsIgnoreCase("Event type")) {
                    evtTypIndex = i;
                    continue;
                }
            }

            UserField userField = new UserField(elementClass, definitions);
            userField.setName(name);
            userField.hasStyle(UserField.Style.SINGLE_LINE);
            UserField oldUserField = doc.getCollection().getUserFieldDefinitions().getUserField(userField.getTargetClass(), userField.getName());
            if (oldUserField == null) {
                userFieldHashStrings.add(userField.getHashCode());
                definitions.add(userField);
            } else {
                userFieldHashStrings.add(oldUserField.getHashCode());
            }
        }

        final int PID = TransactionManager.STANDARD_PID;
        doc.start_transaction(PID);

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        DataOutputStream xmlOutStream = new DataOutputStream(boas);

        try {

            //das hier war ohne das try-catch das Original. Rückgängig: einfach alle xmlOutStream.writeBytes
            //durch xmlBuilder.append ersetzen
            //		StringBuilder xmlBuilder = new StringBuilder(100000);

            xmlOutStream.writeBytes(ToolXMLParser.getCurrentVersionString() + "<" + "import" + ">");
            xmlOutStream.writeBytes(definitions.toXMLString());
            xmlOutStream.writeBytes("<objects><avoidDuplicates>");
            for (int i = 1; i < data.size(); i++) {
                tokens = data.get(i);
                if (tokens.size() < 2) {
                    continue;
                }

                String name = "\"\"";
                String descr = "\"\"";
                String newHashString = "";
                boolean newElement = false;
                //diese Variable wird nie gelesen, aber irgendwo gesetzt.
                //            String superHashString = "";
                String kommBezExtID = "";
                String sendAwbExtID = "";
                String recvAwbExtID = "";
                String sendBssExtID = "";
                String recvBssExtID = "";
                String msgdocTypExtID = "";
                String evtTypExtID = "";

                StringBuilder extIDStringBuilder = new StringBuilder(100000);
                Hashtable<String, String> extIDTable = new Hashtable<String, String>();
                StringBuilder userFieldXMLStringBuilder = new StringBuilder(100000);
                int realIndex = 0;
                for (int j = 0; j < tokens.size(); j++) {
                    if (j == hashFieldIndex) {
                        newHashString = tokens.get(j);
                        continue;
                    }
                    if (j == superHashFieldIndex) {
                        /* superHashString = */tokens.get(j);
                        continue;
                    }
                    if (j == nameIndex) {
                        name = tokens.get(j);
                        continue;
                    }
                    if (j == descrIndex) {
                        descr = tokens.get(j);
                        continue;
                    }
                    Integer J = new Integer(j);
                    if (extIDPoss.contains(J)) {
                        String value = tokens.get(j).toUpperCase();
                        int keyPos = extIDPoss.indexOf(J);
                        extIDTable.put(extIDKeys.get(keyPos), value);
                        if (value.length() > 0 && !value.equals("\"\"") && !value.equalsIgnoreCase("__3LGM_DELETE__")) {
                            extIDStringBuilder.append("<field name=\"" + extIDKeys.get(keyPos) + "\">" + XMLCharacterCoder.encodeString(value) + "</field>");
                        } else {
                            extIDStringBuilder.append("<field name=\"" + extIDKeys.get(keyPos) + "\">__3LGM_DELETE__</field>");
                        }
                        continue;
                    }

                    if (elementClass == KommBeziehung.class) {
                        if (j == sendAwbIndex) {
                            sendAwbExtID = tokens.get(j).toUpperCase();
                            continue;
                        } else if (j == recvAwbIndex) {
                            recvAwbExtID = tokens.get(j).toUpperCase();
                            continue;
                        } else if (j == sendBssIndex) {
                            sendBssExtID = tokens.get(j).toUpperCase();
                            continue;
                        } else if (j == recvBssIndex) {
                            recvBssExtID = tokens.get(j).toUpperCase();
                            continue;
                        } else if (j == msgTypIndex) {
                            msgdocTypExtID = tokens.get(j).toUpperCase();
                            continue;
                        } else if (j == evtTypIndex) {
                            evtTypExtID = tokens.get(j).toUpperCase();
                            continue;
                        }
                    }
                    String hash = realIndex < userFieldHashStrings.size() ? userFieldHashStrings.get(realIndex) : "" + realIndex;
                    String value = tokens.get(j);
                    int index = value.indexOf("_rangedef_");
                    //AXS: die RangeDefs sind komplett rausgeflogen. Da ich die Konsequenzen von Änderungen an dieser Stelle nicht kenne, lasse 
                    //ich den Code hier stehen. nur unten das anhängen der Range-De-Information an den XML-String habe ich rausgenommen.
                    @SuppressWarnings("unused")
                    String rangeDef = "";
                    if (index > -1) {
                        rangeDef += value.substring(index + 10);
                        value = value.substring(0, index);
                    }
                    if (value.length() > 0 && !value.equals("\"\"") && !value.equalsIgnoreCase("__3LGM_DELETE__")) {
                        userFieldXMLStringBuilder.append("<userField hash=\"" + hash + "\">" + XMLCharacterCoder.encodeString(value) + "</userField>");
                    } else {
                        userFieldXMLStringBuilder.append("<userField hash=\"" + hash + "\">__3LGM_DELETE__</userField>");
                    }
                    //
                    //				if (! rangeDef.equals(""))
                    //					userFieldXMLStringBuilder.append("<userFieldRangeDef hash=\"" + hash + "\">" + rangeDef + "</userFieldRangeDef>");
                    realIndex++;
                }

                if (elementClass == KommBeziehung.class) {
                    ModelElement sendAwb = doc.findElementWithExternalID("extID_AWB", sendAwbExtID);
                    ModelElement recvAwb = doc.findElementWithExternalID("extID_AWB", recvAwbExtID);
                    ModelElement sendBss = doc.findElementWithExternalID("extID_BSS", sendBssExtID);
                    ModelElement recvBss = doc.findElementWithExternalID("extID_BSS", recvBssExtID);
                    ModelElement msgdocTyp = doc.findElementWithExternalID("extID_MsgTyp", msgdocTypExtID);
                    ModelElement evtTyp = doc.findElementWithExternalID("extID_EvtTyp", evtTypExtID);
                    ModelElement kommBez = null;
                    int index = extIDKeys.indexOf("extID_KommBez");
                    if (index >= 0) {
                        int kommBezExtIDPos = extIDPoss.get(index).intValue();
                        kommBezExtID = tokens.get(kommBezExtIDPos).toUpperCase();
                        kommBez = doc.findElementWithExternalID("extID_kommBez", kommBezExtID);
                    }

                    if (kommBez == null && (sendAwb == null && sendBss == null || recvAwb == null && recvBss == null)) {
                        continue;
                    }

                    try {
                        if (recvBss != null && sendBss != null) {
                            ArrayList<Kante> connections = recvBss.getEdgesWith(sendBss);
                            if (connections.size() > 0 && kommBez != null) {
                                if (!kommBezExtID.equals(connections.get(0).getExternalID("extID_kommBez"))) {
                                    continue;
                                }
                            }
                        }
                        boolean interactiveMode = doc.getCollection().isInteractiveMode();
                        doc.getCollection().setInteractiveMode(false);
                        if (sendBss == null && kommBez == null) {
                            sendBss = LGMGraphDocument.createAddicted(doc, sendAwb, AwbKommssVerbindung.class, Bausteinschnittstelle.class, sendBssExtID, PID);
                            //						sendBss = doc.createBausteinSNforABS(sendAwb.getHashString(), sendBssExtID, null, PID);
                            if (sendBss == null) {
                                continue;
                            }
                            sendBss.setExternalID("extID_BSS", sendBssExtID);
                        }
                        if (recvBss == null && kommBez == null) {
                            recvBss = LGMGraphDocument.createAddicted(doc, recvAwb, AwbKommssVerbindung.class, Bausteinschnittstelle.class, recvBssExtID, PID);
                            //						recvBss = doc.createBausteinSNforABS(recvAwb.getHashString(), recvBssExtID, null, PID);
                            if (recvBss == null) {
                                continue;
                            }
                            recvBss.setExternalID("extID_BSS", recvBssExtID);
                        }
                        if (kommBez == null) {
                            kommBez = doc.getCollection().link(KommBeziehung.class, newHashString, sendBss, recvBss, PID);
                            if (kommBez == null) {
                                continue;
                            }
                            if (!name.equals("\"\"")) {
                                kommBez.setName(name);
                            }
                            if (!descr.equals("\"\"")) {
                                kommBez.setDescription(descr);
                            }
                        }
                        xmlOutStream.writeBytes("<element class=\"" + elementClass.getSimpleName() + "\" ");
                        xmlOutStream.writeBytes("hash=\"" + kommBez.getHashString() + "\" ");
                        xmlOutStream.writeBytes("layer=\"" + ModelConstants.layerFor(elementClass) + "\"");
                        xmlOutStream.writeBytes(">");
                        xmlOutStream.writeBytes(extIDStringBuilder.toString());
                        xmlOutStream.writeBytes(userFieldXMLStringBuilder.toString());
                        xmlOutStream.writeBytes("</element>");

                        if (msgdocTyp == null) {
                            boolean dokTypAnlegen = false;
                            if (sendAwb instanceof KonAnwendungsbaustein || recvAwb instanceof KonAnwendungsbaustein) {
                                dokTypAnlegen = true;
                            }

                            msgdocTyp = doc.createKnotenWithContainer(dokTypAnlegen ? Dokumententyp.class : Nachrichtentyp.class, PID).getElement();
                            msgdocTyp.setExternalID("extID_MsgTyp", msgdocTypExtID);
                            msgdocTyp.setName(msgdocTypExtID);
                        }
                        if (evtTyp == null) {
                            evtTyp = doc.createKnotenWithContainer(Ereignistyp.class, PID).getElement();
                            evtTyp.setExternalID("extID_EvtTyp", evtTypExtID);
                            evtTyp.setName(evtTypExtID);
                        }
                        ArrayList<ModelElement> etnts = new ArrayList<ModelElement>();
                        if (msgdocTyp instanceof Nachrichtentyp) {
                            etnts.addAll(msgdocTyp.getConnectedElements(EreignisNachrichtenTyp.class));
                        } else if (msgdocTyp instanceof Dokumententyp) {
                            etnts.addAll(msgdocTyp.getConnectedElements(EreignisDokumentenTyp.class));
                        }

                        boolean createEtnt = true;
                        EtntEtdtKombination etnt = null;
                        //diese Variable wird nie gelesen, aber gesetzt. Wozu?
                        @SuppressWarnings("unused")
                        String evtMsgTypHashString = "";
                        for (int j = 0; j < etnts.size(); j++) {
                            etnt = (EtntEtdtKombination) etnts.get(j);
                            if (etnt.isConnectedWith(evtTyp)) {
                                createEtnt = false;
                                evtMsgTypHashString = etnt.getHashString();
                                break;
                            }
                        }
                        if (createEtnt) {
                            NodeContainer etntC = doc.createKnotenWithContainer(msgdocTyp instanceof Nachrichtentyp ? EreignisNachrichtenTyp.class : EreignisDokumentenTyp.class, PID);
                            doc.getCollection().link(EtntEtVerbindung.class, etntC.getElement(), evtTyp, PID);
                            doc.getCollection().link(msgdocTyp instanceof Nachrichtentyp ? EtntNatVerbindung.class : EtntDotVerbindung.class, etntC.getElement(), msgdocTyp, PID);
                            evtMsgTypHashString = etntC.getHashString();
                            etnt = (EtntEtdtKombination) etntC.getElement();
                        }

                        //diese Variable wird nie gelesen, aber gesetzt. Wozu?
                        @SuppressWarnings("unused")
                        String vHashString = null;
                        @SuppressWarnings("unused")
                        KommbezEtntVerbindung v = null;
                        if (/* kommBez != null && */etnt != null) {
                            ArrayList<Kante> connections = kommBez.getEdgesWith(etnt);
                            if (connections.size() > 0) {
                                vHashString = connections.get(0).getHashString();
                            }
                        }
                        //					if (v == null) {
                        boolean forward = true;
                        if (sendBss != null && recvBss != null && /* kommBez != null && */((KommBeziehung) kommBez).getEnd() == sendBss) {
                            forward = false;
                        }
                        if (forward) {
                            doc.getCollection().link(KommbezEtntVerbindung.class, etnt, kommBez, PID);
                        } else {
                            doc.getCollection().link(KommbezEtntVerbindung.class, kommBez, etnt, PID);
                        }
                        //					}
                        doc.getCollection().setInteractiveMode(interactiveMode);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    if (newHashString == null || newHashString.equals("")) {
                        ModelElement el = null;
                        if (extIDKeys.size() > 0) {
                            el = doc.findElementWithExternalID(extIDKeys.get(0), extIDTable.get(extIDKeys.get(0)));
                        }
                        if (el != null) {
                            newHashString = el.getHashString();
                        } else {
                            newHashString = ModelElement.getNewHashString(elementClass);
                            newElement = true;
                        }
                    } else {
                        newElement = doc.findElementCoded(newHashString) == null;
                    }

                    xmlOutStream.writeBytes("<element class=\"" + elementClass.getSimpleName() + "\" ");
                    xmlOutStream.writeBytes("hash=\"" + newHashString + "\" ");
                    xmlOutStream.writeBytes("layer=\"" + ModelConstants.layerFor(elementClass) + "\"");
                    xmlOutStream.writeBytes(">");
                    if (!name.equals("\"\"")) {
                        xmlOutStream.writeBytes("<field name=\"name\">" + XMLCharacterCoder.encodeString(name) + "</field>");
                    }
                    if (!descr.equals("\"\"")) {
                        xmlOutStream.writeBytes("<field name=\"description\">" + XMLCharacterCoder.encodeString(descr) + "</field>");
                    }
                    xmlOutStream.writeBytes(extIDStringBuilder.toString());
                    xmlOutStream.writeBytes(userFieldXMLStringBuilder.toString());
                    xmlOutStream.writeBytes("</element>");
                }

                if (!unique && doc instanceof Szenario) {
                    if (!(elementClass == KommBeziehung.class)) {
                        if (newElement) {
                            xmlOutStream.writeBytes("<container hash=\"" + newHashString + "\">");
                            xmlOutStream.writeBytes("<expanded>true</expanded><visible>true</visible>");
                            GraphElementLayout l = new GraphElementLayout();
                            l.x = x;
                            x += 2;
                            l.y = y;
                            y += 2;
                            xmlOutStream.writeBytes(l.getXMLString(false, true));
                            xmlOutStream.writeBytes("</container>");
                        }
                    }
                }
            }

            xmlOutStream.writeBytes("</avoidDuplicates></objects>");
            xmlOutStream.writeBytes("</" + "import" + ">");

        } catch (Exception e) {
            e.printStackTrace();
        }

        //		doc.pasteInputStream(new StringBufferInputStream(xmlBuffer.toString()));
        doc.pasteInputStream(new ByteArrayInputStream(boas.toByteArray()));
        doc.finish_transaction(PID);
        doc.distributeEvent(GraphDocument.DATA_CHANGED);
    }
}
