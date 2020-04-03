import events.NativeEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.jnativehook.GlobalScreen
import org.jnativehook.mouse.NativeMouseEvent
import org.jnativehook.mouse.NativeMouseMotionAdapter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun NativeEvents.Companion.getMouseMove(): NativeMouseEvent = suspendCoroutine { cont ->
    val callback = object : NativeMouseMotionAdapter() {
        override fun nativeMouseMoved(e: NativeMouseEvent) {
            cont.resume(e)
            GlobalScreen.removeNativeMouseMotionListener(this)
        }
    }
    GlobalScreen.addNativeMouseMotionListener(callback)
}

suspend fun NativeEvents.Companion.getMouseDrag(): NativeMouseEvent = suspendCoroutine { cont ->
    val callback = object : NativeMouseMotionAdapter() {
        override fun nativeMouseDragged(e: NativeMouseEvent) {
            cont.resume(e)
            GlobalScreen.removeNativeMouseMotionListener(this)
        }
    }
    GlobalScreen.addNativeMouseMotionListener(callback)
}

@ExperimentalCoroutinesApi
fun NativeEvents.Companion.mouseMoves(): Flow<NativeMouseEvent> = callbackFlow {
    val listener = object : NativeMouseMotionAdapter() {
        override fun nativeMouseMoved(e: NativeMouseEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeMouseMotionListener(listener)
    awaitClose { GlobalScreen.removeNativeMouseMotionListener(listener) }
}

@ExperimentalCoroutinesApi
fun NativeEvents.Companion.mouseDrags(): Flow<NativeMouseEvent> = callbackFlow {
    val listener = object : NativeMouseMotionAdapter() {
        override fun nativeMouseDragged(e: NativeMouseEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeMouseMotionListener(listener)
    awaitClose { GlobalScreen.removeNativeMouseMotionListener(listener) }
}