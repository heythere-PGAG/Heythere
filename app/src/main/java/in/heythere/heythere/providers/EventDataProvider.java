package in.heythere.heythere.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import in.heythere.heythere.Utilities.SelectionBuilder;
import in.heythere.heythere.Utilities.Tools;

public class EventDataProvider extends ContentProvider {

    EventDatabase database;

    private static final String AUTHORITY = Tools.CONTENT_AUTHORITY;

    public static final int ROUTE_ENTRIES = 1;

    public static final int ROUTE_ENTRIES_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "entries", ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "entries/*", ROUTE_ENTRIES_ID);
    }

    @Override
    public boolean onCreate() {
        database = new EventDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ENTRIES:
                return Tools.CONTENT_TYPE;
            case ROUTE_ENTRIES_ID:
                return Tools.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = database.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_ENTRIES_ID:
                // Return a single entry, by ID.
                String id = uri.getLastPathSegment();
                builder.where(Tools.EVENT_ID + "=?", id);
            case ROUTE_ENTRIES:
                // Return all known entries.
                builder.table(Tools.TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = database.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case ROUTE_ENTRIES:
                long id = db.insertOrThrow(Tools.TABLE_NAME, null, values);
                result = Uri.parse(Tools.CONTENT_URI + "/" + id);
                break;
            case ROUTE_ENTRIES_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = database.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(Tools.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(Tools.TABLE_NAME)
                        .where(Tools.EVENT_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    public static class EventDatabase extends SQLiteOpenHelper{

        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "events.db";

        private static final String TYPE_TEXT = " TEXT";
        private static final String TYPE_INTEGER = " INTEGER";
        private static final String COMMA_SEP = ",";

        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Tools.TABLE_NAME + " (" +
                        Tools.EVENT_ID + " INTEGER PRIMARY KEY," +
                        Tools.EVENT_NAME + TYPE_TEXT + COMMA_SEP +
                        Tools.EVENT_VENUE    + TYPE_TEXT + COMMA_SEP +
                        Tools.EVENT_CITY + TYPE_TEXT + COMMA_SEP +
                        Tools.EVENT_DATE + " DATETIME" + COMMA_SEP +
                        Tools.LIKE_COUNT + TYPE_INTEGER + COMMA_SEP +
                        Tools.EVENT_POSTER + TYPE_TEXT + COMMA_SEP +
                        Tools.EVENT_INTERESTED + TYPE_TEXT + COMMA_SEP+
                        Tools.CREATED_DATE + " DATETIME" + COMMA_SEP+
                        Tools.EVENT_CATEGORY+ TYPE_TEXT +");";

        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Tools.TABLE_NAME;

        public EventDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}
