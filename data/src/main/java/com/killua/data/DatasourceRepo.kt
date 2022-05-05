package com.killua.data

import androidx.annotation.StringRes
import com.killua.data.cashing.ClubsDao
import com.killua.data.cashing.model.ClubsEntity
import com.killua.data.cashing.model.toClub
import com.killua.data.models.Club
import com.killua.data.networking.ClubsServices
import com.killua.data.networking.model.toClub
import com.killua.data.networking.model.toClubEntity
import com.killua.data.utils.OperatorResult
import com.killua.data.utils.RepoResult
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.abs

class DatasourceRepo @Inject constructor(
    private val clubsDao: ClubsDao,
    private val clubsServices: ClubsServices
) : IDatasourceRepo {
    private lateinit var inValue: OperatorResult<Nothing>
    private var compositeDisposable = CompositeDisposable()
    override fun getClub(id: String): Single<Club> {
        return clubsDao.getClub(id).map { it.toClub() }
    }

    private val lister = arrayListOf<Club>()
    override fun getAndCheckClubs(

        isConnected: Boolean,
        @StringRes databaseUpdate: Int
    ): Observable<RepoResult<List<Club>>> {
        return Observable.create<RepoResult<List<Club>>> { emitter ->
            emitter.onNext(RepoResult.Loading)

            var clubsEntity: ClubsEntity? = null
            clubsEntity = getClubsDb(clubsEntity, lister)
            if ((lister.isEmpty() || isDataOld(clubsEntity?.createDate)) && isConnected) {
                emitter.onNext(RepoResult.Failure(databaseUpdate))
                getFromApiAndUpdateDb(emitter, lister).subscribe().also {
                emitter.onNext(RepoResult.Success(lister))
                }

                Timber.tag("geroge").e("api $lister")

            } else {
                emitter.onNext(RepoResult.Success(lister))
                Timber.tag("geroge").e("database $lister")
                emitter.onComplete()
            }
        }.doOnNext {
            if (lister.isNotEmpty())
                RepoResult.Success(lister)

        }

    }

    fun getAndUpdate(   isConnected: Boolean,
                        @StringRes databaseUpdate: Int) =
        Observable.fromSingle<List<Club>> {clubsDao.getAllClubs().subscribe().dispose()  }

     fun getClubsDb(
        clubsEntity: ClubsEntity?,
        lister: ArrayList<Club>
    ): ClubsEntity? {
        var clubsEntity1 = clubsEntity
        clubsDao.getAllClubs().doOnSuccess {
            clubsEntity1 = it.firstOrNull()
            lister.addAll(*it.map { clubsEntity -> clubsEntity.toClub() })
            Timber.e(it.toString())
        }.subscribe()
        return clubsEntity1
    }

    private fun getFromApiAndUpdateDb(
        emitter: ObservableEmitter<RepoResult<List<Club>>>,
        lister: ArrayList<Club>
    ) =
        clubsServices.getAllClubs()
            .flatMapCompletable { list ->
                clubsDao.removeClubs()
                clubsDao.insertClub(*list.map { it.toClubEntity() }.toTypedArray()).mergeWith {
                    clubsDao.getAllClubs().doOnSuccess {
                        lister.addAll(*it.map { clubsEntity -> clubsEntity.toClub() })
                        Timber.e(it.toString())
                    }.doOnSuccess {
                        emitter.onNext(RepoResult.Success(list.map { it.toClub() }))

                    }

                }
            }




    private fun isDataOld(x: Calendar?): Boolean {
        return if (x != null) {
            abs(
                x.get(Calendar.DAY_OF_MONTH) - Calendar.getInstance()
                    .get(Calendar.DAY_OF_MONTH)
            ) > 0
        } else true
    }


    private fun getClubs(
    ): OperatorResult<List<ClubsEntity>> {
        var result: OperatorResult<List<ClubsEntity>> = OperatorResult.Staging
        compositeDisposable.add(
            clubsDao.getAllClubs().subscribeWith(
                object : DisposableSingleObserver<List<ClubsEntity>>() {
                    override fun onSuccess(list: List<ClubsEntity>) {
                        /*     if (list.isNotEmpty())
                                getClubsApi()
                            else*/
                        result = OperatorResult.Error
                    }

                    override fun onError(e: Throwable) {
                        result = OperatorResult.Error
                        Timber.e(e)

                    }
                })
        )

        return result
    }


    private fun updateClubsInDb(
        club: List<Club>,
    ): OperatorResult<List<Club>> {
        inValue = OperatorResult.Staging



        return when (inValue) {
            is OperatorResult.Error -> OperatorResult.Error
            OperatorResult.Staging -> OperatorResult.Staging
            is OperatorResult.Done -> OperatorResult.Done(club)
        }


    }


    private fun insertClubDb(club: Array<ClubsEntity>) =
        clubsDao.insertClub(*club)
            .doOnError { Timber.e(it) }
            .doOnComplete {
                also {
                    Timber.e("i instead in the caching")
                }
            }

    override fun dispose() {
        if (!compositeDisposable.isDisposed)
            compositeDisposable.dispose()
    }
}
