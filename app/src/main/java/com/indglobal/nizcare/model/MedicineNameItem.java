package com.indglobal.nizcare.model;

/**
 * Created by readyassist on 2/7/18.
 */

public class MedicineNameItem {

    private String medicine_id,medicine_name;

    public MedicineNameItem(String medicine_id, String medicine_name) {
        this.medicine_id = medicine_id;
        this.medicine_name = medicine_name;
    }

    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }
}
