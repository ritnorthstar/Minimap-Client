//John Paul Mardelli
//Last updated October 31st, 2013

package com.northstar.minimap.beacon;

import com.google.android.gms.maps.model.Circle;
import com.northstar.minimap.Position;

/**
* Interface that all implementations of beacons will use, regardless of technology. Used so our
* application can use any distance technology as long as there's a concrete implementation.
*/
public interface IBeacon {

    public int getNumber();

    /**
     * Method to obtain the ID of a beacon.
     * @return A string representing a beacon's unique ID.
     */
    public String getId();

    /**
     * Method to get the position of the beacon
     * @return The beacons position
     */
    public Position getPosition();

    /**
     * Method to obtain the signal strength of a beacon.
     * @return A double representing the beacons signal strength.
     */
    public int getSignalStrength();

    /**
     * Method to compute distance based on the necessary method for the
     * given technology
     * @return The distance from the beacon, in (don't know the measurement).
     */
    public Double computeDistance();
    public Double computeUnaveragedDistance();

    public String getFormattedDistance();
    public Circle getMarkerCircle();
    public Circle getRangeCircle();

    public boolean isInProximityZone();

    public void setInProximityZone(boolean isInProximityZone);
    public void setMarkerCircle(Circle markerCircle);
    public void setRangeCircle(Circle rangeCircle);
    public void setSignalStrength(int signalStrength);
}