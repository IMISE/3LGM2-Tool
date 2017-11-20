package de.imise.tool3lgm.imexport.graphml;

import java.util.StringTokenizer;

enum YFilesGraphmlWriterDataKeys {

    //    <key id="d0" for="node" attr.type="boolean" attr.name="Expanded" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/folding/Expanded">
    //        <default>true</default>
    //    </key>
    node_Expanded_boolean {
        @Override
        public String getUriSubFolder() {
            return "folding/";
        }
    },
    //        <key id="d1" for="node" attr.type="string" attr.name="description"/>
    node_description_string,
    //        <key id="d2" for="node" attr.name="NodeLabels" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/NodeLabels"/>
    node_NodeLabels,
    //        <key id="d3" for="node" attr.name="NodeGeometry" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/NodeGeometry"/>
    node_NodeGeometry,
    //        <key id="d4" for="all" attr.name="UserTags" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/UserTags"/>
    all_UserTags,
    //        <key id="d5" for="node" attr.name="NodeStyle" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/NodeStyle"/>
    node_NodeStyle,
    //    <key id="d6" for="node" attr.name="NodeViewState" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/folding/1.1/NodeViewState"/>
    node_NodeViewState {
        @Override
        public String getUriSubFolder() {
            return "folding/1.1/";
        }
    },
    edge_description_string,
    //        <key id="d7" for="edge" attr.name="EdgeLabels" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/EdgeLabels"/>
    edge_EdgeLabels,
    //        <key id="d8" for="edge" attr.name="EdgeGeometry" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/EdgeGeometry"/>
    edge_EdgeGeometry,
    //        <key id="d9" for="edge" attr.name="EdgeStyle" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/EdgeStyle"/>
    edge_EdgeStyle,
    //    <key id="d10" for="edge" attr.name="EdgeViewState" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/folding/1.1/EdgeViewState"/>
    edge_EdgeViewState {
        @Override
        public String getUriSubFolder() {
            return "folding/1.1/";
        }
    },
    //        <key id="d11" for="port" attr.name="PortLocationParameter" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/PortLocationParameter">
    //            <default>
    //                <x:Static Member="y:FreeNodePortLocationModel.NodeCenterAnchored"/>
    //            </default>
    //        </key>
    port_PortLocationParameter {
        @Override
        public String getStaticMember() {
            return "y:FreeNodePortLocationModel.NodeCenterAnchored";
        }
    },
    //        <key id="d12" for="port" attr.name="PortStyle" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/PortStyle">
    //            <default>
    //                <x:Static Member="y:VoidPortStyle.Instance"/>
    //            </default>
    //        </key>
    port_PortStyle {
        @Override
        public String getStaticMember() {
            return "y:VoidPortStyle.Instance";
        }
    },
    //    <key id="d13" for="port" attr.name="PortViewState" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/folding/1.1/PortViewState"/>
    port_PortViewState {
        @Override
        public String getUriSubFolder() {
            return "folding/1.1/";
        }
    },
    //        <key id="d14" attr.name="SharedData" y:attr.uri="http://www.yworks.com/xml/yfiles-common/2.0/SharedData"/>
    SharedData;

    public KeyAttributes keyAttributes() {
        StringTokenizer st = new StringTokenizer(name(), "_");
        int tokenCount = st.countTokens();
        KeyAttributes atts = new KeyAttributes();
        if (tokenCount > 1) {
            atts.attFor = st.nextToken();
        }
        atts.attName = st.nextToken();
        if (tokenCount > 2) {
            atts.attType = st.nextToken();
        }
        //die beiden selbstdefinierten Attribute heißen "description" und fangen als einzige mit einem Kleinbucstaben an -> keine URI
        if (Character.isUpperCase(atts.attName.charAt(0))) {
            atts.attUri = getUri(atts.attName, getUriSubFolder());
        }
        atts.staticMember = getStaticMember();
        return atts;
    }

    public String getDefaultValue() {
        return null;
    }

    public String getStaticMember() {
        return null;
    }

    public String getUriSubFolder() {
        return "";
    }

    private static final String getUri(final String uriLastPart, final String subFolder) {
        return "http://www.yworks.com/xml/yfiles-common/2.0/" + subFolder + uriLastPart;
    }

    public String getKeyID() {
        return "d" + ordinal();
    }

    public static class KeyAttributes {
        public String attFor;
        public String attName;
        public String attUri;
        public String attType;
        public String staticMember;
    }

}
