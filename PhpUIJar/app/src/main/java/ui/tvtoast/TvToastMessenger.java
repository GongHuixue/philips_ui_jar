package ui.tvtoast;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by huixue.gong on 2017/12/8.
 */

public final class TvToastMessenger {

    private static final String TAG = "TvToastMessenger";

    public static final int TYPE_PERMANENT = 0;
    public static final int TYPE_KEY_PRESS = 1;
    public static final int TYPE_TIME_OUT = 2;

    private static final int MAX_QUEUE_CAPACITY = 2;
    private static final int QUEUE_HEAD = 0;

    private static final String TV_TOAST_SERVICE_INTENT = "android.intent.action.START_TV_TOAST_SERVICE";

    private static Map<Context, TvToastMessenger> mMessengerInstances = new WeakHashMap<Context, TvToastMessenger>();

    private WeakReference<Context> mWeakContext;
    private IBinder mContextToken;
    private ITvToastService mTvToastMessageService = null;
    private boolean mBoundToTvToastService = false;

    private TvToast[] mMessageQueue = new TvToast[MAX_QUEUE_CAPACITY];

    /*
     * Tv Toast Service connection
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "TvToastMessenger ---- onServiceDisconnected: " + name);
            mTvToastMessageService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "TvToastMessenger ---- onServiceConnected: " + name);
            mTvToastMessageService = ITvToastService.Stub.asInterface(service);
            showAllFromQueue();
        }

    };

    /**
     * get Tv Toast message type
     *
     * @param msg
     * @return type
     */
    public int getMessageType(TvToast msg) {
        if (msg == null) {
            return -1;
        }

        if (msg instanceof KeyPressTvToastMessage) {
            return TYPE_KEY_PRESS;
        } else if (msg instanceof PermanentTvToastMessage) {
            return TYPE_PERMANENT;
        } else {
            return TYPE_TIME_OUT;
        }
    }

    /*
     * pick all msgs fom queue and show
     */
    private synchronized void showAllFromQueue() {
        for (int i = mMessageQueue.length - 1; i >= QUEUE_HEAD; i--) {
            TvToast msg = mMessageQueue[i];
            if (msg != null) {
                show(msg);
            }
        }
    }

    /*
     * pick msg fom queue head and show
     */
    private synchronized void showQueueHead() {
        show(mMessageQueue[QUEUE_HEAD]);
    }

    /*
     * enqueue msg
     */
    private synchronized void enqueue(TvToast msg) {
        TvToast headMsg = dequeue();
        if (getMessageType(headMsg) == TYPE_PERMANENT) {
            /*
             * ----- current msg [persistent]
			 */
            mMessageQueue[QUEUE_HEAD + 1] = headMsg;
        } else if (getMessageType(msg) == TYPE_PERMANENT &&
                getMessageType(mMessageQueue[QUEUE_HEAD + 1]) == TYPE_PERMANENT) {
			/*
			 * ----- incoming msg [persistent] && stashed msg [persistent]
			 */
            mMessageQueue[QUEUE_HEAD + 1] = null;
        }

        mMessageQueue[QUEUE_HEAD] = msg;
    }

    /*
     * dequeue msg from HEAD
     */
    private synchronized TvToast dequeue() {
        TvToast msg = mMessageQueue[QUEUE_HEAD];
        mMessageQueue[QUEUE_HEAD] = null;
        return msg;
    }

    /*
     * dequeue particular msg
     */
    private synchronized TvToast dequeue(TvToast msg) {
        for (int i = QUEUE_HEAD; i < mMessageQueue.length; i++) {
            if (mMessageQueue[i] != null && mMessageQueue[i].equals(msg)) {
                mMessageQueue[i] = null;
                return msg;
            }
        }

        return null;
    }

    /*
     * show Tv Toast msg
     */
    private void show(TvToast msg) {
        try {
            if (msg != null) {
                mTvToastMessageService.showTvToastMessage(mContextToken, msg);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "error in showing push message: " + e);
        }
    }

    /*
     * cancel Tv Toast msg
     */
    private void cancel(TvToast msg) {
        try {
            if (msg != null) {
                mTvToastMessageService.cancelTvToastMessage(mContextToken, msg);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "error in cancelling push message: " + e);
        }
    }

    private TvToastMessenger(Context context) {
		/*
		 * Start service if not started
		 */
        Intent serviceIntent = new Intent(TV_TOAST_SERVICE_INTENT);
        PackageManager pm = context.getPackageManager();
        ResolveInfo si = pm.resolveService(serviceIntent, 0);
        serviceIntent.setClassName(si.serviceInfo.packageName, si.serviceInfo.name);
        Log.d(TAG, "TvToastMessenger ---- service intent :- package: " + si.serviceInfo.packageName + " class: " + si.serviceInfo.name);

        ComponentName service = context.getApplicationContext().startService(serviceIntent);
        Log.d(TAG, "TvToastMessenger ---- service_started: " + service);
				
		/*
		 * Save context as weak refrence to unbind from service when memory is low.
		 */
        mWeakContext = new WeakReference<Context>(context);

        /*
         * Create context token
         */
        mContextToken = new Binder();

		/*
		 * Bind to service
		 */
        if (!mBoundToTvToastService) {
            bindTvToastService();
        }
    }

    /*
     * Bind if not already bound
     */
    private void bindTvToastServiceIfNotBound() {
        if (!mBoundToTvToastService) {
            bindTvToastService();
        }
    }

