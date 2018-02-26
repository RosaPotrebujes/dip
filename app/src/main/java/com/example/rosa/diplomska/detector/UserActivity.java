package com.example.rosa.diplomska.detector;


import android.databinding.BaseObservable;
import android.databinding.Bindable;
import com.example.rosa.diplomska.BR;

public class UserActivity extends BaseObservable {
    private boolean userActivityInProgress = false;
    private boolean userActivityDone = false;
    private boolean gotMusic;
    private boolean gotLocation;
    private boolean gotMovement;
    private boolean gotPeople;

    private String uaUsername;
    private int uaPeople;
    private String uaLocation;
    private String uaMusic;
    private String uaMovement;

    public UserActivity() {
        this.uaUsername = "";
        this.uaPeople = 0;
        this.uaLocation = "";
        this.uaMusic = "";
        this.uaMovement = "";
        this.gotMusic = false;
        this.gotLocation = false;
        this.gotMovement = false;
        this.gotPeople = false;
    }

    @Bindable
    public boolean getUserActivityInProgress() {
        return this.userActivityInProgress;
    }
    public void setUserActivityInProgress(boolean p) {
        this.userActivityInProgress = p;
        notifyPropertyChanged(BR.userActivityInProgress);
    }

    @Bindable
    public boolean getUserActivityDone() {
        return this.userActivityDone;
    }
    public void setUserActivityDone() {
        boolean b = this.gotLocation && this.gotMovement && this.gotMusic && this.gotPeople;
        if(this.userActivityDone != b) {
            this.userActivityDone = b;
            notifyPropertyChanged(BR.userActivityDone);
        }
        if(this.userActivityDone) {
            mOnActivityDoneListener.onActivityDone();
        }
    }

    @Bindable
    public String getUaUsername() {
        return this.uaUsername;
    }
    public void setUaUsername(String username) {
        this.uaUsername = username;
        notifyPropertyChanged(BR.uaUsername);
    }

    @Bindable
    public int getUaPeople() {
        return this.uaPeople;
    }
    public void setUaPeople(int people) {
        this.uaPeople = people;
        notifyPropertyChanged(BR.uaPeople);
    }

    @Bindable
    public String getUaLocation() {
        return this.uaLocation;
    }
    public void setUaLocation(String location) {
        this.uaLocation = location;
        notifyPropertyChanged(BR.uaLocation);
    }

    @Bindable
    public String getUaMusic() {
        return this.uaMusic;
    }
    public void setUaMusic(String music) {
        this.uaMusic = music;
        notifyPropertyChanged(BR.uaMusic);
    }

    @Bindable
    public String getUaMovement() {
        return this.uaMovement;
    }
    public void setUaMovement(String movement) {
        this.uaMovement = movement;
        notifyPropertyChanged(BR.uaMovement);
    }

    @Bindable
    public boolean getGotMusic() {
        return this.gotMusic;
    }
    public void setGotMusic(boolean p) {
        this.gotMusic = p;
        notifyPropertyChanged(BR.gotMusic);
        setUserActivityDone();
    }

    @Bindable
    public boolean getGotMovement() {
        return this.gotMovement;
    }
    public void setGotMovement(boolean p) {
        this.gotMovement = p;
        notifyPropertyChanged(BR.gotMovement);
        setUserActivityDone();
    }

    @Bindable
    public boolean getGotPeople() {
        return this.gotPeople;
    }
    public void setGotPeople(boolean p) {
        this.gotPeople = p;
        notifyPropertyChanged(BR.gotPeople);
        setUserActivityDone();
    }

    @Bindable
    public boolean getGotLocation() {
        return this.gotLocation;
    }
    public void setGotLocation(boolean p) {
        this.gotLocation = p;
        notifyPropertyChanged(BR.gotLocation);
        setUserActivityDone();
    }

    public interface OnActvityDoneListener {
        public void onActivityDone();
    }
    private OnActvityDoneListener mOnActivityDoneListener;
    public void setOnActivityDoneListener(OnActvityDoneListener lsitener) {
        this.mOnActivityDoneListener = lsitener;
    }
}
