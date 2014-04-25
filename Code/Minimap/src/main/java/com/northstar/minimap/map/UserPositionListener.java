package com.northstar.minimap.map;

import com.northstar.minimap.Position;

public interface UserPositionListener {
    public void onUserPositionChanged(Position userPosition, double positionError);
}
