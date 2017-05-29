package stream_metab.utils;

import java.io.*;
import java.sql.*;
import java.text.*;
import java.util.*;

import org.neosimulation.apps.modelmanager.neov1.*;
import org.neosimulation.apps.modelmanager.neov1.neodatabase.*;
import org.neosimulation.apps.simmanager.*;
import org.w3c.dom.Element;

import stream_metab.doxygen.bound.loaddo.utils.LoadDOFields;
import stream_metab.doxygen.bound.proddo.utils.ProdDOFields;
import stream_metab.doxygen.bound.respdo.utils.RespDOFields;
import stream_metab.doxygen.edge.advectdo.utils.AdvectDOFields;
import stream_metab.doxygen.edge.advectdocalib.utils.AdvectDOCalibFields;
import stream_metab.doxygen.edge.lagrangeb.utils.LagrangeFields;
import stream_metab.doxygen.edge.reaerationdo.utils.ReaerationDOFields;
import stream_metab.doxygen.patch.airdo.utils.AirDOFields;
import stream_metab.doxygen.patch.channeldo.utils.ChannelDOFields;

/**
 * Factory for creating models based on properties
 * 
 * @author robert.payn
 */
public class RiverFactory extends MetaParamApplicator<ConfigElement, NEOModelManager> {
    
    /**
     * Key for switch to add DO to model
     */
    public static final String PROP_SUBKEY_ADD_DO = "addDO";

    /**
     * Key for air pressure
     */
    private static final String PROP_SUBKEY_AIR_PRESSURE = "airPressure";

    /**
     * Key for bed width
     */
    public static final String PROP_SUBKEY_BEDWIDTH = "bedWidth";
    
    /**
     * Key for bed width change with depth
     */
    public static final String PROP_SUBKEY_BEDWIDTH_CHANGE = "bedWidthChangeWithDepth";
    
    /**
     * Key for database name
     */
    public static final String PROP_SUBKEY_DB = "dbName";
    
    /**
     * Key for channel depth
     */
    public static final String PROP_SUBKEY_DEPTH = "depth";
    
    /**
     * Key for upstream boundary name
     */
    public static final String PROP_SUBKEY_DOWNSTREAM_NAME = "downstreamName";
    
    /**
     * Key for edge name
     */
    public static final String PROP_SUBKEY_EDGE_NAME = "edgeName";
    
    /**
     * Key for width during flood conditions (overbank flow)
     */
    public static final String PROP_SUBKEY_FLOODWIDTH = "floodWidth";

    /**
     * Key for name of table with flow values
     */
    public static final String PROP_SUBKEY_FLOWTABLE_UP = "upstreamFlowDataTable";

    /**
     * Key for gradient
     */
    public static final String PROP_SUBKEY_GRADIENT = "bedGradient";
    
    /**
     * Key for initial water depth
     */
    public static final String PROP_SUBKEY_INIT_DEPTH = "initialDepth";
    
    /**
     * Key for initial flow
     */
    private static final String PROP_SUBKEY_INIT_FLOW = "initialFlow";

    /**
     * Key for initial velocity
     */
    public static final String PROP_SUBKEY_INIT_VELOCITY = "initialVelocity";

    /**
     * Key for PAR table
     */
    public static final String PROP_SUBKEY_PAR_TABLE = "PARTable";

    /**
     * Key for number of patches
     */
    public static final String PROP_SUBKEY_PATCH_COUNT = "numPatches";
    
    /**
     * Key for patch name seed
     */
    public static final String PROP_SUBKEY_PATCH_NAME = "patchName";
    
    /**
     * Key for reach length
     */
    public static final String PROP_SUBKEY_REACH_LENGTH = "reachLength";

    /**
     * Key for roughness coefficient
     */
    public static final String PROP_SUBKEY_ROUGHNESS = "roughness";

    /**
     * Key for the source database
     */
    public static final String PROP_SUBKEY_SRC_DB = "sourceDB";

    /**
     * Key for upstream boundary name
     */
    public static final String PROP_SUBKEY_UPSTREAM_NAME = "upstreamName";

