package ui.tvtoast;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by huixue.gong on 2017/12/8.
 */

public class TvToast implements Parcelable {

    private static final String TAG = "TvToast";
    public static final int TIME_OUT_DURATION_REGULAR = 4000;
    public static final int TIME_OUT_DURATION_INFINITE = -1;
    private static final int NUMBER_OF_PARAMETERS_TO_BE_MARSHALLED = 6;
    private static final int ARBITARY_HASHCODE_VALUE = 21;

    private static final String ICON_RES_KEY = "icon_res_id";
    private static final String ICON_DRAWABLE_KEY = "icon_drawable";
    private static final String MESSAGE_KEY = "message";
    private static final String IS_FOCUSABLE_KEY = "is_focusable";
    private static final String IS_PERSISITENT_KEY = "is_persistent";
    private static final String TIME_OUT_KEY = "time_out";

    private int iconResId = 0;
    private Bitmap icon = null;
    private String message = "";
    private boolean isFocusable = false;
    private boolean isPersistent = false;
    private int timeOutPeriod = TIME_OUT_DURATION_REGULAR;

    public TvToast() {
        super();
        isFocusable = false;
        isPersistent = false;
        timeOutPeriod = TIME_OUT_DURATION_REGULAR;
    }

    /**
     * set time out period
     * @param timeOutPeriod
     */
    public void setTimeOutPeriod(int timeOutPeriod) {
        this.timeOutPeriod = timeOutPeriod;
    }

    /**
     * set Tv Toast as persistent
     * @param isPersistent
     */
    public void setPersistent(boolean isPersistent) {
        this.isPersistent = isPersistent;
    }

    /**
     * set Tv Toast as focusable
     * @param isFocusable
     */
    public void setFocusable(boolean isFocusable) {
        this.isFocusable = isFocusable;
    }

    /**
     * is Tv Toast focusable
     * @return focusable
     */
    public boolean isFocusable() {
        return isFocusable;
    }

    /**
     * is Tv Toast persistent
     * @return persistent
     */
    public boolean isPersistent() {
        return isPersistent;
    }

    /**
     * get Tv Toast time out period
     * @return time out
     */
    public int getTimeOutPeriod() {
        return timeOutPeriod;
    }

    /**
     * set Tv Toast icon res ID
     * @param iconResId
     */
    public void setIconRes(int iconResId) {
        this.iconResId = iconResId;
    }

    /**
     * set Tv Toast message
     * @param msg
     */
    public void setMessage(String msg) {
        this.message = msg;
    }

    /**
     * get Tv Toast message
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * get Tv Toast icon res ID
     * @return iconResID
     */
    public int getIconRes() {
        return iconResId;
    }

    /**
     * get Tv Toast icon
     * @return icon
     */
    public Bitmap getIcon() {
        return icon;
    }

    /**
     * set Tv Toast icon
     * @param icon
     */
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Bundle bundle = new Bundle(NUMBER_OF_PARAMETERS_TO_BE_MARSHALLED);
        bundle.putInt(ICON_RES_KEY, getIconRes());
        bundle.putParcelable(ICON_DRAWABLE_KEY, getIcon());
        bundle.putString(MESSAGE_KEY, getMessage());
        bundle.putBoolean(IS_FOCUSABLE_KEY, isFocusable());
        bundle.putBoolean(IS_PERSISITENT_KEY, isPersistent());
        bundle.putInt(TIME_OUT_KEY, getTimeOutPeriod());
        dest.writeBundle(bundle);
    }

    public static final Creator<TvToast> CREATOR
            = new Creator<TvToast>() {

        public TvToast createFromParcel(Parcel in) {
            return new TvToast(in);
        }

        public TvToast[] newArray(int size) {
            return new TvToast[size];
        }
    };

    private TvToast(Parcel in) {
        Bundle bundle = in.readBundle();
        setIconRes(bundle.getInt(ICON_RES_KEY));
        setMessage(bundle.getString(MESSAGE_KEY));
        setIcon((Bitmap)bundle.getParcelable(ICON_DRAWABLE_KEY));
        isFocusable = bundle.getBoolean(IS_FOCUSABLE_KEY);
        isPersistent = bundle.getBoolean(IS_PERSISITENT_KEY);
        timeOutPeriod = bundle.getInt(TIME_OUT_KEY);
    }

    @Override
    public boolean equals(Object o) {
        boolean lEqual = false;

        Log.d(TAG, "Compare obj1: " + o + " obj2: " + this);

        if(o instanceof TvToast) {
            TvToast msg = (TvToast) o;
            if(msg.isFocusable() == isFocusable()
                    && msg.isPersistent() == isPersistent()
                    && msg.getTimeOutPeriod() == getTimeOutPeriod()
                    && msg.getIconRes() == getIconRes()
                    && msg.getMessage().compareTo(getMessage()) == 0 ) {
                // Cannot compare the bitmap objects directly as they're 2 diff objects but with same parameters
                // And bitmaps could be null (, i.e., given as res ID, in most cases,) so need to do null checks
                Bitmap b1 = msg.getIcon() != null ? msg.getIcon():
                        getIcon() != null? getIcon(): null;
                Bitmap b2 = b1 == msg.getIcon()? getIcon(): msg.getIcon();
                if(b1!=null) {
                    lEqual = b1.sameAs(b2);
                } else {
                    lEqual = true;
                }
            }
        }

        Log.d(TAG, "is equal: " + lEqual);
        return lEqual;
    }

    @Override
    public int hashCode() {
        // instances of this class are not expected to be put into a hashmap or hashtree
        return ARBITARY_HASHCODE_VALUE;
    }

    @Override
    public String toString() {
        return "TvToast, isFocusable: " + isFocusable() + " isPersistent: " +
                isPersistent() + " getTimeOutPeriod: " + getTimeOutPeriod() +
                " getIconRes: " + getIconRes() + " message: " +
                getMessage() + " icon: " + getIcon();
    }
}

