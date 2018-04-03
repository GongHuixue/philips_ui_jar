package ui;
/*
*  Copyright(C) 2012 TP Vision Holding B.V.,
*  All Rights Reserved.
*  This  source code and any compilation or derivative thereof is the
*  proprietary information of TP Vision Holding B.V.
*  and is confidential in nature.
*  Under no circumstances is this software to be exposed to or placed
*  under an Open Source License of any type without the expressed
*  written permission of TP Vision Holding B.V.
*
*/
import fany.phpuijar.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import ui.utils.LogHelper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnHoverListener;
import android.view.View.OnTouchListener;


public class InputPicker extends View implements OnTouchListener, OnHoverListener {
	int statesel = 0;
	Rect mTopDrawable = new Rect();
	Rect mBottomDrawable = new Rect();
	private Drawable mUpIndicator;
	private Drawable mDownIndicator;
	private int mMaxValue;
	private int mMinValue;
	private int mCurrValue;
	private String mLabelName;
	private String mCurrValueStr;
	// we will use drawable or drawLine.
	private Drawable divider;
	private int mHeight;
	private int mWidth;
	private Paint valuePaint, labelPaint;
	private TextPaint valueTextPaint, labelTextPaint;
	private TextAppearanceSpan valueTxtSpan, labelTxtSpan;
	private StaticLayout staticLayout;
	// private int uparrowcordx = -1;
	private int uparrowcordy = -1;
	// private int downarrowcordx = -1;
	private int downarrowcordy = -1;
	private int valTextXcord = -1;
	private int valTextYcord = -1;
	int mBgDrawableHeight;
	int mBgDrawableWidth;
	// private boolean mExitMode = true;
	// private boolean mEditText = false;
	private int mTextHeight = 10;
	private Context mContext;
	StateListDrawable mbgDrawable, upBgArrowDrawableState, downBgArrowDrawableState;
	boolean mCalledfrmchecker = false;
	private boolean mOverrideInputPicker = false;
	private static final int STATE_PRESSED_UP = R.attr.state_pressed_up;
	private static final int STATE_PRESSED_DOWN = R.attr.state_pressed_down;
	private int stateWithUpArrow[] = { android.R.attr.state_checked, STATE_PRESSED_UP };
	private int stateWithDownArrow[] = { android.R.attr.state_checked, STATE_PRESSED_DOWN };
	private int upArrowWidth, upArrowHeight, downArrowWidth, downArrowHeight;
	private boolean isUpArrowPressed;

	private BitmapDrawable mIndicatorDownHightlighted, mDownArrowActive;
	private BitmapDrawable mIndicatorUpHighlighted, mUpArrowActive;

	private int mMaxValueDigitLenght;
	private boolean mClearValue = true;
	private boolean mEnableLeadingZero;

	// private int state[]= {android.R.attr.state_checked};
	public InputPicker(Context context) {
		this(context, null);
	}

	public InputPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	// initialiase the InputPicker.
	@SuppressLint("NewApi")
	public InputPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

		if (attrs != null) {
			if (attrs.getStyleAttribute() == R.style.inputPickerStyle_overrideInputPickerStyle) {
				LogHelper.d("TPVision", "Seems like It is different picker");
			}
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.inputPicker, defStyle, 0);
			/*valueTxtSpan = new TextAppearanceSpan(context, a.getResourceId(
					R.styleable.inputPicker_inputPickerDisplayTextStyle, -1));*/
			valueTxtSpan = new TextAppearanceSpan(context, R.style.hnl);
			valuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			valueTextPaint = new TextPaint(valuePaint);
			valueTextPaint.setTextAlign(Align.CENTER);
			valueTxtSpan.updateDrawState(valueTextPaint); // this can be used
															// directly to set
															// the text values

			/*labelTxtSpan = new TextAppearanceSpan(context, a.getResourceId(
					R.styleable.inputPicker_inputPickerLabelTextStyle, -1));*/
			labelTxtSpan = new TextAppearanceSpan(context, R.style.hnl);		
			labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			// AP200 picker text is not ceter aligned
			labelPaint.setTextAlign(Align.CENTER);
			labelTextPaint = new TextPaint(labelPaint);
			labelTxtSpan.updateDrawState(labelTextPaint);
			mTextHeight = labelTxtSpan.getTextSize();
			setOnTouchListener(this);

