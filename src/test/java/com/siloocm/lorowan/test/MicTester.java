package com.siloocm.lorowan.test;

import com.silocom.lorawantest.Mic;
import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 *
 * @author hvarona
 */
public class MicTester extends TestCase {

    public MicTester() {
    }

    @org.junit.jupiter.api.BeforeAll
    public static void setUpClass() throws Exception {
    }

    @org.junit.jupiter.api.AfterAll
    public static void tearDownClass() throws Exception {
    }

    @org.junit.jupiter.api.BeforeEach
    public void setUp() throws Exception {
        super.setUp();
    }

    @org.junit.jupiter.api.AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testMicConstructor() {
        String msg = "20813F47F508FFA2670B6E23E01F84B9E25D9C4115F02EEA0B3DD3E20B3ECA92DA";
        byte MHDR = 0x20;
        byte[] FHDR = new byte[]{(byte) 0x6E, (byte) 0x23, (byte) 0xE0, (byte) 0x1F, (byte) 0x84, (byte) 0xB9, (byte) 0xE2};
        byte fport = 0x0;
        byte[] payload = new byte[]{(byte) 0x5D, (byte) 0x9C, (byte) 0x41, (byte) 0x15, (byte) 0xF0, (byte) 0x2E, (byte) 0xEA, (byte) 0x0B, (byte) 0x3D, (byte) 0xD3, (byte) 0xE2, (byte) 0x0B};
        boolean downlink = false;
        byte[] key = new byte[]{(byte) 0xFD, (byte) 0x57, (byte) 0x7F, (byte) 0x5F, (byte) 0x7B, (byte) 0xFB, (byte) 0xD3, (byte) 0x2B, (byte) 0xA1, (byte) 0x2D, (byte) 0xDD, (byte) 0xEC, (byte) 0xA7, (byte) 0x51, (byte) 0xC6, (byte) 0x23};
        byte[] mic = Mic.calculateMic(MHDR, FHDR, fport, payload, downlink, key);
        System.out.print("Mic calculado : ");
        for (byte mi : mic) {
            if ((mi & 0xff) < 0x10) {
                System.out.print("0");
            }
            System.out.print(Integer.toHexString((mi & 0xff)));
        }
        Assertions.assertEquals(mic, new byte[]{(byte) 0x3E, (byte) 0xCA, (byte) 0x92, (byte) 0xDA});
    }
}
