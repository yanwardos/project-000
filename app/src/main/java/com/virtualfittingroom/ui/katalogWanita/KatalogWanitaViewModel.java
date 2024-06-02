package com.virtualfittingroom.ui.katalogWanita;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.virtualfittingroom.data.models.CatalogModel;

import java.util.ArrayList;
import java.util.List;

public class KatalogWanitaViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    private MutableLiveData<List<CatalogModel>> catalogList;

    public KatalogWanitaViewModel() {
        mText = new MutableLiveData<>();
        catalogList = new MutableLiveData<>(new ArrayList<>());
    }

    public LiveData<String> getText() {
        return mText;
    }

    public MutableLiveData<List<CatalogModel>> getCatalogList() {
        return catalogList;
    }

    public void addCatalog(CatalogModel catalogModel){
        catalogList.getValue().add(catalogModel);
        catalogList.setValue(
                catalogList.getValue()
        );
    }

}