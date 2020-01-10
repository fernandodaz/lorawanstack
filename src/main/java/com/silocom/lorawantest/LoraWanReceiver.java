package com.silocom.lorawantest;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.silocom.m2m.layer.physical.Connection;
import com.silocom.m2m.layer.physical.MessageListener;
import com.silocom.protocol.lorawan.pf.PacketForwarder;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private final Cipher cipher;

    private final Random rand = new Random();
    private final JsonParser parser = new JsonParser();

    public LoraWanReceiver(byte[] nwSKey, byte[] appSKey, byte[] appKey, PacketForwarder pf) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        this.nwSKey = nwSKey;
        this.appSKey = appSKey;
        this.appKey = appKey;
        this.pForwarder = pf;
        this.jsonCons = new JsonConstructor();
        this.Sender = new PayloadConstructor(jsonCons);

    }

    public void ReceiveMessage(byte[] messageComplete, String message, boolean imme, long tmst, float freq, int rfch, int powe,
            String modu, String datr, String codr, boolean ipol, int size, boolean ncrc) {

        byte[] decodeMessage = Base64.decodeBase64(message);
        int mType = decodeMessage[0] & 0xFF;

        switch (mType) {

            case joinRequest:

                String string = new String(messageComplete);
                System.out.println(" Join Request: " + string);

                decodeJoinRequest(message, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc, appKey);
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
                sensorDecoder(message);
                String string2 = new String(messageComplete);
                System.out.println("Data up: " + string2);

        }

    }

    public void decodeJoinRequest(String message, boolean imme, long tmst, float freq, int rfch, int powe,
            String modu, String datr, String codr, boolean ipol, int size, boolean ncrc, byte[] appKey) {

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

        long devAddr = (decodeMessage[9] & 0xFF)
                | (decodeMessage[10] & (long) 0xFF) << 8
                | (decodeMessage[11] & (long) 0xFF) << 16
                | (decodeMessage[12] & (long) 0xFF) << 24
                | (decodeMessage[13] & (long) 0xFF) << 32
                | (decodeMessage[14] & (long) 0xFF) << 40
                | (decodeMessage[15] & (long) 0xFF) << 48
                | (decodeMessage[16] & (long) 0xFF) << 56;

        int devNonce = (decodeMessage[17] & 0xFF)
                | (decodeMessage[18] & 0xFF) << 8;

        int appNonce = rand.nextInt(0x100000) + 0xEFFFFF;

        /* System.out.println(" appnonce: " + Integer.toHexString(appNonce));
        System.out.println(" devNonce: " + Integer.toHexString(devNonce));*/
        appSKey = deriveAppSKey(appNonce, 0x010001, devNonce);
        nwSKey = deriveNwSKey(appNonce, 0x010001, devNonce);

        /* System.out.println(" appSkey : " + Utils.hexToString(appSKey));
        System.out.println(" nwSkey : " + Utils.hexToString(nwSKey));*/
        this.pForwarder.sendMessage(Sender.JoinAccept(appNonce, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc, appKey));

    }

    public void sensorDecoder(String message) {

        byte[] rawData = new byte[11];

        rawData = decodeMACPayload(message);

        System.out.println(" rawdata: " + Utils.hexToString(rawData));
        
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
    }

    public byte[] decodeMACPayload(String message) {
        byte[] decodeMessage = Base64.decodeBase64(message);
        // System.out.println("Message Decoded: " + Utils.hexToString(decodeMessage));
        int mType = decodeMessage[0] & 0xFF;
        int devAddress = (decodeMessage[1] & 0xff)
                | (decodeMessage[2] & 0xff) << 8
                | (decodeMessage[3] & 0xff) << 16
                | (decodeMessage[4] & 0xff) << 24;
        int fCtrl = decodeMessage[5] & 0xFF;
        int fCount = ((decodeMessage[7] & 0xff) << 8 | (decodeMessage[6] & 0xff));

        byte[] payload = new byte[decodeMessage.length - 9];
        System.arraycopy(decodeMessage, 9, payload, 0, decodeMessage.length - 9);
        return decryptPayload(payload, devAddress, fCount, (byte) 0);
    }

    public byte[] decryptPayload(byte[] payload, int devAddress, int fCount, byte dir) {
        try {
            byte[] ivKey = new byte[16];
            Arrays.fill(ivKey, (byte) 0);
            ivKey[0] = 1;
            ivKey[15] = 1;

            ivKey[5] = dir;
            ivKey[6] = (byte) ((devAddress) & 0xFF);
            ivKey[7] = (byte) ((devAddress >> 8) & 0xFF);
            ivKey[8] = (byte) ((devAddress >> 16) & 0xFF);
            ivKey[9] = (byte) ((devAddress >> 24) & 0xFF);

            ivKey[10] = (byte) ((fCount) & 0xFF);
            ivKey[11] = (byte) ((fCount >> 8) & 0xFF);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivKey);

            System.out.println(" APPSKEY : " + Utils.hexToString(appSKey));
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

    /* @Override
    public void receiveMessage(byte[] message, Connection con) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }*/
}
