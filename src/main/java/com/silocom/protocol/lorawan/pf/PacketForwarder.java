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
import com.silocom.lorawantest.Utils;
import java.util.Random;

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

    byte[] sendBuffer = new byte[0];

    public PacketForwarder(Connection con) {
        this.con = con;
    }

    public void setReceiver(LoraWanReceiver receiver) {
        this.receiver = receiver;
    }

    public void receiveMessage(byte[] message) {

        String mesg = new String(message);

        int packetType = message[3] & 0xFF;

        switch (packetType) {

            case 0:
                int tokenPush = message[1] & 0xFF
                        | (message[2] & 0xFF) << 8;

                pushAckPacket(tokenPush);

                if (receiver != null) {
                    try {
                        byte[] mesgWithoutGarbage = new byte[message.length - 12];
                        System.arraycopy(message, 12, mesgWithoutGarbage, 0, mesgWithoutGarbage.length);
                        String jsonMessage = new String(mesgWithoutGarbage);
                        //System.out.println(jsonMessage);

                        JsonObject gsonArr = parser.parse(jsonMessage).getAsJsonObject();
                        if (gsonArr.get("rxpk") != null) {
                            for (JsonElement obj : gsonArr.get("rxpk").getAsJsonArray()) {

                                // Object of array
                                JsonObject gsonObj = obj.getAsJsonObject();
                                // Primitives elements of object
                                data = gsonObj.get("data").getAsString();
                                rfch = gsonObj.get("rfch").getAsInt();
                                size = gsonObj.get("size").getAsInt();
                                datr = gsonObj.get("datr").getAsString();
                                codr = gsonObj.get("codr").getAsString();
                                modu = gsonObj.get("modu").getAsString();
                                tmst = gsonObj.get("tmst").getAsLong();
                                freq = gsonObj.get("freq").getAsFloat();

                            }
                            /* System.out.print(" data: " + data);
                            System.out.print(" rfch: " + rfch);
                            System.out.print(" datr: " + datr);
                            System.out.print(" codr: " + codr);
                            System.out.print(" modu: " + modu);
                            System.out.print(" tmst: " + tmst);
                            System.out.print(" size: " + size);*/
                            // 
                            receiver.ReceiveMessage(message, data, true, tmst, freq, rfch, 14, modu, datr, codr, true, size, true); //funcion que envia mensaje para ver de que tipo es 

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 1:

                break;

            case 2:  //Mantiene la sesion udp activa con el gateway,

               
                int tokenPull = message[1] & 0xFF
                        | (message[2] & 0xFF) << 8;

                pullAckPacket(tokenPull);
                if (sendBuffer.length > 0) {
                    con.sendMessage(sendBuffer);
                    sendBuffer = new byte[0];
                } 

                break;

            case 3:

                break;

            case 4:

                break;
            
            case 5:

                System.out.println(" MSG RECEIVED : " + Utils.hexToString(message));
                
                
                break;

        }

        System.out.println(" - packetType " + Long.toHexString(packetType));

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

    public void sendMessage(String JsonTxpk) {

        byte[] data = JsonTxpk.getBytes();
        byte[] token = new byte[2];
        new Random().nextBytes(token);

        //System.out.println( JsonTxpk);
        byte[] mesgToSend = new byte[4 + data.length];
        mesgToSend[0] = 0x02;
        mesgToSend[1] = token[0];
        mesgToSend[2] = token[1];
        mesgToSend[3] = 0x03;
        System.arraycopy(data, 0, mesgToSend, 4, data.length);
        //con.sendMessage(mesgToSend);
        sendBuffer = mesgToSend;
        String string = new String(mesgToSend);
        System.out.println(" Join accept: " + string);

    }

    @Override
    public void receiveMessage(byte[] message, Connection con) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
