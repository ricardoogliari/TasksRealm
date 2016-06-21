package estudo.example.com.tasksrealm;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import estudo.example.com.tasksrealm.model.Task;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class SaveTask extends AppCompatActivity {

    private int dia, mes, ano;
    private int hora = -1, minuto = -1;

    private TextView saveTaskData;
    private TextView saveTaskHora;
    private EditText saveTaskNome;
    private EditText saveTaskDescricao;
    private EditText saveTaskLocal;

    private String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        key = getIntent().getStringExtra("task");
        if (key != null){
            Task result = MainActivity.results.where().equalTo("nome", key).findFirst();
        }

        saveTaskData = (TextView)findViewById(R.id.saveTaskData);
        saveTaskHora = (TextView)findViewById(R.id.saveTaskHora);

        saveTaskNome = (EditText)findViewById(R.id.saveTaskNome);
        saveTaskDescricao = (EditText)findViewById(R.id.saveTaskDescricao);
        saveTaskLocal = (EditText)findViewById(R.id.saveTaskLocal);

        key = getIntent().getStringExtra("task");
        if (key != null){
            Task result = ((CoreApplication)getApplication()).realm.where(Task.class).equalTo("nome", key).findFirst();
            saveTaskNome.setText(result.nome);
            saveTaskNome.setEnabled(false);
            saveTaskDescricao.setText(result.descricao);
            saveTaskLocal.setText(result.local);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(result.termino);
            dia = cal.get(Calendar.DAY_OF_MONTH);
            mes = cal.get(Calendar.MONTH);
            ano = cal.get(Calendar.YEAR);
            hora = cal.get(Calendar.HOUR_OF_DAY);
            minuto = cal.get(Calendar.MINUTE);
            preencheData();
            preencheHora();
        }


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CoreApplication)getApplication()).realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        Task task = new Task();
                        task.nome = saveTaskNome.getText().toString();
                        task.descricao = saveTaskDescricao.getText().toString();
                        task.local = saveTaskLocal.getText().toString();

                        Calendar cal = Calendar.getInstance();
                        cal.set(ano, mes, dia, hora, minuto);

                        if (cal.getTimeInMillis() < System.currentTimeMillis()){
                            Snackbar.make(fab, "A data de término deve ser no futuro", Snackbar.LENGTH_LONG).show();
                        }

                        task.termino = cal.getTimeInMillis();
                        task.iniciada = false;
                        bgRealm.copyToRealmOrUpdate(task);
                        //User user = bgRealm.createObject(User.class);
                        //user.setName("John");
                        //user.setEmail("john@corporation.com");
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        // Transaction was a success.
                        Snackbar.make(fab, "Tarefa salva com sucesso", Snackbar.LENGTH_INDEFINITE).setAction("SAIR", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        // Transaction failed and was automatically canceled.
                    }
                });
            }
        });
    }

    public void data(View view){
        if (dia == 0){
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            dia = cal.get(Calendar.DAY_OF_MONTH);
            mes = cal.get(Calendar.MONTH);
            ano = cal.get(Calendar.YEAR);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dia = dayOfMonth;
                mes = monthOfYear;
                ano = year;
                preencheData();
            }
        }, ano, mes, dia);
        datePickerDialog.show();
    }

    public void preencheData(){
        saveTaskData.setText("Dia de Término: " +
                (dia < 10 ? "0" + dia : "" + dia) + "/" +
                (mes + 1 < 10 ? "0" + (mes + 1) : "" + (mes + 1)) + "/" +
                ano
        );
        saveTaskData.setVisibility(View.VISIBLE);
    }

    public void hora(View view){
        if (hora == -1 && minuto == -1){
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            hora = cal.get(Calendar.HOUR_OF_DAY);
            minuto = cal.get(Calendar.MINUTE);
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hora = hourOfDay;
                minuto = minute;
                preencheHora();
            }
        }, hora, minuto, true);
        timePickerDialog.show();
    }

    public void preencheHora(){
        saveTaskHora.setText("Hora de Término: " +
                (hora < 10 ? "0" + hora : "" + hora) + ":" +
                (minuto < 10 ? "0" + minuto : "" + minuto));
        saveTaskHora.setVisibility(View.VISIBLE);
    }

}
