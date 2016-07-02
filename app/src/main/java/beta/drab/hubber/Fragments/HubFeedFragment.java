package beta.drab.hubber.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import beta.drab.hubber.Activity.FeedModels.ContentItem;
import beta.drab.hubber.Activity.FeedModels.MotherThread;
import beta.drab.hubber.Activity.PostNewContentActivity;
import beta.drab.hubber.Adapters.FeedAdapterV2;
import beta.drab.hubber.R;
import beta.drab.hubber.ect.DividerItemDecoration;
import beta.drab.hubber.ect.HidingScrollListener;
import beta.drab.hubber.ect.PreCachingLayoutManager;


public class HubFeedFragment extends Fragment {

    static SuperRecyclerView mListView;
    static ArrayList<MotherThread> arrayOfItems = new ArrayList<MotherThread>(100);
    static SwipeRefreshLayout swipeLayout;
    static FloatingActionButton fab;
    static FeedAdapterV2 adapter;
    static HubFeedFragment fragment;
    static Date currentNewestItem;
    static Date currentOldestItem;
    static RelativeLayout bottomBar;
    static View v;
    static int flag4refresh = 0;
    static boolean updateRefresh = false;
    static int i = 0;
    static int flagLoad = 0;
    static boolean flag = true;
    PreCachingLayoutManager manager;
    TextView hotTv;
    TextView newTv;
    private HubFeedFragment context = this;
    private WeakReference<AsyncTaskPopulateFeed> asyncTaskWeakRef;
    private WeakReference<AsyncTaskRefreshFeed> asyncTaskWeakRefForRefresh;


    public HubFeedFragment() {
        // Required empty public constructor

    }


