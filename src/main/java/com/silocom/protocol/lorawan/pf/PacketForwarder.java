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

/**
 *
 * @author silocom01
 */
public class PacketForwarder implements MessageListener {

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

    public void receiveMessage(byte[] message) {

        String mesg = new String(message);

        int packetType = message[3] & 0xFF;

        switch (packetType) {

            case 0:

                break;

            case 1:

                break;

            case 2:

                break;

            case 3:

                break;

            case 4:

                break;

        }

        /*int version = message[0] & 0xFF;
    int token = message[1] & 0xFF
     | (message[2] & 0xFF) << 8;
    
    int
     
    long GwID = (message[4] & 0xFF)
                | (message[5] & (long) 0xFF) << 8
                | (message[6] & (long) 0xFF) << 16
                | (message[7] & (long) 0xFF) << 24
                | (message[8] & (long) 0xFF) << 32
                | (message[9] & (long) 0xFF) << 40;*/
        //System.out.print(" - GwID " + Long.toHexString(GwID));
        // System.out.print(" - Version " + Long.toHexString(version));
        //System.out.println(" - token " + Long.toHexString(token));
        System.out.println(" - packetType " + Long.toHexString(packetType));

    }

    public void pushDataPacket() {
    }

    public void pushAckPacket() {
    }

    public void pullDataPacket() {
    }

    public void pullAckPacket() {
    }

    @Override
    public void receiveMessage(byte[] message, Connection con) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
