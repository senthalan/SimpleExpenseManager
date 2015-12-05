package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.presistentImp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentMemoryAccountDAO implements AccountDAO {

    private SQLiteDatabase database;

    public PersistentMemoryAccountDAO(Context context) {
        MySQLiteHelper dbHelper = new MySQLiteHelper(context);
        database = dbHelper.getWritableDatabase();
    }


    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> accountNoList=new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT "+MySQLiteHelper.ACCOUNT_NO+" FROM "+ MySQLiteHelper.TABLE_ACCOUNTS,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            accountNoList.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("db******", "get account no list");
        return accountNoList;
    }

    @Override
    public List<Account> getAccountsList() {
        ArrayList<Account> accountNoList=new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ MySQLiteHelper.TABLE_ACCOUNTS,null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            accountNoList.add(cursorToAccount(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("db******", "get account list");
        return accountNoList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor cursor = database.rawQuery("SELECT * FROM "+ MySQLiteHelper.TABLE_ACCOUNTS + " WHERE "+MySQLiteHelper.ACCOUNT_NO+"=?;", new String[]{accountNo});
        cursor.moveToFirst();
        Account acc;
        if  (!cursor.isAfterLast()) {
            acc= cursorToAccount(cursor);
            cursor.close();
            Log.d("db******", "get account of "+accountNo);
            return acc;
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        ContentValues cv = new ContentValues();
        cv.put(MySQLiteHelper.ACCOUNT_NO, account.getAccountNo());
        cv.put(MySQLiteHelper.BANK_NAME, account.getBankName());
        cv.put(MySQLiteHelper.ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        cv.put(MySQLiteHelper.ACCOUNT_BALANCE, account.getBalance());
        Log.d("db******", "added " + account.getAccountNo());
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!getAccountNumbersList().contains(accountNo)){
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        database.delete(MySQLiteHelper.TABLE_ACCOUNTS, MySQLiteHelper.ACCOUNT_NO
                + " = ?", new String[]{accountNo});
        Log.d("db******", "removed "+accountNo);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account=getAccount(accountNo);
        switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        database.rawQuery("UPDATE " + MySQLiteHelper.TABLE_ACCOUNTS + " SET " + MySQLiteHelper.ACCOUNT_BALANCE + "=? WHERE "+MySQLiteHelper.ACCOUNT_NO+"=?;", new String[]{String.valueOf(account.getBalance()),accountNo});
        Log.d("db******", "updated");
    }


    private Account cursorToAccount(Cursor cursor) {
        return new Account(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getDouble(3));
    }
}
