import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.smi.Address;
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

    public SNMPFunctions(){ }

    public Map<String, String> getWalk(String oid, CommunityTarget communityTarget) throws IOException {
        Map<String, String> response = new TreeMap<>();
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

            if(variableBindings == null || variableBindings.length == 0){
                continue;
            }

            for(VariableBinding variableBind : variableBindings){
                if(variableBind == null)
                    continue;

                response.put("." + variableBind.getOid().toString(), variableBind.getVariable().toString());

            }
        }
        snmp.close();
        return response;
    }

}
