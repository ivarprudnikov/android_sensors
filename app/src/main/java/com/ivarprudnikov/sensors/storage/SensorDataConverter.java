package com.ivarprudnikov.sensors.storage;

import java.nio.ByteBuffer;

public class SensorDataConverter {

    public static byte[] floatArray2ByteArray(float[] values){
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values){
            buffer.putFloat(value);
        }

        return buffer.array();
    }
}
