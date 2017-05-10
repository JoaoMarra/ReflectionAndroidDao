package br.marraware.reflectiondatabase.exception;

import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public class ColumnNotFoundException extends QueryException {

    public ColumnNotFoundException(Class classe, String fieldName) {
        super(fieldName+" column on "+ DaoModel.tableName(classe)+" not found.");
    }
}
