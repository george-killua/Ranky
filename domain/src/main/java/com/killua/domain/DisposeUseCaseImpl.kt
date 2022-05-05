package com.killua.domain

import com.killua.data.IDatasourceRepo
import javax.inject.Inject

class DisposeUseCaseImpl @Inject constructor(
    private val datasource: IDatasourceRepo
) : DisposeUseCase {
    override fun invoke() = datasource.dispose()

}