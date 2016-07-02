package beta.drab.hubber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import beta.drab.hubber.Activity.Profiles.UserActivity;
import beta.drab.hubber.R;

public class MyFriendsActivity extends AppCompatActivity {

    ListView list;
    ArrayList<String> friends = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    MyFriendsActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friends);
        activity = this;
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        list = (ListView) findViewById(R.id.listView2);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, friends);
        list.setChoiceMode(list.CHOICE_MODE_MULTIPLE);
        ParseRelation relation = ParseUser.getCurrentUser().getRelation("following");
        ParseQuery query = relation.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> list2, ParseException e) {
                if (e == null) {
                    for (ParseUser object : list2) {
                        friends.add(object.getUsername());
                        System.out.println(object);
                        System.out.println(object.getUsername());

                    }
                    list.setAdapter(arrayAdapter);
                    System.out.println(arrayAdapter.getCount());
                    System.out.println(arrayAdapter.getItem(0));
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, UserActivity.class);
                Log.d("username inside onclick", adapterView.getItemAtPosition(i).toString());
                intent.putExtra("username", adapterView.getItemAtPosition(i).toString());
                activity.startActivity(intent);

            }
        }


    );




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_friends, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
