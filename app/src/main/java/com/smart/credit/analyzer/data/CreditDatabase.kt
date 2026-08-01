package com.smart.credit.analyzer.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smart.credit.analyzer.data.dao.*
import com.smart.credit.analyzer.data.entity.*

/**
 * Room数据库 - 存储征信报告数据
 */
@Database(
    entities = [
        CreditReportEntity::class,
        CreditAccountEntity::class,
        PaymentRecordEntity::class,
        InquiryRecordEntity::class,
        PublicRecordEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CreditDatabase : RoomDatabase() {

    abstract fun creditReportDao(): CreditReportDao

    companion object {
        @Volatile
        private var INSTANCE: CreditDatabase? = null

        fun getDatabase(appContext: android.content.Context): CreditDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    appContext.applicationContext,
                    CreditDatabase::class.java,
                    "credit_analyzer_db"
                )
                    .fallbackToDestructiveMigration() // 版本升级时自动重建表
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}