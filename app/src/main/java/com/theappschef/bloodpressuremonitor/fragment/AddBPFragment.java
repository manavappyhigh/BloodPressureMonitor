package com.theappschef.bloodpressuremonitor.fragment;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

import android.animation.Animator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theappschef.bloodpressuremonitor.CommonMethod;
import com.theappschef.bloodpressuremonitor.Constant;
import com.theappschef.bloodpressuremonitor.Expense;
import com.theappschef.bloodpressuremonitor.R;
import com.theappschef.bloodpressuremonitor.activity.MainActivity;
import com.theappschef.bloodpressuremonitor.asyncTask.ImageCompression;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

//https://github.com/polyak01/IconSwitch
public class AddBPFragment extends DialogFragment implements View.OnClickListener, TextWatcher {

    TextView mEtDescription;
    EditText mEtAmount;
    DatabaseReference ref;
    DatabaseReference expensesRef;
    private FirebaseAuth mAuth;
    private String TAG = AddBPFragment.class.getCanonicalName();
    private TextView mTvDate;
    private Expense mExpenseDetailModel = null;

    // Filling the array from XML String Array
    private LinearLayout mLlHeader;
    private View mView;

    private static final int DURATION_COLOR_CHANGE_MS = 400;
    private static final int PICK_CAMERA_IMAGE = 2;
    private static final int PICK_GALLERY_IMAGE = 1;


    private int[] toolbarColors;
    private int[] statusBarColors;

    private Window window;
    private Toolbar mToolbar;
//    private ImageView mIvUserProfile;
    private SimpleDateFormat dateFormatter;
    private Uri imageCaptureUri;
    private File file = null;
    private File sourceFile = null;
    private String imageExtension = "jpg";
    private static final String[] PAYMENT_MODE = {"Cash", "Credit Card", "Debit Card", "Net Banking", "Cheque"};
//    private Spinner spPaymentMode;
    private Spinner spBank;
    private Spinner spCategory;
    private SimpleDateFormat sdf = new SimpleDateFormat(Constant.DATE_FORMAT, Locale.US);
    private static final int CONST_MAX_AMOUNT_LENGTH = 13;

    public static AddBPFragment newInstance(Bundle bundle) {
        AddBPFragment f = new AddBPFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_add_transaction, container, false);
        if (getArguments() != null) {
            mExpenseDetailModel = (Expense) getArguments().getSerializable("ExpenseDetailModel");
        }

        //https://gist.github.com/ferdy182/d9b3525aa65b5b4c468a
//        mView.setBackgroundColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), android.R.color.white));
        // To run the animation as soon as the view is layout in the view hierarchy we add this
        // listener and remove it
        // as soon as it runs to prevent multiple animations if the view changes bounds
//        mView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop,
//                                       int oldRight, int oldBottom) {
//                v.removeOnLayoutChangeListener(this);
//                int cx = getArguments().getInt("cx");
//                int cy = getArguments().getInt("cy");
//
//                // get the hypothenuse so the radius is from one corner to the other
//                int radius = (int) Math.hypot(right, bottom);
//
//                Animator reveal = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0, radius);
//                reveal.setInterpolator(new DecelerateInterpolator(2f));
//                reveal.setDuration(500);
//                reveal.start();
//            }
//        });

//        FirebaseApp.initializeApp(getContext());
        ref = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        file = new File(Environment.getExternalStorageDirectory()
                + "/" + getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                "dd MMM yyyy HH:mm:ss", Locale.US);

