package beta.drab.hubber.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;

import beta.drab.hubber.Models.User;
import beta.drab.hubber.R;
import de.greenrobot.event.EventBus;


public class verifyPhoneActivity extends ActionBarActivity {

    User user;
    EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        phoneNumber = ((EditText) findViewById(R.id.phoneNumber));
        EventBus.getDefault().registerSticky(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_verify_phone, menu);
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

    public void onNextClick(View w) {
        final Intent i = new Intent(this, FindFriendsActivity.class);
        ParseUser user = new ParseUser();
        user.setUsername(this.user.username);
        user.setPassword(this.user.password);
        user.setEmail(this.user.email);

        user.put("phone", this.user.phoneNumber);
        user.put("school", this.user.schoolName);
        user.put("dob", this.user.dob);
        user.put("zip", this.user.zip);

        try {
            user.signUp();
            startActivity(i);
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(User user) {
        this.user = user;
        this.user.phoneNumber = phoneNumber.getText().toString();

    }

}
