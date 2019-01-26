import org.snmp4j.smi.OID;

public class InterfaceInformation {
    int index;
    String description;
    String macAddress;
    int incomingOctets;
    int outgoingOctets;
    String status;
    int interval;
    String incomingOctetsOid;
    String outgoingOctetsOid;

    public InterfaceInformation(){}


    public int getIndex() {
        return index;
    }

    public String getDescription() {
        return description;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getIncomingOctets() {
        return incomingOctets;
    }

    public int getOutgoingOctets() {
        return outgoingOctets;
    }

    public String isStatus() {
        return status;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setIncomingOctets(int incomingOctets) {
        this.incomingOctets = incomingOctets;
    }

    public void setOutgoingOctets(int outgoingOctets) {
        this.outgoingOctets = outgoingOctets;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getDifference() {
        return incomingOctets - outgoingOctets;
    }

    public int getInterval() { return interval; }

    public void setInterval(int delay) { this.interval = delay; }

    public String getStatus() {
        return status;
    }

    public String getIncomingOctetsOid() {
        return incomingOctetsOid;
    }

    public String getOutgoingOctetsOid() {
        return outgoingOctetsOid;
    }

    public void setIncomingOctetsOid(String incomingOctetsOid) {
        this.incomingOctetsOid = incomingOctetsOid;
    }

    public void setOutgoingOctetsOid(String outgoingOctetsOid) {
        this.outgoingOctetsOid = outgoingOctetsOid;
    }

    @Override
    public String toString() {
        return "Interface data: " +
                "index=" + index +
                ", description='" + description + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", incomingOctets=" + incomingOctets +
                ", outgoingOctets=" + outgoingOctets +
                ", status='" + status + '\'' +
                ", incoming octets OID ='" + incomingOctetsOid + '\'' +
                ", outgoing octets OID ='" + outgoingOctetsOid + '\'' +
                ", difference='" + getDifference() + '\'';
    }
}
