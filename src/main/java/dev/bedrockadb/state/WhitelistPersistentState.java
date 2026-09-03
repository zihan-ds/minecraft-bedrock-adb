package dev.bedrockadb.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.PersistentState.Type;

public class WhitelistPersistentState extends PersistentState {
   private static final String KEY = "bedrock_adb";
   private static final Type<WhitelistPersistentState> TYPE = new Type<>(
      WhitelistPersistentState::new, WhitelistPersistentState::fromNbt, DataFixTypes.SAVED_DATA_MAP_DATA
   );
   private final List<String> whitelist = new ArrayList<>();

   public static WhitelistPersistentState get(ServerWorld world) {
      PersistentStateManager manager = world.getPersistentStateManager();
      return (WhitelistPersistentState)manager.getOrCreate(TYPE, "bedrock_adb");
   }

   private static WhitelistPersistentState fromNbt(NbtCompound nbt, WrapperLookup wrapperLookup) {
      WhitelistPersistentState state = new WhitelistPersistentState();
      NbtList list = nbt.getList("whitelist", 8);

      for (int i = 0; i < list.size(); i++) {
         state.whitelist.add(list.getString(i));
      }

      return state;
   }

   public NbtCompound writeNbt(NbtCompound nbt, WrapperLookup registryLookup) {
      NbtList list = new NbtList();

      for (String name : this.whitelist) {
         list.add(NbtString.of(name));
      }

      nbt.put("whitelist", list);
      return nbt;
   }

   public boolean isWhitelisted(String name) {
      return this.whitelist.contains(normalize(name));
   }

   public boolean add(String name) {
      String normalized = normalize(name);
      if (this.whitelist.contains(normalized)) {
         return false;
      } else {
         this.whitelist.add(normalized);
         this.markDirty();
         return true;
      }
   }

   public boolean remove(String name) {
      if (this.whitelist.remove(normalize(name))) {
         this.markDirty();
         return true;
      } else {
         return false;
      }
   }

   public void clear() {
      this.whitelist.clear();
      this.markDirty();
   }

   public List<String> list() {
      return List.copyOf(this.whitelist);
   }

   private static String normalize(String name) {
      return name.toLowerCase(Locale.ROOT);
   }
}
