package com.example.socialcompass.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Upsert;

import com.example.socialcompass.model.Location;

import java.util.List;

@Dao
public abstract class LocationDao {
    @Upsert
    public abstract long upsert(Location location);

    @Query("SELECT EXISTS(SELECT 1 FROM locations WHERE publicCode = :publicCode)")
    public abstract boolean exists(String publicCode);

    @Query("SELECT * FROM locations WHERE publicCode = :publicCode")
    public abstract LiveData<Location> get(String publicCode);

    @Query("SELECT * FROM locations ORDER BY publicCode")
    public abstract LiveData<List<Location>> getAll();

    @Delete
    public abstract int delete(Location location);
}
