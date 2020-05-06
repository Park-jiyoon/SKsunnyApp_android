// iMyCounterService.aidl
package com.example.wooisso.sunnyapplication;

// Declare any non-default types here with import statements

interface iMyCounterService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

     int getCount();

    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
