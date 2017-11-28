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
        String string = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. In maximus nisi eu turpis tempus, ac fringilla neque finibus. Praesent ultricies metus sed est venenatis, ac eleifend mi rhoncus. Duis sit amet consectetur urna. Morbi ut porttitor orci, eget condimentum massa. Sed a ullamcorper ante, quis egestas dui. Praesent imperdiet, magna at auctor posuere, magna arcu eleifend elit, quis maximus augue magna a diam. Mauris in rutrum enim, a dictum diam. Interdum et malesuada fames ac ante ipsum primis in faucibus. Sed blandit odio a finibus volutpat. Nam efficitur non ante sed dictum. Duis ultrices fringilla nibh et condimentum. Donec porta. FIM";
        model.string = string;
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
            TestModel m = Select.from(TestModel.class)
                    .where("string",string, WHERE_COMPARATION.EQUAL)
                    .executeForFirst();
            if(m != null) {
                Log.e("SELECT","Found - "+m.string);
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
