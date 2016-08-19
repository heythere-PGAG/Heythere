package in.heythere.heythere.Utilities;


import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;

public class Tools {
    public static final String EVENT_ID = "e_id";
    public static final String EVENT_NAME = "e_name";
    public static final String EVENT_VENUE = "e_venue";
    public static final String EVENT_CITY = "e_city";
    public static final String EVENT_DATE = "e_date";
    public static final String EVENT_INTERESTED = "e_interested";
    public static final String LIKE_COUNT = "e_like_count";
    public static final String EVENT_CATEGORY = "e_category";
    public static final String CREATED_DATE = "e_created_date";
    public static final String EVENT_POSTER = "e_poster";
    public static final String TABLE_NAME = "events";


    public static final String HOME_URL = "http://www.heythere.in/";

    public static final String CONTENT_AUTHORITY = "in.heythere.heythere.providers";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_ENTRIES = "entries";

    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.providers.entries";
    /**
     * MIME type for individual entries.
     */
    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.providers.entry";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_ENTRIES).build();

    public static final String pref = "PREFERENCES";
    public static final String sharedUserIdMap = "someNotNeeded";
    public static final String login_boolean = "loginBoolean";

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = null;
        if (bitmap != null) {
            output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            RectF rectF = new RectF(rect);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF,10,10,paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
        }
        return output;
    }

}
