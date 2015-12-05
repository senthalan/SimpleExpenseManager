package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.presistentImp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentMemoryTransactionDAO implements TransactionDAO {
    private SQLiteDatabase database;

    public PersistentMemoryTransactionDAO(Context context) {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        ContentValues cv = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = sdf.format(date);
        cv.put(MySQLiteHelper.TRANSACTION_DATE, formattedDate);
        cv.put(MySQLiteHelper.ACCOUNT_NO, accountNo);
        cv.put(MySQLiteHelper.TRANSACTION_EXPENSE_TYPE, String.valueOf(expenseType));
        cv.put(MySQLiteHelper.TRANSACTION_AMOUNT, amount);
        database.insert(MySQLiteHelper.TABLE_TRANSACTION, null, cv);
        Log.d("db******", "date from an " + date);
        Log.d("db******","date to db "+formattedDate);
        Log.d("db******", "log added "+ accountNo);
    }


    @Override
    public List<Transaction> getAllTransactionLogs() {
        ArrayList<Transaction> allTransactionList=new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ MySQLiteHelper.TABLE_TRANSACTION,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            allTransactionList.add(cursorToTransaction(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("db******", "get all log ");
        return allTransactionList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        ArrayList<Transaction> paginatedTransactionList=new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ MySQLiteHelper.TABLE_TRANSACTION+" ORDER BY "+
                        MySQLiteHelper.TRANSACTION_DATE+" LIMIT ?", new String[]{String.valueOf(limit)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            paginatedTransactionList.add(cursorToTransaction(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("db******", "log added limited log ");
        return paginatedTransactionList;
    }

    private Transaction cursorToTransaction(Cursor cursor) {
        String trans_date=cursor.getString(0);
        Log.d("db******", "date from db "+ trans_date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date datcon = null;
        try {
            datcon = sdf.parse(trans_date);
            Log.d("db******", "formated date "+ datcon);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Transaction(datcon,cursor.getString(1),ExpenseType.valueOf(cursor.getString(2).toUpperCase()), cursor.getDouble(3));
    }
}
