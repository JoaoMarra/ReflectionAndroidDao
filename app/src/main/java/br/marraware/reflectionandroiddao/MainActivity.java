package br.marraware.reflectionandroiddao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.ColumnModel;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;
import br.marraware.reflectiondatabase.queries.Delete;
import br.marraware.reflectiondatabase.queries.RawQuery;
import br.marraware.reflectiondatabase.queries.Select;
import br.marraware.reflectiondatabase.queries.SelectDistinct;
import br.marraware.reflectiondatabase.queries.Update;
import br.marraware.reflectiondatabase.utils.AsyncQueryCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReflectionDatabaseManager.initDataBase(DatabaseHelper.createBase(this));

        TestModel model = new TestModel();
        model.string = "Juan";
        model.insert();
        model = new TestModel();
        model.string = "Juan";
        model.insert();
        model = new TestModel();
        model.string = "Julio";
        model.insert();
        model = new TestModel();
        model.string = "Tiago";
        model.insert();
        model = new TestModel();
        model.string = "Yuri";
        model.insert();

        try {

            ArrayList<TestModel> models = Select.from(TestModel.class).whereIn("string","Juan","Julio").execute();
            Log.e("MainActivity","models in:");
            for(int i=0; i < models.size(); i++) {
                Log.e("MainActivity",models.get(i).chave+": "+models.get(i).string);
            }

            SelectDistinct.from(TestModel.class,"string").whereIn("string","Juan","Julio").executeAsync(new AsyncQueryCallback<ColumnModel>() {
                @Override
                public void onBack(List<ColumnModel> models) {
                    Log.e("MainActivity","models in DISTINCT:");
                    for(int i=0; i < models.size(); i++) {
                        Log.e("MainActivity",models.get(i).getValue("string").toString());
                    }
                }
            });

            models = Select.from(TestModel.class).whereNotIn("string","Juan","Julio").execute();
            Log.e("MainActivity","models NOT in:");
            for(int i=0; i < models.size(); i++) {
                Log.e("MainActivity",models.get(i).chave+": "+models.get(i).string);
            }

            ArrayList<ColumnModel> columnModels = RawQuery.query("select * from TestModel where string like '%i%'").execute();
            Log.e("MainActivity","models in RAW:");
            for(int i=0; i < columnModels.size(); i++) {
                Log.e("MainActivity",columnModels.get(i).getValue("chave")+": "+columnModels.get(i).getValue("string"));
            }

            Delete.from(TestModel.class).execute();

        } catch (QueryException e) {
            e.printStackTrace();
        }


    }
}
