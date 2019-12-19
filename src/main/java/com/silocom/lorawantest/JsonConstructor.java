/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

import com.silocom.m2m.layer.physical.Connection;
import com.google.gson.Gson;

/**
 *
 * @author silocom01
 */
public class JsonConstructor {

    Connection con;
    PayloadConstructor Pcons;

    public JsonConstructor(Connection con) {
        this.con = con;
    }

    public void SendJson(String data, boolean imme, long tmst, float freq, int rfch, int powe, String modu, String datr,
            String codr, boolean ipol, int size, boolean ncrc) {
        Gson gson = new Gson();  
        JsonMessage jsonObject = new JsonMessage(imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc, data);        
        String jsonToSend = gson.toJson(jsonObject);  
        con.sendMessage(jsonToSend.getBytes());
        System.out.println("Join accepted: " + jsonToSend);  
    }

    public class JsonMessage {

        public boolean imme;
        public long tmst;
        public float freq;
        public int rfch;
        public int powe;
        public String modu;
        public String datr;
        public String codr;
        public boolean ipol;
        public int size;
        public boolean ncrc;
        public String data;

        public JsonMessage(boolean imme, long tmst, float freq, int rfch, int powe, String modu, String datr, String codr, boolean ipol, int size, boolean ncrc, String data) {
            this.imme = imme;
            this.tmst = tmst;
            this.freq = freq;
            this.rfch = rfch;
            this.powe = powe;
            this.modu = modu;
            this.datr = datr;
            this.codr = codr;
            this.ipol = ipol;
            this.size = size;
            this.ncrc = ncrc;
            this.data = data;
        }

        
    }
}
