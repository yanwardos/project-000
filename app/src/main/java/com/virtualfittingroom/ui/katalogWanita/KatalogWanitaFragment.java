package com.virtualfittingroom.ui.katalogWanita;

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
import com.virtualfittingroom.databinding.FragmentKatalogWanitaBinding;
import com.virtualfittingroom.ui.components.RVCatalogAdapter;

import java.util.List;

public class KatalogWanitaFragment extends Fragment {
    public static final String TAG = "KatalogWanitaFragment";

    private FragmentKatalogWanitaBinding binding;
    private KatalogWanitaViewModel katalogWanitaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        katalogWanitaViewModel =
                new ViewModelProvider(this).get(KatalogWanitaViewModel.class);

        binding = FragmentKatalogWanitaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        >>>> INIT RECYCLERVIEW
        GridLayoutManager gridLayoutManager = new GridLayoutManager(binding.getRoot().getContext(), 2, GridLayoutManager.HORIZONTAL, false);
        RVCatalogAdapter rvCatalogAdapter = new RVCatalogAdapter(katalogWanitaViewModel.getCatalogList().getValue(), new RVCatalogAdapter.CatalogItemCallback() {
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
//        <<<<< INIT RECYCLERVIEW


//        final TextView textView = binding.textSlideshow;
//        katalogWanitaViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        katalogWanitaViewModel.getCatalogList().observe(getViewLifecycleOwner(), new Observer<List<CatalogModel>>() {
            @Override
            public void onChanged(List<CatalogModel> catalogModels) {
                Log.i(TAG, "onChanged: data changed via mutablelivedata. Data size: " + catalogModels.size());
                binding.rvCatalog.getAdapter().notifyDataSetChanged();
            }
        });

        loadCatalogData();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void loadCatalogData(){
        for(int i=0; i<10; i++){
            katalogWanitaViewModel.addCatalog(
                    new CatalogModel(
                            "Catalog " + i,
                            "Catalog describe " + i,
                            "Catalog color " + i,
                            "color" + i
                    )
            );
        }
    }


}