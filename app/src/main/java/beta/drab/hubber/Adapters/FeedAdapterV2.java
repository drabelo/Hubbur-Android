package beta.drab.hubber.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import beta.drab.hubber.Activity.EventDetailActivity;
import beta.drab.hubber.Activity.FeedModels.ContentItem;
import beta.drab.hubber.Activity.FeedModels.EventItem;
import beta.drab.hubber.Activity.FeedModels.MotherThread;
import beta.drab.hubber.Activity.Profiles.SubHubProfileActivity;
import beta.drab.hubber.Activity.Profiles.UserActivity;
import beta.drab.hubber.Activity.contentDetailActivity;
import beta.drab.hubber.R;
import de.greenrobot.event.EventBus;

/**
 * Created by dailtonrabelo on 3/27/15.
 */
public class FeedAdapterV2 extends RecyclerView.Adapter<FeedAdapterV2.ViewHolder> {

    ArrayList<MotherThread> users;
    Context context;

    public FeedAdapterV2(ArrayList<MotherThread> users, Context context) {
        this.users = users;
        this.context = context;


    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(byte[] data,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == 0) {
            View v = inflater.inflate(R.layout.convotext_item, parent, false);
            return new ViewGroupText(v, this.context);
        } else if (viewType == 2) {
            View v = inflater.inflate(R.layout.event_item, parent, false);
            return new ViewHolderEvent(v, this.context);

        } else if (viewType == 4) {
            View v = inflater.inflate(R.layout.convopic2_item, parent, false);
            return new ViewHolderPic2(v, this.context);
//        } else if (viewType == 3) {
//            View v = inflater.inflate(R.layout.footer_view, parent, false);
//            return new ViewHolderFooter(v);
        } else {
            View v = inflater.inflate(R.layout.convopic_item, parent, false);
            return new ViewHolderPic(v, this.context);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Object item = users.get(position);
        Log.d("objects", users.get(position).toString());
        holder.bind(item);
    }

    @Override
    public int getItemViewType(int position) {
        MotherThread user = users.get(position);
        int flag = 0;
        Log.d("debugging", "herE HITT");
        Log.d("debugging", " " + users.size() + " is empty:" + users.isEmpty());
        if (user instanceof ContentItem) {
            if (((ContentItem) user).url == null) {
                flag = 0;
            } else {
                if (((ContentItem) user).description.isEmpty()) {
                    flag = 4;
                } else {
                    flag = 1;
                    System.out.println("HITTTTT " + ((ContentItem) user).description);
                }

            }
        } else if (user instanceof EventItem) {
            System.out.println("EVENT ITEM");
            flag = 2;
        } else {
        }
        return flag;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static abstract class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {

            super(itemView);
        }

        abstract void bind(Object object);
    }

    class ViewGroupText extends ViewHolder {

        RelativeLayout layout;
        ImageButton upVote;
        ImageButton downVote;
        TextView tvName;
        TextView tvHome;
        TextView hub;
        TextView username;
        TextView score;
        TextView replyNum;
        TextView date;
        //ImageView profPic;
        Context context;
        Typeface type1;
        Typeface type2;


        public ViewGroupText(View v, Context ctx) {
            super(v);
            layout = (RelativeLayout) v.findViewById(R.id.relly);
            upVote = (ImageButton) v.findViewById(R.id.hand_up);
            //downVote = (ImageButton) v.findViewById(R.id.hand_down);
            tvName = (TextView) v.findViewById(R.id.title);
            tvHome = (TextView) v.findViewById(R.id.description2);
            hub = (TextView) v.findViewById(R.id.hubName);
            username = (TextView) v.findViewById(R.id.username);
            score = (TextView) v.findViewById(R.id.score);
            replyNum = (TextView) v.findViewById(R.id.commentNum);
            date = (TextView) v.findViewById(R.id.date);
            //profPic = (ImageView) v.findViewById(R.id.profile_image);
            context = ctx;
        }


        @Override
        void bind(Object object) {

            final ContentItem mItem = (ContentItem) object;


            try {
                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EventBus.getDefault().postSticky(mItem);
                        Intent intent = new Intent(context, contentDetailActivity.class);
                        context.startActivity(intent);
                    }
                });


                if (mItem.state == 1) {
                    upVote.setBackgroundColor(Color.parseColor("#00e676"));
                    upVote.setPressed(true);
                    upVote.setEnabled(false);
                } else {
                    upVote.setBackgroundColor(Color.parseColor("#e2e2e2"));
                    upVote.setPressed(false);
                    upVote.setEnabled(true);
                }

                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(context, UserActivity.class);
                        Log.d("username inside onclick", mItem.username);
                        i.putExtra("username", mItem.username);
                        context.startActivity(i);
                    }
                });

                hub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent i = new Intent(context, SubHubProfileActivity.class);
                        Log.d("username inside onclick", mItem.hubName);
                        i.putExtra("username", mItem.hubName);
                        context.startActivity(i);
                    }
                });


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

                            mItem.getParseObj().increment("score");
                            mItem.getParseObj().saveInBackground();


                            //adding to the column dislikedPost in content
                            ParseRelation relationShowingWhoLikedThisPost = mItem.getParseObj().getRelation("likedPost");
                            relationShowingWhoLikedThisPost.add(ParseUser.getCurrentUser());
                            try {
                                mItem.getParseObj().save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            ParseRelation relationShowingWhatPostsIveLiked = ParseUser.getCurrentUser().getRelation("likedComments");
                            relationShowingWhatPostsIveLiked.add(mItem.getParseObj());
                            try {
                                ParseUser.getCurrentUser().save();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            mItem.score++;
                            mItem.state = 1;

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
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
                tvName.setText(mItem.title + "");
                score.setText(mItem.getScore() + "");
                tvHome.setText(((ContentItem) mItem).description + "");
                hub.setText(" " + mItem.getHubName());
                username.setText("@" + mItem.username + " ");
                replyNum.setText(mItem.replyNum + " COMMENTS");


                DateTime dateTime = new DateTime(mItem.date);


                date.setText(DateUtils.getRelativeTimeSpanString(dateTime.getMillis()));
            } catch (Exception e) {
            }


//            byte[] data;
//            try {
//                data = mItem.userProfPic.getData();
//                profPic.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
//
//            } catch (Exception e) {
//               profPic.setImageDrawable(context.getDrawable(R.drawable.profile_image));
//
//            }
        }
    }

    class ViewHolderPic extends ViewHolder {
        FrameLayout layout;
        ImageButton upVote;
        //ImageButton downVote;
        TextView tvName;
        TextView tvHome;
        TextView hub;
        TextView username;
        TextView score;
        TextView replyNum;
        TextView date;
        ImageView profPic;
        ImageView image;
        Context context;
        Typeface type1;
        Typeface type2;


        public ViewHolderPic(View v, Context ctx) {
            super(v);

            layout = (FrameLayout) v.findViewById(R.id.imagess);
            upVote = (ImageButton) v.findViewById(R.id.hand_up);
            //downVote = (ImageButton) v.findViewById(R.id.hand_down);
            tvName = (TextView) v.findViewById(R.id.title);
            tvHome = (TextView) v.findViewById(R.id.descriptionEvent);
            hub = (TextView) v.findViewById(R.id.hubName);
            username = (TextView) v.findViewById(R.id.username);
            score = (TextView) v.findViewById(R.id.score2);
            replyNum = (TextView) v.findViewById(R.id.commentNum);
            date = (TextView) v.findViewById(R.id.date);
            image = (ImageView) v.findViewById(R.id.imagee);
            profPic = (ImageView) v.findViewById(R.id.profile_image);
            context = ctx;

        }

        @Override
        void bind(Object object) {

            final ContentItem mItem = (ContentItem) object;


            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventBus.getDefault().postSticky(mItem);


                    Intent intent = new Intent(context, contentDetailActivity.class);
                    context.startActivity(intent);
                }
            });

            score.setText(mItem.score + "");

            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context, UserActivity.class);
                    Log.d("username inside onclick", mItem.username);
                    i.putExtra("username", mItem.username);
                    context.startActivity(i);
                }
            });

            hub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context, SubHubProfileActivity.class);
                    Log.d("username inside onclick", mItem.hubName);
                    i.putExtra("username", mItem.hubName);
                    context.startActivity(i);
                }
            });


            if (mItem.state == 1) {
                upVote.setBackgroundColor(Color.parseColor("#00e676"));
                upVote.setPressed(true);
                upVote.setEnabled(false);
            } else {
                upVote.setBackgroundColor(Color.parseColor("#e2e2e2"));
                upVote.setPressed(false);
            }


