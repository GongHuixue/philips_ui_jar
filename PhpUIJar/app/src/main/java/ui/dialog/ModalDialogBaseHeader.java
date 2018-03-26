package ui.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fany.phpuijar.R;

/**
 * Created by huixue.gong on 2017/12/14.
 */

public abstract class ModalDialogBaseHeader {
    protected ViewGroup mHeaderPanel;
    protected View mTitleView;
    protected CharSequence mStatusMessage;
    protected Drawable mStatusDrawableLeft;
    protected Drawable mStatusDrawableRight;
    protected Drawable mStatusDrawableTop;
    protected Drawable mStatusDrawableBottom;

    public abstract View getTitleView();

    public abstract void setTitleView(CharSequence heading, CharSequence subHeading);

    public void setupPanel() {
        TextView status = (mHeaderPanel.findViewById(R.id.statusText));
        status.setText(mStatusMessage);
        status.setCompoundDrawablesWithIntrinsicBounds(mStatusDrawableLeft, mStatusDrawableTop, mStatusDrawableRight, mStatusDrawableBottom);
    }

    public void setStatus(CharSequence statusMsg, Drawable left, Drawable right, Drawable top, Drawable bottom) {
        mStatusMessage = statusMsg;
        mStatusDrawableLeft = left;
        mStatusDrawableRight = right;
        mStatusDrawableTop = top;
        mStatusDrawableBottom = bottom;
    }

    public View installHeaderPanel(Context context, ViewGroup parent) {
        mHeaderPanel = parent;
        mTitleView = mHeaderPanel.findViewById(R.id.headerTitle);
        return mHeaderPanel;
    }
}