    public static final String PROP_SUBKEY_DO_CONC_INIT = "doConcInit";

    public static final String PROP_SUBKEY_DOWNSTREAM_EDGE = "downstreamEdge";

    public static final String PROP_SUBKEY_K600 = "k600";

    public static final String PROP_SUBKEY_P_TO_PAR_RATIO = "pToPARRatio";

    public static final String PROP_SUBKEY_DAILY_RESP = "dailyRespRate";

    public static final String PROP_SUBKEY_DOWNSTREAM_DO = "downstreamDOTable";

    public static final String PROP_SUBKEY_DOWNSTREAM_TEMP = "downstreamTempTable";

    public static final String PROP_SUBKEY_UPSTREAM_DO = "upstreamDOTable";

    public static final String PROP_SUBKEY_UPSTREAM_TEMP = "upstreamTempTable";

    public static final String PROP_SUBKEY_LAGRANGE = "lagrange";

    public static final String PROP_SUBKEY_DEPTH_WATER = "depthWater";

    public static final String PROP_SUBKEY_INTERVAL_PAR = "parInterval";

    public static final String PROP_SUBKEY_TRAVEL_TIME = "travelTime";

    public static final String PROP_SUBKEY_FLOWTABLE_CALIB = "calibFlowDataTable";

    public static final String PROP_SUBKEY_VELOCITY_EXP = "velocityExponent";

    public static final String PROP_SUBKEY_RADIUS_EXP = "radiusExponent";

    public static final String PROP_SUBKEY_QVEL_INTERCEPT = "qVelIntercept";

    public static final String PROP_SUBKEY_QVEL_SLOPE = "qVelSlope";

    public static final String PROP_SUBKEY_VELOCITY_EDGE = "velocityEdgeName";

    private static final String PROP_SUBKEY_PARTICLE_TICK_INTERVAL = "particleTickInterval";

    /**
     * Database
     */
    private NEOAccessDatabase db;
    
    private String[] channelPatchNames;
    private String[] manningEdgeNames;
    private NumberFormat formatter;
    
    /**
     * Creates an instance of the metaparameter  
     * 
     * @param paramID 
     *      unique ID for the metaparameter
     * @param buildElem 
     *      XML file defining the metaparameter 
     * @param keyBase 
     *      base of key to use for java properties for this parameter
     */
    public RiverFactory(String paramID, ConfigElement buildElem)
    {
        super(paramID, buildElem);
    }

    /**
     * Adds the dissolved oxygen model to the selected database.
     * Hydrologic model must be added first.
     * 
     * @throws SQLException if database access error
     */
    private void addDO() throws SQLException
    {
        // add the resource to the the network tables
        db.addResource("doxygen");
        
        // create the initialization tables
        FieldMap fieldMap = new AirDOFields();
        db.addBehavior(fieldMap);
        ConfigElement doElem = parameter.getFirstElementByTag("DO");
        fieldMap.get(AirDOFields.REQ_AIR_PRESSURE).setValue(
                doElem.getTextContentForFirstTag(PROP_SUBKEY_AIR_PRESSURE));
        fieldMap.get(AirDOFields.REQ_PAR_TABLE_NAME).setValue(
                doElem.getTextContentForFirstTag(PROP_SUBKEY_PAR_TABLE));
        db.addPatch("air", fieldMap);
        
        ConfigElement neoElem = parameter.getFirstElementByTag("NEO");
        String calibHolon = "'" + 
                neoElem.getTextContentForFirstTag(PROP_SUBKEY_EDGE_NAME) +
                neoElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_EDGE) + "'";

        fieldMap = new ChannelDOFields();
        Field readerPathField = new Field(
                "ReaderPath", 
                "TEXT", 
                doElem.getTextContentForFirstTag("channelPatchReaderPath"), 
                true);
        if (Boolean.valueOf(doElem.getElement().getAttribute("waterreader")))
        {
            fieldMap.put("ReaderPath", readerPathField);
        }


