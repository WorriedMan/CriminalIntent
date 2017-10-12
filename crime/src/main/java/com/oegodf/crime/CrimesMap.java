package com.oegodf.crime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by oegod on 14.09.2017.
 */

public class CrimesMap<T extends CrimeBase> extends HashMap<UUID, CrimeBase> {
    public ArrayList<T> getCrimesList(Class<T> tClass) {
        ArrayList<CrimeBase> list = new ArrayList<>(super.values());
        ArrayList<T> returnList = new ArrayList<>();
        for (CrimeBase aList : list) {
            returnList.add(tClass.cast(aList));
        }
        return returnList;
    }

    public CrimeBase getCrimeByPosition(int position) {
        for (Entry<UUID, CrimeBase> entry : this.entrySet()) {
            CrimeBase crime = entry.getValue();
            if (crime.getPosition() == position) {
                return crime;
            }
        }
        return null;
    }

    public int getNewCrimeId() {
        final Integer[] max = {0};
        for(Map.Entry<UUID, CrimeBase> entry : this.entrySet()) {
            CrimeBase crime = entry.getValue();
            if (crime.getPosition() > max[0]) {
                max[0] = crime.getPosition();
            }
        }
        return max[0]+1;
    }

    public List<CrimeBase> getSortedList() {
        List<CrimeBase> list = new ArrayList<>(this.values());
        Collections.sort(list, new Comparator<CrimeBase>() {
            @Override
            public int compare(CrimeBase o1, CrimeBase o2) {
                return o1.compareTo(o2);
            }
        });
        return list;
    }


}
