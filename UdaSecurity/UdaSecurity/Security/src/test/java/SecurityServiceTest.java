


import me.maradabhargavnaidu.data.*;
import me.maradabhargavnaidu.ImageService.FakeImageService;
import me.maradabhargavnaidu.service.SecurityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    private Sensor testSensor;
    private SecurityService securityService;

    @Mock
    private SecurityRepository securityRepo;

    @Mock
    private FakeImageService imageService;

    private Sensor createSensor() {
        return new Sensor(UUID.randomUUID().toString(), SensorType.WINDOW);
    }

    private Set<Sensor> generateSensors(boolean status, int quantity) {
        Set<Sensor> sensorSet = new HashSet<>();
        for (int i = 0; i < quantity; i++) {
            Sensor sensor = new Sensor(UUID.randomUUID().toString(), SensorType.MOTION);
            sensor.setActive(status);
            sensorSet.add(sensor);
        }
        return sensorSet;
    }

    @BeforeEach
    void setup() {
        testSensor = createSensor();
        securityService = new SecurityService(securityRepo, imageService);
    }

    @ParameterizedTest
    @EnumSource(value= ArmingStatus.class,names={"ARMED_HOME","ARMED_AWAY"})
    void whenAlarmIsArmedAndSensorActivated_thenSetPendingAlarm(ArmingStatus status){
        when(securityRepo.getArmingStatus()).thenReturn(status);
        when(securityRepo.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(testSensor,true);
        verify(securityRepo).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    @Test
    void whenAllSensorsInactiveAndAlarmPending_thenResetToNoAlarm(){
        Set<Sensor> sensors = generateSensors(false,4);
        Sensor activeSensor = sensors.iterator().next();
        activeSensor.setActive(true);
        when(securityRepo.getSensors()).thenReturn(sensors);
        when(securityRepo.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(activeSensor,false);
        verify(securityRepo).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void whenAlarmActive_thenSensorChangesHaveNoEffect(){
        when(securityRepo.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        testSensor.setActive(false);
        securityService.changeSensorActivationStatus(testSensor,true);
        verify(securityRepo,never()).setAlarmStatus(any(AlarmStatus.class));
        testSensor.setActive(true);
        securityService.changeSensorActivationStatus(testSensor,false);
        verify(securityRepo,never()).setAlarmStatus(any(AlarmStatus.class));
    }


    @Test
    void whenImageContainsCatAndAlarmIsArmedHome_thenTriggerAlarm(){
        when(securityRepo.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(any(),anyFloat())).thenReturn(true);
        securityService.processImage(mock(BufferedImage.class));
        verify(securityRepo).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void whenNoCatDetectedAndAllSensorsInactive_thenResetAlarmToNoAlarm(){
        Set<Sensor> sensors = generateSensors(false,4);
        when(securityRepo.getSensors()).thenReturn(sensors);
        when(imageService.imageContainsCat(any(),anyFloat())).thenReturn(false);
        securityService.processImage(mock(BufferedImage.class));
        verify(securityRepo).setAlarmStatus(AlarmStatus.NO_ALARM);
    }


    @Test
    void whenSystemDisarmed_thenResetAlarmStatus(){
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        verify(securityRepo).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class,names = {"ARMED_HOME","ARMED_AWAY"})
    void whenSystemArmed_thenDeactivateAllSensors(ArmingStatus status){
//        securityService.setArmingStatus(ArmingStatus.DISARMED);
//        Set<Sensor> sensors = generateSensors(true,4);
//        when(securityRepo.getSensors()).thenReturn(sensors);
//        when(securityRepo.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
//        securityService.setArmingStatus(status);
//        sensors.forEach(sensor->assertFalse(sensor.getActive()));

        Set<Sensor> sensors = generateSensors(true,4);
        when(securityRepo.getSensors()).thenReturn(sensors);
        securityService.setArmingStatus(status);
        for(Sensor sensor:sensors){
            assertFalse(sensor.getActive());
        }
        verify(securityRepo).setArmingStatus(status);
    }
    @Test
    void whenDeactivatingSensorWhilteOthersAreActive_thenAlarmStatusNotReset(){
        Sensor activeSensor = createSensor();
        activeSensor.setActive(true);

        testSensor.setActive(true);

        when(securityRepo.getSensors()).thenReturn(Set.of(testSensor,activeSensor));
        when(securityRepo.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(testSensor,false);
        verify(securityRepo,never()).setAlarmStatus(AlarmStatus.NO_ALARM);
    }
    @Test
    void whenSensorActivatedDuringPendingAlarm_thenAlarmIsTriggered(){
        when(securityRepo.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        when(securityRepo.getArmingStatus()).thenReturn(ArmingStatus.ARMED_AWAY);
        testSensor.setActive(false);
        securityService.changeSensorActivationStatus(testSensor,true);
        verify(securityRepo).setAlarmStatus(AlarmStatus.ALARM);
    }
    @Test
    void whenSensorActivatedWhileSystemDisarmed_thenNoAlarmStatusChanged(){
        when(securityRepo.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);
        when(securityRepo.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        testSensor.setActive(false);
        securityService.changeSensorActivationStatus(testSensor,true);
        verify(securityRepo,never()).setAlarmStatus(any());
    }
}
