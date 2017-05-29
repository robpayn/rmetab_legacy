package stream_metab.utils;

import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;

public class ManningCalibFields extends FieldMap {
    
    public static final String NAME_RESOURCE = "water";
    public static final String NAME_BEHAVIOR = "manningcalib";
    
    public static final String REQ_FLOW_DATA_TABLE = "FLOWTABLE";
    public static final String REQ_FLOW_EDGE = "EDGENAME";
    public static final String REQ_QVEL_INTERCEPT = "QVELINT";
    public static final String REQ_QVEL_SLOPE = "QVELSLOPE";
    public static final String REQ_REACH_LENGTH = "REACHLENGTH";
    public static final String REQ_VELOCITY_EDGE = "VELEDGENAME";
    public static final String REQ_PARTICLE_TICK_INTERVAL = "PARTICLETICKINTERVAL";
    
    public static final Field[] reqFields = {
        new Field(REQ_FLOW_DATA_TABLE, "TEXT", null, true),
        new Field(REQ_FLOW_EDGE, "TEXT", null, true),
        new Field(REQ_QVEL_INTERCEPT, "FLOAT", null, true),
        new Field(REQ_QVEL_SLOPE, "FLOAT", null, true),
        new Field(REQ_REACH_LENGTH, "FLOAT", null, true),
        new Field(REQ_VELOCITY_EDGE, "TEXT", null, true),
        new Field(REQ_PARTICLE_TICK_INTERVAL, "INTEGER", null, true)
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
