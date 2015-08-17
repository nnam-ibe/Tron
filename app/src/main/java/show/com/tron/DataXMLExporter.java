package show.com.tron;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

/**
 * Used to export current db to parseable xml file.
 */

public class DataXmlExporter {

    private static final String DATASUBDIRECTORY = "Tron";

    private SQLiteDatabase db;
    private XmlBuilder xmlBuilder;

    public DataXmlExporter(SQLiteDatabase db) {
        this.db = db;
    }

    public void export(String exportFileNamePrefix) throws IOException {
        exportFileNamePrefix += new SimpleDateFormat("yyMMddHHmmss").format(Calendar.getInstance().getTime());
        Log.i("DataXmlExporter", "exporting shows table" + " exportFileNamePrefix=" + exportFileNamePrefix);

        this.xmlBuilder = new XmlBuilder();
        this.xmlBuilder.start();

        // get the tables
        String sql = "select * from sqlite_master";
        Cursor c = this.db.rawQuery(sql, new String[0]);
        Log.d("DataXmlExporter", "select * from sqlite_master, cur size " + c.getCount());
        if (c.moveToFirst()) {
            do {
                String tableName = c.getString(c.getColumnIndex("name"));
                Log.d("DataXmlExporter", "table name " + tableName);

                if (tableName.equals("shows")) {
                    this.exportTable(tableName);
                }
            } while (c.moveToNext());
        }
        String xmlString = this.xmlBuilder.end();
        this.writeToFile(xmlString, exportFileNamePrefix + ".xml");
        Log.i("DataXmlExporter", "exporting database complete");
    }

    private void exportTable(final String tableName) throws IOException {
        Log.d("DataXmlExporter", "exporting table - " + tableName);
        String sql = "select * from " + tableName;
        Cursor c = this.db.rawQuery(sql, new String[0]);
        if (c.moveToFirst()) {
            int cols = c.getColumnCount();
            do {
                this.xmlBuilder.openRow();
                for (int i = 0; i < cols; i++) {
                    this.xmlBuilder.addColumn(c.getColumnName(i), c.getString(i));
                }
                this.xmlBuilder.closeRow();
            } while (c.moveToNext());
        }
        c.close();
    }

    private void writeToFile(String xmlString, String exportFileName) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory(), DATASUBDIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, exportFileName);
        file.createNewFile();

        ByteBuffer buff = ByteBuffer.wrap(xmlString.getBytes());
        FileChannel channel = new FileOutputStream(file).getChannel();
        try {
            channel.write(buff);
        } finally {
            if (channel != null)
                channel.close();
        }
    }

    class XmlBuilder {
        private static final String ROW_OPEN = "<show>";
        private static final String ROW_CLOSE = "</show>";
        private static final String DOC_OPENING_TAG = "<shows>";
        private static final String DOC_CLOSING_TAG = "</shows>";
        private static final String OPENING_TAG_START = "<";
        private static final String TAG_END = ">";
        private static final String CLOSING_TAG_START = "</";

        private final StringBuilder sb;

        public XmlBuilder() throws IOException {
            this.sb = new StringBuilder();
        }

        void start() {
            this.sb.append(DOC_OPENING_TAG);
        }

        String end() throws IOException {
            this.sb.append(DOC_CLOSING_TAG);
            return this.sb.toString();
        }

        void openRow() {
            this.sb.append(ROW_OPEN);
        }

        void closeRow() {
            this.sb.append(ROW_CLOSE);
        }

        void addColumn(final String name, final String val) throws IOException {
            this.sb.append(OPENING_TAG_START + name + TAG_END + val + CLOSING_TAG_START + name + TAG_END);
        }
    }

}
