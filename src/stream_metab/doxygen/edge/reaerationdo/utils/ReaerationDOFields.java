package stream_metab.doxygen.edge.reaerationdo.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import stream_metab.doxygen.edge.reaerationdo.*;

public class ReaerationDOFields extends FieldMap {

    public static final String NAME_RESOURCE = "doxygen";
    public static final String NAME_BEHAVIOR = "reaerationdo";
    
    public static final String REQ_K_SCHMIDT_600 = Doxygen.Names.K_SCHMIDT_600;
    
    public static final Field[] reqFields = {
        new Field(REQ_K_SCHMIDT_600, "FLOAT", null, true)
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
