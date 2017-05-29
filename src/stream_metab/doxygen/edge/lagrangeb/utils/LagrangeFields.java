package stream_metab.doxygen.edge.lagrangeb.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import stream_metab.doxygen.edge.lagrangeb.*;

public class LagrangeFields extends FieldMap {

    public static final String NAME_RESOURCE = "doxygen";
    public static final String NAME_BEHAVIOR = "lagrangeb";
    
    public static final String REQ_DEPTH_WATER = Doxygen.Names.DEPTH_WATER;
    public static final String REQ_INTERVAL_PAR_DATA = Doxygen.Names.INTERVAL_PAR_DATA;
    public static final String REQ_K_600 = Doxygen.Names.K_600;
    public static final String REQ_RATIO_PROD_PAR = Doxygen.Names.RATIO_PROD_PAR;
    public static final String REQ_RESP_DAILY_AVG = Doxygen.Names.RESP_DAILY_AVG;
    public static final String REQ_TABLE_DO_CONC_DOWN = Doxygen.Names.TABLE_DO_CONC_DOWN;
    public static final String REQ_TABLE_DO_CONC_UP = Doxygen.Names.TABLE_DO_CONC_UP;
    public static final String REQ_TABLE_PAR = Doxygen.Names.TABLE_PAR;
    public static final String REQ_TABLE_TEMP_DOWN = Doxygen.Names.TABLE_TEMP_DOWN;
    public static final String REQ_TABLE_TEMP_UP = Doxygen.Names.TABLE_TEMP_UP;
    public static final String REQ_TIME_TRANSPORT = Doxygen.Names.TIME_TRANSPORT;
    
    public static final Field[] reqFields = {
        new Field(REQ_DEPTH_WATER, "FLOAT", null, true),
        new Field(REQ_INTERVAL_PAR_DATA, "FLOAT", null, true),
        new Field(REQ_K_600, "FLOAT", null, true),
        new Field(REQ_RATIO_PROD_PAR, "FLOAT", null, true),
        new Field(REQ_RESP_DAILY_AVG, "FLOAT", null, true),
        new Field(REQ_TABLE_DO_CONC_DOWN, "TEXT", null, true),
        new Field(REQ_TABLE_DO_CONC_UP, "TEXT", null, true),
        new Field(REQ_TABLE_PAR, "TEXT", null, true),
        new Field(REQ_TABLE_TEMP_DOWN, "TEXT", null, true),
        new Field(REQ_TABLE_TEMP_UP, "TEXT", null, true),
        new Field(REQ_TIME_TRANSPORT, "FLOAT", null, true)
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
