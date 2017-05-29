package stream_metab.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;

public class FlowFields extends FieldMap {

    public static final String NAME_RESOURCE = "water";
    public static final String NAME_BEHAVIOR = "flow";
    
    public static final String REQ_FLOW_DATA_FILE = "FLOWFILE";
    
    public static final Field[] reqFields = {
        new Field(REQ_FLOW_DATA_FILE, "TEXT", null, true),
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
