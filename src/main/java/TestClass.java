import org.snmp4j.*;
import org.snmp4j.smi.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

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

     ArrayList<InterfaceInformation> interfaces = new ArrayList<>();
     SNMPFunctions snmp = new SNMPFunctions();

     int interfaceCount = 0;
     Map<String, String> interfaceCountResponse = snmp.getWalk(Vars.INTERFACE_COUNT,target);

     for(Map.Entry<String, String> entry : interfaceCountResponse.entrySet())
         interfaceCount = Integer.parseInt(entry.getValue());

        System.out.println("interface count " + interfaceCount);

     for(int i = 0; i != Vars.OID_LIST.length; i++){

         Map<String, String> snmpResponse = snmp.getWalk(Vars.OID_LIST[i], target);

         for (int j = 0; j != 3; j++)
                 interfaces.add(new InterfaceInformation());

         for(Map.Entry<String, String> entry : snmpResponse.entrySet()) {

                String objectID = entry.getKey();
                int objectNumber = Integer.parseInt(entry.getKey().substring(objectID.length() - 1));
                System.out.println(entry.getKey() + " " + entry.getValue());

             }
     }
    }
}