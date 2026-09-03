package dev.bedrockadb.state;

import dev.bedrockadb.registry.ModBlocks;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

/**
 * Tracks, per world instance and per logical side, every loaded
 * {@code bedrock_adb:border_block}, grouped by its world column (x, z)
 * together with the Y coordinate of each border block in that column.
 *
 * <p>This index backs the "vertical column barrier": a border block makes its
 * whole column impassable from the world bottom to the world top, so entities
 * that are not allowed to pass cannot cross over or under it at any height.
 * Movement collisions are computed from block cells around the entity, so a
 * shape placed at the border block's own cell can never reach an entity flying
 * above it - the barrier therefore has to be injected into the collision
 * pipeline (see the entity mixin) using this index.
 *
 * <p>Each logical side maintains its own index from the same block states, so
 * the host (integrated server) and LAN clients stay consistent. Border columns
 * are loaded/unloaded together with their chunk (a column maps to exactly one
 * chunk) and are updated on any block change of the border block itself.
 */
public final class BorderColumnIndex {
   private static final Map<World, Data> ALL = Collections.synchronizedMap(new WeakHashMap<>());

   private static final class Data {
      /** packed column (x,z) -> sorted Y of every loaded border block in that column */
      final Map<Long, NavigableSet<Integer>> columns = new HashMap<>();
      /** packed chunk pos -> packed columns that contributed borders to that chunk */
      final Map<Long, Set<Long>> chunkColumns = new HashMap<>();
   }

   private BorderColumnIndex() {
   }

   // ------------------------------------------------------------------
   // Maintenance (called from chunk events and the World setBlockState mixin)
   // ------------------------------------------------------------------

   public static void onChunkLoad(World world, WorldChunk chunk) {
      Data data = data(world);
      synchronized (data) {
         for (BlockEntity entity : chunk.getBlockEntities().values()) {
            if (entity.getCachedState().isOf(ModBlocks.BORDER_BLOCK)) {
               addBorder(data, world, entity.getPos());
            }
         }
      }
   }

   public static void onChunkUnload(World world, WorldChunk chunk) {
      Data data = data(world);
      synchronized (data) {
         Set<Long> columns = data.chunkColumns.remove(chunkKey(chunk));
         if (columns != null) {
            for (long column : columns) {
               data.columns.remove(column);
            }
         }
      }
   }

   /** Called when a border block was placed (or replaced by one) at {@code pos}. */
   public static void onBorderPlaced(World world, BlockPos pos) {
      Data data = data(world);
      synchronized (data) {
         addBorder(data, world, pos);
      }
   }

   /** Called when the block at {@code pos} is no longer a border block. */
   public static void onBorderRemoved(World world, BlockPos pos) {
      Data data = data(world);
      synchronized (data) {
         long column = columnKey(pos.getX(), pos.getZ());
         NavigableSet<Integer> ys = data.columns.get(column);
         if (ys == null || !ys.remove(pos.getY())) {
            return; // not tracked (yet) - nothing to do
         }
         if (ys.isEmpty()) {
            data.columns.remove(column);
            Set<Long> columnsOfChunk = data.chunkColumns.get(chunkKey(pos));
            if (columnsOfChunk != null) {
               columnsOfChunk.remove(column);
               if (columnsOfChunk.isEmpty()) {
                  data.chunkColumns.remove(chunkKey(pos));
               }
            }
         }
      }
   }

   // ------------------------------------------------------------------
   // Queries (hot path of the collision mixin)
   // ------------------------------------------------------------------

   /** Whether the column at (x, z) currently contains at least one loaded border block. */
   public static boolean hasBorder(World world, int x, int z) {
      Data data = ALL.get(world);
      if (data == null) {
         return false;
      }
      synchronized (data) {
         NavigableSet<Integer> ys = data.columns.get(columnKey(x, z));
         return ys != null && !ys.isEmpty();
      }
   }

   /** Y coordinates of the loaded border blocks in the column at (x, z). */
   public static List<Integer> borderYs(World world, int x, int z) {
      Data data = ALL.get(world);
      if (data == null) {
         return List.of();
      }
      synchronized (data) {
         NavigableSet<Integer> ys = data.columns.get(columnKey(x, z));
         return ys == null ? List.of() : new ArrayList<>(ys);
      }
   }

   // ------------------------------------------------------------------
   // Internals
   // ------------------------------------------------------------------

   private static void addBorder(Data data, World world, BlockPos pos) {
      long column = columnKey(pos.getX(), pos.getZ());
      NavigableSet<Integer> ys = data.columns.computeIfAbsent(column, c -> new TreeSet<>());
      if (ys.add(pos.getY())) {
         data.chunkColumns.computeIfAbsent(chunkKey(pos), c -> new HashSet<>()).add(column);
      }
   }

   private static Data data(World world) {
      synchronized (ALL) {
         return ALL.computeIfAbsent(world, w -> new Data());
      }
   }

   private static long columnKey(int x, int z) {
      return ((long) x << 32) ^ (z & 0xFFFFFFFFL);
   }

   private static long chunkKey(WorldChunk chunk) {
      return chunkKey(chunk.getPos().x, chunk.getPos().z);
   }

   private static long chunkKey(BlockPos pos) {
      return chunkKey(pos.getX() >> 4, pos.getZ() >> 4);
   }

   private static long chunkKey(int cx, int cz) {
      return ((long) cx << 32) ^ (cz & 0xFFFFFFFFL);
   }
}
