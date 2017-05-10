package br.marraware.reflectiondatabase.exception;

import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public class InsertIdNotFoundException extends QueryException {

    public InsertIdNotFoundException(Class classe, String idColumnName) {
        super("Identifier column "+idColumnName+" no find in insertion on "+DaoModel.tableName(classe)+" table");
    }
}
