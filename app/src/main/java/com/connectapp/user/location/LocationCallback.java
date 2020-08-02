package com.connectapp.user.location;

import android.location.Location;

public interface LocationCallback {

	void getLocation(Location mLocation,boolean isSuccess);
}
