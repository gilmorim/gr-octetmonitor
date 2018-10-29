import org.snmp4j.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
*
*   objetos usados: ifIndex
*                   ifDescr
*                   ifPhysAddress
*                   ifInOctets
*                   ifOutOctets
*                   ifAdminStatus
*/
public class TestClass {
    public static void main(String args[]) throws IOException {

     ConfigFileParser configFile = new ConfigFileParser();
     int status = configFile.openConfigFile();
     System.out.println("Status: " + Vars.LOG_MESSAGES[status]);

     /*get data from config file*/
     String ipAddress = configFile.getIpAddress();
     String communityString = configFile.getCommunityString();
     int port = configFile.getPort();
     int version = configFile.getVersion();
     int timeout = configFile.getTimeout();
     int retries = configFile.getRetries();

     /*build community target*/
     CommunityTarget target = new CommunityTarget();
     target.setCommunity(new OctetString(communityString));
     target.setAddress(GenericAddress.parse("udp:"+ipAddress+"/"+port));
     target.setRetries(retries);
     target.setTimeout(timeout);

     String interfacesOID = ".1.3.6.1.2.1.2.2.1.2";
     String macAddressOID = "1.3.6.1.2.1.2.2.1.6";

     Map<String, String> interfacesResponse = getWalk(interfacesOID, target);
     Map<String, String> macAddressResponse = getWalk(macAddressOID, target);

     for(Map.Entry<String, String> entry : interfacesResponse.entrySet()){
         System.out.println(entry.getKey() + " " + entry.getValue());
     }

     for(Map.Entry<String, String> entry : macAddressResponse.entrySet()){
         System.out.println(entry.getKey() + " " + entry.getValue());
     }
    }

    public static Map<String, String> getWalk(String oid, Target target) throws IOException {
        Map<String, String> response = new TreeMap<>();
        TransportMapping<? extends Address> transport = new DefaultUdpTransportMapping();
        Snmp snmp = new Snmp(transport);
        transport.listen();

        TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
        List<TreeEvent> events = treeUtils.getSubtree(target, new OID(oid));

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