/*
 * @Author Fernando Gonzalez.
 */
package com.silocom.lorawantest;

/**
 *
 * @author silocom01
 */
public class Sensor {

    int batVal;
    int batStat;
    int tempBuiltIn;
    int hum;
    int tempExt;

    public Sensor(int batVal, int batStat, int tempBuiltIn, int hum, int tempExt) {
        this.batVal = batVal;
        this.tempBuiltIn = tempBuiltIn;
        this.hum = hum;
        this.tempExt = tempExt;
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

}
