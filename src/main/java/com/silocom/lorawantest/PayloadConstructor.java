/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

import com.silocom.m2m.layer.physical.Connection;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author silocom01
 */
public class PayloadConstructor {

    String payloadB64 = null;

    JsonConstructor jsonCons;

    public PayloadConstructor(JsonConstructor jsonCons) {

        this.jsonCons = jsonCons;
    }

    public String JoinAccept(int appNonce, boolean imme, long tmst, float freq, int rfch, int powe, String modu,
            String datr, String codr, boolean ipol, int size, boolean ncrc, byte[] appKey) {  //falta pasar APPEUI,DevEUI, APPKEY

        byte mhdr = 0x20;

        byte[] AppNonce = new byte[3];
        AppNonce[2] = (byte) (appNonce & 0xFF);
        AppNonce[1] = (byte) ((appNonce >> 8) & 0xFF);
        AppNonce[0] = (byte) ((appNonce >> 16) & 0xFF);

        byte[] NetID = new byte[3];
        NetID[0] = 0x00;
        NetID[1] = 0x00;
        NetID[2] = 0x00;

        byte[] DevAddr = new byte[4];
        DevAddr[0] = 0x00;
        DevAddr[1] = 0x00;
        DevAddr[2] = 0x00;
        DevAddr[2] = 0x00;

        byte DLSetting = 2;

        byte RxDelay = 1;

        byte[] mic = Mic.calculateMicJoinResponse(mhdr, AppNonce, NetID, DevAddr, DLSetting, RxDelay, null, appKey);

        byte[] message = new byte[16];

        message[0] = AppNonce[2];
        message[1] = AppNonce[1];
        message[2] = AppNonce[0];
        message[3] = NetID[2];
        message[4] = NetID[1];
        message[5] = NetID[0];
        message[6] = DevAddr[3];
        message[7] = DevAddr[2];
        message[8] = DevAddr[1];
        message[9] = DevAddr[0];
        message[10] = DLSetting;
        message[11] = RxDelay;
        message[12] = mic[0];
        message[13] = mic[1];
        message[14] = mic[2];
        message[15] = mic[3];

        byte[] buffer = new byte[17];
        try {
            SecretKeySpec key = new SecretKeySpec(appKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            buffer = cipher.update(message, 0, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        message = new byte[17];
        System.arraycopy(buffer, 0, message, 1, 16);
        message[0] = mhdr;

        payloadB64 = Base64.encodeBase64String(message);

        return jsonCons.SendJson(payloadB64, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, message.length, ncrc);

    }

}
