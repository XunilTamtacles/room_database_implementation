package com.example.room_database_discussion.data

import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {
    val students: Flow<List<Student>>
        get() = studentDao.getAllStudents()
    val studentCount: Flow<Int>
        get() = studentDao.getStudentCount()

    suspend fun insertStudents(student: Student){
        studentDao.insertStudent(student)
    }

    suspend fun deleteStudent(student: Student){
        studentDao.deleteStudent(student)
    }

    suspend fun updateStudent(student: Student){
        studentDao.updateStudent(student)
    }

    fun searchStudents(search: String): Flow<List<Student>> {
        return studentDao.searchStudents(search)
    }

    fun getStudentsByCourse(course: String): Flow<List<Student>> {
        return studentDao.getStudentsByCourse(course)
    }

}