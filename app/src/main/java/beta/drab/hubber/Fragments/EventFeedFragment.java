package beta.drab.hubber.Fragments;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import beta.drab.hubber.Activity.FeedModels.EventItem;
import beta.drab.hubber.Activity.FeedModels.MotherThread;
import beta.drab.hubber.Activity.NewEventActivity;
import beta.drab.hubber.Adapters.FeedAdapterV2;
import beta.drab.hubber.R;
import beta.drab.hubber.ect.DividerItemDecoration;


public class EventFeedFragment extends Fragment {
    static ArrayList<MotherThread> arrayOfItems = new ArrayList<MotherThread>();
    static FeedAdapterV2 adapter;
    static Date currentNewestItem;
    static Date currentOldestItem;
    static SuperRecyclerView mListView;
    static EventFeedFragment fragment;
    static boolean updateRefresh = false;
    ViewPager viewPager;
    LinearLayoutManager manager;
    FloatingActionButton fab;
    View tabs;
    private EventFeedFragment context = this;
    private WeakReference<AsyncTaskPopulateFeed> asyncTaskWeakRef;
    private WeakReference<AsyncTaskRefreshFeed> asyncTaskWeakRefForRefresh;

    public static EventFeedFragment newInstance(int page, String title) {
        EventFeedFragment fragment = new EventFeedFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        fragment.setArguments(args);
        return fragment;
    }

