package org.AF.TableUtil.GroupAll;

import java.util.HashMap;
import java.util.List;


public class GroupingMapper {
	private HashMap<List<Object>, int[]> groupingCounter;
	
    public GroupingMapper() {
        this.groupingCounter = new HashMap<>();
    }	

    public void add(List<Object> groupKey, int addVal) {
        // an empty list has to be added for a new user if one has not already been added
    	
        this.groupingCounter.putIfAbsent(groupKey, new int[]{0, 0});

        
        int[] vals = this.groupingCounter.get(groupKey);
        vals[0]++;
        vals[1]= vals[1]+addVal;
    
    }
    
    
    public HashMap<List<Object>, int[]> getData()
    {
    	return this.groupingCounter;
    }
}




