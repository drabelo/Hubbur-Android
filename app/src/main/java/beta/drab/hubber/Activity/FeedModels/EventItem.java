package beta.drab.hubber.Activity.FeedModels;

import com.parse.ParseFile;

import java.util.Date;

/**
 * Created by dailtonrabelo on 2/26/15.
 */
public class EventItem extends MotherThread implements java.io.Serializable {

    public String title;
    public String description;
    public String hubName;
    public int score;
    public ParseFile userProfPic;
    public ParseFile image;
    public String url;
    public String username;
    public int replyNum;
    public Date date;
    public String location;

    public EventItem() {
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "EventItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", hubName='" + hubName + '\'' +
                ", score=" + score +
                ", username='" + username + '\'' +
                ", replyNum=" + replyNum +
                ", date=" + date +
                '}';
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHubName() {
        return hubName;
    }

    public void setHubName(String hubName) {
        this.hubName = hubName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(int replyNum) {
        this.replyNum = replyNum;
    }


}
