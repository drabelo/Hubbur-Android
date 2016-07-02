package beta.drab.hubber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;

import beta.drab.hubber.Activity.FeedModels.EventItem;
import beta.drab.hubber.Fragments.EventCommentsFragment;
import beta.drab.hubber.Fragments.EventDetailFragment;
import beta.drab.hubber.R;
import de.greenrobot.event.EventBus;

public class EventDetailActivity extends AppCompatActivity {


    private static final String TAB_1_TAG = "Event";
    private static final String TAB_2_TAG = "Comments";
    EventDetailActivity activity;
    public EventItem eventItem;

    // maybe profile?


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        activity = this;
        EventBus.getDefault().registerSticky(this);

        System.out.println("Received in just checking");

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        // Give the PagerSlidingTabStrip the ViewPager
        PagerSlidingTabStrip tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return EventDetailFragment.newInstance("0", "Event");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return EventCommentsFragment.newInstance("0", "Comments");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            if (position == 0)
                title = "Event";
            if (position == 1)
                title = "Comments";

            return title;
        }

    }

    public void onEventMainThread(EventItem received) {
        eventItem = received;
        Log.d("Received in Activity!:", "aa" + eventItem.toString());
    }

    public void inviteFriends(View w) {
        Intent i = new Intent(this, InviteFriendsActivity.class);
        EventBus.getDefault().postSticky(eventItem);
        Log.d("Received in sending to Invite Friends!:", "aa" + eventItem.toString());
        Intent intent = new Intent(this, EventDetailActivity.class);
        startActivity(i);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }



}
