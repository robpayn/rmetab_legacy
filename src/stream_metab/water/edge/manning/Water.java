package stream_metab.water.edge.manning;

import stream_metab.water.edge.manning.utils.FrictionCalculator;
import neo.motif.AbstractRoute;
import neo.motif.SimpleCalculator;
import neo.state.HStateDbl;
import neo.state.HStateInt;
import neo.table.TabledInterpolater;
import neo.util.Logger;
import neo.util.Param;
import neo.util.Scheduler;

public class Water extends AbstractRoute {
    
    private HStateInt eqtype = null;

    private FrictionCalculator friction;
    private HStateDbl velocityExpState = null;
    private double velocityExp;
    private HStateDbl radiusExpState = null;
    private double radiusExp;
    private HStateDbl bedslope = null;
    private HStateDbl xsectpre = null;
    private HStateDbl hydgrad = null;
    private HStateDbl wetwth = null;
    private HStateDbl xsectcur = null;
    private HStateDbl hradius = null;
    private HStateDbl velocity = null;
    private SimpleCalculator velocityCalc = null;

    private HStateDbl initflow = null;

    private double windshear;
    private double delT_g; // acceleration times timestep
    private double delT; // timestep
    private double[] velPows = Utility.getVelPows();
    private double[] hRadPows = Utility.getHRadPows();
    private double[] diffusePows = Utility.getDiffusePows();
    private double[] loVelPows = Utility.getLoVelPows();
    private double[] loHRadPows = Utility.getLoHRadPows();
    private double[] loDiffusePows = Utility.getLoDiffusePows();

    public void setDependencies()
    {
        friction = FrictionCalculator.createFrictionCalculator(myHolon);

        // static parameters of this holon
        eqtype = (HStateInt) getInitHState("EQTYPE");
        velocityExpState = null;
        radiusExpState = null;
        try
        {
            velocityExpState = (HStateDbl)myHolon.getHState("VELEXP");
            try
            {
                radiusExpState = (HStateDbl)myHolon.getHState("RADEXP");
                // if this code is reached, both exponent parameters exist
                // use the more flexible friction function based on exponent parameters
                velocityExpState = (HStateDbl) getInitHState("VELEXP");
                radiusExpState = (HStateDbl) getInitHState("RADEXP");
            }
            catch (Exception e)
            {
                // this point in code is an error state
                // there is a velocity exponent but there is no radius exponent
                // terminate to avoid ambiguous model configuration
                initError("VELEXP state exists without a RADEXP state.");
            }
        }
        catch (Exception e)
        {
            try
            {
                radiusExpState = (HStateDbl)myHolon.getHState("RADEXP");
                // this point in code is an error state
                // there is no velocity exponent but there is a radius exponent
                // terminate to avoid ambiguous model configuration
                initError("RADEXP state exists without a VELEXP state.");
            }
            catch (Exception f)
            {
                // neither exponent parameter exists as a state
                // leave exponent states in a null state to use default manning behavior
                // see initialization method
            }
        }

        // dynamic parameters of this holon
        bedslope = (HStateDbl) getInitHState("BEDSLOPE");
        xsectpre = (HStateDbl) getInitHState("XSECT_PREV");
        xsectcur = (HStateDbl) getInitHState("XSECT_CURR");
        hydgrad = (HStateDbl) getInitHState("HYDROGRAD");
        wetwth = (HStateDbl) getInitHState("WETTEDWIDTH");
        hradius = (HStateDbl) getInitHState("HRADIUS");

        initflow = (HStateDbl) getInitHState("INITFLOW");

        try
        {
            velocity = (HStateDbl) myHolon.getHState("VELOCITY");
            velocityCalc = (SimpleCalculator) velocity.getPrimaryModifier();
        }
        catch (Exception e)
        {
            initError(e.toString());
        }

        delT = timeCategory() * Scheduler.getTimeStep();
        delT_g = delT * Param.g;
        windshear = 0;

    }

    protected double initValue()
    {
        if (velocityExpState == null)
        {
            velocityExp = 2.0;
            radiusExp = 4.0 / 3.0;
        }
        else
        {
            velocityExp = velocityExpState.v;
            radiusExp = radiusExpState.v;
        }
        velocityCalc.initialize();
        return calc(initflow.v);
    }

