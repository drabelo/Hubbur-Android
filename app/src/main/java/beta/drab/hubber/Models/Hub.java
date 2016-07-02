package beta.drab.hubber.Models;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * Created by dailtonrabelo on 2/16/15.
 */
public class Hub {

    public String name;
    public Drawable picName;
    ArrayList<String> subHubs = new ArrayList<String>();

    public Hub(String name, Drawable drawable) {
        this.name = name;
        this.picName = drawable;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getPicName() {
        return picName;
    }

    public void setPicName(Drawable picName) {
        this.picName = picName;
    }

    public String toString() {
        return name;
    }


}
