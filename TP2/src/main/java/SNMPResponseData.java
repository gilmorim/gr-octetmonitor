package main.java;

import org.snmp4j.PDU;

public class SNMPResponseData {
    public String response;
    public PDU pdu;

    public SNMPResponseData() {

    }

    public String getResponse() {
        return response;
    }

    public PDU getPdu() {
        return pdu;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setPdu(PDU pdu) {
        this.pdu = pdu;
    }
}
