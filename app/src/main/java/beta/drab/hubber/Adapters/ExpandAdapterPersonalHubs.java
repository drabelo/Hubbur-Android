package beta.drab.hubber.Adapters;

/**
 * Created by dailtonrabelo on 2/19/15.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;

import beta.drab.hubber.R;

public class ExpandAdapterPersonalHubs extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<Group> groups;

    public ExpandAdapterPersonalHubs(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    public ExpandAdapterPersonalHubs() {
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Child> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Child child = (Child) getChild(groupPosition, childPosition);
        final Group group = (Group) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_item2, null);
        }
        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.checkbox);
        cb.setText(child.getName().toString());


        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ParseRelation relation2 = currentUser.getRelation("chosenSubHub");

                //adding Hub//getting HUB


                if (cb.isChecked()) {
                    ParseQuery query = relation2.getQuery();
                    query.whereEqualTo("name", child.getName());
                    ParseObject subhub = null;
                    try {
                        subhub = query.getFirst();
                    } catch (Exception e) {


                        try {
                            ParseQuery lmfao = ParseQuery.getQuery("SubHub");
                            query.whereEqualTo("name", child.getName());
                            ParseObject hello = lmfao.getFirst();
                            relation2.add(hello);
                            currentUser.save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }


                } else {
                    try {
                        ParseQuery query = relation2.getQuery();
                        query.whereEqualTo("name", child.getName());
                        ParseObject subhub = query.getFirst();
                        relation2.remove(subhub);
                        currentUser.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        return convertView;

    }


    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Child> chList = groups.get(groupPosition).getItems();
        return chList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Group group = (Group) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_item, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.group_name);
        tv.setText(group.getName());

        View view = (View) convertView.findViewById(R.id.imageView9);

        //getting random COLOR

        view.setBackgroundColor(group.getColor());

        //ImageView iv = ((ImageView) convertView.findViewById(R.id.imageView2));

        int sdk = android.os.Build.VERSION.SDK_INT;

//
//        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            Drawable pic = group.getPic();
//            pic.setColorFilter(Color.DKGRAY,
//                    PorterDuff.Mode.SCREEN);
//            iv.setBackgroundDrawable(pic);
//        } else {
//            Drawable pic = group.getPic();
//            pic.setColorFilter(Color.DKGRAY,
//                    PorterDuff.Mode.SCREEN);
//            iv.setBackground(pic);
//        }


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}