package estudo.example.com.tasksrealm.model;

import java.io.Serializable;
import java.util.Calendar;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by ricardoogliari on 6/19/16.
 */

public class Task extends RealmObject{

    @PrimaryKey
    public String nome;

    public String descricao;
    public long termino;
    public String local;
    public boolean iniciada;

    @Override
    public String toString() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(termino);

        int dia = cal.get(Calendar.DAY_OF_MONTH);
        int mes = cal.get(Calendar.MONTH) + 1;
        int ano = cal.get(Calendar.YEAR);
        int hora = cal.get(Calendar.HOUR_OF_DAY);
        int minuto = cal.get(Calendar.MINUTE);

        return "Nome: " + nome + "\nDescrição: " + descricao + "\nTérmino: " +
                (dia < 10 ? "0" + dia : "" + dia) + "/" +
                (mes < 10 ? "0" + mes : "" + mes) + "/" +
                ano + " " +
                (hora < 10 ? "0" + hora : "" + hora) + ":" +
                (minuto < 10 ? "0" + minuto : "" + minuto);
    }
}
