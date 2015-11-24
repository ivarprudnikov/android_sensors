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
    byte[] client_certificate;

    public ActionUrl() {}

    public ActionUrl(String url, long frequency, byte[] client_certificate) {
        this.url = url;
        this.frequency = frequency;
        this.timestamp = System.currentTimeMillis();
        this.last_updated = this.timestamp;
        this.client_certificate = client_certificate;
    }

    public ActionUrl(int id, String url, long frequency, long timestamp, long lastUpdated, byte[] client_certificate) {
        this.id = id;
        this.url = url;
        this.frequency = frequency;
        this.timestamp = timestamp;
        this.last_updated = lastUpdated;
        this.client_certificate = client_certificate;
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

    public byte[] getClient_certificate() {
        return client_certificate;
    }

    public void setClient_certificate(byte[] client_certificate) {
        this.client_certificate = client_certificate;
    }

    // Parcelable
    //////////////////

    public ActionUrl(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ActionUrl createFromParcel(Parcel in) {
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
        if(client_certificate != null){
            dest.writeInt(client_certificate.length);
            dest.writeByteArray(client_certificate);
        } else {
            dest.writeInt(0);
            dest.writeByteArray(new byte[0]);
        }
    }

    public void readFromParcel(Parcel in) {
        this.id = in.readInt();
        this.url = in.readString();
        this.frequency = in.readLong();
        this.timestamp = in.readLong();
        this.last_updated = in.readLong();
        this.client_certificate = new byte[in.readInt()];
        in.readByteArray(this.client_certificate);
    }

    @Override
    public int describeContents(){
        return 0;
    };

}
