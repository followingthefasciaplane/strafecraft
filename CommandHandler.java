import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.EnumArgumentType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandHandler {
    private static final Map<UUID, Boolean> playerPluginEnabled = new HashMap<>();
    private static final Map<String, PermissionLevel> commandPermissions = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger("SourceMovement");

    private final PlayerMovementManager playerMovementManager;

    public CommandHandler(PlayerMovementManager playerMovementManager) {
        this.playerMovementManager = playerMovementManager;
        initializeCommandPermissions();
    }

    private void initializeCommandPermissions() {
        commandPermissions.put("bhoptarget", PermissionLevel.valueOf(SourceMovementConfig.BHOP_TARGET_PERMISSION.get()));
        commandPermissions.put("bhop", PermissionLevel.valueOf(SourceMovementConfig.BHOP_PERMISSION.get()));
        commandPermissions.put("permissions", PermissionLevel.valueOf(SourceMovementConfig.PERMISSIONS_PERMISSION.get()));
        commandPermissions.put("setPermission", PermissionLevel.valueOf(SourceMovementConfig.SET_PERMISSION_PERMISSION.get()));
        commandPermissions.put("airAcceleration", PermissionLevel.valueOf(SourceMovementConfig.AIR_ACCELERATION_PERMISSION.get()));
        commandPermissions.put("airStrafeSpeed", PermissionLevel.valueOf(SourceMovementConfig.AIR_STRAFE_SPEED_PERMISSION.get()));
        commandPermissions.put("groundSpeed", PermissionLevel.valueOf(SourceMovementConfig.GROUND_SPEED_PERMISSION.get()));
        commandPermissions.put("bunnyHopMultiplier", PermissionLevel.valueOf(SourceMovementConfig.BUNNY_HOP_MULTIPLIER_PERMISSION.get()));
        commandPermissions.put("maxAirSpeed", PermissionLevel.valueOf(SourceMovementConfig.MAX_AIR_SPEED_PERMISSION.get()));
        commandPermissions.put("friction", PermissionLevel.valueOf(SourceMovementConfig.FRICTION_PERMISSION.get()));
        commandPermissions.put("config", PermissionLevel.valueOf(SourceMovementConfig.CONFIG_PERMISSION.get()));
        commandPermissions.put("help", PermissionLevel.valueOf(SourceMovementConfig.HELP_PERMISSION.get()));
    }

    public void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("sourcemovement")
                .then(Commands.literal("bhoptarget")
                        .requires(this::hasPermission)
                        .then(Commands.argument("targets", EntityArgument.players())
                                .then(Commands.argument("enabled", BoolArgumentType.bool())
                                        .executes(this::executeBhopTarget))))
                .then(Commands.literal("bhop")
                        .requires(this::hasPermission)
                        .executes(this::executeBhop))
                .then(Commands.literal("permissions")
                        .requires(this::hasPermission)
                        .executes(this::executePermissions))
                .then(Commands.literal("setPermission")
                        .requires(this::hasPermission)
                        .then(Commands.argument("command", com.mojang.brigadier.arguments.StringArgumentType.string())
                                .then(Commands.argument("level", EnumArgumentType.enumArgument(PermissionLevel.class))
                                        .executes(this::executeSetPermission))))
                .then(Commands.literal("airAcceleration")
                        .requires(this::hasPermission)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, SourceMovementConfig.MAX_AIR_ACCELERATION.get()))
                                .executes(this::executeAirAcceleration)))
                .then(Commands.literal("airStrafeSpeed")
                        .requires(this::hasPermission)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, SourceMovementConfig.MAX_AIR_STRAFE_SPEED.get()))
                                .executes(this::executeAirStrafeSpeed)))
                .then(Commands.literal("groundSpeed")
                        .requires(this::hasPermission)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, SourceMovementConfig.MAX_GROUND_SPEED.get()))
                                .executes(this::executeGroundSpeed)))
                .then(Commands.literal("bunnyHopMultiplier")
                        .requires(this::hasPermission)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, SourceMovementConfig.MAX_BUNNY_HOP_MULTIPLIER.get()))
                                .executes(this::executeBunnyHopMultiplier)))
                .then(Commands.literal("maxAirSpeed")
                        .requires(this::hasPermission)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, SourceMovementConfig.MAX_MAX_AIR_SPEED.get()))
                                .executes(this::executeMaxAirSpeed)))
                .then(Commands.literal("friction")
                        .requires(this::hasPermission)
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0, SourceMovementConfig.MAX_FRICTION.get()))
                                .executes(this::executeFriction)))
                .then(Commands.literal("config")
                        .requires(this::hasPermission)
                        .executes(this::executeConfig))
                .then(Commands.literal("help")
                        .requires(this::hasPermission)
                        .executes(this::executeHelp)));
    }

    private boolean hasPermission(CommandSource source) {
        try {
            ServerPlayerEntity player = source.asPlayer();
            String command = source.getInput().split(" ")[1];
            PermissionLevel requiredLevel = commandPermissions.getOrDefault(command, PermissionLevel.PLAYER);
            return player.hasPermissionLevel(requiredLevel.getLevel());
        } catch (CommandSyntaxException e) {
            return false;
        }
    }

    private int executeBhopTarget(CommandContext<CommandSource> context) throws CommandSyntaxException {
        boolean enabled = BoolArgumentType.getBool(context, "enabled");
        for (ServerPlayerEntity player : EntityArgument.getPlayers(context, "targets")) {
            UUID playerUUID = player.getUniqueID();
            playerPluginEnabled.put(playerUUID, enabled);
            sendMessageToPlayer(player, "Your Source Movement has been " + (enabled ? "enabled" : "disabled") + " by an administrator.");
        }
        sendFeedback(context.getSource(), "Source Movement " + (enabled ? "enabled" : "disabled") + " for " + EntityArgument.getPlayers(context, "targets").size() + " player(s).");
        return 1;
    }

    private int executeBhop(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        boolean enabled = !playerPluginEnabled.getOrDefault(player.getUniqueID(), false);
        playerPluginEnabled.put(player.getUniqueID(), enabled);
        sendFeedback(context.getSource(), "Source Movement " + (enabled ? "enabled" : "disabled") + " for " + player.getName().getString());
        return 1;
    }

    private int executePermissions(CommandContext<CommandSource> context) {
        sendFeedback(context.getSource(), "Current command permissions:");
        for (Map.Entry<String, PermissionLevel> entry : commandPermissions.entrySet()) {
            sendFeedback(context.getSource(), "- " + entry.getKey() + ": " + entry.getValue().name());
        }
        return 1;
    }

    private int executeSetPermission(CommandContext<CommandSource> context) {
        String command = context.getArgument("command", String.class);
        PermissionLevel newLevel = context.getArgument("level", PermissionLevel.class);
        if (commandPermissions.containsKey(command)) {
            commandPermissions.put(command, newLevel);
            sendFeedback(context.getSource(), "Permission level for '" + command + "' set to " + newLevel.name());
            LOGGER.info("Permission level for '" + command + "' changed to " + newLevel.name() + " by " + context.getSource().getName());
        } else {
            sendErrorMessage(context.getSource(), "Invalid command: " + command);
        }
        return 1;
    }

    private int executeAirAcceleration(CommandContext<CommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        SourceMovementConfig.AIR_ACCELERATION.set(value);
        sendFeedback(context.getSource(), "Air acceleration set to " + value);
        return 1;
    }
    
    private int executeAirStrafeSpeed(CommandContext<CommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        SourceMovementConfig.AIR_STRAFE_SPEED.set(value);
        sendFeedback(context.getSource(), "Air strafe speed set to " + value);
        return 1;
    }
    
    private int executeGroundSpeed(CommandContext<CommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        SourceMovementConfig.GROUND_SPEED.set(value);
        sendFeedback(context.getSource(), "Ground speed set to " + value);
        return 1;
    }
    
    private int executeBunnyHopMultiplier(CommandContext<CommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        SourceMovementConfig.BUNNY_HOP_MULTIPLIER.set(value);
        sendFeedback(context.getSource(), "Bunny hop multiplier set to " + value);
        return 1;
    }
    
    private int executeMaxAirSpeed(CommandContext<CommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        SourceMovementConfig.MAX_AIR_SPEED.set(value);
        sendFeedback(context.getSource(), "Max air speed set to " + value);
        return 1;
    }
    
    private int executeFriction(CommandContext<CommandSource> context) {
        double value = DoubleArgumentType.getDouble(context, "value");
        SourceMovementConfig.FRICTION.set(value);
        sendFeedback(context.getSource(), "Friction set to " + value);
        return 1;
    }

    private int executeConfig(CommandContext<CommandSource> context) {
        sendFeedback(context.getSource(), "Current configuration:");
        sendFeedback(context.getSource(), "- Air Acceleration: " + SourceMovementConfig.AIR_ACCELERATION.get());
        sendFeedback(context.getSource(), "- Air Strafe Speed: " + SourceMovementConfig.AIR_STRAFE_SPEED.get());
        sendFeedback(context.getSource(), "- Ground Speed: " + SourceMovementConfig.GROUND_SPEED.get());
        sendFeedback(context.getSource(), "- Bunny Hop Multiplier: " + SourceMovementConfig.BUNNY_HOP_MULTIPLIER.get());
        sendFeedback(context.getSource(), "- Max Air Speed: " + SourceMovementConfig.MAX_AIR_SPEED.get());
        sendFeedback(context.getSource(), "- Friction: " + SourceMovementConfig.FRICTION.get());
        return 1;
    }

    private int executeHelp(CommandContext<CommandSource> context) {
        sendFeedback(context.getSource(), "Available commands:");
        sendFeedback(context.getSource(), "/sourcemovement bhop - Enable/disable Source Movement for yourself");
        sendFeedback(context.getSource(), "/sourcemovement bhoptarget <targets> <true/false> - Enable/disable Source Movement for specific players");
        sendFeedback(context.getSource(), "/sourcemovement permissions - Display current command permissions");
        sendFeedback(context.getSource(), "/sourcemovement setPermission <command> <level> - Set the permission level for a command");
        sendFeedback(context.getSource(), "/sourcemovement airAcceleration <value> - Set air acceleration");
        sendFeedback(context.getSource(), "/sourcemovement airStrafeSpeed <value> - Set air strafe speed");
        sendFeedback(context.getSource(), "/sourcemovement groundSpeed <value> - Set ground speed");
        sendFeedback(context.getSource(), "/sourcemovement bunnyHopMultiplier <value> - Set bunny hop multiplier");
        sendFeedback(context.getSource(), "/sourcemovement maxAirSpeed <value> - Set max air speed");
        sendFeedback(context.getSource(), "/sourcemovement friction <value> - Set friction");
        sendFeedback(context.getSource(), "/sourcemovement config - Display the current configuration");
        return 1;
    }

    private void sendFeedback(CommandSource source, String message) {
        source.sendFeedback(new StringTextComponent(message), true);
    }

    private void sendErrorMessage(CommandSource source, String message) {
        source.sendErrorMessage(new StringTextComponent(message));
    }

    private void sendMessageToPlayer(ServerPlayerEntity player, String message) {
        player.sendMessage(new StringTextComponent(message), player.getUniqueID());
    }

    public boolean isPlayerPluginEnabled(UUID playerUUID) {
        return playerPluginEnabled.getOrDefault(playerUUID, false);
    }
}