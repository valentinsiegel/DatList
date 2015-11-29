package fr.siegel.datlist.defs;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Val on 13/10/15.
 */
public abstract class RecyclerViewType {

    @IntDef({REGULAR_VIEW, ADD_VIEW})
    @Retention(RetentionPolicy.SOURCE)

    public @interface NavigationMode {
    }

    public static final int REGULAR_VIEW = 0;
    public static final int ADD_VIEW = 1;

    @NavigationMode
    public abstract int getNavigationMode();

    public abstract void setNavigationMode(@NavigationMode int mode);
}