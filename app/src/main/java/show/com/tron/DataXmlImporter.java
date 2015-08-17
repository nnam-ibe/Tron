//package show.com.tron;
//
//import android.content.Context;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlPullParserException;
//import org.xmlpull.v1.XmlPullParserFactory;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URL;
//
///**
// * Used to import shows from file into the db.
// */
//public class DataXmlImporter {
//
//        private String currTag = null;
//        private boolean firstTag = true;
//
//        public String parseXML(Context ctx) {
//            try {
//                StringBuilder sb = new StringBuilder(500);
//                sb.append("INSERT INTO " + DBHelper.SHOW_TABLE + " VALUES (");
//
//                XmlPullParserFactory xppFactory = XmlPullParserFactory.newInstance();
//                xppFactory.setNamespaceAware(true);
//                XmlPullParser xpp = xppFactory.newPullParser();
//
//                URL yourXmlPath = new URL(url);
//                InputStream is = yourXmlPath.openConnection().getInputStream();
//
//                xpp.setInput(is,null);
//
//                int e = xpp.getEventType();
//                while (e != XmlPullParser.END_DOCUMENT)
//                {
//                    if(e == XmlPullParser.START_TAG) {
//                        currTag = xpp.getName();
//                    }
//                    else if (e == XmlPullParser.END_TAG) {
//                        currTag = null;
//                    }
//                    else if (e == XmlPullParser.TEXT) {
//                        if ( currTag.equals("state") ) {    // first table column
//                            if (firstTag)
//                                sb.append( xmlText + "(" ); // for first row insert
//                            else
//                                sb.append( xmlText + ",(" );
//                        }
//
//                        else if ( currTagType.equals("district") ){
//                            sb.append( "'" + xmlText + "')" );  // last table column should have a closing paran ")"
//                        }
//                    }
//                    e = xpp.next();
//                }
//
//            }   catch (XmlPullParserException e) {
//                e.printStackTrace();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//
//            return sb.toString();
//        }
//}
