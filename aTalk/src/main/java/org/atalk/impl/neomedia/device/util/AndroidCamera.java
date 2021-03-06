/*
 * Jitsi, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.atalk.impl.neomedia.device.util;

import android.hardware.Camera;

import net.java.sip.communicator.util.Logger;

import org.atalk.impl.neomedia.NeomediaActivator;
import org.atalk.impl.neomedia.device.DeviceConfiguration;
import org.atalk.service.neomedia.MediaUseCase;

import java.util.List;

import javax.media.*;

/**
 * Class used to represent camera device in Android device systems.
 *
 * @author Pawel Domas
 */
@SuppressWarnings("deprecation")
public class AndroidCamera extends CaptureDeviceInfo
{
    /**
     * The logger
     */
    private final static Logger logger = Logger.getLogger(AndroidCamera.class);

    /**
     * The constant value for camera facing front.
     */
    public static final int FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    /**
     * The constant value for camera facing back.
     */
    public static final int FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;

    /**
     * Creates new instance of <tt>AndroidCamera</tt>
     *
     * @param name human readable name of the camera e.g. front camera.
     * @param locator the <tt>MediaLocator</tt> identifying the camera and it's system.
     * @param formats list of supported formats.
     */
    public AndroidCamera(String name, MediaLocator locator, Format[] formats)
    {
        super(name, locator, formats);
    }

    /**
     * Creates <tt>MediaLocator</tt> for given parameters.
     *
     * @param locatorProtocol locator protocol that identifies device system.
     * @param cameraId ID of camera that identifies the {@link Camera}.
     * @param cameraInfo <tt>CameraInfo</tt> corresponding to given <tt>cameraId</tt>.
     * @return camera <tt>MediaLocator</tt> for given parameters.
     */
    public static MediaLocator constructLocator(String locatorProtocol, int cameraId, Camera.CameraInfo cameraInfo)
    {
        return new MediaLocator(locatorProtocol + ":" + cameraId + "/" + cameraInfo.facing);
    }

    /**
     * Returns the protocol part of <tt>MediaLocator</tt> that identifies camera device system.
     *
     * @return the protocol part of <tt>MediaLocator</tt> that identifies camera device system.
     */
    public String getCameraProtocol()
    {
        return locator.getProtocol();
    }

    /**
     * Returns camera facing direction.
     *
     * @return camera facing direction.
     */
    public int getCameraFacing()
    {
        return getCameraFacing(locator);
    }

    /**
     * Extracts camera id from given <tt>locator</tt>.
     *
     * @param locator the <tt>MediaLocator</tt> that identifies the camera.
     * @return extracted camera id from given <tt>locator</tt>.
     */
    public static int getCameraId(MediaLocator locator)
    {
        String remainder = locator.getRemainder();
        return Integer.parseInt(remainder.substring(0, remainder.indexOf("/")));
    }

    /**
     * Extracts camera facing from given <tt>locator</tt>.
     *
     * @param locator the <tt>MediaLocator</tt> that identifies the camera.
     * @return extracted camera facing from given <tt>locator</tt>.
     */
    public static int getCameraFacing(MediaLocator locator)
    {
        String remainder = locator.getRemainder();
        return Integer.parseInt(remainder.substring(remainder.indexOf("/") + 1, remainder.length()));
    }

    /**
     * Returns array of cameras available in the system.
     *
     * @return array of cameras available in the system.
     */
    public static AndroidCamera[] getCameras()
    {
        DeviceConfiguration devConfig = NeomediaActivator.getMediaServiceImpl().getDeviceConfiguration();
        List<CaptureDeviceInfo> videoDevices = devConfig.getAvailableVideoCaptureDevices(MediaUseCase.CALL);

        AndroidCamera[] cameras = new AndroidCamera[videoDevices.size()];
        for (int i = 0; i < videoDevices.size(); i++) {
            CaptureDeviceInfo device = videoDevices.get(i);
            // All cameras have to be of type AndroidCamera on Android
            cameras[i] = (AndroidCamera) device;
        }
        return cameras;
    }

    /**
     * Finds camera with given <tt>facing</tt> from the same device system as currently selected camera.
     *
     * @param facing the facing direction of camera to find.
     * @return camera with given <tt>facing</tt> from the same device system as currently selected camera.
     */
    public static AndroidCamera getCameraFromCurrentDeviceSystem(int facing)
    {
        AndroidCamera currentCamera = getSelectedCameraDevInfo();
        String currentProtocol = currentCamera != null ? currentCamera.getCameraProtocol() : null;

        AndroidCamera[] cameras = getCameras();
        for (AndroidCamera camera : cameras) {
            // Match facing
            if (camera.getCameraFacing() == facing) {
                // Now match the protocol if any
                if (currentProtocol != null) {
                    if (currentProtocol.equals(camera.getCameraProtocol())) {
                        return camera;
                    }
                }
                else {
                    return camera;
                }
            }
        }
        return null;
    }

    /**
     * Returns device info of currently selected camera.
     *
     * @return device info of currently selected camera.
     */
    public static AndroidCamera getSelectedCameraDevInfo()
    {
        DeviceConfiguration devConfig = NeomediaActivator.getMediaServiceImpl().getDeviceConfiguration();
        return (AndroidCamera) devConfig.getVideoCaptureDevice(MediaUseCase.CALL);
    }

    /**
     * Selects the camera identified by given locator to be used by the system.
     *
     * @param cameraLocator camera device locator that will be used.
     */
    public static AndroidCamera setSelectedCamera(MediaLocator cameraLocator)
    {
        DeviceConfiguration devConfig = NeomediaActivator.getMediaServiceImpl().getDeviceConfiguration();
        List<CaptureDeviceInfo> videoDevices = devConfig.getAvailableVideoCaptureDevices(MediaUseCase.CALL);

        AndroidCamera selectedCamera = null;
        for (CaptureDeviceInfo deviceInfo : videoDevices) {
            if (deviceInfo.getLocator().equals(cameraLocator)) {
                selectedCamera = (AndroidCamera) deviceInfo;
                break;
            }
        }
        if (selectedCamera != null) {
            devConfig.setVideoCaptureDevice(selectedCamera, true);
            return selectedCamera;
        }
        else {
            logger.warn("No camera found for name: " + cameraLocator);
            return null;
        }
    }
}