package android.database;

import java.io.Closeable;

public interface Cursor extends Closeable {
    int FIELD_TYPE_NULL = 0;
    int FIELD_TYPE_INTEGER = 1;
    int FIELD_TYPE_FLOAT = 2;
    int FIELD_TYPE_STRING = 3;
    int FIELD_TYPE_BLOB = 4;

    boolean moveToNext();

    boolean moveToFirst();

    int getColumnCount();

    String getColumnName(int columnIndex);

    int getColumnIndex(String columnName);

    int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException;

    int getType(int columnIndex);

    String getString(int columnIndex);

    long getLong(int columnIndex);

    double getDouble(int columnIndex);

    boolean isNull(int columnIndex);

    void close();
}
