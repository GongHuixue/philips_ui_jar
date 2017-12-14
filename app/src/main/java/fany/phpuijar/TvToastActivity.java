package fany.phpuijar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ui.tvtoast.*;
import ui.tvtoast.TvToast;
import ui.tvtoast.TvToastMessenger;

public class TvToastActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = TvToastActivity.class.getSimpleName();
    private Button timeOutSI;
    /*private Button keyPressSI;
    private Button permanentSI;
    private Button timeOutSILong;
    private Button keyPressSILong;
    private Button permanentSILong;

    private Button timeOutFlash;
    private Button keyPressFlash;
    private Button permanentFlash;
    private Button timeOutFlashLong;
    private Button keyPressFlashLong;
    private Button permanentFlashLong*/;

    private Button timeOutCancel;
    private Button permanentCancel;
    private Button keyPressCancel;

    private boolean isPushMessageShown = false;
    TvToastMessenger messenger;
    TvToast timeOutTvToast, keyPressTvToast, permanentTvToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvtoast);

        Log.d(TAG, "TvToastActivity Enter");

        timeOutSI = (Button) findViewById(R.id.timeOutStatusIndicator);
        /*keyPressSI = (Button) findViewById(R.id.keyPressStatusIndicator);
        permanentSI = (Button) findViewById(R.id.permanentStatusIndicator);
        timeOutSILong = (Button) findViewById(R.id.timeOutStatusIndicatorLong);
        keyPressSILong = (Button) findViewById(R.id.keyPressStatusIndicatorLong);
        permanentSILong = (Button) findViewById(R.id.permanentStatusIndicatorLong);

        timeOutFlash = (Button) findViewById(R.id.timeOutFlashMsg);
        keyPressFlash = (Button) findViewById(R.id.keyPressFlashMsg);
        permanentFlash = (Button) findViewById(R.id.permanentFlashMsg);
        timeOutFlashLong = (Button) findViewById(R.id.timeOutFlashMsgLong);
        keyPressFlashLong = (Button) findViewById(R.id.keyPressFlashMsgLong);
        permanentFlashLong = (Button) findViewById(R.id.permanentFlashMsgLong);*/

        timeOutCancel = (Button) findViewById(R.id.cancelTimeOutMsg);
        permanentCancel = (Button) findViewById(R.id.cancelPermanentMsg);
        keyPressCancel = (Button) findViewById(R.id.cancelKeyPressMsg);

        timeOutSI.setOnClickListener(this);
        /*permanentSI.setOnClickListener(this);
        keyPressSI.setOnClickListener(this);
        timeOutSILong.setOnClickListener(this);
        permanentSILong.setOnClickListener(this);
        keyPressSILong.setOnClickListener(this);

        timeOutFlash.setOnClickListener(this);
        permanentFlash.setOnClickListener(this);
        keyPressFlash.setOnClickListener(this);
        timeOutFlashLong.setOnClickListener(this);
        permanentFlashLong.setOnClickListener(this);
        keyPressFlashLong.setOnClickListener(this);*/

        timeOutCancel.setOnClickListener(this);
        keyPressCancel.setOnClickListener(this);
        permanentCancel.setOnClickListener(this);

        messenger = TvToastMessenger.getInstance(this);

        timeOutTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_TIME_OUT, "", -1);
        keyPressTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_KEY_PRESS, "", -1);
        permanentTvToast = TvToastMessenger.makeTvToastMessage(TvToastMessenger.TYPE_PERMANENT, "", -1);

        Log.d(TAG, "TvToastActivity Exit");
    }


    @Override
    protected void onPause() {
//	messenger.cancelAllTvToastMessage();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        TvToast tvToast = null;
        Log.d(TAG, "TvToastActivity onClick viewid = " + view.getId());

        switch (view.getId()) {

            case R.id.timeOutStatusIndicator:
                timeOutTvToast.setMessage(getResources().getString(R.string.timeOutMsg));
                //timeOutTvToast.setIconRes(org.droidtv.ui.tvwidget2k15.R.drawable.add_to_favourite_hl_ico_20x15_225);
                timeOutTvToast.setIcon(null);
                tvToast = timeOutTvToast;
                break;

            /*case R.id.permanentStatusIndicator:
                permanentTvToast.setMessage(getResources().getString(R.string.permanentMsg));
                //permanentTvToast.setIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.icon_info)).getBitmap());
                permanentTvToast.setIconRes(0);
                tvToast = permanentTvToast;
                break;

            case R.id.keyPressStatusIndicator:
                keyPressTvToast.setMessage(getResources().getString(R.string.keyPressMsg));
                //keyPressTvToast.setIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.icon_warning)).getBitmap());
                keyPressTvToast.setIconRes(0);
                tvToast = keyPressTvToast;
                break;

            case R.id.timeOutStatusIndicatorLong:
                timeOutTvToast.setMessage(getResources().getString(R.string.timeOutMsgLong));
                //timeOutTvToast.setIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.icon_info)).getBitmap());
                timeOutTvToast.setIconRes(0);
                tvToast = timeOutTvToast;
                break;

            case R.id.permanentStatusIndicatorLong:
                permanentTvToast.setMessage(getResources().getString(R.string.permanentMsgLong));
                //permanentTvToast.setIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.icon_network_lost)).getBitmap());

                permanentTvToast.setIconRes(0);
                tvToast = permanentTvToast;
                break;

            case R.id.keyPressStatusIndicatorLong:
                keyPressTvToast.setMessage(getResources().getString(R.string.keyPressMsgLong));
                //keyPressTvToast.setIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.icon_warning)).getBitmap());
                keyPressTvToast.setIconRes(0);
                tvToast = keyPressTvToast;
                break;

            case R.id.timeOutFlashMsg:
                timeOutTvToast.setMessage(getResources().getString(R.string.timeOutMsg));
                timeOutTvToast.setIconRes(-1);
                timeOutTvToast.setIcon(null);
                tvToast = timeOutTvToast;
                break;

            case R.id.permanentFlashMsg:
                permanentTvToast.setMessage(getResources().getString(R.string.permanentMsg));
                permanentTvToast.setIconRes(-1);
                permanentTvToast.setIcon(null);
                tvToast = permanentTvToast;
                break;

            case R.id.keyPressFlashMsg:
                keyPressTvToast.setMessage(getResources().getString(R.string.keyPressMsg));
                keyPressTvToast.setIconRes(-1);
                keyPressTvToast.setIcon(null);
                tvToast = keyPressTvToast;
                break;

            case R.id.timeOutFlashMsgLong:
                timeOutTvToast.setMessage(getResources().getString(R.string.timeOutMsgLong));
                timeOutTvToast.setIconRes(-1);
                timeOutTvToast.setIcon(null);
                tvToast = timeOutTvToast;
                break;

            case R.id.permanentFlashMsgLong:
                permanentTvToast.setMessage(getResources().getString(R.string.permanentMsgLong));
                permanentTvToast.setIconRes(-1);
                permanentTvToast.setIcon(null);
                tvToast = permanentTvToast;
                break;

            case R.id.keyPressFlashMsgLong:
                keyPressTvToast.setMessage(getResources().getString(R.string.keyPressMsgLong));
                keyPressTvToast.setIconRes(-1);
                keyPressTvToast.setIcon(null);
                tvToast = keyPressTvToast;
                break;*/

            case R.id.cancelKeyPressMsg:
                messenger.cancelTvToastMessage(keyPressTvToast);
                return;

            case R.id.cancelPermanentMsg:
                messenger.cancelTvToastMessage(permanentTvToast);
                return;

            case R.id.cancelTimeOutMsg:
                messenger.cancelTvToastMessage(timeOutTvToast);
                return;
        }
        Log.d(TAG, "TvToastActivity  showTvToastMessage");
        messenger.showTvToastMessage(tvToast);

    }

}
