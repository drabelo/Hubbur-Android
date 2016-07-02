package beta.drab.hubber.Activity.Profiles;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import beta.drab.hubber.Activity.Detail4Picture;
import beta.drab.hubber.Activity.MyHubs;
import beta.drab.hubber.Activity.firstActivity;
import beta.drab.hubber.Adapters.MediaStoreUtils;
import beta.drab.hubber.R;

public class UserActivity extends AppCompatActivity {
    ParseUser user;
    private static int REQUEST_PICTURE = 1;
    UserActivity userActivity;

    public static ParseUser findUserFromUsername(String name) {
        ParseQuery<ParseUser> queryForUser = ParseUser.getQuery();
        queryForUser.whereEqualTo("username", name);
        ParseUser user = null;

        try {
            user = queryForUser.getFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(name, " username should be " + user.getUsername());
        return user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Intent intent = getIntent();
    userActivity = this;
        // Inflate the layout for this fragment
        Log.d("username", intent.getStringExtra("username"));
        user = findUserFromUsername(intent.getStringExtra("username"));
        ImageView profPic = (ImageView) findViewById(R.id.profile_image);

            ParseFile image = (ParseFile) user.get("profilePicture");
try {
    Picasso.with(this).load(image.getUrl()).placeholder(R.drawable.profile_image).into(profPic);
}catch(Exception e){e.printStackTrace();}




        TextView username = (TextView) findViewById(R.id.username);

        username.setText("@" + user.getUsername());

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userActivity.finish();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user, menu);
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

    public void onHubClick(View w) {
        Intent i = new Intent(this, MyHubs.class);
        startActivity(i);

    }

    public void onPicClick(View w) {
        Intent i = new Intent(this, Detail4Picture.class);
        startActivity(i);

    }

    public void onLogOut(View w) {
        ParseUser.logOut();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Intent i = new Intent(this, firstActivity.class);
        startActivity(i);

    }

    public void onChangeProfPic(View w) {
        startActivityForResult(MediaStoreUtils.getPickImageIntent(this), REQUEST_PICTURE);

    }

    public void followUser(View w){
        ParseRelation relation = ParseUser.getCurrentUser().getRelation("following");
        relation.add(user);
        ParseUser.getCurrentUser().saveInBackground();
        System.out.println("following");
    }
}
