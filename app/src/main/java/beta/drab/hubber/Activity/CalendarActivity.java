package beta.drab.hubber.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import beta.drab.hubber.Activity.FeedModels.EventItem;
import beta.drab.hubber.Activity.FeedModels.MotherThread;
import beta.drab.hubber.Adapters.FeedAdapterV2;
import beta.drab.hubber.R;
import beta.drab.hubber.ect.DividerItemDecoration;

public class CalendarActivity extends AppCompatActivity {

    SuperRecyclerView mListView;
    LinearLayoutManager manager;
    ArrayList<MotherThread> arrayOfItems = new ArrayList<MotherThread>();
    FeedAdapterV2 adapter = new FeedAdapterV2(arrayOfItems, this);
    CalendarActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        mListView = (SuperRecyclerView) findViewById(R.id.list);
        manager = new LinearLayoutManager(this);
        manager.setSmoothScrollbarEnabled(true);
        mListView.setLayoutManager(manager);
        mListView.getRecyclerView().addItemDecoration(new DividerItemDecoration(30));
        activity= this;
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

        for(ParseObject oneEvent: getLikedEvents()){
            final EventItem SingleEventItem = new EventItem();

            ParseObject subHub = getSubHub(oneEvent);


            Log.d("event", "started");

            SingleEventItem.date = oneEvent.getCreatedAt();

            Log.d("event", "started1");

            try {
                SingleEventItem.state = 1;
                SingleEventItem.setDescription(oneEvent.getString("description"));
                SingleEventItem.setTitle(oneEvent.getString("title"));
                SingleEventItem.parseObj = oneEvent;
                SingleEventItem.id = oneEvent.getObjectId();
                SingleEventItem.date = oneEvent.getDate("event_time");
                SingleEventItem.location = oneEvent.getString("location");

                SingleEventItem.setHubName(subHub.getString("name"));

                //card33.setScore(hub.getInt("score"));
                Log.d("event", "started2");

                SingleEventItem.username = oneEvent.getParseUser("createdBy").getUsername();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("event", "started3");


            Log.d("event", "ended");


            if ((ParseFile) oneEvent.get("eventPicture") != null) {
                SingleEventItem.url = ((ParseFile) oneEvent.get("eventPicture")).getUrl();
            } else {

            }
            Log.d("event", "ended2");

            Log.d("Event event", SingleEventItem.toString());

            arrayOfItems.add(SingleEventItem);
        }

        mListView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
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

    public static List<ParseObject> getLikedEvents() {
        ParseQuery<ParseObject> likedCommentsQuery = ParseQuery.getQuery("Event");
        likedCommentsQuery.addDescendingOrder("event_time");
        likedCommentsQuery.fromPin("ChosenEvents");
        List<ParseObject> chosenEvents = null;
        try {
            chosenEvents = likedCommentsQuery.find();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chosenEvents;
    }


    public static ParseObject getSubHub(ParseObject ParseObjectContentItem) {
        ParseQuery queryHub = ParseQuery.getQuery("SubHub");
        queryHub.whereEqualTo("name", ParseObjectContentItem.getString("subHubName"));
        queryHub.fromLocalDatastore();
        ParseObject subHub = null;
        try {
            subHub = queryHub.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return subHub;
    }
}
