package beta.drab.hubber.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import beta.drab.hubber.Activity.FeedModels.EventItem;
import beta.drab.hubber.R;
import de.greenrobot.event.EventBus;

public class EventDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ImageView image;
    TextView title;
    TextView title2;
    TextView description;
    TextView date;
    TextView date2;
    TextView going;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    static EventItem eventItem;


    public EventDetailFragment() {
        // Required empty public constructor
    }

    public static EventDetailFragment newInstance(String param1, String param2) {
        EventDetailFragment fragment = new EventDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().registerSticky(this);
        System.out.println("Received in just checking inside fragment");


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(EventItem received) {


        eventItem = received;
        Log.d("Received in Fragment:", "aa" + eventItem.toString());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_event_detail, container, false);
        try {
            image = (ImageView) v.findViewById(R.id.imageEvent);
            title = (TextView) v.findViewById(R.id.titleEvent);
            title2 = (TextView) v.findViewById(R.id.titleEvent2);
            description = (TextView) v.findViewById(R.id.descriptionEvent);
            date = (TextView) v.findViewById(R.id.dateEvent);
            date2 = (TextView) v.findViewById(R.id.dateEvent2);
            going = (TextView) v.findViewById(R.id.going);

            Picasso.with(getActivity()).load(eventItem.url).into(image);
            title2.setText(eventItem.getTitle());

            title.setText(eventItem.getTitle());
            description.setText(eventItem.description);
            date.setText(eventItem.date.toString());
            date2.setText(eventItem.date.toString());

        } catch (Exception e) {
        }

        going.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseRelation relation = ParseUser.getCurrentUser().getRelation("ChosenEvents");
                relation.add(eventItem.getParseObj());
                try {
                    ParseUser.getCurrentUser().save();
                    going.setBackgroundColor(Color.parseColor("#FFEB3B"));
                    eventItem.state = 1;
                    going.setPressed(true);
                    going.setEnabled(false);
                    eventItem.getParseObj().pin("ChosenEvents");
                    going.setText("going");
                    ParseRelation relationGoing = eventItem.getParseObj().getRelation("going");
                    relationGoing.add(ParseUser.getCurrentUser());
                    eventItem.getParseObj().saveInBackground();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        if (eventItem.state == 1) {
            going.setBackgroundColor(Color.parseColor("#FFEB3B"));
            going.setPressed(true);
            going.setText("Going");
            going.setEnabled(false);
        } else {
            going.setBackgroundColor(Color.parseColor("#00e676"));
            going.setPressed(false);
            going.setText("Go");
            going.setEnabled(true);

        }


        return v;
    }



}
