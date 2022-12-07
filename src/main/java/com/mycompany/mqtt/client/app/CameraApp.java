
package com.mycompany.mqtt.client.app;

/**
 *
 * @author Carlton Davis
 * Adapted code from The Pi4J Project:
 * https://pi4j.com
 * 
 */
public class CameraApp {
    //Pi4J code to control camera
    private Camera camera;
    public static int cameraNum = 0;
    public void execute() {
        System.out.println("\nInitializing the camera");
        camera = new Camera();

        System.out.println("Setting up the config to take a picture.");
        System.out.println("/home/" + System.getenv("USER") + "/Pictures/");
        //Configure the camera setup
        var config = Camera.PicConfig.Builder.newInstance()
            .outputPath("/home/" + System.getenv("USER") + "/Pictures/"+ cameraNum)
		    .delay(3000)
		    .disablePreview(true)
		    .encoding(Camera.PicEncoding.PNG)
		    .useDate(true)
		    .quality(93)
		    .width(1280)
		    .height(800)
		    .build();

        //Take the picture
        camera.takeStill(config);
        cameraNum++;

        System.out.println("Picture taken");

        System.out.println("Taking a video for 3 seconds");

        var vidconfig = Camera.VidConfig.Builder.newInstance()
            .outputPath("/home/" + System.getenv("USER") + "/Videos/")
            .disablePreview(true)
            .recordTime(3000)
            .useDate(false)
            .build();

        
        camera.takeVid(vidconfig);
        System.out.println("Video taken");
    }
    public String getOutPutPath(){
        return this.camera.getOutputPath();
    }

    public int getCameraCount(){
        return camera.getCameraCount();
    }

    
}
