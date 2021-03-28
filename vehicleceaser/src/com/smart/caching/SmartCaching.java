package com.smart.caching;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.smart.framework.SmartApplication;
import com.smart.framework.SmartDataHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This Class Contains Method IjoomerCaching.
 *
 * @author tasol
 */

public final class SmartCaching {

    private static final String ID = "id";
    private static final String TAG = "SmartCaching";
    private Context context;

    public SmartCaching(Context context) {
        this.context = context;
    }

    public HashMap<String, ArrayList<ContentValues>> parseResponse(Object json, String tableName) {
        final HashMap<String, ArrayList<ContentValues>> mapTableNameAndData = new HashMap<>();
        cacheResponse(mapTableNameAndData, json, tableName, false, true, null, null);
        return mapTableNameAndData;
    }

    public HashMap<String, ArrayList<ContentValues>> parseResponse(Object json, String tableName, String... unNormalizedFields) {

        final HashMap<String, ArrayList<ContentValues>> mapTableNameAndData = new HashMap<>();

        cacheResponse(mapTableNameAndData, json, tableName, false, true, null, null, unNormalizedFields);

        return mapTableNameAndData;
    }

    public HashMap<String, ArrayList<ContentValues>> cacheResponse(Object json, String tableName) {

        final HashMap<String, ArrayList<ContentValues>> mapTableNameAndData = new HashMap<>();

        cacheResponse(mapTableNameAndData, json, tableName, false, false, null, null);

        return mapTableNameAndData;

    }

    private HashMap<String, ArrayList<ContentValues>> cacheResponse(Object json, boolean shouldParseOnly) {

        final HashMap<String, ArrayList<ContentValues>> mapTableNameAndData = new HashMap<>();

        cacheResponse(mapTableNameAndData, json, null, false, shouldParseOnly, null, null);

        return mapTableNameAndData;

    }

    public HashMap<String, ArrayList<ContentValues>> cacheResponse(Object json, String tableName, boolean shouldDeleteOldRecords) {
        final HashMap<String, ArrayList<ContentValues>> mapTableNameAndData = new HashMap<>();

        cacheResponse(mapTableNameAndData, json, tableName, shouldDeleteOldRecords, false, null, null, "");

        return mapTableNameAndData;
    }

    public HashMap<String, ArrayList<ContentValues>> cacheResponse(Object json, String tableName, String... unNormalizedFields) {
        final HashMap<String, ArrayList<ContentValues>> mapTableNameAndData = new HashMap<>();

        cacheResponse(mapTableNameAndData, json, tableName, false, false, null, null, unNormalizedFields);

        return mapTableNameAndData;
    }

    public HashMap<String, ArrayList<ContentValues>> cacheResponse(Object json, boolean shouldParseOnly, String... unNormalizedFields) {
        final HashMap<String, ArrayList<ContentValues>> mapTableNameAndData = new HashMap<>();

        cacheResponse(mapTableNameAndData, json, null, false, shouldParseOnly, null, null, unNormalizedFields);

        return mapTableNameAndData;
    }

    public void cacheResponse(final Object json, final String tableName, final boolean shouldDeleteOldRecords, final OnResponseParsedListener onResponseParsedListener, final String... unNormalizedFields) {
        final HashMap<String, ArrayList<ContentValues>> mapTableNameAndData = new HashMap<>();

        cacheResponse(mapTableNameAndData, json, tableName, shouldDeleteOldRecords, false, null, null, unNormalizedFields);
        onResponseParsedListener.onParsed(mapTableNameAndData);
//        ((Activity) context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });


    }

