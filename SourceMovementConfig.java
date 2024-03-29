import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SourceMovementConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ConfigValue<Double> AIR_ACCELERATION;
    public static final ConfigValue<Double> AIR_STRAFE_SPEED;
    public static final ConfigValue<Double> GROUND_SPEED;
    public static final ConfigValue<Double> BUNNY_HOP_MULTIPLIER;
    public static final ConfigValue<Double> MAX_AIR_SPEED;
    public static final ConfigValue<Double> FRICTION;

    public static final ConfigValue<Double> MAX_AIR_ACCELERATION;
    public static final ConfigValue<Double> MAX_AIR_STRAFE_SPEED;
    public static final ConfigValue<Double> MAX_GROUND_SPEED;
    public static final ConfigValue<Double> MAX_BUNNY_HOP_MULTIPLIER;
    public static final ConfigValue<Double> MAX_MAX_AIR_SPEED;
    public static final ConfigValue<Double> MAX_FRICTION;

    public static final ConfigValue<String> BHOP_TARGET_PERMISSION;
    public static final ConfigValue<String> BHOP_PERMISSION;
    public static final ConfigValue<String> PERMISSIONS_PERMISSION;
    public static final ConfigValue<String> SET_PERMISSION_PERMISSION;
    public static final ConfigValue<String> AIR_ACCELERATION_PERMISSION;
    public static final ConfigValue<String> AIR_STRAFE_SPEED_PERMISSION;
    public static final ConfigValue<String> GROUND_SPEED_PERMISSION;
    public static final ConfigValue<String> BUNNY_HOP_MULTIPLIER_PERMISSION;
    public static final ConfigValue<String> MAX_AIR_SPEED_PERMISSION;
    public static final ConfigValue<String> FRICTION_PERMISSION;
    public static final ConfigValue<String> CONFIG_PERMISSION;
    public static final ConfigValue<String> HELP_PERMISSION;

    private static final Logger LOGGER = LogManager.getLogger();

    static {
        Builder builder = new Builder();

        builder.comment("SourceMovement Configuration");

        builder.push("RuntimeSettings");
        AIR_ACCELERATION = builder.defineInRange("airAcceleration", 10.0, 0.0, MAX_AIR_ACCELERATION.get());
        AIR_STRAFE_SPEED = builder.defineInRange("airStrafeSpeed", 0.02, 0.0, MAX_AIR_STRAFE_SPEED.get());
        GROUND_SPEED = builder.defineInRange("groundSpeed", 0.1, 0.0, MAX_GROUND_SPEED.get());
        BUNNY_HOP_MULTIPLIER = builder.defineInRange("bunnyHopMultiplier", 1.2, 0.0, MAX_BUNNY_HOP_MULTIPLIER.get());
        MAX_AIR_SPEED = builder.defineInRange("maxAirSpeed", 0.6, 0.0, MAX_MAX_AIR_SPEED.get());
        FRICTION = builder.defineInRange("friction", 0.8, 0.0, MAX_FRICTION.get());
        builder.pop();

        builder.push("MaximumLimits");
        MAX_AIR_ACCELERATION = builder.defineInRange("maxAirAcceleration", 50.0, 0.0, Double.MAX_VALUE);
        MAX_AIR_STRAFE_SPEED = builder.defineInRange("maxAirStrafeSpeed", 1.0, 0.0, Double.MAX_VALUE);
        MAX_GROUND_SPEED = builder.defineInRange("maxGroundSpeed", 1.0, 0.0, Double.MAX_VALUE);
        MAX_BUNNY_HOP_MULTIPLIER = builder.defineInRange("maxBunnyHopMultiplier", 3.0, 0.0, Double.MAX_VALUE);
        MAX_MAX_AIR_SPEED = builder.defineInRange("maxMaxAirSpeed", 1.0, 0.0, Double.MAX_VALUE);
        MAX_FRICTION = builder.defineInRange("maxFriction", 1.0, 0.0, Double.MAX_VALUE);
        builder.pop();

        builder.push("Permissions");
        BHOP_TARGET_PERMISSION = builder.define("bhoptarget", "ADMIN");
        BHOP_PERMISSION = builder.define("bhop", "PLAYER");
        PERMISSIONS_PERMISSION = builder.define("permissions", "ADMIN");
        SET_PERMISSION_PERMISSION = builder.define("setPermission", "OWNER");
        AIR_ACCELERATION_PERMISSION = builder.define("airAcceleration", "ADMIN");
        AIR_STRAFE_SPEED_PERMISSION = builder.define("airStrafeSpeed", "ADMIN");
        GROUND_SPEED_PERMISSION = builder.define("groundSpeed", "ADMIN");
        BUNNY_HOP_MULTIPLIER_PERMISSION = builder.define("bunnyHopMultiplier", "ADMIN");
        MAX_AIR_SPEED_PERMISSION = builder.define("maxAirSpeed", "ADMIN");
        FRICTION_PERMISSION = builder.define("friction", "ADMIN");
        CONFIG_PERMISSION = builder.define("config", "PLAYER");
        HELP_PERMISSION = builder.define("help", "PLAYER");
        builder.pop();

        SPEC = builder.build();
    }

    public static void refreshConfig() {
        // update runtime settings with their current values
        airAcceleration = AIR_ACCELERATION.get();
        airStrafeSpeed = AIR_STRAFE_SPEED.get();
        groundSpeed = GROUND_SPEED.get();
        bunnyHopMultiplier = BUNNY_HOP_MULTIPLIER.get();
        maxAirSpeed = MAX_AIR_SPEED.get();
        friction = FRICTION.get();
    
        // if you have permissions or other settings, update them as well
        bhopTargetPermission = BHOP_TARGET_PERMISSION.get();
        bhopPermission = BHOP_PERMISSION.get();
        permissionsPermission = PERMISSIONS_PERMISSION.get();
        setPermissionPermission = SET_PERMISSION_PERMISSION.get();
        airAccelerationPermission = AIR_ACCELERATION_PERMISSION.get();
        airStrafeSpeedPermission = AIR_STRAFE_SPEED_PERMISSION.get();
        groundSpeedPermission = GROUND_SPEED_PERMISSION.get();
        bunnyHopMultiplierPermission = BUNNY_HOP_MULTIPLIER_PERMISSION.get();
        maxAirSpeedPermission = MAX_AIR_SPEED_PERMISSION.get();
        frictionPermission = FRICTION_PERMISSION.get();
        configPermission = CONFIG_PERMISSION.get();
        helpPermission = HELP_PERMISSION.get();
    }
    

    public static void validateConfig() {
        // validate maxAirAcceleration
        if (MAX_AIR_ACCELERATION.get() <= 0) {
            MAX_AIR_ACCELERATION.set(50.0);
            LOGGER.warn("Invalid maxAirAcceleration value. Defaulting to 50.0.");
        }

        // validate maxAirStrafeSpeed
        if (MAX_AIR_STRAFE_SPEED.get() <= 0) {
            MAX_AIR_STRAFE_SPEED.set(1.0);
            LOGGER.warn("Invalid maxAirStrafeSpeed value. Defaulting to 1.0.");
        }

        // validate maxGroundSpeed
        if (MAX_GROUND_SPEED.get() <= 0) {
            MAX_GROUND_SPEED.set(1.0);
            LOGGER.warn("Invalid maxGroundSpeed value. Defaulting to 1.0.");
        }

        // validate maxBunnyHopMultiplier
        if (MAX_BUNNY_HOP_MULTIPLIER.get() <= 0) {
            MAX_BUNNY_HOP_MULTIPLIER.set(3.0);
            LOGGER.warn("Invalid maxBunnyHopMultiplier value. Defaulting to 3.0.");
        }

        // validate maxMaxAirSpeed
        if (MAX_MAX_AIR_SPEED.get() <= 0) {
            MAX_MAX_AIR_SPEED.set(1.0);
            LOGGER.warn("Invalid maxMaxAirSpeed value. Defaulting to 1.0.");
        }

        // validate maxFriction
        if (MAX_FRICTION.get() <= 0 || MAX_FRICTION.get() > 1) {
            MAX_FRICTION.set(1.0);
            LOGGER.warn("Invalid maxFriction value. Defaulting to 1.0.");
        }

        // ensure runtime values are within the max limits
        AIR_ACCELERATION.set(Math.min(AIR_ACCELERATION.get(), MAX_AIR_ACCELERATION.get()));
        AIR_STRAFE_SPEED.set(Math.min(AIR_STRAFE_SPEED.get(), MAX_AIR_STRAFE_SPEED.get()));
        GROUND_SPEED.set(Math.min(GROUND_SPEED.get(), MAX_GROUND_SPEED.get()));
        BUNNY_HOP_MULTIPLIER.set(Math.min(BUNNY_HOP_MULTIPLIER.get(), MAX_BUNNY_HOP_MULTIPLIER.get()));
        MAX_AIR_SPEED.set(Math.min(MAX_AIR_SPEED.get(), MAX_MAX_AIR_SPEED.get()));
        FRICTION.set(Math.min(FRICTION.get(), MAX_FRICTION.get()));
    }
}