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


public class RegisterActivity extends ActionBarActivity {

    EditText email;
    EditText password;
    EditText username;
    EditText birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        email = ((EditText) findViewById(R.id.email));
        password = ((EditText) findViewById(R.id.password));
        username = ((EditText) findViewById(R.id.username));
        birthday = ((EditText) findViewById(R.id.birthday));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

        User user = new User();

        user.email = email.getText().toString();
        user.password = password.getText().toString();
        user.username = username.getText().toString();
        user.dob = birthday.getText().toString();

        EventBus.getDefault().postSticky(user);


        Intent i = new Intent(this, LocationActivity.class);
        startActivity(i);


    }
}
