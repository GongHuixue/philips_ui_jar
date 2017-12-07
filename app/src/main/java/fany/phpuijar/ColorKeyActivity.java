package fany.phpuijar;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import ui.ColorKeys;
import ui.ColorKeys.IColorKeyCallback;

public class ColorKeyActivity extends Activity implements View.OnClickListener {
    private static final String TAG = ColorKeyActivity.class.getSimpleName();
    private ColorKeys mColorKeys;
    private CheckBox cbRed, cbGreen, cbYellow, cbBlue;
    private EditText etRed, etGreen, etYellow, etBlue;
    private static boolean mFlag = false;
    private Button btnRed, btnGreen, btnYellow, btnBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Enter");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_key);

        mColorKeys = findViewById(R.id.color_key);
        initColorKeyUI();

        Log.d(TAG, "OnCreate Exit");
    }

    private void initColorKeyUI() {
        cbRed = findViewById(R.id.red_cb);
        cbGreen = findViewById(R.id.green_cb);
        cbYellow = findViewById(R.id.yello_cb);
        cbBlue = findViewById(R.id.blue_cb);

        etRed = findViewById(R.id.et_red_text);
        etGreen = findViewById(R.id.et_green_text);
        etYellow = findViewById(R.id.et_yellow_text);
        etBlue = findViewById(R.id.et_blue_text);

        btnRed = findViewById(R.id.red_submit_btn);
        btnGreen = findViewById(R.id.green_submit_btn);
        btnYellow = findViewById(R.id.yellow_btn);
        btnBlue = findViewById(R.id.blue_btn);

        btnRed.setOnClickListener(this);
        btnGreen.setOnClickListener(this);
        btnYellow.setOnClickListener(this);
        btnBlue.setOnClickListener(this);

        restore();

        mColorKeys.setColorKeyBarVisibility(true);
        mColorKeys.setTimeBarVisibility(true);

        /*hide some color key*/
        findViewById(R.id.hide_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlag = !mFlag;
                if (cbRed.isChecked()) {
                    mColorKeys.setRedLabel("");
                }
                if (cbGreen.isChecked()) {
                    mColorKeys.setGreenLabel("");
                }
                if (cbYellow.isChecked()) {
                    mColorKeys.setYellowLabel("");
                }
                if (cbBlue.isChecked()) {
                    mColorKeys.setBlueLabel("");
                }
            }
        });

        /*display color key*/
        findViewById(R.id.appear_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restore();
            }
        });

        /*hide or display color key*/
        findViewById(R.id.color_key_animate_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlag = !mFlag;
                Log.d(TAG, "mFlag is " + mFlag);
                mColorKeys.setColorKeyBarVisibility(mFlag);
            }
        });

        mColorKeys.registerCallback(new IColorKeyCallback() {

            @Override
            public boolean onYellowKeyPressed() {
                Log.e(TAG, "Yellow Key Pressed");
                Toast.makeText(ColorKeyActivity.this, "Yellow Key Pressed", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onRedKeyPressed() {
                // TODO Auto-generated method stub
                Log.e(TAG, "Red Key Pressed");
                Toast.makeText(ColorKeyActivity.this, "Red Key Pressed", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onGreenKeyPressed() {
                // TODO Auto-generated method stub
                Log.e(TAG, "Green Key Pressed");
                Toast.makeText(ColorKeyActivity.this, "Green Key Pressed", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onBlueKeyPressed() {
                // TODO Auto-generated method stub
                Log.e(TAG, "Blue Key Pressed");
                Toast.makeText(ColorKeyActivity.this, "Blue Key Pressed", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mColorKeys.setColorKeyBarVisibility(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mColorKeys.unRegisterCallback();
    }

    protected void restore() {
        mColorKeys.setRedLabelId(R.string.Red);
        mColorKeys.setBlueLabelId(R.string.Blue);
        mColorKeys.setYellowLabelId(R.string.Yellow);
        mColorKeys.setGreenLabelId(R.string.Green);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.red_submit_btn:
                mColorKeys.setRedLabel(etRed.getText().toString());
                break;
            case R.id.green_submit_btn:
                mColorKeys.setGreenLabel(etGreen.getText().toString());
                break;
            case R.id.yellow_btn:
                mColorKeys.setYellowLabel(etYellow.getText().toString());
                break;
            case R.id.blue_btn:
                mColorKeys.setBlueLabel(etBlue.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(this, "On Config Changed", Toast.LENGTH_SHORT).show();
        if (mColorKeys != null)
            mColorKeys.refreshColorKeyBarOnConfigChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        boolean lHandled = mColorKeys.handleKeyDown(keyCode);
        if (!lHandled) {
            lHandled = super.onKeyDown(keyCode, event);
        }
        return lHandled;
    }
}