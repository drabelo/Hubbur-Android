package beta.drab.hubber.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import java.util.ArrayList;
import java.util.List;

import beta.drab.hubber.Adapters.Group;
import beta.drab.hubber.Adapters.MediaStoreUtils;
import beta.drab.hubber.R;

public class PostNewContentActivity extends ActionBarActivity implements View.OnClickListener {
    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;
    Spinner spinner;
    ImageView image;
    Button buttonChooseImage;
    Bitmap yourSelectedImage;
    PostNewContentActivity activity;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);
        image = (ImageView) findViewById(R.id.imageView11);
        progress = (ProgressBar) findViewById(R.id.progressBar);
        activity = this;


        ArrayList<Group> hubs = new ArrayList<Group>();

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

        buttonChooseImage = (Button) findViewById(R.id.button5);
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

            CropImageIntentBuilder cropImage = new CropImageIntentBuilder(10, 8, 1200, 960, croppedImage);
            cropImage.setSourceImage(data.getData());


            startActivityForResult(cropImage.getIntent(this), REQUEST_CROP_PICTURE);
        } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == RESULT_OK)) {
            // When we are done cropping, display it in the ImageView.\\
            yourSelectedImage = BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath());
            image.setImageBitmap(BitmapFactory.decodeFile(croppedImageFile.getAbsolutePath()));

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


    public void onPostClick(View w) {


        ((TextView) w).setEnabled(false);


        String hubName = spinner.getSelectedItem().toString();
        System.out.println("HUB NAME" + hubName);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("SubHub");
        query.setLimit(1);
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

        ParseObject comment = new ParseObject("Comments");


        EditText title = (EditText) findViewById(R.id.title);
        EditText descrip = (EditText) findViewById(R.id.description2);
        comment.put("description", descrip.getText().toString());
        comment.put("title", title.getText().toString());

        query.cancel();

        ParseRelation relation = comment.getRelation("subHub");
        relation.add(hub);

        comment.put("subHubName", hub.getString("name"));

        comment.put("score", 0);
        comment.put("drawableID", "00");

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseRelation relation2 = comment.getRelation("user");
        relation2.add(currentUser);

        //putting down image

        if (image.getDrawable() != null) {
            //saving image
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] data = stream.toByteArray();
            ParseFile parseImagefile = new ParseFile("profile_pic.png", data);
            try {
                parseImagefile.saveInBackground(new ProgressCallback() {
                    @Override
                    public void done(Integer percentDone) {
                        progress.setProgress(percentDone);
                    }
                });
            } catch (Exception e) {
            }

            comment.put("image", parseImagefile);
        } else {
            System.out.println("NOOO");
        }


        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        comment.setACL(defaultACL);
        comment.saveInBackground(new SaveCallback() {
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
