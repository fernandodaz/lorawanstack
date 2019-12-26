/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.protocol.lorawan.pf;

import com.silocom.m2m.layer.physical.Connection;
import com.silocom.m2m.layer.physical.MessageListener;
import java.util.Arrays;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.silocom.lorawantest.LoraWanReceiver;


/**
 *
 * @author silocom01
 */
public class PacketForwarder implements MessageListener {

    private LoraWanReceiver receiver;
    
    Connection con;

    String data = null;
    int rssi = 0;
    int rfch = 0;
    int size = 0;
    long tmst = 0;
    float freq = 0;
    String datr = null;
    String codr = null;
    String modu = null;
    private final JsonParser parser = new JsonParser();

    public PacketForwarder(Connection con) {
        this.con = con;
    }

    public void receiveMessage(byte[] message) {

        String mesg = new String(message);
        

        int packetType = message[3] & 0xFF;

        switch (packetType) {

            case 0:
                int tokenPush = message[1] & 0xFF
                        | (message[2] & 0xFF) << 8;
                
                pushAckPacket(tokenPush);
                
                if (receiver != null){
                  receiveMessage(message);
                }
              
                
                break;

            case 1:

                break;

            case 2:  //Mantiene la sesion udp activa con el gateway,

                int tokenPull = message[1] & 0xFF
                        | (message[2] & 0xFF) << 8;

                pullAckPacket(tokenPull);

                
                break;

            case 3:

                break;

            case 4:

                break;

        }

        
        System.out.println(" - packetType " + Long.toHexString(packetType));

    }

    public void setReceiver(LoraWanReceiver receiver) {
        this.receiver = receiver;
    }

    public void pushDataPacket() {
    }

    public void pushAckPacket(int tokenPush) {
        byte[] mesgToSend = new byte[4];
        mesgToSend[0] = 0x02;
        mesgToSend[1] = (byte) (tokenPush & 0xFF);
        mesgToSend[2] = (byte) ((tokenPush >> 8) & 0xFF);
        mesgToSend[3] = 0x01;

        con.sendMessage(mesgToSend);
        
        
    }

    public void pullDataPacket() {
    }

    public void pullAckPacket(int tokenPull) {

        byte[] mesgToSend = new byte[4];

        mesgToSend[0] = 0x02;
        mesgToSend[1] = (byte) (tokenPull & 0xFF);
        mesgToSend[2] = (byte) ((tokenPull >> 8) & 0xFF);
        mesgToSend[3] = 0x04;

        con.sendMessage(mesgToSend);
    }

    public void pullRespPacket() {
    }

    @Override
    public void receiveMessage(byte[] message, Connection con) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
