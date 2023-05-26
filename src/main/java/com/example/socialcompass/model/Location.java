package com.example.socialcompass.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "locations")
public class Location {
    /**
     * The UID of the location. Used as the primary key for social compass (even on the cloud).
     */
    @PrimaryKey
    @SerializedName("public_code")
    @NonNull
    @Expose(serialize = false, deserialize = true)
    public String publicCode;

    /**
     * The private key of the location
     */
    @SerializedName("private_code")
    @Expose(serialize = true)
    public String privateCode;

    /**
     * The username of the location.
     */
    @SerializedName("label")
    @NonNull
    @Expose(serialize = true)
    public String label;

    /**
     * The latitude of the location.
     */
    @SerializedName("latitude")
    @Expose(serialize = true)
    public double latitude;

    /**
     * The longitude of the location.
     */
    @SerializedName("longitude")
    @Expose(serialize = true)
    public double longitude;

    @Expose(serialize = false, deserialize = true)
    public String created_at;

    @Expose(serialize = false, deserialize = true)
    public String updated_at;

    /**
     * General constructor for a location.
     */
    public Location(@NonNull String publicCode, @NonNull String privateCode, @NonNull String label, @NonNull double latitude, @NonNull double longitude) {
        this.publicCode = publicCode;
        this.privateCode = privateCode;
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static Location fromJSON(String json) {
        return new Gson().fromJson(json, Location.class);
    }

    public String toJSON() {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithoutExposeAnnotation();
        return builder.create().toJson(this);
    }
}