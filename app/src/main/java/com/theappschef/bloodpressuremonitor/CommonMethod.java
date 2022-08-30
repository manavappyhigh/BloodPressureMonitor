package com.theappschef.bloodpressuremonitor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.theappschef.bloodpressuremonitor.activity.SplashActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class CommonMethod {


    public static boolean isNetworkConnected(Context activtiy) {
        // TODO Auto-generated method s
        ConnectivityManager cmss = (ConnectivityManager) activtiy
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo niss = cmss.getActiveNetworkInfo();
        if (niss == null) {
            // There are no active networks.
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checking device has camera hardware or not
     */
    public static boolean isDeviceSupportCamera(Activity act) {
        // if this device has a camera
        return act.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    /**
     * Method is used to get file size in KB.
     *
     * @param file
     */
    public static int getFileSize(File file) {

        int fileSize = 0;
        try {
            fileSize = Integer.parseInt(String.valueOf(file.length() / 1024)); // file size in KB
            Log.e("fileSize", "" + fileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileSize;
    }

    public static void displayNetworkImage(Activity activity, String strUrl, ImageView imageView) {

       /* RequestOptions options = new RequestOptions();
        options.diskCacheStrategy(DiskCacheStrategy.ALL);
        options.fitCenter();

        //Loading Image from URL
        try {
            Glide
                    .with(TentCityApplication.getAppContext())
                    .load(strUrl)
                    .apply(options)
                    .error(ContextCompat.getDrawable(activity, R.drawable.error_downloading_icon))
                    .placeholder(ContextCompat.getDrawable(activity, R.drawable.loading_transparent))
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        try {
            Glide.with(activity)
                    .load(strUrl)
                    .apply(options)
                    .thumbnail(0.1f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * String to BitMap
     *
     * @param encodedString
     * @return
     */
    public static Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
    public static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order) {
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        }
        return manufacturer.toUpperCase() + " " + model;
    }

    @SuppressLint("NewApi")
    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shareApplication(Activity activity) {
        try {
            int applicationNameId = activity.getApplicationInfo().labelRes;
            final String appPackageName = activity.getPackageName();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, activity.getString(applicationNameId));

            String link = "https://play.google.com/store/apps/details?id=" + appPackageName + "&hl=en";
            i.putExtra(Intent.EXTRA_TEXT, String.format(activity.getString(R.string.application_share),
                    activity.getString(R.string.app_name))
                    + " " + link);
            activity.startActivity(Intent.createChooser(i, "Share link:"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void rateApplication(Activity activity) {
        final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

//    public static void moreAppsFromCompany(Activity activity) {
//        try {
//            activity.startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse(activity.getString(R.string.companies_more_application))));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void showAlertForChangeDate(final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setTitle(activity.getString(R.string.title_alert_date));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder
                .setMessage(
                        activity.getString(R.string.message_alert_change_date))
                .setCancelable(true)
                .setPositiveButton(activity.getString(R.string.action_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static boolean isAutoDateTimeEnabled(Context context) {
        try {

            String timeSettings = "";
            if (Build.VERSION.SDK_INT > 17) {
                timeSettings = android.provider.Settings.System.getString(
                        context.getContentResolver(),
                        android.provider.Settings.System.AUTO_TIME);

                if (timeSettings.contentEquals("0")) {
//					android.provider.Settings.Global.putString(
//							context.getContentResolver(),
//							android.provider.Settings.Global.AUTO_TIME, "1");
                    return false;
                } else {
                    return true;
                }
            }
            // SDK < 16
            else {
                timeSettings = android.provider.Settings.System.getInt(
                        context.getContentResolver(),
                        android.provider.Settings.System.AUTO_TIME) + "";

                if (timeSettings.contentEquals("0")) {
//					android.provider.Settings.System.putInt(
//							context.getContentResolver(),
//							android.provider.Settings.System.AUTO_TIME, 1);
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void showAlertForLogout(final Activity activity, final FirebaseAuth mAuth) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(activity.getString(R.string.title_alert_confirm_logout));
        alertDialogBuilder.setMessage(activity.getString(R.string.message_alert_confirm_logout))
                .setCancelable(true)
                .setPositiveButton(activity.getString(R.string.action_yes),
                        (dialog, id) -> {

                            if (mAuth != null)
                                mAuth.signOut();

//                            SharePreferences.clearSP();

//                            Intent intentLoginActivity = new Intent(activity, FirebaseAuthenticationActivity.class);
//                            activity.startActivity(intentLoginActivity);
                            activity.finish();
                        })
                .setNegativeButton(activity.getString(R.string.action_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public static void showAlertWithOk(final Activity activity, String strTitle,
                                       String strMessage,String strBtn) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(strTitle);
        alertDialogBuilder.setMessage(strMessage)
                .setCancelable(true)
                .setPositiveButton(strBtn,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    public static List<String> getMonthList() {
        // String[] tempMonthListForYear = new DateFormatSymbols().getShortMonths();
        String[] tempMonthListForYear = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        List<String> arrayList = new ArrayList<>();
        Collections.addAll(arrayList, tempMonthListForYear);

        return arrayList.subList(0, Calendar.getInstance().get(Calendar.MONTH) + 2);
    }

    public static String formatPrice(double value) {
        /*DecimalFormat formatter;
        if (value <= 99999)
            formatter = new DecimalFormat("###,###,##0.00");
        else
            formatter = new DecimalFormat("#,##,##,###.00");*/

        //DecimalFormat formatter = new DecimalFormat("#,##,##,###");
        return Constant.decimalFormat.format(value);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    public static void showConnectionAlert(final Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle(context.getString(R.string.title_alert_connection));
        alertDialogBuilder.setMessage(context.getString(R.string.message_alert_connection))
                .setPositiveButton(context.getResources().getString(R.string.action_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public static void showDialogForFileStorePath(final Activity activity, String text,String path,
                                                  String positiveButton,String negativeButton) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setTitle("Data downloaded success");
        alertDialogBuilder.setMessage(text)
                .setPositiveButton(positiveButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(negativeButton,
                        (dialog, id) -> {
                            dialog.cancel();
                            openFile(activity, path);
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
    static void openFile(Activity activity, String path) {
        try {
            Uri uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", new File(path));

            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (path.contains(".doc") || path.contains(".docx")) {
                // Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (path.contains(".pdf")) {
                // PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (path.contains(".ppt") || path.contains(".pptx")) {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (path.contains(".xls") || path.contains(".xlsx")) {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (path.contains(".zip")) {
                // ZIP file
                intent.setDataAndType(uri, "application/zip");
            } else if (path.contains(".rar")){
                // RAR file
                intent.setDataAndType(uri, "application/x-rar-compressed");
            } else if (path.contains(".rtf")) {
                // RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (path.contains(".wav") || path.contains(".mp3")) {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (path.contains(".gif")) {
                // GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (path.contains(".jpg") || path.contains(".jpeg") || path.contains(".png")) {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (path.contains(".txt")) {
                // Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (path.contains(".3gp") || path.contains(".mpg") ||
                    path.contains(".mpeg") || path.contains(".mpe") || path.contains(".mp4") || path.contains(".avi")) {
                // Video files
                intent.setDataAndType(uri, "video/*");
            } else {
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No application found which can open the file", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showConnectionAlertAndRetry(final Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        alertDialogBuilder.setTitle(context.getString(R.string.title_alert_connection));
        alertDialogBuilder.setMessage(context.getString(R.string.message_alert_connection))
                .setPositiveButton(context.getResources().getString(R.string.action_retry),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (context != null) {
                                    if (context instanceof SplashActivity) {
//                                        ((SplashActivity) context).reloadFirebase();
                                    }
                                }
                            }
                        })
                .setNegativeButton(context.getResources().getString(R.string.action_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (context != null) {
                                    if (context instanceof SplashActivity) {
//                                        ((SplashActivity) context).destroyActivity();
                                    }
                                }
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }


    public static void showApplicationExitDialog(final Activity activity) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);
        alertDialogBuilder.setTitle("Exit");
        alertDialogBuilder.setMessage("Are you sure wants to exit from application ?")
                .setPositiveButton(Html.fromHtml("<font color='#15D5CD'>Yes</font>"),
                        (dialog, id) -> {
                            dialog.cancel();
                            if (activity != null) {
                                activity.finish();
                            }
                        })
                .setNegativeButton(Html.fromHtml("<font color='#15D5CD'>No</font>"),
                        (dialog, id) -> dialog.cancel())
                .setNeutralButton("RATE US",
                        (dialog, id) -> {
                            dialog.cancel();
                            if (activity != null) {
                                rateApplication(activity);
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    public static Dialog mDialog;
    public static void cancelProgressDialog() {
        if (mDialog != null
                && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

//    public static void showProgressDialog(Activity activity) {
//        if (activity != null
//                && mDialog == null) {
//            mDialog = new Dialog(activity);
//            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            mDialog.setCancelable(false);
//            mDialog.setCanceledOnTouchOutside(false);
//            mDialog.setContentView(R.layout.dialog);
//
//            mDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(activity, android.R.color.transparent));
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            lp.copyFrom(mDialog.getWindow().getAttributes());
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//
//            mDialog.getWindow().setAttributes(lp);
//            mDialog.show();
//        }
//    }

    public static void showProgressDialogForDownloading(Activity activity) {
        if (activity != null
                && mDialog == null) {
            mDialog = new Dialog(activity);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
//            mDialog.setContentView(R.layout.dialog_download);

            mDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(activity, android.R.color.transparent));
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(mDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;

            mDialog.getWindow().setAttributes(lp);
            mDialog.show();
        }
    }


    public static enum SubDirectory {
        APP_PDF_DATA("PDF"),APP_XLS_DATA("XLS"), APP_LOG_DIRECTORY("Log"),
        APP_DOWNLOAD_IMAGE("Download");
        private final String subDirectoryName;

        private SubDirectory(String subDirectoryName) {
            this.subDirectoryName = subDirectoryName;
        }

        @Override
        public String toString() {
            return subDirectoryName;
        }
    }


    public static File getApplicationDirectory(SubDirectory subFolderName,
                                               Context activity, boolean isPublic) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File internalDownloadDirectory = new File(getMountedPaths(), activity.getResources().getString(R.string.app_name));
            if (!internalDownloadDirectory.exists()) {
                internalDownloadDirectory.mkdirs();
            }
            internalDownloadDirectory.setReadable(true);
            internalDownloadDirectory.setWritable(true);
            return internalDownloadDirectory;
        } else {
            File directory;
            if (isPublic) {
                directory = new File(Environment.getExternalStorageDirectory()
                        + "/"
                        + activity.getResources().getString(R.string.app_name));
                /*directory = new File(activity.getExternalFilesDir(null)
                        + "/"
                        + activity.getResources().getString(R.string.app_name));*/
            } else {
                directory = activity.getFilesDir();
            }
            if (directory == null
                    || (!directory.exists() && !directory.mkdirs())) {
                return null;
            }
            if (subFolderName != null) {
                directory = new File(directory, subFolderName.toString());
                if (directory == null
                        || (!directory.exists() && !directory.mkdirs())) {
                    return null;
                }
            }
            return directory;
        }
    }


    private static String getMountedPaths() {
        String sdcardPath = "";
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = runtime.exec("mount");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        InputStream is = proc.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        String line;
        BufferedReader br = new BufferedReader(isr);
        try {
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat")) {// TF card
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        sdcardPath = columns[1];
                    }
                } else if (line.contains("fuse")) {// internal storage
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        sdcardPath = columns[1];
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sdcardPath;
    }


    public static void startSound(Activity activity,String filename) {
        AssetFileDescriptor afd = null;
        try {
            afd = activity.getResources().getAssets().openFd(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaPlayer player = new MediaPlayer();
        try {
            assert afd != null;
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.setVolume(1f, 1f);
        player.setLooping(false);
        player.start();
    }


    /**
     * get all the files in specified directory
     *
     * @param parentDir
     * @return
     */
    public static  ArrayList<File> getImagesListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files;
        files = parentDir.listFiles();
        if (files != null) {
            Arrays.sort(files, new Comparator() {
                public int compare(Object o1, Object o2) {

                    if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                        return -1;
                    } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                        return +1;
                    } else {
                        return 0;
                    }
                }

            });

            for (File file : files) {

                if (file.getName().endsWith(".png")) {
                    if (!inFiles.contains(file))
                        inFiles.add(file);
                }else if (file.getName().endsWith(".pdf")) {
                    if (!inFiles.contains(file))
                        inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    public  static void ShareImageFile(Activity activity,File file){
        try {
            Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
            Intent intent = ShareCompat.IntentBuilder.from(activity)
                    .setType("image/jpg")
                    .setStream(uri)
                    .createChooserIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  static  void viewImage(Activity activity,File currentFile){
        Uri VideoURI = FileProvider.getUriForFile(activity,
                activity.getApplicationContext().getPackageName() + ".provider", currentFile);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(VideoURI, "image/*");
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public  static  void viewPDF(Activity activity,File currentFile){
        Uri VideoURI = FileProvider.getUriForFile(activity,
                activity.getApplicationContext().getPackageName() + ".provider", currentFile);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(VideoURI, "application/pdf");
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Method is used to store particular month data into XLS file
     * into SD card
     */


    /**
     * Get decimal formated string to include comma seperator to decimal number
     *
     * @param value
     * @return
     */
    public static String getDecimalFormattedString(String value) {
        if (value != null && !value.equalsIgnoreCase("")) {
            StringTokenizer lst = new StringTokenizer(value, ".");
            String str1 = value;
            String str2 = "";
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken();
                str2 = lst.nextToken();
            }
            String str3 = "";
            int i = 0;
            int j = -1 + str1.length();
            if (str1.charAt(-1 + str1.length()) == '.') {
                j--;
                str3 = ".";
            }
            for (int k = j; ; k--) {
                if (k < 0) {
                    if (str2.length() > 0)
                        str3 = str3 + "." + str2;
                    return str3;
                }
                if (i == 3) {
                    str3 = "," + str3;
                    i = 0;
                }
                str3 = str1.charAt(k) + str3;
                i++;
            }
        }
        return "";
    }



    /**
     * Restrict digits after decimal point value as per currency
     */
    public static class MoneyValueFilter extends DigitsKeyListener {
        private int digits;

        public MoneyValueFilter(int i) {
            super(false, true);
            digits = i;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            CharSequence out = super.filter(source, start, end, dest, dstart, dend);

            // if changed, replace the source
            if (out != null) {
                source = out;
                start = 0;
                end = out.length();
            }

            int len = end - start;

            // if deleting, source is empty
            // and deleting can't break anything
            if (len == 0) {
                return source;
            }

            int dlen = dest.length();

            // Find the position of the decimal .
            for (int i = 0; i < dstart; i++) {
                if (dest.charAt(i) == '.') {
                    // being here means, that a number has
                    // been inserted after the dot
                    // check if the amount of digits is right
                    return getDecimalFormattedString((dlen - (i + 1) + len > digits) ? "" : String.valueOf(new SpannableStringBuilder(source, start, end)));
                }
            }

            for (int i = start; i < end; ++i) {
                if (source.charAt(i) == '.') {
                    // being here means, dot has been inserted
                    // check if the amount of digits is right
                    if ((dlen - dend) + (end - (i + 1)) > digits)
                        return "";
                    else
                        break; // return new SpannableStringBuilder(source,
                    // start, end);
                }
            }

            // if the dot is after the inserted part,
            // nothing can break
            return getDecimalFormattedString(String.valueOf(new SpannableStringBuilder(source, start, end)));
        }
    }
}