    /**
     * perform the (possibly complex) calculation of flux between the associated
     * compartments.
     */
    protected double computeValue()
    {

        velocityCalc.calculate();
        return calc(getToPathValue()); // can't store previous, may have been
                                       // balance()'ed

    }

    private double calc(double flow)
    {
        double frictionValue = friction.calculate();

        double a, b, frac;
        double[] vect;

        // short-circuit test
        if (xsectcur.v == 0)
        {
            return 0.;
        }

        /*** DYNAMIC WAVE CALCULATIONS *******/
        if (eqtype.v == Utility.EquationTypes.DYNAMIC_WAVE)
        {

            double posVel = Math.abs(velocity.v);

            if (Param.preciseWater || Param.WDWBM)
            {
                a = (velocityExpState == null) ? posVel : Math.pow(posVel, (velocityExp - 1.));
                b = Math.pow(hradius.v, radiusExp);
            }
            else
            {
                if (velocityExp == 2.0)
                {
                    a = posVel;
                }
                else
                {
                    if (posVel < Utility.maxLoVelocity)
                    {
                        frac = posVel / Utility.maxLoVelocity;
                        vect = loVelPows;
                    }
                    else
                    {
                        frac = posVel / Utility.maxVelocity;
                        vect = velPows;
                    }
                    a = TabledInterpolater.getValue(vect, frac);
                }
                if (hradius.v <= Utility.maxLoHydroRad)
                {
                    frac = hradius.v / Utility.maxLoHydroRad;
                    vect = loHRadPows;
                }
                else
                {
                    frac = hradius.v / Utility.maxHydroRad;
                    vect = hRadPows;
                }
                b = TabledInterpolater.getValue(vect, frac);
            }

            flow = (flow 
                    + (2 * velocity.v * (xsectcur.v - xsectpre.v))
                    + (delT * velocity.v * velocity.v * wetwth.v * (bedslope.v - hydgrad.v)) 
                    + (delT_g * (xsectcur.v * hydgrad.v)))
                / (1. + (delT_g * frictionValue * a / b));
          
            /********** DIFFUSE WAVE CALCULATION *****************/

        }
        else if (eqtype.v == Utility.EquationTypes.DIFFUSE_WAVE)
        {
            // hydgrad.v = ( -hydgrad.v) - (windShear / hradius.v);

            if (Param.preciseWater || Param.WDWBM)
            {
                if (hydgrad.v < 0)
                {
                    flow = -xsectcur.v
                            * Math.pow((-hydgrad.v * Math.pow(hradius.v, radiusExp) / frictionValue),
                                    (1. / velocityExp));
                }
                else
                {
                    flow = (xsectcur.v * Math.pow((hydgrad.v * Math.pow(hradius.v, radiusExp) / frictionValue),
                            (1. / velocityExp)));
                }
            }
            else
            {

                if (hradius.v < Utility.maxLoHydroRad)
                {
                    frac = hradius.v / (Utility.maxLoHydroRad);
                    vect = loHRadPows;
                }
                else
                {
                    frac = hradius.v / Utility.maxHydroRad;
                    vect = hRadPows;
                }

                a = Math.abs(hydgrad.v) * TabledInterpolater.getValue(vect, frac) / frictionValue;

                if (a > Utility.maxDiffuseVal)
                {
                    initError("Intermediate value 'a' ((hydGrad * hydRad ^ p2) / manN) is greater than "
                            + "MaxDiffuseVal stated in water.edge.manning.utility.");
                    a = Utility.maxDiffuseVal;
                }

                if (a < Utility.maxLoDiffuseVal)
                {
                    frac = a / (Utility.maxLoDiffuseVal);
                    vect = loDiffusePows;
                }
                else
                {
                    frac = a / Utility.maxDiffuseVal;
                    vect = diffusePows;
                }

                double vel = TabledInterpolater.getValue(vect, frac);

                if (hydgrad.v < 0)
                {
                    flow = -xsectcur.v * vel;
                }
                else
                {
                    flow = xsectcur.v * vel;
                }
            }
        }
        else
        {
            Logger.logError(this + ": Invalid equation type specified: " + eqtype.v);
            flow = 0;
        }

        if (Double.isNaN(flow))
            Logger.logError("Severe model instability.  Flow is not a number in edge " + myHolon.getUID().toString());

        return flow;
    }

}
