package com.d202.sonmal.utils;

import android.graphics.Bitmap;
import android.util.Log;

public class BmpProducer extends Thread {

    CustomFrameAvailableListener customFrameAvailableListener;

    public int height = 513,width = 513;
    Bitmap bmp;

    public BmpProducer(){
        start();
    }

    public void stopThread(){
        this.interrupt();
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
        this.height = bmp.getHeight();
        this.width = bmp.getWidth();
    }

    public void setCustomFrameAvailableListener(CustomFrameAvailableListener customFrameAvailableListner){
        this.customFrameAvailableListener = customFrameAvailableListner;
    }

    public static final String TAG="BmpProducer";
    @Override
    public void run() {
        super.run();
        while ((!this.isInterrupted())){
            try{
                if(bmp==null || customFrameAvailableListener == null)
                    continue;
                customFrameAvailableListener.onFrame(bmp);
                Thread.sleep(10);
            }catch (Exception e){
                Log.d(TAG,e.toString());
            }
        }
    }
}