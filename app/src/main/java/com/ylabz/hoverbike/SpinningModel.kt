package com.ylabz.hoverbike

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.EdgeOffset
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.OrbiterEdge
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.layout.SpatialRoundedCornerShape
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.size
import androidx.xr.compose.unit.DpVolumeSize
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
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
