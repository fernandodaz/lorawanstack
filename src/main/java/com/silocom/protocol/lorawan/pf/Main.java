/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.protocol.lorawan.pf;

import com.silocom.m2m.layer.physical.PhysicalLayer;
import com.silocom.lorawantest.LoraWanReceiver;

/**
 *
 * @author silocom01
 */
public class Main {

    public static void main(String args[]) throws Exception {
        
        
        com.silocom.m2m.layer.physical.Connection con = PhysicalLayer.addConnection(1, 1700, "192.168.2.69");
        PacketForwarder rec = new PacketForwarder(con);
        con.addListener(rec);

    }
}
