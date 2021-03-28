package com.smart.framework;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SmartTable {

    private SQLiteDatabase db;

    private String tblName;
    private int colCount;
    private String[] colNames;

    public SmartTable(SQLiteDatabase db, String tblName) {
        this.db = db;
        this.tblName = tblName;

        Cursor cur = this.db.rawQuery("select * from " + tblName, new String[0]);
        this.colCount = cur.getColumnCount();
        this.colNames = new String[cur.getColumnCount()];
        cur.moveToFirst();

        for (int idx = 0; idx < this.colCount; idx++) {
            colNames[idx] = cur.getColumnName(idx);
        }

        cur.close();
    }

    public String getTableName() {
        return this.tblName;
    }


    public ArrayList<ContentValues> readRowSQL(String rawSQL, String[] rawSQLSelectionArguments) {
        ArrayList<ContentValues> rows = new ArrayList<ContentValues>();
        Cursor cursor = this.db.rawQuery(rawSQL, rawSQLSelectionArguments);
        this.colCount = cursor.getColumnCount();
        if (cursor.moveToFirst()) {
            do {
                ContentValues row = new ContentValues();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    row.put(cursor.getColumnName(i), cursor.getString(i));
                }
                rows.add(row);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return rows;
    }


}
