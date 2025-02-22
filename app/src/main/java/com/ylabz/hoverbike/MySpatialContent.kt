package com.ylabz.hoverbike

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.EdgeOffset
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.OrbiterEdge
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
import androidx.xr.scenecore.SpatialCapabilities

// --- Main Spatial Content Composable ---
@SuppressLint("RestrictedApi")
@Composable
fun MySpatialContent(onRequestHomeSpaceMode: () -> Unit) {
    val activity = LocalActivity.current
    val xrSession = LocalSession.current

    if (xrSession != null && activity is ComponentActivity) {
        // Use a SpatialPanel for immersive content.
        SpatialPanel(
            modifier = SubspaceModifier
                .size(DpVolumeSize(width = 1280.dp, height = 800.dp, depth = 7.dp))
                .resizable()
                .movable()
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    MainContent(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(48.dp)
                    )

                    // Spinning 3D model
                    if (xrSession.getSpatialCapabilities().hasCapability(SpatialCapabilities.SPATIAL_CAPABILITY_3D_CONTENT)) {
                        SpinningModel(
                            xrCoreSession = xrSession,
                            modelPath = "hover_bike/scene.gltf",
                            initialPose = Pose(
                                translation = Vector3(0f, 0f, MODEL_TRANSLATION_Z),
                                rotation = Quaternion(0f, 0f, 0f, 1f)
                            )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    FlashyBikeInfoPanel(
                        speed = 25.3,
                        distance = 12.8,
                        cadence = 90.0,
                        heartRate = 135,
                        altitude = 150.5,
                        calories = 320,
                        power = 250
                    )

                    Orbiter(
                        position = OrbiterEdge.Top,
                        offset = EdgeOffset.inner(offset = 20.dp),
                        alignment = Alignment.End,
                        shape = SpatialRoundedCornerShape(CornerSize(28.dp))
                    ) {
                        HomeSpaceModeIconButton(
                            onClick = onRequestHomeSpaceMode,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    } else {
        Log.w("XRApp", "Spatial UI not available: session=$xrSession, activity=$activity")
    }
}