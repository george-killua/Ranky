package com.killua.data.cashing

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.killua.data.cashing.model.ClubsEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single


@Dao
interface ClubsDao {
    @Query("select * from clubs order by name desc")
    fun getAllClubs(): Single<List<ClubsEntity>>

    @Query("select * from clubs where id =:id")
    fun getClub(id: String): Single<ClubsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClub(vararg club: ClubsEntity): Completable

    @Query("delete from clubs")
    fun removeClubs(): Completable
}
