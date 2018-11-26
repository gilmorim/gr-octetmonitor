import org.snmp4j.*;
import org.snmp4j.smi.*;

import java.io.IOException;
import java.util.*;


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

     final int delay = 1000;
     Timer timer = new Timer();

     /*get data from config file*/
     String ipAddress = configFile.getIpAddress();
     String communityString = configFile.getCommunityString();
     int port = configFile.getPort();
     int version = configFile.getVersion();
     int timeout = configFile.getTimeout();
     int retries = configFile.getRetries();

     /*build community target*/
     final CommunityTarget target = new CommunityTarget();

     target.setCommunity(new OctetString(communityString));
     target.setAddress(GenericAddress.parse("udp:"+ipAddress+"/"+port));
     target.setRetries(retries);
     target.setTimeout(timeout);

     final ArrayList<InterfaceInformation> interfaces = new ArrayList<>();
     final SNMPFunctions snmp = new SNMPFunctions();

     int interfaceCount = 0;
     Map<String, SNMPResponseData> interfaceCountResponse = snmp.getWalk(Vars.INTERFACE_COUNT_OID, target);

     if(interfaceCountResponse.size() != 0 && interfaceCountResponse != null) {

         // Get number of interfaces
         for (Map.Entry<String, SNMPResponseData> entry : interfaceCountResponse.entrySet())
             interfaceCount = Integer.parseInt(entry.getValue().getResponse());

         // Fill InterfaceInformation object
         for (int i = 0; i != interfaceCount; i++) {
             interfaces.add(new InterfaceInformation());
             interfaces.get(i).setInterval(1000);
         }
         System.out.println("interface count " + interfaceCount);

         for (int i = 0; i != Vars.OID_LIST.length; i++) {

             Map<String, SNMPResponseData> snmpResponse = snmp.getWalk(Vars.OID_LIST[i], target);

             SNMPResponseData[] values = snmpResponse.values().toArray(new SNMPResponseData[0]);
             String[] keys = snmpResponse.keySet().toArray(new String[0]);

             for (int j = 0; j != values.length; j++) {

                 OID oid = new OID(keys[j]);

                 int interfaceIndex = j;
                 int objectIndex = oid.get(9);
                 interfaces.get(interfaceIndex).setIncomingOctetsOid(Vars.INTERFACE_INCOMING_OCTETS_OID + "." + interfaceIndex);
                 interfaces.get(interfaceIndex).setOutgoingOctetsOid(Vars.INTERFACE_OUTGOING_OCTETS_OID + "." + interfaceIndex);
                 String value = values[j].getResponse();

                 switch (objectIndex) {
                     case Vars.INDEX: {
                         interfaces.get(interfaceIndex).setIndex(Integer.parseInt(value));
                         break;
                     }
                     case Vars.DESCRIPTION: {
                         interfaces.get(interfaceIndex).setDescription(value);
                         break;
                     }
                     case Vars.MACADDRESS: {
                         interfaces.get(interfaceIndex).setMacAddress(value);
                         break;
                     }
                     case Vars.STATUS: {
                         interfaces.get(interfaceIndex).setStatus(value);
                         break;
                     }
                     default: {
                         System.out.println("unknown index");
                         break;
                     }
                 }
             }
         }

         /*iniciar threads que recolhem info dos octets*/

         for(final InterfaceInformation interfaceInformation : interfaces) {
         timer.scheduleAtFixedRate(new TimerTask() {
             @Override
             public void run() {
                 try {
                     SNMPResponseData incomingOctetsResponse = snmp.getNext(interfaceInformation.getIncomingOctetsOid(), target);
                     interfaceInformation.setIncomingOctets(Integer.parseInt(incomingOctetsResponse.getResponse()));
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
                 try {
                     SNMPResponseData outgoingOctetsResponse = snmp.getNext(interfaceInformation.getOutgoingOctetsOid(), target);
                     interfaceInformation.setOutgoingOctets(Integer.parseInt(outgoingOctetsResponse.getResponse()));
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

                 System.out.println("interface " + interfaceInformation.getDescription() + ": " + interfaceInformation.getDifference());
             }
         }, 1000, interfaceInformation.getInterval());
         }
     }

     for(InterfaceInformation interfaceinfo : interfaces)
         System.out.println(interfaceinfo.toString());
    }
}

