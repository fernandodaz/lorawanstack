package com.silocom.lorawantest;

import com.google.gson.JsonParser;
import com.silocom.protocol.lorawan.pf.PacketForwarder;
import static java.lang.Math.random;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.util.Random;

/**
 *
 * @author hvarona
 */
public class LoraWanReceiver {

    String data = null;
    int rssi = 0;
    int rfch = 0;
    int size = 0;
    long tmst = 0;
    float freq = 0;
    String datr = null;
    String codr = null;
    String modu = null;

    String utfString;
    PayloadConstructor Sender;
    JsonConstructor jsonCons;
    PacketForwarder pForwarder;

    final int joinRequest = 0x00;      //Secuencia dada por el documento de LoRaWAN Alliance
    final int joinAccept = 0x01;
    final int unconfirmedDataUp = 0x02;
    final int unconfirmedDataDown = 0x03;
    final int confirmedDataUp = 0x04;
    final int confirmedDataDown = 0x05;
    final int RFU = 0x06;
    final int propietary = 0x07;

    private byte[] nwSKey;
    private byte[] appSKey;
    private final byte[] appKey;
    private final byte[] netID;

    private final byte[] appEUI;
    private final byte[] devEUIExpected;
    private final byte[] devAddrExpected;

    private final Cipher cipher;
    private final SensorListener listener;
    private final Random rand = new Random();
    private final JsonParser parser = new JsonParser();

    public LoraWanReceiver(byte[] nwSKey, byte[] appSKey, byte[] appKey, byte[] netID, byte[] appEUI, byte[] devEUIExpected,
            byte[] devAddrExpected, PacketForwarder pf,
            SensorListener listener) throws NoSuchAlgorithmException, NoSuchPaddingException {

        this.cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        this.listener = listener;
        this.nwSKey = nwSKey;
        this.appSKey = appSKey;
        this.appKey = appKey;
        this.netID = netID;

        this.appEUI = appEUI;
        this.devEUIExpected = devEUIExpected;
        this.devAddrExpected = devAddrExpected;

        this.pForwarder = pf;
        this.jsonCons = new JsonConstructor();
        this.Sender = new PayloadConstructor(jsonCons);

    }

    public void ReceiveMessage(byte[] messageComplete, String message, boolean imme, long tmst, float freq, int rfch, int powe,
            String modu, String datr, String codr, boolean ipol, int size, boolean ncrc, int rssi, String time) {

        byte[] decodeMessage = Base64.decodeBase64(message);
        int mType = decodeMessage[0] & 0xFF;

        switch (mType) {

            case joinRequest:

                String string = new String(messageComplete);
                System.out.println(" Join Request: " + string);

                decodeJoinRequest(message, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc, appKey, devAddrExpected);
                break;

            case joinAccept:
                //ERROR - SERVER SIDE CANNOT RECEIVE JOIN ACCEPT 

                break;

            case unconfirmedDataUp:
                //TODO

                break;

            case unconfirmedDataDown:
                //TODO

                break;

            case confirmedDataUp:

                break;

            case confirmedDataDown:
                //TODO

                break;

            case RFU:
                //TODO

                break;

            case propietary:
                //TODO
                break;

            default:
                sensorDecoder(message, rssi, time);

        }

    }

    public void decodeJoinRequest(String message, boolean imme, long tmst, float freq, int rfch, int powe,
            String modu, String datr, String codr, boolean ipol, int size, boolean ncrc, byte[] appKey, byte[] devAddrExpected) {

        byte[] decodeMessage = Base64.decodeBase64(message);
        int mType = (decodeMessage[0] & 0xE0) << 5;

        long appEUI = (decodeMessage[1] & 0xFF)
                | (decodeMessage[2] & (long) 0xFF) << 8
                | (decodeMessage[3] & (long) 0xFF) << 16
                | (decodeMessage[4] & (long) 0xFF) << 24
                | (decodeMessage[5] & (long) 0xFF) << 32
                | (decodeMessage[6] & (long) 0xFF) << 40
                | (decodeMessage[7] & (long) 0xFF) << 48
                | (decodeMessage[8] & (long) 0xFF) << 56;

        byte[] devEUIReceived = new byte[8];
        devEUIReceived[0] = decodeMessage[16];
        devEUIReceived[1] = decodeMessage[15];
        devEUIReceived[2] = decodeMessage[14];
        devEUIReceived[3] = decodeMessage[13];
        devEUIReceived[4] = decodeMessage[12];
        devEUIReceived[5] = decodeMessage[11];
        devEUIReceived[6] = decodeMessage[10];
        devEUIReceived[7] = decodeMessage[9];

        if (Arrays.equals(devEUIReceived, devEUIExpected)) { //verificar si el mensaje es para mi
            
            byte[] devNonce = new byte[2];
            devNonce[0] = decodeMessage[17];   //pasar a byte
            devNonce[1] = decodeMessage[18];
            
            byte[] appNonce = new byte[3];
            new Random().nextBytes(appNonce); 
              
            appSKey = deriveAppSKey(appNonce, netID, devNonce);
            nwSKey = deriveNwSKey(appNonce, netID, devNonce);

            this.pForwarder.sendMessage(Sender.JoinAccept(appNonce, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc, appKey, netID, devAddrExpected));

        } else {
            System.out.println(" no es el devEUI esperado ");
        }

    }

