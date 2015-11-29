package fr.siegel.datlist.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import fr.siegel.datlist.LoginActivity;

/**
 * Created by Val on 25/09/15.
 */
public class Utils {

    public static boolean checkForEmptyString(String string) {
        return string.trim().length() >= 1;
    }

}
