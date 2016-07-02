package beta.drab.hubber.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import beta.drab.hubber.R;


public class firstActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);


//
//        try {
//
//
//            ParseQuery<ParseObject> query = ParseQuery.getQuery("HubItem");
//            query.setLimit(1);
//            query.whereEqualTo("title", "Science");
//            query.getFirstInBackground(new GetCallback<ParseObject>() {
//                public void done(ParseObject object2, ParseException e) {
//                    if (object2 == null) {
//                        Log.d("score", "The getFirst request failed." );
//                    } else {
//                        Log.d("score", "Retrieved the object." + object2.get("title"));
//
//                    }
//                }
//            });
//            query.cancel();
//            ParseObject hub = query.getFirst();
//
//
//            ParseQuery<ParseObject> query2 = ParseQuery.getQuery("SubHub");
//            query2.setLimit(1);
//            query2.whereEqualTo("name", "Physics");
//            query2.getFirstInBackground(new GetCallback<ParseObject>() {
//                public void done(ParseObject object, ParseException e) {
//                    if (object == null) {
//                        Log.d("score", "The getFirst request failed.");
//                    } else {
//                        Log.d("score", "Retrieved the object."  + object.get("name"));
//                    }
//                }
//            });
//
//            query.cancel();
//            ParseObject hub = query.getFirst();
//            query2.cancel();
//            ParseObject sub = query2.getFirst();
//
//            ParseRelation relation = sub.getRelation("hub");
//            relation.add(hub);
//            sub.saveInBackground();


//
//
//
//            System.out.println("saved");
//        }catch(Exception e){
//            e.printStackTrace();
//        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first, menu);


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

    public void onLoginClick(View w) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void onSignupClick(View w) {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);

    }
}
