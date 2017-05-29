package stream_metab.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;

public class LooprateFields extends FieldMap {

    public static final String NAME_RESOURCE = "water";
    public static final String NAME_BEHAVIOR = "looprate";
    
    public static final String REQ_LINK_ID = "LINKID";
    public static final String REQ_BEDSLOPE = "BEDSLOPE";
    
    public static final Field[] reqFields = {
        new Field(REQ_LINK_ID, "TEXT", null, true),
        new Field(REQ_BEDSLOPE, "FLOAT", null, true),
    };
  
    @Override
    public Field[] getRequiredFieldList()
    {
        return reqFields;
    }

    @Override
    public String getResourceName()
    {
        return NAME_RESOURCE;
    }

    @Override
    public String getBehaviorName()
    {
        return NAME_BEHAVIOR;
    }
    
}
