package com.cupboard.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;

import java.util.function.BiPredicate;

public class BlockSearch
{
    /**
     * Predicate for pos selection
     */
    final BiPredicate<BlockGetter, BlockPos> DOUBLE_AIR        =
      (world, pos) -> world.getBlockState(pos).isAir() && world.getBlockState(pos.above()).isAir();
    final BiPredicate<BlockGetter, BlockPos> DOUBLE_AIR_GROUND = DOUBLE_AIR.and((world, pos) -> world.getBlockState(pos.below()).getMaterial().isSolid());

    /**
     * Finds a nice position around a given start, creeping outwards
     *
     * @param world
     * @param start
     * @param horizontalRange
     * @param verticalRange
     * @param yStep
     * @param predicate
     * @return
     */
    public static BlockPos findAround(
      final ServerLevel world,
      final BlockPos start,
      final int verticalRange,
      final int horizontalRange,
      final int yStep,
      final BiPredicate<BlockGetter, BlockPos> predicate)
    {
        if (horizontalRange < 1 && verticalRange < 1)
        {
            return null;
        }

        BlockPos temp;
        int y = 0;
        int y_offset = yStep;

        for (int i = 0; i < verticalRange + 2; i++)
        {
            for (int steps = 1; steps <= horizontalRange; steps++)
            {
                // Start topleft of middle point
                temp = start.offset(-steps, y, -steps);

                // X ->
                for (int x = 0; x <= steps; x++)
                {
                    temp = temp.offset(1, 0, 0);
                    if (predicate.test(world, temp))
                    {
                        return temp;
                    }
                }

                // X
                // |
                // v
                for (int z = 0; z <= steps; z++)
                {
                    temp = temp.offset(0, 0, 1);
                    if (predicate.test(world, temp))
                    {
                        return temp;
                    }
                }

                // < - X
                for (int x = 0; x <= steps; x++)
                {
                    temp = temp.offset(-1, 0, 0);
                    if (predicate.test(world, temp))
                    {
                        return temp;
                    }
                }

                // ^
                // |
                // X
                for (int z = 0; z <= steps; z++)
                {
                    temp = temp.offset(0, 0, -1);
                    if (predicate.test(world, temp))
                    {
                        return temp;
                    }
                }
            }

            y += y_offset;
            y_offset = y_offset > 0 ? y_offset + 1 : y_offset - 1;
            y_offset *= -1;
        }

        return null;
    }
}
