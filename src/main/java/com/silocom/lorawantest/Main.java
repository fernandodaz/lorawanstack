package com.silocom.lorawantest;

import com.silocom.m2m.layer.physical.PhysicalLayer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;


/**
 *
 * @author hvarona
 */
public class Main {

    private static final String ALGORITHM = "AES";

    private static final byte[] networkKey = new byte[]{(byte) 0xFD, (byte) 0x57,
        (byte) 0x7F, (byte) 0x5F, (byte) 0x7B, (byte) 0xFB, (byte) 0xD3,
        (byte) 0x2B, (byte) 0xA1, (byte) 0x2D, (byte) 0xDD, (byte) 0xEC,
        (byte) 0xA7, (byte) 0x51, (byte) 0xC6, (byte) 0x23};

    private static final byte[] appSessionKey = new byte[]{(byte) 0x4E, 
        (byte) 0x9D, (byte) 0xE6, (byte) 0x48, (byte) 0x63, (byte) 0x2A,
        (byte) 0xD2, (byte) 0x34, (byte) 0xCD, (byte) 0xF9, (byte) 0x77,
        (byte) 0xA5, (byte) 0x8C, (byte) 0xAB, (byte) 0x9B, (byte) 0xBB};

    private static final byte[] appKey = new byte[]{(byte) 0x1C,
        (byte) 0x19, (byte) 0x2F, (byte) 0xBE, (byte) 0xC4, (byte) 0x27,
        (byte) 0x91, (byte) 0x63, (byte) 0x58, (byte) 0xDB, (byte) 0x6C,
        (byte) 0x1B, (byte) 0xE6, (byte) 0xFF, (byte) 0x2D, (byte) 0xDF};

    private IvParameterSpec ivParameterSpec;
    private SecretKeySpec secretKeySpec;
    private Cipher cipher;
    private static PayloadConstructor Sender;
    private static JsonConstructor jsonCons;

    public static void main(String args[]) throws Exception {

        //com.silocom.m2m.layer.physical.Connection con = PhysicalLayer.addConnection(1, 1700, "192.168.2.69");
        //downlinkMIC = new Mic();
        jsonCons = new JsonConstructor();
        Sender = new PayloadConstructor(jsonCons);
        
        //LoraWanReceiver rec = new LoraWanReceiver(networkKey, appKey, appSessionKey, Sender, jsonCons);
        //con.addListener(rec);
       // rec.messageType("AAABAAAAAACg3buBAQBBQKjzxPpPmhg=");
        

    }

    public Main() {

        try {
            ivParameterSpec = new IvParameterSpec(new byte[]{(byte) 0x01, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0xDD, (byte) 0xBB, (byte) 0x81, (byte) 0x01,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x01});
            secretKeySpec = new SecretKeySpec(appSessionKey, "AES");
            cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String encrypt(String toBeEncrypt) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        byte[] encrypted = cipher.doFinal(toBeEncrypt.getBytes());
        return Base64.encodeBase64String(encrypted);
    }

    public String decrypt(String encrypted) throws InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(Base64.decodeBase64(encrypted)));
    }

    public String decrypt(byte[] array) throws InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        return new String(cipher.doFinal(array));
    }
    
    public void calculateMIC(byte[] MHDR, byte[] FHDR, byte[] FPort, byte[] FRMPayload, byte[] NewKey ){
        
        
    }
}
