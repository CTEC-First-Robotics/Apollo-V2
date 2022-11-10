package frc.robot.util;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;


import edu.wpi.first.wpilibj.Timer;
import frc.robot.Robot;
import frc.robot.motors.IntakeMotors;
import frc.robot.operators.CoDriver;
import frc.robot.operators.Driver;
import frc.robot.operators.Limelight;

public class AutonomousV2 {
    public static void init() {
        //Turn Around
        IntakeMotors.upperHoopCR = true;
        Driver.floorMotors.spinMainForward(1);
        Driver.floorMotors.spinSecondaryReverse(1);
        //Move Foward
        Timer.delay(1);
        Driver.floorMotors.spinSecondaryForward(0.5);
        Driver.floorMotors.spinMainForward(0.5);
        CoDriver.intakeMotors.spinMainForward(0.5);
        CoDriver.armSpin.set(TalonSRXControlMode.PercentOutput, -0.5);
        //Intake and turn
        Timer.delay(1);
        CoDriver.intakeMotors.stopMain();
        CoDriver.armSpin.set(TalonSRXControlMode.PercentOutput, 0);
        Driver.floorMotors.spinMainForward(1);
        Driver.floorMotors.spinSecondaryReverse(1);
        //Advance to shoot
        Timer.delay(1);
        Driver.floorMotors.spinMainReverse(0.5);
        Driver.floorMotors.spinSecondaryReverse(0.5);
        //Stop
        Timer.delay(1);
        Driver.floorMotors.stopMain();
        Driver.floorMotors.stopSecondary();
        CoDriver.intakeMotors.spinMainReverse(0.5);
        //Rev Up Shooting Motors
        Timer.delay(1);
        CoDriver.intakeMotors.stopMain();
        double distance = (((Limelight)Robot.operators[2]).getDistance() + 6)/12;
        IntakeMotors.tipFront.set(TalonFXControlMode.PercentOutput, -(0.0149*distance+0.3428));
        IntakeMotors.tipBack.set(TalonFXControlMode.PercentOutput, -(0.0027*distance+0.0605));
        //Shoot
        Timer.delay(2.5);
        CoDriver.intakeMotors.spinMainForward(0.5);
        //Stops robot
        Timer.delay(2);
        CoDriver.intakeMotors.stopMain();
        CoDriver.intakeMotors.stopSecondary();
    } 
}
