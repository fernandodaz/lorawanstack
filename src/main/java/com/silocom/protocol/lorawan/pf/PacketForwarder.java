
/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.protocol.lorawan.pf;

import com.silocom.m2m.layer.physical.Connection;
import com.silocom.m2m.layer.physical.MessageListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.silocom.lorawantest.LoraWanReceiver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PacketForwarder implements MessageListener {

    List<LoraWanReceiver> receivers = new ArrayList<>();

    Connection con;

    long offsetInMs = 6000000;
    private final byte[] gwIDExpected;
    private final JsonParser parser = new JsonParser();

    byte[] sendBuffer = new byte[0];

    public PacketForwarder(Connection con, byte[] gwIDExpected) {
        this.con = con;
        this.gwIDExpected = gwIDExpected;
    }

    public void addReceiver(LoraWanReceiver receiver) {

        receivers.add(receiver);
    }

    public void receiveMessage(byte[] message) {
        
        int packetType = message[3] & 0xFF;
        
        byte[] gwIDReceived = new byte[8];
        gwIDReceived[0] = message[4];
        gwIDReceived[1] = message[5];
        gwIDReceived[2] = message[6];
        gwIDReceived[3] = message[7];
        gwIDReceived[4] = message[8];
        gwIDReceived[5] = message[9];
        gwIDReceived[6] = message[10];
        gwIDReceived[7] = message[11];

        if (Arrays.equals(gwIDReceived, gwIDExpected)) {
            switch (packetType) {

                case 0:
                    int tokenPush = message[1] & 0xFF
                            | (message[2] & 0xFF) << 8;

                    pushAckPacket(tokenPush);

                    if (receivers.size() > 0) {
                        try {
                            byte[] mesgWithoutGarbage = new byte[message.length - 12];
                            System.arraycopy(message, 12, mesgWithoutGarbage, 0, mesgWithoutGarbage.length);
                            String jsonMessage = new String(mesgWithoutGarbage);

                            JsonObject gsonArr = parser.parse(jsonMessage).getAsJsonObject();
                            if (gsonArr.get("rxpk") != null) {
                                for (JsonElement obj : gsonArr.get("rxpk").getAsJsonArray()) {

                                    JsonObject gsonObj = obj.getAsJsonObject();

                                    String data = gsonObj.get("data").getAsString();
                                    int rfch = gsonObj.get("rfch").getAsInt();
                                    int size = gsonObj.get("size").getAsInt();
                                    String datr = gsonObj.get("datr").getAsString();
                                    String codr = gsonObj.get("codr").getAsString();
                                    String modu = gsonObj.get("modu").getAsString();
                                    String time = gsonObj.get("time").getAsString();
                                    long tmst = gsonObj.get("tmst").getAsLong();
                                    int rssi = gsonObj.get("rssi").getAsInt();
                                    double freq = 923.2;

                                    //Mensaje, data, Imme, Tmst, freq, rfch, pow,modu, datr, codr, ipol, size, ncrc
                                    receivers.forEach((receiver) -> {
                                        receiver.ReceiveMessage(message, data, false, tmst + offsetInMs, freq, rfch, 14, modu, datr, codr, true, size, true, rssi, time); //funcion que envia mensaje para ver de que tipo es 
                                    });
                                }
                            }

                        } catch (JsonSyntaxException e) {
                        }
                    }

                    break;

                case 1:

                    break;

                case 2:  //Mantiene la sesion udp activa con el gateway,

                    int tokenPull = message[1] & 0xFF
                            | (message[2] & 0xFF) << 8;

                    pullAckPacket(tokenPull);

                    if (sendBuffer.length > 0) {       //ventana de tiempo de respuesta después de recibir petición de ACK
                        con.sendMessage(sendBuffer);
                        sendBuffer = new byte[0];
                    }

                    break;

                case 3:

                    break;

                case 4:

                    break;

                case 5:

                    break;

            }
        }
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

        byte[] mesgToSend = new byte[4 + data.length];
        mesgToSend[0] = 0x02;
        mesgToSend[1] = token[0];
        mesgToSend[2] = token[1];
        mesgToSend[3] = 0x03;
        System.arraycopy(data, 0, mesgToSend, 4, data.length);
        sendBuffer = mesgToSend;

    }

    @Override
    public void receiveMessage(byte[] message, Connection con) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
