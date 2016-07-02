package beta.drab.hubber.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

import beta.drab.hubber.Activity.FeedModels.ContentItem;
import beta.drab.hubber.Activity.Profiles.SubHubProfileActivity;
import beta.drab.hubber.Activity.Profiles.UserActivity;
import beta.drab.hubber.Adapters.ReplyAdapter;
import beta.drab.hubber.R;
import de.greenrobot.event.EventBus;

public class contentDetailActivity extends AppCompatActivity {
    ListView mListView;
    ContentItem contentItem;
    ReplyAdapter adapter;
    EditText replyy;
    ArrayList<ParseObject> arrayOfItems;
    ImageButton upVote;
    contentDetailActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        EventBus.getDefault().registerSticky(this);




        if (contentItem.url == null) {
            setContentView(R.layout.activity_textcomment_view);

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });


            mListView = (ListView) findViewById(R.id.replyListView);
            adapter = new ReplyAdapter(this, arrayOfItems);


            TextView tvName = (TextView) findViewById(R.id.title);
            TextView tvHome = (TextView) findViewById(R.id.description2);
            TextView hub = (TextView) findViewById(R.id.hubName);
            TextView username = (TextView) findViewById(R.id.username);
            final TextView score = (TextView) findViewById(R.id.score);
            TextView replyNum = (TextView) findViewById(R.id.commentNum);
            TextView date = (TextView) findViewById(R.id.date);
            ImageView profPic = (ImageView) findViewById(R.id.profile_image);
            upVote = (ImageButton) findViewById(R.id.hand_up);




            try {
                ContentItem userr = contentItem;
                tvName.setText(userr.title + "");
                score.setText("" + userr.score);
                tvHome.setText(userr.description + "");
                hub.setText(userr.getHubName() + "");
                username.setText(userr.username + "");
                replyNum.setText(userr.replyNum + "");
                DateTime dateTime = new DateTime(userr.date);
                date.setText(DateUtils.getRelativeTimeSpanString(dateTime.getMillis()));


            } catch (Exception e) {
                e.printStackTrace();
            }

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getBaseContext(), UserActivity.class);
                    Log.d("username inside onclick", contentItem.username);
                    i.putExtra("username", contentItem.username);
                    activity.startActivity(i);
                }
            });

            hub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getBaseContext(), SubHubProfileActivity.class);
                    Log.d("username inside onclick", contentItem.hubName);
                    i.putExtra("username", contentItem.hubName);
                    activity.startActivity(i);
                }
            });

            if (contentItem.state == 1) {
                upVote.setBackgroundColor(Color.parseColor("#00e676"));
                upVote.setPressed(true);
                upVote.setEnabled(false);
            } else {
                upVote.setBackgroundColor(Color.parseColor("#e2e2e2"));
                upVote.setPressed(false);
                upVote.setEnabled(true);
            }


            upVote.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d("touched", "");
                    int i = 0;
                    try {
                        Log.d("what is i?", "" + score.getText() + " " + score.getText().toString());
                        i = Integer.parseInt(score.getText().toString());
                    } catch (Exception e) {

                    }

                    if (!upVote.isPressed()) {
                        upVote.setBackgroundColor(Color.parseColor("#00e676"));

                        contentItem.getParseObj().increment("score");
                        contentItem.getParseObj().saveInBackground();


                        //adding to the column dislikedPost in content
                        ParseRelation relationShowingWhoLikedThisPost = contentItem.getParseObj().getRelation("likedPost");
                        relationShowingWhoLikedThisPost.add(ParseUser.getCurrentUser());
                        try {
                            contentItem.getParseObj().save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        ParseRelation relationShowingWhatPostsIveLiked = ParseUser.getCurrentUser().getRelation("likedComments");
                        relationShowingWhatPostsIveLiked.add(contentItem.getParseObj());
                        try {
                            ParseUser.getCurrentUser().save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        contentItem.score++;
                        contentItem.state = 1;

                        Log.d("done", "what is i? " + i);
                        score.setText((i + 1) + "");
                        upVote.setPressed(true);
                        Log.d("done", "");

                    } else {
                        System.out.println("can't like this post since up : " + upVote.isPressed());
                    }


                    return true;
                }
            });



        }else {
            setContentView(R.layout.activity_picturecomment_view);

            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });

            mListView = (ListView) findViewById(R.id.replyListView);
            ContentItem userr = contentItem;
            ViewGroup header = null;
            TextView tvName = null;
            TextView hub = null;
            TextView tvHome = null;
            TextView username = null;
            TextView replyNum = null;
            TextView date = null;
            ImageView image = null;


            if (userr.description.isEmpty()) {

                System.out.println("here");
                header = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.convopic2_item, mListView, false);

                tvName = (TextView) header.findViewById(R.id.title);
                hub = (TextView) header.findViewById(R.id.hubName2);
                username = (TextView) header.findViewById(R.id.username2);
                replyNum = (TextView) header.findViewById(R.id.commentNum2);
                date = (TextView) header.findViewById(R.id.date2);
                image = (ImageView) header.findViewById(R.id.imagee);
                upVote = (ImageButton) header.findViewById(R.id.hand_up2);

                ///bad coding
                final TextView score = (TextView) header.findViewById(R.id.score2);
                score.setText("" + userr.score);
                upVote.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.d("touched", "");
                        int i = 0;
                        try {
                            Log.d("what is i?", "" + score.getText() + " " + score.getText().toString());
                            i = Integer.parseInt(score.getText().toString());
                        } catch (Exception e) {

                        }

                        if (!upVote.isPressed()) {
                            upVote.setBackgroundColor(Color.parseColor("#00e676"));

                            contentItem.getParseObj().increment("score");
                            contentItem.getParseObj().saveInBackground();


                            //adding to the column dislikedPost in content
                            ParseRelation relationShowingWhoLikedThisPost = contentItem.getParseObj().getRelation("likedPost");
                            relationShowingWhoLikedThisPost.add(ParseUser.getCurrentUser());
                            try {
                                contentItem.getParseObj().save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            ParseRelation relationShowingWhatPostsIveLiked = ParseUser.getCurrentUser().getRelation("likedComments");
                            relationShowingWhatPostsIveLiked.add(contentItem.getParseObj());
                            try {
                                ParseUser.getCurrentUser().save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            contentItem.score++;
                            contentItem.state = 1;

                            Log.d("done", "what is i? " + i);
                            score.setText((i + 1) + "");
                            upVote.setPressed(true);
                            Log.d("done", "");

                        } else {
                            System.out.println("can't like this post since up : " + upVote.isPressed());
                        }


                        return true;
                    }
                });

            } else {
                System.out.println("here 2");

                header = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.convopic_item, mListView, false);
                tvName = (TextView) header.findViewById(R.id.title);
                hub = (TextView) header.findViewById(R.id.hubName);
                tvHome = (TextView) header.findViewById(R.id.descriptionEvent);
                username = (TextView) header.findViewById(R.id.username);
                replyNum = (TextView) header.findViewById(R.id.commentNum);
                date = (TextView) header.findViewById(R.id.date);
                image = (ImageView) header.findViewById(R.id.imagee);
                upVote = (ImageButton) header.findViewById(R.id.hand_up);

                ///bad coding
                final TextView score = (TextView) header.findViewById(R.id.score2);
                score.setText("" + userr.score);
                upVote.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.d("touched", "");
                        int i = 0;
                        try {
                            Log.d("what is i?", "" + score.getText() + " " + score.getText().toString());
                            i = Integer.parseInt(score.getText().toString());
                        } catch (Exception e) {

                        }


                        if (!upVote.isPressed()) {
                            upVote.setBackgroundColor(Color.parseColor("#00e676"));

                            contentItem.getParseObj().increment("score");
                            contentItem.getParseObj().saveInBackground();


                            //adding to the column dislikedPost in content
                            ParseRelation relationShowingWhoLikedThisPost = contentItem.getParseObj().getRelation("likedPost");
                            relationShowingWhoLikedThisPost.add(ParseUser.getCurrentUser());
                            try {
                                contentItem.getParseObj().save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            ParseRelation relationShowingWhatPostsIveLiked = ParseUser.getCurrentUser().getRelation("likedComments");
                            relationShowingWhatPostsIveLiked.add(contentItem.getParseObj());
                            try {
                                ParseUser.getCurrentUser().save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            contentItem.score++;
                            contentItem.state = 1;

                            Log.d("done", "what is i? " + i);
                            score.setText((i + 1) + "");
                            upVote.setPressed(true);
                            Log.d("done", "");

                        } else {
                            System.out.println("can't like this post since up : " + upVote.isPressed());
                        }


                        return true;
                    }
                });

            }


            mListView.addHeaderView(header, null, false);


            try {
                tvName.setText(userr.title + "");
                if (userr.description.isEmpty()) {

                } else {
                    System.out.println(" not null " + userr.description);
                    tvHome.setText(userr.description);
                }
                hub.setText(userr.getHubName() + "");
                username.setText(userr.username + "");
                replyNum.setText(userr.replyNum + "");
                DateTime dateTime = new DateTime(userr.date);
                date.setText(DateUtils.getRelativeTimeSpanString(dateTime.getMillis()));

                Picasso.with(this).load(userr.url).placeholder(R.drawable.profile_image).into(image);


            } catch (Exception e) {
                e.printStackTrace();
            }


            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getBaseContext(), UserActivity.class);
                    Log.d("username inside onclick", contentItem.username);
                    i.putExtra("username", contentItem.username);
                    getApplicationContext().startActivity(i);
                }
            });

            hub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getBaseContext(), SubHubProfileActivity.class);
                    Log.d("username inside onclick", contentItem.hubName);
                    i.putExtra("username", contentItem.hubName);
                    getBaseContext().startActivity(i);
                }
            });





            try {
//                ThreadItem userr = contentItem;
//                tvName.setText(userr.title + "");
//                tvHome.setText(userr.description + "");
//                hub.setText(userr.getHubName() + "");
//                username.setText(userr.username + "");
//                replyNum.setText(userr.replyNum + "");
//                //image.setImageBitmap(userr.image);
//
//
//                DateTime dateTime = new DateTime(userr.date);
//                DateTimeFormatter fmt = DateTimeFormat.forPattern("hh:mm");
//                date.setText(fmt.print(dateTime));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

                loadReplies();


    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public void onEventMainThread(ContentItem received) {


        contentItem = received;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment_view, menu);
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
        Context context = getApplicationContext();
        InputMethodManager inputManager =
                (InputMethodManager) context.
                        getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);


        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.whereEqualTo("title", contentItem.getTitle());
        ParseObject contentItem = null;
        try {
            contentItem = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        replyy = (EditText) findViewById(R.id.replyM);

        ParseObject currentReply = new ParseObject("Reply");

        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
        currentReply.setACL(defaultACL);

        String repl = replyy.getText().toString();
        currentReply.put("reply", repl);

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseRelation relation2 = currentReply.getRelation("user");
        relation2.add(currentUser);

        ParseRelation relation3 = contentItem.getRelation("replies");
        relation3.add(currentReply);

        try {
            if (!replyy.getText().toString().isEmpty()) {
                currentReply.save();
                contentItem.save();
                replyy.clearComposingText();
                replyy.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        arrayOfItems.add(currentReply);
        adapter.notifyDataSetChanged();

    }


    public void loadReplies() {

        ParseRelation<ParseObject> replyRelation = contentItem.getParseObj().getRelation("replies");


        ParseQuery<ParseObject> replyQuery = replyRelation.getQuery();
        replyQuery.addAscendingOrder("createdAt");

        arrayOfItems = new ArrayList<ParseObject>();

           replyQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {

                    for (ParseObject replyObj : list) {
                        arrayOfItems.add(replyObj);
                    }


                    adapter = new ReplyAdapter(getApplicationContext(), arrayOfItems);
                    mListView.setAdapter(adapter);
                }
            });




    }
}
