package ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import fany.phpuijar.R;

/**
 * Created by huixue.gong on 2017/12/14.
 */

public class ModalDialog extends Dialog implements ModalDialogInterface {
    /**
     * Default Header type, with main title and sub title
     */
    public static final int HEADING_TYPE_DEFAULT = 0;
    /**
     * Default header type with no sub title
     */
    public static final int HEADING_TYPE_NO_SUB_TITLE = 1;
    /**
     * A header type with Editable title and without main title and sub title.
     */
    public static final int HEADING_TYPE_EDITABLE_TITLE = 2;
    /**
     * Dialog type. Dialog dimensions are width :828dp and height warp_content
     */
    public static final int MODAL_DIALOG_TYPE_LARGE = 0;
    /**
     * Dialog type. Dialog dimensions are width :644dp and height warp_content
     */
    public static final int MODAL_DIALOG_TYPE_SMALL = 1;

    private ModalDialogFooter builderFooter = null;
    private ModalDialogBaseHeader builderHeader = null;


    /**
     * Constructor: context for dialog
     *
     * @param context
     */
    public ModalDialog(Context context) {
        this(context, false, null);

    }

    /**
     * Constructor
     *
     * @param context:        context for dialog
     * @param cancelable:     boolean for dialog cancelable property
     * @param cancelListener: listener for dialog cancel.
     */
    protected ModalDialog(Context context, boolean cancelable,
                          OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (builderHeader != null) {
            builderHeader.setupPanel();
        }
        if (builderFooter != null) {
            builderFooter.setupPanel();
            builderFooter.setModalDialogListener(this);
        }

        super.onCreate(savedInstanceState);

    }


    void setHeader(ModalDialogBaseHeader header) {
        builderHeader = header;
    }

