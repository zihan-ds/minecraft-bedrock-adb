package dev.bedrockadb.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.bedrockadb.config.BorderBlockMode;
import dev.bedrockadb.config.ModConfig;
import dev.bedrockadb.rule.RegionRule;
import dev.bedrockadb.state.WhitelistPersistentState;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class ModCommands {
   private ModCommands() {
   }

   public static void register() {
      CommandRegistrationCallback.EVENT.register((CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> register(dispatcher));
   }

   private static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal(
                           "allowdeny"
                        )
                        .requires(source -> source.hasPermissionLevel(2)))
                     .then(
                        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("whitelist")
                                    .then(
                                       CommandManager.literal("add")
                                          .then(
                                             CommandManager.argument("player", StringArgumentType.word())
                                                .executes(
                                                   ctx -> {
                                                      String name = StringArgumentType.getString(ctx, "player");
                                                      WhitelistPersistentState state = WhitelistPersistentState.get(
                                                         ((ServerCommandSource)ctx.getSource()).getWorld()
                                                      );
                                                      boolean added = state.add(name);
                                                      ModNetworking.sendSync(((ServerCommandSource)ctx.getSource()).getServer());
                                                      ((ServerCommandSource)ctx.getSource())
                                                         .sendFeedback(
                                                            () -> Text.translatable(
                                                                  added ? "command.bedrock_adb.whitelist.add" : "command.bedrock_adb.whitelist.already",
                                                                  new Object[]{name}
                                                               ),
                                                            true
                                                         );
                                                      return 1;
                                                   }
                                                )
                                          )
                                    ))
                                 .then(
                                    CommandManager.literal("remove")
                                       .then(
                                          CommandManager.argument("player", StringArgumentType.word())
                                             .executes(
                                                ctx -> {
                                                   String name = StringArgumentType.getString(ctx, "player");
                                                   WhitelistPersistentState state = WhitelistPersistentState.get(
                                                      ((ServerCommandSource)ctx.getSource()).getWorld()
                                                   );
                                                   boolean removed = state.remove(name);
                                                   ModNetworking.sendSync(((ServerCommandSource)ctx.getSource()).getServer());
                                                   ((ServerCommandSource)ctx.getSource())
                                                      .sendFeedback(
                                                         () -> Text.translatable(
                                                               removed ? "command.bedrock_adb.whitelist.remove" : "command.bedrock_adb.whitelist.not_found",
                                                               new Object[]{name}
                                                            ),
                                                         true
                                                      );
                                                   return 1;
                                                }
                                             )
                                       )
                                 ))
                              .then(
                                 CommandManager.literal("list")
                                    .executes(
                                       ctx -> {
                                          WhitelistPersistentState state = WhitelistPersistentState.get(((ServerCommandSource)ctx.getSource()).getWorld());
                                          ((ServerCommandSource)ctx.getSource())
                                             .sendFeedback(
                                                () -> Text.translatable("command.bedrock_adb.whitelist.list", new Object[]{String.join(", ", state.list())}),
                                                false
                                             );
                                          return 1;
                                       }
                                    )
                              ))
                           .then(CommandManager.literal("clear").executes(ctx -> {
                              WhitelistPersistentState state = WhitelistPersistentState.get(((ServerCommandSource)ctx.getSource()).getWorld());
                              state.clear();
                              ModNetworking.sendSync(((ServerCommandSource)ctx.getSource()).getServer());
                              ((ServerCommandSource)ctx.getSource()).sendFeedback(() -> Text.translatable("command.bedrock_adb.whitelist.clear"), true);
                              return 1;
                           }))
                     ))
                  .then(
                     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("config")
                              .then(
                                 CommandManager.literal("requireCreative")
                                    .then(
                                       CommandManager.argument("value", BoolArgumentType.bool())
                                          .executes(
                                             ctx -> {
                                                ModConfig.get().setRequireCreative(BoolArgumentType.getBool(ctx, "value"));
                                                ModConfig.save();
                                                ModNetworking.sendSync(((ServerCommandSource)ctx.getSource()).getServer());
                                                ((ServerCommandSource)ctx.getSource())
                                                   .sendFeedback(
                                                      () -> Text.translatable(
                                                            "command.bedrock_adb.config.require_creative", new Object[]{ModConfig.get().requireCreative()}
                                                         ),
                                                      true
                                                   );
                                                return 1;
                                             }
                                          )
                                    )
                              ))
                           .then(
                              CommandManager.literal("opBypass")
                                 .then(
                                    CommandManager.argument("value", BoolArgumentType.bool())
                                       .executes(
                                          ctx -> {
                                             ModConfig.get().setOpBypass(BoolArgumentType.getBool(ctx, "value"));
                                             ModConfig.save();
                                             ModNetworking.sendSync(((ServerCommandSource)ctx.getSource()).getServer());
                                             ((ServerCommandSource)ctx.getSource())
                                                .sendFeedback(
                                                   () -> Text.translatable("command.bedrock_adb.config.op_bypass", new Object[]{ModConfig.get().opBypass()}),
                                                   true
                                                );
                                             return 1;
                                          }
                                       )
                                 )
                           ))
                        .then(
                           CommandManager.literal("showBorderEffect")
                              .then(
                                 CommandManager.argument("value", BoolArgumentType.bool())
                                    .executes(
                                       ctx -> {
                                          ModConfig.get().setShowBorderEffect(BoolArgumentType.getBool(ctx, "value"));
                                          ModConfig.save();
                                          ModNetworking.sendSync(((ServerCommandSource)ctx.getSource()).getServer());
                                          ((ServerCommandSource)ctx.getSource())
                                             .sendFeedback(
                                                () -> Text.translatable(
                                                      "command.bedrock_adb.config.show_border_effect", new Object[]{ModConfig.get().showBorderEffect()}
                                                   ),
                                                true
                                             );
                                          return 1;
                                       }
                                    )
                              )
                        )
                  ))
               .then(
                  CommandManager.literal("borderMode")
                     .then(
                        CommandManager.literal("bedrock_like")
                           .executes(ctx -> {
                              ModConfig.get().setBorderBlockMode(BorderBlockMode.BEDROCK_LIKE);
                              ModConfig.save();
                              ModNetworking.sendSync(((ServerCommandSource)ctx.getSource()).getServer());
                              ((ServerCommandSource)ctx.getSource())
                                 .sendFeedback(
                                    () -> Text.translatable(
                                          "command.bedrock_adb.config.border_mode", new Object[]{"bedrock_like"}
                                       ),
                                    true
                                 );
                              return 1;
                           })
                     )
                     .then(
                        CommandManager.literal("block_all_entities")
                           .executes(ctx -> {
                              ModConfig.get().setBorderBlockMode(BorderBlockMode.BLOCK_ALL_ENTITIES);
                              ModConfig.save();
                              ModNetworking.sendSync(((ServerCommandSource)ctx.getSource()).getServer());
                              ((ServerCommandSource)ctx.getSource())
                                 .sendFeedback(
                                    () -> Text.translatable(
                                          "command.bedrock_adb.config.border_mode", new Object[]{"block_all_entities"}
                                       ),
                                    true
                                 );
                              return 1;
                           })
                     )
               )
               .then(CommandManager.literal("reload").executes(ctx -> {
                  ModConfig.load();
                  ModNetworking.sendSync(((ServerCommandSource)ctx.getSource()).getServer());
                  ((ServerCommandSource)ctx.getSource()).sendFeedback(() -> Text.translatable("command.bedrock_adb.reload"), true);
                  return 1;
               })))
            .then(
               CommandManager.literal("query")
                  .then(
                     CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                        .executes(
                           ctx -> {
                              BlockPos pos = BlockPosArgumentType.getLoadedBlockPos(ctx, "pos");
                              World world = ((ServerCommandSource)ctx.getSource()).getWorld();
                              RegionRule rule = RegionRule.findBelow(world, pos);
                              if (rule == null) {
                                 ((ServerCommandSource)ctx.getSource())
                                    .sendFeedback(
                                       () -> Text.translatable("command.bedrock_adb.query.none", new Object[]{pos.getX(), pos.getY(), pos.getZ()}), false
                                    );
                              } else {
                                 String type = rule.allows() ? "allow" : "deny";
                                 ((ServerCommandSource)ctx.getSource())
                                    .sendFeedback(
                                       () -> Text.translatable(
                                             "command.bedrock_adb.query.found",
                                             new Object[]{type, rule.pos().getX(), rule.pos().getY(), rule.pos().getZ(), pos.getX(), pos.getY(), pos.getZ()}
                                          ),
                                       false
                                    );
                              }

                              return 1;
                           }
                        )
                  )
            )
      );
   }
}
