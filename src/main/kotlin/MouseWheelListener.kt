import events.NativeEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.jnativehook.GlobalScreen
import org.jnativehook.mouse.NativeMouseWheelEvent
import org.jnativehook.mouse.NativeMouseWheelListener
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

suspend fun NativeEvents.Companion.getMouseWheel(): NativeMouseWheelEvent = suspendCoroutine { cont ->
    val callback = object : NativeMouseWheelListener {
        override fun nativeMouseWheelMoved(e: NativeMouseWheelEvent) {
            cont.resume(e)
            GlobalScreen.removeNativeMouseWheelListener(this)
        }
    }
    GlobalScreen.addNativeMouseWheelListener(callback)
}

@ExperimentalCoroutinesApi
fun NativeEvents.Companion.mouseWheels(): Flow<NativeMouseWheelEvent> = callbackFlow {
    val listener = object : NativeMouseWheelListener {
        override fun nativeMouseWheelMoved(e: NativeMouseWheelEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeMouseWheelListener(listener)
    awaitClose { GlobalScreen.removeNativeMouseWheelListener(listener) }
}