package com.example.honggliu.mapapp;

public class DeviceItem {
    private String device_name;
    private int device_power;
    private int device_state;
    private int id;

    public DeviceItem(String device_name, int device_power, int device_state, int id) {
        this.device_name = device_name;
        this.device_power = device_power;
        this.device_state = device_state;
        this.id=id;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public int getDevice_power() {
        return device_power;
    }

    public void setDevice_power(int device_power) {
        this.device_power = device_power;
    }

    public int getDevice_state() {
        return device_state;
    }

    public void setDevice_state(int device_state) {
        this.device_state = device_state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
