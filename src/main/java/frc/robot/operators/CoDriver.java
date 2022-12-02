package frc.robot.operators;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot;
import frc.robot.motors.IntakeMotors;

public class CoDriver extends Operator {
    private static final XboxController controller = new XboxController(1);
    public static final IntakeMotors intakeMotors = new IntakeMotors();
    public static final TalonSRX armSpin = new TalonSRX(8);
    public static final TalonFX armExtnd1 = new TalonFX(13);
    public static final TalonFX armExtnd2 = new TalonFX(3);
    final Timer mTimer = new Timer();
    final Timer shootTimer = new Timer();
    public static Thread thread;
    public static boolean isThreadStarted = true;
    public static boolean isEmergencyStopped = false;
    public static boolean isArmExtended = false;

    static {
        armExtnd1.setNeutralMode(NeutralMode.Brake);
        armExtnd2.setNeutralMode(NeutralMode.Brake);
        thread = new Thread(() -> {//Start of arm extend thread
            while (true) {
                if(isThreadStarted && !isEmergencyStopped) {
                    if (controller.getLeftBumperPressed() || controller.getAButtonPressed()) {
                        isArmExtended = true;
                        while (armExtnd2.getSelectedSensorPosition() * 0.17578152 < 150) {
                            armExtnd1.set(TalonFXControlMode.PercentOutput, -0.35);
                            armExtnd2.set(TalonFXControlMode.PercentOutput, 0.35);
                        }
                    } else if(armExtnd2.getSelectedSensorPosition()*0.17578152 > 145) {
                        armExtnd1.set(TalonFXControlMode.PercentOutput, 0.0);
                        armExtnd2.set(TalonFXControlMode.PercentOutput, 0.0);
                    }
                    if (!controller.getLeftBumper() && !controller.getAButton() && (armExtnd2.getSelectedSensorPosition() * 0.17578152 > 25 || Math.abs(armExtnd1.getSelectedSensorPosition()) * 0.17578152 > 25)) {
                        isArmExtended = false;
                        while (armExtnd2.getSelectedSensorPosition() * 0.17578152 > 10) {
                            armExtnd1.set(TalonFXControlMode.PercentOutput, 0.35);
                            armExtnd2.set(TalonFXControlMode.PercentOutput, -0.35);
                        }
                    } else if(armExtnd2.getSelectedSensorPosition()*0.17578152 < 0) {
                        armExtnd1.set(TalonFXControlMode.PercentOutput, 0.01);
                        armExtnd2.set(TalonFXControlMode.PercentOutput, -0.01);
                    }
                }
            }
        });
        thread.start();
    }
   //End of arm extend thread


    //Start of main thread

    void delayedShootRumble(double time){
      shootTimer.start();
      if(shootTimer.get() > time){
        controller.setRumble(RumbleType.kRightRumble, 1);
        controller.setRumble(RumbleType.kLeftRumble, 1);
      }  
    } 

