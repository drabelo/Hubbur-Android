package beta.drab.hubber.Activity.FeedModels;

import com.parse.ParseObject;

import java.util.Date;

/**
 * Created by dailtonrabelo on 2/26/15.
 */
public class MotherThread {

    public String title;
    public Date date;
    public int score;
    public int state = 0; //0 not set, 1 liked/going


    public String id;
    public ParseObject parseObj;

    public MotherThread() {

    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentItem)) return false;
        ContentItem that = (ContentItem) o;
        return id.equals(that.id);
    }

    public ParseObject getParseObj() {
        return parseObj;
    }

    public void setParseObj(ParseObject parseObj) {
        this.parseObj = parseObj;
    }


}
