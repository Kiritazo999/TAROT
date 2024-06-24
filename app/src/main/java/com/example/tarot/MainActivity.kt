package com.example.tarot
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log


class MainActivity : AppCompatActivity() {

    private lateinit var buttonVhod: Button
    private lateinit var Login: EditText
    private lateinit var Pass: EditText
    private lateinit var dbHelper: DBHelper
    private lateinit var buttonReg: Button


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Assuming you have a separate layout for AuthActivity


        buttonVhod = findViewById(R.id.buttonVhod)
        Login = findViewById(R.id.Login)
        Pass = findViewById(R.id.Pass)

        dbHelper = DBHelper(this)

        buttonVhod.setOnClickListener {
            val login = Login.text.toString().trim()
            val password = Pass.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Не все поля заполнены", Toast.LENGTH_LONG).show()
            } else {
                val isAuth = dbHelper.getUser(login, password)
                if (isAuth) {
                    Toast.makeText(this, "Пользователь '$login' вошёл", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, MainActivityScreen::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
                }
            }

        }
        buttonReg = findViewById(R.id.buttonReg)

        buttonReg.setOnClickListener {
            val intent = Intent(this, RegActivity::class.java)
            startActivity(intent)

        }
    }
    class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_VERSION = 1
            private const val DATABASE_NAME = "users.db"
            private const val TABLE_NAME = "users"
            private const val COLUMN_ID = "id"
            private const val COLUMN_LOGIN = "login"
            private const val COLUMN_PASSWORD = "password"

        }

        override fun onCreate(db: SQLiteDatabase) {
            val query = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_LOGIN TEXT, $COLUMN_PASSWORD TEXT)"
            db.execSQL(query)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }


        fun getUser(login: String, password: String): Boolean {
            val db = this.readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_LOGIN = ? AND $COLUMN_PASSWORD = ?", arrayOf(login, password))
            val isExist = cursor.moveToFirst()
            Log.d("DBHelper", "Попытка входа: логин=$login, пароль=$password, результат=$isExist")
            cursor.close()
            return isExist
        }

    }
}