    public static void populateThreads() {

        //getting current users "chosen subhubs" then pinning them
        ParseRelation<ParseObject> relationToChosenSubHubs = ParseUser.getCurrentUser().getRelation("chosenSubHub");
        ParseQuery<ParseObject> queryToChosenSubHubs = relationToChosenSubHubs.getQuery();
        List<ParseObject> listOfChosenSubHubs = null;

        try {
            //first try localDataStore
            listOfChosenSubHubs = queryToChosenSubHubs.find();
            ParseObject.pinAll(listOfChosenSubHubs);
        } catch (Exception e) {
            try {
                //Find them manually, then pin
                listOfChosenSubHubs = queryToChosenSubHubs.find();
                ParseObject.pinAll(listOfChosenSubHubs);
            } catch (ParseException excep) {
                excep.printStackTrace();
            }
        }

        Log.d("Debugging", "Deubbing size" + listOfChosenSubHubs.size());

        //creating the query for the content
        ParseQuery<ParseObject> queryForTheContent = ParseQuery.getQuery("Event");

        //sorted by
        queryForTheContent.addDescendingOrder("createdAt");
        try {
            Log.d("date", " what is the current oldest item? " + currentOldestItem.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("date", " what is the current oldest item? NULL");
        }
        if (currentOldestItem != null) {
            //if currentOldestItem exists, make query with the new currentOldestItem
            queryForTheContent.whereLessThan("createdAt", currentOldestItem);
        }

        //should be SubHub but typo made it HUB
        queryForTheContent.whereContainedIn("Hub", listOfChosenSubHubs);

        //setting limit to 20
        queryForTheContent.setLimit(20);

        //getting all of the content items
        List<ParseObject> allContentItems = null;
        try {
            allContentItems = queryForTheContent.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("size", allContentItems.size() + " total");

        //identifying the newest/oldest item and keeping it's place
        currentNewestItem = allContentItems.get(0).getCreatedAt();
        currentOldestItem = allContentItems.get(allContentItems.size() - 1).getCreatedAt();


        ArrayList<MotherThread> temporaryContainer = new ArrayList<MotherThread>();

        List<ParseObject> likedCommentsList = getLikedEvents();



        for (ParseObject ParseObjectContentItem : allContentItems) {


            //getting subHub
            ParseObject subHub = getSubHub(ParseObjectContentItem);

            //GETTING USER
            //maybe add only username text instead of object?
            ParseUser createdByUser = ParseObjectContentItem.getParseUser("createdBy");




            try {
                final EventItem SingleEventItem = new EventItem();


                if (likedCommentsList.contains(ParseObjectContentItem)) {
                    SingleEventItem.state = 1;
                }


                Log.d("event", "started");

                SingleEventItem.date = ParseObjectContentItem.getDate("event_time");


                Log.d("event", "started1");

                try {
                    SingleEventItem.setDescription(ParseObjectContentItem.getString("description"));
                    SingleEventItem.setTitle(ParseObjectContentItem.getString("title"));
                    SingleEventItem.parseObj = ParseObjectContentItem;
                    SingleEventItem.id = ParseObjectContentItem.getObjectId();
                    SingleEventItem.location = ParseObjectContentItem.getString("location");
                    SingleEventItem.setHubName(subHub.getString("name"));

                    //card33.setScore(hub.getInt("score"));
                    Log.d("event", "started2");

                    SingleEventItem.username = ParseObjectContentItem.getParseUser("createdBy").getUsername();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("event", "started3");


                Log.d("event", "ended");


                if ((ParseFile) ParseObjectContentItem.get("eventPicture") != null) {
                    SingleEventItem.url = ((ParseFile) ParseObjectContentItem.get("eventPicture")).getUrl();
                } else {

                }
                Log.d("event", "ended2");

                Log.d("Event event", SingleEventItem.toString());

                temporaryContainer.add(SingleEventItem);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        for (MotherThread item : temporaryContainer) {
            if (arrayOfItems.contains(item)) {
                temporaryContainer.remove(item);
                Log.d("equals", "equals");
            }
        }
        arrayOfItems.addAll(temporaryContainer);

    }

    public static void RefreshThread() {

        //getting current users "chosen subhubs" then pinning them
        ParseRelation<ParseObject> relationToChosenSubHubs = ParseUser.getCurrentUser().getRelation("chosenSubHub");
        ParseQuery<ParseObject> queryToChosenSubHubs = relationToChosenSubHubs.getQuery();
        List<ParseObject> listOfChosenSubHubs = null;

        try {
            //first try localDataStore
            listOfChosenSubHubs = queryToChosenSubHubs.find();
            ParseObject.pinAll(listOfChosenSubHubs);
        } catch (Exception e) {
            try {
                //Find them manually, then pin
                listOfChosenSubHubs = queryToChosenSubHubs.find();
                ParseObject.pinAll(listOfChosenSubHubs);
            } catch (ParseException excep) {
                excep.printStackTrace();
            }
        }

        Log.d("Debugging", "Deubbing size" + listOfChosenSubHubs.size());

        //creating the query for the content
        ParseQuery<ParseObject> queryForTheContent = ParseQuery.getQuery("Event");

        //sorted by
        queryForTheContent.addDescendingOrder("createdAt");

        queryForTheContent.whereGreaterThan("createdAt", currentNewestItem);
        //should be SubHub but typo made it HUB
        queryForTheContent.whereContainedIn("Hub", listOfChosenSubHubs);

        //setting limit to 20
        queryForTheContent.setLimit(20);

        //getting all of the content items
        List<ParseObject> allContentItems = null;
        try {
            allContentItems = queryForTheContent.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("size", allContentItems.size() + " total");

        //identifying the newest/oldest item and keeping it's place
        currentNewestItem = allContentItems.get(0).getCreatedAt();


        ArrayList<MotherThread> temporaryContainer = new ArrayList<MotherThread>();


        for (ParseObject ParseObjectContentItem : allContentItems) {


            //getting subHub
            ParseObject subHub = getSubHub(ParseObjectContentItem);

            //GETTING USER
            //maybe add only username text instead of object?
            ParseUser createdByUser = ParseObjectContentItem.getParseUser("createdBy");


            try {
                final EventItem SingleEventItem = new EventItem();


                Log.d("event", "started");

                SingleEventItem.date = ParseObjectContentItem.getCreatedAt();

                Log.d("event", "started1");

                try {
                    SingleEventItem.setDescription(ParseObjectContentItem.getString("description"));
                    SingleEventItem.setTitle(ParseObjectContentItem.getString("title"));
                    SingleEventItem.id = ParseObjectContentItem.getObjectId();
                    SingleEventItem.parseObj = ParseObjectContentItem;

                    //card33.setScore(hub.getInt("score"));
                    Log.d("event", "started2");

//                    SingleEventItem.username = ParseObjectContentItem.getParseUser("createdBy").getUsername();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("event", "started3");


                Log.d("event", "ended");


                if ((ParseFile) ParseObjectContentItem.get("eventPicture") != null) {

                    SingleEventItem.image = (ParseFile) ParseObjectContentItem.get("eventPicture");
                    SingleEventItem.url = ((ParseFile) ParseObjectContentItem.get("eventPicture")).getUrl();
                } else {

                }
                Log.d("event", "ended2");

                Log.d("Event event", SingleEventItem.toString());

                temporaryContainer.add(SingleEventItem);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        for (MotherThread item : temporaryContainer) {
            if (arrayOfItems.contains(item)) {
                temporaryContainer.remove(item);
                Log.d("equals", "equals");
            }
        }
        arrayOfItems.addAll(0, temporaryContainer);

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

    //method to run asynctasks in parallel!
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void StartAsyncTaskInParallel(AsyncTaskPopulateFeed task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }

    public void onEventMainThread(ParseUser event) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("DEBUG", "onCreatteee of hubfeedd fragmernt");
        setRetainInstance(true);

        adapter = new FeedAdapterV2(arrayOfItems, this.getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_feed, container, false);

        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        mListView = (SuperRecyclerView) v.findViewById(R.id.listForEvents);
        manager = new LinearLayoutManager(getActivity());
        manager.setSmoothScrollbarEnabled(true);
        mListView.setLayoutManager(manager);
        mListView.getRecyclerView().addItemDecoration(new DividerItemDecoration(30));
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        tabs = (View) v.findViewById(R.id.tabs);
        mListView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new AsyncTaskRefreshFeed(context).execute();
            }
        });

        mListView.setupMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int i, int i1, int i2) {
                startNewAsyncTask();
            }
        }, 1);

        try {
            populateThreads();
        } catch (Exception e) {
            e.printStackTrace();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getActivity(), NewEventActivity.class);
                startActivity(a);
            }
        });


        try {
            mListView.setAdapter(adapter);
        } catch (Exception e) {
        }


        setHasOptionsMenu(true);
        return v;
    }

