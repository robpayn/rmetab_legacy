package stream_metab.doxygen.bound.proddo.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import stream_metab.doxygen.bound.proddo.*;

public class ProdDOFields extends FieldMap {

    public static final String NAME_RESOURCE = "doxygen";
    public static final String NAME_BEHAVIOR = "proddo";
    
    public static final String REQ_P_TO_PAR_RATIO = Doxygen.Names.P_TO_PAR_RATIO;
    
    public static final Field[] reqFields = {
        new Field(REQ_P_TO_PAR_RATIO, "FLOAT", null, true)
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
