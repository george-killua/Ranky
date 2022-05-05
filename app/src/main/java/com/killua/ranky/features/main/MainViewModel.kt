package com.killua.ranky.features.main

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.killua.data.models.Club
import com.killua.data.utils.RepoResult
import com.killua.domain.DisposeUseCase
import com.killua.domain.GetAndCheckClubsUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getAndCheck: GetAndCheckClubsUseCase,
    private val disposeUseCase: DisposeUseCase
) : ViewModel() {
    private val _clubsLiveData = MutableLiveData<RepoResult<List<Club>>>()
    val clubsLiveData: LiveData<RepoResult<List<Club>>>
        get() = _clubsLiveData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error


    fun getClubs(isConnected: Boolean, @StringRes databaseUpdate: Int) {
        getAndCheck.invoke(
            isConnected, databaseUpdate
        )
            .subscribeOn(Schedulers.io())
            .doOnNext {
                _clubsLiveData.postValue(it)
            }.doOnError {
                _error.value = it.localizedMessage
            }
            .observeOn(AndroidSchedulers.mainThread()).subscribe()
    }

    fun dispose() = disposeUseCase.invoke()

}