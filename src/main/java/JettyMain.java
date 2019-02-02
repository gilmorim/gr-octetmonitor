import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.snmp4j.CommunityTarget;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class JettyMain {
    public static void main(String[] args) throws Exception {
        Server server = new Server(7070);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");

        ConfigFileParser configFile = new ConfigFileParser();
        int status = configFile.openConfigFile();
        System.out.println("Status: " + Vars.LOG_MESSAGES[status]);

        final int delay = 1000;

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
                if(i%2 == 0){
                    interfaces.get(i).setInterval(1000);
                } else {
                    interfaces.get(i).setInterval(5000);
                }
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


            for(final InterfaceInformation interfaceInformation : interfaces) {

                Timer timer = new Timer();
                VariableTimer vt  = new VariableTimer(timer, interfaceInformation, snmp, target);

                Thread t = new Thread(vt);
                t.start();
            }
        }

        InterfacesDataServlet interfacesServlet = new InterfacesDataServlet(interfaces);
        ServletHolder helloHolder = new ServletHolder(interfacesServlet);
        context.addServlet(helloHolder, "/monitor/*");

        server.setHandler(context);
        server.start();
    }
}