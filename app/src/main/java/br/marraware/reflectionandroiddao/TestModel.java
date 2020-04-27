package br.marraware.reflectionandroiddao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.PrimaryKey;
import br.marraware.reflectiondatabase.model.TableDepedency;
import br.marraware.reflectiondatabase.model.Unique;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_FAIL;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_IGNORE;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_NONE;
import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;

/**
 * Created by joao_gabriel on 21/07/17.
 */

public class TestModel extends DaoModel {

//    @PrimaryKey
//    public Long chave;

    @TableDepedency("testChage")
    public TestModelDependent dependent;

    public Boolean boleano;
    @Unique
    public String string;
    public Date date;
    public Integer integer;
    public JSONObject jsonObject;
    public JSONArray jsonArray;

    @Override
    protected int insertConflictAlgorithm() {
        return CONFLICT_NONE;
    }
}
