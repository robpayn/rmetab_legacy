package stream_metab.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;

public class ManningFields extends FieldMap {
    
    public static final String NAME_RESOURCE = "water";
    public static final String NAME_BEHAVIOR = "manning";
    
    public static final String REQ_DEPTH_ACTIVE = "DEPTHACTIVE";
    public static final String REQ_FROM_LENGTH = "FROMLEN";
    public static final String REQ_TO_LENGTH = "TOLEN";
    public static final String REQ_CALC_ELEV = "CALCZ";
    public static final String REQ_ELEVATION_CHANNEL = "ZCHAN";
    public static final String REQ_WAVE_TYPE = "EQTYPE";
    public static final String REQ_BANKSLOPE = "BANKSLOPE";
    public static final String REQ_BTMWTH = "BTMWTH";
    public static final String REQ_FLOW_INITIAL = "INITFLOW";
    public static final String REQ_VELOCITY_EXPONENT = "VELEXP";
    public static final String REQ_RADIUS_EXPONENT = "RADEXP";
    public static final String REQ_VELOCITY = "VELOCITY";
    public static final String REQ_WETTEDINCR = "WETTEDINCR";
    public static final String REQ_WIDTH_AVERAGE = "WIDTHAVG";

    public static final String OPT_CHEZEY = "C";
    public static final String OPT_WIELE_INT = "WIELEINT";
    public static final String OPT_WIELE_SLOPE = "WIELESLOPE";
    
    public static final Field[] reqFields = {
        new Field(REQ_DEPTH_ACTIVE, "FLOAT", null, true),
        new Field(REQ_FROM_LENGTH, "FLOAT", null, true),
        new Field(REQ_TO_LENGTH, "FLOAT", null, true),
        new Field(REQ_CALC_ELEV, "TEXT", null, true),
        new Field(REQ_ELEVATION_CHANNEL, "FLOAT", null, true),
        new Field(REQ_WAVE_TYPE, "INTEGER", null, true),
        new Field(REQ_BANKSLOPE, "FLOAT", null, true),
        new Field(REQ_BTMWTH, "FLOAT", null, true),
        new Field(REQ_FLOW_INITIAL, "FLOAT", null, true),
        new Field(REQ_VELOCITY_EXPONENT, "FLOAT", null, true),
        new Field(REQ_RADIUS_EXPONENT, "FLOAT", null, true),
        new Field(REQ_VELOCITY, "FLOAT", null, true),
        new Field(REQ_WETTEDINCR, "FLOAT", null, true),
        new Field(REQ_WIDTH_AVERAGE, "FLOAT", null, true),
    };
    
  
    public void addChezeyField()
    {
        this.put(OPT_CHEZEY, new Field(OPT_CHEZEY, "FLOAT", null, true));
    }
    
    public void addWieleIntField()
    {
        this.put(OPT_WIELE_INT, new Field(OPT_WIELE_INT, "FLOAT", null, true));
    }

    public void addWieldSlopeField()
    {
        this.put(OPT_WIELE_SLOPE, new Field(OPT_WIELE_SLOPE, "FLOAT", null, true));
    }
    
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
