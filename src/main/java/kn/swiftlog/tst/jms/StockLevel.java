package kn.swiftlog.tst.jms;

public class StockLevel {
    private String clientId;
    private String warehouseId;
    private String requestId;

    public StockLevel(String clientId, String warehouseId, String requestId) {
        this.clientId = clientId;
        this.warehouseId = warehouseId;
        this.requestId = requestId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    @Override
    public String toString(){
        return "Client ID: " + clientId + " Warehouse ID: " + warehouseId + " RequsetID: " +requestId;
    }
}
