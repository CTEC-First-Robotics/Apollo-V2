package frc.robot.operators;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Robot;
import frc.robot.motors.FloorMotors;

public class Driver extends Operator {
    private static final XboxController controller = new XboxController(0);
    public static final FloorMotors floorMotors = new FloorMotors();
    public static boolean inverted = false;
    private boolean driverHalted = false;

    public static double getLeftYIvrt() { // inverts controler y axis so that Up is positive and down is negative
        return controller.getLeftY() * (-1);
    }

    public static double getRightYIvrt() { // inverts controler y axis so that Up is positive and down is negative
        return controller.getRightY() * (-1);
    }

    @Override
    public void tick() {
        // Test
        Limelight limelight = (Limelight) Robot.operators[2];
        System.out.println(limelight.getDistance());

        // DO NOT REMOVE: EMERGENCY STOP ARM
        if (controller.getXButton() && controller.getYButton() && controller.getBButton()
                && controller.getAButton()) {
            CoDriver.isEmergencyStopped = true;
            CoDriver.armExtnd1.set(TalonFXControlMode.PercentOutput, 0);
            CoDriver.armExtnd2.set(TalonFXControlMode.PercentOutput, 0);
        }

        if (controller.getPOV() == 180 && Math.abs(controller.getRightX()) < 0.2 && Math.abs(getLeftYIvrt()) < 0.2 && inverted == false ) {
            floorMotors.invertMotors();
            inverted = true;
        }
        if (controller.getPOV() == 0 && Math.abs(controller.getRightX()) < 0.2 && Math.abs(getLeftYIvrt()) < 0.2 && inverted == true ) {
            floorMotors.invertMotors();
            inverted = false;
        }
         
        //turbo
        if (controller.getRightTriggerAxis() > 0.1) {
            FloorMotors.speedMod = 0.9;
        } else if (controller.getLeftTriggerAxis() > 0.1) {
            FloorMotors.speedMod = 0.5;
        } else {
            FloorMotors.speedMod = 0.25;
        }


        double pivetTurnReduction = 0.35;

        //START ARM PIVOT SPEED LIMITING CODE
        double armPivotLimiter;
        if (CoDriver.isArmExtended == true) {
            armPivotLimiter = 0.5;    //Reduce turn while driving speed since arm is out
        } else {
            armPivotLimiter = 0.5;   //No change to turning speed
        }

        // START PIVOT CODE
        if (Math.abs(getLeftYIvrt()) < 0.2 && controller.getRightX() > 0.2 && !driverHalted) {
            floorMotors.spinMainForward(-pivetTurnReduction * controller.getRightX()); // Reduces rev spin on Rt
            floorMotors.spinSecondaryForward(pivetTurnReduction * controller.getRightX());
        } else if (Math.abs(getLeftYIvrt()) < 0.2 && controller.getRightX() < -0.2 && !driverHalted) {
            floorMotors.spinMainForward(-pivetTurnReduction * controller.getRightX());
            floorMotors.spinSecondaryForward(pivetTurnReduction * controller.getRightX()); // Reduces rev spin on Lt
            // END PIVOT CODE

            // START DRIVE CODE
        } else if (Math.abs(getLeftYIvrt()) >= 0.2 && !driverHalted) {
            if (getLeftYIvrt() > 0.2 && Math.abs(controller.getRightX()) < 0.2) { // positive y without x input
                floorMotors.spinMainForward(Math.pow(10, getLeftYIvrt() - 1));
                floorMotors.spinSecondaryForward(Math.pow(10, getLeftYIvrt() - 1));

            } else if (getLeftYIvrt() < -0.2 && Math.abs(controller.getRightX()) < 0.2) { // negative y without input
                floorMotors.spinMainForward(-Math.pow(10, -getLeftYIvrt() - 1));
                floorMotors.spinSecondaryForward(-Math.pow(10, -getLeftYIvrt() - 1));

            } else if (getLeftYIvrt() > 0.2 && Math.abs(controller.getRightX()) > 0.2) { // postive y with x input
                floorMotors.spinMainForward(Math.pow(10, getLeftYIvrt() - 1) - (armPivotLimiter * controller.getRightX()));
                floorMotors.spinSecondaryForward(Math.pow(10, getLeftYIvrt() - 1) + (armPivotLimiter * controller.getRightX()));

            } else if (getLeftYIvrt() < -0.2 && Math.abs(controller.getRightX()) > 0.2) { // negative y with x input
                floorMotors.spinMainForward(-Math.pow(10, -getLeftYIvrt() - 1) - (armPivotLimiter * controller.getRightX()));
                floorMotors.spinSecondaryForward(-Math.pow(10, -getLeftYIvrt() - 1) + (armPivotLimiter * controller.getRightX()));
            }
            // END DRIVE CODE
        } else {
            floorMotors.stopMain();
            floorMotors.stopSecondary();
        }
        //START LIME ALLIGN CODE
        if(controller.getXButtonPressed()) {
            new Thread(new Runnable() { 
                public void run() {
                    driverHalted = true;
                    int failCount = 0;
                    while(limelight.x > 2 || limelight.x == 0.0 && !(limelight.x < -2 || limelight.x == 0.0)) {
                        floorMotors.spinMainForward(-pivetTurnReduction * 0.5);
                        floorMotors.spinSecondaryForward(pivetTurnReduction * 0.5);
                        if(limelight.x == 0.0) {
                            failCount++;
                        }
                        if(failCount > 9) {
                            break;
                        }
                    }
                    while((limelight.x < -2 || limelight.x == 0.0) && !(limelight.x > 2 || limelight.x == 0.0)) {
                        floorMotors.spinMainForward(pivetTurnReduction * 0.5);
                        floorMotors.spinSecondaryForward(-pivetTurnReduction * 0.5);
                        if(limelight.x == 0.0) {
                            failCount++;
                        }
                        if(failCount > 9) {
                            break;
                        }
                    }
                    driverHalted = false;
                };
            }).start();
        }
        //END LIME ALLIGN CODE
    }
}
        // END NEW CODE WITH SPIN REDUCTION AND ACCELERATION

        // START NEW WITH SPIN REDUCTION AND ACCELERATION V2 (DO NOT USE)
        /*
         * if (getLeftYIvrt() < 0.4 && controller.getRightX() > 0.4) {
         * floorMotors.spinMainForward(-0.5 * controller.getRightX()); //Reduces rev
         * spin on Rt
         * floorMotors.spinSecondaryForward(controller.getRightX());
         * } else if (getLeftYIvrt() < 0.4 && controller.getRightX() < -0.4) {
         * floorMotors.spinMainForward(-controller.getRightX());
         * floorMotors.spinSecondaryForward(0.5 * controller.getRightX()); //Reduces rev
         * spin on Lt
         * } else if (Math.abs(getLeftYIvrt()) > 0.4 && (controller.getRightX() < 0.4 ||
         * controller.getRightX() > -0.4)) {
         * new Timer().schedule(new TimerTask() {
         * 
         * @Override
         * public void run() {
         * // The code here will be run after xxxx/1000 seconds.
         * }
         * }, 2000);
         * floorMotors.spinMainForward(getLeftYIvrt() - controller.getRightX());
         * floorMotors.spinSecondaryForward(getLeftYIvrt() + controller.getRightX()); }
         * } else {
         * floorMotors.stopMain();
         * floorMotors.stopSecondary();
         * }
         * 
         */ // END NEW CODE WITH SPIN REDUCTION AND ACCELERATION V2 (DO NOT USE)
    
