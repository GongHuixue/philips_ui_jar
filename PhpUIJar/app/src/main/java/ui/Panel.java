package ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class Panel extends FrameLayout {

    public Panel(Context context) {
        this(context, null, 0);
    }

    public Panel(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Panel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.i("sudhir in panel cons", " in panel const");
    }

    public void removeAndDetachChildViews() {
        while (getChildCount() > 0) {
            View v = getChildAt(0);
            detachViewFromParent(v);
            removeDetachedView(v, false);
        }
    }

}
