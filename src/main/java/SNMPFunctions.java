import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SNMPFunctions {

    public SNMPFunctions(){
    }

    public Map<String, SNMPResponseData> getWalk(String oid, CommunityTarget communityTarget) throws IOException {
        Map<String, SNMPResponseData> response = new TreeMap<>();
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(communityTarget, new OID(oid));

        if(events == null || events.size() == 0){
            System.out.println("Could not retrieve data...");
            return response;
        }

        for (TreeEvent event : events){
            if(event == null){
                continue;
            }

            if(event.isError()){
                System.out.println("error on OID " + oid + ": " + event.getErrorMessage());
            }

            VariableBinding[] variableBindings = event.getVariableBindings();
            SNMPResponseData data = new SNMPResponseData();

            if(variableBindings == null || variableBindings.length == 0){
                continue;
            }

            for(VariableBinding variableBind : variableBindings){
                if(variableBind == null)
                    continue;

                data.setResponse(variableBind.getVariable().toString());
                response.put("." + variableBind.getOid().toString(), data);

            }
        }
        snmp.close();
        return response;
    }

    public SNMPResponseData getNext(String oid, CommunityTarget target) throws IOException {

        SNMPResponseData snmpResponse = new SNMPResponseData();
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        transport.listen();

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oid)));
        pdu.setRequestID(new Integer32(1));
        pdu.setType(PDU.GETNEXT);

        Snmp snmp = new Snmp(transport);

        ResponseEvent response = snmp.getNext(pdu, target);

        if(response != null){
            PDU responsePDU = response.getResponse();

            if(responsePDU != null){
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if(errorStatus == PDU.noError){
                    snmpResponse.setPdu(responsePDU);
                    snmpResponse.setResponse(responsePDU.getVariableBindings().get(0).getVariable().toString());
                } else {
                    System.out.println("Error: Request failed");
                    System.out.println("Error status " + errorStatus);
                    System.out.println("Error index " + errorIndex);
                    System.out.println("Error status text " + errorStatusText);
                }
            } else {
                System.out.println("Error: response is null");
            }
        } else {
            System.out.println("Agent timeout");
        }

        snmp.close();
        return snmpResponse;
    }

}
