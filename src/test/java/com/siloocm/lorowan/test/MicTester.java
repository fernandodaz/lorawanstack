package com.siloocm.lorowan.test;

import com.silocom.lorawantest.Mic;
import java.security.InvalidKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.junit.Assert;
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
    public void MicJoinAcceptConstructor() {
        String msg = "20813F47F508FFA2670B6E23E01F84B9E25D9C4115F02EEA0B3DD3E20B3ECA92DA";
        byte MHDR = 0x20;
        byte[] AppNonce = new byte[]{};
        byte[] NetId = new byte[]{};
        byte[] DevAddr = new byte[]{};
        byte DLSetting = (byte) 0x00;
        byte RxDelay = (byte) 0x00;

        byte[] key = new byte[]{(byte) 0xFD, (byte) 0x57, (byte) 0x7F, (byte) 0x5F, (byte) 0x7B, (byte) 0xFB, (byte) 0xD3, (byte) 0x2B, (byte) 0xA1, (byte) 0x2D, (byte) 0xDD, (byte) 0xEC, (byte) 0xA7, (byte) 0x51, (byte) 0xC6, (byte) 0x23};

        byte[] mic = Mic.calculateMicJoinResponse(MHDR, AppNonce, NetId, DevAddr, DLSetting, RxDelay, null, key);
        System.out.print("Mic calculado : ");
        for (byte mi : mic) {
            if ((mi & 0xff) < 0x10) {
                System.out.print("0");
            }
            System.out.print(Integer.toHexString((mi & 0xff)));
        }
        Assertions.assertEquals(mic, new byte[]{(byte) 0x3E, (byte) 0xCA, (byte) 0x92, (byte) 0xDA});
    }

    @Test
    public void testMicJoinAcceptConstructor() {
        byte MHDR = 0x40;
        byte[] AppNonce = new byte[]{(byte) 0x09, (byte) 0xFA, (byte) 0x79};
        byte[] NetID = new byte[]{(byte) 0xF0, (byte) 0x56, (byte) 0x30};
        byte[] DevAddr = new byte[]{(byte) 0xDC, (byte) 0xE1, (byte) 0x23, (byte) 0x8C};
        byte DLSetting = 0x3B;
        byte RxDelay = 0x41;
        byte[] CFList = new byte[]{(byte) 0x8F, (byte) 0x8B, (byte) 0x41,
            (byte) 0x20, (byte) 0x80, (byte) 0x2B, (byte) 0xCA, (byte) 0xD3,
            (byte) 0xF8, (byte) 0x92, (byte) 0xA2, (byte) 0xC6, (byte) 0x6B,
            (byte) 0xDD, (byte) 0xD5, (byte) 0xD1};

        byte[] AppKey = new byte[]{(byte) 0x1C, (byte) 0x19, (byte) 0x2F,
            (byte) 0xBE, (byte) 0xC4, (byte) 0x27, (byte) 0x91, (byte) 0x63,
            (byte) 0x58, (byte) 0xDB, (byte) 0x6C, (byte) 0x1B, (byte) 0xE6,
            (byte) 0xFF, (byte) 0x2D, (byte) 0xDF};
        byte[] AppSKey = new byte[]{(byte) 0x4E, (byte) 0x9D, (byte) 0xE6,
            (byte) 0x48, (byte) 0x63, (byte) 0x2A, (byte) 0xD2, (byte) 0x34,
            (byte) 0xCD, (byte) 0xF9, (byte) 0x77, (byte) 0xA5, (byte) 0x8C,
            (byte) 0xAB, (byte) 0x9B, (byte) 0xBB};
        byte[] NetSKey = new byte[]{(byte) 0xFD, (byte) 0x57, (byte) 0x7F,
            (byte) 0x5F, (byte) 0x7B, (byte) 0xFB, (byte) 0xD3, (byte) 0x2B,
            (byte) 0xA1, (byte) 0x2D, (byte) 0xDD, (byte) 0xEC, (byte) 0xA7,
            (byte) 0x51, (byte) 0xC6, (byte) 0x23};

        byte[] mic = Mic.calculateMicJoinResponse(MHDR, AppNonce, NetID, DevAddr, DLSetting, RxDelay, CFList, AppKey);
        byte[] expectedMic = new byte[]{(byte) 0x3A, (byte) 0x5D, (byte) 0x6C, (byte) 0x8F};
        Assert.assertArrayEquals(mic, expectedMic);
        byte[] msg = new byte[]{(byte) 0x20, (byte) 0x79, (byte) 0xFA,
            (byte) 0x09, (byte) 0x30, (byte) 0x56, (byte) 0xF0, (byte) 0x8C,
            (byte) 0x23, (byte) 0xE1, (byte) 0xDC, (byte) 0x3B, (byte) 0x41,
            (byte) 0x8F, (byte) 0x8B, (byte) 0x41, (byte) 0x20, (byte) 0x80,
            (byte) 0x2B, (byte) 0xCA, (byte) 0xD3, (byte) 0xF8, (byte) 0x92,
            (byte) 0xA2, (byte) 0xC6, (byte) 0x6B, (byte) 0xDD, (byte) 0xD5,
            (byte) 0xD1};
        mic = Mic.calculateMicJoinResponse(msg, AppSKey);
        Assert.assertArrayEquals(mic, expectedMic);
    }

    @Test
    public void testMicJoinRequestConstructor() {

        try {
            byte MHDR = 0x00;
            byte[] AppEUI = new byte[]{(byte) 0xA0, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00};
            byte[] DevEUI = new byte[]{(byte) 0xA8, (byte) 0x40, (byte) 0x41, (byte) 0x00, (byte) 0x01, (byte) 0x81, (byte) 0xBB, (byte) 0xDD};
            byte[] DevNonce = new byte[]{(byte) 0x63, (byte) 0x2E};

            byte[] AppKey = new byte[]{(byte) 0x1C, (byte) 0x19, (byte) 0x2F,
                (byte) 0xBE, (byte) 0xC4, (byte) 0x27, (byte) 0x91, (byte) 0x63,
                (byte) 0x58, (byte) 0xDB, (byte) 0x6C, (byte) 0x1B, (byte) 0xE6,
                (byte) 0xFF, (byte) 0x2D, (byte) 0xDF};
            byte[] AppSKey = new byte[]{(byte) 0x4E, (byte) 0x9D, (byte) 0xE6,
                (byte) 0x48, (byte) 0x63, (byte) 0x2A, (byte) 0xD2, (byte) 0x34,
                (byte) 0xCD, (byte) 0xF9, (byte) 0x77, (byte) 0xA5, (byte) 0x8C,
                (byte) 0xAB, (byte) 0x9B, (byte) 0xBB};
            byte[] NetSKey = new byte[]{(byte) 0xFD, (byte) 0x57, (byte) 0x7F,
                (byte) 0x5F, (byte) 0x7B, (byte) 0xFB, (byte) 0xD3, (byte) 0x2B,
                (byte) 0xA1, (byte) 0x2D, (byte) 0xDD, (byte) 0xEC, (byte) 0xA7,
                (byte) 0x51, (byte) 0xC6, (byte) 0x23};

            byte[] mic = Mic.calculateMicJoinRequest(MHDR, AppEUI, DevEUI, DevNonce, AppKey);

            byte[] expectedMic = new byte[]{(byte) 0xDE, (byte) 0x01, (byte) 0xB2, (byte) 0x6C};

            Assert.assertArrayEquals(mic, expectedMic);
        } catch (Exception ex) {
            Logger.getLogger(MicTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
