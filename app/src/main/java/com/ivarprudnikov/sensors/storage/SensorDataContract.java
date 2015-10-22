/**
 * This file is part of com.ivarprudnikov.sensors package.
 *
 * com.ivarprudnikov.sensors is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * com.ivarprudnikov.sensors is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with com.ivarprudnikov.sensors.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp_milli";
        public static final String COLUMN_NAME_SENSOR_NAME = "sensor_name";
        public static final String COLUMN_NAME_SENSOR_DATA_VALUE = "sensor_data_value";
        public static final String COLUMN_NAME_SENSOR_DATA_VALUE_INDEX = "sensor_data_value_index";
    }

    /* Inner class that defines the table contents */
    public static abstract class ActionUrl implements BaseColumns {
        public static final String TABLE_NAME = "action_url";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_FREQUENCY = "frequency";
        public static final String COLUMN_NAME_ENABLED = "enabled";
    }
}
