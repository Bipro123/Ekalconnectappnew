package com.connectapp.user.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.activity.GPSTutorialActivity;
import com.connectapp.user.data.Data;
import com.connectapp.user.data.PrefUtils;
import com.connectapp.user.data.Threads;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.font.RobotoTextView;
import com.connectapp.user.chatmodel.ChatContactList;
import com.connectapp.user.chatmodel.UserChatClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author raisahab.ritwik Utility class
 */
@SuppressWarnings("unused")
public class Util {

    private static String USERCLASS = "USERCLASS";
    private static String PREFUTILSCLASS = "PREFUTILSCLASS";
    private static String OFFLINEKEYWORDS = "OFFLINEKEYWORDS";
    private static String OFFLINEDATA = "OFFLINEDATA";
    private static String CHATCLASS = "CHATCLASS";
    private static String USERCHATCLASS = "USERCHATCLASS";
    private static String SELECTEDUSERCLASS = "SELECTEDUSERCLASS";
    private static String PERSONCOUNT = "PERSONCOUNT";

    private static int CONNECTIONTIMEOUT = 8000;
    private static StringBuilder sb = null;
    private static final String PREF_NAME = "FTSPrefs";
    private static final String PREF_PREFUTILS = "PrefUtilsPrefs";
    private static final String PREF_NAME_KEYWORDS = "KeywordsFTSPrefs";
    private static final String PREF_NAME_DATA = "DataFTSPrefs";
    private static final String CHAT_PREFS = "ChatPrefs";
    private static final String PREF_NAME_USER_CHAT = "UserChatPrefs";
    private static final String PREF_NAME_SELECTED_USER = "SelectedUserPrefs";


    /**
     * To check Internet Connection
     */
    public static boolean isInternetAvailable(Context context) {

        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conManager.getActiveNetworkInfo();
        if ((i == null) || (!i.isConnected()) || (!i.isAvailable())) {

            return false;
        }
        return true;
    }

