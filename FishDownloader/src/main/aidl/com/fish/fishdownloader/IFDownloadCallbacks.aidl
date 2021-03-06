// IFDownloadCallbacks.aidl
package com.fish.fishdownloader;

// Declare any non-default types here with import statements

interface IFDownloadCallbacks {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString);
     void onProgress(double pg);
     void onComplete(String filePath);
     void onFailed(String msg);
     void onCanceled(String msg);
     void onPause(String msg);
}
