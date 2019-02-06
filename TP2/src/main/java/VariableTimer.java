package main.java;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.snmp4j.CommunityTarget;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class VariableTimer extends TimerTask {
    Timer timer;

    InterfaceInformation interfaceInformation;
    SNMPResponseData inOctets;
    SNMPResponseData outOctets;
    SNMPFunctions snmpFunctions;
    CommunityTarget target;
    HttpClient http;

    public VariableTimer(Timer t, InterfaceInformation interfaceInformation, SNMPFunctions snmpFunctions, CommunityTarget target){
        timer = t;
        this.interfaceInformation = interfaceInformation;
        this.snmpFunctions = snmpFunctions;
        this.inOctets = new SNMPResponseData();
        this.outOctets = new SNMPResponseData();
        this.target = target;
        http = HttpClientBuilder.create().build();
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

        HttpPost request = new HttpPost("http://localhost:3000/stats");
        request.addHeader("content-type", "application/json");
        JSONObject json = new JSONObject();
        json.put("index", new StringBuilder().append(interfaceInformation.getIndex()).toString());
        json.put("macAddress", interfaceInformation.macAddress);
        json.put("description", interfaceInformation.getDescription());
        json.put("octetsIn", interfaceInformation.getIncomingOctets());
        json.put("octetsOut", interfaceInformation.getOutgoingOctets());
        json.put("difference", interfaceInformation.getDifference());
        json.put("rateChange", interfaceInformation.getPreviousDifference());
        json.put("timestamp", new StringBuilder().append(System.currentTimeMillis()).toString());

        try {
            request.setEntity(new StringEntity(json.toJSONString()));
            HttpResponse res = http.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        schedule();
    }
}
