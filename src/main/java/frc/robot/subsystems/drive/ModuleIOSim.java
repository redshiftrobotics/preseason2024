package frc.robot.subsystems.drive;

import static frc.robot.subsystems.drive.DriveConstants.MODULE_CONSTANTS;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.simulation.DCMotorSim;
import frc.robot.Constants;

/**
 * Physics sim implementation of module IO.
 *
 * <p>Uses two flywheel sims for the drive and turn motors, with the absolute position initialized
 * to a random value. The flywheel sims are not physically accurate, but provide a decent
 * approximation for the behavior of the module.
 */
public class ModuleIOSim implements ModuleIO {
    private DCMotorSim driveSim =
            new DCMotorSim(DCMotor.getNEO(1), MODULE_CONSTANTS.driveReduction(), 0.025);
    private DCMotorSim turnSim =
            new DCMotorSim(DCMotor.getNEO(1), MODULE_CONSTANTS.turnReduction(), 0.004);

    private final Rotation2d turnAbsoluteInitPosition =
            new Rotation2d(Math.random() * 2.0 * Math.PI);
    private double driveAppliedVolts = 0.0;
    private double turnAppliedVolts = 0.0;

    @Override
    public void updateInputs(ModuleIOInputs inputs) {
        driveSim.update(Constants.LOOP_PERIOD_SECONDS);
        turnSim.update(Constants.LOOP_PERIOD_SECONDS);

        // --- Drive ---
        inputs.drivePositionRad = driveSim.getAngularPositionRad();
        inputs.driveVelocityRadPerSec = driveSim.getAngularVelocityRadPerSec();
        inputs.driveAppliedVolts = driveAppliedVolts;
        inputs.driveCurrentAmps = new double[] {Math.abs(driveSim.getCurrentDrawAmps())};

        // --- Turn ---
        inputs.turnAbsolutePosition =
                new Rotation2d(turnSim.getAngularPositionRad()).plus(turnAbsoluteInitPosition);
        inputs.turnPosition = new Rotation2d(turnSim.getAngularPositionRad());
        inputs.turnVelocityRadPerSec = turnSim.getAngularVelocityRadPerSec();
        inputs.turnAppliedVolts = turnAppliedVolts;
        inputs.turnCurrentAmps = new double[] {Math.abs(turnSim.getCurrentDrawAmps())};

        // --- Odometry ---
        inputs.odometryTimestamps = new double[] {Timer.getFPGATimestamp()};
        inputs.odometryDrivePositionsRad = new double[] {inputs.drivePositionRad};
        inputs.odometryTurnPositions = new Rotation2d[] {inputs.turnPosition};
    }

    @Override
    public void setDriveVoltage(double volts) {
        driveAppliedVolts = MathUtil.clamp(volts, -12.0, +12.0);
        driveSim.setInputVoltage(driveAppliedVolts);
    }

    @Override
    public void setTurnVoltage(double volts) {
        turnAppliedVolts = MathUtil.clamp(volts, -12.0, +12.0);
        turnSim.setInputVoltage(turnAppliedVolts);
    }
}
