package frc.robot.motors;

import com.ctre.phoenix.motorcontrol.can.BaseTalon;

public interface MotorGroup {
    public void spinMainForward(double speed);
    public void spinMainReverse(double speed);
    public void stopMain();
    public void spinSecondaryForward(double speed);
    public void spinSecondaryReverse(double speed);
    public void stopSecondary();
    public void invertMotors();
    public BaseTalon[] getMotors();
}
