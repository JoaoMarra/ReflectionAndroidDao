package br.marraware.reflectionandroiddao;

import br.marraware.reflectiondatabase.model.DaoModel;
import br.marraware.reflectiondatabase.model.PrimaryKey;

/**
 * Created by joao_gabriel on 21/07/17.
 */

public class TestModel extends DaoModel {

    @PrimaryKey
    public Long chave;

    public Boolean boleano;
    public String string;
}