        FieldMap advectEdgeFieldMap = new AdvectDOFields();
        readerPathField = new Field(
                "ReaderPath", 
                "TEXT", 
                doElem.getTextContentForFirstTag("advectEdgeReaderPath"), 
                true);
        if (Boolean.valueOf(doElem.getElement().getAttribute("waterreader")))
        {
            advectEdgeFieldMap.put("ReaderPath", readerPathField);
        }

        FieldMap airEdgeFieldMap = new ReaerationDOFields();
        FieldMap prodBoundFieldMap = new ProdDOFields();
        FieldMap respBoundFieldMap = new RespDOFields();
        FieldMap observedEdgeFieldMap = new AdvectDOCalibFields();
        if (Boolean.valueOf(doElem.getElement().getAttribute("waterreader")))
        {
            observedEdgeFieldMap.put("ReaderPath", readerPathField);
        }
        
        db.addBehavior(fieldMap);
        db.addBehavior(airEdgeFieldMap);
        db.addBehavior(advectEdgeFieldMap);
        db.addBehavior(prodBoundFieldMap);
        db.addBehavior(respBoundFieldMap);
        db.addBehavior(observedEdgeFieldMap);
        
        fieldMap.get(ChannelDOFields.REQ_DO_CONC_INIT)
                .setValue(doElem.getTextContentForFirstTag(PROP_SUBKEY_DO_CONC_INIT));
        fieldMap.get(ChannelDOFields.REQ_SAT_DO_CONC_AVG_EDGE).setValue(calibHolon);
        fieldMap.get(ChannelDOFields.REQ_TEMP_AVG_EDGE).setValue(calibHolon);
        airEdgeFieldMap.get(ReaerationDOFields.REQ_K_SCHMIDT_600).setValue(
                doElem.getTextContentForFirstTag(PROP_SUBKEY_K600));
        prodBoundFieldMap.get(ProdDOFields.REQ_P_TO_PAR_RATIO).setValue(
                doElem.getTextContentForFirstTag(PROP_SUBKEY_P_TO_PAR_RATIO));
        respBoundFieldMap.get(RespDOFields.REQ_DAILY_RESP_RATE).setValue(
                doElem.getTextContentForFirstTag(PROP_SUBKEY_DAILY_RESP));
        
