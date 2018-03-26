package ui.dialog;

import android.widget.Button;

import ui.dialog.ModalDialogInterface.ButtonOnClickListener;


/**
 * Created by huixue.gong on 2017/12/14.
 */

public class ModalDialogFooterButtonProp {
    private boolean enable;
    private CharSequence text;
    private ModalDialogInterface.ButtonOnClickListener clickListener;
    private Button buttonView;

    public ModalDialogFooterButtonProp(boolean enable, CharSequence text,
                                       ButtonOnClickListener clickListener) {
        this.enable = enable;
        this.text = text;
        this.clickListener = clickListener;
    }

    /**
     * @return the clickListener
     */
    public ModalDialogInterface.ButtonOnClickListener getClickListener() {
        return clickListener;
    }

    /**
     * @return the enable
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * @return the text
     */
    public CharSequence getText() {
        return text;
    }

    /**
     * @param enable the enable to set
     */
    public void setEnable(boolean enable) {
        if (buttonView != null) {
            buttonView.setEnabled(enable);
            buttonView.invalidate();
        }
        this.enable = enable;
    }

    /**
     * @param buttonView the buttonView to set
     */
    void setButtonView(Button buttonView) {
        this.buttonView = buttonView;
    }

    public Button getButtonView() {
        return this.buttonView;
    }

    public boolean requestFocus() {
        boolean isFocus = false;
        if (buttonView != null) {
            isFocus = buttonView.requestFocus();
            buttonView.invalidate();
        }
        return isFocus;
    }
}
