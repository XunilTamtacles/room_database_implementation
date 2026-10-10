package com.example.room_database_discussion.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Query("SELECT * FROM students ORDER BY name")
    fun getAllStudents(): Flow<List<Student>>
    @Query("SELECT * FROM students WHERE name LIKE '%' || :search || '%' ORDER BY name")
    fun searchStudents(search: String): Flow<List<Student>>
    @Query(" SELECT * FROM students WHERE course = :course ORDER BY name")
    fun getStudentsByCourse(course: String): Flow<List<Student>>
    @Query("SELECT COUNT(*) FROM students")
    fun getStudentCount(): Flow<Int>
    @Insert
    suspend fun insertStudent(student: Student)
    @Delete
    suspend fun deleteStudent(student: Student)
    @Update
    suspend fun  updateStudent(student: Student)
}