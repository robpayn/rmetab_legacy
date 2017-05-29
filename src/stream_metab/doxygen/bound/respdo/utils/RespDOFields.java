package stream_metab.doxygen.bound.respdo.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import stream_metab.doxygen.bound.respdo.*;

public class RespDOFields extends FieldMap {

    public static final String NAME_RESOURCE = "doxygen";
    public static final String NAME_BEHAVIOR = "respdo";
    
    public static final String REQ_DAILY_RESP_RATE = Doxygen.Names.DAILY_RESP_RATE;
    
    public static final Field[] reqFields = {
        new Field(REQ_DAILY_RESP_RATE, "FLOAT", null, true)
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
