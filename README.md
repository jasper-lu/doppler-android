# Motion sensing using the doppler effect, for android

This is my implementation of Gupta's [Soundwave paper](http://research.microsoft.com/en-us/um/redmond/groups/cue/publications/guptasoundwavechi2012.pdf) for android. The project is heavily inspired by Daniel Rapp's [javascript plugin](https://github.com/DanielRapp/doppler) for the same effect.

##How to Use
I haven't figured out how to work this project into gradle/maven, so the only way to use it at the moment is to download the library as a zip and add it to your Android Studio project as a module.

To use the library in your code, just initialize a new Doppler instance, and start it:

    Doppler doppler = new Doppler();
    doppler.start();
    //also, make sure to pause doppler when the user exits your app:
    doppler.pause();

You can then start listening for gestures by attach a GestureListener: 

    doppler.setOnGestureCallback(new Doppler.OnGestureListener() {
      public void onPush() {
        //scroll down
      }
      public void onPull() {
        //scroll up
      }
      public void onTap() {
        //click button
      }
      public void onDoubleTap() {
        //pause scrolling?
      }
    });

If you would rather manipulate the raw data, you can attach a ReadCallback as well:

    doppler.setOnReadCallback(new Doppler.OnReadCallback() {
      public void onBandwidthRead(int leftBandwidth, int rightBandwidth) {
        //play around with the bandwidth differences
      }
      public void onBinsRead(double[] bins) {
        //graph the frequency bin magnitudes, for example
      }
    });

The library is also set to automatically calibrate by default. I find that the best way to use this library is to let Doppler autocalibrate for several seconds before starting whatever task you intend to use it for. Thanks to (dracolytch)[https://github.com/DanielRapp/doppler/pull/9] for his autocalibration algorithm. 

Should you wish to turn off autocalibration, just call the method:

    doppler.setCalibrate(false);

##Disclaimer
I was only able to test the library on my OnePlus One so far, so the library may still be a little buggy. Also, I get some jagged lines near the edges of the 20k frequency reading, which may either be caused by bad code or my device.

Some times, this causes Doppler to bug out and "lock" to one direction, so if this happens, just reinitialize the Doppler instance and it should work again.
