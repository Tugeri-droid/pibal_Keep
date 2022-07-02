package com.example.pibal_keep

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.pibal_keep.databinding.ActivityMainBinding
import java.sql.Time
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import android.hardware.SensorEventListener

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var mSensorManager: SensorManager? = null  // menambah sistem senor manager
    private var mSensorAccelerometer: Sensor? = null
    private var mSensorMagnetometer: Sensor? = null
    private var mAccelerometerData = FloatArray(3)  // Data terkini dari akselerometer & magnetometer. Array menyimpan nilai untuk X, Y, dan Z
    private var mMagnetometerData = FloatArray(3)
    private var mTextSensorAzimuth: TextView? = null   // nilai untuk menampilkan data ke tampilan textview
    private var mTextSensorPitch: TextView? = null
    private var mdataAzm: TextView? = null
    //private var dataAzimutListValues: MutableList<dataAzm> = ArrayList<dataAzm>()
    //private var dataElevasiListValue: MutableList<dataEl> = ArrayList<dataEl>()
    private var msensorAccelerometerSet = false  //kalau di sumber sensorAccelerometerSet
    private var msensormagnetometerSet = false  ////kalau di sumber sensorMagFieldSet

    private var mDisplay: Display? = null  //System display. Need this for determining rotation.
    //------stopwatch-----//
    private var seconds = 0
    private var running = false  //is the stopwatch running?
    private var wasRunning = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )


        //menempatkan data sensor azimut dan elevasi ke view sebagai textview
            mTextSensorAzimuth = findViewById<View>(R.id.value_Azimuth) as TextView
            mTextSensorPitch = findViewById<View>(R.id.value_Pitch) as TextView
            mdataAzm = findViewById<View>(R.id.az1) as TextView

        //mengambil data akselerometer dan magnetometer dari sensor manager.
        // The getDefaultSensor() metode mengembalikan null jika sensor tidak tersedia di perangkat.
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mSensorAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorMagnetometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)


        /*----saveistancestate stopwatch----*/
        // Get the previous state of the stopwatch if the activity has been destroyed and recreated
        if (savedInstanceState != null) { // Get the previous state of the stopwatch if the activity has been destroyed and recreated
            seconds = savedInstanceState.getInt("seconds")
            running = savedInstanceState.getBoolean("running")
            wasRunning = savedInstanceState.getBoolean("wasRunning")
        }
        runTimer()

        val mulai: Button =findViewById(R.id.start_button)
        val selesai: Button =findViewById(R.id.finish_button)

            mulai.setOnClickListener{
                running= true
            }
            selesai.setOnClickListener{
                running= false
            }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // dapatkan tampilan dari the window manager (untuk rotasi).
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        mDisplay = wm.defaultDisplay

    }

    // val ketinggianStasiun = binding.ketinggianStasiun.text


    // save the state of the stopwatch if it's about to be destroyed
    public override fun onSaveInstanceState(savedInstanceState: Bundle){
        savedInstanceState.putInt("seconds", seconds)
        savedInstanceState.putBoolean("running", running)
        savedInstanceState.putBoolean("wasRunning", wasRunning)
    }

    override fun onStart() {
        super.onStart()
        // Listeners untuk sensor terdaftar dalam this callback dan dapat dibatalkan dalam onStop().
        // Check to memastikan apakah sensor tersedia sebelum di daftarkan listeners.
        // Both listeners are registered with a "normal" amount of delay  (SENSOR_DELAY_NORMAL).
        if (mSensorAccelerometer != null) {
            mSensorManager!!.registerListener(
                this, mSensorAccelerometer!!,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        if (mSensorMagnetometer != null) {
            mSensorManager!!.registerListener(
                this, mSensorMagnetometer!!,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override  fun onResume() {
        super.onResume()
        val sensorEventListener =null
        msensorAccelerometerSet = false
        msensormagnetometerSet = false
        if (mSensorAccelerometer != null) {
            mSensorManager!!.registerListener(
                this, mSensorAccelerometer!!,
                SensorManager.SENSOR_DELAY_NORMAL
            ) }
        if (mSensorMagnetometer != null) {
            mSensorManager!!.registerListener(
                this, mSensorMagnetometer!!,
                SensorManager.SENSOR_DELAY_NORMAL
            ) }
    }

    override fun onPause() {
        super.onPause()
        val sensorEventlistener = null
        mSensorManager!!.unregisterListener(this as android.hardware.SensorEventListener)  //mSensorManager!!.unregisterListener(sensorEventlistener,mSensorAccelerometer)
    }







    override fun onStop() {
        super.onStop()
        // batalkan pendaftaran semua sensor listeners dalam callback agar tidak melanjutkan
        // untuk menggunakan resources saat aplikasi app is stopped.
        mSensorManager!!.unregisterListener(this)
    }

     @OptIn(ExperimentalTime::class)
     override fun onSensorChanged(sensorEvent: SensorEvent) {
        // The sensor type (seperti yang terdefinisi dalam Sensor class).
        val sensorType = sensorEvent.sensor.type
        when (sensorType) {
            Sensor.TYPE_ACCELEROMETER -> mAccelerometerData = sensorEvent.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> mMagnetometerData = sensorEvent.values.clone()
            else -> return
        }
        // Compute the rotation matrix: merges and translates the data
        // in the device coordinate into a matrix in the world's coordinate system.
        val rotationMatrix = FloatArray(9)
        val rotationOK = SensorManager.getRotationMatrix(
            rotationMatrix,
            null, mAccelerometerData, mMagnetometerData
        )

        // Remap the matrix based on current device/activity rotation.
        var rotationMatrixAdjusted = FloatArray(9)
        when (mDisplay!!.rotation) {
            Surface.ROTATION_0 -> rotationMatrixAdjusted = rotationMatrix.clone()

            Surface.ROTATION_90 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                rotationMatrixAdjusted
            )
            Surface.ROTATION_180 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                rotationMatrixAdjusted
            )
            Surface.ROTATION_270 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                rotationMatrixAdjusted
            )
        }

        // dapatkan orientasi perangkat (azimuth, pitch, roll) berdasarkan on the rotation matrix(masih dalam radian)
        val orientationValues = FloatArray(3)
        if (rotationOK) {
            SensorManager.getOrientation(
                rotationMatrixAdjusted,
                orientationValues
            )
        }
        // tarik nilai individual dari the array. mengubah dalam satuan dejat
        val azimuth = orientationValues[0] * 57.27272727272727F
        val pitch = orientationValues[1] * 57.27272727272727F

        val azimuth360= if (azimuth<0f){ azimuth+360f }
            else { azimuth*1 }
        val pitchPositif = if (pitch<0) { pitch * -1 }
            else { pitch *1 }

        // Fill in the string placeholders and set the tampilan text.
        mTextSensorAzimuth!!.text = resources.getString(
            R.string.value_format, azimuth360
        )
        mTextSensorPitch!!.text = resources.getString(
            R.string.value_format, pitchPositif
        )

        //using the arraylist
      /*   var dataAzm : FloatArray?()
        //val waktucatat = minutes(Int)
         if(minutes:Int =1){
             dataAzm = dataAzm?.let { addAzimuth360(it,azimuth360.absoluteValue) }
             else -> dataAzm?.set(0, 1F)
         }
        dataAzm?.let { print("0") }



        val dataAzm1 = dataAzm?.get(0) //mengambil data dari array

         mdataAzm?.text ?: resources.getString(
             R.string.dataAz1,dataAzm1)

 */
         /*
          if (sensorEvent.sensor.type == orientationValues) {
              dataAzm = sensorEvent.values[0]
              dataAzimutListValues.add(
                  azimuth360(
                      dataAzm
                  )
              )
              for (i in dataAzimutListValues.indices) {
                  val output: azimut360= dataAzimutListValues[i]
                  dataAzm!!.absoluteValue = output
              }
          }
          untu tes 1{]

     */
    }

    private fun minutes(int: Int.Companion) {

    }

    private fun addAzimuth360(dataAzm: FloatArray, Azimuth360: Float): FloatArray{
        val mutableArray = dataAzm.toMutableList()
        mutableArray.add(Azimuth360)
        return mutableArray.toFloatArray()
    }

    private fun initListeners(){
        mSensorManager!!.registerListener(
            this,
            mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_FASTEST
        )
        mSensorManager!!.registerListener(
            this,
            mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_FASTEST
        )
    }

    //Harus diimplementasikan untuk memenuhi antarmuka SensorEventListener; tidak digunakan dalam aplikasi ini.
    override fun onAccuracyChanged(sensor: Sensor, i: Int) {}

    companion object {
        // nilai sangat kecil untuk accelerometer (on all three axes) seharusnya
        // ditafsirkan sebagai 0. Nilai ini adalah jumlah yang dapat diterima
        // non-zero drift.
        private const val VALUE_DRIFT = 0.05f
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun runTimer() {
        val timeView = findViewById<View>(R.id.time_view) as TextView  // Get the text view.
        val handler = Handler()// Creates a new Handler

        /* Call the post() method, passing in a new Runnable. The post() method processes code without a delay, so the code in the Runnable will run almost immediately. */
        handler.post(object : Runnable {
            override fun run() {
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60
                // Format the seconds into hours, minutes, and seconds.
                val time = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    minutes, secs
                )
                timeView.text = time   // Set the text view text.
                if (running) { seconds++ } // If running is true, increment the  seconds variable.
                handler.postDelayed(this, 1000) // Post the code again with a delay of 1 second.
            }
        })
    }



}

