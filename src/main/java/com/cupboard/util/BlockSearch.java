package com.cupboard.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

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
      final BlockGetter world,
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

        BlockPos.MutableBlockPos temp = new BlockPos.MutableBlockPos();
        int y = 0;
        int y_offset = yStep;

        boolean checkLoaded = world instanceof Level;
        Level level = checkLoaded ? (Level) world : null;

        for (int i = 0; i < verticalRange + 2; i++)
        {
            for (int steps = 1; steps <= horizontalRange; steps++)
            {
                // Start topleft of middle point
                temp.set(start.getX() - steps, start.getY() + y, start.getZ() - steps);

                // X ->
                for (int x = 0; x <= steps; x++)
                {
                    temp.set(temp.getX() + 1, temp.getX(), temp.getZ());

                    if (checkLoaded)
                    {
                        if (level.hasChunk(temp.getX() >> 4, temp.getZ() >> 4))
                        {

                            if (predicate.test(world, temp))
                            {
                                return temp;
                            }
                        }
                    }
                    else
                    {
                        if (predicate.test(world, temp))
                        {
                            return temp;
                        }
                    }
                }

                // X
                // |
                // v
                for (int z = 0; z <= steps; z++)
                {
                    temp.set(temp.getX(), temp.getY(), temp.getZ() + 1);

                    if (checkLoaded)
                    {
                        if (level.hasChunk(temp.getX() >> 4, temp.getZ() >> 4))
                        {
                            if (predicate.test(world, temp))
                            {
                                return temp;
                            }
                        }
                    }
                    else
                    {
                        if (predicate.test(world, temp))
                        {
                            return temp;
                        }
                    }
                }

                // < - X
                for (int x = 0; x <= steps; x++)
                {
                    temp.set(temp.getX() - 1, temp.getY(), temp.getZ());
                    if (checkLoaded)
                    {
                        if (level.hasChunk(temp.getX() >> 4, temp.getZ() >> 4))
                        {
                            if (predicate.test(world, temp))
                            {
                                return temp;
                            }
                        }
                    }
                    else
                    {
                        if (predicate.test(world, temp))
                        {
                            return temp;
                        }
                    }
                }

                // ^
                // |
                // X
                for (int z = 0; z <= steps; z++)
                {
                    temp.set(temp.getX(), temp.getY(), temp.getZ() - 1);
                    if (checkLoaded)
                    {
                        if (level.hasChunk(temp.getX() >> 4, temp.getZ() >> 4))
                        {
                            if (predicate.test(world, temp))
                            {
                                return temp;
                            }
                        }
                    }
                    else
                    {
                        if (predicate.test(world, temp))
                        {
                            return temp;
                        }
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