    void setFooter(ModalDialogFooter footer) {
        builderFooter = footer;

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void cancel() {
        super.cancel();

    }

    @Override
    public void show() {
        super.show();

    }

    /**
     * ModalDialog builder builds a dialog window with opted Header and footer.
     *
     * @author rankush.agrawal
     */

    public static class Builder {
        private boolean mIsHeaderEnable = true;
        private boolean mIsFooterEnable = true;
        private LayoutInflater mInflater;
        private ModalDialogBaseHeader mHeader;
        private ModalDialogFooter mFooter;
        private ViewGroup mView;
        private int mHeaderType;
        private Context mContext;

        /**
         * Constructs an instance of Builder.
         *
         * @param context:    context for dialog
         * @param headerType: header type.
         */
        public Builder(Context context, int headerType) {
            this(context, headerType, false, null);
        }

        /**
         * Constructs an instance of Builder.
         *
         * @param context
         * @param headerType
         * @param cancelable
         * @param dialogInterface
         */
        public Builder(Context context, int headerType, boolean cancelable,
                       ModalDialogInterface dialogInterface) {
            mContext = context;
            mHeaderType = headerType;
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (headerType == HEADING_TYPE_EDITABLE_TITLE) {
                mHeader = new ModalDialogEditableHeader();
            } else {
                mHeader = new ModalDialogDeafultHeader();
            }
            mFooter = new ModalDialogFooter(dialogInterface);
        }

        /**
         * Call this to disable the header from dialog
         */
        public void disableHeader() {
            mIsHeaderEnable = false;
        }

        /**
         * Call this to remove footer from dialog.
         */
        public void disableFooter() {
            mIsFooterEnable = false;
        }

        /**
         * Sets the content view at the middle of dialog with Header at top and footer at bottom.
         *
         * @param layoutResID: resource id of the layout
         */
        public final void setView(int layoutResID) {
            View view = mInflater.inflate(layoutResID, null);
            this.setView(view);
        }

        /**
         * Sets the content view at the middle of dialog with Header at top and footer at bottom.
         *
         * @param layoutResID: resource id of the layout
         */
        public final void setView(View view) {
            this.setView(view, null);
        }

        /**
         * Sets the content view at the middle of dialog with Header at top and footer at bottom.
         *
         * @param layoutResID: resource id of the layout
         * @param params       : layout params for parent
         */

        public final void setView(final View view, ViewGroup.LayoutParams params) {
            LinearLayout group = (LinearLayout) mInflater.inflate(R.layout.layout_modal_dialog_view, null);
            if (view != null) {
                View replace = group.findViewById(R.id.dialogContent);
                view.setLayoutParams(replace.getLayoutParams());
                view.setId(replace.getId());
                int index = group.indexOfChild(replace);
                group.addView(view, index);
                group.removeView(replace);

            }
            mView = group;


        }

        /**
         * Sets the titles for default header
         *
         * @param heading:    main title
         * @param subHeading: sub title
         */
        public void setHeading(CharSequence heading, CharSequence subHeading) {


            mHeader.setTitleView(heading, subHeading);

        }

        /**
         * Sets status info
         *
         * @param statusMsg:
         * @param left
         * @param right
         * @param top
         * @param bottom
         */
        public void setStatus(CharSequence statusMsg, Drawable left,
                              Drawable right, Drawable top, Drawable bottom) {
            mHeader.setStatus(statusMsg, left, right, top, bottom);
        }

        public EditText getEditableView() {

            if (mHeaderType == HEADING_TYPE_EDITABLE_TITLE) {
                return (EditText) mHeader.getTitleView();
            } else {
                return null;
            }
        }

        public void setMessage(CharSequence message) {
            mView = (LinearLayout) mInflater.inflate(R.layout.layout_modal_dialog_view, null);
            ((ScrollView) mView.findViewById(R.id.dialogContent)).setFocusable(false);
            ((TextView) mView.findViewById(R.id.dialogContentMessage)).setText(message);
        }

        public ModalDialogFooterButtonProp setButton(int buttonType, CharSequence textMessage, boolean enable,
                                                     ModalDialogInterface.ButtonOnClickListener clickListener) {

            if (mFooter != null || mIsFooterEnable) {
                ModalDialogFooterButtonProp buttonProp = new ModalDialogFooterButtonProp(enable, textMessage, clickListener);
                setButton(buttonType, buttonProp);
                return buttonProp;
            } else {
                throw new UnsupportedOperationException("Button can not be set Either Footer is diabled or Footer view is null");
            }

        }

        public void setButton(int buttonType, ModalDialogFooterButtonProp buttonProp) {
            if (mFooter != null) {
                mFooter.setButton(buttonType, buttonProp);
            }
        }

        public void setButtons(ModalDialogFooterButtonProp... footerButtonProps) {

            switch (footerButtonProps.length) {
                case 1:
                    setButton(BUTTON_RIGHT, footerButtonProps[0]);
                    break;
                case 2:
                    setButton(BUTTON_RIGHT, footerButtonProps[0]);
                    setButton(BUTTON_MID_RIGHT, footerButtonProps[1]);
                    break;
                case 3:
                    setButton(BUTTON_RIGHT, footerButtonProps[0]);
                    setButton(BUTTON_MID_RIGHT, footerButtonProps[1]);
                    setButton(BUTTON_LEFT, footerButtonProps[2]);
                    break;
                case 4:
                    setButton(BUTTON_RIGHT, footerButtonProps[0]);
                    setButton(BUTTON_MID_RIGHT, footerButtonProps[1]);
                    setButton(BUTTON_LEFT, footerButtonProps[2]);
                    setButton(BUTTON_MID_LEFT, footerButtonProps[3]);
                    break;

                default:
                    break;
            }
        }

        public void setDismissable(boolean disable) {
            mFooter.mDismissable = disable;
        }

        public ModalDialog build() {
            return build(MODAL_DIALOG_TYPE_SMALL);
        }

        private ModalDialog build(int which) {
            ModalDialog dialog = new ModalDialog(mContext);

            Window window = dialog.getWindow();
            /* We use a custom title so never request a window title and set required layout params */
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            if (mIsFooterEnable) {
                mFooter.installFooterPanel(mContext, (ViewGroup) mView.findViewById(R.id.dialogFooter));
                dialog.setFooter(mFooter);
            } else {
                dialog.setFooter(null);
                mView.findViewById(R.id.dialogFooter).setVisibility(View.GONE);
            }


            if (mIsHeaderEnable) {
                mHeader.installHeaderPanel(mContext, (ViewGroup) mView.findViewById(R.id.dialogHeader));
                dialog.setHeader(mHeader);
            } else {
                dialog.setHeader(null);
                mView.findViewById(R.id.dialogHeader).setVisibility(View.GONE);
            }
            window.setContentView(mView);

            if (which == MODAL_DIALOG_TYPE_LARGE) {
                int lWidth = mContext.getResources().getDimensionPixelSize(R.dimen.large_dialogue_container_width);
                window.setLayout(lWidth, WindowManager.LayoutParams.WRAP_CONTENT);
            } else {
                window.setLayout((int) mContext.getResources().getDimension(R.dimen.dialogues_container_width),
                        WindowManager.LayoutParams.WRAP_CONTENT);
            }
            //window.setAttributes(lp);
            return dialog;
        }
    }
}
