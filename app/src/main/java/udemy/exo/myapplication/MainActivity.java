package udemy.exo.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private String name;
    private int age;
    private String species;
    private String owner;
    private Realm realm;

    private Adapter adapter;
    private List<Task> list;

    private EditText nameEdit, ageEdit, speciesEdit, ownerEdit;
    private FloatingActionButton fab;
    private RecyclerView rcv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();

        rcv = findViewById(R.id.rcv);
        configRcv();

        nameEdit = findViewById(R.id.edit_name);
        ageEdit = findViewById(R.id.edit_age);
        speciesEdit = findViewById(R.id.edit_species);
        ownerEdit = findViewById(R.id.edit_owner);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

        realm = Realm.getDefaultInstance();
        getResults();
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

        String test2  = "";

    }

    private void addTask(){
        getInfos();
        Task task = new Task(name, age, species, owner);
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
}