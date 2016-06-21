package estudo.example.com.tasksrealm;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ricardoogliari on 6/20/16.
 */

public class CoreApplication extends Application{

    public Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(realmConfig);
        realm = Realm.getDefaultInstance();
    }
}
