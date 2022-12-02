package frc.robot.util;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.motors.IntakeMotors;
import frc.robot.operators.CoDriver;
import frc.robot.operators.Driver;
import frc.robot.operators.Limelight;

public class AutonomousV1 {
    public static void init() {
        IntakeMotors.upperHoopCR = true;
        // Driver.floorMotors.straightEncoderDrive(-0.5, 50);
        Driver.floorMotors.spinMainReverse(0.5);
        Driver.floorMotors.spinSecondaryReverse(0.5);
        Timer.delay(.75);
        Driver.floorMotors.stopMain();
        Driver.floorMotors.stopSecondary();
        Timer.delay(1);
        double distance = (((Limelight)Robot.operators[2]).getDistance() + 70)/12;
        if (distance > 25 || distance < -1 ){
            distance = 10;
        }

        // Intake main control code
        IntakeMotors.tipFront.set(TalonFXControlMode.PercentOutput, -((0.0149*distance+.3428)*1.05));
        IntakeMotors.tipBack.set(TalonFXControlMode.PercentOutput, -((0.0027*distance+.0605)*1.05));
        Timer.delay(2.5);
        CoDriver.intakeMotors.spinMainForward(0.5);
        Timer.delay(4);
        CoDriver.intakeMotors.stopMain();
        CoDriver.intakeMotors.stopSecondary();
    }

    public static void tick() {
    }
}