        setToolbar(mView);
        loadAllViews(mView);
    }

    private void setToolbar(View rootView) {
//        window = Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        }

        initColors();

        mToolbar = rootView.findViewById(R.id.toolbar);
//        mToolbar.setTitle(getString(R.string.toolbar_title_add_expense));
//        mToolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
//        mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
//        mToolbar.setNavigationOnClickListener(v -> {
//            if (getActivity() != null) {
//                if (getActivity() instanceof DashboardActivity) {
//                    ((DashboardActivity) getActivity()).dismissDialog();
//                }
//            }
//        });

//        iconSwitch = rootView.findViewById(R.id.icon_switch);
//        iconSwitch.setCheckedChangeListener(this);
        updateColors(false);
    }


    private void loadAllViews(final View rootView) {

        mLlHeader = rootView.findViewById(R.id.ll_header);

        mTvDate = rootView.findViewById(R.id.tv_date);
        mEtDescription = rootView.findViewById(R.id.et_description);
        TextView tvCurrencySymbol = rootView.findViewById(R.id.tv_currency_symbol);
        mEtAmount = rootView.findViewById(R.id.et_amount);

//        spPaymentMode = rootView.findViewById(R.id.sp_diastolic);

        LinearLayout llPaymentMode = rootView.findViewById(R.id.ll_payment_mode);
        LinearLayout llBank = rootView.findViewById(R.id.ll_bank);
//        spBank = rootView.findViewById(R.id.sp_systolic);
        rootView.findViewById(R.id.add_account).setOnClickListener(this);
        rootView.findViewById(R.id.add_category).setOnClickListener(this);

        spCategory = rootView.findViewById(R.id.sp_category);
//        mToggleSwitch=rootView.findViewById(R.id.toggle_switch);
//        mToggleSwitch.setOnToggleSwitchChangeListener(new BaseToggleSwitch.OnToggleSwitchChangeListener() {
//            @Override
//            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
//                if(position==0){
//                    iconSwitch.setChecked(IconSwitch.Checked.LEFT);
//                }
//                else {
//                    iconSwitch.setChecked(IconSwitch.Checked.RIGHT);
//                }
//            }
//        });

//        RelativeLayout mRlCaptureUserProfileImage = (RelativeLayout) rootView.findViewById(R.id.rl_capture_proof);
//        mIvUserProfile = rootView.findViewById(R.id.iv_proof);
        Button save=rootView.findViewById(R.id.save);
        save.setOnClickListener(this);
//        fabSubmit.setOnClickListener(this);
        mEtAmount.addTextChangedListener(this);
//        mRlCaptureUserProfileImage.setOnClickListener(this);

        // Set caret/cursor to the end on focus change
        mEtAmount.setOnFocusChangeListener((editText, hasFocus) -> {
            if (hasFocus) {
                ((EditText) editText).setSelection(((EditText) editText).getText().length());
            }
        });

        if (mExpenseDetailModel != null) {
            mTvDate.setText(mExpenseDetailModel.getDate());
            mTvDate.setClickable(false);

            mEtAmount.setText(String.valueOf(mExpenseDetailModel.getAmount()));
            mEtDescription.setText(mExpenseDetailModel.getDescription());

            if(mExpenseDetailModel.getPaymentMode()!=null)
//                spPaymentMode.setSelection(getIndexForPaymentMode(mExpenseDetailModel.getPaymentMode()));

            if(mExpenseDetailModel.getBankName()!=null)
                spBank.setSelection(getIndexForBank(spBank, mExpenseDetailModel.getBankName()));

            if (mExpenseDetailModel.getProofUri().trim().length() > 0){

            }
//                CommonMethod.displayNetworkImage(getActivity(), mExpenseDetailModel.getProofUri(), mIvUserProfile);
            } else {
            displayCategory();
            mTvDate.setText(sdf.format(Calendar.getInstance().getTime()));
        }

        mTvDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_date:
                Date selectedDate = null;
                try {
                    selectedDate = sdf.parse(mTvDate.getText().toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedDate);
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getActivity(), (view, year, month, dayOfMonth) -> {
                            Calendar c = Calendar.getInstance();
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.MONTH, month);
                            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                            mTvDate.setText(sdf.format(c.getTime()));
                        }, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
                datePickerDialog.show();
                break;
            case R.id.add_category:
//                startActivity(new Intent(getContext(), AddTransactionCategoryActivity.class));
                break;
            case R.id.add_account:
//                Intent intent=new Intent(getContext(), AddBankActivity.class);
//                intent.putExtra("from","add");
//                startActivity(intent);
                break;
//            case R.id.rl_capture_proof:
//                if (!CommonMethod.isDeviceSupportCamera(getActivity())) {
//                    Toast.makeText(getActivity(),
//                            R.string.message_device_not_support_camera,
//                            Toast.LENGTH_LONG).show();
//                } else {
//                    captureImages();
//                }
//                break;
            case R.id.save:
                try {
                    String strSelectedDate = mTvDate.getText().toString().trim();
                    String amount = mEtAmount.getText().toString().trim();
                    String strPaymentMode = "spPaymentMode.getSelectedItem().toString();";
                    amount = amount.replaceAll(",", "");

                    if (strSelectedDate.trim().length() == 0) {
                        Toast.makeText(getActivity(), R.string.message_select_date, Toast.LENGTH_LONG).show();
                    } else if (amount.length() == 0 || Double.valueOf(amount) <= 0) {
//                        CommonMethod.showAlertWithOk(getActivity(), getString(R.string.alert),
//                                getString(R.string.message_enter_amount), getString(R.string.action_ok));
                    }
//                    else if (!strPaymentMode.equalsIgnoreCase(PAYMENT_MODE[0])
//                            && spBank.getSelectedItemPosition() < 0) {
//                        Toast.makeText(getActivity(), R.string.message_select_bank, Toast.LENGTH_LONG).show();
//                    }
//                    else if (spCategory.getSelectedItemPosition() < 0) {
//                        Toast.makeText(getActivity(), R.string.message_select_category, Toast.LENGTH_LONG).show();
//                    }
                    else if (sourceFile != null
                            && sourceFile.exists()
                            && CommonMethod.getFileSize(sourceFile) <
                            Constant.CONST_IMAGE_FILE_SIZE) {
                        Toast.makeText(getActivity(), R.string.message_captured_image_is_corrupted_or_invalid, Toast.LENGTH_LONG).show();
                    } else {
                        if(CommonMethod.isNetworkConnected(requireActivity())) {
                            /*
                             * Here proof taken is not compulsory. So if proof is not taken then we simply add/update details to firebase.
                             */
                            if (sourceFile == null
                                    || !sourceFile.exists()) {
                                uploadDetailsToFirebase("", "");
                            } else {
                                File destiFile = new File(file, "img_compressed" + "." + imageExtension);
                                new ImageCompression(AddBPFragment.this)
                                        .execute(sourceFile.getAbsolutePath(), destiFile.getAbsolutePath());
                            }
                        }else{
                            CommonMethod.showConnectionAlert(getActivity());
                        }
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.message_enter_valid_amount, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.message_enter_valid_amount, Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        // This method should be called in the parent Activity's onPause() method.
//        if (mAdView != null) {
//            mAdView.pause();
//        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        // This method should be called in the parent Activity's onResume() method.
//        if (mAdView != null) {
//            mAdView.resume();
//        }
    }

    @Override
    public void onDetach() {
//        if (mAdView != null) {
//            mAdView.destroy();
//        }
        super.onDetach();
        System.gc();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICK_GALLERY_IMAGE:
                    Uri uriPhoto = data.getData();
//                    mIvUserProfile.setImageURI(uriPhoto);
                    sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));
                    break;
                case PICK_CAMERA_IMAGE:
//                    mIvUserProfile.setImageURI(imageCaptureUri);
                    break;
            }
        }
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(getActivity(), res);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        /*if (s.toString().trim().length() > 0) {

            String value = s.toString().replaceAll(",", "");
            //Remove the listener
            String amount = Constant.decimalFormat.format(Double.valueOf(value));
            mEtAmount.removeTextChangedListener(this);
            mEtAmount.setText(amount);
            mEtAmount.addTextChangedListener(this);
            mEtAmount.setSelection(((EditText) mEtAmount).getText().length());
        }*/

        if (mEtAmount.getText().toString().replace(",", "").length() > CONST_MAX_AMOUNT_LENGTH) {
            int cursorPosition = mEtAmount.getSelectionEnd();
            String originalStr = mEtAmount.getText().toString();

            mEtAmount.removeTextChangedListener(this);

            String str = mEtAmount.getText().toString().replace(",", "").substring(0, CONST_MAX_AMOUNT_LENGTH);
            mEtAmount.setText(CommonMethod.getDecimalFormattedString(str));

            int diff = mEtAmount.getText().toString().length() - originalStr.length();
            mEtAmount.setSelection(cursorPosition + diff);

            mEtAmount.addTextChangedListener(this);
            return;
        }

        if (s.toString().trim().length() > 0) {
            int cursorPosition = mEtAmount.getSelectionEnd();
            String originalStr = mEtAmount.getText().toString();

            //To restrict only two digits after decimal place
            mEtAmount.setFilters(new InputFilter[]{new CommonMethod.MoneyValueFilter(Integer.parseInt(String.valueOf(2)))});

            try {
                mEtAmount.removeTextChangedListener(this);
                String value = mEtAmount.getText().toString();

                if (!value.equals("")) {
                    if (value.startsWith(".")) {
                        mEtAmount.setText("0.");
                    }
                    /*if (value.startsWith("0") && !value.startsWith("0.")) {
                        mEtAmount.setText("");
                    }*/
                    String str = mEtAmount.getText().toString().replaceAll(",", "");
                    mEtAmount.setText(CommonMethod.getDecimalFormattedString(str));

                    int diff = mEtAmount.getText().toString().length() - originalStr.length();
                    mEtAmount.setSelection(cursorPosition + diff);
                }
                mEtAmount.addTextChangedListener(this);
            } catch (Exception ex) {
                ex.printStackTrace();
                mEtAmount.addTextChangedListener(this);
            }
        }
    }

    private void displayCategory() {

    }

    private void initColors() {
//
//        TypedValue typedValue = new TypedValue();
//        Resources.Theme theme = getContext().getTheme();
//        theme.resolveAttribute(R.attr.colorPrimaryDarkT, typedValue, true);
//        @ColorInt int color = typedValue.data;
//
//        toolbarColors = new int[IconSwitch.Checked.values().length];
//        statusBarColors = new int[toolbarColors.length];
//        toolbarColors[IconSwitch.Checked.LEFT.ordinal()] = color;
//        statusBarColors[IconSwitch.Checked.LEFT.ordinal()] = color;
//        toolbarColors[IconSwitch.Checked.RIGHT.ordinal()] = color;
//        statusBarColors[IconSwitch.Checked.RIGHT.ordinal()] = color;
    }

    private void updateColors(boolean animated) {
//        int colorIndex = iconSwitch.getChecked().ordinal();
//        mToolbar.setBackgroundColor(toolbarColors[colorIndex]);
//        if (mLlHeader != null)
//            mLlHeader.setBackgroundColor(toolbarColors[colorIndex]);
//        if (animated) {
//            displayCategory();
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.setStatusBarColor(statusBarColors[colorIndex]);
//        }
    }

    private void captureImages() {

        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
        myAlertDialog.setTitle(R.string.alert_title_capture_photo);
        myAlertDialog.setMessage(R.string.alert_message_capture_photo);

        myAlertDialog.setPositiveButton(R.string.action_gallery, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intentGalley = new Intent(Intent.ACTION_PICK);
                intentGalley.setType("image/*");
                startActivityForResult(intentGalley, PICK_GALLERY_IMAGE);
            }
        });

        myAlertDialog.setNegativeButton(R.string.action_camera, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                sourceFile = new File(file, "camera_"
                        + dateFormatter.format(new Date()).toString() + "." + imageExtension);

                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    imageCaptureUri = Uri.fromFile(sourceFile);
                    intentCamera.addFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                } else {
                    imageCaptureUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", sourceFile);
                    intentCamera.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
                    intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
                }
                startActivityForResult(intentCamera, PICK_CAMERA_IMAGE);
            }
        });
        myAlertDialog.show();
    }

    private void uploadDetailsToFirebase(String proofName, String proofFirebasePath) {
        String strSelectedDate = mTvDate.getText().toString().trim();
        String strDescription = mEtDescription.getText().toString().trim();
        String amount = mEtAmount.getText().toString().trim();
        amount = amount.replaceAll(",", "");

        String strPaymentMode="spPaymentMode.getSelectedItem().toString()";
//        String strBankName=strPaymentMode.equalsIgnoreCase(PAYMENT_MODE[0]) ? "" : ((BankModel) spBank.getSelectedItem()).getBankName();;

        Expense thisExpense = null;
        if (proofFirebasePath.trim().length() == 0) {

            if (mExpenseDetailModel != null) {
                thisExpense = new Expense(strDescription, strPaymentMode,
                       "strBankName",
                        "((CategoryModel) spCategory.getSelectedItem()).getCategory()",
                        "((CategoryModel) spCategory.getSelectedItem()).getColor()", Double.valueOf(amount)
                        , Constant.TYPE_INCOME,
                        mExpenseDetailModel.getProofName(), mExpenseDetailModel.getProofUri());
            } else {
                thisExpense = new Expense(strDescription, strPaymentMode,
                        "strBankName",
                        "((CategoryModel) spCategory.getSelectedItem()).getCategory()",
                        "((CategoryModel) spCategory.getSelectedItem()).getColor()", Double.valueOf(amount)
                        ,  Constant.TYPE_INCOME,
                        proofName, proofFirebasePath);
            }

        } else {
            thisExpense = new Expense(strDescription, strPaymentMode,
                    "strBankName",
                    "((CategoryModel) spCategory.getSelectedItem()).getCategory()",
                    "((CategoryModel) spCategory.getSelectedItem()).getColor()", Double.valueOf(amount)
                    , Constant.TYPE_INCOME,
                    proofName, proofFirebasePath);
        }


        Date selectedDate=null;
        try {
            selectedDate = sdf.parse(strSelectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String monthYearVal = new SimpleDateFormat(Constant.MONTH_YEAR_FORMAT, Locale.US).format(selectedDate);


        String uid = mAuth.getCurrentUser().getUid();

        expensesRef = ref.child(Constant.FIREBASE_NODE_EXPENSE).child(uid)
                .child(monthYearVal).child(strSelectedDate);

//        CommonMethod.showProgressDialog(getActivity());
        //User come for Update
        if (mExpenseDetailModel != null) {
            expensesRef.child(mExpenseDetailModel.getId()).setValue(thisExpense)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonMethod.cancelProgressDialog();
                            // Write was successful!
                            if (getActivity() != null) {
                                if (getActivity() instanceof MainActivity) {
//                                    ((MainActivity) getActivity()).insertTransactionEntry();
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonMethod.cancelProgressDialog();
                            // Write failed
                            Log.d(TAG, "Entry not sync : " + e.getMessage());
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            String key = expensesRef.push().getKey();
            expensesRef.child(key).setValue(thisExpense)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonMethod.cancelProgressDialog();
                            // Write was successful!
                            if (getActivity() != null) {
//                                if (getActivity() instanceof DashboardActivity) {
//                                    ((DashboardActivity) getActivity()).insertTransactionEntry();
//                                }
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            CommonMethod.cancelProgressDialog();
                            // Write failed
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    /**
     * This is useful when an image is not available in sdcard physically but it displays into photos application via google drive(Google Photos) and also for if image is available in sdcard physically.
     *
     * @param uriPhoto
     * @return
     */

    public String getPathFromGooglePhotosUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = getActivity().getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(getActivity());
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    private String getTempFilename(Context context) throws IOException {
        sourceFile = new File(file, "gallery_"
                + dateFormatter.format(new Date()).toString() + "." + imageExtension);
        return sourceFile.getAbsolutePath();
    }

    private static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }




    private int getIndexForPaymentMode(String paymentMode){
        int selectedPosition = 0;
        int size = PAYMENT_MODE.length;
        for(int i=0;i<size;i++){
            if(paymentMode.equalsIgnoreCase(PAYMENT_MODE[i])){
                selectedPosition=i;
                break;
            }
        }
        return selectedPosition;
    }

    private int getIndexForBank(Spinner spinner, String name) {
        int index = 0;
        int size = spinner.getCount();
        for (int i = 0; i < size; i++) {
            try {
//                BankModel model = (BankModel) spinner.getItemAtPosition(i);
//                if (model.getBankName().equalsIgnoreCase(name)) {
//                    index = i;
//                    break;
//                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        return index;
    }

    private int getIndexForCategory(Spinner spinner, String name) {
        int index = 0;
//        int size = spinner.getCount();
//        for (int i = 0; i < size; i++) {
//            try {
//                CategoryModel model = (CategoryModel) spinner.getItemAtPosition(i);
//                if (model.getCategory().equalsIgnoreCase(name)) {
//                    index = i;
//                    break;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                break;
//            }
//        }
        return index;
    }


    /**
     * Get result from ImageCompression AsyncTask
     *
     * @param destinationPath
     */
    public void uploadDetailsToServer(String destinationPath) {

        //displaying progress dialog while image is uploading
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(getString(R.string.alert_title_uploading_image));
        progressDialog.show();


        String uid = mAuth.getCurrentUser().getUid();

        String strSelectedDate = mTvDate.getText().toString().trim();
        Date selectedDate=null;
        try {
            selectedDate = sdf.parse(strSelectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String monthYearVal = new SimpleDateFormat(Constant.MONTH_YEAR_FORMAT, Locale.US).format(selectedDate);

        /**
         * Here we use the same name of proof image if
         * user is try to updating existing proof image
         */
        String strImageProofName;
        if (mExpenseDetailModel != null
                && mExpenseDetailModel.getProofName() != null
                && mExpenseDetailModel.getProofName().trim().length() > 0) {
            strImageProofName=mExpenseDetailModel.getProofName();
        } else {
            SimpleDateFormat hh_mm_ss = new SimpleDateFormat(
                    "HH_mm_ss", Locale.US);
            strImageProofName = hh_mm_ss.format(new Date()).toString() + "." + imageExtension;
        }

        /**
         * Here i have used the same structure for storing image same as which we made
         * for storing details
         */
        final StorageReference sRef = FirebaseStorage.getInstance().getReference(uid
                + "/" + Constant.CONST_IMAGE_STORE_CONTAINER
                + "/" + monthYearVal
                + "/" + mTvDate.getText().toString().trim()).child(strImageProofName);

        //adding the file to reference
        sRef.putFile(Uri.fromFile(new File(destinationPath)))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //displaying success toast
                        Toast.makeText(getActivity(), "Proof Uploaded Successfully.", Toast.LENGTH_LONG).show();

                        sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //dismissing the progress dialog
                                progressDialog.dismiss();
                                uploadDetailsToFirebase(strImageProofName, uri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //dismissing the progress dialog
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //displaying the upload progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage(getActivity().getString(R.string.label_uploaded).concat(" ").concat(((int) progress)+ "%...") );
                    }
                });
    }

}