package com.example.womanoftheday

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils.queryNumEntries
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DataBaseHelper
/**
 * Constructor
 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
 * @param context
 * Parameters of super() are:
 * 1. Context
 * 2. Data Base Name.
 * 3. Cursor Factory.
 * 4. Data Base Version.
 */
    (var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Apply your methods and class to fetch data using raw or queries on data base using
     * following demo example code as:
     */
    val allNamesDB: String
        get() {
            val query = "select name From $TABLE_NAME"
            val cursor = sqliteDataBase!!.rawQuery(query, null)
            var userName = ""
            if (cursor.count > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        userName = userName + cursor.getString(0) + ','.toString()
                    } while (cursor.moveToNext())
                }
            }
            return userName
        }

    init {
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.applicationInfo.dataDir + "/databases/"
        } else {
            DB_PATH = "/data/data/" + context.packageName + "/databases/"
        }
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * By calling this method and empty database will be created into the default system path
     * of your application so we are gonna be able to overwrite that database with our database.
     */
    @Throws(IOException::class)
    fun createDataBase() {
        //check if the database exists
        val databaseExist = checkDataBase()
        Log.d("DatB", "database exists = $databaseExist")

        if (databaseExist) {
            // Do Nothing.
        } else {
            Log.d("DatB", "Entered else")
            this.writableDatabase
            Log.d("DatB", "Got Writable DataBase")
            copyDataBase()
        }// end if else dbExist
    } // end createDataBase().

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    fun checkDataBase(): Boolean {
        Log.d("DatB", "Database filepath = $DB_PATH$DATABASE_NAME")
        val databaseFile = File(DB_PATH + DATABASE_NAME)
        return databaseFile.exists()
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     */
    @Throws(IOException::class)
    private fun copyDataBase() {
        //Open your local db as the input stream
        val myInput = context.assets.open(DATABASE_NAME)
        Log.d("DatB", "Opened $DATABASE_NAME")
        // Path to the just created empty db
        val outFileName = DB_PATH + DATABASE_NAME
        Log.d("DatB", "outFileName = $outFileName")
        //Open the empty db as the output stream
        val myOutput = FileOutputStream(outFileName)
        Log.d("DatB", "got output Stream")

        //transfer bytes from the input file to the output file
        val buffer = ByteArray(1024)
        var length = myInput.read(buffer)
        Log.d("DatB", "Start Copy")
        while (length > 0) {
            myOutput.write(buffer, 0, length)
            Log.d("DatB", "Copying")
            length = myInput.read(buffer)
        }
        Log.d("DatB", "End Copy")

        //Close the streams
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

    /**
     * This method opens the data base connection.
     * First it create the path up till data base of the device.
     * Then create connection with data base.
     */
    @Throws(SQLException::class)
    fun openDataBase() {
        //Open the database
        val myPath = DB_PATH + DATABASE_NAME
        sqliteDataBase = SQLiteDatabase.openDatabase(
            myPath, null,
            SQLiteDatabase.OPEN_READWRITE
        )
        Log.d("DatB", "Opened DataBase")
    }

    /**
     * This Method is used to close the data base connection.
     */
    @Synchronized
    override fun close() {
        if (sqliteDataBase != null)
            sqliteDataBase!!.close()
        super.close()
    }

    fun getName(id: Int): String {
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_NAME, COLUMNS,
            " id = ?",
            arrayOf(id.toString()), null, null, null, null
        )

        cursor?.moveToFirst()

        return cursor!!.getString(1)
    }

    fun getSumm(id: Int): String {
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_NAME, COLUMNS,
            " id = ? ",
            arrayOf(id.toString()), null, null, null
        )
        cursor?.moveToFirst()

        return cursor!!.getString(2)
    }

    fun getBio(id: Int): String {
        val db: SQLiteDatabase = this.readableDatabase

        val cursor: Cursor = db.query(
            TABLE_NAME, COLUMNS,
            " id = ? ",
            arrayOf(id.toString()), null, null, null)
        cursor?.moveToFirst()

        return cursor!!.getString(3)
    }

    fun getNumRows(): Long {
        val db: SQLiteDatabase = this.readableDatabase
        return queryNumEntries(db, TABLE_NAME)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // No need to write the create table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // No need to write the update table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
        // We should not update it as requirements of application.
    }

    companion object {
        //The Android's default system path of your application database.
        private var DB_PATH = "/data/data/package_name/databases/"
        // Data Base Name.
        private val DATABASE_NAME = "testDB.db"
        // Data Base Version.
        private val DATABASE_VERSION = 1
        // Table Names of Data Base.
        internal val TABLE_NAME = "women"
        // Columns in the table
        private val COLUMNS = arrayOf("id", "name", "summary", "biography")
        internal var sqliteDataBase: SQLiteDatabase? = null
    }
}