// START ORIGINAL CODE
        /*
         * if(getLeftYIvrt() > 0.2 || getLeftYIvrt() < -0.2 || controller.getRightX() <
         * 0.3 || controller.getRightX() > -0.3) {
         * floorMotors.spinMainForward(getLeftYIvrt() - controller.getRightX());
         * floorMotors.spinSecondaryForward(getLeftYIvrt() + controller.getRightX());
         * } else {
         * floorMotors.stopMain();
         * floorMotors.stopSecondary();
         * }
         * // END ORIGINAL CODE
         * 
         * /* // START NEW WITH SPIN REDUCTION
         * 
         * if (getLeftYIvrt() < 0.4 && controller.getRightX() > 0.4) {
         * floorMotors.spinMainForward(-0.5 * controller.getRightX()); //Reduces rev
         * spin on Rt
         * floorMotors.spinSecondaryForward(controller.getRightX());
         * } else if (getLeftYIvrt() < 0.4 && controller.getRightX() < -0.4) {
         * floorMotors.spinMainForward(-controller.getRightX());
         * floorMotors.spinSecondaryForward(0.5 * controller.getRightX()); //Reduces rev
         * spin on Lt
         * } else if (Math.abs(controller.getLeftY()) > 0.4 && (controller.getRightX() <
         * 0.4 || controller.getRightX() > -0.4)) {
         * floorMotors.spinMainForward(getLeftYIvrt() - controller.getRightX());
         * floorMotors.spinSecondaryForward(getLeftYIvrt() + controller.getRightX());
         * } else {
         * floorMotors.stopMain();
         * floorMotors.stopSecondary();
         * }
         */// EXPIRENTAL FOWARD TURN SPEED REDUCTION
        /*
         * 
         * if (Math.abs(getLeftYIvrt()) < 0.2 && controller.getRightX() > 0.4) {
         * floorMotors.spinMainForward(-pivetTurnReduction * controller.getRightX());
         * //Reduces rev spin on Rt
         * floorMotors.spinSecondaryForward(controller.getRightX());
         * } else if (Math.abs(getLeftYIvrt()) < 0.2 && controller.getRightX() < -0.4) {
         * floorMotors.spinMainForward(-controller.getRightX());
         * floorMotors.spinSecondaryForward(pivetTurnReduction *
         * controller.getRightX()); //Reduces rev spin on Lt
         * } else if (Math.abs(getLeftYIvrt()) > 0.2 && (controller.getRightX() < 0.4 ||
         * controller.getRightX() > -0.4)) {
         * if (getLeftYIvrt() > 0.2 && controller.getRightX() < -0.4) { //Foward left
         * floorMotors.spinMainForward(Math.pow(10, getLeftYIvrt() - 1) -
         * controller.getRightX());
         * floorMotors.spinSecondaryForward(Math.pow(10, getLeftYIvrt() - 1) +
         * (movementTurnReduction * controller.getRightX()));
         * } else if (getLeftYIvrt() > 0.2 && controller.getRightX() > 0.4) { //Foward
         * Right
         * floorMotors.spinMainForward(-Math.pow(10, -getLeftYIvrt() - 1) -
         * (movementTurnReduction * controller.getRightX()));
         * floorMotors.spinSecondaryForward(-Math.pow(10, -getLeftYIvrt() - 1) +
         * controller.getRightX());
         * } else if (getLeftYIvrt() < -0.2 && controller.getRightX() < -0.4) { //Back
         * Left
         * floorMotors.spinMainForward(-Math.pow(10, -getLeftYIvrt() - 1) -
         * controller.getRightX());
         * floorMotors.spinSecondaryForward(-Math.pow(10, -getLeftYIvrt() - 1) +
         * (movementTurnReduction * controller.getRightX()));
         * } else if (getLeftYIvrt() < -0.2 && controller.getRightX() > 0.4) { //Back
         * Right
         * floorMotors.spinMainForward(-Math.pow(10, -getLeftYIvrt() - 1) -
         * controller.getRightX());
         * floorMotors.spinSecondaryForward(-Math.pow(10, -getLeftYIvrt() - 1) +
         * (movementTurnReduction * controller.getRightX()));
         * }
         * } else {
         * floorMotors.stopMain();
         * floorMotors.stopSecondary();
         * }
         */