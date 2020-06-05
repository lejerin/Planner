package com.example.planner.Realm;

import io.realm.RealmObject;

public class User extends RealmObject {

    private int time = 0;
    private int success = 0;


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

}
