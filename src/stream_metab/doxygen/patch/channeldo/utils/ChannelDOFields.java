package stream_metab.doxygen.patch.channeldo.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import stream_metab.doxygen.patch.channeldo.*;

public class ChannelDOFields extends FieldMap {

    public static final String NAME_RESOURCE = "doxygen";
    public static final String NAME_BEHAVIOR = "channeldo";
    
    public static final String REQ_DO_CONC_INIT = Doxygen.Names.DO_CONC_INIT;
    public static final String REQ_SAT_DO_CONC_AVG_EDGE = Doxygen.Names.SAT_DO_CONC_AVG_EDGE;
    public static final String REQ_TEMP_AVG_EDGE = Doxygen.Names.TEMP_AVG_EDGE;

    public static final Field[] reqFields = {
        new Field(REQ_DO_CONC_INIT, "FLOAT", null, true),
        new Field(REQ_SAT_DO_CONC_AVG_EDGE, "TEXT", null, true),
        new Field(REQ_TEMP_AVG_EDGE, "TEXT", null, true)
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
