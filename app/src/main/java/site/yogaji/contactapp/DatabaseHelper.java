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
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "contact_db";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Contact.CREATE_TABLE);
        sqLiteDatabase.execSQL(Contact.CREATE_COMPNAY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        /**在很多Demo中为了简单，直接删除重建表
         //drop old table if is existed
         sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BusinessCard.TABLE_NAME);
         //create table again
         onCreate(sqLiteDatabase); **/

        //但在实际开发中需要严谨的对待数据库migration，一旦造成用户数据丢失，是很差的用户体验。
        if (oldVersion < 2) {
            upgradeVersion2(sqLiteDatabase);
        }
    }

    private void upgradeVersion2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + Contact.TABLE_NAME + " ADD COLUMN " + " TEXT");
    }

    //需要测试一下回滚DB, 数据库降级，数据库版本从3降到2
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion > 2) { //DATABASE_VERSION == 3
            db.execSQL("DROP TABLE IF EXISTS company");
        }
    }

    /**
     *
     * @param contact
     * @return 返回新插入的行的ID，发生错误，插入不成功，则返回-1
     */
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

    /**
     *
     * @param searchName query database by name
     * @return BusinessCard//contact
     */
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

    /**
     *
     * @return 读取数据库，返回一个 BusinessCard 类型的 ArrayList
     */
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

    /**
     *
     * @return 返回数据库行数
     */
    public int getContactCount() {
        String countQuery = "SELECT * FROM " + Contact.TABLE_NAME;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
     *
     * @param id update row id （需要更新的ID）
     * @param contact update value （去更新数据库的内容）
     * @return the number of rows affected (影响到的行数，如果没更新成功，返回0。所以当return 0时，需要告诉用户更新不成功)
     */
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

    /**
     *
     * @param id the database table row id need to delete(需要删除的数据库表中行的ID)
     * @return 返回影响到的行数，如果在 whereClause 有传入条件，返回该条件下影响到的行数，否则返回0。
     * 想要删除所有行，只要在 whereClause 传入 String "1"，并返回删除掉的行数总数（比如：删除了四行就返回4）
     */
    public int deleteContact(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int idReturnByDelete = db.delete(Contact.TABLE_NAME, Contact.COLUMN_ID + "=? ", new String[]{String.valueOf(id)});
        db.close();
        return idReturnByDelete;
    }

    /**
     * 删除所有行，whereClause 传入 String "1"
     * @return 返回删除掉的行数总数（比如：删除了四行就返回4）
     */
    public int deleteAllContact() {
        SQLiteDatabase db = getWritableDatabase();
        int idReturnByDelete = db.delete(Contact.TABLE_NAME, String.valueOf(1), null);
        db.close();
        return idReturnByDelete;
    }
}
