package fany.phpuijar;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView lv;
    private List<ResolveInfo> infos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.list_view_activity);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = infos.get(position).activityInfo.name;
                Intent intent = new Intent();
                ComponentName comp = new ComponentName(infos.get(position).activityInfo.packageName, name);
                Log.d(TAG, "Component name " + comp);
                intent.setComponent(comp);
                startActivity(intent);
            }
        });
        getActivitiesName();
    }

    private void getActivitiesName() {
        String[] activities = null;

        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory("UI_Component_Demo_Activity");

        infos = pm.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        activities = new String[infos.size()];
        int i = 0;
        for (ResolveInfo info : infos) {
            ActivityInfo activityInfo = info.activityInfo;
            activities[i++] = activityInfo.labelRes != 0 ? getResources().getString(activityInfo.labelRes) : activityInfo.name;
        }
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, activities));
    }

}

