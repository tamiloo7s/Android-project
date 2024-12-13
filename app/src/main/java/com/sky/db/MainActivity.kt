package com.sky.db

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sky.db.ui.theme.DBTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DBTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                   tt(this)
                }
            }
        }
    }
}

@Dao
interface studentdao{


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(insert:studentMC)

    @Query("SELECT * FROM student")
    fun display():List<studentMC>

    @Query("SELECT EXISTS(SELECT * FROM student WHERE id = :id AND (name IS NULL OR name = ''))")
    fun check(id :String):Boolean

}

@Dao
interface employeedao{


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(insert:employeeMC)

    @Query("SELECT * FROM employee")
    fun display():List<employeeMC>

}

@Entity(tableName = "student")
data class studentMC(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var name:String,
    var rollno:Int
)

@Entity(tableName = "employee")
data class employeeMC(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var name:String,
    var rollno:Int
)

class rep(var rep:studentdao)
{
    fun insert(insert:studentMC){
        rep!!.insert(insert)
    }
    fun display(): List<studentMC> {
        return rep!!.display()
    }
    fun check(id:String):Boolean{
        return rep!!.check(id)
    }
}

class erep(var rep:employeedao)
{
    fun insert(insert:employeeMC){
        rep!!.insert(insert)
    }
    fun display(): List<employeeMC> {
        return rep!!.display()
    }
}

class vm(app: Application): AndroidViewModel(app){
    var vmobj:rep? = null
    init {

        var ins = RoomDB.getinstance(app).dataabs()
        vmobj =rep(ins)
    }

    fun insert(insert:studentMC)
    {
        vmobj!!.insert(insert)
    }
    fun display():List<studentMC> {
        return  vmobj!!.display()
    }
    fun check(id:String):Boolean{
        return vmobj!!.check(id)
    }

}

class evm(app: Application): AndroidViewModel(app){
    var vmobj:erep? = null
    init {

        var ins = RoomDB.getinstance(app).dataabs1()
        vmobj =erep(ins)
    }

    fun insert(insert:employeeMC)
    {
        vmobj!!.insert(insert)
    }
    fun display():List<employeeMC> {
        return  vmobj!!.display()
    }

}


@Database(entities = [studentMC::class,employeeMC::class], version = 2, exportSchema = false)

abstract class RoomDB: RoomDatabase(){
    abstract fun dataabs():studentdao
    abstract fun dataabs1():employeedao
    companion object {

        private var INSTANCE:RoomDB?=null

        fun getinstance(context: Context):RoomDB{

            val Migration1_2: Migration =   object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    try {

                        database.execSQL("CREATE TABLE IF NOT EXISTS employee ('id' INTEGER NOT NULL,'name' TEXT NOT NULL, 'rollno' INTEGER NOT NULL,PRIMARY KEY('id'))")


                    }
                    catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            synchronized(this)
            {
                return INSTANCE?: Room.databaseBuilder(
                    context,
                    RoomDB::class.java,
                    "database"
                )
                    .addMigrations(Migration1_2)
                    .allowMainThreadQueries().build().also {
                        INSTANCE = it
                    }
            }
        }
    }
}


@Composable
fun tt(mainActivity: MainActivity)
{
    var vm = ViewModelProvider(mainActivity)[vm::class.java]
    var evm = ViewModelProvider(mainActivity)[evm::class.java]

    var value by remember{
        mutableStateOf("")
    }


    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        TextField(value = value, onValueChange = {
            value = it
        })

        Button(onClick = {

            println("aaaaaaa${ vm.check("1") }")

            vm.insert(
                studentMC(
                    id = 0,
                    name = value,
                    rollno = 1
                )
            )
            value = ""
        }) {
            Text("sumbit")
        }
    }
}

