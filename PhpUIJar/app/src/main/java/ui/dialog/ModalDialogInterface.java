package ui.dialog;

/**
 * Created by huixue.gong on 2017/12/14.
 */

public interface ModalDialogInterface {
    /**
     * The identifier for the LEFT button.
     */
    public static final int BUTTON_LEFT = -1;

    /**
     * The identifier for the MID LEFT button.
     */
    public static final int BUTTON_MID_LEFT = -2;

    /**
     * The identifier for the MID RIGHT button.
     */
    public static final int BUTTON_MID_RIGHT = -3;

    /**
     * The identifier for the RIGHT button.
     */
    public static final int BUTTON_RIGHT = -4;


    public void dismiss();

    public void cancel();

    interface ButtonOnClickListener {
        void onClick(ModalDialogInterface modalDialogInterface, int which);
    }
}
