package frc.robot.motors;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.Timer;

public class FloorMotors implements MotorGroup {
    public static double speedMod = 0.7;
    // Front Motors
    public static final TalonFX leftFrontMotor = new TalonFX(4);
    public static final TalonFX rightFrontMotor = new TalonFX(15);
    // Back Motors
    public static final TalonFX leftBackMotor = new TalonFX(2);
    public static final TalonFX rightBackMotor = new TalonFX(0);
    // Misc
    public static final BaseTalon[] motors = new BaseTalon[] {
            leftFrontMotor,
            rightFrontMotor,
            leftBackMotor,
            rightBackMotor
    };

    @Override
    public void spinMainForward(double speed) { // right side
        rightFrontMotor.set(TalonFXControlMode.PercentOutput, speed * speedMod);
        rightBackMotor.set(TalonFXControlMode.PercentOutput, speed * speedMod);
    }

    @Override
    public void spinMainReverse(double speed) {
        rightFrontMotor.set(TalonFXControlMode.PercentOutput, -speed * speedMod);
        rightBackMotor.set(TalonFXControlMode.PercentOutput, -speed * speedMod);
    }

    @Override
    public void spinSecondaryForward(double speed) { // left side
        leftFrontMotor.set(TalonFXControlMode.PercentOutput, speed * speedMod);
        leftBackMotor.set(TalonFXControlMode.PercentOutput, speed * speedMod);
    }

    @Override
    public void spinSecondaryReverse(double speed) {
        leftFrontMotor.set(TalonFXControlMode.PercentOutput, -speed * speedMod);
        leftBackMotor.set(TalonFXControlMode.PercentOutput, -speed * speedMod);
    }

    @Override
    public BaseTalon[] getMotors() {
        return motors;
    }

    @Override
    public void stopMain() {// right side
        rightFrontMotor.set(TalonFXControlMode.PercentOutput, 0);
        rightBackMotor.set(TalonFXControlMode.PercentOutput, 0);
    }

    @Override
    public void stopSecondary() { // left side
        leftFrontMotor.set(TalonFXControlMode.PercentOutput, 0);
        leftBackMotor.set(TalonFXControlMode.PercentOutput, 0);
    }

    public void straightTimedDrive(double startpower, double time) { // simple P.I.D
        Timer timer = new Timer();
        leftFrontMotor.set(TalonFXControlMode.PercentOutput, startpower);
        rightFrontMotor.set(TalonFXControlMode.PercentOutput, startpower);
        double rightAdjustedPower = startpower;
        double leftAdjustedPower = startpower;
        while (true) {
            if (timer.get() < time) {
                leftFrontMotor.set(TalonFXControlMode.PercentOutput, leftAdjustedPower);
                rightFrontMotor.set(TalonFXControlMode.PercentOutput, rightAdjustedPower);
                if (rightFrontMotor.getSelectedSensorPosition() > leftFrontMotor.getSelectedSensorPosition() + 100) { // Right
                                                                                                                      // motor
                                                                                                                      // adjustment
                    rightAdjustedPower = rightAdjustedPower - 0.001;
                    leftAdjustedPower = leftAdjustedPower + 0.001;
                }
                if (leftFrontMotor.getSelectedSensorPosition() > rightFrontMotor.getSelectedSensorPosition() + 100) { // Left
                                                                                                                      // motor
                                                                                                                      // adjustment
                    leftAdjustedPower = leftAdjustedPower - 0.001;
                    rightAdjustedPower = rightAdjustedPower + 0.001;
                }
            } else {
                break;
            }
        }
    }
            // 11:60, 20:28
    public void straightEncoderDrive(double startpower, double inches) { // simple P.I.D
        leftFrontMotor.set(TalonFXControlMode.PercentOutput, startpower);
        rightFrontMotor.set(TalonFXControlMode.PercentOutput, startpower);
        rightBackMotor.set(TalonFXControlMode.PercentOutput, startpower);
        leftBackMotor.set(TalonFXControlMode.PercentOutput, startpower);
        FloorMotors.rightFrontMotor.setSelectedSensorPosition(0);
        FloorMotors.leftFrontMotor.setSelectedSensorPosition(0);
        double rightAdjustedPower = startpower;
        double leftAdjustedPower = startpower;
        double ticksPassedRight = 0;
        double ticksPassedLeft = 0;
        double oneTick = ((Math.PI * 6)*(11/60)*(20/28)) / 2048;
        while (true) {
            ticksPassedLeft = FloorMotors.leftFrontMotor.getSelectedSensorPosition();
            ticksPassedRight = FloorMotors.rightFrontMotor.getSelectedSensorPosition();
            if(ticksPassedLeft < inches * oneTick && ticksPassedRight < inches * oneTick) {
                leftFrontMotor.set(TalonFXControlMode.PercentOutput, leftAdjustedPower);
                rightFrontMotor.set(TalonFXControlMode.PercentOutput, rightAdjustedPower);
                rightBackMotor.set(TalonFXControlMode.PercentOutput, rightAdjustedPower);
                leftBackMotor.set(TalonFXControlMode.PercentOutput, leftAdjustedPower);
                if (rightFrontMotor.getSelectedSensorPosition() > leftFrontMotor.getSelectedSensorPosition() + 100) { // Right
                    // motor
                    // adjustment
                    rightAdjustedPower = rightAdjustedPower - 0.001;
                    leftAdjustedPower = leftAdjustedPower + 0.001;
                }
                if (leftFrontMotor.getSelectedSensorPosition() > rightFrontMotor.getSelectedSensorPosition() + 100) { // Left
                    // motor
                    // adjustment
                    leftAdjustedPower = leftAdjustedPower - 0.001;
                    rightAdjustedPower = rightAdjustedPower + 0.001;
                }
            } else {
                leftFrontMotor.set(TalonFXControlMode.PercentOutput, 0);
                rightFrontMotor.set(TalonFXControlMode.PercentOutput, 0);
                rightBackMotor.set(TalonFXControlMode.PercentOutput, 0);
                leftBackMotor.set(TalonFXControlMode.PercentOutput, 0);
                break;
            }
        }
    }

    @Override
    public void invertMotors() {
        leftFrontMotor.setInverted(!leftFrontMotor.getInverted());
        leftBackMotor.setInverted(!leftBackMotor.getInverted());
        rightFrontMotor.setInverted(!rightFrontMotor.getInverted());
        rightBackMotor.setInverted(!rightBackMotor.getInverted());
        System.out.print(String.format("\033[2J"));
    }

    public static void printTemperature() {
        SmartDashboard.putNumber("Right Front motor temp: ", rightFrontMotor.getTemperature());
        SmartDashboard.putNumber("Right Back motor temp: ", rightBackMotor.getTemperature());
        SmartDashboard.putNumber("Left Front motor temp: ", leftFrontMotor.getTemperature());
        SmartDashboard.putNumber("Left Back motor temp: ", leftBackMotor.getTemperature());
        SmartDashboard.putNumber("Front Right Encoder Value: ", rightFrontMotor.getSelectedSensorPosition());
        SmartDashboard.putNumber("Front Left Encoder Value: ", leftFrontMotor.getSelectedSensorPosition());
    }

    static {
        leftBackMotor.setInverted(true);
        leftFrontMotor.setInverted(true);
    }
}
