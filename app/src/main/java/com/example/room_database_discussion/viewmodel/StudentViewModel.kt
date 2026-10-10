package com.example.room_database_discussion.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.room_database_discussion.data.Student
import com.example.room_database_discussion.data.StudentDatabase
import com.example.room_database_discussion.data.StudentRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentViewModel(application: Application): AndroidViewModel(application) {
    private val database = StudentDatabase.getData(application)
    private val repository = StudentRepository(database.studentDao())
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    private val _nameError = MutableStateFlow<String?>(null)
    private val _emailError = MutableStateFlow<String?>(null)
    private val _courseError = MutableStateFlow<String?>(null)
    private val _ageError = MutableStateFlow<String?>(null)
    private val _studentCount = MutableStateFlow(0)

    val nameError: StateFlow<String?> = _nameError.asStateFlow()
    val emailError: StateFlow<String?> = _emailError.asStateFlow()
    val courseError: StateFlow<String?> = _courseError.asStateFlow()
    val ageError: StateFlow<String?> = _ageError.asStateFlow()
    val students: StateFlow<List<Student>> = _students.asStateFlow()
    val studentCount: StateFlow<Int> = _studentCount.asStateFlow()

    private var studentJob: Job? = null

    init {
        searchStudents("")

        viewModelScope.launch {
            repository.studentCount.collect { count ->
                _studentCount.value = count
            }
        }
    }

    fun searchStudents(search: String) {
        studentJob?.cancel()

        studentJob = viewModelScope.launch {
            val studentFlow =
                if (search.isBlank()) {
                    repository.students
                } else {
                    repository.searchStudents(search)
                }

            studentFlow.collect { studentList ->
                _students.value = studentList
            }
        }
    }

    fun filterStudentsByCourse(course: String) {
        studentJob?.cancel()

        studentJob = viewModelScope.launch {
            val studentFlow =
                if (course == "All Courses") {
                    repository.students
                } else {
                    repository.getStudentsByCourse(course)
                }

            studentFlow.collect { studentList ->
                _students.value = studentList
            }
        }
    }

    fun addStudent(
        name: String,
        email: String,
        course: String,
        age: String
    ): Boolean
    {
        if (!isFormValid(name, email, course, age)) return false

        viewModelScope.launch {
            repository.insertStudents(
                Student(
                    name = name,
                    email = email,
                    course = course,
                    age = age.toInt()
                )
            )
        }
        return true
    }

    fun deleteStudent(student: Student){
        viewModelScope.launch {
            repository.deleteStudent(student)
        }
    }

    fun updateStudent(student: Student, age: String): Boolean {
        if (!isFormValid(student.name, student.email, student.course, age)) {
            return false
        }

        viewModelScope.launch {
            repository.updateStudent(student.copy(age = age.toInt()))
        }

        return true
    }

    fun validateName(name: String) {
        _nameError.value =
            if (name.isBlank()) "Name cannot be empty"
            else null
    }

    fun validateEmail(email: String) {
        _emailError.value = when {
            email.isBlank() -> "Email cannot be empty"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Enter a valid email address"
            else -> null
        }
    }

    fun validateCourse(course: String) {
        _courseError.value =
            if (course.isBlank()) "Course cannot be empty"
            else null
    }

    fun validateAge(age: String) {
        _ageError.value = when {
            age.isBlank() -> "Age cannot be empty"
            age.toIntOrNull() == null || age.toInt() < 0 ->
                "Enter a valid age"
            else -> null
        }
    }

    private fun isFormValid(
        name: String,
        email: String,
        course: String,
        age: String
    ): Boolean
    {
        validateName(name)
        validateEmail(email)
        validateCourse(course)
        validateAge(age)

        return _nameError.value == null &&
                _emailError.value == null &&
                _courseError.value == null &&
                _ageError.value == null
    }
}
