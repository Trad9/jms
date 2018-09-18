package kn.swiftlog.tst.jms.configuration;

public enum XpathConfig {

    CLIENT_ID("CLIENT_ID","//CLIENT_ID/text()"),
    WH_ID("WH_ID","//WH_ID/text()"),
    REQUEST_ID("REQUEST_ID","//REQUEST_ID/text()");

    private String nodeName;
    private String pathToNode;

    private XpathConfig(String nodeName, String pathToNode){
        this.nodeName = nodeName;
        this.pathToNode = pathToNode;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getPathToNode(){
        return pathToNode;
    }
}
