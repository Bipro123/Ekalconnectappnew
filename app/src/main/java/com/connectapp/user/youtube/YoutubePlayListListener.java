package com.connectapp.user.youtube;

/**
 * @author raisahab.ritwik
 **/
import java.util.ArrayList;

import com.connectapp.user.data.Video;


/** Interface that provides call-back on loading of you-tube play-list */
public interface YoutubePlayListListener {

	/** Returns call back of the Video play-list on this method */
	void videoPlaylistAsyncCallback(ArrayList<Video> result);

}
