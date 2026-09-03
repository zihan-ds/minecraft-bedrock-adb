package dev.bedrockadb.block.entity;

import dev.bedrockadb.registry.ModBlockEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;

public class UtilityBlockEntity extends BlockEntity {
   private static final String WHITELIST_KEY = "whitelist";
   private final List<String> whitelist = new ArrayList<>();

   public UtilityBlockEntity(BlockPos pos, BlockState state) {
      super(ModBlockEntities.UTILITY, pos, state);
   }

   public boolean isWhitelisted(String name) {
      return this.whitelist.contains(name.toLowerCase(Locale.ROOT));
   }

   public void setWhitelist(List<String> names) {
      this.whitelist.clear();

      for (String name : names) {
         this.whitelist.add(name.toLowerCase(Locale.ROOT));
      }

      this.markDirty();
   }

   public List<String> getWhitelist() {
      return List.copyOf(this.whitelist);
   }

   public void readNbt(NbtCompound nbt, WrapperLookup registryLookup) {
      super.readNbt(nbt, registryLookup);
      this.whitelist.clear();
      NbtList list = nbt.getList("whitelist", 8);

      for (int i = 0; i < list.size(); i++) {
         this.whitelist.add(list.getString(i));
      }
   }

   protected void writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
      super.writeNbt(nbt, registryLookup);
      NbtList list = new NbtList();

      for (String name : this.whitelist) {
         list.add(NbtString.of(name));
      }

      nbt.put("whitelist", list);
   }
}
