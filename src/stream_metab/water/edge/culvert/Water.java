package stream_metab.water.edge.culvert;

import neo.motif.*;
import neo.state.*;
import neo.util.*;

/**
 * <p>
 * Controls the flow of water through a culvert
 * </p>
 * <p>
 * Flow estimates are based on the dynamic wave equation without the nonlinear
 * terms
 * </p>
 * <b>Full list of parameters needed for calculation of water flux:</b> <ul
 * style = "list-style-type: none"> <li><b>C</b> - coefficient of friction for
 * wave equation (currently hard-coded to be equivalent to Manning's friction
 * factor)</li> <li><b>DIAMETER</b> - diameter of the culvert (currently must be
 * circular)</li> <li><b>FLOWINIT</b> - initial flow in the culvert</li> <li>
 * <b>ELEVFROM</b> - elevation of the base of the culvert on the "from" side of
 * the edge</li> <li><b>ELEVTO</b> - elevation of the base of the culvert on the
 * "to" side of the edge</li> <li><b>FROMLEN (IF USING "culvert" MOTIF)</b> -
 * Length associated with "from" patch</li> <li><b>TOLEN (IF USING "culvert"
 * MOTIF)</b> - Length associated with "to" patch</li> <li><b>LENGTH (ONLY IF
 * USING "culvert.localgradient" MOTIF)</b> - length of the culvert</li> </ul>
 * 
 * @author robert.payn
 */
public class Water extends AbstractRoute {

    /**
     * Exponent for velocity term in partial dynamic wave equation
     */
    private static final double EXP_VEL = 2.0;
    /**
     * Exponent for velocity term in partial dynamic wave equation minus one
     * (optimization step)
     */
    private static final double EXP_VEL_MINUS_ONE = EXP_VEL - 1.0;
    /**
     * Exponent for hydraulic radius term in partial dynamic wave equation
     */
    private static final double EXP_RAD = 1.33333;

    /**
     * Time step [Time]
     */
    private double delT;
    /**
     * Influence of acceleration of gravity over a time step [Length Time
     * <sup><small>-1</small></sup>]
     */
    private double delT_g;
    /**
     * Coefficient of friction
     */
    private HStateDbl friction = null;
    /**
     * Hydraulic gradient [Length<sub><small>&Delta;head</small></sub>
     * Length<sub><small>edge</small></sub><sup><small>-1</small></sup>]
     */
    private HStateDbl gradientHyd = null;
    /**
     * Hydraulic radius [Length]
     */
    private HStateDbl radiusHyd = null;
    /**
     * Velocity of water along the edge [Length
     * Time<sup><small>-1</small></sup>]
     */
    private HStateDbl velocity = null;
    /**
     * Calculator for velocity of water
     */
    private SimpleCalculator velocityCalc = null;
    /**
     * Cross-sectional area of the wetted channel
     * [Length<sup><small>2</small></sup>]
     */
    private HStateDbl areaXsect = null;

    /**
     * Calculates the volumetric water flow through a culvert
     * 
     * @return volumetric water flow [Length<sup><small>3</small></sup>
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double computeValue()
    {

        // short-circuit test
        if (areaXsect.v == 0)
        {
            return 0.;
        }
        velocityCalc.calculate();
        // can't store previous, may have been balance()'ed
        double flow = getToPathValue();
        flow = (flow + delT_g * areaXsect.v * gradientHyd.v)
                / (1.0 + ((delT_g * friction.v * Math.pow(Math.abs(velocity.v), EXP_VEL_MINUS_ONE)) / Math.pow(
                        radiusHyd.v, EXP_RAD)));
        if (Double.isNaN(flow))
        {
            Logger.logError("Severe model instability.  Flow is not a number in edge " + myHolon.getUID().toString());
        }
        return flow;

    }

    /**
     * Calculates the initial volumetric water flow through a culvert
     * 
     * @return volumetric water flow [Length<sup><small>3</small></sup>
     *         Time<sup><small>-1</small></sup>]
     */
    @Override
    protected double initValue()
    {

        velocityCalc.initialize();
        return 0.0;

    }

    /**
     * Define state dependencies for calculation of water flow in a culvert
     */
    @Override
    public void setDependencies()
    {

        // static parameters of this holon
        friction = (HStateDbl) getInitHState("C");
        // dynamic parameters of this holon
        areaXsect = (HStateDbl) getInitHState("AREAXSECT");
        gradientHyd = (HStateDbl) getInitHState("GRADIENTHYD");
        radiusHyd = (HStateDbl) getInitHState("RADIUSHYD");
        try
        {
            // Dependencies are not set because this is a stateval
            // controlled by a simple calculator called by this route
            velocity = (HStateDbl) myHolon.getHState("VELOCITY");
            velocityCalc = (SimpleCalculator) velocity.getPrimaryModifier();
        }
        catch (Exception e)
        {
            initError(e.toString());
        }
        delT = timeCategory() * Scheduler.getTimeStep();
        delT_g = delT * Param.g;

    }

}