			setFocusable(true);
			// setFocusableInTouchMode(true);
			setClickable(true);
			setOnHoverListener(this);
			setLongClickable(true);
			setEnabled(true);
			mContext = context;
			// this.setOnClickListener(clickListener);
			// this.setOnHoverListener(hoverListener);
			loadAttributes(a);
			a.recycle();
		}

	}

	@SuppressLint("NewApi")
	private void loadAttributes(TypedArray iTypedArray) {
		mOverrideInputPicker = iTypedArray.getBoolean(R.styleable.inputPicker_overrideInputPicker, false);
		mUpIndicator = iTypedArray.getDrawable(R.styleable.inputPicker_inputPickerDownIndicator);
		
		/*mIndicatorUpHighlighted = ((BitmapDrawable) iTypedArray
				.getDrawable(R.styleable.inputPicker_inputPickerUpIndicatorsel));
		mIndicatorDownHightlighted = ((BitmapDrawable) iTypedArray
				.getDrawable(R.styleable.inputPicker_inputPickerDownIndicatorsel));*/
		mIndicatorUpHighlighted=(BitmapDrawable)getResources().getDrawable(R.drawable.pickers_arrow_up_activated_highlighted);
		mIndicatorDownHightlighted=(BitmapDrawable)getResources().getDrawable(R.drawable.pickers_arrow_down_activated_highlighted);	
		//mUpArrowActive = ((BitmapDrawable) iTypedArray.getDrawable(R.styleable.inputPicker_inputPickerUpIndicator));
		mUpArrowActive=(BitmapDrawable)getResources().getDrawable(R.drawable.pickers_arrow_up_activated);
	
		//mDownArrowActive = ((BitmapDrawable) iTypedArray.getDrawable(R.styleable.inputPicker_inputPickerDownIndicator));
		mDownArrowActive=(BitmapDrawable)getResources().getDrawable(R.drawable.pickers_arrow_down_activated);
		
		mDownIndicator = iTypedArray.getDrawable(R.styleable.inputPicker_inputPickerUpIndicator);
		mMaxValue = iTypedArray.getInt(R.styleable.inputPicker_inputPickerMaxValue, 0);
		mMinValue = iTypedArray.getInt(R.styleable.inputPicker_inputPickerMinValue, 0);

		mCurrValue = iTypedArray.getInt(R.styleable.inputPicker_inputPickerCurrValue, mMinValue);
		mCurrValueStr = mCurrValue + "";
		mLabelName = iTypedArray.getString(R.styleable.inputPicker_inputPickerLabelName);

		// mHeight = iTypedArray.getDimensionPixelSize(android.R.attr.height,
		// 0);
		mHeight = getHeight();

		// mHeight = iTypedArray.getDimensionPixelSize(android.R.attr.height,0);
		mWidth = getWidth();

		Resources resource = mContext.getResources();

		upArrowWidth = resource.getDimensionPixelSize(R.dimen.components_picker_arrow_up_width);
		upArrowHeight = resource.getDimensionPixelSize(R.dimen.components_picker_arrow_up_height);
		downArrowWidth = resource.getDimensionPixelSize(R.dimen.components_picker_arrow_down_width);
		downArrowHeight = resource.getDimensionPixelSize(R.dimen.components_picker_arrow_down_height);

		if (!mOverrideInputPicker) {
			int topViewYCord = resource.getDimensionPixelSize(R.dimen.components_picker_container_y1);
			int bottomViewYCord = resource.getDimensionPixelSize(R.dimen.components_picker_label_text_y2);
			mHeight = bottomViewYCord - topViewYCord;
			mBgDrawableHeight = resource.getDimensionPixelSize(R.dimen.components_picker_container_height);
			LogHelper.d("TPVision", "mHeight =" + mHeight);
			LogHelper.d("TPVision", "mBgDrawableHeight =" + mBgDrawableHeight);
			mBgDrawableWidth = iTypedArray.getDimensionPixelSize(R.styleable.inputPicker_inputPickerDrawableWidth, 0);
			LogHelper.d("TPVision", "mBgDrawableWidth =" + mBgDrawableWidth);
			// int arrowcordx =
			// resource.getDimensionPixelSize(R.dimen.components_picker_arrow_up_x1);
			int baserefx = resource.getDimensionPixelSize(R.dimen.components_picker_container_x1);
			// uparrowcordx = arrowcordx - baserefx;

			int arrowcordy = resource.getDimensionPixelSize(R.dimen.components_picker_arrow_up_y1);
			int baserefy = resource.getDimensionPixelSize(R.dimen.components_picker_container_y1);
			uparrowcordy = arrowcordy - baserefy;

			// arrowcordx =
			// resource.getDimensionPixelSize(R.dimen.components_picker_arrow_down_x1);
			// downarrowcordx = arrowcordx - baserefx;

			arrowcordy = resource.getDimensionPixelSize(R.dimen.components_picker_arrow_down_y1);
			downarrowcordy = arrowcordy - baserefy;

			mWidth = mBgDrawableWidth;

			valTextXcord = resource.getDimensionPixelSize(R.dimen.components_picker_input_text_x1);
			valTextXcord = valTextXcord - baserefx;
			valTextYcord = resource.getDimensionPixelSize(R.dimen.components_picker_input_text_y2)
					+ resource.getDimensionPixelSize(R.dimen.components_picker_input_text_y1);
			// valTextYcord =
			// resource.getDimensionPixelSize(R.dimen.components_picker_divider_top_y2)+resource.getDimensionPixelSize(R.dimen.components_picker_divider_bottom_y1);
			valTextYcord = valTextYcord / 2;

			LogHelper.d("TPVision", "valTextYcord =" + valTextYcord + "baserefY" + baserefy);
			valTextYcord = valTextYcord - baserefy;
			LogHelper.d("TPVision", "valTextYcord =" + valTextYcord);
		} else {
			int topViewYCord = resource.getDimensionPixelSize(R.dimen.components_extended_picker_container_y1);
			int bottomViewYCord = resource.getDimensionPixelSize(R.dimen.components_extended_picker_label_text_y2);
			mHeight = bottomViewYCord - topViewYCord;
			mBgDrawableHeight = resource.getDimensionPixelSize(R.dimen.components_extended_picker_container_height);
			LogHelper.d("TPVision", "mHeight =" + mHeight);
			mBgDrawableHeight = resource.getDimensionPixelSize(R.dimen.components_extended_picker_container_height);
			mBgDrawableWidth = resource.getDimensionPixelSize(R.dimen.components_extended_picker_container_width);
			LogHelper.d("TPVision", "mBgDrawableWidth =" + mBgDrawableWidth + " mBgDrawableHeight =" + mBgDrawableHeight);
			// int arrowcordx =
			// resource.getDimensionPixelSize(R.dimen.components_extended_picker_arrow_up_x1);
			int baserefx = resource.getDimensionPixelSize(R.dimen.components_extended_picker_container_x1);
			// uparrowcordx = arrowcordx - baserefx;

			int arrowcordy = resource.getDimensionPixelSize(R.dimen.components_extended_picker_arrow_up_y1);
			int baserefy = resource.getDimensionPixelSize(R.dimen.components_extended_picker_container_y1);
			uparrowcordy = arrowcordy - baserefy;

			// arrowcordx =
			// resource.getDimensionPixelSize(R.dimen.components_extended_picker_arrow_down_x1);
			// downarrowcordx = arrowcordx - baserefx;

			arrowcordy = resource.getDimensionPixelSize(R.dimen.components_extended_picker_arrow_down_y1);
			downarrowcordy = arrowcordy - baserefy;

			// mWidth = mBgDrawableWidth;

			valTextXcord = resource.getDimensionPixelSize(R.dimen.components_extended_picker_input_text_x1);
			valTextXcord = valTextXcord - baserefx;
			valTextYcord = resource.getDimensionPixelSize(R.dimen.components_extended_picker_input_text_y2)
					+ resource.getDimensionPixelSize(R.dimen.components_extended_picker_input_text_y1);
			valTextYcord = valTextYcord / 2;

			LogHelper.d("TPVision", "valTextYcord =" + valTextYcord + "baserefY" + baserefy);
			valTextYcord = valTextYcord - baserefy;
			LogHelper.d("TPVision", "valTextYcord =" + valTextYcord);
		}
		//mbgDrawable = (StateListDrawable) iTypedArray.getDrawable(R.styleable.inputPicker_inputPickerbgDrawable);
		mbgDrawable = (StateListDrawable)getResources().getDrawable(R.drawable.background);
		mbgDrawable.setState(getDrawableState());

		/*upBgArrowDrawableState = (StateListDrawable) iTypedArray
				.getDrawable(R.styleable.inputPicker_inputPickerUpArrowBgDrawable);*/
		upBgArrowDrawableState= (StateListDrawable)getResources().getDrawable(R.drawable.up_arrow_selector);
		upBgArrowDrawableState.setState(getDrawableState());

		/*downBgArrowDrawableState = (StateListDrawable) iTypedArray
				.getDrawable(R.styleable.inputPicker_inputPickerDownArrowBgDrawable);*/
		downBgArrowDrawableState=(StateListDrawable)getResources().getDrawable(R.drawable.down_arrow_selector);
		downBgArrowDrawableState.setState(getDrawableState());
		// this.setBackground(mbgDrawable);

		invalidate();
	}

	/**
	 * This is used to get the UpIndicator of the picker
	 * */
	public Drawable getmUpIndicator() {
		return mUpIndicator;
	}

	/**
	 * This is used to set the UpIndicator of the picker
	 * */
	public void setmUpIndicator(Drawable mUpIndicator) {
		this.mUpIndicator = mUpIndicator;
		invalidate();
	}

	/**
	 * This is used to get the DownIndicator of the picker
	 * */
	public Drawable getmDownIndicator() {
		return mDownIndicator;
	}

	/**
	 * This is used to set the DownIndicator of the picker
	 * */
	public void setmDownIndicator(Drawable mDownIndicator) {
		this.mDownIndicator = mDownIndicator;
	}

	/**
	 * This is used to get the Maximum Value of the picker
	 * */
	public int getmMaxValue() {
		return mMaxValue;
	}

	/**
	 * This is used to set the Maximum Value of the picker
	 * */
	public void setmMaxValue(int mMaxValue) {
		this.mMaxValue = mMaxValue;
		this.mMaxValueDigitLenght = lenghtOfDigit(mMaxValue);
	}

	/**
	 * This is used to get the MinimumValue of the picker
	 * */
	public int getmMinValue() {
		return mMinValue;
	}

	/**
	 * This is used to set the Minimum value of the picker
	 * */
	public void setmMinValue(int mMinValue) {
		this.mMinValue = mMinValue;
		invalidate();
	}

	/**
	 * This is used to set the LabelName of the picker
	 * */
	public void setLabelName(String labelName) {
		this.mLabelName = labelName;
		invalidate();

	}

	/**
	 * This is used to get the LabelName of the picker
	 * */
	public String getLabelName() {
		return (mLabelName);
	}

	/**
	 * This is used to get the currentValue of the current view of the picker
	 * */
	public int getmCurrValue() {
		return mCurrValue;

	}

	/**
	 * This is used to set the currentValue of the current view of the picker
	 * */
	public void setmCurrValue(String mCurrValue) {
		this.mCurrValueStr = mCurrValue;
		if (mEnableLeadingZero) {
			try {
				this.mCurrValue = Integer.parseInt(mCurrValue);
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
			}
		}
		invalidate();
	}
	
	public void setmCurrValue(int mCurrValue) {
		if (mCurrValue > mMaxValue){
			mCurrValue =mMaxValue;
		} else if(mCurrValue < mMinValue){
			mCurrValue = mMinValue;
		}
		this.mCurrValueStr = mCurrValue + "";
		this.mCurrValue = mCurrValue;
		invalidate();
	}
	
	private void setCurrValue(int mCurrValue) {
		this.mCurrValueStr = mCurrValue + "";
		this.mCurrValue = mCurrValue;
		invalidate();
	}

	private void adjustmentOfLeadingZero(int currValue, int calculateValue) {
		this.mClearValue = false;

		if (mEnableLeadingZero) {

			if (mCurrValueStr.length() < mMaxValueDigitLenght) {
				setmCurrValue(mCurrValueStr += "" + currValue);
			} else {
				setmCurrValue(String.format("%0" + mMaxValueDigitLenght + "d", calculateValue));
			}
		} else {
			setCurrValue(currValue);
		}
	}

	private void incrementDecrementValue(int currValue) {
		this.mClearValue = false;
		if (mEnableLeadingZero) {
			setmCurrValue(String.format("%0" + this.mCurrValueStr.length() + "d", currValue));
		} else {
			setmCurrValue(mCurrValue);
		}
	}


	private void setCurrValueForDigit(int mCurrValue) {

		if (this.mClearValue) {
			this.mClearValue = false;
			this.mCurrValue = 0;
			this.mCurrValueStr = "";
		}
		int calulcateValue = this.mCurrValue * 10 + mCurrValue;
		if (calulcateValue > mMaxValue) {
			this.mCurrValue = 0;
			this.mCurrValueStr = "";
			calulcateValue = mCurrValue;
		}
		int currValueDigitLenght = lenghtOfDigit(calulcateValue);
		if (mEnableLeadingZero) {
			adjustmentOfLeadingZero(mCurrValue, calulcateValue);
		} else {
			setCurrValue(calulcateValue);
		}

		LogHelper.d("TPVision", "maxValueDigitLenght: " + mMaxValueDigitLenght + " currValueDigitLenght: "
				+ currValueDigitLenght);
		if (mInputPickerValueEntered != null 
			&& (mMaxValueDigitLenght == this.mCurrValueStr.length() || mMaxValueDigitLenght == currValueDigitLenght)) {
			mInputPickerValueEntered.onInputPickerValueEntered(this.mCurrValue);
		}
	}

	private int lenghtOfDigit(int digit) {
		return String.valueOf(digit).length();
	}

	public Drawable getDivider() {
		return divider;
	}

	public void setDivider(Drawable divider) {
		this.divider = divider;
		invalidate();
	}

	/**
	 * Set the height of the view Nav-PageControl. check it when we are using
	 * the onMeasure
	 * */
	public void setHeight(int iHeight) {
		// mHeight=iHeight;

		invalidate();
	}

	/**
	 * This is to set the width of the picker
	 * */
	public void setWidth(int width) {
		// mWidth = width;
		invalidate();
	}

	/**
	 * This is overridden onMeasure method for setting the view's width and
	 * height.
	 * */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mWidth > 0 && mHeight > 0) {
			this.setMeasuredDimension(getMeasurement(widthMeasureSpec, mWidth),
					getMeasurement(heightMeasureSpec, mHeight));
		} else if (mWidth > 0) {
			this.setMeasuredDimension(getMeasurement(widthMeasureSpec, mWidth), heightMeasureSpec);
		} else if (mHeight > 0) {
			this.setMeasuredDimension(widthMeasureSpec, getMeasurement(heightMeasureSpec, mHeight));
		} else {
			setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
		}
	}

	private int getMeasurement(int measureSpec, int preferred) {
		int specSize = MeasureSpec.getSize(measureSpec);
		int measurement = 0;

		switch (MeasureSpec.getMode(measureSpec)) {
		case MeasureSpec.EXACTLY:
			// This means the width of this view has been given.
			measurement = specSize;
			break;
		case MeasureSpec.AT_MOST:
			// Take the minimum of the preferred size and what
			// we were told to be.
			measurement = Math.min(preferred, specSize);
			break;
		default:
			measurement = preferred;
			break;
		}

		return measurement;
	}

	/**
	 * This method is used to render the view data on the canvas
	 * */

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		int iWidth = this.getWidth();
		// AP200 picker text is not ceter alignedA
		
		staticLayout = new StaticLayout(mLabelName, labelTextPaint, iWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
		canvas.save();
		canvas.translate(0, 0);

		mbgDrawable.setBounds(0, 0, mBgDrawableWidth, mBgDrawableHeight);
		mbgDrawable.getCurrent().draw(canvas);
		canvas.restore();
		canvas.save();
		double width = iWidth / (double) 2;
		canvas.translate((int) width, this.getHeight() - (labelTxtSpan.getTextSize() + 4));
		staticLayout.draw(canvas);
		canvas.restore();
		canvas.save();

		canvas.drawText(mCurrValueStr, (int) width, (valTextYcord + valueTxtSpan.getTextSize() / 2), valueTextPaint);

		LogHelper.d("TPVision", "text coord " + (valTextYcord + valueTxtSpan.getTextSize() / 2));

		if (mTopDrawable.isEmpty()) {
			mTopDrawable.set(0, this.getHeight() / 2 + mTextHeight, this.getWidth(), this.getHeight());
		}
		if (mBottomDrawable.isEmpty()) {
			mBottomDrawable.set(0, 0, this.getWidth(), this.getHeight() / 2 - mTextHeight);
		}

		if (statesel == 2) {
			canvas.save();
			canvas.translate((float)(mWidth - mIndicatorDownHightlighted.getBitmap().getWidth()) / 2, downarrowcordy);
			mIndicatorDownHightlighted.setBounds(0, 0, downArrowWidth, downArrowHeight);
			mIndicatorDownHightlighted.getCurrent().draw(canvas);
			canvas.restore();

			canvas.save();
			canvas.translate((float)(mWidth - mUpArrowActive.getBitmap().getWidth()) / 2, uparrowcordy);
			mUpArrowActive.setBounds(0, 0, upArrowWidth, upArrowHeight);
			mUpArrowActive.getCurrent().draw(canvas);
			canvas.restore();

		} else if (statesel == 3) {

			canvas.save();
			canvas.translate((float)(mWidth - mDownArrowActive.getBitmap().getWidth()) / 2, downarrowcordy);
			mDownArrowActive.setBounds(0, 0, downArrowWidth, downArrowHeight);
			mDownArrowActive.getCurrent().draw(canvas);
			canvas.restore();

			canvas.save();
			canvas.translate((float)(mWidth - mIndicatorUpHighlighted.getBitmap().getWidth()) / 2, uparrowcordy);
			mIndicatorUpHighlighted.setBounds(0, 0, upArrowWidth, upArrowHeight);
			mIndicatorUpHighlighted.getCurrent().draw(canvas);
			canvas.restore();

		} else {
			canvas.save();
			canvas.translate((float)(mWidth - upBgArrowDrawableState.getIntrinsicWidth()) / 2, uparrowcordy);
			upBgArrowDrawableState.setBounds(0, 0, upArrowWidth, upArrowHeight);
			upBgArrowDrawableState.getCurrent().draw(canvas);
			canvas.restore();

			canvas.save();
			canvas.translate((float)(mWidth - downBgArrowDrawableState.getIntrinsicWidth()) / 2, downarrowcordy);
			downBgArrowDrawableState.setBounds(0, 0, downArrowWidth, downArrowHeight);
			downBgArrowDrawableState.getCurrent().draw(canvas);
			canvas.restore();
		}

	}

	/**
	 * This is called when we have to get the u-point events and update the UI
	 * */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		statesel = 0;
		switch (event.getActionMasked()) {

		case MotionEvent.ACTION_DOWN:
			if (!mTopDrawable.isEmpty() && !mBottomDrawable.isEmpty()) {

				if (mTopDrawable.contains((int) event.getX(), (int) event.getY())) {
					// statesel = 2;
					if (mIInputPickerInfrmValueChangeListener == null && !mOverrideInputPicker) {
						mCurrValue--;
						if (mCurrValue < mMinValue) {
							mCurrValue = mMaxValue;

						}
						incrementDecrementValue(mCurrValue);

					} else {
						if (mIInputPickerInfrmValueChangeListener != null) {
							mIInputPickerInfrmValueChangeListener.onInputPickerInfrmValueDownEvent();
						}
					}

					isUpArrowPressed = false;
					refreshDrawableState();

				} else if (mBottomDrawable.contains((int) event.getX(), (int) event.getY())) {
					// statesel = 1;
					if (mIInputPickerInfrmValueChangeListener == null && !mOverrideInputPicker) {
						mCurrValue++;
						if (mCurrValue > mMaxValue) {
							mCurrValue = mMinValue;
							this.mCurrValueStr = mCurrValue+"";
						}
						incrementDecrementValue(mCurrValue);
					} else {
						if (mIInputPickerInfrmValueChangeListener != null) {
							mIInputPickerInfrmValueChangeListener.onInputPickerInfrmValueUpEvent();
						}
					}

					isUpArrowPressed = true;
					refreshDrawableState();
				}
				// PR fix for AN-4600
				if (mInputPickerValueChangeListener != null) {
					mInputPickerValueChangeListener.onInputPickerValueChanged(mCurrValue);
				}
				// PR fix for AN-4600 ends.

			}
			break;
		case MotionEvent.ACTION_UP:
			if (!mTopDrawable.isEmpty() && !mBottomDrawable.isEmpty()) {

				if (mTopDrawable.contains((int) event.getX(), (int) event.getY())) {
					statesel = 0;

				} else if (mBottomDrawable.contains((int) event.getX(), (int) event.getY())) {
					statesel = 0;

				}
			}

			break;

		}

		invalidate();
		return false;
	}

	public void enableLeandingZero(boolean enableZeroInStart) {
		this.mEnableLeadingZero = enableZeroInStart;
	}

	// Added for PR 1033
	public interface IInputPickerValueChangeListener {
		public void onInputPickerValueChanged(int mCurrValue);
	}

	public interface IInputPickerInfrmValueChangeListener {
		public void onInputPickerInfrmValueUpEvent();

		public void onInputPickerInfrmValueDownEvent();

	}

	public interface IInputPickerValueConfirmation {
		public void onInputPickerValueConfirm(int mCurrValue);
	}

	public interface IInputPickerValueEntered {
		public void onInputPickerValueEntered(int mCurrValue);
	}

	IInputPickerInfrmValueChangeListener mIInputPickerInfrmValueChangeListener = null;
	IInputPickerValueChangeListener mInputPickerValueChangeListener = null;
	private IInputPickerValueConfirmation mInputPickerValueConfirmationListener = null;
	private IInputPickerValueEntered mInputPickerValueEntered = null;

	public void setInputPickerValueChangeListener(IInputPickerValueChangeListener aInputPickerValueChangeListener) {
		LogHelper.i("Input picker", "setInputPickerValueChangeListener");
		mInputPickerValueChangeListener = aInputPickerValueChangeListener;
	}

	public void setIInputPickerInfrmValueChangeListener(
			IInputPickerInfrmValueChangeListener aIInputPickerInfrmValueChangeListener) {
		LogHelper.i("Input picker", "setIInputPickerInfrmValueChangeListener");
		mIInputPickerInfrmValueChangeListener = aIInputPickerInfrmValueChangeListener;
	}

	public void setValueConfirmListener(IInputPickerValueConfirmation aInputPickerValueConfirmation) {
		LogHelper.i("Input picker", "setValueConfirmListener");
		mInputPickerValueConfirmationListener = aInputPickerValueConfirmation;
	}

	public void setValueEnteredListener(IInputPickerValueEntered aInputPickerValueEntered) {
		LogHelper.i("Input picker", "setValueConfirmListener");
		this.mInputPickerValueEntered = aInputPickerValueEntered;
	}

	//

	/**
	 * On Keydown is used for changing the drawing selector and remove the
	 * runnable task
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent ev) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			isUpArrowPressed = false;

			statesel = 2;

			refreshDrawableState();
			invalidate();
			if( ev.getRepeatCount() == 0){
				if (mIInputPickerInfrmValueChangeListener == null && !mOverrideInputPicker) {
					mCurrValue--;
					if (mCurrValue < mMinValue) {
						mCurrValue = mMaxValue;

					}
					// mCurrValueStr = mCurrValue + "";
					incrementDecrementValue(mCurrValue);
				} else {
					if (mIInputPickerInfrmValueChangeListener != null) {
						mIInputPickerInfrmValueChangeListener.onInputPickerInfrmValueDownEvent();
					}
				}
				if (mInputPickerValueChangeListener != null) {
					mInputPickerValueChangeListener.onInputPickerValueChanged(mCurrValue);
				}
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			isUpArrowPressed = true;

			statesel = 3;

			refreshDrawableState();
			invalidate();
			if( ev.getRepeatCount() == 0){
				if (mIInputPickerInfrmValueChangeListener == null && !mOverrideInputPicker) {
					mCurrValue++;

					if (mCurrValue > mMaxValue) {
						mCurrValue = mMinValue;
						this.mCurrValueStr = mCurrValue+"";
					}
					// mCurrValueStr = mCurrValue + "";
					incrementDecrementValue(mCurrValue);
				} else {
					if (mIInputPickerInfrmValueChangeListener != null) {
						mIInputPickerInfrmValueChangeListener.onInputPickerInfrmValueUpEvent();
					}
				}
				if (mInputPickerValueChangeListener != null) {
					mInputPickerValueChangeListener.onInputPickerValueChanged(mCurrValue);
				}
			}
			return true;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:

			if (mInputPickerValueChangeListener != null && ev.getRepeatCount() == 0) {
				mInputPickerValueChangeListener.onInputPickerValueChanged(mCurrValue);
			}
			if (mEnableLeadingZero)
				setmCurrValue(String.format("%0" + mMaxValueDigitLenght + "d", mCurrValue));

			refreshDrawableState();
			invalidate();
			break;

		case KeyEvent.KEYCODE_ENTER:
		case KeyEvent.KEYCODE_DPAD_CENTER:
			if (mInputPickerValueConfirmationListener != null && ev.getRepeatCount() == 0) {
				mInputPickerValueConfirmationListener.onInputPickerValueConfirm(mCurrValue);
			}
			if (mEnableLeadingZero)
				setmCurrValue(String.format("%0" + mMaxValueDigitLenght + "d", mCurrValue));
			break;

		case KeyEvent.KEYCODE_0:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(0);
			break;
		case KeyEvent.KEYCODE_1:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(1);
			break;
		case KeyEvent.KEYCODE_2:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(2);
			break;
		case KeyEvent.KEYCODE_3:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(3);
			break;
		case KeyEvent.KEYCODE_4:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(4);
			break;
		case KeyEvent.KEYCODE_5:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(5);
			break;
		case KeyEvent.KEYCODE_6:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(6);
			break;
		case KeyEvent.KEYCODE_7:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(7);
			break;
		case KeyEvent.KEYCODE_8:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(8);
			break;
		case KeyEvent.KEYCODE_9:
			if (ev.getRepeatCount() == 0)
				setCurrValueForDigit(9);
			break;

		}
		return false;
	}

	/**
	 * onKeyUp is called to get the key events this is used to increment or
	 * decrement the values. This also has the call to wrap the value and
	 * activating the edit state for the view.
	 * */
	@SuppressLint("NewApi")
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent ev) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_DOWN:
			isUpArrowPressed = false;

			statesel = 0;

			refreshDrawableState();
			invalidate();
			

			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			isUpArrowPressed = true;

			statesel = 0;
			refreshDrawableState();
			invalidate();
			
			return true;
		}
		return super.onKeyUp(keyCode, ev);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public boolean onHover(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_HOVER_ENTER:

			setHovered(true);
			refreshDrawableState();
			invalidate();
			return true;

		case MotionEvent.ACTION_HOVER_EXIT:
			LogHelper.d("TPVision", "onHover Exit: " + hasFocus() + " " + mLabelName);
			setHovered(false);
			refreshDrawableState();
			invalidate();

		}

		return false;
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
		LogHelper.i("TPVision", "On Focus: " + gainFocus + " " + mLabelName);
		if (gainFocus) {
			this.mClearValue = true;
		} else {
			this.mClearValue = false;
		}
		refreshDrawableState();
		invalidate();
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		mbgDrawable.setState(getDrawableState());
		upBgArrowDrawableState.setState(getDrawableState());
		downBgArrowDrawableState.setState(getDrawableState());
		invalidate();
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 2);
		if (isUpArrowPressed)
			mergeDrawableStates(drawableState, stateWithUpArrow);
		else
			mergeDrawableStates(drawableState, stateWithDownArrow);
		return drawableState;
	}
}