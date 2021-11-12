package App.GraphUtility;

import java.util.ArrayList;

public class SimpleFilterGroup implements FilterGroup {

    private final String name;
    private final ArrayList<Filter> filters;

    public SimpleFilterGroup(String name, ArrayList<Filter> filters){
        this.name = name;
        this.filters = filters;

    }

    public ArrayList<Filter> getFilters() {
        return filters;
    }

    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        return "{" + name +
                "," + filters +
                '}';
    }
}
