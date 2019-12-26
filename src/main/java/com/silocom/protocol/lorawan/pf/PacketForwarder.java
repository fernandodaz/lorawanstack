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
    int tipo = message[0] & 0xFF;
    long GwID = (message[4] & 0xFF)
                | (message[5] & (long) 0xFF) << 8
                | (message[6] & (long) 0xFF) << 16
                | (message[7] & (long) 0xFF) << 24
                | (message[8] & (long) 0xFF) << 32
                | (message[9] & (long) 0xFF) << 40;

    
    System.out.println(" GwID " + Long.toHexString(GwID));
       
    }

    @Override
    public void receiveMessage(byte[] message, Connection con) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
