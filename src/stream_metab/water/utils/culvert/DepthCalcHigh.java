package stream_metab.water.utils.culvert;

import stream_metab.water.utils.DepthCalculator;
import neo.state.HStateDbl;

/**
 * Provides a method to calculate depth at the high end of a culvert.
 * <p>
 * Minimum depth will be zero. There is no maximum, so the depth may be greater
 * than the diameter of the culvert. Diameter conditions must be checked by
 * other code.
 * </p>
 * 
 * @author robert.payn
 */
public class DepthCalcHigh extends DepthCalculator {

    /**
     * Constructs an instance of the calculator
     * 
     * @param elevBase
     *            - elevation of the base of the culvert at the high end
     * @param headPatch
     *            - Hydraulic head in the patch at the high end of the culvert
     *            [Length]
     * @param depthPatch
     *            - Depth of water in the patch at the high end of the culvert
     *            [Length]
     */
    public DepthCalcHigh(double elevBase, HStateDbl headPatch, HStateDbl depthPatch)
    {

        super(elevBase, headPatch, depthPatch);

    }

    /**
     * Calculates depth at the high end of the culvert
     * 
     * @return water depth [Length]
     */
    @Override
    public double calcDepth()
    {

        return Math.max(0, Math.min(depthPatch.v, headPatch.v - elevBase));

    }

}
