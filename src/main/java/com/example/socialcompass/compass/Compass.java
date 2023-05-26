package com.example.socialcompass.compass;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialcompass.R;
import com.example.socialcompass.activity.AddFriendsActivity;
import com.example.socialcompass.activity.CompassActivity;
import com.example.socialcompass.model.Location;
import com.example.socialcompass.model.LocationDatabase;
import com.example.socialcompass.model.LocationRepository;
import com.example.socialcompass.viewmodel.FriendViewModel;

public class Compass {

    private ArrayList<CompassLocation> compassLocations;
    private float scale;
    private SharedPreferences sharedPreferences;
    private CompassLocation user;
    private FriendViewModel viewModel;
    private Activity activity;
    private HashMap<String, ArrayList<Object>> listOfFriends;
    private SharedPreferences.Editor editor;
    private boolean gotRemoteRan = false;
    private LocationDatabase locationDatabase;


    public Compass(CompassLocation user, FriendViewModel viewModel, Activity activity, float scale){
        this.user = user;
        this.viewModel = viewModel;
        this.activity = activity;
        this.listOfFriends = new HashMap<>();
        this.sharedPreferences = activity.getSharedPreferences("inputs", activity.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
        this.scale = scale;
    }

    public void setMockLof(HashMap<String, ArrayList<Object>> f) {
        this.listOfFriends = f;
    }

    public List<LiveData<Location>> handleLocationListOfLiveData(List<Location> lld) {
        List<LiveData<Location>> list = new ArrayList<>();
        for (int i = 0; i < lld.size(); i++) {
            handleFriendData(lld.get(i));
            String publicCode = lld.get(i).publicCode;
            if (!gotRemoteRan && lld.get(i).privateCode == null) {
                locationDatabase = LocationDatabase.provide(activity);
                LocationRepository locationRepository = new LocationRepository(locationDatabase.getDao());
                LiveData<Location> friendLocationLiveData = locationRepository.getRemote(publicCode);
                list.add(friendLocationLiveData);
                friendLocationLiveData.observe((LifecycleOwner) activity, x -> {
                    if (x != null) {
                        locationRepository.upsertLocal(x);
                    } else {
                        Log.v("Null: ", "null");
                    }
                });
            } else if (!gotRemoteRan){
                this.user.setLatitude(lld.get(i).latitude);
                this.user.setLongitude(lld.get(i).longitude);
            }
        }
        gotRemoteRan = true;
        updateUIAngleOfFriend();
        setLocationRadius();
        return list;
    }

    public List<LiveData<Location>> mockHandleLocationListOfLiveData(CompassLocation user, HashMap<String, ArrayList<Object>> lof, List<Location> lld) {
        List<LiveData<Location>> list = new ArrayList<>();
        for (int i = 0; i < lld.size(); i++) {
            String publicCode = lld.get(i).publicCode;
            if (!gotRemoteRan && lld.get(i).privateCode == null) {
                locationDatabase = LocationDatabase.provide(activity);
                LocationRepository locationRepository = new LocationRepository(locationDatabase.getDao());
                LiveData<Location> friendLocationLiveData = locationRepository.getRemote(publicCode);
                list.add(friendLocationLiveData);
                friendLocationLiveData.observe((LifecycleOwner) activity, x -> {
                    if (x != null) {
                        locationRepository.upsertLocal(x);
                    } else {
                        Log.v("Null: ", "null");
                    }
                });
            } else if (!gotRemoteRan){
                user.setLatitude(lld.get(i).latitude);
                user.setLongitude(lld.get(i).longitude);
            }
        }
        gotRemoteRan = true;
        return list;
    }

    public void setLocationRadius() {
        int zoomLvl = sharedPreferences.getInt("zoomLvl", 2);
        for (String key : this.listOfFriends.keySet()) {

            TextView label = (TextView) this.listOfFriends.get(key).get(2);
            ImageView im = (ImageView) this.listOfFriends.get(key).get(1);
            Location loc = (Location) this.listOfFriends.get(key).get(0);
            Log.v("Zoom Level", "" + zoomLvl);
            Log.v("Loc Lat", "" + loc.latitude);
            Log.v("Loc Long", "" + loc.longitude);
            ConstraintLayout.LayoutParams imLayoutParams = (ConstraintLayout.LayoutParams) im.getLayoutParams();
            ConstraintLayout.LayoutParams labelLayoutParams = (ConstraintLayout.LayoutParams) label.getLayoutParams();
            activity.runOnUiThread(() ->{
                double distance = RelativeDistance.calculate_relative_distance(this.user.getLatitude(), this.user.getLongitude(), loc.latitude, loc.longitude);
                if (zoomLvl == 1) {
                    if (distance <= 1) {
                        imLayoutParams.circleRadius = (int) (170 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 40;
                        label.setVisibility(View.VISIBLE);
                        Log.v("Zoom1", "< 1 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    } else {
                        imLayoutParams.circleRadius = (int) (195 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 40;
                        label.setVisibility(View.GONE);
                        Log.v("Zoom1", "> 1 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    }
                } else if (zoomLvl == 2) {
                    System.out.println("Distance: " + distance);
                    if (distance <= 1) {
                        imLayoutParams.circleRadius = (int) (100 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 40;
                        label.setVisibility(View.VISIBLE);
                        Log.v("Zoom2", "< 1 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    } else if (distance <= 10) {
                        imLayoutParams.circleRadius = (int) (170 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 40;
                        label.setVisibility(View.VISIBLE);
                        Log.v("Zoom2", "< 10 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    } else {
                        imLayoutParams.circleRadius = (int) (195 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 40;
                        label.setVisibility(View.GONE);
                        Log.v("Zoom2", "> 10 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    }
                } else if (zoomLvl == 3) {
                    System.out.println("Distance: " + distance);
                    if (distance <= 1) {
                        imLayoutParams.circleRadius = (int) (50 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 60;
                        label.setVisibility(View.VISIBLE);
                        Log.v("Zoom3", "<1 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    } else if (distance <= 10) {
                        imLayoutParams.circleRadius = (int) (100 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 60;
                        label.setVisibility(View.VISIBLE);
                        Log.v("Zoom3", "< 10 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    } else if (distance <= 500) {
                        imLayoutParams.circleRadius = (int) (170 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 60;
                        label.setVisibility(View.VISIBLE);
                        Log.v("Zoom3", "< 500 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    } else {
                        imLayoutParams.circleRadius = (int) (195 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 60;
                        label.setVisibility(View.GONE);
                        Log.v("Zoom3", "> 500 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    }
                } else {
                    if (distance <= 1) {
                        imLayoutParams.circleRadius = (int) (40 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 60;
                        label.setVisibility(View.VISIBLE);
                        Log.v("Zoom4", "<1 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    } else if (distance <= 10) {
                        imLayoutParams.circleRadius = (int) (80 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 60;
                        label.setVisibility(View.VISIBLE);
                        Log.v("Zoom4", "<10 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    } else if (distance <= 500) {
                        imLayoutParams.circleRadius = (int) (130 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 60;
                        label.setVisibility(View.VISIBLE);
                        Log.v("Zoom4", "<500 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    } else {
                        imLayoutParams.circleRadius = (int) (175 * this.scale + 0.5f);
                        labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 60;
                        try {
                            if (isOnline(loc)) {
                                label.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        Log.v("Zoom4", ">500 mile");
                        Log.v("Radius", "" + imLayoutParams.circleRadius);
                    }
                }

                im.setLayoutParams(imLayoutParams);
                label.setLayoutParams(labelLayoutParams);

            });

        }
    }

    // Changes compass background depending on zoom level
    public void setCompassZoom() {

        Button zoomOutBtn = (Button) activity.findViewById(R.id.zoomOut);
        Button zoomInBtn = (Button) activity.findViewById(R.id.zoomIn);
        ImageView zoom1 = (ImageView) activity.findViewById(R.id.compass_zoom1);
        ImageView zoom2 = (ImageView) activity.findViewById(R.id.compass_zoom2);
        ImageView zoom3 = (ImageView) activity.findViewById(R.id.compass_zoom3);
        ImageView zoom4 = (ImageView) activity.findViewById(R.id.compass_zoom4);
        int zoomLvl = sharedPreferences.getInt("zoomLvl", 2);


        if (zoomLvl == 1) {
            zoomInBtn.setEnabled(false);
            zoom1.setVisibility(View.VISIBLE);
            zoom2.setVisibility(View.INVISIBLE);
            zoom3.setVisibility(View.INVISIBLE);
            zoom4.setVisibility(View.INVISIBLE);
        } else if (zoomLvl == 2) {
            zoom1.setVisibility(View.INVISIBLE);
            zoom2.setVisibility(View.VISIBLE);
            zoom3.setVisibility(View.INVISIBLE);
            zoom4.setVisibility(View.INVISIBLE);
        } else if (zoomLvl == 3) {
            zoom1.setVisibility(View.INVISIBLE);
            zoom2.setVisibility(View.INVISIBLE);
            zoom3.setVisibility(View.VISIBLE);
            zoom4.setVisibility(View.INVISIBLE);
        } else {
            zoomOutBtn.setEnabled(false);
            zoom1.setVisibility(View.INVISIBLE);
            zoom2.setVisibility(View.INVISIBLE);
            zoom3.setVisibility(View.INVISIBLE);
            zoom4.setVisibility(View.VISIBLE);
        }
    }

    public boolean doesFriendExistLocally(Location friend) {
        boolean doesFriendExist = false;
        if (friend.privateCode != null) {
            doesFriendExist = true;
        }
        for (String key : this.listOfFriends.keySet()) {
            if (key.equals(friend.publicCode)) {
                doesFriendExist = true;
            }
        }
        return doesFriendExist;
    }

    public void handleFriendData(Location friend) {
        if (!doesFriendExistLocally(friend)) {
            addFriendLocally(friend);
        } else if (friend.privateCode == null) {
            Location loc = (Location) this.listOfFriends.get(friend.publicCode).get(0);
            loc.latitude = friend.latitude;
            loc.longitude = friend.longitude;
            loc.updated_at = friend.updated_at;

            activity.runOnUiThread(() -> {
                try {
                    ImageView x = (ImageView) this.listOfFriends.get(friend.publicCode).get(1);
                    TextView t = (TextView) this.listOfFriends.get(friend.publicCode).get(2);
                    if (isOnline(friend)) {
                        x.setVisibility(View.VISIBLE);
                        t.setVisibility(View.VISIBLE);
                    } else {
                        x.setVisibility(View.GONE);
                        t.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public boolean isOnline(Location friend) throws Exception {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            String ut = friend.updated_at;
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date d1 = format.parse(ut);
            Date d2 = new Date();
            long difference = d2.getTime() - d1.getTime();
            return (difference / 1000) / 60 < 10;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public void addFriendLocally(Location friend) {
        ArrayList<Object> newArrayList = new ArrayList<>();
        newArrayList.add(friend);
        newArrayList.add(createImageViewIcon());
        newArrayList.add(createUILabel(friend.label));
        this.listOfFriends.put(friend.publicCode, newArrayList);
    }

    public ImageView createImageViewIcon() {
        ImageView image = new ImageView(activity);
        image.setImageResource(R.drawable.black_circle);
        image.setVisibility(View.INVISIBLE);
        ConstraintLayout constraintLayout = (ConstraintLayout) activity.findViewById(R.id.constraintLayout);
        constraintLayout.addView(image);
        ConstraintLayout.LayoutParams templateParams = (ConstraintLayout.LayoutParams) activity.findViewById(R.id.black_circle).getLayoutParams();
        ConstraintLayout.LayoutParams newimageLayout = (ConstraintLayout.LayoutParams) image.getLayoutParams();
        newimageLayout.circleConstraint = templateParams.circleConstraint;
        newimageLayout.height = templateParams.height;
        newimageLayout.width = templateParams.width;
        newimageLayout.circleRadius = templateParams.circleRadius;
        return image;
    }

    public TextView createUILabel(String label) {
        TextView text = new TextView(activity);
        text.setText(label);
        text.setVisibility(View.INVISIBLE);
        ConstraintLayout constraintLayout = (ConstraintLayout) activity.findViewById(R.id.constraintLayout);
        constraintLayout.addView(text);
        ConstraintLayout.LayoutParams templateParams = (ConstraintLayout.LayoutParams) activity.findViewById(R.id.black_circle).getLayoutParams();
        ConstraintLayout.LayoutParams newimageLayout = (ConstraintLayout.LayoutParams) text.getLayoutParams();
        newimageLayout.circleConstraint = templateParams.circleConstraint;
        newimageLayout.height = templateParams.height;
        newimageLayout.width = templateParams.width * 2;
        newimageLayout.circleRadius = templateParams.circleRadius;
        return text;
    }

    public void updateUIAngleOfFriend() {
        for (String key : this.listOfFriends.keySet()) {
            TextView label = (TextView) this.listOfFriends.get(key).get(2);
            ImageView im = (ImageView) this.listOfFriends.get(key).get(1);
            Location loc = (Location) this.listOfFriends.get(key).get(0);
            ConstraintLayout.LayoutParams imLayoutParams = (ConstraintLayout.LayoutParams) im.getLayoutParams();
            ConstraintLayout.LayoutParams labelLayoutParams = (ConstraintLayout.LayoutParams) label.getLayoutParams();

            imLayoutParams.circleAngle = (float) ((-user.getAngleFacing() + RelativeAngle.relative_angle(user.getLatitude(), user.getLongitude(), loc.latitude, loc.longitude)) + 360) % 360;
            labelLayoutParams.circleAngle = imLayoutParams.circleAngle;
            labelLayoutParams.circleRadius = imLayoutParams.circleRadius + 40;
            im.setLayoutParams(imLayoutParams);
            label.setLayoutParams(labelLayoutParams);
        }
    }

    public void start_friend_tracking() {
        setCompassZoom();
        viewModel.getFriends().observe((LifecycleOwner) activity, ld -> {
            handleLocationListOfLiveData(ld);
        });
    }
    public void onZoomInClicked() {
        Button zoomInBtn = (Button) activity.findViewById(R.id.zoomIn);
        Button zoomOutBtn = (Button) activity.findViewById(R.id.zoomOut);
        int zoomLvl = sharedPreferences.getInt("zoomLvl", 2);

        if (zoomLvl == 1) {
            zoomInBtn.setEnabled(false);
        }
        if (zoomLvl > 1) {
            zoomInBtn.setEnabled(true);
            zoomOutBtn.setEnabled(true);
            editor.putInt("zoomLvl", zoomLvl - 1);
            editor.apply();
        }

        setCompassZoom();
    }
    public void onZoomOutClicked() {
        Button zoomOutBtn = (Button) activity.findViewById(R.id.zoomOut);
        Button zoomInBtn = (Button) activity.findViewById(R.id.zoomIn);
        int zoomLvl = sharedPreferences.getInt("zoomLvl", 2);

        if (zoomLvl == 4) {
            zoomOutBtn.setEnabled(false);
        }
        if (zoomLvl < 4) {
            zoomOutBtn.setEnabled(true);
            zoomInBtn.setEnabled(true);
            editor.putInt("zoomLvl", zoomLvl + 1);
            editor.apply();
        }
        setCompassZoom();
    }
    public HashMap<String, ArrayList<Object>> getListOfFriends() {
        return this.listOfFriends;
    }
}