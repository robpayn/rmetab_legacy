package stream_metab.doxygen.edge.advectdo.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import stream_metab.doxygen.edge.reaerationdo.*;

public class AdvectDOFields extends FieldMap {

    public static final String NAME_RESOURCE = "doxygen";
    public static final String NAME_BEHAVIOR = "advectdo";
    
    public static final Field[] reqFields = {};
    
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
