package me.maradabhargavnaidu.service;

import me.maradabhargavnaidu.ImageService.FakeImageService;
import me.maradabhargavnaidu.application.StatusListener;
import me.maradabhargavnaidu.data.AlarmStatus;
import me.maradabhargavnaidu.data.ArmingStatus;
import me.maradabhargavnaidu.data.SecurityRepository;
import me.maradabhargavnaidu.data.Sensor;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

/**
 * Service that receives information about changes to the security system. Responsible for
 * forwarding updates to the repository and making any decisions about changing the system state.
 *
 * This is the class that should contain most of the business logic for our system, and it is the
 * class you will be writing unit tests for.
 */
public class SecurityService {

    private FakeImageService imageService;
    private SecurityRepository securityRepository;
    private Set<StatusListener> statusListeners = new HashSet<>();
    private boolean isAnyCatDetected = false;

    public SecurityService(SecurityRepository securityRepository, FakeImageService imageService) {
        this.securityRepository = securityRepository;
        this.imageService = imageService;
    }

    /**
     * Sets the current arming status for the system. Changing the arming status
     * may update both the alarm status.
     * @param armingStatus
     */
    public void setArmingStatus(ArmingStatus armingStatus){
//        if(armingStatus==ArmingStatus.ARMED_HOME || armingStatus==ArmingStatus.ARMED_AWAY){
//            securityRepository.getSensors().forEach(sensor->{sensor.setActive(false);
//            securityRepository.updateSensor(sensor);
//            });
//        }
//        if(armingStatus!=ArmingStatus.DISARMED && isAnyCatDetected){
//            setAlarmStatus(AlarmStatus.ALARM);
//        }
//        if(armingStatus==ArmingStatus.DISARMED){
//            setAlarmStatus(AlarmStatus.NO_ALARM);
//        }
        securityRepository.setArmingStatus(armingStatus);
        if(armingStatus==ArmingStatus.DISARMED){
            securityRepository.setAlarmStatus(AlarmStatus.NO_ALARM);
        }else{
            securityRepository.getSensors().forEach(sensor->sensor.setActive(false));
        }
    }
    /**
     * Internal method that handles alarm status changes based on whether
     * the camera currently shows a cat.
     * @param cat True if a cat is detected, otherwise false.
     */

    private void catDetected(Boolean cat) {
        this.isAnyCatDetected = cat;
        if(cat && getArmingStatus() == ArmingStatus.ARMED_HOME) {
            setAlarmStatuss(AlarmStatus.ALARM);
        } else {
            setAlarmStatuss(AlarmStatus.NO_ALARM);
        }

        statusListeners.forEach(sl -> sl.catDetected(cat));
    }

    /**
     * Register the StatusListener for alarm system updates from within the SecurityService.
     * @param statusListener
     */
    public void addStatusListener(StatusListener statusListener) {
        statusListeners.add(statusListener);
    }

    public void removeStatusListener(StatusListener statusListener) {
        statusListeners.remove(statusListener);
    }

    /**
     * Change the alarm status of the system and notify all listeners.
     * @param status
     */
    public void setAlarmStatuss(AlarmStatus status) {
        System.out.println(status);
        securityRepository.setAlarmStatus(status);
        statusListeners.forEach(sl -> sl.notify(status));
    }
    public boolean allSensorsInactive(){
        return securityRepository.getSensors().stream().noneMatch(Sensor::getActive);
    }

    /**
     * Internal method for updating the alarm status when a sensor has been activated.
     */
    private void handleSensorActivated() {
        if(securityRepository.getArmingStatus() == ArmingStatus.DISARMED) {
            return; //no problem if the system is disarmed
        }
        switch(securityRepository.getAlarmStatus()) {
            case NO_ALARM -> setAlarmStatuss(AlarmStatus.PENDING_ALARM);
            case PENDING_ALARM -> setAlarmStatuss(AlarmStatus.ALARM);
            default->{}
        }
    }

    /**
     * Internal method for updating the alarm status when a sensor has been deactivated
     */
    private void handleSensorDeactivated() {
        switch(securityRepository.getAlarmStatus()) {
            case PENDING_ALARM -> setAlarmStatuss(AlarmStatus.NO_ALARM);
            case ALARM -> setAlarmStatuss(AlarmStatus.PENDING_ALARM);
        }
    }

