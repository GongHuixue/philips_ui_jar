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
package ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;

import fany.phpuijar.R;
import ui.utils.LogHelper;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.content.res.Configuration;
/**<p>Design document for slider</p>
 <h1>Slider</h1>
 <p><strong><span style="text-decoration: underline;">Purpose and Scope</span></strong></p>
<p>The main purpose of the slider is to fine tune settings in the setup and experience menu. Depending on the required adjustments three different sort of sliders are used:</p>
<p>1)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Range Slider<strong></strong></p>
<p>2)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Balanced Slider<strong></strong></p>
<p>3)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Step Slider<strong></strong></p>
<p>The current seek bar in android does not support these variations and is placed horizontal. Rotating the canvas would still not support these variations; hence this is created as a custom view.</p>
<p><strong><span style="text-decoration: underline;">References</span></strong></p>
<p><span style="font-size: small; font-family: arial, helvetica, sans-serif;">UIS Doc : <a href="http://blrreviewtool.tpvision.com/review/review_info_form.php?rev_id=6793">http://blrreviewtool.tpvision.com/review/review_info_form.php?rev_id=6793</a></span></p>
<p>&nbsp;</p>
<p><strong><span style="text-decoration: underline;">List of applications using this component</span></strong></p>
<p>Settings</p>
<p>DemoMe</p>
<h3><span style="text-decoration: underline;">Requirements</span></h3>
<p>1) Slider currently supports 3 styles :<br />&nbsp; &nbsp; &nbsp;For range slider which is default style<br />&nbsp; &nbsp; &nbsp;Balanced slider for &ndash; to + range.<br />&nbsp; &nbsp; &nbsp;Step slider where the increments are by more than 1 unit<br /><br /></p>
<img src="{@docRoot}/slider_design_asset/req1.png"/>
<p>2) The slider has:<br />&nbsp; &nbsp; &nbsp;1. Optional title.<br />&nbsp; &nbsp; &nbsp;2. A predefined maximum value.<br />&nbsp; &nbsp; &nbsp;3. A predefined minimum value.<br />&nbsp; &nbsp; &nbsp;4. Increase icon.<br />&nbsp; &nbsp; &nbsp;5. Value divider (With filling).<br />&nbsp; &nbsp; &nbsp;6. Decrease icon.<br />&nbsp; &nbsp; &nbsp;7. Current numeric value, moves with the thumb.<br />&nbsp; &nbsp; &nbsp;8. Optional step size.<br />&nbsp; &nbsp; &nbsp;9. Label.</p>
<img src="{@docRoot}/slider_design_asset/req2.png"/>
<p>3)The user moves highlight focus to the slider, using the cursor keys.By pressing RC_Up and RC_Down the value is immediately adjusted.</p>
<p>4)Pressing the directional keys can be repeated to increase or decrease the value. When long pressed the value is increased<br />or decrease until the maximum value is reached. While adjusting the value an arrow shows the direction the slider is being<br />adjusted to.</p>
<p>5) Value indication behaviour<br />- maximum and minimum values are shown at the top left and bottom left side of the slider.<br />- current value of slider is always shown in the middle of slider. This value get changed dynamically.</p>
<img src="{@docRoot}/slider_design_asset/req5.png"/>
<h3>Usage Guidelines&nbsp;</h3>
<p><span class="text" style="color: #000000;">Slider is used to fine tune settings in the setup and experience menu.</span></p><h3>Used in XML&nbsp;</h3>
<pre>The Slider can include in the main xml:</pre>
<table style="background-color: #e4e4e6;" border="1">
<tbody>
<tr>
<td valign="top" width="328">
<pre>&lt;org.droidtv.ui.comps.Slider<br />        android:id="@+id/Slider1"<br />        android:layout_width="wrap_content"<br />        android:layout_height="wrap_content"<br />        style="@style/DefaultVerticalSlider"<br />       &gt;<br />&lt;/org.droidtv.ui.comps.Slider&gt;</pre>
</td>
</tr>
</tbody>
</table>
<h3>Used in Java Code</h3>
<pre>Creation of instance of Slider:<br />When the main.xml is inflated using &ldquo;SetContentView(R.layout.main_xml)&rdquo;, the instance of Slider is created.</pre>
<table style="background-color: #e4e4e6;" border="1">
<tbody>
<tr>
<td valign="top" width="328">
<pre>Slider slider=(Slider)(findViewById(R.id.Slider1));<br />slider.setLabel(String txt); //sets label for slider<br />slidersetEnabled(boolean enabled); //Sets slider enable or disable<br />slider.setSliderValue(int mMinValue, int mMaxValue, int mCurrentValue,int stepValue); // set values for slider</pre>
</td>
</tr>
</tbody>
</table>
<pre>&nbsp;</pre>
<h3>Register for value change listener</h3>
<table style="background-color: #e4e4e6;" border="1">
<tbody>
<tr>
<td valign="top" width="328">
<pre>SliderValueChangeListener listner = new SliderValueChangeListener () {<br /><br />            public void onSliderValueChanged(int viewState){}<br /><br />}<br />npb.setViewStateChangeListener(listner);</pre>
</td>
</tr>
</tbody>
</table>
<p><strong><br /></strong></p>

<h3><span style="text-decoration: underline;">Static Design</span></h3>
<pre>The custom view is managed by various drawables for background, thumb, fill icons, etc.<br />Each drawable is drawn when its bounds are set.<br />The draw of the view is done in onDraw() method with the bounds set by each of the drawable.</pre>
<pre><strong>Customization of the component</strong><br /><br />Slider exposes various attributes for customizing the type and VI . The attributes are as follows.<br /><br />&lt;style name="DefaultVerticalSlider"&gt;<br />  &lt;item name="android:focusable"&gt;true&lt;/item&gt; <br />  &lt;item name="isBalancedSlider"&gt;false&lt;/item&gt;  <br />  &lt;item name="sliderMinValue"&gt;0&lt;/item&gt; <br />  &lt;item name="sliderMaxValue"&gt;25&lt;/item&gt; <br />  &lt;item name="sliderCurrentValue"&gt;0&lt;/item&gt; <br />  &lt;item name="valueDisplayTextStyle"&gt;@style/lnl&lt;/item&gt; <br />  &lt;item name="updateResolution"&gt;1&lt;/item&gt;  <br />  &lt;item name="sliderBgDrawable"&gt;@drawable/slider_bg&lt;/item&gt; <br />  &lt;item name="sliderContainerWidth"&gt;@dimen/components_slider_container_width&lt;/item&gt; <br />  &lt;item name="sliderContainerHeight"&gt;@dimen/components_slider_container_height&lt;/item&gt; <br />  &lt;item name="sliderDrawable"&gt;@drawable/slider_bar_drawable&lt;/item&gt; <br />  &lt;item name="stepSliderDrawable"&gt;@drawable/sliders_step_divider&lt;/item&gt; <br />  &lt;item name="sliderFill"&gt;@drawable/slider_fill_drawable&lt;/item&gt; <br />  &lt;item name="sliderWidth"&gt;@dimen/components_slider_bg_width&lt;/item&gt; <br />  &lt;item name="sliderHeight"&gt;@dimen/components_slider_bg_height&lt;/item&gt; <br />  &lt;item name="upArrowIconDrawable"&gt;@drawable/sliders_up_arrow&lt;/item&gt;<br />  &lt;item name="upArrowIconPressedDrawable"&gt;@drawable/slider_arrow_up_pressed&lt;/item&gt; <br />  &lt;item name="downArrowIconDrawable"&gt;@drawable/sliders_down_arrow&lt;/item&gt;<br />  &lt;item name="downArrowIconPressedDrawable"&gt;@drawable/slider_arrow_down_pressed&lt;/item&gt; <br />  &lt;item name="sliderLabelText"&gt;"Brightness"&lt;/item&gt; <br />  &lt;item name="sliderLabelTextStyle"&gt;@style/lnl&lt;/item&gt;<br />  &lt;item name="rightLabelBalancedSlider"&gt;"Right"&lt;/item&gt; <br /> &lt;item name="leftLabelBalancedSlider"&gt;"Left"&lt;/item&gt; <br /> <br /> &lt;/style&gt;<br /><br /><br />Slider currently supports 3 styles :<br />1)For range slider which is default style<br />2)Balanced slider for &ndash; to + range.<br />3)Step slider where the increments are by more than 1 unit<br /><br />On creation of this component, the attributes are read and the drawables and the values are assigned accordingly.</pre>
<h3><span style="text-decoration: underline;">Class Diagram</span></h3>
<img src="{@docRoot}/slider_design_asset/ClassDiagram.png"/>
<p><strong><span style="text-decoration: underline;">Attributes</span></strong></p>
<table border="1" cellspacing="0" cellpadding="0">
<tbody>
<tr>
<td valign="top" width="322">
<p>Attribute Name</p>
</td>
<td valign="top" width="304">
<p>Attribute Description</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>android:focusable</p>
</td>
<td valign="top" width="304">
<p>Sets the focusable true</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>isBalancedSlider</p>
</td>
<td valign="top" width="304">
<p>Whether the slider is balanced or not</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderMinValue</p>
</td>
<td valign="top" width="304">
<p>Slider min value</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderMaxValue</p>
</td>
<td valign="top" width="304">
<p>Slider max value</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderCurrentValue</p>
</td>
<td valign="top" width="304">
<p>Slider current value</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>valueDisplayTextStyle</p>
</td>
<td valign="top" width="304">
<p>Slider value text style</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>stepSize</p>
</td>
<td valign="top" width="304">
<p>Step size for step slider</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderBgDrawable</p>
</td>
<td valign="top" width="304">
<p>Slider Container drawable</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderContainerWidth</p>
</td>
<td valign="top" width="304">
<p>Slider Container width</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderContainerHeight</p>
</td>
<td valign="top" width="304">
<p>Slider Container height</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderDrawable</p>
</td>
<td valign="top" width="304">
<p>Slider bar drawable</p>
</td>
<tr>
<td valign="top" width="322">
<p>sliderFill</p>
</td>
<td valign="top" width="304">
<p>Slider fill drawable</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderWidth</p>
</td>
<td valign="top" width="304">
<p>Slider bar width</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderHeight</p>
</td>
<td valign="top" width="304">
<p>Slider bar height</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>upArrowIconDrawable</p>
</td>
<td valign="top" width="304">
<p>Up arrow drawable</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>downArrowIconDrawable</p>
</td>
<td valign="top" width="304">
<p>Down arrow drawable</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>upArrowIconPressedDrawable</p>
</td>
<td valign="top" width="304">
<p>Up arrow activated drawable</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>downArrowIconPressedDrawable</p>
</td>
<td valign="top" width="304">
<p>Down arrow activated drawable</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderLabelText</p>
</td>
<td valign="top" width="304">
<p>Sets Slider label</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>sliderLabelTextStyle</p>
</td>
<td valign="top" width="304">
<p>Sets Slider label text style</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>leftLabelBalancedSlider</p>
</td>
<td valign="top" width="304">
<p>Sets left label for balanced slider</p>
</td>
</tr>
<tr>
<td valign="top" width="322">
<p>rightLabelBalancedSlider</p>
</td>
<td valign="top" width="304">
<p>Sets right label for balanced slider</p>
</td>
</tr>
</tbody>
</table>
<h3><span style="text-decoration: underline; font-family: arial, helvetica, sans-serif; font-size: large;">Use case scenarios </span></h3>
<h4><span style="font-family: arial, helvetica, sans-serif; font-size: small;">1)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Slider Creation Sequence</span></h4>
<img src="{@docRoot}/slider_design_asset/SliderSeqDiagramCreation.gif"/>
<p><span style="font-family: arial, helvetica, sans-serif;"><strong><span style="font-size: small;">2)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Handling DPAP UP key press(Same sequence for DPAP DOWN key press)</span></strong></span></p>
<img src="{@docRoot}/slider_design_asset/SliderSeqDiagramUpKeyPress.gif"/>
<p><span style="font-family: arial, helvetica, sans-serif;"><strong><span style="font-size: small;">3)&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Handling DPAP UP key long press(Same sequence for long press of DPAP DOWN key)</span></strong> &nbsp;</span></p>
<img src="{@docRoot}/slider_design_asset/SliderSeqDiagramLongUpKeyPress.gif"/>
<h3><span style="text-decoration: underline;">Execution Design&nbsp;</span></h3>
<h3>Handling Focus</h3>
<p><span style="font-family: arial, helvetica, sans-serif; font-size: small;">The view gets the focus from android framework if the view is focusable.</span></p>
<h3>Handling Keys</h3>
<p>If the view is focusable and gets focus, the key is routed to this view.</p>
<p>The user can increment or decrement the slider value using up and down keys.&nbsp;</p>
<p>The code snippet for KEYCODE_DPAD_DOWN is as below:</p>
<table style="background-color: #e4e4e6;" border="1">
<tbody>
<tr>
<td valign="top" width="328">
<pre>case KeyEvent.KEYCODE_DPAD_DOWN: {<br />			if (mCurrentValue &gt; mMinValue) {<br />				event.startTracking();          //For long down key press <br />				updateBottomIconOrLabel(); <br />				updateTopIconOrLabel();<br />				mCurrentValue = (mCurrentValue - mUpdateResolution) &lt; mMinValue ? mMinValue<br />						: (mCurrentValue - mUpdateResolution);<br />				updateSliderFill();<br />				if (mValueChangeListener != null) {<br />					mValueChangeListener.onSliderValueChanged(this,<br />							mMinValue, mCurrentValue, mMaxValue);<br />				}<br />				return true;<br /><br />			} else {<br />				return false;<br />			}<br />		}</pre>
</td>
</tr>
</tbody>
</table>
<pre>The code snippet for KEYCODE_DPAD_UP is as below:</pre>
<table style="background-color: #e4e4e6;" border="1">
<tbody>
<tr>
<td valign="top" width="328">
<pre>case KeyEvent.KEYCODE_DPAD_UP: {<br />			if (mCurrentValue &lt; mMaxValue) {<br />				event.startTracking(); //For long up key press,Expained in handling long key press<br />				updateBottomIcon();<br />				updateTopIcon();<br />				mCurrentValue = (mCurrentValue + mUpdateResolution) &gt; mMaxValue ? mMaxValue<br />						: (mCurrentValue + mUpdateResolution);<br />				updateSliderFill();<br />				if (mValueChangeListener != null) {<br />					mValueChangeListener.onSliderValueChanged(this,<br />							mMinValue, mCurrentValue, mMaxValue);<br />				}<br />				return true;<br /><br />			} else {<br />				return false;<br />			}<br /><br />		}</pre>
</td>
</tr>
</tbody>
</table>
<h3><span style="font-size: medium;">Handling long press of up and down Keys</span></h3>
<pre>onKeyLongPress called when a long press has occurred. In order to receive this callback, event change must return true from onKeyDown(int, KeyEvent) <br />and call startTracking() on the event.</pre>
<p>To realize this, an object animator is used.Following values are set to the animator:</p>
<ul>
<li>Start and end value : &nbsp;Animator to calculate the interpolated value</li>
</ul>
<ul>
<li>Total duration&agrave; : Total duration for completion of the animation</li>
</ul>
<p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;We need to set this because we have to define what is the time duration needed to reach the max value from minimum value</p>
<p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;This time duration is specified and aligned with design team</p>
<ul>
<li>Interpolator(deceleration interpolator) &agrave; to indicate what is the animation used</li>
</ul>
<ul>
<li>Property &agrave; to indicate what to change</li>
</ul>
<p>Below is the code snippet:</p>
<table style="background-color: #e4e4e6;" border="1">
<tbody>
<tr>
<td valign="top" width="328">
<pre>ObjectAnimator mIncrementObjectAnimator = new ObjectAnimator();</pre>
<pre>mIncrementObjectAnimator.setDuration(mDuration); // mDuration : Duration is 2500 ms given by UXD</pre>
<pre>mIncrementObjectAnimator.setTarget(Slider.this); // Sets the target</pre>
<pre>//DecelerateInterpolator is An interpolator where the rate of change starts out quickly and and then decelerates.</pre><pre>//mFactor is degree to which the animation should be eased.</pre>
<pre>mIncrementObjectAnimator.setInterpolator(new DecelerateInterpolator(mFactor));</pre>
<pre>mIncrementObjectAnimator.setPropertyName("increment"); //Sets the name of the property that will be animated</pre>
<pre>mIncrementObjectAnimator.setIntValues(int... values); //Sets int values that will be animated between.</pre>
</td>
</tr>
</tbody>
</table>
<h3><span style="text-decoration: underline; font-family: arial, helvetica, sans-serif; font-size: large;">Test Strategy </span></h3>Unit Testing: It will be done with JUnit, UiAutomator and Instrumentation.<br /> <br />SubSystem Testing : Slider Test apk will be provided to SubSystem for testing with unit test report containing all the critical functionalities.</h3>
<h3><span style="text-decoration: underline; font-family: arial, helvetica, sans-serif; font-size: large;">Deliverable </span></h3>Slider Component<br /> <br />SliderTestApp.apk</h3>
</br>*/

