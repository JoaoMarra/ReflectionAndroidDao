package br.marraware.reflectiondatabase.utils;

import java.util.List;

import br.marraware.reflectiondatabase.model.DaoModel;

/**
 * Created by joao_gabriel on 09/05/17.
 */

public interface AsyncQueryCallback<T extends DaoModel> {

    void onBack(List<T> models);
}
