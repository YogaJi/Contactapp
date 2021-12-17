package site.yogaji.contactapp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import site.yogaji.contactapp.model.Contact;

import java.util.ArrayList;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {
    //set sqlite
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "contact_db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //create db
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Contact.CREATE_TABLE);
        sqLiteDatabase.execSQL(Contact.CREATE_COMPNAY_TABLE);
    }
    //set db update migration
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if (oldVersion < 2) {
            upgradeVersion2(sqLiteDatabase);
        }
    }

    private void upgradeVersion2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + Contact.TABLE_NAME + " ADD COLUMN " + " TEXT");
    }

    //set downgrade db version
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion > 2) { //DATABASE_VERSION == 3
            db.execSQL("DROP TABLE IF EXISTS company");
        }
    }

    //return inset id
    public long insertContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contact.COLUMN_NAME, contact.getName());
        values.put(Contact.COLUMN_TELEPHONE, contact.getTelephone());
        values.put(Contact.COLUMN_ADDRESS, contact.getAddress());
        long id = db.insert(Contact.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    //query db by name and return contact
    public Contact getContactQueryByName(String searchName) {
        Contact contact = new Contact();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columnArray = new String[]{
                Contact.COLUMN_ID,
                Contact.COLUMN_NAME,
                Contact.COLUMN_AVATAR,
                Contact.COLUMN_TELEPHONE,
                Contact.COLUMN_ADDRESS};
        Cursor cursor = db.query(Contact.TABLE_NAME,
                columnArray,
                Contact.COLUMN_NAME + "=? ",
                new String[]{searchName},
                null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(Contact.COLUMN_ID));
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_NAME));
            @SuppressLint("Range") int portrait = cursor.getInt(cursor.getColumnIndex(Contact.COLUMN_AVATAR));
            @SuppressLint("Range") String telephone = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_TELEPHONE));
            @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_ADDRESS));
            contact.setId(id);
            contact.setName(name);
            contact.setAvatar(portrait);
            contact.setTelephone(telephone);
            contact.setAddress(address);

            cursor.close();
            return contact;
        }
        return null;
    }


    //get and return contact arraylist
    public ArrayList<Contact> getAllContact() {
        ArrayList<Contact> contactArrayList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + Contact.TABLE_NAME
                + " ORDER BY " + Contact.COLUMN_ID + " ASC";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(Contact.COLUMN_ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_NAME));
                @SuppressLint("Range") int portrait = cursor.getInt(cursor.getColumnIndex(Contact.COLUMN_AVATAR));
                @SuppressLint("Range") String telephone = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_TELEPHONE));
                @SuppressLint("Range") String address = cursor.getString(cursor.getColumnIndex(Contact.COLUMN_ADDRESS));
                contact.setId(id);
                contact.setName(name);
                contact.setAvatar(portrait);
                contact.setTelephone(telephone);
                contact.setAddress(address);

                contactArrayList.add(contact);
            }
        }

        db.close();
        return contactArrayList;
    }

    //return db count
    public int getContactCount() {
        String countQuery = "SELECT * FROM " + Contact.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //update row id, contact ,successful return
    public int updateContact(int id, Contact contact) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Contact.COLUMN_AVATAR, contact.getAvatar());
        values.put(Contact.COLUMN_NAME, contact.getName());
        values.put(Contact.COLUMN_TELEPHONE, contact.getTelephone());
        values.put(Contact.COLUMN_ADDRESS, contact.getAddress());
        int idReturnByUpdate = db.update(Contact.TABLE_NAME, values, Contact.COLUMN_ID + " =? ", new String[]{String.valueOf(id)});
        db.close();
        return idReturnByUpdate;
    }

    //set delete contact and return delete id
    public int deleteContact(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int idReturnByDelete = db.delete(Contact.TABLE_NAME, Contact.COLUMN_ID + "=? ", new String[]{String.valueOf(id)});
        db.close();
        return idReturnByDelete;
    }


    //delete all contact
    public int deleteAllContact() {
        SQLiteDatabase db = getWritableDatabase();
        int idReturnByDelete = db.delete(Contact.TABLE_NAME, String.valueOf(1), null);
        db.close();
        return idReturnByDelete;
    }
}
