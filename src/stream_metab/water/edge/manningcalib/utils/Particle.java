package stream_metab.water.edge.manningcalib.utils;

import neo.holon.Boundary;
import neo.holon.Edge;
import neo.holon.Holon;
import neo.key.Key;
import neo.state.HStateDbl;
import neo.state.HStateNotFoundException;

public class Particle {
    
    private Edge currentEdge;
    private Edge endEdge;
    private boolean isDone;
    private HStateDbl currentVelocityState;
    private HStateDbl currentEdgeLengthState;
    private double currentDistance;
    private double travelTime;

    public Particle(Edge startEdge, Edge endEdge) throws HStateNotFoundException
    {
        setEdge(startEdge);
        this.endEdge = endEdge;
        isDone = false;
        travelTime = 0;
    }
    
    public boolean isDone()
    {
        return isDone;
    }

    public double getTravelTime()
    {
        return travelTime;
    }
    
    public void process(double timeRemaining) throws HStateNotFoundException
    {
        if (!isDone)
        {
            // Determine if the particle should advance out of the current edge
            double advanced = currentDistance + timeRemaining * currentVelocityState.v;
            if (advanced > currentEdgeLengthState.v)
            {
                // Particle goes to new edge
                
                // Time consumed in leaving the current edge
                double timeConsumed = (currentEdgeLengthState.v - currentDistance) / currentVelocityState.v;
                travelTime += timeConsumed;
                
                // Advance to the next edge
                Holon[] manningEdges = currentEdge.getTo().getAttachedHolons(Key.cast("Water"), "manning");
                for (Holon manningEdge: manningEdges)
                {
                    if (!manningEdge.equals(currentEdge))
                    {
                        setEdge(((Edge)manningEdge).getTwin());
                        break;
                    }
                }
                
                // Check if at last edge
                if (currentEdge.equals(endEdge))
                {
                    // At last edge
                    isDone = true;
                }
                else
                {
                    // Not at last edge
                    
                    // Process remaining time spent in new edge (recursive)
                    process(timeRemaining - timeConsumed);
                }
            }
            else
            {
                // All time accounted in current cell
                currentDistance = advanced;
                travelTime += timeRemaining;
            }
        }
    }

    private void setEdge(Edge edge) throws HStateNotFoundException
    {
        currentEdge = edge;
        currentDistance = 0;
        currentVelocityState = (HStateDbl)currentEdge.getHState("Velocity");
        currentEdgeLengthState = (HStateDbl)currentEdge.getHState("LinkLength");
    }

}
