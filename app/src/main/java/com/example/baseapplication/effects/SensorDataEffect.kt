package com.example.projandroid1.effects

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.atan2


/**
 * Compoable Effect to update the current location state.
 * Based on the location effect example.
 */
@Composable
fun SensorDataEffect(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onUpdate: (data: SensorData) -> Unit,
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val dataManager = SensorDataManager(context)
        dataManager.init()

        val job = scope.launch {
            Log.d("SensorDataEffect#DisposableEffect" ,"job thingy before")
            dataManager.data
                .receiveAsFlow()
                //.onEach { onUpdate(it) }
                .collect({onUpdate(it) })
            Log.d("SensorDataEffect#DisposableEffect" ,"job thingy before")
        }

        onDispose {
            dataManager.cancel()
            job.cancel()
        }
    }
}

class SensorDataManager (context: Context): SensorEventListener {

    private val sensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    fun init() {
        Log.d("SensorDataManager", "init")
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    val data: Channel<SensorData> = Channel(Channel.UNLIMITED)

    override fun onSensorChanged(event: SensorEvent?) {
        //Log.d("SensorDataManager", "onSensorChanged")
        if (event?.sensor?.type == Sensor.TYPE_GRAVITY)
            gravity = event.values

        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values


        if (gravity != null && geomagnetic != null) {
            val r = FloatArray(9)
            val i = FloatArray(9)

            //SensorManager.getInclination(i);
            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                //var orientation = FloatArray(3)
                //SensorManager.getOrientation(r, orientation)


                data.trySend(
                    SensorData(
                        // https://stackoverflow.com/questions/69626915/get-angle-between-phones-z-axis-and-the-magnetic-north-pole-instead-of-y-axis
                        // https://stackoverflow.com/questions/53408642/azimuth-reading-changes-to-opposite-when-user-holds-the-phone-upright
                        azimuth =  -atan2(r[3].toDouble(), r[0].toDouble()),
                        pitch = Math.asin(-r[7].toDouble()).toFloat(),
                        roll = 0.0f//orientation[2],
                    )
                )
            }
        }
    }

    fun cancel() {
        Log.d("SensorDataManager", "cancel")
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

data class SensorData(
    val azimuth: Double,
    val pitch: Float =0f,
    val roll: Float=0f,

    )