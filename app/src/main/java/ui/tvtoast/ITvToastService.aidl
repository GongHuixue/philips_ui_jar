package ui;

import ui.TvToast;

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