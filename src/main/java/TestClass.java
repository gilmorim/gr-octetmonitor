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
     Map<String, String> interfaceCountResponse = snmp.getWalk(Vars.INTERFACE_COUNT_OID,target);

     for(Map.Entry<String, String> entry : interfaceCountResponse.entrySet())
         interfaceCount = Integer.parseInt(entry.getValue());

     for (int j = 0; j != interfaceCount; j++)
         interfaces.add(new InterfaceInformation());

     System.out.println("interface count " + interfaceCount);

     for(int i = 0; i != Vars.OID_LIST.length; i++){

         Map<String, String> snmpResponse = snmp.getWalk(Vars.OID_LIST[i], target);
         for(Map.Entry<String, String> entry : snmpResponse.entrySet()) {

                OID oid = new OID(entry.getKey());
                int interfaceIndex = oid.get(10) - 1;
                int objectIndex = oid.get(9);

                String value =  entry.getValue();

                switch (objectIndex){
                    case Vars.INDEX : {interfaces.get(interfaceIndex).setIndex(Integer.parseInt(value)); break;}
                    case Vars.DESCRIPTION : {interfaces.get(interfaceIndex).setDescription(value); break;}
                    case Vars.MACADDRESS : {interfaces.get(interfaceIndex).setMacAddress(value); break;}
                    case Vars.STATUS : {interfaces.get(interfaceIndex).setStatus(value); break;}
                    case Vars.INCOMING_OCTETS : {interfaces.get(interfaceIndex).setIncomingOctets(Integer.parseInt(value)); break;}
                    case Vars.OUTGOING_OCTETS : {interfaces.get(interfaceIndex).setOutgoingOctets(Integer.parseInt(value)); break;}
                    default: { System.out.println("unknown index"); break;}
                }
             }
     }

     for(InterfaceInformation interfaceinfo : interfaces)
         System.out.println(interfaceinfo.toString());
    }
}

