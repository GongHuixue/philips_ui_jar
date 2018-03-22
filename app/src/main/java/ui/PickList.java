package ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

import fany.phpuijar.R;

/**
 * Created by huixue.gong on 2017/12/22.
 */

public class PickList {
    private final static String TAG = PickList.class.getSimpleName();
    private static PickList singleInstance = null;
    private static Context mContext = null;
    private Dialog pickListDialog;
    private ListView lv;
    private String[] mCheckListArray;
    private String[] mAvailableListArray;
    private boolean[] mAvailability;
    private boolean[] mControllability;
    private Drawable[] mDrawables;
    private AdapterView.OnItemClickListener plItemclicked;
    private int mSelectedPos = -1;
    private boolean mSelected = false;
    private TextView mTitleText;
    private boolean mIsRequiredDim = true;
    private int mIconWidth, mIconHeight;
    private View mLastView;

    private PickListAdapter dataProvider;
    private pickListItemSelectedListener mListner;
    private IPickListCancelListener mPickListCancelListener;
    private IPickListUnHandledKeyListener mPickListUnHandledKeyListener;
    private AvailabilityControllability listener;

    public static synchronized PickList getSingleInstance(Context context) {
        if(singleInstance == null) {
            singleInstance = new PickList(context);
        }else if ( (mContext != null) && !mContext.equals(context) ) {
            mContext = context;
            singleInstance = new PickList(context);
        }
        return singleInstance;
    }

