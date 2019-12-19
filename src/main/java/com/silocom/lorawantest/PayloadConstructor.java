/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

import com.silocom.m2m.layer.physical.Connection;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author silocom01
 */
public class PayloadConstructor {

    String payloadB64 = null;
    Connection send;
    JsonConstructor jsonCons;
    Mic downlinkMIC;

    public PayloadConstructor(Connection send, JsonConstructor jsonCons, Mic downlinkMIC) {
        this.send = send;
        this.jsonCons = jsonCons;
        this.downlinkMIC = downlinkMIC;
    }

    public void JoinAccept(int appNonce, boolean imme, long tmst, float freq, int rfch, int powe, String modu,
            String datr, String codr, boolean ipol, int size, boolean ncrc) {

        byte[] message = new byte[14];
        //System.out.println("payloadConstr");
        message[0] = 0x20;  //MHDR para mensaje de join accept siempre es 20
        message[1] = (byte) (appNonce & 0xFF);
        message[2] = (byte) ((appNonce >> 8) & 0xFF);
        message[3] = (byte) ((appNonce >> 16) & 0xFF);
        message[4] =  0x00;   //netid
        message[5] =  0x00;  //netid
        message[6] =  0x00;  //netid
        message[7] =  0x00;  //devaddr
        message[8] =  0x00;  //devaddr
        message[9] =  0x00;  //devaddr
        message[10] =  0x00; //devaddr
        message[11] = (byte) 0x95; //DL settings
        message[12] = (byte) 0xDF; // rxDelay
        message[13] = (byte) downlinkMIC.MIC();
        payloadB64 = Base64.encodeBase64String(message);

        jsonCons.SendJson(payloadB64, imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc);

    }

}

//0 000
