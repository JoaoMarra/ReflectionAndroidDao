package br.marraware.reflectionandroiddao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import br.marraware.reflectiondatabase.queries.Insert;
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
        model.integer = 1;
        try {
            model.jsonObject = new JSONObject();
            model.jsonObject.put("SOMETHING","HERE!");
            model.jsonArray = new JSONArray();
            model.jsonArray.put(1);
            model.jsonArray.put(10);
            model.jsonArray.put(100);
            model.jsonArray.put(2312);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        model.insert();

        try {

            TestModel firstModel = Select.from(TestModel.class).executeForFirst();
            Log.e("MainActivity","OBJECT:");
            Log.e("MainActivity",firstModel.jsonObject.toString(2));
            Log.e("MainActivity","Array:");
            Log.e("MainActivity",firstModel.jsonArray.toString(2));


            firstModel.jsonObject.put("SOMETHING 2",5);
            firstModel.jsonArray.put(999);
            firstModel.update();

            firstModel = Select.from(TestModel.class).executeForFirst();
            Log.e("MainActivity","[2]OBJECT:");
            Log.e("MainActivity",firstModel.jsonObject.toString(2));
            Log.e("MainActivity","[2]Array:");
            Log.e("MainActivity",firstModel.jsonArray.toString(2));

            Delete.from(TestModel.class).execute();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
