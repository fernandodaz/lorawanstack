/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class PayloadConstructor {

    String payloadB64 = null;

    JsonConstructor jsonCons;

    public PayloadConstructor(JsonConstructor jsonCons) {

        this.jsonCons = jsonCons;
    }

    public String JoinAccept(byte[] appNonce, boolean imme, long tmst, float freq, int rfch, int powe, String modu,
            String datr, String codr, boolean ipol, int size, boolean ncrc, byte[] appKey, byte[] netID, byte[] devAddr) {  //falta pasar APPEUI,DevEUI, APPKEY

        byte MHDR = 0x20;

         byte DLSetting = 2;

        byte RxDelay = 1;

        
        byte[] mic = Mic.calculateMicJoinResponse(MHDR, appNonce, netID, devAddr, DLSetting, RxDelay, null, appKey);

        byte[] message = new byte[16];

        message[0] = appNonce[2];
        message[1] = appNonce[1];
        message[2] = appNonce[0];
        message[3] = netID[2];
        message[4] = netID[1];
        message[5] = netID[0];
        message[6] = devAddr[3];
        message[7] = devAddr[2];
        message[8] = devAddr[1];
        message[9] = devAddr[0];
        message[10] = DLSetting;
        message[11] = RxDelay;
        message[12] = mic[0];
        message[13] = mic[1];
        message[14] = mic[2];
        message[15] = mic[3];

        try {
            SecretKeySpec key = new SecretKeySpec(appKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] buffer = cipher.update(message, 0, message.length);
            message = new byte[buffer.length + 1];
            System.arraycopy(buffer, 0, message, 1, buffer.length);
            message[0] = MHDR;

            decodeJoinAccept(message, appKey);

            payloadB64 = Base64.encodeBase64String(message);

            return jsonCons.SendJson(payloadB64, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, message.length, ncrc);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static JoinAcceptMessage decodeJoinAccept(byte[] message, byte[] appKey) {
        try {
            byte[] srcmsg = message;
            int index = 0;
            byte MHDR = srcmsg[index];
            index++;

            SecretKeySpec key = new SecretKeySpec(appKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] buffer = cipher.update(srcmsg, 1, srcmsg.length - 1);
            srcmsg = new byte[17];
            srcmsg[0] = MHDR;
            System.arraycopy(buffer, 0, srcmsg, 1, buffer.length);

            byte[] msgToMic = new byte[srcmsg.length - 4];
            System.arraycopy(srcmsg, 0, msgToMic, 0, msgToMic.length);

            byte[] AppNonce = new byte[3];

            for (int i = 2; i >= 0; i--) {
                AppNonce[i] = srcmsg[index];
                index++;
            }
            byte[] NetID = new byte[3];
            for (int i = 2; i >= 0; i--) {
                NetID[i] = srcmsg[index];
                index++;
            }
            byte[] DevAddr = new byte[4];
            for (int i = 3; i >= 0; i--) {
                DevAddr[i] = srcmsg[index];
                index++;
            }
            byte DLSetting = srcmsg[index];
            index++;
            byte RxDelay = srcmsg[index];
            index++;
            byte[] CFList = new byte[srcmsg.length - (index + 4)];
            for (int i = CFList.length - 1; i >= 0; i--) {
                CFList[i] = srcmsg[index];
                index++;
            }

            byte[] mic = new byte[4];
            return new JoinAcceptMessage(MHDR, AppNonce, NetID, DevAddr, DLSetting, RxDelay, mic);
            
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
            Logger.getLogger(PayloadConstructor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static class JoinAcceptMessage {

        byte mhdr;
        byte[] appNonce;
        byte[] netId;
        byte[] devAddr;
        byte dlSetting;
        byte rxDelay;
        byte[] CFList = null;
        byte[] mic;

        public JoinAcceptMessage() {
        }

        public JoinAcceptMessage(byte mhdr, byte[] appNonce, byte[] netId, byte[] devAddr, byte dlSetting, byte rxDelay, byte[] mic) {
            this.mhdr = mhdr;
            this.appNonce = appNonce;
            this.netId = netId;
            this.devAddr = devAddr;
            this.dlSetting = dlSetting;
            this.rxDelay = rxDelay;
            this.mic = mic;
        }

        public byte getMhdr() {
            return mhdr;
        }

        public void setMhdr(byte mhdr) {
            this.mhdr = mhdr;
        }

        public byte[] getAppNonce() {
            return appNonce;
        }

        public void setAppNonce(byte[] appNonce) {
            this.appNonce = appNonce;
        }

        public byte[] getNetId() {
            return netId;
        }

        public void setNetId(byte[] netId) {
            this.netId = netId;
        }

        public byte[] getDevAddr() {
            return devAddr;
        }

        public void setDevAddr(byte[] devAddr) {
            this.devAddr = devAddr;
        }

        public byte getDlSetting() {
            return dlSetting;
        }

        public void setDlSetting(byte dlSetting) {
            this.dlSetting = dlSetting;
        }

        public byte getRxDelay() {
            return rxDelay;
        }

        public void setRxDelay(byte rxDelay) {
            this.rxDelay = rxDelay;
        }

        public byte[] getCFList() {
            return CFList;
        }

        public void setCFList(byte[] CFList) {
            this.CFList = CFList;
        }

        public byte[] getMic() {
            return mic;
        }

        public void setMic(byte[] mic) {
            this.mic = mic;
        }

    }
}