    public static HubFeedFragment newInstance(int param1, String param2) {
        HubFeedFragment fragment = new HubFeedFragment();
        Bundle args = new Bundle();
        args.putInt("int", param1);
        args.putString("string", param2);
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
            }
        }

        Log.d("Debugging", "Deubbing size" + listOfChosenSubHubs.size());

        //creating the query for the content
        ParseQuery<ParseObject> queryForTheContent = ParseQuery.getQuery("Comments");

        //sorted by
        queryForTheContent.addDescendingOrder("createdAt");
        if (currentOldestItem != null) {
            //if currentOldestItem exists, make query with the new currentOldestItem
            queryForTheContent.whereLessThan("createdAt", currentOldestItem);
        }
        queryForTheContent.whereContainedIn("subHub", listOfChosenSubHubs);

        //setting limit to 20
        queryForTheContent.setLimit(20);

        //getting all of the content items
        List<ParseObject> allContentItems = null;
        try {
            allContentItems = queryForTheContent.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //identifying the newest/oldest item and keeping it's place
        currentNewestItem = allContentItems.get(0).getCreatedAt();
        currentOldestItem = allContentItems.get(allContentItems.size() - 1).getCreatedAt();


        ArrayList<MotherThread> temporaryContainer = new ArrayList<MotherThread>();


        //getting all the liked comments the user has touched
        List<ParseObject> likedCommentsList = getLikedComments();


        //getting all the disliked comments the user has touched
        List<ParseObject> dislikedCommentsList = getDislikedComments();


        for (ParseObject ParseObjectContentItem : allContentItems) {


            //getting subHub
            ParseObject subHub = getSubHub(ParseObjectContentItem);

            //GETTING USER
            //maybe add only username text instead of object?
            ParseUser createdByUser = getUser(ParseObjectContentItem);


            try {
                final ContentItem singleContentItem = new ContentItem();

                //2 = liked, 1 = disliked
                if (likedCommentsList.contains(ParseObjectContentItem)) {
                    singleContentItem.state = 1;
                }

                //setting the fields
                singleContentItem.setParseObj(ParseObjectContentItem);
                singleContentItem.date = ParseObjectContentItem.getCreatedAt();
                singleContentItem.setDescription(ParseObjectContentItem.getString("description"));
                singleContentItem.setTitle(ParseObjectContentItem.getString("title"));
                singleContentItem.setHubName(subHub.getString("name"));
                singleContentItem.setScore(ParseObjectContentItem.getInt("score"));
                singleContentItem.username = createdByUser.getUsername();
                singleContentItem.replyNum = getReplies(ParseObjectContentItem, singleContentItem);
                singleContentItem.id = ParseObjectContentItem.getObjectId();


                if ((ParseFile) ParseObjectContentItem.get("image") != null) {
                    singleContentItem.url = ((ParseFile) ParseObjectContentItem.get("image")).getUrl();
                }

                temporaryContainer.add(singleContentItem);

            } catch (Exception e) {
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
            }
        }

        Log.d("Debugging", "Deubbing size" + listOfChosenSubHubs.size());

        //creating the query for the content
        ParseQuery<ParseObject> queryForTheContent = ParseQuery.getQuery("Comments");

        //sorted by
        queryForTheContent.addDescendingOrder("createdAt");

        if(currentNewestItem != null) {
            queryForTheContent.whereGreaterThan("createdAt", currentNewestItem);
        }
        queryForTheContent.whereContainedIn("subHub", listOfChosenSubHubs);

        //setting limit to 20
        queryForTheContent.setLimit(20);

        //getting all of the content items
        List<ParseObject> allContentItems = null;
        try {
            allContentItems = queryForTheContent.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //identifying the newest/oldest item and keeping it's place
        try {
            currentNewestItem = allContentItems.get(0).getCreatedAt();
        }catch(Exception e){

        }

        ArrayList<MotherThread> temporaryContainer = new ArrayList<MotherThread>();


        //getting all the liked comments the user has touched
        List<ParseObject> likedCommentsList = getLikedComments();


        //getting all the disliked comments the user has touched
        List<ParseObject> dislikedCommentsList = getDislikedComments();


        for (ParseObject ParseObjectContentItem : allContentItems) {


            //getting subHub
            ParseObject subHub = getSubHub(ParseObjectContentItem);

            //GETTING USER
            //maybe add only username text instead of object?
            ParseUser createdByUser = getUser(ParseObjectContentItem);


            try {
                final ContentItem singleContentItem = new ContentItem();

                //2 = liked, 1 = disliked
                if (likedCommentsList.contains(ParseObjectContentItem)) {
                    singleContentItem.state = 2;
                } else if (dislikedCommentsList.contains(ParseObjectContentItem)) {
                    singleContentItem.state = 1;
                }

                //setting the fields
                singleContentItem.setParseObj(ParseObjectContentItem);
                singleContentItem.date = ParseObjectContentItem.getCreatedAt();
                singleContentItem.setDescription(ParseObjectContentItem.getString("description"));
                singleContentItem.setTitle(ParseObjectContentItem.getString("title"));
                singleContentItem.setHubName(subHub.getString("name"));
                singleContentItem.setScore(ParseObjectContentItem.getInt("score"));
                singleContentItem.username = createdByUser.getUsername();
                singleContentItem.replyNum = getReplies(ParseObjectContentItem, singleContentItem);
                singleContentItem.id = ParseObjectContentItem.getObjectId();


                if ((ParseFile) ParseObjectContentItem.get("image") != null) {
                    singleContentItem.url = ((ParseFile) ParseObjectContentItem.get("image")).getUrl();
                }

                temporaryContainer.add(singleContentItem);

            } catch (Exception e) {
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


    // TODO: Rename method, update argument and hook method into UI event

    public static ParseUser getUser(ParseObject ParseObjectContentItem) {
        ParseRelation relationOfCreatedBy = ParseObjectContentItem.getRelation("user");
        ParseQuery queryToGetCreatedByUser = relationOfCreatedBy.getQuery();
        ParseUser createdByUser = null;


        try {
            createdByUser = (ParseUser) queryToGetCreatedByUser.getFirst();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return createdByUser;
    }

    public static List<ParseObject> getDislikedComments() {
        //getting all the disliked comments the user has touched
        ParseQuery<ParseObject> dislikedCommentsQuery = ParseQuery.getQuery("Comments");
        dislikedCommentsQuery.fromLocalDatastore();
        dislikedCommentsQuery.fromPin("dislikedComments");
        List<ParseObject> dislikedCommentsList = null;
        try {
            dislikedCommentsList = dislikedCommentsQuery.find();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dislikedCommentsList;
    }

    public static List<ParseObject> getLikedComments() {
        ParseQuery<ParseObject> likedCommentsQuery = ParseQuery.getQuery("Comments");
        likedCommentsQuery.fromLocalDatastore();
        likedCommentsQuery.fromPin("likedComments");
        List<ParseObject> likedCommentsList = null;
        try {
            likedCommentsList = likedCommentsQuery.find();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return likedCommentsList;
    }

    public static int getReplies(ParseObject ParseObjectContentItem, ContentItem singleContentItem) {

        ParseRelation<ParseObject> repliesToContentRelation = ParseObjectContentItem.getRelation("replies");
        ParseQuery<ParseObject> repliesToContentQuery = repliesToContentRelation.getQuery();
        int replyCount = 0;
        try {
            List<ParseObject> replies = repliesToContentQuery.find();
            replyCount = replies.size();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return replyCount;
    }

    //method to run asynctasks in parallel!
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static void StartAsyncTaskInParallel(AsyncTaskPopulateFeed task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        fragment = this;


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("DEBUG", "onCreatteee of hubfeedd fragmernt");
        setRetainInstance(true);

        adapter = new FeedAdapterV2(arrayOfItems, fragment.getActivity());

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_thread, container, false);

        Log.e("DEBUG", "called from onCreatEVIEW " + arrayOfItems.size());


        try {
//            hotTv = (TextView) v.findViewById(R.id.hotTV);
//            hotTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ArrayList<MotherThread> duplicate = new ArrayList<MotherThread>();
////                    Collections.copy(duplicate, arrayOfItems);
//                    Collections.sort(arrayOfItems, new Comparator<MotherThread>() {
//                        @Override
//                        public int compare(MotherThread obj1, MotherThread obj2) {
//
//                            if (obj1.score > obj2.score)
//                                return 1;
//                            else if (obj1.score < obj2.score)
//                                return -1;
//                            else
//                                return 0;
//                        }
//                    });
//                    Collections.reverse(arrayOfItems);
//                    adapter.notifyDataSetChanged();
////                    mListView.scrollToPosition(0);
//
//
//                }
//            });

//            newTv = (TextView) v.findViewById(R.id.newTV);
//            newTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Collections.copy(duplicate, arrayOfItems);
//                    Collections.sort(arrayOfItems, new Comparator<MotherThread>() {
//                        @Override
//                        public int compare(MotherThread obj1, MotherThread obj2) {
//
//                            return obj1.date.compareTo(obj2.date);
//                        }
//                    });
//                    Collections.reverse(arrayOfItems);
//                    adapter.notifyDataSetChanged();
////                    mListView.scrollToPosition(0);
//
//
//                }
//            });
            fab = (FloatingActionButton) v.findViewById(R.id.fab);
            mListView = (SuperRecyclerView) v.findViewById(R.id.list);
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
            }, 3);

            manager = new PreCachingLayoutManager(getActivity());
            mListView.setLayoutManager(manager);
            //bottomBar = (RelativeLayout) v.findViewById(R.id.bttmToolbar);
            mListView.getRecyclerView().addItemDecoration(new DividerItemDecoration(30));


            if (adapter == null) {
                System.out.println("NULLLL ADAPTER");
            } else {
                System.out.println("not nulllll " + adapter.getItemCount());
            }
            mListView.setAdapter(adapter);
            Log.e("DEBUG", "called from onCreatEVIEW  22" + arrayOfItems.size());

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent a = new Intent(getActivity(), PostNewContentActivity.class);
                    startActivity(a);
                }
            });


//            View footerView =  ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_view, null, false);
//            mListView.addFooterView(footerView);

            //querring to test
        } catch (Exception e) {

        }


        final Animation fadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_in);
        final Animation fadeOut = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_out);

        mListView.setOnScrollListener(new HidingScrollListener() {
            int pastVisiblesItems, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = manager.getChildCount();
                totalItemCount = manager.getItemCount();
                pastVisiblesItems = manager.findFirstVisibleItemPosition();

                Log.d("adapter count: ", manager.getItemCount() + "");
                if (flag) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 25) {

//                        flag = false;
//                        asyncTask4 = new MyAsyncTask4(fragment);
//                        asyncTask4.execute();
                    }
                }

            }

            @Override
            public void onHide() {
//                bottomBar.setVisibility(View.GONE);
//                fab.hide(true);
            }

            @Override
            public void onShow() {
//                bottomBar.setVisibility(View.VISIBLE);
//                fab.show(true);
            }
        });


        startNewAsyncTask();
        System.out.println("RETURNED V");
        return v;
    }

    private static class AsyncTaskPopulateFeed extends AsyncTask<Void, Void, Void> {

        private WeakReference<HubFeedFragment> fragmentWeakRef;

        private AsyncTaskPopulateFeed(HubFeedFragment fragment) {
            this.fragmentWeakRef = new WeakReference<HubFeedFragment>(fragment);
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
            if (updateRefresh == true) {
                updateRefresh = false;
            }

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

        private WeakReference<HubFeedFragment> fragmentWeakRef;

        private AsyncTaskRefreshFeed(HubFeedFragment fragment) {
            this.fragmentWeakRef = new WeakReference<HubFeedFragment>(fragment);
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


}