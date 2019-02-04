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
        //interfaceInformation.setInterval((int)Math.round(Math.random()*15000)+5000);
        adjustInterval();
        System.out.println("difference: " + interfaceInformation.getDifference() + "\nprevious: " + interfaceInformation.getPreviousDifference());
        System.out.println("current polling interval: " + interfaceInformation.getInterval()/1000 + "s\n");
        timer.schedule(new VariableTimer(timer, interfaceInformation, snmpFunctions, target), interfaceInformation.getInterval());
    }

    public void adjustInterval(){
        int currentDifference = Math.abs(interfaceInformation.getDifference());
        int rateChange = Math.abs(currentDifference - Math.abs(interfaceInformation.getPreviousDifference()));
        if(rateChange < 300){
            interfaceInformation.increaseInterval(1000);
            System.out.println("small rate change of " + rateChange + ". Increasing poll interval by 1s");
        } else {
            interfaceInformation.decreaseInterval(5000);
            System.out.println("big rate change of " + rateChange + ". Decreasing poll interval by 2s");
        }
    }

    public void run(){
        interfaceInformation.setPreviousDifference(interfaceInformation.getDifference());
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
        System.out.println("interface " + interfaceInformation.getDescription() + ": " + interfaceInformation.getDifference());
        schedule();
    }
}
