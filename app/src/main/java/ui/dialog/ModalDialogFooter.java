package ui.dialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.lang.ref.WeakReference;

import fany.phpuijar.R;

/**
 * Created by huixue.gong on 2017/12/14.
 */

public class ModalDialogFooter {
    private Button mLeftButton;
    private Button mMidLeftButton;
    private Button mMidRightButton;
    private Button mRightButton;

    private ModalDialogFooterButtonProp mLeftButtonProp;
    private ModalDialogFooterButtonProp mMidLeftButtonProp;
    private ModalDialogFooterButtonProp mMidRightButtonProp;
    private ModalDialogFooterButtonProp mRightButtonProp;

    private CharSequence mButtonLeftText;
    private CharSequence mButtonMidLeftText;
    private CharSequence mButtonMidRightText;
    private CharSequence mButtonRightText;

    private Message mButtonLeftMessage;
    private Message mButtonMidLeftMessage;
    private Message mButtonMidRightMessage;
    private Message mButtonRightMessage;

    private ModalDialogInterface mDialogInterface;

    private Handler mHandler;

    private View mFooterView;

    public boolean mDismissable=true;

    public ModalDialogFooter(ModalDialogInterface dialogInterface) {
        mDialogInterface = dialogInterface;
        mHandler = new ButtonHandler(dialogInterface);
    }

    public void setModalDialogListener(ModalDialogInterface modalDialogInterface) {
        mDialogInterface = modalDialogInterface;
    }

    public View installFooterPanel(Context context, ViewGroup parent) {
        return mFooterView=parent.findViewById(R.id.dialogFooter);
    }

    public void setupPanel(){
        // setup the left button
        mLeftButton = (Button) mFooterView.findViewById(R.id.leftButton);
        mLeftButton.setOnClickListener(mButtonHandler);

        if (TextUtils.isEmpty(mButtonLeftText)) {
            mLeftButton.setVisibility(View.INVISIBLE);
        } else {
            mLeftButtonProp.setButtonView(mLeftButton);
            mLeftButton.setText(mButtonLeftText);
            mLeftButton.setVisibility(View.VISIBLE);
        }

        // setup the mid left button
        mMidLeftButton = (Button) mFooterView.findViewById(R.id.midLeftButton);
        mMidLeftButton.setOnClickListener(mButtonHandler);

        if (TextUtils.isEmpty(mButtonMidLeftText)) {
            mMidLeftButton.setVisibility(View.INVISIBLE);
        } else {
            mMidLeftButtonProp.setButtonView(mMidLeftButton);
            mMidLeftButton.setText(mButtonMidLeftText);
            mMidLeftButton.setVisibility(View.VISIBLE);
        }

        // setup the mid right button
        mMidRightButton = (Button) mFooterView.findViewById(R.id.midRightButton);
        mMidRightButton.setOnClickListener(mButtonHandler);

        if (TextUtils.isEmpty(mButtonMidRightText)) {
            mMidRightButton.setVisibility(View.INVISIBLE);
        } else {
            mMidRightButtonProp.setButtonView(mMidRightButton);
            mMidRightButton.setText(mButtonMidRightText);
            mMidRightButton.setVisibility(View.VISIBLE);
        }

        // setup the right button
        mRightButton = (Button) mFooterView.findViewById(R.id.rightButton);
        mRightButton.setOnClickListener(mButtonHandler);

        if (TextUtils.isEmpty(mButtonRightText)) {
            mRightButton.setVisibility(View.INVISIBLE);
        } else {
            mRightButtonProp.setButtonView(mRightButton);
            mRightButton.setText(mButtonRightText);
            mRightButton.setVisibility(View.VISIBLE);
        }
    }

    View.OnClickListener mButtonHandler = new View.OnClickListener() {
        public void onClick(View v) {
            Message m = null;
            if (v != null) {
                if (v.equals(mLeftButton)) {
                    m = Message.obtain(mButtonLeftMessage);
                } else if (v.equals(mRightButton)) {
                    m = Message.obtain(mButtonRightMessage);
                } else if (v.equals(mMidLeftButton)) {
                    m = Message.obtain(mButtonMidLeftMessage);
                } else if (v.equals(mMidRightButton)) {
                    m = Message.obtain(mButtonMidRightMessage);
                }
            }
            if (m != null) {
                m.sendToTarget();
            }

            if (mDismissable) {
                // Post a message so we dismiss after the above handlers are
                // executed
                mHandler.obtainMessage(ButtonHandler.MSG_DISMISS_DIALOG,
                        mDialogInterface).sendToTarget();
            }

        }
    };

    private static final class ButtonHandler extends Handler {
        private static final int MSG_DISMISS_DIALOG = 1;

        private WeakReference<ModalDialogInterface> mDialog;

        public ButtonHandler(ModalDialogInterface dialog) {
            mDialog = new WeakReference<ModalDialogInterface>(dialog);
        }

        @Override
        public void handleMessage(Message msg) {
            ModalDialogInterface dialogInterface=null;
            ModalDialogInterface.ButtonOnClickListener dialogClickInterface=null;
            if (msg.obj instanceof ModalDialogInterface) {
                dialogInterface=((ModalDialogInterface) msg.obj);
            }
            if (msg.obj instanceof ModalDialogInterface.ButtonOnClickListener) {
                dialogClickInterface =((ModalDialogInterface.ButtonOnClickListener) msg.obj);
            }
            switch (msg.what) {

                case ModalDialogInterface.BUTTON_LEFT:
                case ModalDialogInterface.BUTTON_MID_LEFT:
                case ModalDialogInterface.BUTTON_MID_RIGHT:
                case ModalDialogInterface.BUTTON_RIGHT:
                    if (dialogClickInterface !=null) {
                        dialogClickInterface.onClick(mDialog.get(), msg.what);
                    }
                    break;

                case MSG_DISMISS_DIALOG:
                    if (dialogInterface !=null) {
                        dialogInterface.dismiss();
                    }
            }
        }
    }

    public void setButton(int whichButton, ModalDialogFooterButtonProp footerButtonProp) {

        Message msg = mHandler.obtainMessage(whichButton, footerButtonProp.getClickListener());

        CharSequence text=footerButtonProp.getText();
        switch (whichButton) {

            case ModalDialogInterface.BUTTON_LEFT:
                mButtonLeftText = text;
                mButtonLeftMessage = msg;
                mLeftButtonProp=footerButtonProp;
                break;

            case ModalDialogInterface.BUTTON_MID_LEFT:
                mButtonMidLeftText = text;
                mButtonMidLeftMessage = msg;
                mMidLeftButtonProp=footerButtonProp;
                break;

            case ModalDialogInterface.BUTTON_MID_RIGHT:
                mButtonMidRightText = text;
                mButtonMidRightMessage = msg;
                mMidRightButtonProp=footerButtonProp;
                break;
            case ModalDialogInterface.BUTTON_RIGHT:
                mButtonRightText = text;
                mButtonRightMessage = msg;
                mRightButtonProp=footerButtonProp;
                break;

            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }
}
