package beta.drab.hubber.Adapters;

/**
 * Created by dailtonrabelo on 2/19/15.
 */

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class Group {

    public ArrayList<Child> Items;
    int color;
    Drawable pic;
    private String Name;

    public Drawable getPic() {
        return pic;
    }

    public void setPic(Drawable pic) {
        this.pic = pic;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public ArrayList<Child> getItems() {
        return Items;
    }

    public void setItems(ArrayList<Child> Items) {
        this.Items = Items;
    }

    /**
     * Gets color.
     *
     * @return Value of color.
     */
    public int getColor() {
        return color;
    }

    /**
     * Sets new color.
     *
     * @param color New value of color.
     */
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Group group = (Group) o;

        if (!Name.equals(group.Name)) return false;

        return true;
    }


}
