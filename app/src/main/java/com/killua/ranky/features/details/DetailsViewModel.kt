package com.killua.ranky.features.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.killua.data.models.Club
import com.killua.domain.DisposeUseCase
import com.killua.domain.GetClubUseCase
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class DetailsViewModel @Inject constructor(
    private val getClubUseCase: GetClubUseCase,
    private val disposeUseCase: DisposeUseCase
) : ViewModel() {


    private val _clubLiveData = MutableLiveData<Club>()
    val clubLiveData: LiveData<Club>
        get() = _clubLiveData

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getClub(clubId: String) {
        getClubUseCase.invoke(clubId)
            .subscribeOn(Schedulers.trampoline())
            .doOnSuccess {
                _clubLiveData.value = it
            }.doOnError {
                _error.value = it.localizedMessage
            }
            .observeOn(Schedulers.io()).subscribe()
    }

    fun dispose() = disposeUseCase.invoke()

}