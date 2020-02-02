package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import frc.robot.userinterface.*;
import frc.robot.subsystems.*;
import frc.robot.commands.*;
import frc.robot.commands.autonomous.*;
import io.github.pseudoresonance.pixy2api.*;

/**
 * The main Robot class whence all things come.
 */
public class Robot extends TimedRobot {

    private UsbCamera camera;

    private AutonomousSwitch autonomous;
    private SendableChooser<String> positionChooser;

    public Robot() {
        super(0.06);
    }

    public void robotInit() {
        RobotMap.setBot("practice");
        System.out.println("Initializing " + RobotMap.botName + "\n");

        camera = CameraServer.getInstance().startAutomaticCapture();

        Subsystems.driveBase.cheesyDrive.setSafetyEnabled(false);
        RobotMap.setSpeedAndRotationCaps(0.3, 0.5);

        //Setup Shuffleboard interface
        positionChooser = new SendableChooser<String>();
        positionChooser.setDefaultOption("Center", "C");
        positionChooser.addOption("Left", "L");
        positionChooser.addOption("Right", "R");

        Shuffleboard.getTab("Match").add("Position", positionChooser);
    }

    public void disabledInit() {
        System.out.println("Disabled Initialized");
        Scheduler.getInstance().removeAll();
    }

    public void disabledPeriodic() {
        printDataToSmartDashboard();
        Scheduler.getInstance().run();
    }

    public void autonomousInit() {
        System.out.println("Autonomous Initalized");
        Scheduler.getInstance().removeAll();

        autonomous = new AutonomousSwitch(positionChooser.getSelected());
        autonomous.start();
    }

    public void autonomousPeriodic() {
        Scheduler.getInstance().run();
        printDataToSmartDashboard();
    }

    public void teleopInit() {
        System.out.println("TeleOp Initalized");
        Scheduler.getInstance().removeAll();
    }

    public void teleopPeriodic() {
        Scheduler.getInstance().run();
        printDataToSmartDashboard();

        //Run flywheel when operator Y pressed down (change on operator request)
        if (UserInterface.driverController.Y.get()) {
            Subsystems.flyboi.spinWheel(0.5);
        } else {
            Subsystems.flyboi.stopWheel();
        }
    }

    /**
     * Puts data into the Smart Dashboard. This will be updated even if the robot is disabled.
     */
    private void printDataToSmartDashboard() {
        try {
            Pixy2CCC.Block block = Subsystems.pixy.getBiggestBlock();
            SmartDashboard.putNumber("blockX", block.getX());
        } catch (java.lang.NullPointerException e) {
            return;
        }
    }
}