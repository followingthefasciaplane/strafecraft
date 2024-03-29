import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerMovementManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final boolean DEBUG_MODE = false; // set to true for debug messages

    public static double airAcceleration = SourceMovementConfig.AIR_ACCELERATION.get();
    public static double airStrafeSpeed = SourceMovementConfig.AIR_STRAFE_SPEED.get();
    public static double groundSpeed = SourceMovementConfig.GROUND_SPEED.get();
    public static double bunnyHopMultiplier = SourceMovementConfig.BUNNY_HOP_MULTIPLIER.get();
    public static double maxAirSpeed = SourceMovementConfig.MAX_AIR_SPEED.get();
    public static double friction = SourceMovementConfig.FRICTION.get();

    private static boolean wasOnGround = false;
    private static long lastTickTime = 0;
    private static Vector3d lastLookVec = null;

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        PlayerEntity player = event.player;

        if (!CommandHandler.isPlayerPluginEnabled(player.getUniqueID())) {
            return;
        }

        adjustRuntimeValues(player);

        Vector3d motion = player.getMotion();

        long currentTime = System.currentTimeMillis();
        float tickLength = (currentTime - lastTickTime) / 1000.0f;
        lastTickTime = currentTime;

        boolean isOnGround = player.isOnGround();
        Vector3d lookVec = player.getLookVec();
        double moveForward = player.moveForward;
        double moveStrafing = player.moveStrafing;

        if (!isOnGround) {
            Vector3d wishdir = Vector3d.ZERO;

            if (moveForward != 0 || moveStrafing != 0) {
                Vector3d forwardVec = new Vector3d(lookVec.x, 0, lookVec.z).normalize();
                Vector3d strafeVec = new Vector3d(-lookVec.z, 0, lookVec.x).normalize();

                wishdir = wishdir.add(forwardVec.scale(moveForward));
                wishdir = wishdir.add(strafeVec.scale(moveStrafing));
            }

            double wishspeed = wishdir.length();
            double currentSpeed = motion.dotProduct(wishdir);
            double addSpeed = Math.min(wishspeed - currentSpeed, airStrafeSpeed);

            AirAccelerate(motion, wishdir, addSpeed, airAcceleration, tickLength);

            if (motion.length() > maxAirSpeed) {
                motion = motion.scale(maxAirSpeed / motion.length());
            }

            player.setMotion(motion);
        } else {
            if (moveForward > 0) {
                Vector3d forwardVec = new Vector3d(lookVec.x, 0, lookVec.z).normalize();
                player.setMotion(player.getMotion().add(forwardVec.scale(moveForward * groundSpeed)));
            }

            double speed = motion.length();
            if (speed > 0.0) {
                double control = Math.max(speed, 10.0);
                double drop = control * friction * tickLength;
                motion = motion.scale(Math.max(speed - drop, 0) / speed);
                player.setMotion(motion);
            }

            if (wasOnGround && player.isJumping) {
                Vector3d direction = player.getMotion().normalize();
                motion = direction.scale(motion.length() * bunnyHopMultiplier);
                player.setMotion(motion);
            }
        }

        wasOnGround = isOnGround;
    }

    private static void adjustRuntimeValues(PlayerEntity player) {
        boolean valuesAdjusted = false;

        if (airAcceleration > SourceMovementConfig.MAX_AIR_ACCELERATION.get()) {
            airAcceleration = SourceMovementConfig.MAX_AIR_ACCELERATION.get();
            valuesAdjusted = true;
        }

        if (airStrafeSpeed > SourceMovementConfig.MAX_AIR_STRAFE_SPEED.get()) {
            airStrafeSpeed = SourceMovementConfig.MAX_AIR_STRAFE_SPEED.get();
            valuesAdjusted = true;
        }

        if (groundSpeed > SourceMovementConfig.MAX_GROUND_SPEED.get()) {
            groundSpeed = SourceMovementConfig.MAX_GROUND_SPEED.get();
            valuesAdjusted = true;
        }

        if (bunnyHopMultiplier > SourceMovementConfig.MAX_BUNNY_HOP_MULTIPLIER.get()) {
            bunnyHopMultiplier = SourceMovementConfig.MAX_BUNNY_HOP_MULTIPLIER.get();
            valuesAdjusted = true;
        }

        if (maxAirSpeed > SourceMovementConfig.MAX_MAX_AIR_SPEED.get()) {
            maxAirSpeed = SourceMovementConfig.MAX_MAX_AIR_SPEED.get();
            valuesAdjusted = true;
        }

        if (friction > SourceMovementConfig.MAX_FRICTION.get()) {
            friction = SourceMovementConfig.MAX_FRICTION.get();
            valuesAdjusted = true;
        }

        if (valuesAdjusted) {
            if (DEBUG_MODE) {
                LOGGER.info("Runtime values adjusted for player: " + player.getName().getString());
            }
            player.sendMessage(new StringTextComponent("Your movement settings have been adjusted to stay within the allowed limits."), player.getUniqueID());
        }
    }

    private static void AirAccelerate(Vector3d velocity, Vector3d wishdir, double wishspeed, double accel, float tickLength) {
        double currentSpeed = velocity.dotProduct(wishdir);
        double addSpeed = wishspeed - currentSpeed;

        if (addSpeed > 0) {
            double accelSpeed = accel * wishspeed * tickLength;
            accelSpeed = Math.min(accelSpeed, addSpeed);
            velocity = velocity.add(wishdir.scale(accelSpeed));
        }
    }
}