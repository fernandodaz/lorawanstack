/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.protocol.lorawan.pf;

import com.silocom.lorawantest.Mic;
import com.silocom.lorawantest.Utils;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author silocom01
 */
public class Main {

    public static void main(String args[]) throws Exception {

        byte[] srcmsg = new byte[]{(byte) 0x20, (byte) 0x9d, (byte) 0xe0,
            (byte) 0x58, (byte) 0x77, (byte) 0x44, (byte) 0x2c, (byte) 0x64,
            (byte) 0x43, (byte) 0x4f, (byte) 0x2a, (byte) 0xe6, (byte) 0xe4,
            (byte) 0x44, (byte) 0x8d, (byte) 0xc5, (byte) 0x23};
        int index = 0;
        byte MHDR = srcmsg[index];
        index++;

        SecretKeySpec key = new SecretKeySpec(appKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] buffer = cipher.update(srcmsg, 1, srcmsg.length-1);
        srcmsg = new byte[17];
        srcmsg[0] = MHDR;
        System.arraycopy(buffer, 0, srcmsg, 1, buffer.length);
        System.out.println("msg " + Utils.hexToString(srcmsg));

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
        System.out.println("srcmsg.length " + srcmsg.length + " " + index);
        byte[] CFList = new byte[srcmsg.length - (index + 4)];
        for (int i = CFList.length - 1; i >= 0; i--) {
            CFList[i] = srcmsg[index];
            index++;
        }

        byte[] mic = new byte[4];
        System.arraycopy(srcmsg, srcmsg.length - 4, mic, 0, 4);
        System.out.println("MHDR : " + Integer.toHexString(MHDR));
        System.out.println("AppNonce : " + Utils.hexToString(AppNonce));
        System.out.println("NetId : " + Utils.hexToString(NetID));
        System.out.println("DevAddr : " + Utils.hexToString(DevAddr));
        System.out.println("DLSetting : " + Integer.toHexString(DLSetting));
        System.out.println("RxDelay : " + Integer.toHexString(RxDelay));
        System.out.println("CFList : " + Utils.hexToString(CFList));
        System.out.println("Mic : " + Utils.hexToString(mic));

        System.out.println(Utils.hexToString(Mic.calculateMicJoinResponse(msgToMic, appKey)));
        System.out.println(Utils.hexToString(Mic.calculateMicJoinResponse(msgToMic, appSKey)));
        System.out.println(Utils.hexToString(Mic.calculateMicJoinResponse(msgToMic, nwSKey)));

        System.out.println(Utils.hexToString(Mic.calculateMicJoinResponse(MHDR, AppNonce, NetID, DevAddr, DLSetting, RxDelay, CFList, appKey)));
        System.out.println(Utils.hexToString(Mic.calculateMicJoinResponse(MHDR, AppNonce, NetID, DevAddr, DLSetting, RxDelay, CFList, appSKey)));
        System.out.println(Utils.hexToString(Mic.calculateMicJoinResponse(MHDR, AppNonce, NetID, DevAddr, DLSetting, RxDelay, CFList, nwSKey)));


        /*com.silocom.m2m.layer.physical.Connection con = PhysicalLayer.addConnection(PhysicalLayer.UDPCALLBACK, 1700, "192.168.2.69");
        PacketForwarder rec = new PacketForwarder(con);
        LoraWanReceiver LoraWan = new LoraWanReceiver(nwSKey, appSKey, appKey, rec);
        rec.setReceiver(LoraWan);
        con.addListener(rec);*/
    }

    private static final byte[] nwSKey = new byte[]{(byte) 0xFD, (byte) 0x57,
        (byte) 0x7F, (byte) 0x5F, (byte) 0x7B, (byte) 0xFB, (byte) 0xD3,
        (byte) 0x2B, (byte) 0xA1, (byte) 0x2D, (byte) 0xDD, (byte) 0xEC,
        (byte) 0xA7, (byte) 0x51, (byte) 0xC6, (byte) 0x23};

    private static final byte[] appSKey = new byte[]{(byte) 0x4E,
        (byte) 0x9D, (byte) 0xE6, (byte) 0x48, (byte) 0x63, (byte) 0x2A,
        (byte) 0xD2, (byte) 0x34, (byte) 0xCD, (byte) 0xF9, (byte) 0x77,
        (byte) 0xA5, (byte) 0x8C, (byte) 0xAB, (byte) 0x9B, (byte) 0xBB};

    private static final byte[] appKey = new byte[]{(byte) 0x1C,
        (byte) 0x19, (byte) 0x2F, (byte) 0xBE, (byte) 0xC4, (byte) 0x27,
        (byte) 0x91, (byte) 0x63, (byte) 0x58, (byte) 0xDB, (byte) 0x6C,
        (byte) 0x1B, (byte) 0xE6, (byte) 0xFF, (byte) 0x2D, (byte) 0xDF};
}
