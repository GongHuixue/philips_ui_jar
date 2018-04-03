// ITvToastService.aidl
package ui.tvtoast;

import ui.tvtoast.TvToast;

// Declare any non-default types here with import statements

interface ITvToastService {
	/**
	* show Tv Toast
	*/
	void showTvToastMessage(IBinder contextToken, in TvToast msg);

	/**
	* cancel Tv Toast
	*/
	void cancelTvToastMessage(IBinder contextToken, in TvToast msg);
}
