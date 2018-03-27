package fany.phpuijar;

import java.util.ArrayList;


import ui.NPanelBrowser;
import ui.VerticalText;
import ui.NPanelBrowser.NPanelBrowserListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Intent;
import android.widget.TextView;
import android.graphics.Color;

public class NPanelBrowserActivity extends Activity {

    NPanelBrowser.NPanelBrowserListener listner;
    ArrayAdapter<String> aa;

    ArrayAdapter<String> ab;

    ArrayAdapter<String> ac;

    String root = "Root";
    int pos = 0;
    int panelIndex = 0;

    private int myDepth = 0;

    ListView lv;
    ListView lv1;
    ListView lv2;
    GridView gd;
    final NPanelBrowser npb = null;
    NodeImpl nodeRoot;
    NodeImpl nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, nodeG, nodeH;
    NodeImpl nodeA1, nodeA2, nodeA3, nodeA4, nodeA5, nodeA6, nodeA7, nodeA11, nodeA12, nodeA13, nodeA14, nodeA111, nodeA112, nodeA113, nodeA1111;
    NodeImpl nodeB1, nodeB2, nodeB3, nodeB4, nodeB5, nodeB11, nodeB12, nodeB13;
    NodeImpl nodeC1, nodeC2, nodeC3, nodeC4, nodeC5, nodeC6;
    NodeImpl nodeD1, nodeD2, nodeD3;
    NodeImpl nodeE1, nodeE2, nodeE3;
    NodeImpl nodeF1, nodeF2, nodeF3;
    NodeImpl nodeG1;
    NodeImpl nodeH1;
    private int MAX_NO_OF_SCREENS = 5;
    Node node;
    int mCurrentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npanel);

        nodeRoot = new NodeImpl("Root");

        nodeA = new NodeImpl("A");
        nodeB = new NodeImpl("B");
        nodeC = new NodeImpl("C");
        nodeD = new NodeImpl("D");
        nodeE = new NodeImpl("E");
        nodeF = new NodeImpl("F");
        nodeG = new NodeImpl("G");
        nodeH = new NodeImpl("H");

        nodeA1 = new NodeImpl("A1");
        nodeA2 = new NodeImpl("A2");
        nodeA3 = new NodeImpl("A3");
        nodeA4 = new NodeImpl("A4");
        nodeA5 = new NodeImpl("A5");
        nodeA6 = new NodeImpl("A6");
        nodeA7 = new NodeImpl("A7");

        nodeA11 = new NodeImpl("A11");
        nodeA12 = new NodeImpl("A12");
        nodeA13 = new NodeImpl("A13");
        nodeA14 = new NodeImpl("A14");

        nodeA111 = new NodeImpl("A111(leaf node)");
        nodeA112 = new NodeImpl("A112");
        nodeA113 = new NodeImpl("A113");

        nodeA1111 = new NodeImpl("A1111");


        nodeB1 = new NodeImpl("B1");
        nodeB2 = new NodeImpl("B2");
        nodeB3 = new NodeImpl("B3");
        nodeB4 = new NodeImpl("B4");
        nodeB5 = new NodeImpl("B5");


        nodeRoot.addChild(nodeA);
        nodeRoot.addChild(nodeB);
        nodeRoot.addChild(nodeC);
        nodeRoot.addChild(nodeD);
        nodeRoot.addChild(nodeE);
        nodeRoot.addChild(nodeF);
        nodeRoot.addChild(nodeG);
        nodeRoot.addChild(nodeH);

        nodeA.addChild(nodeA1);
        nodeA.addChild(nodeA2);
        nodeA.addChild(nodeA3);
        nodeA.addChild(nodeA4);
        nodeA.addChild(nodeA5);
        nodeA.addChild(nodeA6);
        nodeA.addChild(nodeA7);

        nodeA1.addChild(nodeA11);
        nodeA1.addChild(nodeA12);
        nodeA1.addChild(nodeA13);
        nodeA1.addChild(nodeA14);

        nodeA11.addChild(nodeA111);
        nodeA11.addChild(nodeA112);
        nodeA11.addChild(nodeA113);

        nodeA111.addChild(nodeA1111);

        nodeB.addChild(nodeB1);
        nodeB.addChild(nodeB2);
        nodeB.addChild(nodeB3);
        nodeB.addChild(nodeB4);
        nodeB.addChild(nodeB5);
        nodeB11 = new NodeImpl("B11");
        nodeB12 = new NodeImpl("B12");
        nodeB13 = new NodeImpl("B13");

        nodeB1.addChild(nodeB11);
        nodeB1.addChild(nodeB12);
        nodeB1.addChild(nodeB13);


        final NPanelBrowser npb = (NPanelBrowser) findViewById(R.id.nPanelBrowser1);
        lv = new ListView(this);
        // lv.setFadingEdgeLength(42);
        //  lv.setVerticalFadingEdgeEnabled(true);
        lv1 = new ListView(this);
        lv2 = new ListView(this);
        lv.setLayoutParams(new LayoutParams(328, LayoutParams.MATCH_PARENT));
        lv1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        lv2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        gd = new GridView(this);
        gd.setNumColumns(6);
        LinearLayout.LayoutParams lvParams = new LinearLayout.LayoutParams(-1, -1);

        //lv.setLayoutParams(lvParams);
        //lv.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.green));
        //lv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,406));
        /*final FrameLayout.LayoutParams params=(android.widget.FrameLayout.LayoutParams) lv.getLayoutParams();
		params.gravity = Gravity.CENTER_VERTICAL;*/
        // lv.setLayoutParams(params);
        lv.setDividerHeight(0);
        lv.setPadding(0, 0, 2, 0);
        lv.setScrollBarSize(2);
        lv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        lv.setLayoutParams(new LayoutParams(320, LayoutParams.MATCH_PARENT));
        lv1.setDividerHeight(0);
        lv1.setPadding(0, 0, 2, 0);
        lv1.setScrollBarSize(2);
        lv1.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        lv2.setDividerHeight(0);
        lv2.setPadding(0, 0, 2, 0);
        lv2.setScrollBarSize(2);
        lv2.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        //	lv1.setLayoutParams(new LayoutParams(320,LayoutParams.MATCH_PARENT));
        //lv1.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,406));
        //	aa = new ArrayAdapter<String>(this, R.layout.row,listOfArraysOfStrings.get(0));

        //	ab = new ArrayAdapter<String>(this, R.layout.row,listOfArraysOfStrings.get(1));

        //ac = new ArrayAdapter<String>(this, R.layout.row,names3);

		/*lv.setAdapter(aa);
		lv1.setAdapter(ab);
		lv2.setAdapter(ac);*/


        listner = new NPanelBrowserListener() {

            @Override
            public void setDepth(int depth) {
                // TODO Auto-generated method stub
                myDepth = depth;


            }

            @Override
            public boolean isFocussable(int index) {
                // TODO Auto-generated method stub
				/*if(index >= 0 && index < 25)
				{
					//Toast.makeText(getApplicationContext(), "width: "+npb.getWidth(), Toast.LENGTH_LONG).show();
					return true;
				}
				else
				{
					return false;
				}*/
                node = nodeRoot;
                for (int i = 0; i < index; i++) {
                    node = node.getChildren().get(node.getSelectedItemPosition());
                }
                if (node.getChildren().size() > 0) {
                    if (index == 4) {
                        npb.collapse();
                    }
                    return true;
                }/*else
				{
					npb.collapse();
					return false;
				}*/
                return false;
            }

            @Override
            public View getPanelView(int index) {
                if (nodeRoot.getChildren().size() > 0) {
                    node = nodeRoot;
                    if (lv1.getSelectedItemPosition() >= 0) {
                        node.setSelectedItemPosition(lv1.getSelectedItemPosition());
                    } else {
                        node.setSelectedItemPosition(0);
                    }
                    for (int i = 0; i < index; i++) {

                        node = node.getChildren().get(node.getSelectedItemPosition());

                    }


                    if (index % 2 == 0) {
                        if (index != 4) {
                            // lv1 = new ListView(NPanel2k15TestAppActivity.this);
                            lv1.setAdapter(new myAdapter(NPanelBrowserActivity.this, node.getChildrenName()));

                            return lv1;
                        } else {
                            TextView tx = new TextView(getApplicationContext());
                            tx.setText("Collapsed");
                            tx.setFocusable(true);
                            return tx;
                        }
                    } else {
                        //lv2 = new ListView(NPanel2k15TestAppActivity.this);
                        lv2.setAdapter(new myAdapter(NPanelBrowserActivity.this, node.getChildrenName()));

                        return lv2;
                    }

                }

                return null;

            }

            public int getDepth() {
                // TODO Auto-generated method stub
                return myDepth;
            }

            public View getBacktraceView(int depth, View v) {
                node = nodeRoot;
                for (int i = 0; i <= depth; i++) {

                    node = node.getChildren().get(node.getSelectedItemPosition());

                }
                return createVertTextView(depth, v, node.getName());
            }

            public boolean MoveToNextPage() {
                // TODO Auto-generated method stub
                node = nodeRoot;
                for (int i = 0; i <= getDepth() + 1; i++) {
                    node = node.getChildren().get(node.getSelectedItemPosition());
                }
                if (node.getChildren().size() > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        npb.setNPanelBrowserListner(listner);

        lv1.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (npb.isNextPanelUpdate(lv1)) {
                    npb.updateNextPanel();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


    }

    private final View createVertTextView(int depth, View v, String name) {
        if (v == null) {
            if (depth == -1) {
                VerticalText retView = new VerticalText(getApplicationContext());
                retView.setText(name);

                return retView;
            } else {
                VerticalText retView = new VerticalText(getApplicationContext());
                //retView.setText(backtraceNames[depth]);
                retView.setText(name);
                return retView;
            }
        } else {
            if (depth == -1) {
                VerticalText retView = (VerticalText) v;
                retView.setText(name);

                return v;
            } else {
                VerticalText retView = (VerticalText) v;
                //retView.setText(backtraceNames[depth]);
                retView.setText(name);

                return v;
            }
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //Log.i("MainActivity","in dispatchKeyEvent ret  ");
        return super.dispatchKeyEvent(event);

    }

}