package ui;

import java.text.Bidi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.StateSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import fany.phpuijar.R;

/**
 * <p>VerticalText is TextView rotated -90 which is used to add as stacked bars in BrackTace</p>
 */
public class VerticalText extends TextView {
    private LayoutParams mLayoutParams;
    private Rect mBounds;
    private int mFadeWidth;
    private int mColor;
    private int mStringResourceId;
    private int mCalcWidth;

    /*AN-64998
     * handling the back trace click events and adding vertical text clickable
     * */
    public VerticalText(Context context) {
        this(context, null, 0);
    }

    public VerticalText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mLayoutParams = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.npanel_secondlevel_stack01_width),
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(mLayoutParams);
        mFadeWidth = getResources().getDimensionPixelSize(R.dimen.verttextview_fadingEdgeLength);
        mColor = getTextColors().getColorForState(StateSet.NOTHING, Color.WHITE);
        mBounds = new Rect();
    }

    public void setStringResourceId(int resourceId) {
        mStringResourceId = resourceId;
    }

    public int getStringResourceId() {
        return mStringResourceId;
    }

    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
        }
        if (right != null) {
            right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
        }
        if (top != null) {
            top.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
        }
        super.setCompoundDrawables(bottom, left, top, right);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        // Here we remove right padding which we get from xml and add top
        // padding from java file
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    private boolean rotateDrawable;

    public void wantToRotateDrawableIcon(boolean rotateDrawable) {
        this.rotateDrawable = rotateDrawable;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint textPaint = getPaint();
        textPaint.setColor(mColor);
        textPaint.drawableState = getDrawableState();
        textPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.verttext_size));
        textPaint.getTextBounds(getText().toString(), 0, getText().length(), mBounds);
        String text = null;
        mCalcWidth = (getHeight() - getCompoundDrawablePadding() - getCompoundDrawables()[0].getIntrinsicHeight());
        if (mBounds.width() > mCalcWidth) { //for long text
            text = getCroppedText(getText().toString());
            drawFadingEdgeOnPaint(); //draw fading edge for long text
        } else {
            text = getText().toString();
        }
        textPaint.getTextBounds(text, 0, text.length(), mBounds);
        drawStackIcon(canvas);
        drawVerticalText(canvas, text);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        this.mColor = color;
    }

    private void drawFadingEdgeOnPaint() {
        // [0..stop] will be current text color, [stop..1] will be the actual
        // gradient
        final int height = getHeight() - getCompoundDrawablePadding() - getCompoundDrawables()[0].getIntrinsicHeight();
        float stop = ((float) (height - mFadeWidth) / (float) height);

        LinearGradient gradient = new LinearGradient(0, 0, height, 0, new int[]{mColor, mColor, Color.TRANSPARENT},
                new float[]{0, stop, 1.0f}, Shader.TileMode.CLAMP);
        getPaint().setShader(gradient);
    }

    private String getCroppedText(String text) {
        int noOfChars = getPaint().breakText(getText().toString(), true, mCalcWidth, null);
        return text.substring(0, noOfChars);
    }

    private void drawVerticalText(Canvas canvas, String text) {
        canvas.save();
        canvas.translate(mBounds.height(), mBounds.width() + (getCompoundDrawablePadding() / 4));
        canvas.rotate(-90);
        canvas.drawText(text, 0, 0, getPaint());
        canvas.restore();
    }

    private void drawStackIcon(Canvas canvas) {
        canvas.save();
        Drawable drawable = getCompoundDrawables()[0];
        if (drawable != null) {
            canvas.translate(0, mBounds.width() + getCompoundDrawablePadding());
            if (rotateDrawable) {
                canvas.translate(getPaddingLeft(), 0);
                drawable.draw(canvas);
            } else {
                drawable.draw(canvas);
            }
        }
        canvas.restore();
    }

}