//            downVote.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//
//
//                    int i = Integer.parseInt(score.getText().toString());
//                    if (!downVote.isPressed() && !upVote.isPressed()) {
//
//                        if (Integer.parseInt(score.getText().toString()) > 0) {
//
//                            if (mItem.getParseObj() != null)
//                                mItem.getParseObj().increment("score", -1);
//                            else
//                                Log.d("debug", "nope");
//
//                            //adding to the column dislikedPost in content
//                            ParseRelation relation = mItem.getParseObj().getRelation("dislikedPost");
//                            relation.add(ParseUser.getCurrentUser());
//
//
//                            //saving comment item and user with data in each being changed
//                            try {
//                                mItem.getParseObj().save();
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            ParseRelation relation2 = ParseUser.getCurrentUser().getRelation("dislikedComments");
//                            relation2.add(mItem.getParseObj());
//                            try {
//                                ParseUser.getCurrentUser().save();
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//                            mItem.score--;
//                            downVote.setBackgroundColor(Color.parseColor("#e93a4b"));
//                            score.setText((i - 1) + "");
//                            downVote.setPressed(true);
//                        }
//                    } else {
//                        System.out.println("can't like this post since up : " + upVote.isPressed() + " downvote: " + downVote.isPressed());
//                    }
//
//
//                    return true;
//                }
//            });


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

                        mItem.getParseObj().increment("score");
                        mItem.getParseObj().saveInBackground();


                        //adding to the column dislikedPost in content
                        ParseRelation relationShowingWhoLikedThisPost = mItem.getParseObj().getRelation("likedPost");
                        relationShowingWhoLikedThisPost.add(ParseUser.getCurrentUser());
                        try {
                            mItem.getParseObj().save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        ParseRelation relationShowingWhatPostsIveLiked = ParseUser.getCurrentUser().getRelation("likedComments");
                        relationShowingWhatPostsIveLiked.add(mItem.getParseObj());
                        try {
                            ParseUser.getCurrentUser().save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        mItem.score++;
                        mItem.state = 1;

                        upVote.setBackgroundColor(Color.parseColor("#00e676"));
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


            try {
                tvName.setText(mItem.title + "");
                score.setText(mItem.getScore() + "");
                tvHome.setText(((ContentItem) mItem).description + "");
                hub.setText(" " + mItem.getHubName());
                username.setText("@" + mItem.username + " ");
                replyNum.setText(mItem.replyNum + " COMMENTS");

                DateTime dateTime = new DateTime(mItem.date);

                date.setText(DateUtils.getRelativeTimeSpanString(dateTime.getMillis()));
            } catch (Exception e) {
            }


//            byte[] data;
//            try {
//                data = mItem.userProfPic.getData();
//                profPic.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
//
//            } catch (Exception e) {
//                profPic.setImageDrawable(context.getDrawable(R.drawable.profile_image));
//
//            }

            try {
                Picasso.with(context).load(mItem.url).into(image);


            } catch (Exception e) {
                Log.d("debug", e.getMessage());
            }


        }

//        @Override
//        void bind(Object object) {
//            mItem = (Group) object;
//            String suffix = mItem.mExpanded ? " -" : " +";
//            mTextView.setText(mItem.mTitle + suffix);
//        }
    }

    class ViewHolderPic2 extends ViewHolder {
        FrameLayout layout;
        ImageButton upVote;
        ImageButton downVote;
        TextView tvName;
        TextView hub;
        TextView username;
        TextView score;
        TextView replyNum;
        TextView date;
        ImageView profPic;
        ImageView image;
        Context context;
        Typeface type1;
        Typeface type2;


        public ViewHolderPic2(View v, Context ctx) {
            super(v);

            layout = (FrameLayout) v.findViewById(R.id.imagess);
            upVote = (ImageButton) v.findViewById(R.id.hand_up2);
//            downVote = (ImageButton) v.findViewById(R.id.hand_down2);
            tvName = (TextView) v.findViewById(R.id.title);
            hub = (TextView) v.findViewById(R.id.hubName2);
            username = (TextView) v.findViewById(R.id.username2);
            score = (TextView) v.findViewById(R.id.score2);
            replyNum = (TextView) v.findViewById(R.id.commentNum2);
            date = (TextView) v.findViewById(R.id.date2);
            image = (ImageView) v.findViewById(R.id.imagee);
            context = ctx;

        }


        @Override
        void bind(Object object) {

            final ContentItem mItem = (ContentItem) object;


            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventBus.getDefault().postSticky(mItem);


                    Intent intent = new Intent(context, contentDetailActivity.class);
                    context.startActivity(intent);
                }
            });

            score.setText(mItem.score + "");


            if (mItem.state == 1) {
                upVote.setBackgroundColor(Color.parseColor("#00e676"));
                upVote.setPressed(true);
                upVote.setEnabled(false);
            } else {
                upVote.setBackgroundColor(Color.parseColor("#e2e2e2"));
                upVote.setPressed(false);
            }


            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context, UserActivity.class);
                    Log.d("username inside onclick", mItem.username);
                    i.putExtra("username", mItem.username);
                    context.startActivity(i);
                }
            });

            hub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(context, SubHubProfileActivity.class);
                    Log.d("username inside onclick", mItem.hubName);
                    i.putExtra("username", mItem.hubName);
                    context.startActivity(i);
                }
            });


            upVote.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    upVote.setBackgroundColor(Color.parseColor("#00e676"));
                    Log.d("touched", "");
                    int i = 0;
                    try {
                        Log.d("what is i?", "" + score.getText() + " " + score.getText().toString());
                        i = Integer.parseInt(score.getText().toString());
                    } catch (Exception e) {

                    }

                    if (!upVote.isPressed()) {

                        mItem.getParseObj().increment("score");
                        mItem.getParseObj().saveInBackground();


                        //adding to the column dislikedPost in content
                        ParseRelation relationShowingWhoLikedThisPost = mItem.getParseObj().getRelation("likedPost");
                        relationShowingWhoLikedThisPost.add(ParseUser.getCurrentUser());

                        try {
                            mItem.getParseObj().save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        ParseRelation relationShowingWhatPostsIveLiked = ParseUser.getCurrentUser().getRelation("likedComments");
                        relationShowingWhatPostsIveLiked.add(mItem.getParseObj());
                        try {
                            ParseUser.getCurrentUser().save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        mItem.score++;
                        mItem.state = 1;

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

            try {
                tvName.setText(mItem.title + "");
                score.setText(mItem.getScore() + "");
                hub.setText(" " + mItem.getHubName());
                username.setText("@" + mItem.username + " ");
                replyNum.setText(mItem.replyNum + " COMMENTS");

                DateTime dateTime = new DateTime(mItem.date);

                date.setText(DateUtils.getRelativeTimeSpanString(dateTime.getMillis()));
            } catch (Exception e) {
            }


//            byte[] data;
//            try {
//                data = mItem.userProfPic.getData();
//                profPic.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
//
//            } catch (Exception e) {
//                profPic.setImageDrawable(context.getDrawable(R.drawable.profile_image));
//
//            }

            try {
                Picasso.with(context).load(mItem.url).into(image);

            } catch (Exception e) {
            }

        }

//        @Override
//        void bind(Object object) {
//            mItem = (Group) object;
//            String suffix = mItem.mExpanded ? " -" : " +";
//            mTextView.setText(mItem.mTitle + suffix);
//        }
    }

    class ViewHolderFooter extends ViewHolder {


        public ViewHolderFooter(View v) {
            super(v);

        }

        @Override
        void bind(Object object) {

        }
    }

    class ViewHolderEvent extends ViewHolder {
        FrameLayout layout;
        ImageButton upVote;
        ImageButton downVote;
        TextView tvName;
        TextView tvHome;
        TextView hub;
        TextView going;
        TextView username;
        TextView score;
        TextView replyNum;
        TextView date;
        ImageView profPic;
        ImageView image;
        Context context;
        Typeface type1;
        Typeface type2;
        TextView location;


        public ViewHolderEvent(View v, Context ctx) {
            super(v);

            layout = (FrameLayout) v.findViewById(R.id.imagess);
            going = (TextView) v.findViewById(R.id.going);
            //upVote = (ImageButton) v.findViewById(R.id.hand_up);
            //downVote = (ImageButton) v.findViewById(R.id.hand_down);
            tvName = (TextView) v.findViewById(R.id.titleEventMain);
            //tvHome = (TextView) v.findViewById(R.id.description);
            hub = (TextView) v.findViewById(R.id.hubName);
            username = (TextView) v.findViewById(R.id.username);
            score = (TextView) v.findViewById(R.id.score);
            //replyNum = (TextView) v.findViewById(R.id.commentNum);
            date = (TextView) v.findViewById(R.id.dateEventMain);
            image = (ImageView) v.findViewById(R.id.imagee);
            profPic = (ImageView) v.findViewById(R.id.profile_image);
            location = (TextView) v.findViewById(R.id.locationEventMain);
            context = ctx;

        }

        @Override
        void bind(Object object) {

            final EventItem mItem = (EventItem) object;


            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EventBus.getDefault().postSticky(mItem);
                    Intent intent = new Intent(context, EventDetailActivity.class);
                    context.startActivity(intent);
                }
            });

            going.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ParseRelation relation = ParseUser.getCurrentUser().getRelation("ChosenEvents");
                    relation.add(mItem.getParseObj());
                    try {
                        ParseUser.getCurrentUser().save();
                        going.setBackgroundColor(Color.parseColor("#FFEB3B"));
                        mItem.state = 1;
                        going.setPressed(true);
                        going.setEnabled(false);
                        mItem.getParseObj().pin("ChosenEvents");
                        going.setText("going");
                        ParseRelation relationGoing = mItem.getParseObj().getRelation("going");
                        relationGoing.add(ParseUser.getCurrentUser());
                        mItem.getParseObj().saveInBackground();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });

            if (mItem.state == 1) {
                going.setBackgroundColor(Color.parseColor("#FFEB3B"));
                going.setPressed(true);
                going.setText("Going");
                going.setEnabled(false);
            } else {
                going.setBackgroundColor(Color.parseColor("#00e676"));
                going.setPressed(false);
                going.setText("Go");
                going.setEnabled(true);

            }


            try {
                tvName.setText(mItem.title + "");
                location.setText(mItem.location + "");

                DateFormat format = new SimpleDateFormat("MM/dd/yyyy kk:mm", Locale.ENGLISH);

                date.setText(format.format(mItem.date));
            } catch (Exception e) {
            }


            try {
                Picasso.with(context).load(mItem.url).into(image);

            } catch (Exception e) {
            }


        }

    }

}