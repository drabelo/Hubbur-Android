package beta.drab.hubber.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.camera.CropImageIntentBuilder;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import beta.drab.hubber.Adapters.MediaStoreUtils;
import beta.drab.hubber.Pickers.DatePickerFragment;
import beta.drab.hubber.Pickers.TimePickerFragment;
import beta.drab.hubber.R;

public class NewEventActivity extends ActionBarActivity implements View.OnClickListener {
    public static String time;
    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;
    Spinner spinner;
    Bitmap yourSelectedImage;
    Button buttonChooseImage;
    ImageView picture;
    ProgressBar progress;
    NewEventActivity activity;
    TextView dateTV;
    TextView hourTV;
    DateFormat format = new SimpleDateFormat("MM/dd/yyyy kk:mm", Locale.ENGLISH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        activity = this;

        progress = (ProgressBar) findViewById(R.id.progressBarForEvent);

        dateTV = (TextView) findViewById(R.id.dateForEvent);
        hourTV = (TextView) findViewById(R.id.timeForEvent);

        picture = (ImageView) findViewById(R.id.imageView21);

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseRelation<ParseObject> relation = currentUser
                .getRelation("chosenSubHub");


        ParseQuery<ParseObject> query = relation.getQuery();
        List<ParseObject> queryHub = null;
        try {

            queryHub = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ArrayList<String> values = new ArrayList<String>();
        for (ParseObject hub : queryHub) {
            values.add(hub.getString("name"));
        }

        spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        buttonChooseImage = (Button) findViewById(R.id.button4);
        buttonChooseImage.setOnClickListener(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File croppedImageFile = new File(getFilesDir(), "test.jpg");

        if ((requestCode == REQUEST_PICTURE) && (resultCode == RESULT_OK)) {
            // When the user is done picking a picture, let's start the CropImage Activity,
            // setting the output image file and size to 200x200 pixels square.
            Uri croppedImage = Uri.fromFile(croppedImageFile);

            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(1, 1, 150, 150, croppedImage);
            cropImage.setSourceImage(data.getData());


            startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
        } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == RESULT_OK)) {
            // When we are done cropping, display it in the ImageView.
            yourSelectedImage = BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath());
            picture.setImageBitmap(BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath()));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_comment, menu);
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

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void onPostClick(View v) {
        try {
            String newDate =dateTV.getText().toString() +" " +hourTV.getText().toString();
            System.out.println(newDate);
            System.out.println(format.parse(newDate));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        ((TextView) v).setEnabled(false);


        String hubName = spinner.getSelectedItem().toString();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SubHub");
        query.whereEqualTo("name", hubName);
        ParseObject hub = null;
        try {
            hub = query.getFirst();
            if (hub == null) {
                System.out.println("came back null");

            } else {
                System.out.println(hub.getString("name"));
            }
        } catch (Exception e) {
            e.getMessage();
        }


        ParseObject event = new ParseObject("Event");
        EditText title = (EditText) findViewById(R.id.title);
        EditText descrip = (EditText) findViewById(R.id.description2);
        EditText location = (EditText) findViewById(R.id.location);

        event.put("description", descrip.getText().toString());
        event.put("title", title.getText().toString());
        event.put("location", location.getText().toString());

        String newDate =dateTV.getText().toString() +" " +hourTV.getText().toString();
        try {
            event.put("event_time", format.parse(newDate));
        }catch(Exception e){
            e.printStackTrace();
        }
        ///get date

        //get time

        ParseRelation relation = event.getRelation("Hub");
        relation.add(hub);


        //saving user
        ParseRelation relation2 = event.getRelation("createdBy");
        relation2.add(ParseUser.getCurrentUser());


        //saving image
        try {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] data = stream.toByteArray();
        ParseFile parseImagefile = new ParseFile("profile_pic.png", data);

            parseImagefile.saveInBackground(new ProgressCallback() {
                @Override
                public void done(Integer percentDone) {
                    progress.setProgress(percentDone);
                }
            });
            event.put("eventPicture", parseImagefile);
        } catch (Exception e) {
        }




        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        event.setACL(defaultACL);

        event.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    activity.finish();
                } else {
                }
            }
        });


    }


    @Override
    public void onClick(View v) {
        if (v.equals(buttonChooseImage)) {
            startActivityForResult(MediaStoreUtils.getPickImageIntent(this), REQUEST_PICTURE);
        }
    }
}