        observedEdgeFieldMap.get(AdvectDOCalibFields.REQ_SAT_DO_CONC_IN).setValue(
                "'" + neoElem.getTextContentForFirstTag(PROP_SUBKEY_UPSTREAM_NAME) + "'");
        observedEdgeFieldMap.get(AdvectDOCalibFields.REQ_TEMP_IN).setValue(
                "'" + neoElem.getTextContentForFirstTag(PROP_SUBKEY_UPSTREAM_NAME) + "'");
        observedEdgeFieldMap.get(AdvectDOCalibFields.REQ_TABLE_DO_CONC).setValue(
                doElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_DO));
        observedEdgeFieldMap.get(AdvectDOCalibFields.REQ_TABLE_TEMP).setValue(
                doElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_TEMP));
        
        db.addPatch(channelPatchNames[0], fieldMap);
        String airEdgeName = neoElem.getTextContentForFirstTag(PROP_SUBKEY_PATCH_NAME) 
                + "air" + formatter.format(1);
        db.addEdge(airEdgeName, airEdgeFieldMap);
        db.addLink(airEdgeName, "air", channelPatchNames[0]);
        String prodBoundName = "prod" + formatter.format(1);
        db.addBound(prodBoundName, prodBoundFieldMap);
        db.addLink(prodBoundName, null, channelPatchNames[0]);
        String respBoundName = "resp" + formatter.format(1);
        db.addBound(respBoundName, respBoundFieldMap);
        db.addLink(respBoundName, null, channelPatchNames[0]);
        
        for (int i = 1; i < channelPatchNames.length; i++)
        {
            db.addPatch(channelPatchNames[i], fieldMap);
            if (i == Integer.valueOf(neoElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_EDGE)))
            {
                db.addEdge(manningEdgeNames[i - 1], observedEdgeFieldMap);
                ConfigElement lagrangeElem = doElem.getFirstElementByTag(PROP_SUBKEY_LAGRANGE);
                if (lagrangeElem.getElement().getAttribute("active").equals("true"))
                {
                    FieldMap lagrangeEdgeFieldMap = new LagrangeFields();
                    db.addBehavior(lagrangeEdgeFieldMap);
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_DEPTH_WATER).setValue(
                            lagrangeElem.getTextContentForFirstTag(PROP_SUBKEY_DEPTH_WATER));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_INTERVAL_PAR_DATA).setValue(
                            lagrangeElem.getTextContentForFirstTag(PROP_SUBKEY_INTERVAL_PAR));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_K_600).setValue(
                            doElem.getTextContentForFirstTag(PROP_SUBKEY_K600));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_RATIO_PROD_PAR).setValue(
                            doElem.getTextContentForFirstTag(PROP_SUBKEY_P_TO_PAR_RATIO));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_RESP_DAILY_AVG).setValue(
                            doElem.getTextContentForFirstTag(PROP_SUBKEY_DAILY_RESP));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_TABLE_DO_CONC_DOWN).setValue(
                            doElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_DO));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_TABLE_DO_CONC_UP).setValue(
                            doElem.getTextContentForFirstTag(PROP_SUBKEY_UPSTREAM_DO));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_TABLE_PAR).setValue(
                            doElem.getTextContentForFirstTag(PROP_SUBKEY_PAR_TABLE));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_TABLE_TEMP_DOWN).setValue(
                            doElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_TEMP));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_TABLE_TEMP_UP).setValue(
                            doElem.getTextContentForFirstTag(PROP_SUBKEY_UPSTREAM_TEMP));
                    lagrangeEdgeFieldMap.get(LagrangeFields.REQ_TIME_TRANSPORT).setValue(
                            lagrangeElem.getTextContentForFirstTag(PROP_SUBKEY_TRAVEL_TIME));
                    db.addEdge("lagrange", lagrangeEdgeFieldMap);
                    db.addLink("lagrange", channelPatchNames[i - 1], channelPatchNames[i]);
                }
            }
            else
            {
                db.addEdge(manningEdgeNames[i - 1], advectEdgeFieldMap);
            }
            airEdgeName = neoElem.getTextContentForFirstTag(PROP_SUBKEY_PATCH_NAME) 
                    + "air" + formatter.format(i + 1);
            db.addEdge(airEdgeName, airEdgeFieldMap);
            db.addLink(airEdgeName, "air", channelPatchNames[i]);
            prodBoundName = "prod" + formatter.format(i + 1);
            db.addBound(prodBoundName, prodBoundFieldMap);
            db.addLink(prodBoundName, null, channelPatchNames[i]);
            respBoundName = "resp" + formatter.format(i + 1);
            db.addBound(respBoundName, respBoundFieldMap);
            db.addLink(respBoundName, null, channelPatchNames[i]);
        }
        
        fieldMap = new LoadDOFields();
        readerPathField = new Field(
                "ReaderPath", 
                "TEXT", 
                doElem.getTextContentForFirstTag("inflowReaderPath"), 
                true);
        if (Boolean.valueOf(doElem.getElement().getAttribute("waterreader")))
        {
            fieldMap.put("ReaderPath", readerPathField);
        }
        db.addBehavior(fieldMap);
        fieldMap.get(LoadDOFields.REQ_TABLE_DO_CONC).setValue(
                doElem.getTextContentForFirstTag(PROP_SUBKEY_UPSTREAM_DO));
        fieldMap.get(LoadDOFields.REQ_TABLE_TEMP).setValue(
                doElem.getTextContentForFirstTag(PROP_SUBKEY_UPSTREAM_TEMP));
        db.addBound(neoElem.getTextContentForFirstTag(PROP_SUBKEY_UPSTREAM_NAME), fieldMap);
        fieldMap.get(LoadDOFields.REQ_TABLE_DO_CONC).setValue("'ts_blank'");
        fieldMap.get(LoadDOFields.REQ_TABLE_TEMP).setValue("'ts_blank'");
        if (Boolean.valueOf(doElem.getElement().getAttribute("waterreader")))
        {
            fieldMap.get("ReaderPath").setValue(doElem.getTextContentForFirstTag("outflowReaderPath"));
        }
        db.addBound(neoElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_NAME), fieldMap);
    }

    /**
     * Adds the hydrologic model to the selected database
     * 
     * @throws SQLException if database access error
     */
    private void addWater() throws SQLException
    {
        ConfigElement hydroElem = parameter.getFirstElementByTag("Hydro");
        
        // add the resource to the the network tables
        db.addResource("water");
        
        // create the initialization tables
        FieldMap fieldsChannel = new ChannelFields(); 
        db.addBehavior(fieldsChannel);
        ManningFields fieldsManning = new ManningFields();
        ConfigElement wieleElem = hydroElem.getFirstElementByTag("wiele");
        if (wieleElem != null)
        {
            fieldsManning.addWieleIntField();
            fieldsManning.get(ManningFields.OPT_WIELE_INT)
                .setValue(wieleElem.getTextContentForFirstTag("intercept"));
            fieldsManning.addWieldSlopeField();
            fieldsManning.get(ManningFields.OPT_WIELE_SLOPE)
                .setValue(wieleElem.getTextContentForFirstTag("slope"));
            
        }
        else
        {
            fieldsManning.addChezeyField();
            fieldsManning.get(ManningFields.OPT_CHEZEY)
                .setValue(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_ROUGHNESS));
        }
        db.addBehavior(fieldsManning);

        // create number format with appropriate number of digits for
        //      number of patches
        ConfigElement neoElem = parameter.getFirstElementByTag("NEO");
        int cellCount = Integer.valueOf(neoElem.getTextContentForFirstTag(PROP_SUBKEY_PATCH_COUNT));
        int size = (int)Math.log10((double)cellCount);
        String intFormat = "0";
        for (int i = 0; i < size; i++)
        {
            intFormat += "0";
        }
        formatter = new DecimalFormat(intFormat);

        // initialize uniform channel values
        double reachLength = Double.valueOf(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_REACH_LENGTH));
        double cellLength = reachLength / cellCount;
        fieldsChannel.get(ChannelFields.REQ_LENGTH).setValue(Double.toString(cellLength));
        double bedWidth = Double.valueOf(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_BEDWIDTH));
        fieldsChannel.get(ChannelFields.REQ_AREA_SURF).setValue(Double.toString(cellLength * bedWidth));
        fieldsManning.get(ManningFields.REQ_BTMWTH).setValue(Double.toString(bedWidth));
        double bedWidthChange = Double.valueOf(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_BEDWIDTH_CHANGE));
        fieldsChannel.get(ChannelFields.REQ_AREA_SURF_DELTA).setValue(Double.toString(cellLength * bedWidthChange));
        fieldsManning.get(ManningFields.REQ_WETTEDINCR).setValue(Double.toString(bedWidthChange));
        double floodWidth = Double.valueOf(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_FLOODWIDTH));
        fieldsChannel.get(ChannelFields.REQ_AREA_SURF_MAX).setValue(Double.toString(cellLength * floodWidth));
        double bedGradient = Double.valueOf(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_GRADIENT));
        double depth = Double.valueOf(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_DEPTH));
        double iniDepth = Double.valueOf(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_INIT_DEPTH));
        fieldsChannel.get(ChannelFields.REQ_YCOORD).setValue("0");
        fieldsChannel.get(ChannelFields.REQ_XCOORD).setValue(Double.toString(cellLength * 0.5));
        double zBot = bedGradient * reachLength;
        fieldsChannel.get(ChannelFields.REQ_ZBOT).setValue(Double.toString(zBot));
        fieldsChannel.get(ChannelFields.REQ_ZTOP).setValue(Double.toString(zBot + depth));
        fieldsChannel.get(ChannelFields.REQ_HEAD_INITIAL).setValue(Double.toString(zBot + iniDepth));
        fieldsManning.get(ManningFields.REQ_VELOCITY)
                .setValue(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_INIT_VELOCITY));
        fieldsManning.get(ManningFields.REQ_FLOW_INITIAL)
                .setValue(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_INIT_FLOW));
        fieldsChannel.get(ChannelFields.REQ_BANKSLOPE).setValue("0.0");
        fieldsChannel.get(ChannelFields.REQ_WIDTHAVG).setValue("0.0");
        fieldsManning.get(ManningFields.REQ_BANKSLOPE).setValue("0.0");
        fieldsManning.get(ManningFields.REQ_WIDTH_AVERAGE).setValue("0.0");
        fieldsManning.get(ManningFields.REQ_DEPTH_ACTIVE).setValue("0.0");
        fieldsManning.get(ManningFields.REQ_VELOCITY_EXPONENT)
                .setValue(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_VELOCITY_EXP));
        fieldsManning.get(ManningFields.REQ_RADIUS_EXPONENT)
                .setValue(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_RADIUS_EXP));
        fieldsManning.get(ManningFields.REQ_FROM_LENGTH).setValue("0.0");
        fieldsManning.get(ManningFields.REQ_TO_LENGTH).setValue("0.0");
        fieldsManning.get(ManningFields.REQ_CALC_ELEV).setValue("'Y'");
        fieldsManning.get(ManningFields.REQ_ELEVATION_CHANNEL).setValue("0.0");
        fieldsManning.get(ManningFields.REQ_WAVE_TYPE).setValue("1");
        
        channelPatchNames = new String[cellCount];
        manningEdgeNames = new String[cellCount - 1];
        
        // add the first channel patch to network and initialization tables
        channelPatchNames[0] = neoElem.getTextContentForFirstTag(PROP_SUBKEY_PATCH_NAME) 
                + formatter.format(1);
        db.addPatch(channelPatchNames[0], fieldsChannel);

        // iterate to chain remaining channel cells and edges
        int numCellsReach = Integer.valueOf(neoElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_EDGE));
        for (int i = 1; i < cellCount; i++)
        {
            // update spatially variable values for patch
            channelPatchNames[i] = neoElem.getTextContentForFirstTag(PROP_SUBKEY_PATCH_NAME) 
                    + formatter.format(i+1);
            fieldsChannel.get(ChannelFields.REQ_XCOORD).setValue(Double.toString(cellLength * ((double)i + 0.5)));
            zBot = bedGradient * (reachLength  - (double)i * cellLength);
            fieldsChannel.get(ChannelFields.REQ_ZBOT).setValue(Double.toString(zBot));
            fieldsChannel.get(ChannelFields.REQ_ZTOP).setValue(Double.toString(zBot + depth));
            fieldsChannel.get(ChannelFields.REQ_HEAD_INITIAL).setValue(Double.toString(zBot + iniDepth));
            
            // add the next patch to the network and initialization tables 
            db.addPatch(channelPatchNames[i], fieldsChannel);
            
            // update spatially variable values for edge
            manningEdgeNames[i - 1] = neoElem.getTextContentForFirstTag(PROP_SUBKEY_EDGE_NAME) + formatter.format(i);
            
            // add the edge between the previous patch and next patch
            if (i == numCellsReach)
            {
                String flowCalibData = hydroElem.getTextContentForFirstTag(PROP_SUBKEY_FLOWTABLE_CALIB);
                if (flowCalibData != null)
                {
                    // creates a calibration edge in parallel with the edge
                    // with corresponding predicted values
                    FieldMap fieldsManningCalib = new ManningCalibFields();
                    db.addBehavior(fieldsManningCalib);
                    fieldsManningCalib.get(ManningCalibFields.REQ_FLOW_DATA_TABLE).setValue(flowCalibData);
                    fieldsManningCalib.get(ManningCalibFields.REQ_FLOW_EDGE)
                            .setValue("'" + manningEdgeNames[i - 1] + "'");
                    fieldsManningCalib.get(ManningCalibFields.REQ_QVEL_INTERCEPT)
                            .setValue(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_QVEL_INTERCEPT));
                    fieldsManningCalib.get(ManningCalibFields.REQ_QVEL_SLOPE)
                            .setValue(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_QVEL_SLOPE));
                    fieldsManningCalib.get(ManningCalibFields.REQ_REACH_LENGTH)
                            .setValue(Double.toString(cellLength * (double)numCellsReach));
                    fieldsManningCalib.get(ManningCalibFields.REQ_VELOCITY_EDGE)
                            .setValue(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_VELOCITY_EDGE));
                    fieldsManningCalib.get(ManningCalibFields.REQ_PARTICLE_TICK_INTERVAL)
                            .setValue(hydroElem.getTextContentForFirstTag(PROP_SUBKEY_PARTICLE_TICK_INTERVAL));
                    db.addEdge("manningcalib", fieldsManningCalib);
                    db.addLink("manningcalib", channelPatchNames[i - 1], channelPatchNames[i]);
                }
            }
            db.addEdge(manningEdgeNames[i - 1], fieldsManning);
            db.addLink(manningEdgeNames[i - 1], channelPatchNames[i - 1], channelPatchNames[i]);
        }
        
        // add upstream boundary
        FieldMap fieldsFlow = new FlowFields();
        db.addBehavior(fieldsFlow);
        fieldsFlow.get(FlowFields.REQ_FLOW_DATA_FILE).setValue(
                hydroElem.getTextContentForFirstTag(PROP_SUBKEY_FLOWTABLE_UP));
        db.addBound(neoElem.getTextContentForFirstTag(PROP_SUBKEY_UPSTREAM_NAME),fieldsFlow);
        db.addLink(neoElem.getTextContentForFirstTag(PROP_SUBKEY_UPSTREAM_NAME), null,
                neoElem.getTextContentForFirstTag(PROP_SUBKEY_PATCH_NAME) + formatter.format(1));

        // add downstream boundary
        FieldMap fieldsLooprate = new LooprateFields();
        db.addBehavior(fieldsLooprate);
        fieldsLooprate.get(LooprateFields.REQ_LINK_ID)
                .setValue("'" + neoElem.getTextContentForFirstTag(PROP_SUBKEY_EDGE_NAME) + formatter.format(cellCount - 1) + "'");
        fieldsLooprate.get(LooprateFields.REQ_BEDSLOPE).setValue("0.0");
        db.addBound(neoElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_NAME), fieldsLooprate);
        db.addLink(neoElem.getTextContentForFirstTag(PROP_SUBKEY_DOWNSTREAM_NAME), null,
                neoElem.getTextContentForFirstTag(PROP_SUBKEY_PATCH_NAME) + formatter.format(cellCount));
        
    }

    /**
     * Not implemented
     */
    @Override
    public void writeParam(NEOModelManager simManager, String runID) throws Exception
    {
        throw new UnsupportedOperationException("This meta parameter applicator cannot be written to input.");
    }

    /**
     * Apply the parameter by building the river model with the properties provided
     */
    @Override
    public void applyParam(NEOModelManager simManager) throws Exception
    {
        String workingDir = simManager.getInputManager().getBaseLocation().getAbsolutePath();
        db = new NEOAccessDatabase(workingDir, simManager.getInputManager().getDBFileName());
        ConfigElement neoElem = parameter.getFirstElementByTag("NEO");
        db.createNew(workingDir + File.separator + neoElem.getTextContentForFirstTag(PROP_SUBKEY_SRC_DB));
        addWater();
        ConfigElement doElem = parameter.getFirstElementByTag("DO");
        if (Boolean.valueOf(doElem.getElement().getAttribute("active")))
        {
            addDO();
        }
        db.close();
    }

    /**
     * Not implemented
     */
    @Override
    public MetaParamApplicator<ConfigElement, NEOModelManager> clone()
    {
        throw new UnsupportedOperationException("This meta parameter applicator cannot be cloned.");
    }

}
