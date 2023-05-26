package com.example.socialcompass.compass;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.socialcompass.model.Location;

public class CompassLocation {
    private double longitude, latitude;
    private ImageView ui_component;
    private String label;
    private double angleFacing;
    private double angle;
    private int radius;

    public CompassLocation(double latitude, double longitude, String label, ImageView ui_component) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ui_component = ui_component;
        this.angleFacing = 0;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = (angle + 360) % 360;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setAngleFacing(double angleFacing) {
        this.angleFacing = (angleFacing + 360) % 360;
    }

    public double getAngleFacing() {
        return this.angleFacing;
    }

    public String getLabel() {
        return label;
    }
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public ImageView getUi_component() {
        return ui_component;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}