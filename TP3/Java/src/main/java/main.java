import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.File;
import java.util.Scanner;
import static java.lang.System.out;
import java.util.*;
import static java.lang.System.*;
import static org.snmp4j.agent.mo.snmp.NotificationLogMib.NlmLogVariableValueTypeEnum.ipAddress;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class main {
    public static void main(String args[]) throws IOException {



        // ask for oid
        System.out.println("Enter your oid: ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] parts = input.split(Pattern.quote("."));
        System.out.println(Arrays.toString(parts));

        String a = parts[6];
        System.out.println(a);

        File file = new File("containership-conf.txt");

        /*build community target*/
        final CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(communityString));
        target.setAddress(GenericAddress.parse("udp:"+ipAddress+"/"+port));
        target.setRetries(retries);
        target.setTimeout(timeout);
        switch (a){
            case "1":
                funcaoparam(input);
                break;
            case "2":
                funcaoimageTable();
                break;
            case "3":
                funcaoContainerTable();
                break;
            case "4":
                funcaoContainerStatus();
                break;

        }
        /*for (String t : parts){
            String[] parts2 = t.split(".");
            System.out.println(parts2);

        }*/

    }

    public static void funcaoparam(String input){
        System.out.println("entrou na param");
        String[] parts = input.split(Pattern.quote("."));
        System.out.println(Arrays.toString(parts));
        String a = parts[7];
        System.out.println(a);
        switch (a){
            case "1":
                snmpgetindex(input);
                break;
            case "2":
                snmpgetimage();
                break;
            case "3":
                snmpgetflag();
                break;
            case "":
                snmpwalkparam();
                break;
        }

        //1 ler os
    }
    public static void snmpgetindex(String input) {
        System.out.println("snmpgetindex");
        final SNMPFunctions snmp = new SNMPFunctions();
        snmp.getNext(input);
    }


    }
    public static void snmpgetimage() {
        System.out.println("snmpgetimage");

    }
    public static void snmpgetflag() {
        System.out.println("snmpgetflag");

    }

    public static void snmpwalkparam() {
        System.out.println("snmpwalkparam");

    }
    public static void funcaoimageTable() {
        System.out.println("entrou na image Table ");

    }

    public static void funcaoContainerTable() {
        System.out.println("entrou na param");

    }
    public static void funcaoContainerStatus() {
        System.out.println("entrou na param");

    }
}
