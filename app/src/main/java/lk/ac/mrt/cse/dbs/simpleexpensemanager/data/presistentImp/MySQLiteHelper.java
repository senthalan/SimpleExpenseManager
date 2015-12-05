package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.presistentImp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper  extends SQLiteOpenHelper {

    //table for accounts
    public static final String TABLE_ACCOUNTS = "accounts";
    public static final String ACCOUNT_NO = "account_no";
    public static final String BANK_NAME = "bank_name";
    public static final String ACCOUNT_HOLDER_NAME = "account_holder_name";
    public static final String ACCOUNT_BALANCE = "account_balance";

    // Database creation sql statement
    private static final String CREATE_ACCOUNTS = "create table IF NOT EXISTS "
            + TABLE_ACCOUNTS + "(" + ACCOUNT_NO
            + " TEXT not null, " + BANK_NAME
            + " TEXT not null, " + ACCOUNT_HOLDER_NAME
            + " TEXT not null,"  + ACCOUNT_BALANCE
            + " DOUBLE );";


    //table for transaction
    public static final String TABLE_TRANSACTION = "transactions";
    public static final String TRANSACTION_DATE = "transaction_date";
    public static final String TRANSACTION_EXPENSE_TYPE = "transaction_expense_type";
    public static final String TRANSACTION_AMOUNT = "transaction_amount";


    // Database creation sql statement
    private static final String CREATE_TRANSACTIONS = "create table IF NOT EXISTS "
            + TABLE_TRANSACTION + "(" + TRANSACTION_DATE
            + " DATE not null, " + ACCOUNT_NO
            + " TEXT not null, " + TRANSACTION_EXPENSE_TYPE
            + " TEXT not null,"  + TRANSACTION_AMOUNT
            + " DOUBLE not null);";


    public MySQLiteHelper(Context context) {
        super(context,"ExpenseManager.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACCOUNTS);
        db.execSQL(CREATE_TRANSACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

