package show.com.tron;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DataXmlImporter {

    /**
     * Method parsers an xml file in the exporter format and returns a list of Show objects
     * @param file the xml file to be parsed by the method
     * @return a list of Show objects contained in the document, empty list if none
     * @throws Exception throws exception if any weird thing happens, like out of format.
     */
    public static List<Show> importer(File file)throws Exception {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document =
                builder.parse(file);

        List<Show> importList = new ArrayList<>();

        NodeList nodeList = document.getDocumentElement().getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);
            if (node instanceof Element) {
                String name = ""; Day weekday = Day.OFFAIR; int noOfEpisodes =0, season =0, episode=0;


                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    Node cNode = childNodes.item(j);

                    if (cNode instanceof Element) {
                        String content = cNode.getLastChild().
                                getTextContent().trim();
                        switch (cNode.getNodeName()) {
                            case "name":
                                name = content;
                                break;
                            case "air_date":
                                weekday = Day.valueOf(content);
                                break;
                            case "no_episodes":
                                noOfEpisodes = Integer.valueOf(content);
                                break;
                            case "season":
                                season = Integer.valueOf(content);
                                break;
                            case "episode":
                                episode = Integer.valueOf(content);
                                break;
                        }
                    }
                }
                importList.add(new Show(name, weekday, noOfEpisodes, season, episode, Calendar.getInstance().getTime()));//TODO SAVE TIME
            }
        }
        return importList;
    }
}