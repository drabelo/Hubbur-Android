package beta.drab.hubber.Adapters;

/**
 * Created by dailtonrabelo on 2/19/15.
 */
public class Child {

    private String Name;

    public Child(String name) {
        Name = name;

    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Child child = (Child) o;

        if (!Name.equals(child.Name)) return false;

        return true;
    }


}
