package br.marraware.reflectiondatabase.exception;

import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public abstract class QueryException extends Exception {

    public QueryException(String message) {
        super(message);
    }
}
