package com.example.socialcompass.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class LocationRepository {

    private final LocationDao dao;
    private ScheduledFuture<?> clockFuture;

    public LocationRepository(LocationDao dao) {
        this.dao = dao;
    }

    public LiveData<Location> getSynced(String publicCode) {
        var location = new MediatorLiveData<Location>();

        Observer<Location> updateFromRemote = remoteLocation -> {
            var localLocation = location.getValue();
            //if the user location isn't on the server
            if (remoteLocation == null) return; // do nothing

            Instant localUpdateTime = Instant.parse(localLocation.updated_at);
            Instant remoteUpdateTime = Instant.parse(remoteLocation.updated_at);
            if (localLocation == null || localUpdateTime.compareTo(remoteUpdateTime) < 0) {
                upsertLocal(remoteLocation);
            }
        };

        // If we get a local update, pass it on.
        location.addSource(getLocal(publicCode), location::postValue);
        // If we get a remote update, update the local version (triggering the above observer)
        location.addSource(getRemote(publicCode), updateFromRemote);

        return location;
    }

    public void deleteLocal(Location location) {
        dao.delete(location);
    }

    public boolean existsLocal(String publicCode) {
        return dao.exists(publicCode);
    }

    public LiveData<Location> getLocal(String publicCode) {
        return dao.get(publicCode);
    }

    public LiveData<List<Location>> getAllLocal() {
        return dao.getAll();
    }

    public LiveData<Location> getRemote(String publicCode) {

        if (this.clockFuture != null && !this.clockFuture.isCancelled()) {
            clockFuture.cancel(true);
        }
        MutableLiveData<Location> res_location = new MutableLiveData<>();
        var executor = Executors.newSingleThreadScheduledExecutor();
        clockFuture = executor.scheduleAtFixedRate(() -> {
            res_location.postValue(LocationAPI.provide().getLocation(publicCode));
        }, 0, 3000, TimeUnit.MILLISECONDS);
        return res_location;
    }

    public void upsertLocal(Location location) {
        dao.upsert(location);
    }

    public void upsertRemote(Location location) {
        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(() -> {
            try {
                LocationAPI.provide().updateLocation(location);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e);
                throw new RuntimeException(e);
            }
        });
    }

    public void createRemote(Location location) {
        var executor = Executors.newSingleThreadScheduledExecutor();
        executor.submit(() -> {
            try {
                LocationAPI.provide().putLocation(location);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void createSynced(Location location) {
        upsertLocal(location);
        createRemote(location);
    }

    public void upsertSynced(Location location) {
        upsertRemote(location);
        upsertLocal(location);
    }
}

