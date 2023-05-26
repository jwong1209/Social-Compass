package com.example.socialcompass.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.socialcompass.model.Location;
import com.example.socialcompass.model.LocationDatabase;
import com.example.socialcompass.model.LocationRepository;

import java.util.List;

public class FriendViewModel extends AndroidViewModel {

    private LiveData<List<Location>> friends;
    private final LocationRepository repo;

    public FriendViewModel(@NonNull Application application) {
        super(application);
        var context = application.getApplicationContext();
        var db = LocationDatabase.provide(context);
        var dao = db.getDao();
        this.repo = new LocationRepository(dao);
    }

    public LocationRepository getRepo(){
        return repo;
    }

    /**
     * Load all notes from the database.
     *
     * @return a LiveData object that will be updated when any notes change.
     */
    public LiveData<List<Location>> getFriends() {
        if (friends == null) {
            friends = repo.getAllLocal();
        }
        return friends;
    }

    public void upsertUserLocation(String publicCode, String privateCode, String label, double latitude, double longitude) {
        var location = new Location(publicCode, privateCode, label, latitude, longitude);
        repo.upsertSynced(location);
    }

    public void createUserLocation(String publicCode, String privateCode, String label, double latitude, double longitude) {
        var location = new Location(publicCode, privateCode, label, latitude, longitude);
        repo.createSynced(location);
    }

    public LiveData<List<Location>> setMockFriends(LiveData<List<Location>> mockFriends) {
        friends = mockFriends;
        return friends;
    }
    public LiveData<Location> getLocation(String publicCode) {
        return repo.getLocal(publicCode);
    }
}
