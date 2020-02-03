package com.silocom.lorawantest;

/*
 * @Author Fernando Gonzalez.
 */

public interface SensorListener {
 
    public void onData(Sensor sensor);
    
    public void updateAppSKey(byte[] appSKey);
    
    public void updateNwSKey(byte[] nwSkey);
}
