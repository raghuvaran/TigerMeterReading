package edu.clemson.tigermeterreading;

/**
 * Created by rchowda on 7/16/2016.
 */
public class Meter {
    int mID, digits;
    String number, type, facName, serial, units;


    public Meter(int mID, int digits, String number, String type, String facName, String serial, String units) {
        this.mID = mID;
        this.digits = digits;
        this.number = number;
        this.type = type;
        this.facName = facName;
        this.serial = serial;
        this.units = units;
    }

    public Meter(){

    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public int getDigits() {
        return digits;
    }

    public void setDigits(int digits) {
        this.digits = digits;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFacName() {
        return facName;
    }

    public void setFacName(String facName) {
        this.facName = facName;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }
}
