package com.virtualfittingroom.ui.katalogPria;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.virtualfittingroom.data.models.CatalogModel;
import com.virtualfittingroom.databinding.FragmentKatalogPriaBinding;
import com.virtualfittingroom.ui.components.RVCatalogAdapter;

import java.util.List;

public class KatalogPriaFragment extends Fragment {
    public static final String TAG = "KatalogPriaFragment";

    private FragmentKatalogPriaBinding binding;
    private KatalogPriaViewModel katalogPriaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        katalogPriaViewModel =
                new ViewModelProvider(this).get(KatalogPriaViewModel.class);

        binding = FragmentKatalogPriaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        >>>> INIT RV
        GridLayoutManager gridLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        RVCatalogAdapter rvCatalogAdapter = new RVCatalogAdapter(katalogPriaViewModel.getCatalogList().getValue(), new RVCatalogAdapter.CatalogItemCallback() {
            @Override
            public void onClick(CatalogModel catalog) {
//                Toast.makeText(getContext(), "Item: " + catalog.getName(), Toast.LENGTH_SHORT).show();
//                startActivity(
//                        new Intent(getContext(), ARActivity.class)
//                );
            }
        });
        binding.rvCatalog.setAdapter(rvCatalogAdapter);
        binding.rvCatalog.setLayoutManager(gridLayoutManager);
//        <<<< INIT RV


//        >>>> INIT DATA
        katalogPriaViewModel.getCatalogList().observe(getViewLifecycleOwner(), new Observer<List<CatalogModel>>() {
            @Override
            public void onChanged(List<CatalogModel> catalogModels) {
                Log.i(TAG, "onChanged: data changed via mutableLiveData. Data count: " + catalogModels.size());
                binding.rvCatalog.getAdapter().notifyDataSetChanged();
            }
        });

        loadCatalogData();
        return root;
    }

    private void loadCatalogData(){
        for(int i=0; i<10; i++){
            katalogPriaViewModel.addCatalog(
                    new CatalogModel(
                            "Catalog pri " + i,
                            "Catalog describe " + i,
                            "Catalog color " + i,
                            "color" + i
                    )
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}