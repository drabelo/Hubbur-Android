package beta.drab.hubber.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.camera.CropImageIntentBuilder;
import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import beta.drab.hubber.Activity.Profiles.UserActivity;
import beta.drab.hubber.Adapters.MediaStoreUtils;
import beta.drab.hubber.Fragments.EventFeedFragment;
import beta.drab.hubber.Fragments.HubFeedFragment;
import beta.drab.hubber.R;

public class homeActivity extends ActionBarActivity {

    private static final String TAB_1_TAG = "HOME";
    private static final String TAB_2_TAG = "DISCOVER";
    private static final String TAB_3_TAG = "CALENDAR";
    private static final String TAB_4_TAG = "SETTINGS";
    static ParseUser currUser = ParseUser.getCurrentUser();
    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;
    RelativeLayout mDrawerPane;
    PagerSlidingTabStrip tabsStrip = null;
    ArrayAdapter<String> adapter;
    ImageView profPic;
    TextView username;
    Bitmap yourSelectedImage;
    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayout mTabsLinearLayout;
    private CharSequence mTitle;

    public static View getToolbarLogoIcon(Toolbar toolbar) {
        //check if contentDescription previously was set
        boolean hadContentDescription = android.text.TextUtils.isEmpty(toolbar.getLogoDescription());
        String contentDescription = String.valueOf(!hadContentDescription ? toolbar.getLogoDescription() : "logoContentDescription");
        toolbar.setLogoDescription(contentDescription);
        ArrayList<View> potentialViews = new ArrayList<View>();
        //find the view based on it's content description, set programatically or with android:contentDescription
        toolbar.findViewsWithText(potentialViews, contentDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
        //Nav icon is always instantiated at this point because calling setLogoDescription ensures its existence
        View logoIcon = null;
        if (potentialViews.size() > 0) {
            logoIcon = potentialViews.get(0);
        }
        //Clear content description if not previously present
        if (hadContentDescription)
            toolbar.setLogoDescription(null);
        return logoIcon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        MyAsyncTask5 asyncTask2 = new MyAsyncTask5(this);
        asyncTask2.execute();

//        ListView listView1 = (ListView) findViewById(R.id.navListt);
//
//        String[] items = {"Profile", "My Hubs", "Settings", "Logout"};
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, items);
//
//        listView1.setAdapter(adapter);


        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        profPic = (ImageView) findViewById(R.id.avatar);

        if ((ParseFile) ParseUser.getCurrentUser().get("profilePicture") != null) {
            ParseFile image = (ParseFile) ParseUser.getCurrentUser().get("profilePicture");
            byte[] data;
            try {
                data = image.getData();
                profPic.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {

        }
        username = (TextView) findViewById(R.id.userName);
        username.setText("@" + currUser.getUsername());


        mTitle = getTitle();

        //getting all likes and pinning them
        ParseRelation<ParseObject> rel = ParseUser.getCurrentUser().getRelation("likedComments");
        ParseQuery<ParseObject> query = rel.getQuery();
        List<ParseObject> lst = null;
        try {
            lst = query.find();
            ParseObject.pinAll("likedComments", lst);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //getting all joined events and pinning them
        ParseRelation<ParseObject> relForEvents = ParseUser.getCurrentUser().getRelation("ChosenEvents");
        ParseQuery<ParseObject> queryForEvents = relForEvents.getQuery();
        List<ParseObject> lstForEvents = null;
        try {
            lstForEvents = queryForEvents.find();
            ParseObject.pinAll("ChosenEvents", lstForEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }






        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.menu);

        View logoView = getToolbarLogoIcon(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mDrawerPane);

            }
        });


        Intent intent = getIntent();


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        // Give the PagerSlidingTabStrip the ViewPager
        tabsStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);

        int id = intent.getIntExtra("page_number", 0);
        viewPager.setCurrentItem(id);


