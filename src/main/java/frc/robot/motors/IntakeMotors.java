package frc.robot.motors;


import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.operators.CoDriver;



public class IntakeMotors implements MotorGroup {
    // Intake Motors
    public static TalonSRX mainBack = new TalonSRX(7);
    public static TalonSRX mainFront = new TalonSRX(6);
    // Tip Motors
    public static final TalonFX tipFront = new TalonFX(10);
    public static final TalonFX tipBack = new TalonFX(9);
    // Misc.
    public static final BaseTalon[] motors = new BaseTalon[] {
            mainFront,
            mainBack,
            tipFront,
            tipBack,
    };
    public static boolean upperHoopCR = false;
    public static boolean upperHoopMR = false;
    public static boolean upperHoopLR = false;



    @Override
    public void spinMainForward(double speed) {
        mainFront.set(TalonSRXControlMode.PercentOutput, speed);
        mainBack.set(TalonSRXControlMode.PercentOutput, -speed);
    }

    @Override
    public void spinMainReverse(double speed) {
        mainFront.set(TalonSRXControlMode.PercentOutput, -speed);
        mainBack.set(TalonSRXControlMode.PercentOutput, speed);
    }

    @Override
    public void spinSecondaryForward(double speed) {
        tipFront.set(TalonFXControlMode.PercentOutput, -speed);
        tipBack.set(TalonFXControlMode.PercentOutput, -speed);
    }

    @Override
    public void spinSecondaryReverse(double speed) {
        tipFront.set(TalonFXControlMode.PercentOutput, speed);
        tipBack.set(TalonFXControlMode.PercentOutput, speed);
    }

    @Override
    public BaseTalon[] getMotors() {
        return motors;
    }

    @Override
    public void stopMain() {
        mainFront.set(TalonSRXControlMode.PercentOutput, 0);
        mainBack.set(TalonSRXControlMode.PercentOutput, 0);
    }

    @Override
    public void stopSecondary() {
        tipFront.set(TalonFXControlMode.PercentOutput, 0);
        tipBack.set(TalonFXControlMode.PercentOutput, 0);
    }

    @Override
    public void invertMotors() {
        for(BaseTalon talon : motors) {
            talon.setInverted(!talon.getInverted());
        }
    }
    public static void printTemperature(){
        SmartDashboard.putNumber("Main Front motor temp =", mainFront.getTemperature());
        SmartDashboard.putNumber("Main Back motor temp =", mainBack.getTemperature());
        SmartDashboard.putNumber("Tip Front motor temp =", tipFront.getTemperature());
        SmartDashboard.putNumber("Tip Back motor temp =", tipBack.getTemperature());
        SmartDashboard.putNumber("Arm Extend 1 motor temp =", CoDriver.armExtnd1.getTemperature());
        SmartDashboard.putNumber("Arm Extend 2 motor temp =", CoDriver.armExtnd2.getTemperature());
        SmartDashboard.putNumber("Arm Intake motor temp =", CoDriver.armSpin.getTemperature());

        SmartDashboard.putBoolean("Upper Close Range", upperHoopCR);
        SmartDashboard.putBoolean("Upper Mid Range", upperHoopMR);
        SmartDashboard.putBoolean("Upper Long Range", upperHoopLR);

        SmartDashboard.putNumber("Arm Extend 2 motor encoder", CoDriver.armExtnd2.getSelectedSensorPosition() * 0.17578152);
        SmartDashboard.putNumber("Arm Extend 1 motor encoder", CoDriver.armExtnd1.getSelectedSensorPosition() * 0.17578152);

    }
}
