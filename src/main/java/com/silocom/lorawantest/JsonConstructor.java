/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

import com.silocom.m2m.layer.physical.Connection;
import com.google.gson.Gson;
import com.silocom.protocol.lorawan.pf.PacketForwarder;

/**
 *
 * @author silocom01
 */
public class JsonConstructor {

    public void SendJson(String data, boolean imme, long tmst, float freq, int rfch, int powe, String modu, String datr,
            String codr, boolean ipol, int size, boolean ncrc) {

        Gson gson = new Gson();
        JsonMessage jsonObject = new JsonMessage(imme, tmst, freq, rfch, powe, modu, datr, codr, ipol, size, ncrc, data);  //Construye un objeto y envia como parametros los datos necesarios a la clase JsonMessage para poder construir el Json     
        String jsonToSend = gson.toJson(jsonObject);

        FinalJson finalJsonToSend = new FinalJson(jsonObject);
        String finaljsonToSend = gson.toJson(finalJsonToSend);
        //enviar a packetForwarder

        System.out.println(" Join Accept > " + finaljsonToSend);
    }

    public class FinalJson {

        public JsonMessage txpk;

        public FinalJson(JsonMessage txpk) {
            this.txpk = txpk;
        }

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
