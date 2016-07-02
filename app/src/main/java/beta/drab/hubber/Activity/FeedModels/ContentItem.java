package beta.drab.hubber.Activity.FeedModels;

import com.parse.ParseFile;

/**
 * Created by dailtonrabelo on 2/26/15.
 */
public class ContentItem extends MotherThread implements java.io.Serializable {

    public String title;


    public String description;
    public String hubName;
    public ParseFile userProfPic;
    public String username;
    public int replyNum;
    public ParseFile image;
    public String url;

    @Override
    public String toString() {
        return "ContentItem{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", hubName='" + hubName + '\'' +
                ", userProfPic=" + userProfPic +
                ", username='" + username + '\'' +
                ", replyNum=" + replyNum +
                ", image=" + image +
                ", url='" + url + '\'' +
                ", state=" + state +
                '}';
    }

    public ContentItem() {

        image = null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (hubName != null ? hubName.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getHubName() {
        return this.hubName;
    }

    public void setHubName(String name) {
        this.hubName = name;
    }

    /**
     * Gets title.
     *
     * @return Value of title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets new title.
     *
     * @param title New value of title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets description.
     *
     * @return Value of description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets new description.
     *
     * @param description New value of description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets gname.
     *
     * @return Value of username.
     */


    /**
     * Sets new username.
     *
     * @param username New value of username.
     */
}
