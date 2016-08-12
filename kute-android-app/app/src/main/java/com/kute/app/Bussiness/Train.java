package com.kute.app.Bussiness;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nrv on 8/13/16.
 */
public class Train {

    private String fullName;
    private String start;
    private String startTime;
    private String End;
    private String endTime;

    @JsonProperty("fullName")
    public String getTrainname() {
        return fullName;
    }

    public void setTrainname(String trainname) {
        this.fullName = trainname;
    }
    @JsonProperty("Start")
    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
    @JsonProperty("StartTime")
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    @JsonProperty("End")
    public String getEnd() {
        return End;
    }

    public void setEnd(String end) {
        this.End = end;
    }
    @JsonProperty("EndTime")
    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
