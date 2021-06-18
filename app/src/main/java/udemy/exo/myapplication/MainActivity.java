package udemy.exo.myapplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.security.Permissions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    static int PERMISSION_CAMERA = 100;
    private static String TAG = "CameraXBasic";
    private static String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    private static int REQUEST_CODE_PERMISSIONS = 10;
    private String REQUIRED_PERMISSIONS = Manifest.permission.CAMERA;


    private ActivityResultLauncher<Intent> someActivityResultLauncher;


    private String name;
    private int age;
    private String species;
    private String owner;
    private Realm realm;

    private Adapter adapter;
    private List<Task> list;

    private EditText nameEdit, ageEdit, speciesEdit, ownerEdit;
    private FloatingActionButton fab, fabFilter;
    private RecyclerView rcv;

    private BarcodeScanner scanner = BarcodeScanning.getClient();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkReadPermission();
        checkPermission();

       if (getAllPermissions()){
           startCamera();
       } else {
           ActivityCompat.requestPermissions(this, new String[]{REQUIRED_PERMISSIONS}, REQUEST_CODE_PERMISSIONS);
       }
       fabFilter = findViewById(R.id.fab_filter);
       fabFilter.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               pickFile();
           }
       });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CAMERA) {
            if (getAllPermissions()) {
                startCamera();
            }
        }
    }

    private void checkPermission(){
        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (result != null && result.getData() != null) {
                                readImage(result.getData().getData());
                            }

                        }
                    }
                });
    }

    private void pickFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PERMISSION_CAMERA);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                readImage(data.getData());
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkReadPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }
    }


    private boolean getAllPermissions() {
        return ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS) == PERMISSION_GRANTED;
    }


    private void readImage(Uri uri) {
        InputImage image = null;

        try {
            image = InputImage.fromFilePath(this, uri);
        } catch (Exception e) {
            String debug = e.getMessage();
        }


        com.google.android.gms.tasks.Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                    @Override
                    public void onSuccess(List<Barcode> barcodes) {
                        readBarcodes(barcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
    }


    private void readBarcodes(List<Barcode>barcodes){
        for (Barcode barcode: barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

            int valueType = barcode.getValueType();
            // See API reference for complete list of supported types
            switch (valueType) {
                case Barcode.TYPE_WIFI:
                    String ssid = barcode.getWifi().getSsid();
                    String password = barcode.getWifi().getPassword();
                    int type = barcode.getWifi().getEncryptionType();
                    break;
                case Barcode.TYPE_URL:
                    String title = barcode.getUrl().getTitle();
                    String url = barcode.getUrl().getUrl();
                    break;
            }
        }
    }

    private void startCamera(){



    }
    private void takePhoto(){

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }



    private void getInfos(){
        if(nameEdit.getText() != null && ageEdit.getText() != null && speciesEdit.getText() != null && ownerEdit.getText() != null) {
            name = nameEdit.getText().toString();
            age =  Integer.parseInt(ageEdit.getText().toString());
            species = speciesEdit.getText().toString();
            owner = ownerEdit.getText().toString();
        }
    }

    private void configRcv() {
        adapter = new Adapter(list);
        rcv.setAdapter(adapter);
        rcv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getResults(){

        RealmResults<Task> results = realm.where(Task.class).findAll();

        OrderedRealmCollectionChangeListener<RealmResults<Task>> listener = new OrderedRealmCollectionChangeListener<RealmResults<Task>>() {
            @Override
            public void onChange(RealmResults<Task> tasks, OrderedCollectionChangeSet changeSet) {
                String debug = "";
            }
        };
        results.addChangeListener(listener);


        list.clear();
        list.addAll(realm.copyFromRealm(results));
        String debug = "";

        /*realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm2) {
                RealmResults<Task> results = realm2.where(Task.class).findAll();
                list.clear();
                list.addAll(results);
                String debug = "";
            }
        });*/

        String test4  = "";

    }

    private void addTask(){
        getInfos();
        Task task = new Task(name, age, species, owner);
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        try {
            realm.copyToRealmOrUpdate(task);
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            String debug = "";
        }finally {
            getResults();
            realm.close();
        }
    }

    private void configureBarcode(){
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_CODABAR)
                        .build();
    }

    private void getResultsWithFilter(){

        RealmQuery<Task> query = realm.where(Task.class);
        RealmResults<Task> results = query.rawPredicate("age = 1 OR age = 2").findAll();

        OrderedRealmCollectionChangeListener<RealmResults<Task>> listener = new OrderedRealmCollectionChangeListener<RealmResults<Task>>() {
            @Override
            public void onChange(RealmResults<Task> tasks, OrderedCollectionChangeSet changeSet) {
                String debug = "";
            }
        };
        results.addChangeListener(listener);


        String debug2 = "";
    }

    @Override
    protected void onStop() {
        super.onStop();
        //realm.close();
    }
}