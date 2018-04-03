package ui.media;

import fany.phpuijar.R;
import ui.media.ui.AbsPlayBackControlLayout;


import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class PlaybackViewCreator {

    private final PopupWindow mPopupwindow;
    private final View mAnchor;

    public PlaybackViewCreator(View anchor, boolean isSlideShow) {
        mAnchor = anchor;
        mPopupwindow = new PopupWindow(anchor.getContext());

        LayoutInflater inflater = (LayoutInflater) anchor.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AbsPlayBackControlLayout mPlaybackLayout;
        if (isSlideShow) {
            mPlaybackLayout = (AbsPlayBackControlLayout) inflater.inflate(
                    R.layout.photocontrol, null);
        } else {
            mPlaybackLayout = (AbsPlayBackControlLayout) inflater.inflate(
                    R.layout.playercontrol, null);
        }
        mPopupwindow.setContentView(mPlaybackLayout);

        initWindow();
    }

    private void initWindow() {

        mPopupwindow.setFocusable(true);
        mPopupwindow.setWidth(LayoutParams.MATCH_PARENT);
        mPopupwindow.setHeight(LayoutParams.MATCH_PARENT);
        mPopupwindow.setBackgroundDrawable(null);

    }

    PopupWindow getPupupWindow() {
        return mPopupwindow;
    }

    void show() {
        mPopupwindow.showAtLocation(mAnchor, Gravity.TOP, 0, 0);
    }


}
