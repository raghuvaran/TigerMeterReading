package edu.clemson.tigermeterreading;

/**
 * Created by rchowda on 7/20/2016.
 */
public class Reading {
    private int rId, mId;
    private String timeStamp;
    private double reading;
    private String notes;

    public Reading(int rId) {
        this.rId = rId;
    }

    public Reading(int rId, int mId, String timeStamp, double reading, String notes) {
        this.rId = rId;
        this.mId = mId;
        this.timeStamp = timeStamp;
        this.reading = reading;
        this.notes = notes;
    }

    public int getrId() {
        return rId;
    }

    public int getmId() {
        return mId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public double getReading() {
        return reading;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public String toString() {
        return "Reading{" +
                "rId=" + rId +
                ", mId=" + mId +
                ", timeStamp='" + timeStamp + '\'' +
                ", reading=" + reading +
                ", notes='" + notes + '\'' +
                '}';
    }
}
