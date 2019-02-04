package main.java;

public class InterfaceInformation {
    int index;
    String description;
    String macAddress;
    int incomingOctets = 0;
    int outgoingOctets = 0;
    String status;
    int interval;
    int previousDifference = 0;
    String incomingOctetsOid;
    String outgoingOctetsOid;

    public InterfaceInformation(){}

    public String getDescription() {
        return description;
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

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getPreviousDifference() { return previousDifference; }

    public void setPreviousDifference(int previousDifference){this.previousDifference = previousDifference;}

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

    public void increaseInterval(int increment){
        if(interval + increment > 15000){
            interval = 15000;
        } else {
            interval+=increment;
        }

    }

    public void decreaseInterval(int decrement){
        if(interval - decrement < 0){
            interval = 1000;
        } else {
            interval-=decrement;
        }

    }

    @Override
    public String toString() {
        return  "description = " + description + " \n" +
                "macAddress = " + macAddress + " \n" +
                "incomingOctets = " + incomingOctets + " \n" +
                "outgoingOctets = " + outgoingOctets + " \n" +
                "status = " + status + " \n" +
                "interval = " + interval/1000 + "s \n" +
                "difference = " + getDifference() + "\n" +
                "change in rate = " + Math.abs(getDifference() - previousDifference) + "\n\n";
    }
}
