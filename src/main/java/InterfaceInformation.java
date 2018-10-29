public class InterfaceInformation {
    int index;
    String description;
    String macAddress;
    int incomingOctets;
    int outgoingOctets;
    boolean status;

    public InterfaceInformation(){}

    public InterfaceInformation(int index, String description, String macAddress, int incomingOctets, int outgoingOctets, boolean status) {
        this.index = index;
        this.description = description;
        this.macAddress = macAddress;
        this.incomingOctets = incomingOctets;
        this.outgoingOctets = outgoingOctets;
        this.status = status;
    }


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

    public boolean isStatus() {
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

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getDifference() {
        return incomingOctets - outgoingOctets;
    }
}
