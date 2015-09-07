package fr.siegel.datlist;

import android.content.res.Configuration;

import fr.siegel.datlist.backend.datListApi.model.User;

/**
 * Created by Val on 07/09/15.
 */
public class Application extends android.app.Application {

    private static Application mApplication;
    private static User user;

    public Application getApplication() {
        return mApplication;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
