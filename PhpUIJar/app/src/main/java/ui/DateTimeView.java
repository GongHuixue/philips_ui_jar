package ui;

/**
 * Created by huixue.gong on 2017/7/12.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeView extends TextView {
    private static final String TAG = DateTimeView.class.getSimpleName();

    private Calendar mCalendar;
    private String mFormat;
    private boolean mLive = true;
    private boolean mAttached;
    private Context mContext;
    private StringBuffer mDateBuffer = new StringBuffer("");
    private String mDate = null;
    private final static String M24 = "kk:mm";

    private static final int MSG_TIMEZONE_CHANGED = 1;
    private static final int MSG_DATE_CHANGED = 0;
    private static final int MSG_TIME_CHANGED = 2;
    private static final int MSG_TIME_TICK = 3;

    /* called by system on minute ticks */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage msg.what = " + msg.what);
            switch (msg.what) {
                case MSG_DATE_CHANGED:
                    break;
                case MSG_TIME_CHANGED:
                    break;
                case MSG_TIME_TICK:
                    updateTime();
                    break;
                case MSG_TIMEZONE_CHANGED:
                    break;
                default:
                    updateTime();
                    break;
            }
        }
    };

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: Intent action: " + intent.getAction());

            Message msg = mHandler.obtainMessage();
            if (mLive) {
                if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                    if (!mHandler.hasMessages(MSG_TIMEZONE_CHANGED)) {
                        msg.what = MSG_TIMEZONE_CHANGED;
                        mHandler.sendMessageDelayed(msg, 500);
                    }
                } else if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) { // listens for the date changed
                    if (!mHandler.hasMessages(MSG_DATE_CHANGED)) {
                        msg.what = MSG_DATE_CHANGED;
                        mHandler.sendMessageDelayed(msg, 500);
                    }
                } else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) { // listens for the date changed
                    if (!mHandler.hasMessages(MSG_TIME_CHANGED)) {
                        msg.what = MSG_TIME_CHANGED;
                        mHandler.sendMessageDelayed(msg, 500);
                    }
                } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) { // listens for the date changed
                    if (!(mHandler.hasMessages(MSG_TIMEZONE_CHANGED) || mHandler.hasMessages(MSG_DATE_CHANGED) || mHandler.hasMessages(MSG_TIME_CHANGED))) {
                        msg.what = MSG_TIME_TICK;
                        mHandler.sendMessageDelayed(msg, 500);
                    }
                }
            }
        }
    };


    public DateTimeView(Context context) {
        this(context, null);
        Log.d(TAG, "DateTimeView(Context context) ");
    }

    public DateTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "DateTimeView(Context context, AttributeSet attrs) ");
        mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mAttached) {
            return;
        }
        mAttached = true;
        mCalendar = Calendar.getInstance();
        setDateFormat();

        if (mLive) {
            /* monitor time ticks, time changed, timezone */
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_DATE_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            mContext.registerReceiver(mIntentReceiver, filter);
        }
        updateTime();
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.d(TAG,"onDetachedFromWindow enter");
        super.onDetachedFromWindow();

        if (!mAttached) {
            return;
        }
        mAttached = false;

        if (mLive) {
            mContext.unregisterReceiver(mIntentReceiver);
        }
        mHandler.removeCallbacksAndMessages(null);
        Log.d(TAG,"onDetachedFromWindow exit");
    }

    void updateTime(Calendar c) {
        mCalendar = c;
        updateTime();
    }

    void updateTime() {
        /*mCalendar.setTimeInMillis(System.currentTimeMillis());

        //set the date also
        setDateFormat();
        // changing the order
        mDateBuffer.setLength(0);// clearing the string buffer

        CharSequence newTime = DateFormat.format(mFormat, mCalendar);
        mDateBuffer.append(newTime);
        mDateBuffer.append(" ");

        mDateBuffer.append(getDate());
        Log.i(TAG, "updateTime: Text: " + mDateBuffer.toString());
        setText(mDateBuffer.toString());*/

        setDateFormat();

        mDateBuffer.setLength(0);
        mDateBuffer.append(getDate());
        Log.i(TAG, "updateTime: Text: " + mDateBuffer.toString());
        setText(mDateBuffer.toString());
    }

    private void setDateFormat() {
        mFormat = M24;

        /*SimpleDateFormat sdf = new SimpleDateFormat("cccc, dd LLL",
                getResources().getConfiguration().getLocales().get(0));
        mDate = sdf.format(new Date());*/

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        mDate = sdf.format(new Date());
        Log.d(TAG, "setDateFormat mDate = " + mDate);
    }


    private String getDate() {
        return mDate;
    }

    void setLive(boolean live) {
        mLive = live;
    }
}
