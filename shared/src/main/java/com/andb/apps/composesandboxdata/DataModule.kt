package com.andb.apps.composesandboxdata

import com.andb.apps.composesandboxdata.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, androidContext(), "test.db") }
    single { Database(get()) }
}