package stream_metab.utils;

import org.neosimulation.apps.modelmanager.neov1.NEOModelManager;
import org.neosimulation.apps.modelmanager.neov1.strategy.metaparam.TimeSeriesExtractor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DataExtractor {
    
    public static final String KEY_SOURCE_FILE_NAME = "path";

    public static final String KEY_TIME_DATUM = "timeDatum";
    public static final String KEY_START_TIME = "startTime";
    public static final String KEY_STOP_TIME = "stopTime";
    public static final String KEY_FILE_COUNT = "numFiles";

    public static final String KEY_FILE = "file.";

    public static final String KEY_STATE_COUNT = "numStates";

    public static final String KEY_STATE = "state.";
    
    public static final String KEY_EXTRACTOR = "extractor";

    private ConfigElement element;

    public DataExtractor(ConfigElement element)
    {
        this.element = element;
    }

    public void run(NEOModelManager modelManager) throws Exception
    {
        String timeDatum = element.getTextContentForFirstTag(KEY_TIME_DATUM);
        String startTime = element.getTextContentForFirstTag(KEY_START_TIME);
        String stopTime = element.getTextContentForFirstTag(KEY_STOP_TIME);
        NodeList fileList = element.getElement().getElementsByTagName("file");
        for (int i = 0; i < fileList.getLength(); i++)
        {
            Element fileElem = (Element)fileList.item(i);
            String filePath = fileElem.getAttribute(KEY_SOURCE_FILE_NAME);
            NodeList stateList = fileElem.getElementsByTagName("state");
            for (int j = 0; j < stateList.getLength(); j++)
            {
                ConfigElement stateElem = new ConfigElement((Element)stateList.item(j));
                String stateName = stateElem.getElement().getAttribute("name");
                String extractorType = stateElem.getElement().getAttribute(KEY_EXTRACTOR);
                TimeSeriesExtractor extractor = null;
                
                if(extractorType.equals("SiteExtractor"))
                {
                    extractor = new SiteExtractor("extractedData", stateElem.getElement(),
                            timeDatum, startTime, stopTime, filePath);
                }
                else if(extractorType.equals("InstrumentExtractor"))
                {
                    extractor = new InstrumentExtractor("extractedData", stateElem.getElement(),
                            timeDatum, startTime, stopTime, filePath);
                }
                else if(extractorType.equals("TheoreticalPARExtractor"))
                {
                    extractor = new TheoreticalPARExtractor("extractedData", stateElem.getElement(),
                            timeDatum, startTime, stopTime, filePath);
                }
                
                if (extractor != null)
                {
                    extractor.applyParam(modelManager);
                }
                else
                {
                    throw new Exception("Invalid extractor specified.");
                }
            }
        }
    }

}