public class Slider extends View{
	private static final String TAG = "VerticalSeekBar";
	// Changed all the attribute names starting with m... eg mWidth, mHeight as comment given by Sudhir.
	// With reference to code review comment AP153,added scope qualifier like public or private to variables.
	private Drawable mSliderDrwbl, mSliderFillDrwbl, mTopIconDrwbl, mBottomIconDrwbl,mUpArrowDrwbl, mDownArrowDrwbl,mISFMinColorDrwl,mISFMaxColorDrwl,mISFHeaderColorDrwl;
	private Drawable mSliderContainerDrwbl, mUpArrowPressedDrwbl,mDownArrowPressedDrwbl;
	private String mSliderLabel,mBalancedSliderTopLabel,mBalancedSliderBottomLabel;
	private Paint mCurrentValuePaint,mMinMaxValuePaint, mLabelPaint;
	private TextPaint mLabelTextPaint; //Removed minValueTextPaint,maxValueTextPaint. Using mValueTextPaint for drawing min max value.
	private TextPaint mCurrentValueTextPaint,mMinMaxValueTextPaint;
	private TextAppearanceSpan mCurrentValueTxtSpan,mMinMaxValueTxtSpan, mLabelTxtSpan; // helper class to retrive the txt sytle and
	// appearence feature
	private int mMinValue, mCurrentValue, mMaxValue, mUpdateResolution,mSliderValuepading;
	/**
	 * this has to be a spec by the design. or else we may not have a correct
	 * looking progress bar
	 */
	private int mMaxSliderBarWidth;
	private int mMaxSliderBarHeight;
	private static long mDuration = 2500;
	private boolean mBalancedSlider;
	private int mSliderContainerWidth, mSliderContainerHeight;
	private SliderValueChangeListener mValueChangeListener;

