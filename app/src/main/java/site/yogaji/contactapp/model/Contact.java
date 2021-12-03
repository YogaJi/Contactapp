package site.yogaji.contactapp.model;

public class Contact {
    public static final String TABLE_NAME = "Contact";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_AVATAR = "avatar";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TELEPHONE = "telephone";
    public static final String COLUMN_ADDRESS = "address";

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_AVATAR + " INTEGER,"
            + COLUMN_TELEPHONE + " TEXT,"
            + COLUMN_ADDRESS + " TEXT"
            + ")";

    public static final String CREATE_COMPNAY_TABLE = "CREATE TABLE " + "Company" + "("
            + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT," + "name" + " TEXT,"
            + "telephone" + " TEXT," + "address" + " TEXT" + ")";

    private int id;
    private int avatar;
    private String name;
    private String telephone;
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

