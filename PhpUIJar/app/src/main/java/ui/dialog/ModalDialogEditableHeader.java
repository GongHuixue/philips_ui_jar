package ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import fany.phpuijar.R;

/**
 * Created by huixue.gong on 2017/12/14.
 */

public class ModalDialogEditableHeader extends ModalDialogBaseHeader {
    private EditText mEditableTitle;

    public void setupPanel() {
        super.setupPanel();
    }

    @Override
    public View installHeaderPanel(Context context, ViewGroup parent) {
        super.installHeaderPanel(context, parent);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_modal_dialog_header_editable, null);
        mEditableTitle = (EditText) ((TextView) view.findViewById(R.id.editableHeading));

        mEditableTitle.setLayoutParams(mTitleView.getLayoutParams());
        ViewGroup headerParent = ((ViewGroup) mTitleView.getParent());
        int index = headerParent.indexOfChild(mTitleView);
        headerParent.removeView(mTitleView);
        headerParent.addView(mEditableTitle, index);
        return mHeaderPanel;
    }

    @Override
    public View getTitleView() {
        return mEditableTitle;
    }

    @Override
    public void setTitleView(CharSequence heading, CharSequence subHeading) {
    }
}
