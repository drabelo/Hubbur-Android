package beta.drab.hubber.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import beta.drab.hubber.Activity.FeedModels.EventItem;
import beta.drab.hubber.Adapters.ReplyAdapter;
import beta.drab.hubber.R;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventCommentsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ListView mListView;
    EventItem contentItem;
    ReplyAdapter adapter;
    EditText editText;
    ArrayList<ParseObject> arrayOfItems;
    Button post;
    private String mParam1;
    private String mParam2;

    public EventCommentsFragment() {
        // Required empty public constructor
    }

    public static EventCommentsFragment newInstance(String param1, String param2) {
        EventCommentsFragment fragment = new EventCommentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_event_comments, container, false);
        EventBus.getDefault().registerSticky(this);
        mListView = (ListView) v.findViewById(R.id.eventReplyListView);
        post = (Button) v.findViewById(R.id.sendEvent);
        editText = (EditText) v.findViewById(R.id.replyEvent);


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPostClick(v);
            }
        });

        Log.d("Testing event:", contentItem.getTitle());
        loadReplies();
        return v;
    }


    public void onPostClick(View w) {
        Context context = getActivity().getApplicationContext();
        InputMethodManager inputManager =
                (InputMethodManager) context.
                        getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event");
        query.whereEqualTo("objectId", contentItem.getParseObj().getObjectId());
        ParseObject contentItem = null;
        try {
            contentItem = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        ParseObject currentReply = new ParseObject("Reply");

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        currentReply.setACL(defaultACL);

        String repl = editText.getText().toString();
        currentReply.put("reply", repl);

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseRelation relation2 = currentReply.getRelation("user");
        relation2.add(currentUser);

        ParseRelation relation3 = contentItem.getRelation("replies");
        relation3.add(currentReply);

        try {
            if (!editText.getText().toString().isEmpty()) {
                currentReply.save();
                contentItem.save();
                editText.clearComposingText();
                editText.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayOfItems.add(currentReply);
        adapter.notifyDataSetChanged();

    }


    public void loadReplies() {

        ParseRelation<ParseObject> replyRelation = contentItem.getParseObj().getRelation("replies");


        ParseQuery<ParseObject> replyQuery = replyRelation.getQuery();
        replyQuery.addAscendingOrder("createdAt");

        List<ParseObject> hubItems = null;
        try {

            hubItems = replyQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        arrayOfItems = new ArrayList<ParseObject>();

        for (ParseObject replyObj : hubItems) {
            arrayOfItems.add(replyObj);
        }


        adapter = new ReplyAdapter(getActivity(), arrayOfItems);
        mListView.setAdapter(adapter);
    }

    public void onEventMainThread(EventItem received) {


        contentItem = received;
        Log.d("Received:", "aa" + received.toString());

    }


}
