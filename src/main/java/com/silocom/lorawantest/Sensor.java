/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

public class Sensor {

    int batVal;
    int batStat;
    int tempBuiltIn;
    int hum;
    int tempExt;
    int rssi;
    String time;

    public Sensor(int batVal, int batStat, int tempBuiltIn, int hum, int tempExt, int rssi, String time) {
        this.batVal = batVal;
        this.tempBuiltIn = tempBuiltIn;
        this.hum = hum;
        this.tempExt = tempExt;
        this.rssi = rssi;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public int getRssi() {
        return rssi;
    }

    public int getBatVal() {
        return batVal;
    }

    public int getBatStat() {
        return batStat;
    }

    public int getTempBuiltIn() {
        return tempBuiltIn;
    }

    public int getHum() {
        return hum;
    }

    public int getTempExt() {
        return tempExt;
    }

    public void setBatVal(int batVal) {
        this.batVal = batVal;
    }

    public void setBatStat(int batStat) {
        this.batStat = batStat;
    }

    public void setTempBuiltIn(int tempBuiltIn) {
        this.tempBuiltIn = tempBuiltIn;
    }

    public void setHum(int hum) {
        this.hum = hum;
    }

    public void setTempExt(int tempExt) {
        this.tempExt = tempExt;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
