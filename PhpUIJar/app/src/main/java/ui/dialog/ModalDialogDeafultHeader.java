package ui.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fany.phpuijar.R;

/**
 * Created by huixue.gong on 2017/12/14.
 */

public class ModalDialogDeafultHeader extends ModalDialogBaseHeader {

    private View mView;
    private CharSequence mHeading;
    private CharSequence mSubheading;

    public void setupPanel() {
        super.setupPanel();
        if (TextUtils.isEmpty(mHeading)) {
            ((TextView) mView.findViewById(R.id.headingText)).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) mView.findViewById(R.id.headingText)).setText(mHeading);
            ((TextView) mView.findViewById(R.id.headingText)).setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(mSubheading)) {
            ((TextView) mView.findViewById(R.id.subHeadingText)).setVisibility(View.GONE);
        } else {
            ((TextView) mView.findViewById(R.id.subHeadingText)).setText(mSubheading);
            ((TextView) mView.findViewById(R.id.subHeadingText)).setVisibility(View.VISIBLE);
        }
        /*((TextView)mView.findViewById(R.id.headingText)).setText(mHeading);
        ((TextView)mView.findViewById(R.id.subHeadingText)).setText(mSubheading);*/
    }

    @Override
    public View installHeaderPanel(Context context, ViewGroup parent) {
        super.installHeaderPanel(context, parent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.layout_modal_dialog_header_default, null);


        mView.setLayoutParams(mTitleView.getLayoutParams());
        int index = mHeaderPanel.indexOfChild(mTitleView);
        mHeaderPanel.removeView(mTitleView);
        mHeaderPanel.addView(mView, index);
        return mHeaderPanel;
    }

    @Override
    public View getTitleView() {
        return mView;
    }

    @Override
    public void setTitleView(CharSequence heading, CharSequence subHeading) {
        mHeading = heading;
        mSubheading = subHeading;

    }
}
