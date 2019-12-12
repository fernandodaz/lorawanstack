/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;



/**
 *
 * @author silocom01
 */
public class DownlinkMIC {

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
    
    public int MIC(){
    
    
    return 0;
    }

    //public String AES128Calculator() {
    //}

    public DownlinkMIC() {
    }
}
