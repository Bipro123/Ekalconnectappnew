package com.connectapp.user.application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

import com.connectapp.user.R;

@ReportsCrashes(formKey = "", mailTo = "connectapp.net@gmail.com;ritwikrai04@gmail.com;raixellos480@gmail.com", customReportContent = {
		ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
		ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT }, forceCloseDialogAfterToast = false, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast)
public class ConnectAppApplication extends Application {
	public static ConnectAppApplication mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
		//ACRA.init(this);
	}
}
