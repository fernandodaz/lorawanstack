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
        PacketForwarder rec = new PacketForwarder(con, gwIDExpected);

        LoraWanReceiver LoraWan_N1 = new LoraWanReceiver(nwSKey_N1, appSKey_N1, appKey_N1, netID, appEUI_N1, devEUIExpected_N1, devAddrExpected_N1,
                rec, new SensorListener() {
            @Override
            public void onData(Sensor sensor) {
                System.out.println(" Sensor_N1 " + sensor.getTempBuiltIn());
            }
        });

        rec.addReceiver(LoraWan_N1);

       LoraWanReceiver LoraWan_N2 = new LoraWanReceiver(nwSKey_N2, appSKey_N2, appKey_N2, netID, appEUI_N2, devEUIExpected_N2, devAddrExpected_N2,
                rec, new SensorListener() {
            @Override
            public void onData(Sensor sensor) {
                System.out.println(" Sensor_N2 " + sensor.getTempBuiltIn());
            }
        });

        rec.addReceiver(LoraWan_N2);
        con.addListener(rec);
    }

    private static final byte[] netID = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01};

    private static final byte[] gwIDExpected = new byte[]{(byte) 0xA8,
        (byte) 0x40, (byte) 0x41, (byte) 0x1D, (byte) 0x25, (byte) 0xA0,
        (byte) 0x41, (byte) 0x50};

    
    
    private static final byte[] nwSKey_N1 = new byte[]{(byte) 0xFD, (byte) 0x57,
        (byte) 0x7F, (byte) 0x5F, (byte) 0x7B, (byte) 0xFB, (byte) 0xD3,
        (byte) 0x2B, (byte) 0xA1, (byte) 0x2D, (byte) 0xDD, (byte) 0xEC,
        (byte) 0xA7, (byte) 0x51, (byte) 0xC6, (byte) 0x23};

    private static final byte[] appSKey_N1 = new byte[]{(byte) 0x4E,
        (byte) 0x9D, (byte) 0xE6, (byte) 0x48, (byte) 0x63, (byte) 0x2A,
        (byte) 0xD2, (byte) 0x34, (byte) 0xCD, (byte) 0xF9, (byte) 0x77,
        (byte) 0xA5, (byte) 0x8C, (byte) 0xAB, (byte) 0x9B, (byte) 0xBB};

    private static final byte[] appKey_N1 = new byte[]{(byte) 0x1C,
        (byte) 0x19, (byte) 0x2F, (byte) 0xBE, (byte) 0xC4, (byte) 0x27,
        (byte) 0x91, (byte) 0x63, (byte) 0x58, (byte) 0xDB, (byte) 0x6C,
        (byte) 0x1B, (byte) 0xE6, (byte) 0xFF, (byte) 0x2D, (byte) 0xDF};

    private static final byte[] appEUI_N1 = new byte[]{(byte) 0xA0,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x01, (byte) 0x00};

    private static final byte[] devEUIExpected_N1 = new byte[]{(byte) 0xA8,
        (byte) 0x40, (byte) 0x41, (byte) 0x00, (byte) 0x01, (byte) 0x81,
        (byte) 0xBB, (byte) 0xDD};

    private static final byte[] devAddrExpected_N1 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01};

    
    
    private static final byte[] nwSKey_N2 = new byte[]{(byte) 0xBD, (byte) 0x85,
        (byte) 0x64, (byte) 0x4B, (byte) 0x69, (byte) 0xFA, (byte) 0xA8,
        (byte) 0xFD, (byte) 0x38, (byte) 0x55, (byte) 0x24, (byte) 0xE2,
        (byte) 0xA5, (byte) 0x5E, (byte) 0xDE, (byte) 0x86};

    private static final byte[] appSKey_N2 = new byte[]{(byte) 0x1A,
        (byte) 0x6A, (byte) 0xB4, (byte) 0xEE, (byte) 0x9C, (byte) 0xB9,
        (byte) 0x6A, (byte) 0x9F, (byte) 0x8D, (byte) 0x9F, (byte) 0x97,
        (byte) 0x73, (byte) 0x44, (byte) 0xB5, (byte) 0x2A, (byte) 0x8F};

    private static final byte[] appKey_N2 = new byte[]{(byte) 0xBA,
        (byte) 0x68, (byte) 0x23, (byte) 0x8E, (byte) 0xDB, (byte) 0x33,
        (byte) 0xEB, (byte) 0x2C, (byte) 0xC8, (byte) 0x29, (byte) 0x94,
        (byte) 0x27, (byte) 0xB9, (byte) 0x5D, (byte) 0x1B, (byte) 0x2B};

    private static final byte[] appEUI_N2 = new byte[]{(byte) 0xA0,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x01, (byte) 0x00};

    private static final byte[] devEUIExpected_N2 = new byte[]{(byte) 0xA8,
        (byte) 0x40, (byte) 0x41, (byte) 0x00, (byte) 0x01, (byte) 0x81,
        (byte) 0xBB, (byte) 0xDC};

    private static final byte[] devAddrExpected_N2 = new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x01};

}
