package stream_metab.doxygen.patch.airdo.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import stream_metab.doxygen.patch.airdo.*;

public class AirDOFields extends FieldMap {

    public static final String NAME_RESOURCE = "doxygen";
    public static final String NAME_BEHAVIOR = "airdo";
    
    public static final String REQ_AIR_PRESSURE = BaroPressure.class.getSimpleName();
    public static final String REQ_PAR_TABLE_NAME = Doxygen.Names.PAR_TABLE;
    
    public static final Field[] reqFields = {
        new Field(REQ_AIR_PRESSURE, "FLOAT", null, true),
        new Field(REQ_PAR_TABLE_NAME, "TEXT", null, true)
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