    @Override
    public void tick() {

        IntakeMotors.tipBack.setNeutralMode(NeutralMode.Coast);
        IntakeMotors.tipFront.setNeutralMode(NeutralMode.Coast);
        IntakeMotors.mainFront.setNeutralMode(NeutralMode.Coast);
        IntakeMotors.mainBack.setNeutralMode(NeutralMode.Coast);

        // Tip control code
        // DO NOT REMOVE: EMERGENCY STOP ARM
        if(controller.getXButton() && controller.getYButton() && controller.getBButton() && controller.getAButton()) {
            isEmergencyStopped = true;
            armExtnd1.set(TalonFXControlMode.PercentOutput, 0);
            armExtnd2.set(TalonFXControlMode.PercentOutput, 0);
        } 
        if (controller.getRightTriggerAxis() > 0.1) {
            double voltage = RobotController.getBatteryVoltage();
            if (IntakeMotors.upperHoopCR) {
                IntakeMotors.tipFront.set(TalonFXControlMode.PercentOutput, -0.50); //-0.50 upper hub close range
                IntakeMotors.tipBack.set(TalonFXControlMode.PercentOutput, 0.10); //0.12 Tip Back -0.08 | Tip Front -0.4
                delayedShootRumble(2); //2.2 from low and close
            } else if (IntakeMotors.upperHoopMR) {
                IntakeMotors.tipFront.set(TalonFXControlMode.PercentOutput, -0.50); //-0.45 upper hub mid range
                IntakeMotors.tipBack.set(TalonFXControlMode.PercentOutput, -0.08); //-0.08
                delayedShootRumble(2); //3.5 from low and close
            } else if (IntakeMotors.upperHoopLR) {
                IntakeMotors.tipFront.set(TalonFXControlMode.PercentOutput, -0.56);
                 //-0.56 // upper hub long range
                IntakeMotors.tipBack.set(TalonFXControlMode.PercentOutput, -0.10); //-0.10
                delayedShootRumble(2); //4.5 from low and close
            } else {  
                double distance = (((Limelight)Robot.operators[2]).getDistance() + 6) / 12;
                if (distance > 300/12){
                    distance = 10;
                } else if (distance < -1){
                    distance = 10;
                }
                IntakeMotors.tipFront.set(TalonFXControlMode.PercentOutput, -(0.0149*distance+.3428)); // upper hub mid range
                IntakeMotors.tipBack.set(TalonFXControlMode.PercentOutput, -(0.0027*distance+.0605));
            }
        } else if (controller.getAButton()) {
            intakeMotors.spinSecondaryReverse(0.5);
        } else {
            shootTimer.reset();
            controller.setRumble(RumbleType.kLeftRumble, 0);
            controller.setRumble(RumbleType.kRightRumble, 0);
            if (!controller.getLeftBumper()) {
                IntakeMotors.tipFront.set(TalonFXControlMode.PercentOutput, -0.11111*1.05); // upper hub mid range
                IntakeMotors.tipBack.set(TalonFXControlMode.PercentOutput, -0.2*1.05);
            }
        }
        double distance = (((Limelight)Robot.operators[2]).getDistance() + 70) / 12;
        if (distance > 25 || distance < -1){
            distance = 10;
        }
        // Intake main control code
        SmartDashboard.putNumber("Limelight Distance", distance);
        // upperhoop and distance toggle control
        if (controller.getPOV() == 0) { // Upper hoop close range
            IntakeMotors.upperHoopCR = true;
            IntakeMotors.upperHoopMR = false;
            IntakeMotors.upperHoopLR = false;
        }
        if (controller.getPOV() == 90) { // upper hoop mid range
            IntakeMotors.upperHoopMR = true;
            IntakeMotors.upperHoopCR = false;
            IntakeMotors.upperHoopLR = false;
        }
        if (controller.getPOV() == 270) { // upper hoop long range
            IntakeMotors.upperHoopLR = true;
            IntakeMotors.upperHoopCR = false;
            IntakeMotors.upperHoopMR = false;
        }
        if (controller.getPOV() == 180) { // lower hoop close range
            IntakeMotors.upperHoopCR = false;
            IntakeMotors.upperHoopMR = false;
            IntakeMotors.upperHoopLR = false;
        }


        // intake inputs
        if (controller.getRightBumper()) {
            intakeMotors.spinMainForward(0.5);
            armSpin.set(TalonSRXControlMode.PercentOutput, 0.35);
        } else if (controller.getAButton()) {
            intakeMotors.spinMainReverse(0.5);
            armSpin.set(TalonSRXControlMode.PercentOutput, -0.35);
        } else if (controller.getLeftBumper() || controller.getLeftTriggerAxis() > 0.1) {
            IntakeMotors.mainFront.set(TalonSRXControlMode.PercentOutput, 0.5);
            IntakeMotors.mainBack.set(TalonSRXControlMode.PercentOutput, 0.5);
            armSpin.set(TalonSRXControlMode.PercentOutput, 0.35);
            IntakeMotors.tipFront.set(TalonFXControlMode.PercentOutput, -0.1111);
            IntakeMotors.tipBack.set(TalonFXControlMode.PercentOutput, -0.2);        
        } else {
            intakeMotors.stopMain();
            armSpin.set(TalonSRXControlMode.PercentOutput, 0);
        }
    }
}
