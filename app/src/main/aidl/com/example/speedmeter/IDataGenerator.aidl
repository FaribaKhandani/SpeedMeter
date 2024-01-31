// IDataGenerator.aidl
package com.example.speedmeter;
import com.example.speedmeter.IDataCallback;



interface IDataGenerator {
        void registerDataCallback(IDataCallback callback);
        void unregisterDataCallback(IDataCallback callback);
        void startDataGeneration();
        void stopDataGeneration();

}