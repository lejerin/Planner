package com.example.planner.Realm;

import android.text.BoringLayout;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Plans extends RealmObject {

    //계획 등록시 계획 이름, 계획 날짜, (계획 날짜 중 연도,월만 - 비교를 위해)
    // 시작시간, 종료시간, 총 소요시간
    // 성공 여부 0: 시작안함 / 1: 성공 / 2: 실패
    // 집중도

    @PrimaryKey
    private int id; //기본키
    private String title, timeText, yearMonth;
    private Date startTime, endTime;
    private int duration; //초 단위
    private int isSuccess = 0;
    private int focus = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public int getSuccess() {
        return isSuccess;
    }

    public void setSuccess(int success) {
        isSuccess = success;
    }

    public int getFocus() {
        return focus;
    }

    public void setFocus(int focus) {
        this.focus = focus;
    }

    public String getyearMonth() {
        return yearMonth;
    }

    public void setyearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimeText() {
        return timeText;
    }

    public void setTimeText(String timeText) {
        this.timeText = timeText;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
