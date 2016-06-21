package estudo.example.com.tasksrealm;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import estudo.example.com.tasksrealm.model.Task;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {

    public static RealmResults<Task> results;

    private RealmChangeListener callback = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            Log.e("TAKS", "atualizou: " + element);
            results = (RealmResults<Task>) element;
            results = results.sort("termino", Sort.ASCENDING);
            listTasks.setAdapter(new ArrayAdapter<Task>(
                    MainActivity.this,
                    android.R.layout.simple_list_item_1,
                    results
                )
            );
        }
    };

    private ListView listTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SaveTask.class);
                startActivity(intent);
            }
        });

        listTasks = (ListView) findViewById(R.id.listTasks);
        listTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mostraOpcoes(results.get(position));
                return false;
            }
        });

        RealmResults<Task> result = ((CoreApplication)getApplication()).realm.where(Task.class).findAllAsync();
        result.addChangeListener(callback);
    }

    public void mostraOpcoes(final Task task){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("O que deseja fazer com esta tarefa?");
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, SaveTask.class);
                intent.putExtra("task", task.nome);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Remover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //só pode ser feito da mesma thread onde o objeto foi criado
                ((CoreApplication)getApplication()).realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        task.deleteFromRealm();
                    }
                });

            }
        });
        builder.create().show();
    }
}
