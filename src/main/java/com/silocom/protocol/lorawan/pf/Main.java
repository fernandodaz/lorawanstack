/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.protocol.lorawan.pf;

import com.silocom.lorawantest.LoraWanReceiver;
import com.silocom.lorawantest.Sensor;
import com.silocom.lorawantest.SensorListener;
import com.silocom.m2m.layer.physical.PhysicalLayer;

public class Main {

    public static void main(String args[]) throws Exception {

        com.silocom.m2m.layer.physical.Connection con = PhysicalLayer.addConnection(PhysicalLayer.UDPCALLBACK, 1700, "192.168.2.69");
        PacketForwarder rec = new PacketForwarder(con);

        LoraWanReceiver LoraWan = new LoraWanReceiver(nwSKey, appSKey, appKey, netID, appEUI_N1, devEUI_N1, devAddr_N1,
                appEUI_N2, devEUI_N2, devAddr_N2, appEUI_N3, devEUI_N3, devAddr_N3, appEUI_N4, devEUI_N4, devAddr_N4,
                rec, new SensorListener() {
            @Override
            public void onData(Sensor sensor) {
                System.out.println(" temp ");
            }
        });

        rec.addReceiver(LoraWan);
        con.addListener(rec);
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

    private static final byte[] netID = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};

    private static final byte[] appEUI_N1 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};
    private static final byte[] devEUI_N1 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};
    private static final byte[] devAddr_N1 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};

    private static final byte[] appEUI_N2 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};
    private static final byte[] devEUI_N2 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};
    private static final byte[] devAddr_N2 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};

    private static final byte[] appEUI_N3 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};
    private static final byte[] devEUI_N3 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};
    private static final byte[] devAddr_N3 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};

    private static final byte[] appEUI_N4 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};
    private static final byte[] devEUI_N4 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};
    private static final byte[] devAddr_N4 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};

}
