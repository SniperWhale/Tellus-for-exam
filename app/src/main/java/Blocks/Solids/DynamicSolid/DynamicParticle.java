package Blocks.Solids.DynamicSolid;

import Blocks.Particle;
import Blocks.Solids.Solid;
import Window.Grid;



abstract class DynamicParticle extends Solid {

    private int maxSpeed = 0; // how many cells to move ain one frame
    private float acceleration = 0; // 32bits, will never need more
    private float velocity = 0;
    
    public DynamicParticle() {
        super();
    }

    public void setMaxSpeed(int max) {
        maxSpeed = max;
    }

    public void setAcceleration(float accel) {
        acceleration = accel;
    }

    public void resetVelocity() {
        velocity = 0;
    }

    public float getVelocity() {
        return velocity;
    }

    public void updateVelocity() {
        velocity = Math.min(velocity + acceleration, maxSpeed);
    }

    @Override
    public void update(int j, int i, Grid grid) {
        updateVelocity();

        for (int n = 0; n <= velocity; n++) {

            Particle[] under = grid.getLowerNeighbors(j, i);
            if (under[1] == null) return; // cannot move or you finish out of bounds

            // NOTE: it always swaps with the cell it goes to
            // make smoke disappear (if it is gas it creates air and doesnt make the gas rise)

            if (under[1] instanceof Solid) resetVelocity();

            // if block under is not a solid swap with block under
            if (!(under[1] instanceof Solid)) {
                grid.setParticle(j, i, grid.getAtPosition(j + 1, i));
                grid.setParticle(j + 1, i, this);
                j = j + 1;
            }
            // go to block to left if is not solid
            else if (under[0] != null && !(under[0] instanceof Solid)) {
                grid.setParticle(j, i, grid.getAtPosition(j + 1, i - 1));
                grid.setParticle(j + 1, i - 1, this);
                j = j + 1;
                i = i - 1;
            }

            // go to block to right if is not solid
            else if (under[2] != null && !(under[2] instanceof Solid)) {
                grid.setParticle(j, i, grid.getAtPosition(j + 1, i + 1));
                grid.setParticle(j + 1, i + 1, this);
                j = j + 1;
                i = i + 1;
            }

        }


    }

    
}
