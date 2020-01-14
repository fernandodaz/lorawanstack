/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.params.KeyParameter;

public abstract class Mic {

    public static byte[] calculateMicMacPayload(byte MHDR, byte[] FHDR,
            byte fport, byte[] payload, boolean downlink, byte[] key) {

        BlockCipher cipher = new AESEngine();
        CMac cmac = new CMac(cipher);
        final CipherParameters params = new KeyParameter(key);
        cmac.init(params);

        cmac.update((byte) 0x49);
        cmac.update((byte) 0x00);
        cmac.update((byte) 0x00);
        cmac.update((byte) 0x00);
        cmac.update((byte) 0x00);
        cmac.update((byte) (downlink ? 1 : 0));

        for (int i = 3; i >= 0; i--) {
            cmac.update(FHDR[i]);
        }

        cmac.update((byte) 0x00);
        cmac.update((byte) 0x00);
        for (int i = 6; i >= 5; i--) {
            cmac.update(FHDR[i]);
        }
        int msgLen = 2 + FHDR.length + payload.length;
        cmac.update((byte) msgLen);

        cmac.update(MHDR);

        for (int i = FHDR.length - 1; i >= 0; i--) {
            cmac.update(FHDR[i]);
        }

        cmac.update(fport);

        for (int i = payload.length - 1; i >= 0; i--) {
            cmac.update(payload[i]);
        }

        byte[] temp = new byte[cmac.getMacSize()];
        byte[] answer = new byte[4];
        cmac.doFinal(temp, 0);
        System.arraycopy(temp, 0, answer, 0, answer.length);
        return answer;
    }

    public static byte[] calculateMicJoinResponse(byte[] msg, byte[] appKey) {
        BlockCipher cipher = new AESEngine();
        CMac cmac = new CMac(cipher);
        final CipherParameters params = new KeyParameter(appKey);
        cmac.init(params);
        cmac.update(msg, 0, msg.length);

        byte[] temp = new byte[cmac.getMacSize()];
        byte[] answer = new byte[4];
        cmac.doFinal(temp, 0);
        System.arraycopy(temp, 0, answer, 0, answer.length);
        return answer;
    }

    public static byte[] calculateMicJoinResponse(byte MHDR, byte[] AppNonce,
            byte[] NetID, byte[] DevAddr, byte DLSetting, byte RxDelay,
            byte[] CFList, byte[] appKey) {

        if (AppNonce.length != 3 || NetID.length != 3 || DevAddr.length != 4) {
            return null;
        }
        if (CFList == null) {
            CFList = new byte[0];
        }

        BlockCipher cipher = new AESEngine();
        CMac cmac = new CMac(cipher);
        final CipherParameters params = new KeyParameter(appKey);
        cmac.init(params);
        cmac.update(MHDR);

        for (int i = AppNonce.length - 1; i >= 0; i--) {
            cmac.update(AppNonce[i]);
        }

        for (int i = NetID.length - 1; i >= 0; i--) {
            cmac.update(NetID[i]);
        }

        for (int i = DevAddr.length - 1; i >= 0; i--) {
            cmac.update(DevAddr[i]);
        }

        cmac.update(DLSetting);
        cmac.update(RxDelay);
        cmac.update(CFList, 0, CFList.length);

        byte[] temp = new byte[cmac.getMacSize()];
        byte[] answer = new byte[4];
        cmac.doFinal(temp, 0);
        System.arraycopy(temp, 0, answer, 0, answer.length);
        return answer;
    }

    public static byte[] calculateMicJoinRequest(byte MHDR, byte[] AppEUI, byte[] DevEUI, byte[] DevNonce, byte[] AppKey) {

        if (AppEUI.length != 8 || DevEUI.length != 8 | DevNonce.length != 2) {
            return null;
        }

        BlockCipher cipher = new AESEngine();
        CMac cmac = new CMac(cipher);
        final CipherParameters params = new KeyParameter(AppKey);
        cmac.init(params);
        cmac.update(MHDR);

        for (int i = AppEUI.length - 1; i >= 0; i--) {
            cmac.update(AppEUI[i]);
        }

        for (int i = DevEUI.length - 1; i >= 0; i--) {
            cmac.update(DevEUI[i]);
        }

        for (int i = DevNonce.length - 1; i >= 0; i--) {
            cmac.update(DevNonce[i]);
        }

        byte[] temp = new byte[cmac.getMacSize()];
        byte[] answer = new byte[4];
        cmac.doFinal(temp, 0);
        System.arraycopy(temp, 0, answer, 0, answer.length);
        return answer;
    }
}
