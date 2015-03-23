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

If you would rather manipulate the raw data, you can attach a ReadCallback instead:

    doppler.setOnReadCallback(new Doppler.OnReadCallback() {
      public void onBandwidthRead(int leftBandwidth, int rightBandwidth) {
        //play around with the bandwidth differences
      }
      public void onBinsRead(double[] bins) {
        //graph the frequency bin magnitudes, for example
      }
    });