    public PickList(Context context) {
        LayoutInflater inflater = null;
        LinearLayout ll = null;
        mCheckListArray = null;
        mAvailableListArray = null;
        mDrawables = null;

        if(context != null) {
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            pickListDialog = new Dialog(context);
        }

        if(inflater != null) {
            View view = inflater.inflate(R.layout.picklist_layout, null);

            if(view instanceof LinearLayout) {
                ll = (LinearLayout) view;
                mTitleText = ll.findViewById(R.id.picklist_title_text_view);
                lv = ll.findViewById(R.id.picklist_list_view);
            }
        }

        // no title and title bar for this dialog
        if(pickListDialog!=null){
            pickListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            enableDiming(mIsRequiredDim);
            pickListDialog.setContentView(ll);

            pickListDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mTitleText.setText(null);

                    if (mPickListCancelListener != null) {
                        mPickListCancelListener.onPickListCancelled();
                    }
                }
            });

            pickListDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    boolean ifHandled = false;

                    if ((event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT)
                            || (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT)
                            || (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
                            || (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN)) {
                        // handled by child view npanel browser
                    } else if (mPickListUnHandledKeyListener != null) {
                        ifHandled = mPickListUnHandledKeyListener.dispatchUnhandledKeys(event);
                    }
                    return ifHandled;
                }
            });
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(pickListDialog.getWindow().getAttributes());

        lp.width = (int) (mContext.getResources().getDimension(R.dimen.picklist_01_container_width) + 0.5f);
        lp.height = (int) (mContext.getResources().getDimension(R.dimen.picklist_01_container_height) + 0.5f);
        // set the position of the dialog
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.y = mContext.getResources().getDimensionPixelSize(R.dimen.picklist_01_container_y1);
        lp.x = mContext.getResources().getDimensionPixelSize(R.dimen.picklist_01_container_x1);

        pickListDialog.getWindow().setAttributes(lp);
        pickListDialog.getWindow().getAttributes().windowAnimations = android.R.anim.slide_in_left;
        if( lv != null){
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    View checkedView = getViewAtPosition(mSelectedPos);
                    if (checkedView != null) {
                        TextView tempView = (TextView) checkedView.findViewById(R.id.text1);
                        updateTextView(tempView, false);
                    }
                    mSelectedPos = position;
                    mSelected = true;

                    if (mControllability[position]) {
                        new Handler().postDelayed(new Runnable() {

                            public void run() {
                                if (mListner != null) {
                                    mListner.onItemPicked(mAvailableListArray,  mSelectedPos, mSelected);
                                }
                                hide();
                            }
                        }, 300);
                    }
                }
            });

            lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                    if (mLastView != null && !mLastView.isActivated()) {
                        TextView tempView = (TextView) mLastView.findViewById(R.id.text1);
                        updateTextView(tempView, false);
                    }
                    if (view != null) {
                        mLastView = view;
                        TextView tempView = (TextView) view.findViewById(R.id.text1);
                        if (tempView != null) {
                            updateTextView(tempView, true);
                            if (tempView.getMeasuredWidth() < mContext.getResources().getDimensionPixelOffset(
                                    R.dimen.picklist_02_row01_width)) {
                                tempView.setFadingEdgeLength(0);
                                tempView.setEllipsize(TextUtils.TruncateAt.END);
                            } else {
                                tempView.setFadingEdgeLength(15);
                                tempView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            }
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
        mIconWidth = (int) mContext.getResources().getDimension(R.dimen.picklist_02_row01_icon_width);
        mIconHeight = (int) mContext.getResources().getDimension(R.dimen.picklist_02_row01_icon_height);
    }

    public View getViewAtPosition(int position) {
        if (lv != null) {
            final int firstVisiblePosition = lv.getFirstVisiblePosition();
            final int lastVisiblePosition = firstVisiblePosition + lv.getChildCount() - 1;
            if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
                final int childIndex = position - firstVisiblePosition;
                return lv.getChildAt(childIndex);
            }
        }
        return null;
    }

    private void updateTextView(TextView tv, boolean highlight) {
        if (tv != null) {
            Typeface tf = null;
            if (highlight) {
                tf = Typeface.create("sans-serif", Typeface.NORMAL);
            } else {
                tf = Typeface.create("sans-serif-light", Typeface.NORMAL);
            }
            tv.setTypeface(tf);
        }
    }


    public void setAvailabilityControllabiltyListener(AvailabilityControllability listener) {
        this.listener = listener;
    }

    public void enableDiming(boolean isRequiedDim) {
        this.mIsRequiredDim = isRequiedDim;
        if (pickListDialog != null) {
            Window window = pickListDialog.getWindow();

            window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            if (isRequiedDim) {
                window.setDimAmount(0.75f);
            } else {
                window.setDimAmount(0.0f);
            }
        }
    }

    public void setOnAvailabilityChanged(int index, boolean isAvailability) {
        if (mAvailability != null && mAvailability.length > index) {
            mAvailability[index] = isAvailability;
        }
        if (pickListDialog != null && pickListDialog.isShowing()) {
            dataProvider.notifyDataSetChanged();
        }
    }

    public void setOnControllabilityChanged(int index, boolean isContralable) {
        if (mControllability != null && mControllability.length > index) {
            mControllability[index] = isContralable;
        }
        if (pickListDialog != null && pickListDialog.isShowing()) {
            dataProvider.notifyDataSetChanged();
        }
    }

    public void show() {
        if (listener != null && mCheckListArray != null) {
            boolean[] controllability = new boolean[mCheckListArray.length];
            mAvailability = new boolean[mCheckListArray.length];
            Arrays.fill(controllability, true);
            int availableCount = 0;
            for (int i = 0; i < mCheckListArray.length; i++) {
                if (listener.getItemAvailability(i)) {
                    mAvailability[i] = true;
                    availableCount++;
                }
                controllability[i] = listener.getItemControllability(i);
            }

            mAvailableListArray = new String[availableCount];
            mControllability = new boolean[availableCount];
            Arrays.fill(mControllability, true);
            int index = 0;
            for (int i = 0; i < mCheckListArray.length; i++) {
                if (mAvailability[i]) {
                    mAvailableListArray[index] = mCheckListArray[i];
                    mControllability[index] = controllability[i];
                    index++;
                }
            }
        }
        if (dataProvider == null) {
            dataProvider = new PickListAdapter();
            lv.setAdapter(dataProvider);
        } else {
            dataProvider.notifyDataSetChanged();
        }
        pickListDialog.show();
    }

    public void hide() {
        pickListDialog.dismiss();
        mTitleText.setText(null);
    }

    /**
     * label of the pick list(basically a vertical text
     *
     * @param s
     *            String that is used for label
     */
    public void setTitleText(String s) {
        mTitleText.setText(s);

    }

    public void setLabel(String s) {
        setTitleText(s);
    }

    /**
     * Set the Focused position in the PickList This call is ignored if index is
     * greater than the item count
     *
     * @param index
     *            - Starting from 0
     */
    public void setFocusPosition(int index) {

        if (lv != null && lv.getAdapter() != null && index < lv.getAdapter().getCount()) {
            mSelectedPos = index;
            if (mSelectedPos < 0) {
                mSelected = false;
                lv.setSelected(false);
            } else {
                mSelected = true;
                lv.post(new Runnable() {

                    @Override
                    public void run() {
                        lv.setSelection(mSelectedPos);
                    }
                });
            }
        }
    }

    /**
     * Checks the item at the passes position Index. Clears the Check is index
     * is less than 0 This call is ignored if index is greater than the item
     * count
     *
     * @param index
     *            - starting from 0
     */
    public void setCheckedPosition(int index) {
        if (lv != null && lv.getAdapter() != null && index < lv.getAdapter().getCount()) {
            if (index < 0) {
                lv.clearChoices();
            } else {
                lv.setItemChecked(index, true);
            }
        }
    }

    public void setArray(Context c,String pickerArray[]) {
        setArray(pickerArray, null);
    }
    /**
     * Array of strings that gets displayed as part of the pick list data
     *
     * @param pickerArray
     */
    public void setArray(String pickerArray[]) {
        setArray(pickerArray, null);
    }

    public void setArray(String pickerArray[], Drawable[] drawables) {

        if (pickerArray != null) {
            mCheckListArray = pickerArray.clone();
            mAvailableListArray = pickerArray.clone();
            mControllability = new boolean[pickerArray.length];
        }

        mSelected = false;
        mSelectedPos = -1;
        if (drawables != null) {
            this.mDrawables = drawables.clone();
        } else {
            this.mDrawables = null;
        }

        Arrays.fill(mControllability, true);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        if (dataProvider == null) {
            dataProvider = new PickListAdapter();
            lv.setAdapter(dataProvider);
        } else {
            dataProvider.notifyDataSetChanged();
        }
    }

    /**
     * listener to the application to get callback on selected/Deselected item
     *
     * @param listener
     */
    public void setPickListListner(pickListItemSelectedListener listener) {
        mListner = listener;

    }

    public void setPickListCancelListener(IPickListCancelListener listner) {
        mPickListCancelListener = listner;
    }

    public void setPickListUnHandledKeyListner(IPickListUnHandledKeyListener listener) {
        mPickListUnHandledKeyListener = listener;
    }

    public void clearChoices() {
        lv.clearChoices();
        lv.setSelection(0);
    }

    private final class PickListAdapter extends BaseAdapter {

        private PickListAdapter() {

        }

        public int getCount() {
            return mAvailableListArray == null ? 0 : mAvailableListArray.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            CheckedTextView lcheckedTV = null;
            if (convertView == null) {
                convertView = View.inflate(mContext, R.layout.simple_list_item_activated_ticked, null);
                lcheckedTV = convertView.findViewById(R.id.text1);
                convertView.setTag(lcheckedTV);
            } else {
                lcheckedTV = (CheckedTextView) convertView.getTag();
            }

            lcheckedTV.setText(mAvailableListArray[position]);

            if (mDrawables != null) {
                mDrawables[position].setBounds(0, 0, mIconWidth, mIconHeight);
                ScaleDrawable sd = new ScaleDrawable(mDrawables[position], 0, mIconWidth, mIconHeight);
                lcheckedTV.setCompoundDrawables(sd.getDrawable(), null, null, null);
            }

            return convertView;
        }
    }

    /**
     * This is the interface for callback to receive the selected array, item
     * and the selection status
     *
     * @author sudhir.prabhu
     *
     */
    public interface pickListItemSelectedListener {
        void onItemPicked(String[] array, int position, boolean selected);
    }

    public interface IPickListCancelListener {
        void onPickListCancelled();
    }

    public interface IPickListUnHandledKeyListener {
        boolean dispatchUnhandledKeys(KeyEvent event);
    }

    public abstract class AvailabilityControllability {
        public abstract boolean getItemAvailability(int index);
        public abstract boolean getItemControllability(int index);
    }
}
