package com.irfan.dailygoals.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "daily_habits", primaryKeys = ["date", "habitId"])
data class DailyHabitCheck(@ColumnInfo(name = "date") val date: String, val habitId: String, val done: Boolean)

@Entity(tableName = "weekly_goals")
data class WeeklyGoal(@PrimaryKey val id: String, val name: String, val weeklyTarget: Int = 1)

@Entity(tableName = "weekly_checks", primaryKeys = ["goalId", "date"])
data class WeeklyCheck(val goalId: String, val date: String, val done: Boolean = true)

@Entity(tableName = "gym_sessions")
data class GymSession(@PrimaryKey(autoGenerate = true) val id: Long = 0, val date: String, val focus: String, val notes: String = "")

@Dao
interface GoalsDao {
    @Query("SELECT * FROM weekly_goals ORDER BY name") fun weeklyGoals(): Flow<List<WeeklyGoal>>
    @Query("SELECT * FROM weekly_goals") suspend fun weeklyGoalsSnapshot(): List<WeeklyGoal>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun saveWeeklyGoal(goal: WeeklyGoal)
    @Delete suspend fun deleteWeeklyGoal(goal: WeeklyGoal)
    @Query("SELECT * FROM weekly_checks WHERE date = :date") fun checksForDate(date: String): Flow<List<WeeklyCheck>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun setWeeklyCheck(check: WeeklyCheck)
    @Query("DELETE FROM weekly_checks WHERE goalId = :goalId AND date = :date") suspend fun clearWeeklyCheck(goalId: String, date: String)
    @Query("SELECT * FROM daily_habits WHERE date = :date") fun habitsForDate(date: String): Flow<List<DailyHabitCheck>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun setHabitCheck(check: DailyHabitCheck)
    @Query("SELECT * FROM gym_sessions ORDER BY date DESC") fun gymSessions(): Flow<List<GymSession>>
    @Insert suspend fun addGymSession(session: GymSession)
}

@Database(entities = [DailyHabitCheck::class, WeeklyGoal::class, WeeklyCheck::class, GymSession::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun goalsDao(): GoalsDao
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "daily-goals.db").build().also { INSTANCE = it }
        }
    }
}
