package com.example.oegod.criminalintent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by oegod on 14.09.2017.
 */

public class CrimesMap extends HashMap<UUID, Crime> {
    public Crime getCrimeByPosition(int position) {
        for (Map.Entry<UUID, Crime> entry : this.entrySet()) {
            Crime crime = entry.getValue();
            if (crime.getPosition() == position) {
                return crime;
            }
        }
        return null;
    }
}