    /*
     * Bind to service
     */
    private void bindTvToastService() {
        Log.d(TAG, "bindTvToastService");
        Context context = mWeakContext.get();
        if (context != null) {
            Intent serviceIntent = new Intent(TV_TOAST_SERVICE_INTENT);
            PackageManager pm = context.getPackageManager();
            ResolveInfo si = pm.resolveService(serviceIntent, 0);
            serviceIntent.setClassName(si.serviceInfo.packageName, si.serviceInfo.name);
            boolean connected = context.getApplicationContext().bindService(serviceIntent, mConnection, 0);
            Log.d(TAG, "TvToastMessenger ---- service_connected: " + connected);
        }
        mBoundToTvToastService = true;
    }

    /**
     * create {@link TvToastMessenger} instance associated with particular context i.e. activity/service
     *
     * @param context
     * @return {@link TvToastMessenger}
     */
    public static TvToastMessenger getInstance(Context context) {
        Log.d(TAG, "getInstance ---- enter ---- context: " + context);
        TvToastMessenger messenger = mMessengerInstances.get(context);

        // make new instance for the context
        if (messenger == null) {
            messenger = new TvToastMessenger(context);
            mMessengerInstances.put(context, messenger);
        }

        Log.d(TAG, "getInstance ---- enter ---- messenger_queue: " + mMessengerInstances);
        Log.d(TAG, "getInstance ---- enter ---- messenger: " + messenger);
        return messenger;
    }

    /**
     * create Tv Toast Message
     *
     * @param type
     * @param msg
     * @param iconResId
     * @return {@link TvToast}
     */
    public static TvToast makeTvToastMessage(int type, String msg, int iconResId) {
        TvToast tvToastMsg;

        switch (type) {
            case TYPE_KEY_PRESS:
                tvToastMsg = new KeyPressTvToastMessage();
                break;

            case TYPE_PERMANENT:
                tvToastMsg = new PermanentTvToastMessage();
                break;

            case TYPE_TIME_OUT:
                tvToastMsg = new TimeOutTvToastMessage();
                break;

            default:
                tvToastMsg = new TimeOutTvToastMessage();
                break;
        }

        tvToastMsg.setIconRes(iconResId);
        tvToastMsg.setMessage(msg);

        return tvToastMsg;
    }

    /**
     * create Tv Toast Message
     *
     * @param type
     * @param msg
     * @param icon
     * @return {@link TvToast}
     */
    public static TvToast makeTvToastMessage(int type, String msg, BitmapDrawable icon) {
        TvToast tvToastMsg;

        switch (type) {
            case TYPE_KEY_PRESS:
                tvToastMsg = new KeyPressTvToastMessage();
                break;

            case TYPE_PERMANENT:
                tvToastMsg = new PermanentTvToastMessage();
                break;

            case TYPE_TIME_OUT:
                tvToastMsg = new TimeOutTvToastMessage();
                break;

            default:
                tvToastMsg = new TimeOutTvToastMessage();
                break;
        }

        tvToastMsg.setIcon(icon.getBitmap());
        tvToastMsg.setMessage(msg);

        return tvToastMsg;
    }

    /**
     * Show a particular Tv Toast Message
     *
     * @param msg
     */
    public void showTvToastMessage(TvToast msg) {
        enqueue(msg);
        bindTvToastServiceIfNotBound();

        if (mTvToastMessageService != null) {
            showQueueHead();
        }
        Log.d(TAG, "showTvToastMessage: msg type: " + getMessageType(msg) + " msg: " + msg);
    }

    /**
     * Cancel a particular Tv Toast Message
     *
     * @param msg
     */
    public void cancelTvToastMessage(TvToast msg) {
        TvToast removedMsg = dequeue(msg);
        bindTvToastServiceIfNotBound();

        if (mTvToastMessageService != null) {
            cancel(removedMsg);
        }
        Log.d(TAG, "cancelTvToastMessage: msg type: " + getMessageType(removedMsg) + " msg: " + removedMsg);
    }

    /**
     * Cancel All Tv Toasts messages sent by this {@link TvToastMessenger} instance, associated with particular context {@link Context}
     * Used by {@link Activity}, and {@link Service} to cancel all Tv Toast sent by them when they're going into background state.
     */
    public synchronized void cancelAllTvToastMessage() {
        bindTvToastServiceIfNotBound();


        for (int i = QUEUE_HEAD; i < mMessageQueue.length; i++) {
            TvToast removedMsg = dequeue(mMessageQueue[i]);
            if (mTvToastMessageService != null) {
                cancel(removedMsg);
            }
        }
        Log.d(TAG, "cancelAllTvToastMessage");
    }

    /**
     * Permanent type Tv Toast
     *
     * @author savan.kiran
     */
    private static class PermanentTvToastMessage extends TvToast {

        public PermanentTvToastMessage() {
            super();
            setFocusable(false);
            setPersistent(true);
            setTimeOutPeriod(TIME_OUT_DURATION_INFINITE);
        }

    }

    /**
     * Key press type Tv Toast
     *
     * @author savan.kiran
     */
    private static class KeyPressTvToastMessage extends TvToast {

        public KeyPressTvToastMessage() {
            super();
            setFocusable(true);
            setPersistent(false);
            setTimeOutPeriod(TIME_OUT_DURATION_INFINITE);
        }

    }

    /**
     * Time Out type Tv Toast
     *
     * @author savan.kiran
     */
    private static class TimeOutTvToastMessage extends TvToast {

        public TimeOutTvToastMessage() {
            super();
            setFocusable(false);
            setPersistent(false);
            setTimeOutPeriod(TIME_OUT_DURATION_REGULAR);
        }
    }
}

