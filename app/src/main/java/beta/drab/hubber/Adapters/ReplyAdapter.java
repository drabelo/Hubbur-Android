package beta.drab.hubber.Adapters;

/**
 * Created by dailtonrabelo on 3/9/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;

import beta.drab.hubber.R;

/**
 * Created by dailtonrabelo on 2/26/15.
 */

public class ReplyAdapter extends ArrayAdapter<ParseObject> {


    public ReplyAdapter(Context context, ArrayList<ParseObject> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ParseObject reply = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view

        // Lookup view for data population

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.reply_item, parent, false);
        }
        TextView username = (TextView) convertView.findViewById(R.id.username_reply);
        TextView replyy = (TextView) convertView.findViewById(R.id.reply);

        replyy.setText(reply.getString("reply"));

        ParseRelation userrelation = reply.getRelation("user");
        ParseQuery queryuser = userrelation.getQuery();
        ParseUser currentUser = null;

        try {
            currentUser = (ParseUser) queryuser.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        username.setText(currentUser.getUsername());


        // Populate the data into the template view using the data object
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
        //tvHome.setText(((ThreadItem) user).description + "");

        // Return the completed view to render on screen


        return convertView;
    }
}