    /**
     * Change the activation status for the specified sensor and update alarm status if necessary.
     * @param sensor
     * @param active
     */
    public void changeSensorActivationStatus(Sensor sensor, Boolean active) {
//        AlarmStatus currentAlarmStatus = securityRepository.getAlarmStatus();
//        ArmingStatus currentArmingStatus = securityRepository.getArmingStatus();
//        sensor.setActive(active);
//        securityRepository.updateSensor(sensor);
//        boolean anyActiveSensor = securityRepository.getSensors().stream().anyMatch(Sensor::getActive);
//        if(!sensor.getActive() && !anyActiveSensor && currentAlarmStatus==AlarmStatus.PENDING_ALARM){
//            securityRepository.setAlarmStatus(AlarmStatus.NO_ALARM);
//        }else if(sensor.getActive() && (currentArmingStatus==ArmingStatus.ARMED_HOME || currentArmingStatus ==ArmingStatus.ARMED_AWAY)){
//            if(currentAlarmStatus==AlarmStatus.NO_ALARM){
//                securityRepository.setAlarmStatus(AlarmStatus.PENDING_ALARM);
//            }else if(currentAlarmStatus==AlarmStatus.PENDING_ALARM){
//                securityRepository.setAlarmStatus(AlarmStatus.ALARM);
//            }
//        }

        AlarmStatus currentAlarmStatus = securityRepository.getAlarmStatus();
        ArmingStatus currentArmingStatus = securityRepository.getArmingStatus();
        boolean wasActive = sensor.getActive();
        if(!wasActive && active){
            if(currentArmingStatus == ArmingStatus.ARMED_HOME || currentArmingStatus == ArmingStatus.ARMED_AWAY){
                if(currentAlarmStatus==AlarmStatus.NO_ALARM){
                    securityRepository.setAlarmStatus(AlarmStatus.PENDING_ALARM);
                }else if(currentAlarmStatus==AlarmStatus.PENDING_ALARM){
                    securityRepository.setAlarmStatus(AlarmStatus.ALARM);
                }
            }
        }
        if(wasActive && !active){
            boolean anyOtherSensorActive = securityRepository.getSensors().stream()
                    .filter(s->!s.getSensorId().equals(sensor.getSensorId()))
                    .anyMatch(Sensor::getActive);
            if(!anyOtherSensorActive && currentAlarmStatus==AlarmStatus.PENDING_ALARM){
                securityRepository.setAlarmStatus(AlarmStatus.NO_ALARM);
            }
        }
        sensor.setActive(active);
        securityRepository.updateSensor(sensor);

    }

    /**
     * Send an image to the SecurityService for processing. The securityService will use its provided
     * ImageService to analyze the image for cats and update the alarm status accordingly.
     * @param currentCameraImage
     */
    public void processImage(BufferedImage currentCameraImage) {
//        boolean catDetected = imageService.imageContainsCat(currentCameraImage,50.0f);
//        if(catDetected){
//            if(securityRepository.getArmingStatus()==ArmingStatus.ARMED_HOME){
//                securityRepository.setAlarmStatus(AlarmStatus.NO_ALARM);
//            }
//        }else {
//            boolean allSensorsInactive = securityRepository.getSensors().stream().noneMatch(Sensor::getActive);
//            if (allSensorsInactive) {
//                securityRepository.setAlarmStatus(AlarmStatus.NO_ALARM);
//            }
//        }
        boolean catDetected = imageService.imageContainsCat(currentCameraImage,0.5f);
        ArmingStatus currentArmingStatus = securityRepository.getArmingStatus();
        statusListeners.forEach(l->l.catDetected(catDetected));
        if(catDetected && currentArmingStatus==ArmingStatus.ARMED_HOME){
            securityRepository.setAlarmStatus(AlarmStatus.ALARM);
        }else if(!catDetected && securityRepository.getSensors().stream().noneMatch(Sensor::getActive)){
            securityRepository.setAlarmStatus(AlarmStatus.NO_ALARM);
        }
    }


    public AlarmStatus getAlarmStatus() {
        return securityRepository.getAlarmStatus();
    }

    public Set<Sensor> getSensors() {
        return securityRepository.getSensors();
    }

    public void addSensor(Sensor sensor) {
        securityRepository.addSensor(sensor);
    }

    public void removeSensor(Sensor sensor) {
        securityRepository.removeSensor(sensor);
    }

    public ArmingStatus getArmingStatus() {
        return securityRepository.getArmingStatus();
    }
}