package beta.drab.hubber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import beta.drab.hubber.Activity.FeedModels.ContentItem;
import beta.drab.hubber.Activity.FeedModels.EventItem;
import beta.drab.hubber.Activity.Profiles.UserActivity;
import beta.drab.hubber.R;
import de.greenrobot.event.EventBus;

public class InviteFriendsActivity extends AppCompatActivity {

    ListView list;
    ArrayList<String> friends = new ArrayList<String>();
    ArrayAdapter<String> arrayAdapter;
    InviteFriendsActivity activity;
    EventItem eventItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().registerSticky(this);
        setContentView(R.layout.activity_invite_friends);
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_friends, menu);
        return true;
    }

    public void onDoneClick(View w) {

        //List list = new ArrayList();

        SparseBooleanArray a = list.getCheckedItemPositions();

        for(int i = 0; i < friends.size() ; i++)
        {
            if (a.valueAt(i))
            {
                try {
                    ParseObject event = new ParseObject("InvitedEvent");
                    event.put("InvitedBy", ParseUser.getCurrentUser());
                    event.put("Event", eventItem.getParseObj());
                    event.put("InvitedTo", findUserFromUsername(friends.get(i)));
                    event.save();
                }catch(ParseException e){
                    e.printStackTrace();
                }



            }
        }
       this.finish();
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

    public static ParseUser findUserFromUsername(String name) {
        ParseQuery<ParseUser> queryForUser = ParseUser.getQuery();
        queryForUser.whereEqualTo("username", name);
        ParseUser user = null;

        try {
            user = queryForUser.getFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(name, " username should be " + user.getUsername());
        return user;
    }


    public void onEventMainThread(EventItem received) {
        eventItem = received;
        Log.d("Received in Activity inside event!:", eventItem.toString());
    }


}