	private int state[] = { android.R.attr.state_checked };
	private ObjectAnimator mObjectAnimator;
	private StaticLayout mTextLayout=null;
	private StaticLayout mBalancedSliderTopLabelLayout,mBalancedSliderBottomLabelLayout;
	private float mPadding;
	private float mTopIconLeftPadding,mTopIconTopPadding,mSliderBarTopPadding,mBottomIconLeftPadding,mBottomIconTopPadding;
	private float mLabelPadding;
	private float mSliderFillPadding;
	private int mCurrentHt;
	private boolean mIsIsfSlider,mIsLabelSlider;
	private float mMinMaxValueTextHeight;
	private int mISFMinColor,mISFMaxColor,mISFHeaderColor;
	private String mTopLabel,mBottomLabel,mCenterLabel; 
	private Rect bounds = new Rect();
	// With reference to code review comment AP157, removed variables and functions which are not used.
	public Slider(Context context) {
		this(context, null, 0);
	}

	public Slider(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	
/**Interface definition for a callback to be invoked when slider value changes.*/
	public interface SliderValueChangeListener {
/**Called whenever slider value changes*/
		public void onSliderValueChanged(View s, int mMinValue,
                                         int mCurrentValue, int mMaxValue);
	}

	public Slider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LogHelper.e(TAG, "Slider - Constructor");
		if (attrs != null && attrs.getStyleAttribute() == R.style.DefaultVerticalSliderWithLabel)
		{
			mIsLabelSlider=true;
		}else
		{
			mIsLabelSlider=false;
		}
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.VerticalSlider);
		if(a.getIndexCount()==0)
		{
			int defaultAttrs[]={ R.attr.isBalancedSlider,R.attr.sliderValueDisplay,R.attr.sliderMinValue,R.attr.sliderMaxValue,R.attr.sliderCurrentValue
					,R.attr.currentvalueDisplayTextStyle,R.attr.minmaxvalueDisplayTextStyle,R.attr.updateResolution
					,R.attr.sliderBarDrawable,R.attr.sliderBarFill,R.attr.sliderBarWidth,R.attr.sliderBarHeight
			        ,R.attr.upArrowIconDrawable,R.attr.downArrowIconDrawable,R.attr.upArrowIconPressedDrawable,R.attr.downArrowIconPressedDrawable
			        ,R.attr.bottomIconDrawable,R.attr.sliderLabelText,R.attr.balancedSliderTopLabelText,R.attr.balancedSliderBottomLabelText,R.attr.sliderLabelTextStyle,R.attr.sliderContainerDrawable,R.attr.sliderContainerWidth,R.attr.sliderContainerHeight
			};
	        a=context.obtainStyledAttributes(R.style.DefaultVerticalSlider,defaultAttrs);  
		}
		setFocusable(true);
			mBalancedSlider = a.getBoolean(
					R.styleable.VerticalSlider_isBalancedSlider, false);