    public void sensorDecoder(String message, int rssi, String time) {

        byte[] rawData = decodeMACPayload(message);

        if (rawData != null) {
        } else {
            return;
        }


        int batVal = ((rawData[0] & 0x3F) << 8) | (rawData[1] & 0xFF);
        int batStat = ((((rawData[0] & 0xFF) << 8) | (rawData[1] & 0xFF)) >> 14) & 0xFF;
        int tempBuiltInVal = (((rawData[2] & 0xFF) << 8) | (rawData[3] & 0xFF));

        if ((rawData[2] & 0x80) > 0) {
            tempBuiltInVal |= 0xFFFF0000;
        }
        int tempBuiltIn = tempBuiltInVal;
        int Hum = (((rawData[4] & 0xFF) << 8) | (rawData[5] & 0xFF));

        int tempExtVal = (((rawData[7] & 0xFF) << 8) | (rawData[8] & 0xFF));
        if ((rawData[7] & 0x80) > 0) {
            tempExtVal |= 0xFFFF0000;
        }
        int tempExt = tempExtVal; //DS18B20,

        Sensor sensor = new Sensor(batVal, batStat, tempBuiltIn, Hum, tempExt, rssi, time);
        listener.onData(sensor);
    }

    public byte[] decodeMACPayload(String message) {
        byte[] decodeMessage = Base64.decodeBase64(message);
        int mType = decodeMessage[0] & 0xFF;
        byte[] devAddrReceived = new byte[4];

        devAddrReceived[0] = (byte) (decodeMessage[1] & 0xff);
        devAddrReceived[1] = (byte) (decodeMessage[2] & 0xff);
        devAddrReceived[2] = (byte) (decodeMessage[3] & 0xff);
        devAddrReceived[3] = (byte) (decodeMessage[4] & 0xff);

        int fCtrl = decodeMessage[5] & 0xFF;
        int fCount = ((decodeMessage[7] & 0xff) << 8 | (decodeMessage[6] & 0xff));

        if (Arrays.equals(devAddrReceived, devAddrExpected)) {//comparar devAddr

            byte[] payload = new byte[decodeMessage.length - 9];
            System.arraycopy(decodeMessage, 9, payload, 0, decodeMessage.length - 9);
            return decryptPayload(payload, devAddrExpected, fCount, (byte) 0);
        } else {
            System.out.println(" no es el devAddr esperado ");
        }
        return null;
    } //add
    //comm
    //ann

    public byte[] decryptPayload(byte[] payload, byte[] devAddress, int fCount, byte dir) {
        try {
            byte[] ivKey = new byte[16];
            Arrays.fill(ivKey, (byte) 0);
            ivKey[0] = 1;
            ivKey[15] = 1;

            ivKey[5] = dir;
            ivKey[6] = devAddress[0];
            ivKey[7] = devAddress[1];
            ivKey[8] = devAddress[2];
            ivKey[9] = devAddress[3];

            ivKey[10] = (byte) ((fCount) & 0xFF);
            ivKey[11] = (byte) ((fCount >> 8) & 0xFF);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivKey);

            SecretKeySpec secretKeySpec = new SecretKeySpec(appSKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            return cipher.doFinal(payload);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
        }
        return null;
    }

    public byte[] deriveAppSKey(byte[] AppNonce, byte[] NetId, byte[] DevNonce) {

        try {
            SecretKeySpec key = new SecretKeySpec(appKey, "AES");
            Cipher ciph = Cipher.getInstance("AES/ECB/NoPadding");
            ciph.init(Cipher.ENCRYPT_MODE, key);
            byte[] toKey = new byte[16];
            org.bouncycastle.util.Arrays.fill(toKey, (byte) 0);
            toKey[0] = 0x02;
            toKey[1] = AppNonce[0];
            toKey[2] = AppNonce[1];
            toKey[3] = AppNonce[2];
            toKey[4] = NetId[0];
            toKey[5] = NetId[1];
            toKey[6] = NetId[2];
            toKey[7] = DevNonce[0];
            toKey[8] = DevNonce[1];
            return ciph.update(toKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ignore) {
        }
        return null;
    }

    public byte[] deriveNwSKey(byte[] AppNonce, byte[] NetId, byte[] DevNonce) {
        try {
            SecretKeySpec key = new SecretKeySpec(appKey, "AES");
            Cipher ciph = Cipher.getInstance("AES/ECB/NoPadding");
            ciph.init(Cipher.ENCRYPT_MODE, key);
            byte[] toKey = new byte[16];
            org.bouncycastle.util.Arrays.fill(toKey, (byte) 0);
            toKey[0] = 0x01;
            toKey[1] = AppNonce[0];
            toKey[2] = AppNonce[1];
            toKey[3] = AppNonce[2];
            toKey[4] = NetId[0];
            toKey[5] = NetId[1];
            toKey[6] = NetId[2];
            toKey[7] = DevNonce[0];
            toKey[8] = DevNonce[1];
            return ciph.update(toKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ignore) {
        }
        return null;
    }
}
