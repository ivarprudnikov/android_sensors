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

import android.os.Parcel;
import android.os.Parcelable;

public class ActionUrl implements Parcelable {

    Integer id;
    String url;
    long frequency;
    long timestamp;
    long last_updated;

    public ActionUrl() {}

    public ActionUrl(String url, long frequency) {
        this.url = url;
        this.frequency = frequency;
        this.timestamp = System.currentTimeMillis();
        this.last_updated = this.timestamp;
    }

    public ActionUrl(int id, String url, long frequency, long timestamp, long lastUpdated) {
        this.id = id;
        this.url = url;
        this.frequency = frequency;
        this.timestamp = timestamp;
        this.last_updated = lastUpdated;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isEnabled() {
        return this.frequency > 0;
    }

    public long getLast_updated() {
        return last_updated;
    }

    public void setLast_updated(long last_updated) {
        this.last_updated = last_updated;
    }

    // Parcelable
    //////////////////

    public ActionUrl(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ActionUrl createFromParcel(Parcel in ) {
            return new ActionUrl( in );
        }
        public ActionUrl[] newArray(int size) {
            return new ActionUrl[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(url);
        dest.writeLong(frequency);
        dest.writeLong(timestamp);
        dest.writeLong(last_updated);
    }

    public void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.frequency = in.readLong();
        this.timestamp = in.readLong();
        this.last_updated = in.readLong();
    }

    @Override
    public int describeContents(){
        return 0;
    };

}
