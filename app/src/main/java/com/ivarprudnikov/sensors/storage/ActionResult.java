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

import java.util.Date;

public class ActionResult {

    Integer id;
    long timestamp;
    Integer action_id;
    boolean is_success;
    Long data_from_time;
    Long data_to_time;

    public ActionResult() {}

    public ActionResult(int action_id, boolean is_success, Long from, Long to) {
        this.timestamp = (new Date()).getTime();
        this.action_id = action_id;
        this.is_success = is_success;
        this.data_from_time = from;
        this.data_to_time = to;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getAction_id() {
        return action_id;
    }

    public void setAction_id(Integer action_id) {
        this.action_id = action_id;
    }

    public boolean is_success() {
        return is_success;
    }

    public void setIs_success(boolean is_success) {
        this.is_success = is_success;
    }

    public Long getData_from_time() {
        return data_from_time;
    }

    public void setData_from_time(Long data_from_time) {
        this.data_from_time = data_from_time;
    }

    public Long getData_to_time() {
        return data_to_time;
    }

    public void setData_to_time(Long data_to_time) {
        this.data_to_time = data_to_time;
    }
}
