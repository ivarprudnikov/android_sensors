package com.ivarprudnikov.sensors.storage;

import android.provider.BaseColumns;

/**
 * http://developer.android.com/training/basics/data-storage/databases.html
 */
public final class SensorDataContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public SensorDataContract() {}

    /* Inner class that defines the table contents */
    public static abstract class DataEntry implements BaseColumns {
        public static final String TABLE_NAME = "data";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp_nano";
        public static final String COLUMN_NAME_SENSOR_NAME = "sensor_name";
        public static final String COLUMN_NAME_SENSOR_DATA_VALUE = "sensor_data_value";
        public static final String COLUMN_NAME_SENSOR_DATA_VALUE_INDEX = "sensor_data_value_index";
    }
}
