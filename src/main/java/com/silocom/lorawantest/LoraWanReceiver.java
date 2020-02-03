package com.silocom.lorawantest;

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
    private final byte[] devEUI_Expected;
    private final byte[] devAddr_Expected;

    private final Cipher cipher;
    private final SensorListener listener;

    public LoraWanReceiver(byte[] nwSKey, byte[] appSKey, byte[] appKey, byte[] netID, byte[] appEUI, byte[] devEUI_Expected,
            byte[] devAddr_Expected, PacketForwarder pf,
            SensorListener listener) throws NoSuchAlgorithmException, NoSuchPaddingException {

        this.cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        this.listener = listener;
        this.nwSKey = nwSKey;
        this.appSKey = appSKey;
        this.appKey = appKey;
        this.netID = netID;

        this.appEUI = appEUI;
        this.devEUI_Expected = devEUI_Expected;
        this.devAddr_Expected = devAddr_Expected;

        this.pForwarder = pf;
        this.jsonCons = new JsonConstructor();
        this.Sender = new PayloadConstructor(jsonCons);

    }

    public void ReceiveMessage(byte[] messageComplete, String message, boolean imme, long tmst, double freq, int rfch, int powe,
            String modu, String datr, String codr, boolean ipol, int size, boolean ncrc, int rssi, String time) {

        byte[] decodeMessage = Base64.decodeBase64(message);
        int mType = decodeMessage[0] & 0xFF;
        switch (mType) {

            case joinRequest:

                decodeJoinRequest(message, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc, appKey, devAddr_Expected);
               
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
                //Reserved for future uses

                break;

            case propietary:
                //TODO
                break;

            default:
               
                sensorDecoder(message, rssi, time);
              
        }

    }

    public void decodeJoinRequest(String message, boolean imme, long tmst, double freq, int rfch, int powe,
            String modu, String datr, String codr, boolean ipol, int size, boolean ncrc, byte[] appKey, byte[] devAddr_Expected) {

        byte[] decodeMessage = Base64.decodeBase64(message);
        
        //Not in use yet
        byte[] appEUI_Received = new byte[8];
        appEUI_Received[0] = decodeMessage[1];
        appEUI_Received[1] = decodeMessage[2];
        appEUI_Received[2] = decodeMessage[3];
        appEUI_Received[3] = decodeMessage[4];
        appEUI_Received[4] = decodeMessage[5];
        appEUI_Received[5] = decodeMessage[6];
        appEUI_Received[6] = decodeMessage[7];
        appEUI_Received[7] = decodeMessage[8];

        byte[] devEUI_Received = new byte[8];
        devEUI_Received[0] = decodeMessage[16];
        devEUI_Received[1] = decodeMessage[15];
        devEUI_Received[2] = decodeMessage[14];
        devEUI_Received[3] = decodeMessage[13];
        devEUI_Received[4] = decodeMessage[12];
        devEUI_Received[5] = decodeMessage[11];
        devEUI_Received[6] = decodeMessage[10];
        devEUI_Received[7] = decodeMessage[9];

        if (Arrays.equals(devEUI_Received, devEUI_Expected)) { 

            byte[] devNonce = new byte[2];
            devNonce[0] = decodeMessage[18];   
            devNonce[1] = decodeMessage[17];

            byte[] appNonce = new byte[3];
            new Random().nextBytes(appNonce);

            appSKey = deriveAppSKey(appNonce, devNonce);
            nwSKey = deriveNwSKey(appNonce, devNonce);
            listener.updateAppSKey(appSKey);
            listener.updateNwSKey(nwSKey);
            this.pForwarder.sendMessage(Sender.JoinAccept(appNonce, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc, appKey, netID, devAddr_Expected));

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

        byte[] devAddr_Received = new byte[4];
        devAddr_Received[0] = decodeMessage[4];
        devAddr_Received[1] = decodeMessage[3];
        devAddr_Received[2] = decodeMessage[2];
        devAddr_Received[3] = decodeMessage[1];

        byte[] fCnt = new byte[2];
        fCnt[0] = decodeMessage[7];
        fCnt[1] = decodeMessage[6];

        if (Arrays.equals(devAddr_Received, devAddr_Expected)) {//comparar devAddr

            byte[] payload = new byte[decodeMessage.length - 9];
            System.arraycopy(decodeMessage, 9, payload, 0, decodeMessage.length - 9);
            byte[] dir = new byte[1];
            return decryptPayload(payload, devAddr_Expected, fCnt, dir);
        } else {
            return null;
        }
      
    }

    public byte[] decryptPayload(byte[] payload, byte[] devAddress, byte[] fCnt, byte[] dir) {
        try {

            byte[] ivKey = new byte[16];
            Arrays.fill(ivKey, (byte) 0);
            ivKey[0] = 1;
            ivKey[15] = 1;

            ivKey[5] = dir[0];
            ivKey[6] = devAddress[3];
            ivKey[7] = devAddress[2];
            ivKey[8] = devAddress[1];
            ivKey[9] = devAddress[0];

            ivKey[10] = fCnt[1];
            ivKey[11] = fCnt[0];

            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivKey);

            SecretKeySpec secretKeySpec = new SecretKeySpec(appSKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            return cipher.doFinal(payload);
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException ex) {
        }
        return null;
    }

    public byte[] deriveAppSKey(byte[] AppNonce, byte[] DevNonce) {

        try {

            SecretKeySpec key = new SecretKeySpec(appKey, "AES");
            Cipher ciph = Cipher.getInstance("AES/ECB/NoPadding");
            ciph.init(Cipher.ENCRYPT_MODE, key);
            byte[] toKey = new byte[16];
            org.bouncycastle.util.Arrays.fill(toKey, (byte) 0);
            toKey[0] = 0x02;
            toKey[1] = AppNonce[2];
            toKey[2] = AppNonce[1];
            toKey[3] = AppNonce[0];
            toKey[4] = netID[2];
            toKey[5] = netID[1];
            toKey[6] = netID[0];
            toKey[7] = DevNonce[1];
            toKey[8] = DevNonce[0];
            
            
            return ciph.update(toKey);
            
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ignore) {
        }
        return null;
    }

    //Not in use yet
    public byte[] deriveNwSKey(byte[] AppNonce, byte[] DevNonce) {
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
            toKey[4] = netID[0];
            toKey[5] = netID[1];
            toKey[6] = netID[2];
            toKey[7] = DevNonce[0];
            toKey[8] = DevNonce[1];
            
            
            return ciph.update(toKey);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ignore) {
        }
        return null;
    }
}
