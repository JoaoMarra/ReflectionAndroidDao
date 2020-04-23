package br.marraware.reflectionandroiddao;

import java.util.Date;

import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.PrimaryKey;
import br.marraware.reflectiondatabase.model.TableDepedency;

/**
 * Created by joao_gabriel on 21/07/17.
 */

public class TestModel extends DaoModel {

    @PrimaryKey
    public Long chave;

    @TableDepedency("testChage")
    public TestModelDependent dependent;

    public Boolean boleano;
    public String string;
    public Date date;
    public Integer integer;
}