    private void startNewAsyncTask() {
        AsyncTaskPopulateFeed asyncTask = new AsyncTaskPopulateFeed(this);
        this.asyncTaskWeakRef = new WeakReference<AsyncTaskPopulateFeed>(asyncTask);
        StartAsyncTaskInParallel(asyncTask);
    }

    @Override
    public void onResume() {

        Log.e("DEBUG", "onResume of hubfeedd fragmernt");
        super.onResume();
        new AsyncTaskRefreshFeed(context).execute();

    }

    private static class AsyncTaskPopulateFeed extends AsyncTask<Void, Void, Void> {

        private WeakReference<EventFeedFragment> fragmentWeakRef;

        private AsyncTaskPopulateFeed(EventFeedFragment fragment) {
            this.fragmentWeakRef = new WeakReference<EventFeedFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                populateThreads();


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            Log.d("hit hereee" + "size " + arrayOfItems.size(), ".");
            //progress.setVisibility(View.INVISIBLE);

            if (this.fragmentWeakRef.get() != null) {
                Log.d("Finished onpostExecute for REFRESH23" + "size " + arrayOfItems.size(), ".");
                System.out.println("bam");
                mListView.hideMoreProgress();
                mListView.getProgressView();
                mListView.hideProgress();
                mListView.setLoadingMore(false);
                adapter.notifyDataSetChanged();

                if (mListView.getSwipeToRefresh().isRefreshing()) {
                    mListView.getSwipeToRefresh().setRefreshing(false);
                }
                //progress.setVisibility(View.INVISIBLE);
            }
        }
    }

    private static class AsyncTaskRefreshFeed extends AsyncTask<Void, Void, Void> {

        private WeakReference<EventFeedFragment> fragmentWeakRef;

        private AsyncTaskRefreshFeed(EventFeedFragment fragment) {
            this.fragmentWeakRef = new WeakReference<EventFeedFragment>(fragment);
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                RefreshThread();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            Log.d("hit hereee" + "size " + arrayOfItems.size(), ".");
            //progress.setVisibility(View.INVISIBLE);
            if (updateRefresh == true) {
                updateRefresh = false;
            }

            if (this.fragmentWeakRef.get() != null) {
                Log.d("Finished onpostExecute for REFRESH23" + "size " + arrayOfItems.size(), ".");
                System.out.println("bam");
                adapter.notifyDataSetChanged();
                if (mListView.getSwipeToRefresh().isRefreshing()) {
                    mListView.getSwipeToRefresh().setRefreshing(false);
                }
                //progress.setVisibility(View.INVISIBLE);
            }
        }
    }


    public static List<ParseObject> getLikedEvents() {
        ParseQuery<ParseObject> likedCommentsQuery = ParseQuery.getQuery("Event");
        likedCommentsQuery.fromLocalDatastore();
        likedCommentsQuery.fromPin("ChosenEvents");
        List<ParseObject> chosenEvents = null;
        try {
            chosenEvents = likedCommentsQuery.find();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return chosenEvents;
    }



}
