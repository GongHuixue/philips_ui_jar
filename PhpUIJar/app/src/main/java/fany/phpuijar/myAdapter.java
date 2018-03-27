package fany.phpuijar;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class myAdapter extends ArrayAdapter<String>
{
	
	
	 Context context; 
	    ArrayList<String> data=null;
	    
	    public myAdapter(Context context,ArrayList<String> data) {
	        super(context, R.layout.row, data);
	        this.context = context;
	        this.data = data;
	    }

	    @Override
	    public View getView(int position, View view, ViewGroup parent) {
	          TextView ctv = (TextView) View.inflate(context, R.layout.row, null);
	    	  ctv.setTextColor(Color.WHITE);
	          ctv.setText(data.get(position));
	          ctv.setHeight(55);
	          ctv.setGravity(Gravity.CENTER_VERTICAL);
	          return ctv;
	    }
} 