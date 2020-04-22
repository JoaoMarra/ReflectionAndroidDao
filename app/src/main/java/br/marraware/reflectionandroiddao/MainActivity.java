package br.marraware.reflectionandroiddao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.marraware.reflectiondatabase.ReflectionDatabaseManager;
import br.marraware.reflectiondatabase.exception.ColumnNotFoundException;
import br.marraware.reflectiondatabase.exception.QueryException;
import br.marraware.reflectiondatabase.helpers.DaoHelper;
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

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date beforeyesterday = calendar.getTime();

        TestModel model = new TestModel();
        model.string = "Juan";
        model.date = now;
        model.insert();
        model = new TestModel();
        model.string = "Julio";
        model.date = yesterday;
        model.insert();
        model = new TestModel();
        model.string = "Tiago";
        model.date = beforeyesterday;
        model.insert();
        model = new TestModel();
        model.string = "Yuri";
        model.date = beforeyesterday;
        model.insert();

        try {

            ArrayList<TestModel> models = Select.from(TestModel.class).where("date", yesterday,WHERE_COMPARATION.MORE_THAN).execute();
            Log.e("MainActivity","models more:");
            for(int i=0; i < models.size(); i++) {
                Log.e("MainActivity",models.get(i).chave+": "+models.get(i).string);
            }

            models = Select.from(TestModel.class).where("date", yesterday,WHERE_COMPARATION.LESS_THAN).execute();
            Log.e("MainActivity","models less:");
            for(int i=0; i < models.size(); i++) {
                Log.e("MainActivity",models.get(i).chave+": "+models.get(i).string);
            }

            models = Select.from(TestModel.class).whereBetween("date", beforeyesterday, yesterday).execute();
            Log.e("MainActivity","models between:");
            for(int i=0; i < models.size(); i++) {
                Log.e("MainActivity",models.get(i).chave+": "+models.get(i).string);
            }

            Delete.from(TestModel.class).execute();

        } catch (QueryException e) {
            e.printStackTrace();
        }


    }
}
