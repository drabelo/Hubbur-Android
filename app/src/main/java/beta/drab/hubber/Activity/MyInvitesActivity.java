package beta.drab.hubber.Activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import beta.drab.hubber.Activity.FeedModels.EventItem;
import beta.drab.hubber.Activity.FeedModels.MotherThread;
import beta.drab.hubber.Adapters.FeedAdapterV2;
import beta.drab.hubber.R;
import beta.drab.hubber.ect.DividerItemDecoration;

public class MyInvitesActivity extends ActionBarActivity {

    SuperRecyclerView mListView;
    LinearLayoutManager manager;
    ArrayList<MotherThread> arrayOfItems = new ArrayList<MotherThread>();
    FeedAdapterV2 adapter = new FeedAdapterV2(arrayOfItems, this);
    MyInvitesActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_invites);
        mListView = (SuperRecyclerView) findViewById(R.id.list);
        manager = new LinearLayoutManager(this);
        manager.setSmoothScrollbarEnabled(true);
        mListView.setLayoutManager(manager);
        mListView.getRecyclerView().addItemDecoration(new DividerItemDecoration(30));
        activity= this;


        for(ParseObject oneEvent: getInvitedToEvents()){
            final EventItem SingleEventItem = new EventItem();

            ParseObject subHub = getSubHub(oneEvent);


            Log.d("event", "started");

            SingleEventItem.date = oneEvent.getCreatedAt();

            Log.d("event", "started1");

            try {
                SingleEventItem.state = 1;

                SingleEventItem.setDescription(((ParseObject)oneEvent.get("Event")).getString("description"));
                SingleEventItem.setTitle(((ParseObject)oneEvent.get("Event")).getString("title"));
                SingleEventItem.parseObj = (((ParseObject)oneEvent.get("Event")));
                SingleEventItem.id = ((ParseObject)oneEvent.get("Event")).getObjectId();
                SingleEventItem.date = ((ParseObject)oneEvent.get("Event")).getDate("event_time");
                SingleEventItem.location = ((ParseObject)oneEvent.get("Event")).getString("location");

               // SingleEventItem.setHubName(subHub.getString("name"));

                //card33.setScore(hub.getInt("score"));
                Log.d("event", "started2");

                SingleEventItem.username = ((ParseObject)oneEvent.get("Event")).getParseUser("createdBy").getUsername();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("event", "started3");


            Log.d("event", "ended");


            if ((ParseFile) (((ParseObject)oneEvent.get("Event"))).get("eventPicture") != null) {
                SingleEventItem.url = ((ParseFile) ((ParseObject)oneEvent.get("Event")).get("eventPicture")).getUrl();
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
        getMenuInflater().inflate(R.menu.menu_my_invites, menu);
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

    public static List<ParseObject> getInvitedToEvents() {
        ParseQuery<ParseObject> likedCommentsQuery = ParseQuery.getQuery("InvitedEvent");
        likedCommentsQuery.whereEqualTo("InvitedTo", ParseUser.getCurrentUser());
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
