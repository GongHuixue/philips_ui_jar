package fany.phpuijar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ui.PickList;
import ui.PickList.IPickListCancelListener;
import ui.PickList.IPickListUnHandledKeyListener;
import ui.PickList.pickListItemSelectedListener;

public class PickListActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PickListActivity.class.getSimpleName();
    private PickList mPickList = null;
    private int mSelectedPosition = 0;
    private TextView mSelectedText;
    private EditText etTitle;
    private String mTitleText = "Select Day";

    private List<String> mLabelList = new ArrayList<String>();
    private String[] mStringLableAttr;
    private Drawable[] mDrawablesArray;
    private RadioButton rbSimple, rbRecommender;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_list);
        mSelectedText = (TextView) findViewById(R.id.tv_selected_item);
        etTitle = (EditText) findViewById(R.id.et_title_text);
        mPickList = new PickList(this);
        rbSimple = (RadioButton) findViewById(R.id.rb_simple);
        rbRecommender = (RadioButton) findViewById(R.id.rb_recommender);
        imageView = (ImageView) findViewById(R.id.tv_selected_image);
        mStringLableAttr = new String[]{"Moday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        mLabelList.addAll(Arrays.asList(mStringLableAttr));
        mDrawablesArray = new Drawable[]{ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null)};
        mPickList.setArray(mStringLableAttr);

        mPickList.setPickListListner(mPiItemSelectedListener);
        mPickList.setPickListCancelListener(new IPickListCancelListener() {
            @Override
            public void onPickListCancelled() {
                Toast.makeText(PickListActivity.this, "Pick list closed", Toast.LENGTH_SHORT).show();
            }
        });
        mPickList.setPickListUnHandledKeyListner(new IPickListUnHandledKeyListener() {
            @Override
            public boolean dispatchUnhandledKeys(KeyEvent event) {
                return false;
            }
        });

        rbSimple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && mPickList != null) {
                    mPickList.setArray(mStringLableAttr);
                }
            }
        });

        rbRecommender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && mPickList != null) {
                    mPickList.setArray(mStringLableAttr, mDrawablesArray);
                    mPickList.clearChoices();
                }
            }
        });

        ((CheckBox) findViewById(R.id.cb_simple)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPickList.enableDiming(!isChecked);
                if (isChecked) {
                    buttonView.setText("Enable dimming");
                } else {
                    buttonView.setText("Disable dimming");
                }
            }
        });

        mPickList.setAvailabilityControllabiltyListener(new PickListImpl());

        findViewById(R.id.btn_picklist_show).setOnClickListener(this);

        initializePickList(true);
    }

    private void initializePickList(boolean wantToSelected) {
        if (mPickList != null) {
            mPickList.setTitleText(mTitleText);
            mPickList.show();
            if (wantToSelected) {
                mPickList.setFocusPosition(mSelectedPosition);
            }
            mPickList.setCheckedPosition(mSelectedPosition);
        }
    }

    private pickListItemSelectedListener mPiItemSelectedListener = new pickListItemSelectedListener() {
        @Override
        public void onItemPicked(String[] array, int position, boolean selected) {
            mSelectedPosition = position;
            if (array != null && array.length > position) {
                mSelectedText.setText("Selected Item: " + array[position]);
            }
        }
    };

    private class PickListImpl extends PickList.AvailabilityControllability {
        public PickListImpl() {
            mPickList.super();
        }

        @Override
        public boolean getItemAvailability(int index) {
            if (index == 3 || index == 4) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public boolean getItemControllability(int index) {
            if (index != 2)
                return true;
            else
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }
}
