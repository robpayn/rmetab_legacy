package stream_metab.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;

public class ChannelFields extends FieldMap {

    public static final String NAME_RESOURCE = "water";
    public static final String NAME_BEHAVIOR = "channel";
    
    public static final String REQ_AREA_SURF = "AS";
    public static final String REQ_AREA_SURF_MAX = "ASMAX";
    public static final String REQ_BANKSLOPE = "BANKSLOPE";
    public static final String REQ_AREA_SURF_DELTA = "DELAS";
    public static final String REQ_HEAD_INITIAL = "INIHEAD";
    public static final String REQ_LENGTH = "LENGTH";
    public static final String REQ_WIDTHAVG = "WIDTHAVG";
    public static final String REQ_XCOORD = "XCOORD";
    public static final String REQ_YCOORD = "YCOORD";
    public static final String REQ_ZBOT = "ZBOT";
    public static final String REQ_ZTOP = "ZTOP";
    
    public static final Field[] reqFields = {
        new Field(REQ_AREA_SURF, "FLOAT", null, true),
        new Field(REQ_AREA_SURF_MAX, "FLOAT", null, true),
        new Field(REQ_BANKSLOPE, "FLOAT", null, true),
        new Field(REQ_AREA_SURF_DELTA, "FLOAT", null, true),
        new Field(REQ_HEAD_INITIAL, "FLOAT", null, true),
        new Field(REQ_LENGTH, "FLOAT", null, true),
        new Field(REQ_WIDTHAVG, "FLOAT", null, true),
        new Field(REQ_XCOORD, "FLOAT", null, true),
        new Field(REQ_YCOORD, "FLOAT", null, true),
        new Field(REQ_ZBOT, "FLOAT", null, true),
        new Field(REQ_ZTOP, "FLOAT", null, true)
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
