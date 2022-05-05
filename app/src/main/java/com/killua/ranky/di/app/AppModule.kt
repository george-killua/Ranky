package com.killua.ranky.di.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.killua.data.DatasourceRepo
import com.killua.data.IDatasourceRepo
import com.killua.data.cashing.ClubsDao
import com.killua.data.cashing.DbClubs
import com.killua.data.networking.ClubsServices
import com.killua.domain.*
import com.killua.ranky.BuildConfig
import com.killua.ranky.di.viewmodel.ViewModelModule
import com.squareup.moshi.Moshi
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module(
    includes = [
        ViewModelModule::class,
        AppModule.Bindings::class
    ]
)
class AppModule {

    @Module
    interface Bindings {
        @Binds
        fun context(app: Application): Context
    }

    @Singleton
    @Provides
    fun provideDatabase(context: Context): DbClubs {
        return Room.databaseBuilder(context, DbClubs::class.java, "db_clubs")
            .allowMainThreadQueries().build()
    }

    @Provides
    fun provideClubDao(db: DbClubs): ClubsDao {
        return db.clubsDao()
    }


    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(
        moshi: Moshi
    ): ClubsServices {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_API_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(ClubsServices::class.java)
    }

    @Singleton
    @Provides
    fun provideDatasourceRepo(clubsServices: ClubsServices, clubsDao: ClubsDao): IDatasourceRepo {
        return DatasourceRepo(clubsDao, clubsServices)
    }

    @Singleton
    @Provides
    fun provideGetAndCheckClubsUseCase(datasourceRepo: IDatasourceRepo): GetAndCheckClubsUseCase {
        return GetAndCheckClubsUseCaseImpl(dataSource = datasourceRepo)
    }

    @Singleton
    @Provides
    fun provideGetClubUseCase(datasourceRepo: IDatasourceRepo): GetClubUseCase {
        return GetClubUseCaseImpl(dataSource = datasourceRepo)
    }

    @Singleton
    @Provides
    fun provideDisposeUseCase(datasourceRepo: IDatasourceRepo): DisposeUseCase {
        return DisposeUseCaseImpl(datasourceRepo)
    }
}
