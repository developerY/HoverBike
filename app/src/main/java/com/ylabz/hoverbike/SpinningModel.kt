package com.ylabz.hoverbike

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.GltfModelEntity
import androidx.xr.scenecore.Session
import androidx.xr.scenecore.SpatialCapabilities
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await


@Composable
fun SpinningModel(
    xrCoreSession: Session,
    modelPath: String,
    initialPose: Pose,
    onEntityCreated: (GltfModelEntity) -> Unit = {}
) {
    // State to hold our spin angle.
    var angle by remember { mutableFloatStateOf(0f) }

    // Load the model and create the entity once.
    val gltfEntity by produceState<GltfModelEntity?>(initialValue = null, key1 = xrCoreSession) {
        value = try {
            val gltfModel = xrCoreSession.createGltfResourceAsync(modelPath).await()
            if (xrCoreSession.getSpatialCapabilities().hasCapability(SpatialCapabilities.SPATIAL_CAPABILITY_3D_CONTENT)) {
                val entity = xrCoreSession.createGltfEntity(gltfModel)
                entity.setScale(MODEL_SCALE)
                entity.setPose(initialPose)
                entity.startAnimation(loop = true, animationName = "Hovering")
                onEntityCreated(entity)
                Log.d("XRApp", "Successfully created glTF entity!")
                entity
            } else {
                Log.w("XRApp", "Device does NOT support 3D content.")
                null
            }
        } catch (e: Exception) {
            Log.e("XRApp", "Failed to load or create entity: ${e.message}")
            null
        }
    }

    // If entity is loaded, spin it continuously.
    LaunchedEffect(gltfEntity) {
        gltfEntity ?: return@LaunchedEffect
        while (true) {
            angle += SPIN_INCREMENT
            val rotation = axisAngleQuaternion(angle, Vector3(0f, 1f, 0f))
            val updatedPose = Pose(
                translation = Vector3(0f, 0f, MODEL_TRANSLATION_Z),
                rotation = rotation
            )
            gltfEntity!!.setPose(updatedPose)
            Log.d("XRApp", "Angle: $angle, Pose updated: $updatedPose")
            delay(FRAME_DELAY_MS)
        }
    }
}