        tabsStrip.setViewPager(viewPager);
        tabsStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                TextView title = (TextView) findViewById(R.id.toolbar_title);
                title.setText("HUBBUR");


            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        toolbar.inflateMenu(R.menu.menu_home);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File croppedImageFile = new File(getFilesDir(), "test.jpg");

        if ((requestCode == REQUEST_PICTURE) && (resultCode == RESULT_OK)) {
            // When the user is done picking a picture, let's start the CropImage Activity,
            // setting the output image file and size to 200x200 pixels square.
            Uri croppedImage = Uri.fromFile(croppedImageFile);

            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(500, 500, 150, 150, croppedImage);
            cropImage.setSourceImage(data.getData());


            startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
        } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == RESULT_OK)) {
            // When we are done cropping, display it in the ImageView.
            yourSelectedImage = BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 0, stream);
            byte[] data2 = stream.toByteArray();
            ParseFile parseImagefile = new ParseFile("profile_pic.png", data2);
            try {
                parseImagefile.save();
                currUser.put("profilePicture", parseImagefile);
                currUser.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showLogo() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.splash);
        layout.setVisibility(View.VISIBLE);
    }

    public void hideLogo() {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.splash);
        layout.setVisibility(View.GONE);

    }


    public void onCommentClick(View w) {
        Intent i = new Intent(this, contentDetailActivity.class);
        startActivity(i);

    }

    public void onProfileClick(View w) {
        mDrawerLayout.closeDrawer(mDrawerPane);
        Intent i = new Intent(this, UserActivity.class);
        Log.d("username inside onclick", ParseUser.getCurrentUser().getUsername());
        i.putExtra("username", ParseUser.getCurrentUser().getUsername());
        startActivity(i);

    }

    public void onHubClick(View w) {
        mDrawerLayout.closeDrawer(mDrawerPane);
        Intent i = new Intent(this, MyHubs.class);
        startActivity(i);

    }

    public void onCalendarClick(View w) {
        mDrawerLayout.closeDrawer(mDrawerPane);
        Intent i = new Intent(this, CalendarActivity.class);
        startActivity(i);

    }

    public void onLogOutClick(View w) {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Intent i = new Intent(this, firstActivity.class);
        startActivity(i);

    }

    public void onFriendsClick(View w) {
        Intent i = new Intent(this, MyFriendsActivity.class);
        startActivity(i);
    }

    public void onMyInvitesClick(View w) {
        Intent i = new Intent(this, MyInvitesActivity.class);
        startActivity(i);
    }


    public void onSettingClick(View w) {
//        Intent i = new Intent(this, contentDetailActivity.class);
//        startActivity(i);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
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

                    return HubFeedFragment.newInstance(0, "Home");
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return EventFeedFragment.newInstance(1, "Thread");
//                case 2: // Fragment # 0 - This will show FirstFragment different title
//                    return UserFragment.newInstance(2, "SETTINGS");
//                    //return BlankFragment.newInstance(2, "DISCOVER");
//                case 3: // Fragment # 0 - This will show FirstFragment different title
//                    return CalendarFragment.newInstance(3, "CALENDAR");
//                case 4: // Fragment # 0 - This will show FirstFragment different title
//                    return UserFragment.newInstance(4, "SETTINGS");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            String title = "";
            if (position == 0)
                title = "FEED";
            if (position == 1)
                title = "EVENTS";
//            if (position == 2)
//                title = "Profile";
//                title = "Discover";
//            if(position == 3)
//                title = "Calendar";
//            if(position == 4)
//                title = "Settings";

            return title;
        }

    }

    private static class MyAsyncTask5 extends AsyncTask<Void, Integer, Void> {

        private Activity activity;

        private MyAsyncTask5(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {

            new Thread(new Runnable() {
                public void run() {

                    try {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((homeActivity) activity).showLogo();
                            }
                        });

                        Thread.sleep(5000);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((homeActivity) activity).hideLogo();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        protected Void doInBackground(Void... params) {


            return null;
        }


        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);


        }


    }


}