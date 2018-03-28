package ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckedTextView;

import java.text.Bidi;

import android.text.TextUtils.TruncateAt;

public class ActivatedTextView extends CheckedTextView {

    private boolean mControllable = true;
    private Drawable mCheckMarkDrawable;
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};

    public ActivatedTextView(Context context) {
        this(context, null);
    }

    public ActivatedTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivatedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (mControllable == true) {
            if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                setHovered(true);
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                setHovered(false);
                return true;
            }
        } else {
            setHovered(false);
            return true;
        }
        return true;
        // return super.dispatchHoverEvent(event);
    }

    @Override
    public void setCheckMarkDrawable(Drawable d) {
        if (d != null) {
            mCheckMarkDrawable = d;
            //setCompoundDrawablePadding(3);
            setCompoundDrawablesWithIntrinsicBounds(mCheckMarkDrawable, null, null, null);
        }
    }

    public void setControllable(boolean control) {
        mControllable = control;
        setEnabled(control);
    }

    public boolean getControllable() {
        return mControllable;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null) {
            Bidi bidi = new Bidi(text.toString(), Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
            if (bidi.getBaseLevel() == 1) // returns 1 if the text is RTL (Arabic,Hebrew)and set the layout direction as RTL
                this.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            else
                this.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);

            this.setSingleLine(true);
            this.setGravity(Gravity.CENTER | Gravity.LEFT);
            this.setEllipsize(TruncateAt.MARQUEE);
            this.setMarqueeRepeatLimit(-1);
            this.setHorizontalFadingEdgeEnabled(true);
            this.setFadingEdgeLength(15);
        }
        super.setText(text, type);
    }
}
