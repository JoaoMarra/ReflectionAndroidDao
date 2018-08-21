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

        TestModel model = new TestModel();
        TestModelDependent dependent = new TestModelDependent();
        dependent.dependentName = "dependente Juan";

        model.chave = 1L;
        model.string = "Juan model";
        model.dependent = dependent;

        model.insert();

        try {
            TestModel model1 = Select.from(TestModel.class)
                    .executeForFirst();

            Log.e("SELECT","Found - "+model1.string);
            Log.e("SELECT","Found - D : "+model1.dependent.dependentName);

            model = new TestModel();
            dependent = new TestModelDependent();
            dependent.dependentName = "dependente Juan editado";

            model.chave = 1L;
            model.string = "Juan editado";
            model.dependent = dependent;

            model.update();

            ArrayList<TestModelDependent> dependents = Select.from(TestModelDependent.class).execute();

            Log.e("SELECT DEPENDENT","Found - "+dependents.size());
            Log.e("SELECT DEPENDENT","Found - N : "+dependents.get(0).dependentName);

            Delete.from(TestModel.class).execute();
            Delete.from(TestModelDependent.class).execute();

        } catch (QueryException e) {
            e.printStackTrace();
        }


    }
}