			mMinValue = a.getInt(R.styleable.VerticalSlider_sliderMinValue, 0);

			mMaxValue = a.getInt(R.styleable.VerticalSlider_sliderMaxValue, 100);


			mCurrentValue = a.getInt(
					R.styleable.VerticalSlider_sliderCurrentValue, 0);
			
			if(mCurrentValue<mMinValue || mCurrentValue>mMaxValue)
			{
				mCurrentValue=mMinValue;
			}

			mCurrentValueTxtSpan = new TextAppearanceSpan(context, a.getResourceId(
					R.styleable.VerticalSlider_currentvalueDisplayTextStyle, -1));
			mMinMaxValueTxtSpan = new TextAppearanceSpan(context, a.getResourceId(
					R.styleable.VerticalSlider_minmaxvalueDisplayTextStyle, -1));

			mCurrentValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			mMinMaxValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

			mCurrentValueTextPaint = new TextPaint(mCurrentValuePaint);
			mMinMaxValueTextPaint  = new TextPaint(mMinMaxValuePaint);
			//issue fix AN-1739
			mCurrentValueTextPaint.setTextAlign(Align.CENTER);
			mCurrentValueTxtSpan.updateDrawState(mCurrentValueTextPaint); // this can be used
			
			mMinMaxValueTextPaint.setTextAlign(Align.RIGHT);
			mMinMaxValueTxtSpan.updateDrawState(mMinMaxValueTextPaint); // this can be used
			
			// directly to set
			// the text values

			mUpdateResolution = a.getInt(
					R.styleable.VerticalSlider_updateResolution, -1);


			mSliderContainerDrwbl = getResources().getDrawable(a.getResourceId(R.styleable.VerticalSlider_sliderContainerDrawable, -1));

			mSliderContainerDrwbl.setState(getDrawableState());

			mSliderContainerWidth = a.getDimensionPixelSize(
					R.styleable.VerticalSlider_sliderContainerWidth, 0);

			mSliderContainerHeight = a.getDimensionPixelSize(
					R.styleable.VerticalSlider_sliderContainerHeight, 0);

			mSliderDrwbl = getResources().getDrawable(
					a.getResourceId(R.styleable.VerticalSlider_sliderBarDrawable,
							-1));

			mSliderFillDrwbl = getResources().getDrawable(
					a.getResourceId(R.styleable.VerticalSlider_sliderBarFill, -1));



			mMaxSliderBarWidth = a.getDimensionPixelSize(
					R.styleable.VerticalSlider_sliderBarWidth, 0);

			mMaxSliderBarHeight = a.getDimensionPixelSize(
					R.styleable.VerticalSlider_sliderBarHeight, 0);
			

			mTopIconDrwbl = getResources().getDrawable(
					a.getResourceId(R.styleable.VerticalSlider_upArrowIconDrawable,
							-1));
			mUpArrowDrwbl = mTopIconDrwbl;

			mUpArrowPressedDrwbl = getResources()
					.getDrawable(
							a.getResourceId(
									R.styleable.VerticalSlider_upArrowIconPressedDrawable,
									-1));
			mDownArrowPressedDrwbl = getResources().getDrawable(
					a.getResourceId(
							R.styleable.VerticalSlider_downArrowIconPressedDrawable,
							-1));

			mBottomIconDrwbl = getResources().getDrawable(
					a.getResourceId(
							R.styleable.VerticalSlider_downArrowIconDrawable, -1));
			mDownArrowDrwbl = mBottomIconDrwbl;

			mSliderLabel = a
					.getString(R.styleable.VerticalSlider_sliderLabelText);
            mBalancedSliderTopLabel=a.getString(R.styleable.VerticalSlider_balancedSliderTopLabelText);
			mBalancedSliderBottomLabel=a.getString(R.styleable.VerticalSlider_balancedSliderBottomLabelText);
			mTopLabel=a.getString(R.styleable.VerticalSlider_sliderTopLabelText);
			mBottomLabel=a.getString(R.styleable.VerticalSlider_sliderBottomLabelText);
			mCenterLabel=a.getString(R.styleable.VerticalSlider_sliderCenterLabelText);
			mLabelTxtSpan = new TextAppearanceSpan(context, a.getResourceId(
					R.styleable.VerticalSlider_sliderLabelTextStyle, -1));

			mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

			mLabelTextPaint = new TextPaint(mLabelPaint);


			mLabelTxtSpan.updateDrawState(mLabelTextPaint);

			//issue fix 	AN-1683