    /**
     * Hide Soft Keyboard
     **/
    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }

    /**
     * Hide Soft Keyboard
     **/
    public static void showSoftKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        ;
    }

    /**
     * This method checks wether the provided email is valid or not.
     */
    public static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Shows custom alert dialog with OK option.
     **/
    public static void showMessageWithOk(final Activity mContext, final String message) {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            public void run() {
                final Dialog customDialog = new Dialog(mContext);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                        mContext.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(R.layout.dialog_universal_info, null);
                RobotoTextView dialog_universal_info_text = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_text);
                RobotoTextView dialog_universal_info_ok = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_ok);

                dialog_universal_info_text.setText(message);

                dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                    }
                });
                customDialog.setContentView(view);
                customDialog.setCancelable(false);
                customDialog.show();
				/*final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
				LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
				alert.setContentView(inflater.inflate(R.layout.dialog_universal_info, null));
				alert.setTitle(R.string.app_name);

				alert.setMessage(message);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

					}
				});
				alert.show();*/
            }
        });
    }

    /**
     * Shows custom alert dialog with OK option, success and NO Callback
     **/
    public static void showMessageWithOk_success(final Context mContext, final String message, final String title) {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            public void run() {
                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle(title);
                alert.setIcon(R.drawable.success);
                alert.setMessage(message);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
                alert.show();
            }
        });
    }

    /**
     * Shows custom alert dialog with OK option and a callback
     **/
    public static void showCallBackMessageWithOkCallback_success(final Context mContext, final String message,
                                                                 final AlertDialogCallBack callBack) {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            public void run() {

                final Dialog customDialog = new Dialog(mContext);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                        mContext.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(R.layout.dialog_universal_info, null);
                RobotoTextView dialog_universal_info_text = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_text);
                RobotoTextView dialog_universal_info_ok = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_ok);

                dialog_universal_info_text.setText(message);

                dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        callBack.onSubmit();
                    }
                });
                customDialog.setContentView(view);
                customDialog.setCancelable(false);
                customDialog.show();
            }
        });
    }

    /**
     * Shows custom alert dialog with OK and cancel
     **/
    public static void showCallBackMessageWithOkCancel(final Context mContext, final String message,
                                                       final AlertDialogCallBack callBack) {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            public void run() {

                final Dialog customDialog = new Dialog(mContext);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                        mContext.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(R.layout.dialog_universal_warning, null);
                RobotoTextView dialog_universal_warning_text = (RobotoTextView) view
                        .findViewById(R.id.dialog_universal_warning_text);
                RobotoTextView dialog_universal_warning_ok = (RobotoTextView) view.findViewById(R.id.dialog_universal_warning_ok);
                RobotoTextView dialog_universal_warning_cancel = (RobotoTextView) view
                        .findViewById(R.id.dialog_universal_warning_cancel);

                dialog_universal_warning_text.setText(message);

                dialog_universal_warning_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        callBack.onSubmit();

                    }
                });

                dialog_universal_warning_cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        callBack.onCancel();

                    }
                });
                customDialog.setContentView(view);
                customDialog.setCancelable(false);
                customDialog.show();
            }
        });
    }

    /**
     * Shows custom alert dialog with OK and cancel
     **/
    public static void showCallBackMessageWithOkCancelGPS(final Context mContext, final String message,
                                                          final AlertDialogCallBack callBack) {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            public void run() {

                final Dialog customDialog = new Dialog(mContext);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                        mContext.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(R.layout.dialog_universal_warning_gps, null);
                RobotoTextView dialog_universal_warning_text = (RobotoTextView) view
                        .findViewById(R.id.dialog_universal_warning_text);
                RobotoTextView dialog_universal_warning_ok = (RobotoTextView) view.findViewById(R.id.dialog_universal_warning_ok);
                RobotoTextView dialog_universal_warning_cancel = (RobotoTextView) view
                        .findViewById(R.id.dialog_universal_warning_cancel);
                ImageView dialog_help_gps = (ImageView) view.findViewById(R.id.dialog_help_gps);

                dialog_universal_warning_text.setText(message);

                dialog_universal_warning_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        callBack.onSubmit();

                    }
                });

                dialog_universal_warning_cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        callBack.onCancel();

                    }
                });

                dialog_help_gps.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mContext.startActivity(new Intent(mContext, GPSTutorialActivity.class));
                    }
                });

                customDialog.setContentView(view);
                customDialog.setCancelable(false);

                customDialog.show();
            }
        });
    }

    /**
     * Shows custom alert dialog with OK option and a callback
     **/
    public static void showCallBackMessageWithOkCallback(final Context mContext, final String message,
                                                         final AlertDialogCallBack callBack) {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            public void run() {

                final Dialog customDialog = new Dialog(mContext);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                        mContext.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(R.layout.dialog_universal_info, null);
                RobotoTextView dialog_universal_info_text = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_text);
                RobotoTextView dialog_universal_info_ok = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_ok);

                dialog_universal_info_text.setText(message);

                dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        callBack.onSubmit();
                        customDialog.dismiss();
                    }
                });
                customDialog.setContentView(view);
                customDialog.setCancelable(false);
                customDialog.show();

            }
        });
    }

    /**
     * Shows custom alert dialog with Success header, OK option and a callback
     **/
    public static void showCallBackMessageWithOkCallback_success(final Context mContext, final String message,
                                                                 final AlertDialogCallBack callBack, final String title) {

        ((Activity) mContext).runOnUiThread(new Runnable() {

            public void run() {

                final Dialog customDialog = new Dialog(mContext);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                        mContext.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(R.layout.dialog_universal_info, null);
                RobotoTextView dialog_universal_info_text = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_text);
                RobotoTextView dialog_universal_info_ok = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_ok);

                dialog_universal_info_text.setText(message);

                dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        callBack.onSubmit();
                    }
                });
                customDialog.setContentView(view);
                customDialog.setCancelable(false);
                customDialog.show();
            }
        });
    }

    /**
     * Dialog to update app or continue/skip
     **/
    public static void showMessageWithOkContinueUpdate(final Context mContext, final String message,
                                                       final AlertDialogCallBack callBack) {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            public void run() {

                final Dialog customDialog = new Dialog(mContext);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                        mContext.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(R.layout.dialog_update_app_continue, null);
                RobotoTextView dialog_universal_warning_text = (RobotoTextView) view
                        .findViewById(R.id.dialog_universal_warning_text);
                Button dialog_universal_warning_ok = (Button) view.findViewById(R.id.dialog_universal_warning_ok);
                Button dialog_universal_warning_cancel = (Button) view.findViewById(R.id.dialog_universal_warning_cancel);

                dialog_universal_warning_text.setText(message);

                dialog_universal_warning_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        callBack.onSubmit();

                    }
                });

                dialog_universal_warning_cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        customDialog.dismiss();
                        callBack.onCancel();

                    }
                });
                customDialog.setContentView(view);
                customDialog.setCancelable(false);
                customDialog.show();
            }
        });
    }

    /**
     * Shows custom alert dialog with OK option and a callback
     **/
    public static void showMessageWithOkUpdate(final Context mContext, final String message,
                                               final AlertDialogCallBack callBack) {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            public void run() {

                final Dialog customDialog = new Dialog(mContext);
                customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                        mContext.LAYOUT_INFLATER_SERVICE);

                View view = layoutInflater.inflate(R.layout.dialog_update_app, null);
                RobotoTextView dialog_universal_info_text = (RobotoTextView) view.findViewById(R.id.dialog_universal_warning_text);
                Button dialog_universal_info_ok = (Button) view.findViewById(R.id.dialog_universal_warning_ok);


                dialog_universal_info_text.setText(message);

                dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        callBack.onSubmit();
                        customDialog.dismiss();
                    }
                });
                customDialog.setContentView(view);
                customDialog.setCancelable(false);
                customDialog.show();
            }
        });
    }

    /**
     * Saving UserClass details
     **/
    public static void saveUserClass(final Context mContext, UserClass userClass) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
        try {
            prefsEditor.putString(USERCLASS, ObjectSerializer.serialize(userClass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefsEditor.commit();
    }

    /**
     * Fetching UserClass details
     **/
    public static UserClass fetchUserClass(final Context mContext) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        UserClass userClass = null;
        String serializeOrg = LastMilePrefs.getString(USERCLASS, null);
        try {
            if (serializeOrg != null) {
                userClass = (UserClass) ObjectSerializer.deserialize(serializeOrg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return userClass;
    }

    /**
     * Saving UserChatClass details
     **/
    public static void saveUserChatClass(final Context mContext, UserChatClass userClass) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_USER_CHAT, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
        try {
            prefsEditor.putString(USERCHATCLASS, ObjectSerializer.serialize(userClass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefsEditor.commit();
    }

    /**
     * Fetching UserChatClass details
     **/
    public static UserChatClass fetchUserChatClass(final Context mContext) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_USER_CHAT, Context.MODE_PRIVATE);
        UserChatClass userClass = null;
        String serializeOrg = LastMilePrefs.getString(USERCHATCLASS, null);
        try {
            if (serializeOrg != null) {
                userClass = (UserChatClass) ObjectSerializer.deserialize(serializeOrg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return userClass;
    }


    /**
     * Saving PrefUtilsClass details
     **/
    public static void savePrefUtilClass(final Context mContext, PrefUtils prefUtilsClass) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_PREFUTILS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
        try {
            prefsEditor.putString(PREFUTILSCLASS, ObjectSerializer.serialize(prefUtilsClass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefsEditor.commit();
    }

    /**
     * Fetching PrefUtilsClass details
     **/
    public static PrefUtils fetchPrefUtilClass(final Context mContext) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_PREFUTILS, Context.MODE_PRIVATE);
        PrefUtils prefUtilsClass = null;
        String serializeOrg = LastMilePrefs.getString(PREFUTILSCLASS, null);
        try {
            if (serializeOrg != null) {
                prefUtilsClass = (PrefUtils) ObjectSerializer.deserialize(serializeOrg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return prefUtilsClass;
    }

    /**
     * Saving Selected UserClass details
     **/
    public static void saveSelectedUserClass(final Context mContext, UserClass userClass) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_SELECTED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
        try {
            prefsEditor.putString(SELECTEDUSERCLASS, ObjectSerializer.serialize(userClass));
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefsEditor.commit();
    }

    /**
     * Fetching Selected UserClass details
     **/
    public static UserClass fetchSelectedUserClass(final Context mContext) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_SELECTED_USER, Context.MODE_PRIVATE);
        UserClass userClass = null;
        String serializeOrg = LastMilePrefs.getString(SELECTEDUSERCLASS, null);
        try {
            if (serializeOrg != null) {
                userClass = (UserClass) ObjectSerializer.deserialize(serializeOrg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return userClass;
    }

    /**
     * Saving threads
     **/
    public static void saveOfflineKeywordsThreads(final Context mContext, Threads threads) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_KEYWORDS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
        try {
            prefsEditor.putString(OFFLINEKEYWORDS, ObjectSerializer.serialize(threads));
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefsEditor.commit();
    }

    /**
     * Fetching threads
     **/
    public static Threads fetchOfflineKeywordsThreads(final Context mContext) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_KEYWORDS, Context.MODE_PRIVATE);
        Threads thread = null;
        String serializeOrg = LastMilePrefs.getString(OFFLINEKEYWORDS, null);
        try {
            if (serializeOrg != null) {
                thread = (Threads) ObjectSerializer.deserialize(serializeOrg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return thread;
    }

    /**
     * Saving Data details
     **/
    public static void saveOfflineData(final Context mContext, Data data) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
        try {
            prefsEditor.putString(OFFLINEDATA, ObjectSerializer.serialize(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefsEditor.commit();
    }

    /**
     * Fetching Keywords details
     **/
    public static Data fetchOfflineData(final Context mContext) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME_DATA, Context.MODE_PRIVATE);
        Data data = null;
        String serializeOrg = LastMilePrefs.getString(OFFLINEDATA, null);
        try {
            if (serializeOrg != null) {
                data = (Data) ObjectSerializer.deserialize(serializeOrg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Saving personCount
     **/
    public static void savePersonCount(final Context mContext, int count) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = LastMilePrefs.edit();
        try {
            prefsEditor.putString(PERSONCOUNT, ObjectSerializer.serialize(count));
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefsEditor.commit();
    }

    /**
     * Fetching Person count
     **/
    public static int fetchPersonCount(final Context mContext) {
        SharedPreferences LastMilePrefs = mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int personCount = 0;
        String serializeOrg = LastMilePrefs.getString(PERSONCOUNT, null);
        try {
            if (serializeOrg != null) {
                personCount = (int) ObjectSerializer.deserialize(serializeOrg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return personCount;
    }

    /**
     * Get the current date
     **/
    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return date;
    }

    /**
     * Get the current Time
     **/
    @SuppressLint("SimpleDateFormat")
    public static String getTime() {
        String date = new SimpleDateFormat("HH:mm:ss").format(new Date());
        return date;
    }

    /**
     * show global alert message
     *
     * @param context   application context
     * @param title     alert title
     * @param btn_title alert click button name
     * @param msg       alert message
     */
    public static void alertMessage(Context context, String title, String btn_title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg).setCancelable(false).setPositiveButton(btn_title, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Show log
     *
     * @param type       of log 0 for info,1 error,2 for verbosa
     * @param logtitle
     * @param logcontent
     * @return
     */
    public static void printLog(int type, String logtitle, String logcontent) {

        switch (type) {
            case 0:
                Log.e(logtitle, logcontent + "");
                break;
            case 1:
                Log.e(logtitle, logcontent + "");
                break;
            case 2:
                Log.e(logtitle, logcontent + "");
                break;

            default:
                Log.e(logtitle, logcontent + "");
                break;
        }
    }

    //	public static String postMethodWay_json(String hostName, String data) throws ClientProtocolException, IOException {
    //
    //		sb = new StringBuilder();
    //		HttpClient client = new DefaultHttpClient();
    //		HttpConnectionParams.setConnectionTimeout(client.getParams(), CONNECTIONTIMEOUT);
    //		HttpConnectionParams.setSoTimeout(client.getParams(), CONNECTIONTIMEOUT);
    //		HttpResponse response;
    //
    //		try {
    //			HttpPost post = new HttpPost(hostName);
    //
    //			StringEntity se = new StringEntity(data);
    //			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
    //			post.setEntity(se);
    //			response = client.execute(post);
    //			int statuscode = response.getStatusLine().getStatusCode();
    //
    //			/* Checking response */
    //			if (statuscode == 200) {
    //
    //				if (response != null) {
    //					InputStream in = response.getEntity().getContent();
    //					// InputStream in = /* your InputStream */;
    //					InputStreamReader is = new InputStreamReader(in);
    //
    //					BufferedReader br = new BufferedReader(is);
    //					String read = br.readLine();
    //
    //					while (read != null) {
    //						// System.out.println(read);
    //						sb.append(read);
    //						read = br.readLine();
    //
    //					}
    //					// the entity
    //					Log.i("tAG", "" + sb.toString());
    //					return sb.toString();
    //				}
    //			} else {
    //				// the entity
    //				System.out.println("Response code is:: " + response.getStatusLine().getStatusCode());
    //				sb.append(statuscode);
    //				return sb.toString();
    //			}
    //
    //		} catch (Exception e) {
    //			e.printStackTrace();
    //			sb.append(e.getLocalizedMessage());
    //			Log.i("connection", "Cannot Estabilish Connection! " + e.getLocalizedMessage());
    //		}
    //
    //		return sb.toString();
    //	}

    /**
     * Get base64 string from bitmap
     */
    public static String getBase64StringFromBitmap(Bitmap mBitmap) {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte[] ba = bao.toByteArray();
        return (Base64.encodeToString(ba, Base64.DEFAULT));
    }

    /**
     * Get bitmap from base64 string
     */
    public static Bitmap getBitmapBase64FromString(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return (decodedByte);
    }

    /**
     * Shows a system dialog for GPS settings.
     */
    public static AlertDialog showSettingsAlert(final Context applicationContext, AlertDialog systemAlertDialog) {
        Log.e("showSettingsAlert()", "true");

        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
        builder.setTitle("GPS Disabled");
        builder.setIcon(R.drawable.warning);
        builder.setCancelable(false);
        builder.setMessage("Your GPS seems to be disabled, tap Ok to enable it?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // mContext.startActivity(new
                // Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                viewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // <-- Newly
                // added
                // line
                applicationContext.startActivity(viewIntent);
                dialog.dismiss();
            }
        });
        //		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
        //			public void onClick(DialogInterface dialog, int whichButton) {
        //				dialog.dismiss();
        //			}
        //		});
        systemAlertDialog = builder.create();
        /*WindowManager windowManager = (WindowManager)applicationContext.getSystemService(applicationContext.WINDOW_SERVICE);
        //here is all the science of params
        final WindowManager.LayoutParams myParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                PixelFormat.TRANSLUCENT
        );*/

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_TOAST ;
        }
        systemAlertDialog.getWindow().setType(LAYOUT_FLAG);
        systemAlertDialog.show();
        return systemAlertDialog;
    }

    /**
     * This method parses the local json from the assets folder
     *
     * @throws JSONException
     */
    public static JSONObject loadJSONFromAsset(Context mContext, String jsonFileName) throws JSONException {

        String json = null;
        try {
            InputStream is = mContext.getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return new JSONObject(json);
    }

    public static void initToast(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    public static String local(String latitudeFinal, String longitudeFinal) {
        return "https://maps.googleapis.com/maps/api/staticmap?center=" + latitudeFinal + "," + longitudeFinal + "&zoom=18&size=280x280&markers=color:red|" + latitudeFinal + "," + longitudeFinal;
    }


    //===================== CHAT OFFLINE DATA =================================

    /**
     * Saving Chat Contact details
     **/
    public static void saveStudentList(final Context mContext, ChatContactList students) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(CHAT_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        try {
            prefsEditor.putString(CHATCLASS, ObjectSerializer.serialize(students));
        } catch (IOException e) {
            e.printStackTrace();
        }
        prefsEditor.commit();
    }

    /**
     * Fetching Chat Contact LIst details
     **/
    public static ChatContactList fetchStudentList(final Context mContext) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(CHAT_PREFS, Context.MODE_PRIVATE);
        ChatContactList chatContactList = null;
        String serializeOrg = sharedPreferences.getString(CHATCLASS, null);
        try {
            if (serializeOrg != null) {
                chatContactList = (ChatContactList) ObjectSerializer.deserialize(serializeOrg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return chatContactList;
    }

    /**
     * Open permission page in setting
     */
    public static void grantPermissionRedirect(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }
}
