package fany.phpuijar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class ProgressBarActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ProgressBarActivity.class.getSimpleName();
    private ProgressBar mFirstPb, mSecondPb, mThirdPb, mRingPb;
    private EditText etProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "ProgressBarActivity Enter");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bar);

        mRingPb = (ProgressBar) findViewById(R.id.ring_pb);
        mFirstPb = (ProgressBar) findViewById(R.id.first_progress);
        mSecondPb = (ProgressBar) findViewById(R.id.second_progress);
        mThirdPb = (ProgressBar) findViewById(R.id.third_progress);

        etProgress = (EditText) findViewById(R.id.edit_text);

        findViewById(R.id.btn_second_progress_decrement).setOnClickListener(this);
        findViewById(R.id.btn_second_progress_increment).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int mProgress;
        int mFactor = 1;

        if (etProgress.getText().toString().trim().length() > 0) {
            mFactor = Integer.parseInt(etProgress.getText().toString().trim());
        } else {
            mFactor = 1;
        }

        switch (view.getId()) {
            case R.id.btn_second_progress_decrement:
                mProgress = mFirstPb.getProgress();
                mProgress -= mFactor;

                mRingPb.setProgress(mProgress);
                mFirstPb.setProgress(mProgress);
                mSecondPb.setProgress(mProgress);
                mThirdPb.setProgress(mProgress);
                break;
            case R.id.btn_second_progress_increment:
                mProgress = mFirstPb.getProgress();
                mProgress += mFactor;

                mRingPb.setProgress(mProgress);
                mFirstPb.setProgress(mProgress);
                mSecondPb.setProgress(mProgress);
                mThirdPb.setProgress(mProgress);
                break;
            default:
                break;
        }
    }
}
