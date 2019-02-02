package main.java;

import org.snmp4j.CommunityTarget;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class VariableTimer extends TimerTask {
    Timer timer;

    InterfaceInformation interfaceInformation;
    SNMPResponseData inOctets;
    SNMPResponseData outOctets;
    SNMPFunctions snmpFunctions;
    CommunityTarget target;

    public VariableTimer(Timer t, InterfaceInformation interfaceInformation, SNMPFunctions snmpFunctions, CommunityTarget target){
        timer = t;
        this.interfaceInformation = interfaceInformation;
        this.snmpFunctions = snmpFunctions;
        this.inOctets = new SNMPResponseData();
        this.outOctets = new SNMPResponseData();
        this.target = target;
    }
    public void schedule(){
        interfaceInformation.setInterval((int)Math.round(Math.random()*15000)+5000);
        System.out.println(interfaceInformation.getInterval()/1000 + "s");
        timer.schedule(new VariableTimer(timer, interfaceInformation, snmpFunctions, target), interfaceInformation.getInterval());
    }
    public void run(){
        try {
            SNMPResponseData incomingOctetsResponse = snmpFunctions.getNext(interfaceInformation.getIncomingOctetsOid(), target);
            interfaceInformation.setIncomingOctets(Integer.parseInt(incomingOctetsResponse.getResponse()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            SNMPResponseData outgoingOctetsResponse = snmpFunctions.getNext(interfaceInformation.getOutgoingOctetsOid(), target);
            interfaceInformation.setOutgoingOctets(Integer.parseInt(outgoingOctetsResponse.getResponse()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        interfaceInformation.setPreviousDifference(interfaceInformation.getDifference());
        System.out.println("interface " + interfaceInformation.getDescription() + ": " + interfaceInformation.getDifference());
        schedule();
    }
}
