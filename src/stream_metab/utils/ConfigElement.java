package stream_metab.utils;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ConfigElement {
    
    private Element element;
    
    public ConfigElement(Element element)
    {
        this.element = element;
    }
    
    public Element getElement()
    {
        return element;
    }
    
    public ConfigElement getFirstElementByTag(String tagName)
    {
        NodeList tagList = element.getElementsByTagName(tagName);
        if (tagList != null && tagList.getLength() > 0)
        {
            return new ConfigElement((Element)tagList.item(0));
        }
        else
        {
            return null;
        }
    }
    
    public String getTextContentForFirstTag(String tagName)
    {
        ConfigElement configElement = this.getFirstElementByTag(tagName);
        if (configElement != null)
        {
            return configElement.getElement().getTextContent();
        }
        else
        {
            return null;
        }
    }

}