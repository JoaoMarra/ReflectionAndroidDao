package br.marraware.reflectionandroiddao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.model.WHERE_COMPARATION;
import br.marraware.reflectiondatabase.queries.Delete;
import br.marraware.reflectiondatabase.queries.Select;
import br.marraware.reflectiondatabase.queries.Update;
import br.marraware.reflectiondatabase.utils.AsyncQueryCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ReflectionDatabaseManager.initDataBase(DatabaseHelper.createBase(this));

        final TestModel model = new TestModel();
        model.boleano = true;

        model.insert();

        try {
            ArrayList<TestModel> models = Select.from(TestModel.class)
                    .where("boleano",true, WHERE_COMPARATION.EQUAL)
                    .execute();
            if(models != null) {
                Log.e("SELECT","Found - "+models.size());
            } else {
                Log.e("SELECT","NOT Found");
            }
            Update.table(TestModel.class)
                    .set("boleano",false)
                    .where("boleano",true,WHERE_COMPARATION.EQUAL)
                    .executeAsync(new AsyncQueryCallback<TestModel>() {
                        @Override
                        public void onBack(List<TestModel> models) {
                            if(models != null) {
                                Log.e("UPDATE","Found - "+models.size());
                            } else {
                                Log.e("UPDATE","NOT Found");
                            }
                            try {
                                ArrayList<TestModel> mdls = Select.from(TestModel.class)
                                        .where("boleano",false, WHERE_COMPARATION.EQUAL)
                                        .execute();
                                if(mdls != null) {
                                    Log.e("SELECT 2","Found - "+mdls.size());
                                } else {
                                    Log.e("SELECT 2","NOT Found");
                                }
                                Delete.from(TestModel.class).execute();
                            } catch (QueryException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (ColumnNotFoundException e) {
            e.printStackTrace();
        } catch (QueryException e) {
            e.printStackTrace();
        }


    }
}