			mLabelTextPaint.setTextAlign(Align.CENTER);
			mIsIsfSlider=a.getBoolean(R.styleable.VerticalSlider_isISFSlider, false);
			try
			{
				mISFMinColorDrwl=getResources().getDrawable(a.getResourceId(R.styleable.VerticalSlider_isfMinColorDrawable, -1));
			}catch(Exception e)
			{
				mISFMinColorDrwl=getResources().getDrawable(R.drawable.isf_min_color);
			}
			try
			{	
				mISFMaxColorDrwl=getResources().getDrawable(a.getResourceId(R.styleable.VerticalSlider_isfMaxColorDrawable, -1));
			}catch(Exception e)
			{
				mISFMaxColorDrwl=getResources().getDrawable(R.drawable.isf_max_color);
			}
			try
			{	
				mISFHeaderColorDrwl=getResources().getDrawable(a.getResourceId(R.styleable.VerticalSlider_isfHeaderColorDrawable, -1));
			}catch(Exception e)
			{
				mISFHeaderColorDrwl=getResources().getDrawable(R.drawable.isf_header_color);
			}	
			a.recycle();

		
		
		setClickable(true);
		setEnabled(true);
		if(mMinValue<0 && mMaxValue>0)
		{
			mBalancedSlider=true;
		}else
		{
			mBalancedSlider=false;
		}
		mSliderValuepading=(int) getResources().getDimension(R.dimen.slider_normal_padding_side_value_to_bar_container_height);
		mObjectAnimator = new ObjectAnimator();
		mObjectAnimator.setDuration(mDuration);
		mObjectAnimator.setTarget(Slider.this);
		
	
		mMinMaxValueTextPaint.getTextBounds(String.valueOf(mMaxValue), 0, String.valueOf(mCurrentValue).length(), bounds);
		mMinMaxValueTextHeight=(float)bounds.height()/2;
		refreshDrawableState();

	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
	}

	public void setOnValueChangeListener(SliderValueChangeListener listner) {
		mValueChangeListener = listner;
	}

	private void updateSliderBg() {
		mSliderContainerDrwbl.setBounds(0,0,mSliderContainerWidth,mSliderContainerHeight);
		mSliderContainerDrwbl.setAlpha(0);
		invalidate();
	}


	private void updateSliderBar() {
		mSliderDrwbl.setBounds(0,0,mMaxSliderBarWidth,mMaxSliderBarHeight);
		invalidate();
	}
	private void updateIsfSliderMinColorDrwl() {
		mISFMinColorDrwl.setBounds(0,0, (int)getResources().getDimension(R.dimen.slider_isf_min_color_width), (int)getResources().getDimension(R.dimen.slider_isf_min_color_height));
		invalidate();
	}
	private void updateIsfSliderMaxColorDrwl() {
		mISFMaxColorDrwl.setBounds(0,0, (int)getResources().getDimension(R.dimen.slider_isf_max_color_width), (int)getResources().getDimension(R.dimen.slider_isf_max_color_height));
		invalidate();
	}
	private void updateIsfSliderHeaderColorDrwl() {
		mISFHeaderColorDrwl.setBounds(0,0, (int)getResources().getDimension(R.dimen.slider_isf_header_color_width), (int)getResources().getDimension(R.dimen.slider_isf_header_color_height));
		invalidate();
	}
	
	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);

		mergeDrawableStates(drawableState, state);

		return drawableState;
	}

	private void updateFill()
	{
		/*calculate height of the fill
		 two seperate calculations for range slider and balanced slider 
		 range slider starts from minimum value and for balanced slider it starts from zero.
		 so for balanced slider search for the position where there is zero.
		 also remember balanced slider is one where the minimum value is less than zero and max greater
		 than zero. however it shud be remembered that balanced slider need not be accurately balanced. 
		 eg max = 25 and min = -20 is possible.
		 */ 
		try
		{
		Rect bounds = mSliderDrwbl.getBounds();
		int totalHeight = bounds.bottom - bounds.top;
			if(mBalancedSlider)
			{
				mCurrentHt = (mCurrentValue * totalHeight)/(mMaxValue - mMinValue);
			}
			else
			{	
				mCurrentHt = (mCurrentValue - mMinValue) * totalHeight/(mMaxValue - mMinValue);
			}
			if(mCurrentHt<0)
			{
				mSliderFillDrwbl.setBounds(0,0 ,mSliderDrwbl.getBounds().right, -mCurrentHt);
			}
			else
			{
				mSliderFillDrwbl.setBounds(0, -mCurrentHt,mSliderDrwbl.getBounds().right, 0);
			}
		invalidate();
		}catch(ArithmeticException e)
		{
			LogHelper.d(TAG,e.getMessage());
		}
	}

	// Changes done for code review comment Ref:AP161
	private void updateTopIcon()
	{ 
		mTopIconDrwbl.setBounds(0,0,getResources().getDimensionPixelSize(R.dimen.slider_normal_arrow_up_width),getResources().getDimensionPixelSize(R.dimen.slider_normal_arrow_up_height));
		invalidate();
	}
	// Changes done for code review comment Ref:AP162
	private void updateBottomIcon() {

		mBottomIconDrwbl.setBounds(0,0,getResources().getDimensionPixelSize(R.dimen.slider_normal_arrow_down_width),getResources().getDimensionPixelSize(R.dimen.slider_normal_arrow_down_height));
		invalidate();
	}

        public int getCurrentValue() {
	        return mCurrentValue;
	}

	@Deprecated
	public void setCurrentValue(int value) {
		if (value <= mMaxValue && value >= mMinValue) {
			mCurrentValue = value;
			updateFill();
		}
		
	}
	
	@Deprecated
	//Changes done for code review comment Ref:AP163
	public void setMaxValue(int mMaxValue)
	{
		this.mMaxValue = mMaxValue;
		if(mCurrentValue>mMaxValue)
		{
			mCurrentValue=mMinValue;
		}
		if(this.mMinValue<0 && mMaxValue>0)
		{
			mBalancedSlider=true;
		}else
		{
			mBalancedSlider=false;
		}
	}
	
	@Deprecated
	//Changes done for code review comment Ref:AP163
	public void setMinValue(int mMinValue) 
	{
		this.mMinValue = mMinValue;
		if(mCurrentValue<mMinValue)
		{
			mCurrentValue=mMinValue;
		}
		if(this.mMinValue<0 && mMaxValue>0)
		{
			mBalancedSlider=true;
		}else
		{
			mBalancedSlider=false;
		}
        
	}
	
	@Deprecated
	//Changes done for code review comment Ref:AP164
	public void setMinMaxValue(int mMinValue, int mMaxValue) 
	{
		this.mMinValue = mMinValue;
		this.mMaxValue = mMaxValue;
		if(mCurrentValue<mMinValue && mCurrentValue>mMaxValue)
		{
			mCurrentValue=mMinValue;
		}
		
		if(this.mMinValue<0 && mMaxValue>0)
		{
			mBalancedSlider=true;
		}else
		{
			mBalancedSlider=false;
		}
	}
