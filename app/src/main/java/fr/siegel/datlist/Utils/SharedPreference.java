package fr.siegel.datlist.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Val on 01/09/15.
 */
public class SharedPreference
{
    static final String PREF_USER_ID = "userId";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setUserName(Context ctx, long userId)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putLong(PREF_USER_ID, userId);
        editor.commit();
    }

    public static long getUserName(Context ctx)
    {
        return getSharedPreferences(ctx).getLong(PREF_USER_ID, 0);
    }
}
