package rtoOdisha.subhamjit;

import static rtoOdisha.subhamjit.config.Constant.WEB_URL;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;
import rtoOdisha.subhamjit.config.ChromeClient;
import rtoOdisha.subhamjit.config.Constant;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    ProgressBar progressBar;
    private Context context;
    private Activity activity;
    private TextView home, exit;
    private CoordinatorLayout coordinatorLayout;
    private WebView webView;
    private ImageView splash;
    private static final String[] REQUIRED_PERMISSION =
            {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ChromeClient chromeClient;

    private FirebaseDatabase _firebase = FirebaseDatabase.getInstance();
    private DatabaseReference rto_webview = _firebase.getReference("rto_webview");
    private ChildEventListener _rto_webview_child_listener;


    private TextView upload;
String web_url;
EditText Edit_url;

    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
          context = getApplicationContext();
        activity = this;




        Edit_url = findViewById(R.id.url_);

        Edit_url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(Edit_url.getText().toString().trim().startsWith("http") && Edit_url.getText().toString().contains(".")){

                } else {
                    if(Edit_url.getText().toString().trim().length()>4)
                    {
                        Edit_url.setError("Invalid url");
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        _rto_webview_child_listener  = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = dataSnapshot.getKey();
                final HashMap<String, Object> _childValue = dataSnapshot.getValue(_ind);
                Log.d("fire_inside","on add");
                assert _childKey != null;


                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                myEdit.putString("offline_url", web_url);
                //  Toast.makeText(context, "on Child Added", Toast.LENGTH_SHORT).show();
                if(_childKey.equals("website"))
                {
                    web_url = _childValue.get("rto_url").toString();
                   // myEdit.putString("offline_url", web_url);
                    webView.loadUrl(web_url);
                    Edit_url.setText(web_url);

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                GenericTypeIndicator<HashMap<String, Object>> _ind = new GenericTypeIndicator<HashMap<String, Object>>() {};
                final String _childKey = dataSnapshot.getKey();
                final HashMap<String, Object> _childValue = dataSnapshot.getValue(_ind);
                Log.d("fire_inside","on change");
                //Toast.makeText(context, "on Child Changed", Toast.LENGTH_SHORT).show();

               //SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
               //SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
               //SharedPreferences.Editor myEdit = sharedPreferences.edit();

                //  Toast.makeText(context, "on Child Added", Toast.LENGTH_SHORT).show();
                if(_childKey.equals("website"))
                {
                    web_url = _childValue.get("rto_url").toString();
                   // myEdit.putString("offline_url", web_url);
                    webView.loadUrl(web_url);
                    Edit_url.setText(web_url);
                    //Toast.makeText(context, sh.getString("offline_url", ""), Toast.LENGTH_SHORT).show();

                }



            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
      rto_webview.addChildEventListener(_rto_webview_child_listener);

        init();

    }




    private  void init(){



        upload = findViewById(R.id.upload_);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
if(Edit_url.getText().toString().trim().startsWith("http") && Edit_url.getText().toString().contains(".")){

    webView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
    HashMap<String, Object> up = new HashMap<>();
    up.put("rto_url", Edit_url.getText().toString().trim());
    rto_webview.child("website").updateChildren(up);
    up.clear();
} else {

    Edit_url.setError("Invalid url");
}


            }
        });


        home = findViewById(R.id.home_);



        home.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                webView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                webView.loadUrl(web_url);
                return true;
            }
        });



        exit = findViewById(R.id.exit_);

        exit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                finishAffinity();
                return true;
            }
        });



        progressBar = findViewById(R.id.pb_webLoad);

        coordinatorLayout = findViewById(R.id.cl_webView);
        coordinatorLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSplashScreenBackground));

        splash = findViewById(R.id.img_splash);

        webView = findViewById(R.id.wv_nyoloWeb);
        webView.setVisibility(View.GONE);

        if (hasCameraPermission())
            loadWeb();
        else
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permission_message_denied_camera),
                    Constant.REQUEST_REQUIRED_PERMISSION,
                    REQUIRED_PERMISSION);

    }

    private void loadWeb() {
        final Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);

        final Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(1000);

        final AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(fadeOut);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                splash.setAnimation(fadeOut);
                splash.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        splash.setVisibility(View.GONE);
                        webView.setVisibility(View.VISIBLE);
                        webView.setAnimation(fadeIn);
                    }
                }, 1000);
            }
        });


      //  webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
      //  webView.loadUrl(WEB_URL);




        //Set Custom Chrome Client
        chromeClient = new ChromeClient(this, (intent, resultCode) ->
                startActivityForResult(intent, resultCode));
        webView.setWebChromeClient(chromeClient);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            settings.setDatabasePath("/data/data" + webView.getContext().getPackageName() + "/databases/");
        }
    }

    protected void exitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(getString(R.string.alert_quit_title));
        builder.setMessage(getString(R.string.alert_quit_message));
        builder.setCancelable(true);

        builder.setPositiveButton(getText(R.string.text_yes), (dialogInterface, i) -> MainActivity.super.onBackPressed());

        builder.setNegativeButton(getString(R.string.text_no), (dialogInterface, i) -> dialogInterface.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onBackPressed() {

            exitDialog();

    }

    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(MainActivity.this, REQUIRED_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        loadWeb();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_message_denied_camera),
                Constant.REQUEST_REQUIRED_PERMISSION,
                REQUIRED_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            ValueCallback<Uri[]> mUploadMessages = chromeClient.getResultChooserImage().getValueCallback();
            Uri mCapturedImageURI = chromeClient.getResultChooserImage().getUri();
            if (mUploadMessages != null)
                handleUploadMessages(intent, mUploadMessages, mCapturedImageURI);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleUploadMessages(Intent intent, ValueCallback<Uri[]> mUploadMessages, Uri mCapturedImageURI) {
        Uri[] results = null;
        try {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            } else {
                results = new Uri[]{mCapturedImageURI};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUploadMessages.onReceiveValue(results);
    }
}