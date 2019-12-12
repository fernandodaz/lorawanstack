/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

/**
 *
 * @author silocom01
 */
public class Utils {

    public static String hexToString(byte[] message) {
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < message.length; i++) {
            
            if ((message[i] & 0xff) < 0x10) {
                answer.append("0");
            }

            answer.append(Integer.toHexString(message[i] & 0xFF));

        }
        return answer.toString();
    }
}
