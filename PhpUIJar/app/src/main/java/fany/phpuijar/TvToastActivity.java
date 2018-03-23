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
    private Button timeOutSI, keyPressSI, permanentSI;
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
        keyPressSI = (Button) findViewById(R.id.keyPressStatusIndicator);
        permanentSI = (Button)findViewById(R.id.permanentStatusIndicator);

        timeOutCancel = (Button) findViewById(R.id.cancelTimeOutMsg);
        permanentCancel = (Button) findViewById(R.id.cancelPermanentMsg);
        keyPressCancel = (Button) findViewById(R.id.cancelKeyPressMsg);

        timeOutSI.setOnClickListener(this);
        keyPressSI.setOnClickListener(this);
        permanentSI.setOnClickListener(this);
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
                timeOutTvToast.setIconRes(R.drawable.add_to_favourite_hl_ico_20x15_225);
                timeOutTvToast.setIcon(null);
                tvToast = timeOutTvToast;
                break;

            case R.id.keyPressStatusIndicator:
                keyPressTvToast.setMessage(getResources().getString(R.string.keyPressMsg));
                keyPressTvToast.setIconRes(R.drawable.add_to_favourite_hl_ico_20x15_225);
                keyPressTvToast.setIcon(null);
                tvToast = keyPressTvToast;
                break;

            case R.id.permanentStatusIndicator:
                permanentTvToast.setMessage(getResources().getString(R.string.permanentMsg));
                permanentTvToast.setIconRes(R.drawable.add_to_favourite_hl_ico_20x15_225);
                permanentTvToast.setIcon(null);
                tvToast = permanentTvToast;
                break;

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
