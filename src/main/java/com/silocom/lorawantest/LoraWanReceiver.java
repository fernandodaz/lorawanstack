
package com.silocom.lorawantest;

import com.google.gson.JsonParser;
import com.silocom.protocol.lorawan.pf.PacketForwarder;
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
                String string2 = new String(messageComplete);
                System.out.println("Uplink data: " + string2);

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
        devEUIReceived[0] = (byte) (decodeMessage[9] & 0xFF);
        devEUIReceived[1] = (byte) (decodeMessage[10] & 0xFF);
        devEUIReceived[2] = (byte) (decodeMessage[11] & 0xFF);
        devEUIReceived[3] = (byte) (decodeMessage[12] & 0xFF);
        devEUIReceived[4] = (byte) (decodeMessage[13] & 0xFF);
        devEUIReceived[5] = (byte) (decodeMessage[14] & 0xFF);
        devEUIReceived[6] = (byte) (decodeMessage[15] & 0xFF);
        devEUIReceived[7] = (byte) (decodeMessage[16] & 0xFF);

        if (Arrays.equals(devEUIReceived, devEUIExpected)) { //verificar si el mensaje es para mi
            int devNonce = (decodeMessage[17] & 0xFF)
                    | (decodeMessage[18] & 0xFF) << 8;

            int appNonce = rand.nextInt(0x100000) + 0xEFFFFF;

            appSKey = deriveAppSKey(appNonce, 0x010001, devNonce);
            nwSKey = deriveNwSKey(appNonce, 0x010001, devNonce);

            this.pForwarder.sendMessage(Sender.JoinAccept(appNonce, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc, appKey));

        } else {

            System.out.println(" No es para mi ");
        }

    }

    public void sensorDecoder(String message, int rssi, String time) {

        byte[] rawData = decodeMACPayload(message);

        if (rawData != null) {
        } else {
            return;
        }

        System.out.println(" Payload received: " + Utils.hexToString(rawData));

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

        System.out.println(" batVal : " + batVal);
        System.out.println(" batStat : " + batStat);
        System.out.println(" tempBuiltIn : " + tempBuiltIn);
        System.out.println(" Hum : " + Hum);
        System.out.println(" tempExt : " + tempExt);

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

    public byte[] deriveAppSKey(int AppNonce, int NetId, int DevNonce) {

        try {
            SecretKeySpec key = new SecretKeySpec(appKey, "AES");
            Cipher ciph = Cipher.getInstance("AES/ECB/NoPadding");
            ciph.init(Cipher.ENCRYPT_MODE, key);
            byte[] toKey = new byte[16];
            org.bouncycastle.util.Arrays.fill(toKey, (byte) 0);
            toKey[0] = 0x02;
            toKey[1] = (byte) (AppNonce & 0xff);
            toKey[2] = (byte) ((AppNonce >> 8) & 0xff);
            toKey[3] = (byte) ((AppNonce >> 16) & 0xff);
            toKey[4] = (byte) (NetId & 0xff);
            toKey[5] = (byte) ((NetId >> 8) & 0xff);
            toKey[6] = (byte) ((NetId >> 16) & 0xff);
            toKey[7] = (byte) (DevNonce & 0xff);
            toKey[8] = (byte) ((DevNonce >> 8) & 0xff);
            return ciph.update(toKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ignore) {
        }
        return null;
    }

    public byte[] deriveNwSKey(int AppNonce, int NetId, int DevNonce) {
        try {
            SecretKeySpec key = new SecretKeySpec(appKey, "AES");
            Cipher ciph = Cipher.getInstance("AES/ECB/NoPadding");
            ciph.init(Cipher.ENCRYPT_MODE, key);
            byte[] toKey = new byte[16];
            org.bouncycastle.util.Arrays.fill(toKey, (byte) 0);
            toKey[0] = 0x01;
            toKey[1] = (byte) (AppNonce & 0xff);
            toKey[2] = (byte) ((AppNonce >> 8) & 0xff);
            toKey[3] = (byte) ((AppNonce >> 16) & 0xff);
            toKey[4] = (byte) (NetId & 0xff);
            toKey[5] = (byte) ((NetId >> 8) & 0xff);
            toKey[6] = (byte) ((NetId >> 16) & 0xff);
            toKey[7] = (byte) (DevNonce & 0xff);
            toKey[8] = (byte) ((DevNonce >> 8) & 0xff);
            return ciph.update(toKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ignore) {
        }
        return null;
    }
}
