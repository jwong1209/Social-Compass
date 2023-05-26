package com.example.socialcompass;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import android.widget.ImageView;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ActivityScenario;
import androidx.test.rule.GrantPermissionRule;

import com.example.socialcompass.activity.CompassActivity;
import com.example.socialcompass.compass.Compass;
import com.example.socialcompass.compass.CompassLocation;
import com.example.socialcompass.compass.OrientationService;
import com.example.socialcompass.model.Location;
import com.example.socialcompass.viewmodel.FriendViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class MS2US3BDDTest {

    @Rule
    public GrantPermissionRule mRuntimePermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void test_orientation_service(){
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            //initializing variables
            CompassLocation user = new CompassLocation(0, 0, "",  null);
            FriendViewModel viewModel = activity.setupViewModel();
            HashMap<String, ArrayList<Object>> listOfFriends = new HashMap<>();
            Compass c = new Compass(user, viewModel,activity, 0);
            ArrayList<Object> a = new ArrayList<>();
            a.add(new Location("123",null, "Joe", 0, 0 ));
            a.add(c.createImageViewIcon());
            a.add(c.createUILabel("Joe"));
            listOfFriends.put("Joe", a);
            c.setMockLof(listOfFriends);

            //facing the same direction
            user.setAngleFacing(0*180/Math.PI);
            c.updateUIAngleOfFriend();
            ImageView im = (ImageView) c.getListOfFriends().get("Joe").get(1);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) im.getLayoutParams();
            assertEquals(0, layoutParams.circleAngle, 1);

            //friend is 90 degrees to the right
            user.setAngleFacing(-Math.PI/2*180/Math.PI);
            c.updateUIAngleOfFriend();
            layoutParams = (ConstraintLayout.LayoutParams) im.getLayoutParams();
            assertEquals(90, layoutParams.circleAngle, 1);
        });
    }
    @Test
    public void test_friend_movement() {
        ActivityScenario<CompassActivity> scenario = ActivityScenario.launch(CompassActivity.class);
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.moveToState(Lifecycle.State.STARTED);
        scenario.onActivity(activity -> {
            //initializing variables
            CompassLocation user = new CompassLocation(32.8818526, -117.2348335, "", null);
            FriendViewModel viewModel = activity.setupViewModel();
            HashMap<String, ArrayList<Object>> listOfFriends = new HashMap<>();
            Compass c = new Compass(user, viewModel,activity, 0);

            //setting mock values
            ArrayList<Object> a = new ArrayList<>();
            a.add(new Location("123",null, "Joe", 32.8818526, -117.2348335 ));
            a.add(c.createImageViewIcon());
            a.add(c.createUILabel("Joe"));
            listOfFriends.put("Joe", a);
            c.setMockLof(listOfFriends);

            //facing the same direction
            user.setAngleFacing(0*180/Math.PI);
            c.updateUIAngleOfFriend();
            ImageView im = (ImageView) c.getListOfFriends().get("Joe").get(1);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) im.getLayoutParams();
            assertEquals(0, layoutParams.circleAngle, 1);

            //friend moves 90 degrees to the right
            Location loc = (Location) c.getListOfFriends().get("Joe").get(0);
            loc.latitude = 32.881743;
            loc.longitude =  -117.2107002;
            c.updateUIAngleOfFriend();
            layoutParams = (ConstraintLayout.LayoutParams) im.getLayoutParams();
            assertEquals(90, layoutParams.circleAngle, 1);
        });
    }
}