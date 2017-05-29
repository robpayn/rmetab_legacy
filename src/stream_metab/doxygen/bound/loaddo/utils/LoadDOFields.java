package stream_metab.doxygen.bound.loaddo.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import stream_metab.doxygen.bound.loaddo.*;

public class LoadDOFields extends FieldMap {

    public static final String NAME_RESOURCE = "doxygen";
    public static final String NAME_BEHAVIOR = "loaddo";
    
    public static final String REQ_TABLE_DO_CONC = Doxygen.Names.TABLE_DO_CONC;
    public static final String REQ_TABLE_TEMP = Doxygen.Names.TABLE_TEMP;
    
    public static final Field[] reqFields = {
        new Field(REQ_TABLE_DO_CONC, "TEXT", null, true),
        new Field(REQ_TABLE_TEMP, "TEXT", null, true)
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
