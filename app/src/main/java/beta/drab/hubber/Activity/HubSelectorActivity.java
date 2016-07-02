package beta.drab.hubber.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import beta.drab.hubber.Adapters.Child;
import beta.drab.hubber.Adapters.ExpandListAdapter;
import beta.drab.hubber.Adapters.Group;
import beta.drab.hubber.R;
import de.greenrobot.event.EventBus;


public class HubSelectorActivity extends Activity {
//    ArrayList<Hub> hub = new ArrayList<Hub>();
//    ArrayList<JoinedHub> joinedHubs = new ArrayList<JoinedHub>();
//    ExpandableLayoutListView expandableLayoutListView;

    int page = 0;
    private ExpandListAdapter ExpAdapter;
    private ArrayList<Group> ExpListItems;
    private ExpandableListView ExpandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_selector);

        ExpandList = (ExpandableListView) findViewById(R.id.exp_list);
        ExpListItems = populateHubs2();
        ExpAdapter = new ExpandListAdapter(HubSelectorActivity.this, ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
        ExpandList.setGroupIndicator(null);
        ExpandList.setDividerHeight(0);


        LayoutInflater inflater = getLayoutInflater();


        ExpandList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {


                return true;
            }
        });

        ExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {


            }
        });

        ExpandList.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        Intent intent = getIntent();
        page = intent.getIntExtra("page_number", 0);


    }


    public ArrayList<Group> populateHubs2() {

        ArrayList<Group> setHubs = new ArrayList<Group>();

        //getting HUBS
        ParseQuery<ParseObject> query = ParseQuery.getQuery("HubItem");
        List<ParseObject> hubItems = null;
        try {
            hubItems = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int picker = 0;
        for (ParseObject hub : hubItems) {


            Group group = new Group();
            group.setName(hub.getString("title"));
            switch (picker) {
                case 0:
                    group.setColor(Color.parseColor("#d01716"));
                    break;
                case 1:
                    group.setColor(Color.parseColor("#29b6f6"));
                    break;
                case 2:
                    group.setColor(Color.parseColor("#7cb342"));
                    break;
                case 3:
                    group.setColor(Color.parseColor("#ffd54f"));
                    break;
                case 4:
                    group.setColor(Color.parseColor("#9e9e9e"));
                    break;
                case 5:
                    group.setColor(Color.parseColor("#d84315"));
                    break;
                case 6:
                    group.setColor(Color.parseColor("#455a64"));
                    break;

            }

            ArrayList<Child> child = new ArrayList<Child>();

            //GETTING SUBHUBS

            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("SubHub");
            query2.whereEqualTo("hub", hub);
            List<ParseObject> subHubs = null;
            try {
                subHubs = query2.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            //iterate through subHubss
            for (ParseObject subHubb : subHubs) {
                Child childd = new Child(subHubb.getString("name"));
                child.add(childd);

            }
            group.setItems(child);
            setHubs.add(group);
            picker++;
            if (picker == 6) picker = 0;
        }

        return setHubs;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hub_selector, menu);
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

    public void onNextClick(View w) {
        Intent i = new Intent(this, homeActivity.class);
        i.putExtra("page_number", page);
        startActivity(i);
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


}