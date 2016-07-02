package beta.drab.hubber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import beta.drab.hubber.Models.User;
import beta.drab.hubber.R;
import de.greenrobot.event.EventBus;


public class LocationActivity extends ActionBarActivity {

    User user = new User();
    EditText zipcode;
    EditText school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        zipcode = ((EditText) findViewById(R.id.zipcode));
        school = ((EditText) findViewById(R.id.school));
        EventBus.getDefault().registerSticky(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(User event) {

        user = event;

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

    public void onNextClick(View w) {
        Intent i = new Intent(this, verifyPhoneActivity.class);


        user.zip = zipcode.getText().toString();
        user.schoolName = school.getText().toString();


        EventBus.getDefault().postSticky(user);

        startActivity(i);

    }
}
