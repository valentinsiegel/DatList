package fr.siegel.datlist.Utils;

/**
 * Created by Val on 25/09/15.
 */
public class Utils {
    /**
     * Return false if string is empty or composed of spaces
     *
     * @param string to check
     * @return
     */
    public static boolean checkForEmptyString(String string) {
        return string.trim().length() >= 1;
    }
}
