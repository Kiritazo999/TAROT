package com.example.tarot

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegActivity : AppCompatActivity() {
    private lateinit var button: Button
    private lateinit var userLogin: EditText
    private lateinit var userPass: EditText
    private lateinit var userEmail: EditText
    private lateinit var dbHelper: DBHelper

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)


        // Инициализация элементов интерфейса
        button = findViewById(R.id.button)
        userLogin = findViewById(R.id.userLogin)
        userPass = findViewById(R.id.userPass)
        userEmail = findViewById(R.id.userEmail)

        // Инициализация DBHelper
        dbHelper = DBHelper(this)

        button.setOnClickListener {
            val login = userLogin.text.toString().trim()
            val pass = userPass.text.toString().trim()
            val email = userEmail.text.toString().trim()
            if (login.isEmpty() || pass.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            } else if (!dbHelper.isLoginUnique(login)) {
                Toast.makeText(this, "Логин уже существует", Toast.LENGTH_LONG).show()
            } else {
                // Добавление пользователя в базу данных
                dbHelper.addUser(login, pass, email)
                // Показ сообщения о добавлении пользователя
                Toast.makeText(this, "Пользователь '$login' добавлен", Toast.LENGTH_LONG).show()
            }
        }
    }

    class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_VERSION =   1
            private const val DATABASE_NAME = "users.db"
            private const val TABLE_NAME = "users"
            private const val COLUMN_ID = "id"
            private const val COLUMN_LOGIN = "login"
            private const val COLUMN_PASSWORD = "password"
            private const val COLUMN_EMAIL = "email"
        }

        override fun onCreate(db: SQLiteDatabase) {
            val query = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_LOGIN TEXT, $COLUMN_PASSWORD TEXT, $COLUMN_EMAIL TEXT)"
            db.execSQL(query)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }

        fun addUser(login: String, password: String, email: String) {
            val db = this.writableDatabase
            val contentValues = ContentValues().apply {
                put(COLUMN_LOGIN, login)
                put(COLUMN_PASSWORD, password)
                put(COLUMN_EMAIL, email)
            }
            db.insert(TABLE_NAME, null, contentValues)
            db.close()
        }

        fun isLoginUnique(login: String): Boolean {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_LOGIN = ?", arrayOf(login))
            val isExist = cursor.moveToFirst()
            cursor.close()
            return !isExist
        }


    }
}