    private void cacheResponse(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData, Object json, String tableName, boolean shouldDeleteOldRecords, boolean shouldParseOnly, ArrayList<String> parentColumnNames, ContentValues parentContentValues, @Nullable String... unNormalizedFields) {
        ArrayList<String> primaryColumnNames = new ArrayList<>();
        ContentValues primaryContentValues = new ContentValues();
        ArrayList<String> columnNames = new ArrayList<>();

        ArrayList<ContentValues> tableData = new ArrayList<>();

        // In recursive call just use the parent key in sub table
        if (isOldValuesExists(parentColumnNames)) {
            primaryColumnNames = parentColumnNames;
            primaryContentValues = parentContentValues;
        }

        try {
            if (json instanceof JSONObject) {
                cacheResponse(mapTableNameAndData, tableData, (JSONObject) json, tableName, primaryColumnNames, columnNames, primaryContentValues, shouldDeleteOldRecords, shouldParseOnly, unNormalizedFields);
            } else if (json instanceof JSONArray) {
                cacheResponse(mapTableNameAndData, tableData, (JSONArray) json, tableName, primaryColumnNames, columnNames, primaryContentValues, shouldDeleteOldRecords, shouldParseOnly, unNormalizedFields);
            } else {
                Log.d(TAG, " Invalid json format found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cacheResponse(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData, ArrayList<ContentValues> tableData, JSONObject jsonObject, String tableName, ArrayList<String> primaryColumnNames, ArrayList<String> columnNames, ContentValues primaryContentValues, boolean shouldDeleteOldRecords, boolean shouldParseOnly, String... unNormalizeFields) {
        populateSchemaAndValues(tableName, mapTableNameAndData, tableData, jsonObject, primaryColumnNames, columnNames, primaryContentValues, shouldDeleteOldRecords, shouldParseOnly, true, unNormalizeFields);

        if (mapTableNameAndData != null) {
            if (mapTableNameAndData.keySet().contains(tableName)) {
                mapTableNameAndData.get(tableName).addAll(tableData);
            } else {
                mapTableNameAndData.put(tableName, tableData);
            }
        }

        if (shouldParseOnly) {
            return;
        }

        if (createTable(tableName, primaryColumnNames, columnNames, shouldDeleteOldRecords)) {
            insertIntoTable(tableData, tableName, primaryColumnNames, columnNames, shouldDeleteOldRecords);
        }
    }

    private void cacheResponse(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData, ArrayList<ContentValues> tableData, JSONArray jsonArray, String tableName, ArrayList<String> primaryColumnNames, ArrayList<String> columnNames, ContentValues primaryContentValues, boolean shouldDeleteOldRecords, boolean shouldParseOnly, String... unNormalizeFields) {
        try {

            // Handle empty json array
            if (jsonArray == null || jsonArray.length() <= 0) {
                return;
            }

            int size = jsonArray.length();
            for (int i = 0; i < size; i++) {
                populateSchemaAndValues(tableName, mapTableNameAndData, tableData, jsonArray.getJSONObject(i), primaryColumnNames, columnNames, primaryContentValues, shouldDeleteOldRecords, shouldParseOnly, true, unNormalizeFields);
            }

            if (mapTableNameAndData != null) {
                if (mapTableNameAndData.keySet().contains(tableName)) {
                    mapTableNameAndData.get(tableName).addAll(tableData);
                } else {
                    mapTableNameAndData.put(tableName, tableData);
                }
            }

            if (shouldParseOnly) {
                return;
            }

            if (createTable(tableName, primaryColumnNames, columnNames, shouldDeleteOldRecords)) {
                insertIntoTable(tableData, tableName, primaryColumnNames, columnNames, shouldDeleteOldRecords);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void populateSchemaAndValues(String tableName, HashMap<String, ArrayList<ContentValues>> mapTableNameAndData, ArrayList<ContentValues> tableData, JSONObject jsonObject, ArrayList<String> primaryColumnNames, ArrayList<String> columnNames, ContentValues primaryContentValues, boolean shouldDeleteOldRecords, boolean shouldParseOnly, boolean shouldCategorizePrimaryKey, String... unNormalizedFields) {
        Iterator<String> keys = jsonObject.keys();
        // Create content value for a single row
        ContentValues row;
        if (primaryContentValues != null && primaryContentValues.size() > 0) {
            row = primaryContentValues;
        } else {
            row = new ContentValues();
        }
        while (keys.hasNext()) {

            String key = keys.next();
            try {
                // If json array is found in un normalized field then do not make normalize table of it just keep it as key-value pair in database
                if (jsonObject.get(key) instanceof JSONArray && isKeyNormalized(key, unNormalizedFields)) {

                    ArrayList<String> originalParentColumnNames = new ArrayList<>();
                    ArrayList<String> parentColumnNames = new ArrayList<>();
                    ContentValues parentContentValues = new ContentValues();
                    categorizeParentKey(tableName, jsonObject, originalParentColumnNames, parentColumnNames);
                    categorizeParentContentValues(jsonObject, originalParentColumnNames, parentColumnNames, parentContentValues);

                    cacheResponse(mapTableNameAndData, jsonObject.get(key), key, shouldDeleteOldRecords, shouldParseOnly, parentColumnNames, parentContentValues, unNormalizedFields);

                    // If json object is found in un normalized field then do not add fields of it in the parent table just keep it as key-value pair in database
                } else if (jsonObject.get(key) instanceof JSONObject && isKeyNormalized(key, unNormalizedFields)) {

                    populateSchemaAndValues(tableName, mapTableNameAndData, tableData, jsonObject.getJSONObject(key), primaryColumnNames, columnNames, primaryContentValues, shouldDeleteOldRecords, shouldParseOnly, false, unNormalizedFields);

                    // For normal key-value pair
                } else {

                    categorizeKey(key, primaryColumnNames, columnNames, shouldCategorizePrimaryKey);
                    setKeyValue(jsonObject, key, row);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // add a filled row in the row array

        if (row != null && row.size() > 0) {

            tableData.add(row);
        }
    }


    private void categorizeKey(String key, ArrayList<String> primaryColumnNames, ArrayList<String> columnNames, boolean shouldCategorizePrimaryKey) {

        // In case of nested json object if the nested object has the primary key then do not add that key in the parent's primary keys
        if (isPrimaryKey(key) && shouldCategorizePrimaryKey) {

            if (!primaryColumnNames.contains(key)) {

                primaryColumnNames.add(key);
            }

        } else {

            if (!columnNames.contains(key)) {

                columnNames.add(key);
            }

        }
    }

    private void setKeyValue(JSONObject jsonObject, String key, ContentValues row) {
        try {
            row.put(key, jsonObject.getString(key));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void categorizeParentKey(String tableName, JSONObject jsonObject, ArrayList<String> originalParentColumnNames, ArrayList<String> parentColumnNames) {

        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {

            String key = keys.next();
            if (isPrimaryKey(key)) {

                if (!parentColumnNames.contains(key)) {

                    originalParentColumnNames.add(key);
                    parentColumnNames.add(tableName + "_" + key);
                }

            }
        }
    }

    private void categorizeParentContentValues(JSONObject jsonObject, ArrayList<String> originalParentColumnNames, ArrayList<String> primaryColumnNames, ContentValues primaryContentValues) {
        int size = primaryColumnNames.size();
        for (int i = 0; i < size; i++) {

            try {

                primaryContentValues.put(primaryColumnNames.get(i), jsonObject.getString(originalParentColumnNames.get(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean isPrimaryKey(String key) {

        return key.toLowerCase().endsWith(ID);
    }

    private boolean isOldValuesExists(ArrayList<String> oldPrimaryColumnNames) {

        return oldPrimaryColumnNames != null && oldPrimaryColumnNames.size() > 0;
    }


    private boolean isKeyNormalized(String key, String[] unNormalizedFields) {

        if (unNormalizedFields == null || unNormalizedFields.length <= 0) {

            return true;
        }

        for (String field : unNormalizedFields) {

            if (field.equalsIgnoreCase(key)) {
                return false;
            }
        }

        return true;
    }

    public void createOrInsertData(ArrayList<ContentValues> dataToInsert, boolean shouldDeleteOldRecords, String tableName) {
        ArrayList<String> primaryColumnNames = new ArrayList<>();
        ArrayList<String> columnNames = new ArrayList<>();

        Set<Map.Entry<String, Object>> s = dataToInsert.get(0).valueSet();
        Iterator itr = s.iterator();

        while (itr.hasNext()) {
            Map.Entry me = (Map.Entry) itr.next();
            String key = me.getKey().toString();

            if (isPrimaryKey(key)) {
                primaryColumnNames.add(key);
            } else {
                columnNames.add(key);
            }
        }

        createTable(tableName, primaryColumnNames, columnNames, shouldDeleteOldRecords);
        insertIntoTable(dataToInsert, tableName, primaryColumnNames, columnNames, shouldDeleteOldRecords);
    }

    public boolean createTable(String tableName, ArrayList<String> primaryColumnNames, ArrayList<String> columnNames,
                               boolean shouldDeleteOldRecords) {

        if ((tableName == null || tableName.trim().length() <= 0 || columnNames == null || columnNames.size() <= 0) && (primaryColumnNames == null || primaryColumnNames.size() <= 0)) {
            return false;
        }

        // Delete all records if user provide provision for the same
        if (shouldDeleteOldRecords) {
            dropTable(tableName);
        }

        StringBuffer query = new StringBuffer("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

		/*add columns*/

        int size = primaryColumnNames.size();
        for (int i = 0; i < size; i++) {

            query.append(primaryColumnNames.get(i));
            query.append(" TEXT");
            if (i < (size - 1)) {

                query.append(",");
            }

        }

        // if primary column name added then put coma(,) before adding other column names
        if (primaryColumnNames.size() > 0 && columnNames.size() > 0) {

            query.append(",");
        }

        size = columnNames.size();
        for (int i = 0; i < size; i++) {

            query.append(columnNames.get(i));
            query.append(" TEXT");
            if (i < (size - 1)) {

                query.append(",");
            }
        }

		/*add primary key constraint*/
        if (primaryColumnNames.size() > 0) {

            query.append(",");
            query.append(" PRIMARY KEY (");
            size = primaryColumnNames.size();
            for (int i = 0; i < size; i++) {

                query.append(primaryColumnNames.get(i));
                if (i < (size - 1)) {

                    query.append(",");
                }

            }
            query.append(")");
        }

        query.append(")");

        Log.d(TAG, "Create Table: " + query);
        getDB().execSQL(query.toString());
        getDBHelper().addTable(tableName);
        return true;
    }

    // Insert into table
    public void insertIntoTable(ArrayList<ContentValues> table, String tableName, ArrayList<String> primaryColumnNames, ArrayList<String> columnNames, boolean shouldDeleteOldRecords) {
        if ((tableName == null || tableName.trim().length() <= 0 || table == null || table.size() <= 0)) {
            return;
        }
        getDB().beginTransaction();
        try {
            if (table != null && table.size() > 0) {
                int size = table.size();
                for (int i = 0; i < size; i++) {
                    ContentValues row = table.get(i);
                    getDB().insertWithOnConflict(tableName, null, row, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }

            int size = table.size();
            for (int i = 0; i < size; i++) {
                Log.d(TAG, "Content values row " + i + " : " + table.get(i));
            }

            getDB().setTransactionSuccessful();
            getDB().endTransaction();
            // If any new column is added then handle that exception and create new table with new schema
        } catch (SQLiteException e) {
            if (e.getMessage().contains("has no column named")) {
                Log.d(TAG, " Table " + tableName + " has been altered");
                dropTable(tableName);
                createTable(tableName, primaryColumnNames, columnNames, shouldDeleteOldRecords);
                insertIntoTable(table, tableName, primaryColumnNames, columnNames, shouldDeleteOldRecords);
            }
        }
    }

    public long insertIntoTable(String tableName, ArrayList<ContentValues> rows) {
        return insertIntoTable(tableName, rows, null);
    }

    public long insertIntoTable(String tableName, ArrayList<ContentValues> rows, String nullColumnHack) {
        long rowID = 0;
        getDB().beginTransaction();
        if (rows != null && rows.size() > 0) {
            for (ContentValues row : rows) {
                rowID = getDB().insertWithOnConflict(tableName, nullColumnHack, row, SQLiteDatabase.CONFLICT_REPLACE);
            }
        }
        getDB().setTransactionSuccessful();
        getDB().endTransaction();
        return rowID;
    }

    public boolean deleteDataFromCache(String query) {
        try {
            getDB().execSQL(query);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public void updateTable(String tableName, ArrayList<ContentValues> rows) {
        // Just insert the updated content values
        insertIntoTable(tableName, rows);
    }

    private void dropTable(String tableName) {
        String query = "DROP TABLE IF EXISTS " + tableName;
        getDB().execSQL(query);
    }

    /**
     * This method used to get data from table.
     *
     * @param tableName represented database table name
     * @return {@link ArrayList < HashMap < String ,  String >>}
     */
    public ArrayList<ContentValues> getDataFromCache(String tableName) {
//        SmartUtils.showProgressDialog(context, null, false);
        try {
            ArrayList<ContentValues> tableRows = getDBHelper().getTableList().get(tableName).readRowSQL("SELECT * FROM " + tableName + "", null);
//            SmartUtils.hideProgressDialog();
            return tableRows;
        } catch (Throwable e) {
//            SmartUtils.hideProgressDialog();
            e.printStackTrace();
        }
        return null;
    }

    //Getting all the fields of the vehicle from table
    public ArrayList<ContentValues> getDataFromCacheForAdmin(String tableName) {
        try {
//            return getDBHelper().getTableList().get(tableName)
//                    .readRowSQL("SELECT * FROM " + tableName + " where " + searchType + " LIKE '%" + searchItem + "%'", null);
            return getDBHelper().getTableList().get(tableName)
                    .readRowSQL("SELECT * FROM " + tableName, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    //Getting all the fields of the vehicle from table
    public ArrayList<ContentValues> getDataFromCacheForExec(String tableName) {
        try {
//            return getDBHelper().getTableList().get(tableName).
//                    readRowSQL("SELECT reg_no,chasis_no,engine_no,asset_desc FROM " + tableName + " where " + searchType + " LIKE '%" + searchItem + "%'", null);
            return getDBHelper().getTableList().get(tableName).
                    readRowSQL("SELECT reg_no,chasis_no,engine_no,asset_desc FROM " + tableName, null);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method used to update table.
     *
     * @param query represented update query
     */
    public void updateTable(String query) {
        getDB().execSQL(query);
    }

    /**
     * This method used to delete from table or not.
     *
     * @param query represented database query
     * @return {@link boolean}
     */
    public boolean deleteFromTable(String query) {
        try {
            getDB().execSQL(query);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    private SQLiteDatabase getDB() {

        return SmartApplication.REF_SMART_APPLICATION.getDataHelper().getDB();
    }

    private SmartDataHelper getDBHelper() {

        return SmartApplication.REF_SMART_APPLICATION.getDataHelper();
    }


    /**
     * This method used to reset database
     */
    public void resetDataBase() {
        String Query = "select 'drop table  ' || name || ';' from sqlite_master where type = 'table'";
        Cursor c = getDB().rawQuery(Query, null);
        try {
            c.moveToFirst();
            while (c.getPosition() < c.getCount()) {
                if ((!c.getString(0).contains("android_metadata")) && (!c.getString(0).contains("applicationConfig")) && (!c.getString(0).contains("menus"))) {
                    getDB().execSQL(c.getString(0));
                }
                c.moveToNext();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        c.close();
    }

    public interface OnResponseParsedListener {
        void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData);
    }
}