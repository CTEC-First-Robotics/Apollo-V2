package frc.robot.util;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.motors.IntakeMotors;
import frc.robot.operators.CoDriver;
import frc.robot.operators.Driver;

public class AutonomousV1 {
    public static void init() {
        IntakeMotors.upperHoopCR = true;
        // Driver.floorMotors.straightEncoderDrive(-0.5, 50);
        IntakeMotors.tipFront.set(TalonFXControlMode.PercentOutput, -0.40); // upper hub long range
        IntakeMotors.tipBack.set(TalonFXControlMode.PercentOutput, -0.08);
        Timer.delay(2.5);
        CoDriver.intakeMotors.spinMainForward(0.5);
        Timer.delay(2);
        CoDriver.intakeMotors.stopMain();
        CoDriver.intakeMotors.stopSecondary();
        Driver.floorMotors.spinMainReverse(0.5);
        Driver.floorMotors.spinSecondaryReverse(0.5);
        Timer.delay(1.25);
        Driver.floorMotors.stopMain();
        Driver.floorMotors.stopSecondary();
    }

    public static void tick() {
    }
}
