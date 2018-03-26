package fany.phpuijar;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import ui.PickList;
import ui.dialog.ModalDialog;
import ui.dialog.ModalDialogFooterButtonProp;
import ui.dialog.ModalDialogInterface;

public class ModalDialogActivity extends AppCompatActivity {
    private final static String TAG = ModalDialogActivity.class.getSimpleName();
    private ModalDialog modalDialog;
    private ModalDialogFooterButtonProp mbutton;
    private EditText editText;
    private PickList pickList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modal_dialog);

        pickList = new PickList(this);
        final String[] pickListVals = {
                "Dialog 1", "Dialog 2", "Dialog 3", "Dialog 4",
                "Dialog 5", "Dialog 6", "Dialog 7", "Dialog 8",
                "Dialog 9", "Dialog 10", "Dialog 11", "Dialog 12"
        };

        pickList.setTitleText("Select Dialog");
        pickList.setCheckedPosition(0);
        pickList.setFocusPosition(0);
        findViewById(R.id.selectdialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickList.setArray(pickListVals);
                pickList.setPickListListner(new PickList.pickListItemSelectedListener() {
                    @Override
                    public void onItemPicked(String[] array, int position, boolean selected) {
                        Toast.makeText(getApplicationContext(), "Clicked position: " + position, Toast.LENGTH_SHORT).show();
                        switch (position) {
                            case 0:
                                modalDialog = dialog0();
                                break;
                            case 1:
                                modalDialog = dialog1();
                                break;
                            case 2:
                                modalDialog = dialog2();
                                break;
                            case 3:
                                modalDialog = dialog3();
                                break;
                            case 4:
                                modalDialog = dialog4();
                                break;
                            case 5:
                                modalDialog = dialog5();
                                break;
                            case 6:
                                modalDialog = dialog6();
                                break;
                            case 7:
                                modalDialog = dialog7();
                                break;
                            case 8:
                                modalDialog = dialog8();
                                break;
                            case 9:
                                modalDialog = dialog9();
                                break;
                            default:
                                break;
                        }
                    }
                });
                pickList.show();
            }
        });

        findViewById(R.id.dialogshow).setEnabled(true);
        findViewById(R.id.dialogshow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modalDialog.setCancelable(true);
                modalDialog.show();
            }
        });
    }

    private ModalDialog dialog0() {
        final ModalDialog.Builder builder = new ModalDialog.Builder(this, ModalDialog.HEADING_TYPE_DEFAULT);
        builder.setHeading("Default Heading", "One Button Example");
        builder.setStatus("Small",
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                null, null, null);
        builder.setMessage(getResources().getString(R.string.dialog_message));
        builder.setButton(ModalDialog.BUTTON_RIGHT, "Cancel", true, new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "Cancel Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return builder.build();
    }

    private ModalDialog dialog1() {
        final ModalDialog.Builder builder = new ModalDialog.Builder(this, ModalDialog.HEADING_TYPE_DEFAULT);
        builder.setHeading("Default Heading", "Two Button Example");
        builder.setStatus("Small",
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                null, null, null);
        builder.setMessage(getResources().getString(R.string.dialog_message));
        builder.setButtons(new ModalDialogFooterButtonProp(true, "OK", new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "OK Button Clicked", Toast.LENGTH_SHORT).show();
            }
        }), new ModalDialogFooterButtonProp(true, "Cancel", new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "Cancel Button Clicked", Toast.LENGTH_SHORT).show();
            }
        }));

        return builder.build();
    }

    private ModalDialog dialog2() {
        final ModalDialog.Builder builder = new ModalDialog.Builder(this, ModalDialog.HEADING_TYPE_DEFAULT);
        builder.setHeading("Default Heading", "Three Button Example");
        builder.setStatus("Small",
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                null, null, null);
        builder.setMessage(getResources().getString(R.string.dialog_message));
        builder.setButtons(new ModalDialogFooterButtonProp(true, "OK", new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "OK Button Clicked", Toast.LENGTH_SHORT).show();
            }
        }), new ModalDialogFooterButtonProp(true, "Cancel", new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "Cancel Button Clicked", Toast.LENGTH_SHORT).show();
            }
        }), new ModalDialogFooterButtonProp(true, "Done", new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "Done Button Clicked", Toast.LENGTH_SHORT).show();
            }
        }));

        return builder.build();
    }

    private ModalDialog dialog3() {
        final ModalDialog.Builder builder = new ModalDialog.Builder(this, ModalDialog.HEADING_TYPE_DEFAULT);
        builder.setHeading("Default Heading", "Four Button Example");
        builder.setStatus("Small",
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                null, null, null);
        builder.setMessage(getResources().getString(R.string.dialog_message));
        builder.setButtons(new ModalDialogFooterButtonProp(true, "OK", new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "OK Button Clicked", Toast.LENGTH_SHORT).show();
            }
        }), new ModalDialogFooterButtonProp(true, "Cancel", new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "Cancel Button Clicked", Toast.LENGTH_SHORT).show();
            }
        }), new ModalDialogFooterButtonProp(true, "Done", new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "Done Button Clicked", Toast.LENGTH_SHORT).show();
            }
        }), new ModalDialogFooterButtonProp(true, "Confirm", new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "confirm Button Clicked", Toast.LENGTH_SHORT).show();
            }
        }));

        return builder.build();
    }

    private ModalDialog dialog4() {
        final ModalDialog.Builder builder = new ModalDialog.Builder(this, ModalDialog.HEADING_TYPE_DEFAULT);
        builder.setHeading("Default Heading", "Long Text");
        builder.setStatus("Large",
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                null, null, null);
        builder.setMessage(getResources().getString(R.string.dialog_message));
        builder.setButton(ModalDialog.BUTTON_RIGHT, "Done", true, new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                modalDialog.dismiss();
            }
        });

        return builder.build();
    }

    private ModalDialog dialog5() {
        final ModalDialog.Builder builder = new ModalDialog.Builder(this, ModalDialog.HEADING_TYPE_DEFAULT);
        builder.setHeading("Default Heading", "without any content");
        builder.setStatus("small",
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                null, null, null);
        builder.setView(null);
        builder.setButton(ModalDialog.BUTTON_RIGHT, "Done", true, new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                modalDialog.dismiss();
            }
        });

        return builder.build();
    }

    private ModalDialog dialog6() {
        final ModalDialog.Builder builder = new ModalDialog.Builder(this, ModalDialog.HEADING_TYPE_DEFAULT);
        String[] values = new String[]{"Android", "Iphone", "WindowsMobile", "Ubuntu", "Windows7", "Linux", "Mac OS"};
        ArrayList<String> listval = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            listval.add(values[i]);
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_list, null);
        ListView listView = view.findViewById(R.id.listView1);
        listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listval));

        mbutton = builder.setButton(ModalDialog.BUTTON_RIGHT, "Cancel", true, new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "Cancel Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mbutton.setEnable(false);
                mbutton.requestFocus();
            }
        });

        builder.setView(view);
        builder.setHeading("Default Heading", "without any content");
        builder.setStatus("small",
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                null, null, null);

        return builder.build();
    }

    private ModalDialog dialog7() {
        final ModalDialog.Builder builder = new ModalDialog.Builder(this, ModalDialog.HEADING_TYPE_DEFAULT);
        String[] values = new String[]{"Android", "Iphone", "WindowsMobile", "Ubuntu", "Windows7", "Linux", "Mac OS"};
        ArrayList<String> listval = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            listval.add(values[i]);
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_grid, null);
        GridView gridView = view.findViewById(R.id.gridView1);
        gridView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listval));

        mbutton = builder.setButton(ModalDialog.BUTTON_RIGHT, "Cancel", true, new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "Cancel Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mbutton.setEnable(false);
                mbutton.requestFocus();
            }
        });

        builder.setView(view);
        builder.setHeading("Default Heading", "without any content");
        builder.setStatus("small",
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                null, null, null);

        return builder.build();
    }

    private ModalDialog dialog8() {
        final ModalDialog.Builder builder = new ModalDialog.Builder(this, ModalDialog.HEADING_TYPE_DEFAULT);
        String[] values = new String[]{"Android", "Iphone", "WindowsMobile", "Ubuntu", "Windows7", "Linux", "Mac OS"};
        ArrayList<String> listval = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            listval.add(values[i]);
        }

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_grid, null);
        GridView gridView = view.findViewById(R.id.gridView1);
        gridView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, listval));

        editText = null;

        mbutton = builder.setButton(ModalDialog.BUTTON_RIGHT, "Cancel", true, new ModalDialogInterface.ButtonOnClickListener() {
            @Override
            public void onClick(ModalDialogInterface modalDialogInterface, int which) {
                Toast.makeText(ModalDialogActivity.this, "Cancel Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mbutton.setEnable(false);
                mbutton.requestFocus();
            }
        });

        builder.setView(view);
        builder.setHeading("Default Heading", "without any content");
        builder.setStatus("small",
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_launcher_background, null),
                null, null, null);

        return builder.build();
    }

    private ModalDialog dialog9() {
        ModalDialog dialog = new ModalDialog(this);
        return dialog;
    }
}
