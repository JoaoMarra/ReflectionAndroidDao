package br.marraware.reflectiondatabase;


import java.util.ArrayList;

import br.marraware.reflectiondatabase.model.DaoAbstractModel;

/**
 * Created by joao_gabriel on 02/05/17.
 */

public interface DataBaseTransactionQuantityCallBack {

    void onBack(int rowCount);
}
