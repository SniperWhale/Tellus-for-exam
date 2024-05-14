package Blocks.Gases;

import java.util.concurrent.ThreadLocalRandom;

import Blocks.Air;
import Blocks.Particle;
import Blocks.Solids.SolidParticle;
import Window.Grid;

abstract class GasParticle extends Particle {

    boolean hasMovedLastFrame;

    // for how many frames it will live for
    int lifetime = Math.max(200, ThreadLocalRandom.current().nextInt(300));
    int hasLivedFor = 0; // how many frames particle has been alive for in respect of lifetime

    public GasParticle() {
        super();

        isRising = true;
        scanDirection = 2; // top to bottom

        canBeOverridden = true;

        // spawnRate = 0.03f;
        spawnRate = 0.01f;
    }

    public void setLifetime(int lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public int[] update(int[] coords, Grid grid) {
        super.update(coords, grid);

        hasLivedFor++;
        // has lived a fulfilling life and now needs to go
        if (hasLivedFor >= lifetime) {
            grid.setParticle(coords[0], coords[1], new Air());
            return coords;
        }


        hasMovedLastFrame = !hasMovedLastFrame;
        if (hasMovedLastFrame) return coords;

        // change color to more dark the more it has lived
        setColors(
            this.getColorRed()  - 1,
            this.getColorGreen() - 1,
            this.getColorBlue() - 1
        );

        // TODO: add in grid second pass to update from top to bottom

        Particle[] upper = grid.getUpperNeighbors(coords[0], coords[1]);
        if (upper[1] == null) {
            isRising = false;
            return coords;
        }

        isRising = upper[1] instanceof Air || upper[1].isRising
            || !(coords[0] == previousPosition[0] && coords[1] == previousPosition[1]);
        if (isRising) previousPosition = coords.clone();

        // if block upper is not a solid swap with block upper
        if (isRising && upper[1] != null && !(upper[1] instanceof SolidParticle || upper[1] instanceof GasParticle)) {
            grid.setParticle(coords[0], coords[1], grid.getAtPosition(coords[0] - 1, coords[1]));
            grid.setParticle(coords[0] - 1, coords[1], this);
            coords[0]--;
        }

        // go to block to left if is not solid
        else if (upper[0] != null && !(upper[0] instanceof SolidParticle || upper[0] instanceof GasParticle)) {
            grid.setParticle(coords[0], coords[1], grid.getAtPosition(coords[0] - 1, coords[1] - 1));
            grid.setParticle(coords[0] - 1, coords[1] - 1, this);
            coords[0]--;
            coords[1]--;
        }

        // go to block to right if is not solid
        else if (upper[2] != null && !(upper[2] instanceof SolidParticle || upper[2] instanceof GasParticle)) {
            grid.setParticle(coords[0], coords[1], grid.getAtPosition(coords[0] - 1, coords[1] + 1));
            grid.setParticle(coords[0] - 1, coords[1] + 1, this);
            coords[0]--;
            coords[1]++;
        }

        Particle[] side = grid.getSideNeighbors(coords[0], coords[1]);

        if (side[0] != null && ThreadLocalRandom.current().nextInt(2) == 0 && !(side[0] instanceof SolidParticle || side[0] instanceof GasParticle)) {
            grid.setParticle(coords[0], coords[1], grid.getAtPosition(coords[0], coords[1] - 1));
            grid.setParticle(coords[0], coords[1] - 1, this);
            coords[1]--;
        }
        else if (side[1] != null && !(side[1] instanceof SolidParticle || side[1] instanceof GasParticle)) {
            grid.setParticle(coords[0], coords[1], grid.getAtPosition(coords[0], coords[1] + 1));
            grid.setParticle(coords[0], coords[1] + 1, this);
            coords[1]++;
        }


        return coords;
    }

}
