//John Paul Mardelli
//Last updated October 31st, 2013

package com.northstar.minimap.beacon;

import com.northstar.minimap.map.Position;

/**
 * Interface that all implementations of beacons will use, regardless of technology. Used so our
 * application can use any distance technology as long as there's a concrete implementation.
 */
public interface IBeacon {

    /**
     * Method to obtain the ID of a beacon.
     * @return A string representing a beacon's unique ID.
     */
    public String getID();

    /**
     * Method to get the position of the beacon
     * @return The beacons position
     */
    public Position getPos();

}
