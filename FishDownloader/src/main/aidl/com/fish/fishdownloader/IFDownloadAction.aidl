// IFDownloadAction.aidl
package com.fish.fishdownloader;

// Declare any non-default types here with import statements

interface IFDownloadAction {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString, IBinder aCK);
    String getAbsFilePath(String tag);
    void startDownload(String tag);
    void cancelDownloadByTag(String tag);
    void cancelAll();
    void registerCK(String tag, IBinder ck);
    void unregisterCK(String tag);
    void unregisterAllCKs();
    boolean hasTag(String tag);
    void pauseByTag(String tag);
    void initInfo(String tag, String name, String downloadUrl, int size);
}
