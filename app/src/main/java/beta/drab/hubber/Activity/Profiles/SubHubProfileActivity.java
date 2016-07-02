package beta.drab.hubber.Activity.Profiles;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import beta.drab.hubber.R;

public class SubHubProfileActivity extends ActionBarActivity {
    ParseObject user;
    SubHubProfileActivity activity;
    public static ParseObject findSubHubFromTitle(String name) {
        ParseQuery<ParseUser> queryForUser = ParseQuery.getQuery("SubHub");
        queryForUser.whereEqualTo("name", name);
        ParseObject subHubObject = null;

        try {
            subHubObject = queryForUser.getFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(name, " username should be " + subHubObject.getString("name"));
        return subHubObject;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub_profile);
        activity = this;
        // Inflate the layout for this fragment
        Intent intent = getIntent();

        Log.d("username", intent.getStringExtra("username"));
        user = findSubHubFromTitle(intent.getStringExtra("username"));
        ImageView profPic = (ImageView) findViewById(R.id.profile_image);

        if ((ParseFile) user.get("profilePicture") != null) {
            ParseFile image = (ParseFile) user.get("profilePicture");
            byte[] data;
            try {
                data = image.getData();
                profPic.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {

        }


        TextView subHubName = (TextView) findViewById(R.id.username);

        subHubName.setText(user.getString("name"));

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
        getMenuInflater().inflate(R.menu.menu_hub_profile, menu);
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
}
