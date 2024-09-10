package frc.robot.utility.swerve254util;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveModuleState;

public record SwerveSetpoint(
	ChassisSpeeds chassisSpeeds, SwerveModuleState[] moduleStates) {}