/**Sets the slider values(min, max, current and step value)*/
	public void setSliderValue(int mMinValue, int mMaxValue, int mCurrentValue,int stepValue) 
	{
		this.mMinValue = mMinValue;
		this.mMaxValue = mMaxValue;
		if(this.mMinValue<0 && mMaxValue>0)
		{
			mBalancedSlider=true;
		}else
		{
			mBalancedSlider=false;
		}
		mUpdateResolution=stepValue;
		if (mCurrentValue <= mMaxValue && mCurrentValue >= mMinValue) {
			this.mCurrentValue = mCurrentValue;
			updateFill();
		} else if(mCurrentValue < mMinValue){
			LogHelper.e(TAG,"Current value is less than minimum Value");
			this.mCurrentValue = mMinValue;
			updateFill();
		} else if(mCurrentValue > mMaxValue){
			LogHelper.e(TAG,"Current value is greater than Maximum Value");
			this.mCurrentValue = mMaxValue;
			updateFill();
		}
		
	}
	
	public int getMinValue()
	{
		return mMinValue;
	}
	
	public int getMaxValue()
	{
		return mMaxValue;
	}
	public void setLabel(String txt) {
		if (txt != null) {
			mSliderLabel = txt;
			mTextLayout=null;
			invalidate();
		}
	}
	
	public String getLabel()
	{
		return mSliderLabel;
	}
	
	public void setBalancedSliderTopLabel(String label)
	{
		mBalancedSliderTopLabel=label;
		mBalancedSliderTopLabelLayout=null;
		invalidate();
	}
	
	public String getBalancedSliderTopLabel()
	{
		return mBalancedSliderTopLabel;
		
	}
	public void setBalancedSliderBottomLabel(String label)
	{
		mBalancedSliderBottomLabel=label;
		mBalancedSliderBottomLabelLayout=null;
		invalidate();
	}
	public String getBalancedSliderBottomLabel()
	{
		return mBalancedSliderBottomLabel;
		
	}
	/**
	 * This function is used to set the resolution of increase or decrease of
	 * the slider value E.g. If the step value is set to 1, then the increments
	 * or decrements on Key press or on the clicks will be by one if the step is
	 * set to 5, the increments or decrements will be by 5. However in case in
	 * the increments is different from that of the required steps, this will be
	 * adjusted at the ends of the slider. For E.g, the mMinValue is 0 and max
	 * value is 20 and the step is 3. so the initial increments will be by 3 and
	 * when the value reaches 18 and then the user increments again then the
	 * last increment will be 2.
	 * 
	 * @param stepValue
	 *            : Describes the resolution of change
	 */

	@Deprecated
	public void setValueStep(int stepValue) {
		mUpdateResolution = stepValue;
	}

	@Override
	public void onDraw(Canvas c) {
		mLabelTextPaint.setColor(mLabelTxtSpan.getTextColor().getColorForState(getDrawableState(), mLabelTxtSpan.getTextColor().getDefaultColor()));
		mMinMaxValueTextPaint.setColor(mMinMaxValueTxtSpan.getTextColor().getColorForState(getDrawableState(), mMinMaxValueTxtSpan.getTextColor().getDefaultColor()));
		mCurrentValueTextPaint.setColor(mCurrentValueTxtSpan.getTextColor().getColorForState(getDrawableState(), mCurrentValueTxtSpan.getTextColor().getDefaultColor()));	
		if(mMaxValue<mMinValue)
		{
			LogHelper.e(TAG, "Slider Max Value is less than Min Value");
		}
		Rect bgBounds=mSliderContainerDrwbl.getBounds();
		
       
		mSliderContainerDrwbl.draw(c);
		mPadding=(float)(mSliderContainerWidth-mMaxSliderBarWidth)/2;
		mTopIconLeftPadding=(float)(mSliderContainerWidth-mTopIconDrwbl.getIntrinsicWidth())/2;
		mTopIconTopPadding=(getResources().getDimension(R.dimen.slider_normal_header_text_height)+getResources().getDimension(R.dimen.slider_normal_padding_header_textow_height));
		mBottomIconLeftPadding=(float)(mSliderContainerWidth-mBottomIconDrwbl.getIntrinsicWidth())/2;
		mSliderBarTopPadding=mTopIconTopPadding+(getResources().getDimension(R.dimen.slider_normal_arrow_up_height))+(getResources().getDimension(R.dimen.slider_normal_padding_arrow_up_to_bar_container_height));
		mBottomIconTopPadding=mSliderBarTopPadding+(getResources().getDimension(R.dimen.slider_normal_bar_container_height))+(getResources().getDimension(R.dimen.slider_normal_padding_bar_container_to_arrow_down_height));
		c.save();

		//Changes done for code review comment Ref:AP166
		if(mTextLayout==null)
		{
			Configuration config = getResources().getConfiguration();
			if(config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
				mTextLayout = new StaticLayout(mSliderLabel,mLabelTextPaint,bgBounds.width(), Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false);
				if(mTextLayout.getLineCount()>1)
				{
					mTextLayout = new StaticLayout(mSliderLabel,0,mTextLayout.getLineStart (2),mLabelTextPaint,bgBounds.width(), Alignment.ALIGN_OPPOSITE, 1.0f, 0.0f, false);
				}
			}else
			{
				mTextLayout = new StaticLayout(mSliderLabel,mLabelTextPaint,bgBounds.width(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
				if(mTextLayout.getLineCount()>1)
				{
					mTextLayout = new StaticLayout(mSliderLabel,0,mTextLayout.getLineStart (2),mLabelTextPaint,bgBounds.width(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
				}
			}
			mLabelPadding = bgBounds.exactCenterX();
		}
		c.translate(mLabelPadding,0);
		
	        
		mTextLayout.draw(c);
		c.restore();
		c.save();
		c.translate(mTopIconLeftPadding,mTopIconTopPadding);
		mTopIconDrwbl.draw(c);
		c.restore();

		c.save();
		c.translate(mPadding,mSliderBarTopPadding);
		mSliderDrwbl.draw(c);
		if(!mIsLabelSlider)
		{
			c.drawText(String.valueOf(mMaxValue).replace("-", "- ").trim(), -mSliderValuepading, mSliderDrwbl.getBounds().top+mMinMaxValueTextHeight,
					mMinMaxValueTextPaint);
		
			c.drawText(String.valueOf(mMinValue).replace("-", "- ").trim(), -mSliderValuepading, mSliderDrwbl.getBounds().bottom+mMinMaxValueTextHeight,
					mMinMaxValueTextPaint);
			c.drawText(String.valueOf((mMaxValue+mMinValue)/2).replace("-", "- ").trim(), -mSliderValuepading,(float)(mSliderDrwbl.getBounds().bottom/2.0)+mMinMaxValueTextHeight,
					mMinMaxValueTextPaint);
		}else
		{
			if(mTopLabel!=null)
			{
				c.drawText(mTopLabel, -mSliderValuepading, mSliderDrwbl.getBounds().top+mMinMaxValueTextHeight,
					mMinMaxValueTextPaint);
			}
			if(mBottomLabel!=null)
			{
				c.drawText(mBottomLabel, -mSliderValuepading, mSliderDrwbl.getBounds().bottom+mMinMaxValueTextHeight,
					mMinMaxValueTextPaint);
			}
			if(mCenterLabel!=null)
			{
				c.drawText(mCenterLabel, -mSliderValuepading,(float)(mSliderDrwbl.getBounds().bottom/2.0)+mMinMaxValueTextHeight,
					mMinMaxValueTextPaint);
			}
		}
		if(mBalancedSlider)
		{
			int x=(mMaxValue-mMinValue);
			mSliderFillPadding=(float)(( (float)mMaxSliderBarHeight/x)*mMaxValue);
			c.translate(0,mSliderFillPadding);
		}else
		{
				
			c.translate(0,mSliderDrwbl.getBounds().bottom);
		}

		mSliderFillDrwbl.draw(c);
		
		if(mUpdateResolution>1)
		{
			c.restore();
			c.save();
			c.translate(mPadding,mSliderBarTopPadding);
			float stepHeight=(float)mSliderDrwbl.getBounds().bottom/10;
			if(!mIsLabelSlider)
			{
				Paint linePaint=new Paint();
				linePaint.setStrokeWidth(1);
				linePaint.setColor(getResources().getColor(R.color.slider_step_line_color,null));
				for(int i=1;i<=9;i++)
				{
					c.drawLine(-1, i*stepHeight, mSliderDrwbl.getBounds().right, i*stepHeight,linePaint);
				}
			}
			if(mBalancedSlider)
			{
				c.translate(0,mSliderFillPadding);
			}else
			{
				c.translate(0,mSliderDrwbl.getBounds().bottom);

			}
		}
		
		c.restore();
		if(!mIsLabelSlider)
		{
			c.save();
			c.translate(mPadding,mSliderBarTopPadding);
			c.drawText(String.valueOf(mCurrentValue).replace("-", "- ").trim(),(float)(mSliderDrwbl.getBounds().right/2.0),(float)(mSliderDrwbl.getBounds().bottom/2.0+mMinMaxValueTextHeight), mCurrentValueTextPaint);
			c.restore();
		}
		c.save();
		c.translate(mBottomIconLeftPadding,mBottomIconTopPadding);
		mBottomIconDrwbl.draw(c);
		c.restore();
		if(mBalancedSlider)
		{
			if(mBalancedSliderTopLabel!=null)
			{
				c.save();
				c.translate(getResources().getDimension(R.dimen.slider_balance_top_right_verticle_text_x1),(float)(mSliderDrwbl.getBounds().bottom/2.0));
				c.rotate(-90);
				if(mBalancedSliderTopLabelLayout==null)
				{
					mBalancedSliderTopLabelLayout = new StaticLayout(mBalancedSliderTopLabel,mLabelTextPaint,(int) getResources().getDimension(R.dimen.slider_balance_top_right_verticle_text_height), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
				}
				
				mBalancedSliderTopLabelLayout.draw(c);
				c.restore();
			}
			if(mBalancedSliderBottomLabel!=null)
			{
				c.save();
				c.translate(getResources().getDimension(R.dimen.slider_balance_bottom_right_verticle_text_x1),mSliderDrwbl.getBounds().bottom);
				c.rotate(-90);
				if(mBalancedSliderBottomLabelLayout==null)
				{
					mBalancedSliderBottomLabelLayout = new StaticLayout(mBalancedSliderBottomLabel,mLabelTextPaint,(int) getResources().getDimension(R.dimen.slider_balance_bottom_right_verticle_text_height), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
				}
				
				mBalancedSliderBottomLabelLayout.draw(c);
				c.restore();
			}
		}
		if(mIsIsfSlider)
		{
			c.save();
			c.translate(getResources().getDimension(R.dimen.slider_isf_header_color_x1),getResources().getDimension(R.dimen.slider_isf_header_color_y1));
			mISFHeaderColorDrwl.draw(c);
			c.restore();
			c.save();
			c.translate(getResources().getDimension(R.dimen.slider_isf_max_color_x1),getResources().getDimension(R.dimen.slider_isf_max_color_y1));
			mISFMaxColorDrwl.draw(c);
			c.restore();
			c.save();
			c.translate(getResources().getDimension(R.dimen.slider_isf_min_color_x1),getResources().getDimension(R.dimen.slider_isf_min_color_y1));
			mISFMinColorDrwl.draw(c);
			c.restore();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(isEnabled())
		{
		
				LogHelper.i("VerticalSeekBar", "onKeyDown");
				refreshDrawableState();
				//Changes done for code review comment Ref:AP167	
				switch (keyCode) {
		
				case KeyEvent.KEYCODE_DPAD_DOWN: {
					if (mCurrentValue > mMinValue) {
						event.startTracking();
						mBottomIconDrwbl = mDownArrowPressedDrwbl;
						mTopIconDrwbl = mUpArrowDrwbl;
						updateBottomIcon();
						updateTopIcon();
						mCurrentValue = (mCurrentValue - mUpdateResolution) < mMinValue ? mMinValue
								: (mCurrentValue - mUpdateResolution);
						updateFill();
						if (mValueChangeListener != null) {
							mValueChangeListener.onSliderValueChanged(this,
									mMinValue, mCurrentValue, mMaxValue);
						}
						return true;
		
					} else {
						return false;
					}
				}
		
				case KeyEvent.KEYCODE_DPAD_UP: {
		
					if (mCurrentValue < mMaxValue) {
						event.startTracking();
						mTopIconDrwbl = mUpArrowPressedDrwbl;
						mBottomIconDrwbl = mDownArrowDrwbl;
						updateBottomIcon();
						updateTopIcon();
						mCurrentValue = (mCurrentValue + mUpdateResolution) > mMaxValue ? mMaxValue
								: (mCurrentValue + mUpdateResolution);
						updateFill();
						if (mValueChangeListener != null) {
							mValueChangeListener.onSliderValueChanged(this,
									mMinValue, mCurrentValue, mMaxValue);
						}
						return true;
		
					} else {
						return false;
					}
		
				}
		
		
				default:
					return false;
		
				}
		}
		return false;

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent ev) {
		LogHelper.i("VerticalSeekBar", "onKeyUP");
		if(mCurrentValue==0)
		{
			mTopIconDrwbl = mUpArrowPressedDrwbl;
			mBottomIconDrwbl = mDownArrowPressedDrwbl;
		}else
		{
			mTopIconDrwbl = mUpArrowDrwbl;
			mBottomIconDrwbl = mDownArrowDrwbl;
		}
		updateBottomIcon();
		updateTopIcon();
		if(mObjectAnimator.isStarted()) {
			mObjectAnimator.cancel();
		}
			
		return false;
	}

	public void setIncrement(int value) {

		if (mCurrentValue < mMaxValue && mCurrentValue<value) {

			if(mUpdateResolution>1)
			{
				mCurrentValue = (mCurrentValue + mUpdateResolution) > mMaxValue ? mMaxValue
						: (mCurrentValue + mUpdateResolution);
			}else
			{
				mCurrentValue = value > mMaxValue ? mMaxValue : value;
			}
			updateFill();
			if (mValueChangeListener != null) {
				mValueChangeListener.onSliderValueChanged(this, mMinValue,
						mCurrentValue, mMaxValue);
			}

		}

	}

	public int getIncrement() {
		return mCurrentValue;
	}

	public void setDecrement(int value) {

		if (mCurrentValue > mMinValue && mCurrentValue>value) {

			if(mUpdateResolution>1)
			{
				mCurrentValue = (mCurrentValue - mUpdateResolution) < mMinValue ? mMinValue
						: (mCurrentValue - mUpdateResolution);

			}else
			{
				mCurrentValue = value < mMinValue ? mMinValue : value;
			}
			updateFill();
			if (mValueChangeListener != null) {
				mValueChangeListener.onSliderValueChanged(this, mMinValue,
						mCurrentValue, mMaxValue);
			}

		}

	}

	public int getDecrement() {
		return mCurrentValue;
	}

	/*
	 * Added for PR 447
	 */

	public interface ISliderOnTouchListener {
		public void onSliderTouchEvent(MotionEvent event);
	}

	public void setSliderOnTouchListener(
			ISliderOnTouchListener mSliderOnTouchListener) {
	}

	

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		mSliderFillDrwbl.setState(getDrawableState());
		mUpArrowDrwbl.setState(getDrawableState());
		mDownArrowDrwbl.setState(getDrawableState());
		invalidate();

	}
    /**Called to determine the size requirements for this view and all of its children.*/
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int width;
		int height;

		//Measure Width
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			width = Math.min(mSliderContainerWidth, widthSize);
		} else {
			width = mSliderContainerWidth;
		}

		//Measure Height
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			height = Math.min(mSliderContainerHeight, heightSize);
		} else {
			//Be whatever you want
			height = mSliderContainerHeight;
		}
		setMeasuredDimension(width, height);

		updateSliderBg();
		updateSliderBar();
		updateFill();
		updateTopIcon();
		updateBottomIcon();
		if(mIsIsfSlider)
		{
			updateIsfSliderMaxColorDrwl();
			updateIsfSliderMinColorDrwl();
			updateIsfSliderHeaderColorDrwl();
		}

	}

	@Override
	public void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

		if(!gainFocus)
		{
			mTopIconDrwbl = mUpArrowDrwbl;
			mBottomIconDrwbl = mDownArrowDrwbl;
		}else
		{
			if(mCurrentValue==0)
			{
				mTopIconDrwbl=mUpArrowPressedDrwbl;
				mBottomIconDrwbl=mDownArrowPressedDrwbl;
			}
		}
		
		refreshDrawableState();
		updateFill();
		invalidate();


	}
   
   
	//Changes done for code review comment Ref:AP167
	@Override
	public boolean onKeyLongPress( int keyCode, KeyEvent event ) {
		if(keyCode == KeyEvent.KEYCODE_DPAD_UP )
		{
			Property incrementProperty = Property.of(Slider.class, 
					   Integer.class, "increment");
			mObjectAnimator.setProperty(incrementProperty);
			if(mUpdateResolution>1)
			{
				mObjectAnimator.setInterpolator(new LinearInterpolator());
				mObjectAnimator.setIntValues(mCurrentValue,mMaxValue);
				
			}else
			{
				mObjectAnimator.setInterpolator(new DecelerateInterpolator());
				mObjectAnimator.setIntValues(mCurrentValue,mMaxValue);
				
			}
			mObjectAnimator.start();
			mTopIconDrwbl=mUpArrowPressedDrwbl;
			mBottomIconDrwbl=mDownArrowDrwbl;
			updateBottomIcon();
			updateTopIcon();

			return true;
		}else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
		{
			Property decrementProperty = Property.of(Slider.class, 
					   Integer.class, "decrement");
			mObjectAnimator.setProperty(decrementProperty);
			if(mUpdateResolution>1)
			{
				mObjectAnimator.setInterpolator(new LinearInterpolator());
				mObjectAnimator.setIntValues(mCurrentValue,mMinValue);
				
			}else
			{
				mObjectAnimator.setInterpolator(new DecelerateInterpolator());
				mObjectAnimator.setIntValues(mCurrentValue,mMinValue);
				
			}
			mObjectAnimator.start();
			mBottomIconDrwbl=mDownArrowPressedDrwbl;
			mTopIconDrwbl=mUpArrowDrwbl;
			updateBottomIcon();
			updateTopIcon();

			return true;
		}
		else
		{
			return false;
		}


	}
	public void setISFMinColor(int color)
	{
		((GradientDrawable)mISFMinColorDrwl).setColor(color);
		mISFMinColor=color;
		invalidate();
	}
	public void setISFMaxColor(int color)
	{
		((GradientDrawable)mISFMaxColorDrwl).setColor(color);
		mISFMaxColor=color;
		invalidate();
	}
	public void setISFHeaderColor(int color)
	{
		((GradientDrawable)mISFHeaderColorDrwl).setColor(color);
		mISFHeaderColor=color;
		invalidate();
	}
	public int getISFMinColor()
	{
		return mISFMinColor;
	}
	public int getISFMaxColor()
	{
		return mISFMaxColor;
	}
	public int getISFHeaderColor()
	{
		return mISFHeaderColor;
	}
	public void setTopLabel(String label)
	{
		mTopLabel=label;
		invalidate();
	}
	public void setBottomLabel(String label)
	{
		mBottomLabel=label;
		invalidate();
	}
	public void setCenterLabel(String label)
	{
		mCenterLabel=label;
		invalidate();
	}
	public String getTopLabel()
	{
		return mTopLabel;
	}
	public String getBottomLabel()
	{
		return mBottomLabel;
	}
	public String getCenterLabel()
	{
		return mCenterLabel;
	}
}
