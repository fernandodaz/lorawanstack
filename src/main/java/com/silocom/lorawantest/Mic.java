/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author silocom01
 */
public class Mic {

    private static final Cipher cipher;

    static {
        Cipher tmp;
        try {
            tmp = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception ex) {
            Logger.getLogger(Mic.class.getName()).log(Level.SEVERE, null, ex);
            tmp = null;
        }
        cipher = tmp;
    }

    public static byte[] calculateMic(byte MHDR, byte[] FHDR, byte fport, byte[] payload, boolean downlink, byte[] key) {
        byte[] msg = new byte[3 + FHDR.length + payload.length];
        int index = 0;
        msg[index] = MHDR;
        index++;
        System.arraycopy(FHDR, 0, msg, index, FHDR.length);
        index += FHDR.length + 1;
        msg[index] = fport;
        index++;
        System.arraycopy(payload, 0, msg, index, payload.length);

        byte[] blocks = new byte[msg.length + 4 + 1 + 4 + 2 + 1 + 1]; //mensaje + vector inicial + mtpe + address + fcount +00 + tamaño mensaje

        Arrays.fill(blocks, (byte) 0);
        blocks[0] = 0x49;
        blocks[1] = downlink ? (byte) 1 : (byte) 0;
        System.arraycopy(FHDR, 0, blocks, 2, 4);
        System.arraycopy(FHDR, 5, blocks, 6, 2);
        blocks[9] = (byte) msg.length;
        System.arraycopy(msg, 0, blocks, 10, msg.length);

        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] temp = cipher.doFinal(blocks);
            byte[] answer = new byte[4];
            System.arraycopy(temp, 0, answer, 0, 4);
            return answer;
            
        } catch (InvalidKeyException ex) {
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(Mic.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(Mic.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public void B0block(byte[] nwkSKey, String message, int devAddr) {

        byte[] B0 = new byte[16];
        int fcntDown = 0;

        B0[0] = 0x49;
        B0[1] = 0x00;
        B0[2] = 0x00;
        B0[3] = 0x00;
        B0[4] = 0x00;
        B0[5] = 0x01;    //0x00 for uplink 0x01 for downlink
        B0[6] = 0x00;
        B0[7] = 0x00;
        B0[8] = 0x00;
        B0[9] = 0x00;
        B0[10] = (byte) (fcntDown & 0xFF);
        B0[11] = (byte) ((fcntDown >> 8) & 0xFF);
        B0[12] = (byte) ((fcntDown >> 16) & 0xFF);
        B0[13] = (byte) ((fcntDown >> 24) & 0xFF);
        B0[14] = 0x00;
        B0[15] = (byte) message.length();

        ++fcntDown;

    }

    public int MIC() {

        return 0;
    }

    //public String AES128Calculator() {
    //}
    public Mic() {
    }
}
