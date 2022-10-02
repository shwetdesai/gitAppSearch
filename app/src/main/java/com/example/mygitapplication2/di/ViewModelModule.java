package com.example.mygitapplication2.di;

import androidx.lifecycle.ViewModel;

import com.example.mygitapplication2.viewModel.DetailsFragmentViewModel;
import com.example.mygitapplication2.viewModel.MainFragmentViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainFragmentViewModel.class)
    abstract ViewModel bindMainFragmentViewModel(MainFragmentViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DetailsFragmentViewModel.class)
    abstract ViewModel bindDetailsFragmentViewModel(DetailsFragmentViewModel viewModel);
}
