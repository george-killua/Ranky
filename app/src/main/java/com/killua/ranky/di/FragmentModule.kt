package com.killua.ranky.di

import com.killua.ranky.features.details.DetailsFragment
import com.killua.ranky.features.main.MainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentModule {

    @PerFragment
    @ContributesAndroidInjector
    fun provideMainFragment(): MainFragment

    @PerFragment
    @ContributesAndroidInjector
    fun provideDetailFragment(): DetailsFragment
}
