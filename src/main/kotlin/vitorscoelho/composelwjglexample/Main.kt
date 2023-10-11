package vitorscoelho.composelwjglexample

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import javax.swing.BoxLayout
import javax.swing.JPanel
import kotlin.math.max
import kotlin.math.min

private const val INITIAL_ROTATION_SPEED = 0f
private const val SCROLL_DELTA_SPEED = 0.1f
private const val MIN_ROTATION_SPEED = 0f
private const val MAX_ROTATION_SPEED = 10f

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose + LWJGL",
        state = rememberWindowState(width = 800.dp, height = 600.dp)
    ) {
        var rotationSpeed by remember { mutableStateOf(INITIAL_ROTATION_SPEED) }

        MaterialTheme {
            Column(
                modifier = Modifier.fillMaxSize().padding(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Column {
                    Text(text = "Move slider or mouse scroll on canvas to speed control", fontSize = 25.sp)
                    Divider(thickness = 1.dp, color = Color.DarkGray)
                    Slider(
                        value = rotationSpeed,
                        onValueChange = { rotationSpeed = it },
                        valueRange = (MIN_ROTATION_SPEED..MAX_ROTATION_SPEED),
                    )
                }
                LWJGLSwingPanel(
                    rotationSpeed = rotationSpeed,
                    onScroll = {
                        val delta: Float = when {
                            it < 0 -> SCROLL_DELTA_SPEED
                            it > 0 -> -SCROLL_DELTA_SPEED
                            else -> 0f
                        }
                        rotationSpeed = max(MIN_ROTATION_SPEED, min(MAX_ROTATION_SPEED, rotationSpeed + delta))
                    },
                )
            }
        }
    }
}

@Composable
fun LWJGLSwingPanel(
    rotationSpeed: Float,
    onScroll: (wheelRotation: Int) -> Unit,
) {
    val lwjglCanvas by remember {
        mutableStateOf(
            LWJGLCanvas(
                renderer = Renderer(),
                rotationSpeed = rotationSpeed,
            )
        )
    }
    if (lwjglCanvas.mouseWheelListeners.isEmpty()) {
        lwjglCanvas.addMouseWheelListener { onScroll(it.wheelRotation) }
    } else {
        lwjglCanvas.mouseWheelListeners[0] = { onScroll(it.wheelRotation) }
    }
    lwjglCanvas.rotationSpeed = rotationSpeed
    SwingPanel(
        modifier = Modifier.fillMaxSize(),
        factory = {
            JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                add(lwjglCanvas)
            }
        }
    )
    LaunchedEffect(Unit) {
        println("START")
        lwjglCanvas.startLoop()
    }
    DisposableEffect(Unit) {
        object : DisposableEffectResult {
            override fun dispose() {
                lwjglCanvas.dispose()
                println("FINISH")
            }
        }
    }
}