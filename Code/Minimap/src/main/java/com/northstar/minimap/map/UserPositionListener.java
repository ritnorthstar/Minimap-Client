package com.northstar.minimap.map;

import com.northstar.minimap.Position;

/**
 * Created by Chris on 4/11/14.
 */
public interface UserPositionListener {
    public void onUserAzimuthChanged(double azimuth);
    public void onUserPositionChanged(Position userPosition, double positionError);
}
