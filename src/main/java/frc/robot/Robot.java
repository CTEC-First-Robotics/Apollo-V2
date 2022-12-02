// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.motors.FloorMotors;
import frc.robot.motors.IntakeMotors;
import frc.robot.operators.CoDriver;
import frc.robot.operators.Driver;
import frc.robot.operators.GlobalOperator;
import frc.robot.operators.Limelight;
import frc.robot.operators.Operator;
import frc.robot.util.AutonomousV1;
import frc.robot.util.AutonomousV2;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the manifest
 * file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
  public static final Operator[] operators = new Operator[] {
    new CoDriver(),
    new Driver(),
    new Limelight()
  };
  public static final Timer timer = new Timer();
  @Override
  public boolean isDisabled() {
      if(CoDriver.isThreadStarted) {
        CoDriver.isThreadStarted = false;
      }
      return super.isDisabled();
  }

  @Override
  public boolean isEnabled() {
    CoDriver.armExtnd2.setSelectedSensorPosition(0);
    CoDriver.armExtnd1.setSelectedSensorPosition(0);
    if(!CoDriver.isThreadStarted) {
      CoDriver.isThreadStarted = true;
    }
    return super.isEnabled();
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    CoDriver.armExtnd2.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
    CoDriver.armExtnd1.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
    FloorMotors.rightFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
    FloorMotors.leftFrontMotor.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
    CameraServer.startAutomaticCapture(0);
    //CameraServer.startAutomaticCapture(1);
    for(BaseTalon talon: FloorMotors.motors) {
      talon.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor, 0, 0);
    }

    for(Operator op : Robot.operators) {
      if(op instanceof GlobalOperator) {
          ((GlobalOperator)op).init();
      } 
    }
  }

  /** This function is run once each time the robot enters autonomous mode. */
  @Override
  public void autonomousInit() {
    // AutonomousV1.init(timer);
    AutonomousV1.init();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    // AutonomousV1.tick();
    double distance = (((Limelight)Robot.operators[2]).getDistance() + 6)/12;
        /*
        if (distance > 300/12){
            distance = 10;
        } else if (distance < -1){
            distance = 10;
        }*/
        // Intake main control code
    SmartDashboard.putNumber("Limelight Distance", distance);
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
    //  Temp.mTimer.reset();
    //  Temp.mTimer.start();
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    double distance = (((Limelight)Robot.operators[2]).getDistance() + 6)/12;
    System.out.println(-(0.0149*distance+0.3428));
    System.out.println(-(0.0027*distance+0.0605));
    FloorMotors.printTemperature();
    IntakeMotors.printTemperature();
    for(Operator op : Robot.operators) {
      if(op instanceof GlobalOperator) {
        ((GlobalOperator)op).onGlobalTick();
      } else {
        op.tick();
      }
    }
  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    Driver.floorMotors.spinMainForward(0.2);
  }
}
