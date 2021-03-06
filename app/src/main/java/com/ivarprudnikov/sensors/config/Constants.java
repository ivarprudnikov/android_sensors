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

package com.ivarprudnikov.sensors.config;

public class Constants {
    public final static String INTENT_KEY_SENSOR_NAME = "com.ivarprudnikov.sensors.SENSOR_NAME";
    public final static String INTENT_KEY_ACTION_URL_ID = "com.ivarprudnikov.sensors.ACTION_URL_ID";
    public final static String INTENT_KEY_ACTION_URL_REGISTRATION_TIME = "com.ivarprudnikov.sensors.ACTION_URL_REGISTRATION_TIME";

    public final static String INTENT_ACTION_TRIGGER_FROM_BOOT = "com.ivarprudnikov.sensors.ACTION_TRIGGER_FROM_BOOT";
    public final static String INTENT_ACTION_TRIGGER_EXPORT = "com.ivarprudnikov.sensors.ACTION_TRIGGER_EXPORT";
    public final static String INTENT_ACTION_TRIGGER_REREGISTER_EXPORT = "com.ivarprudnikov.sensors.ACTION_TRIGGER_REREGISTER_EXPORT";

    public static final String TEXT_NO_SENSOR_FOUND = "Could not identify the sensor";
    public static final String TEXT_SENSOR_TYPE_UNRESOLVABLE = "Unresolvable";
    public static final int CHART_MAX_HORIZONTAL_POINTS = 200;
    public static final String PREFS_IS_SENSOR_LOG_ENABLED = "IS_SENSOR_LOG_ENABLED";
    public static final String PREFS_SENSOR_ENABLED_PREFIX = "IS_SENSOR_ENABLED_";
    public static final String PREFS_STORAGE_DURATION = "STORAGE_DURATION";
    public static final String PREFS_STORAGE_LIMIT_ACTION = "STORAGE_LIMIT_ACTION";
}
