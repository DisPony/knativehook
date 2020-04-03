import events.NativeEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.jnativehook.GlobalScreen
import org.jnativehook.mouse.NativeMouseAdapter
import org.jnativehook.mouse.NativeMouseEvent
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun NativeEvents.Companion.getMousePress(): NativeMouseEvent = suspendCoroutine { cont ->
    val callback = object : NativeMouseAdapter() {
        override fun nativeMousePressed(e: NativeMouseEvent) {
            cont.resume(e)
            GlobalScreen.removeNativeMouseListener(this)
        }
    }
    GlobalScreen.addNativeMouseListener(callback)
}

suspend fun NativeEvents.Companion.getMouseClick(): NativeMouseEvent = suspendCoroutine { cont ->
    val callback = object : NativeMouseAdapter() {
        override fun nativeMouseClicked(e: NativeMouseEvent) {
            cont.resume(e)
            GlobalScreen.removeNativeMouseListener(this)
        }
    }
    GlobalScreen.addNativeMouseListener(callback)
}

suspend fun NativeEvents.Companion.getMouseRelease(): NativeMouseEvent = suspendCoroutine { cont ->
    val callback = object : NativeMouseAdapter() {
        override fun nativeMouseReleased(e: NativeMouseEvent) {
            cont.resume(e)
            GlobalScreen.removeNativeMouseListener(this)
        }
    }
    GlobalScreen.addNativeMouseListener(callback)
}

@ExperimentalCoroutinesApi
fun NativeEvents.Companion.mousePresses(): Flow<NativeMouseEvent> = callbackFlow {
    val listener = object : NativeMouseAdapter() {
        override fun nativeMousePressed(e: NativeMouseEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeMouseListener(listener)
    awaitClose { GlobalScreen.removeNativeMouseListener(listener) }
}

@ExperimentalCoroutinesApi
fun NativeEvents.Companion.mouseClicks(): Flow<NativeMouseEvent> = callbackFlow {
    val listener = object : NativeMouseAdapter() {
        override fun nativeMouseClicked(e: NativeMouseEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeMouseListener(listener)
    awaitClose { GlobalScreen.removeNativeMouseListener(listener) }
}

@ExperimentalCoroutinesApi
fun NativeEvents.Companion.mouseReleases(): Flow<NativeMouseEvent> = callbackFlow {
    val listener = object : NativeMouseAdapter() {
        override fun nativeMouseClicked(e: NativeMouseEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeMouseListener(listener)
    awaitClose { GlobalScreen.removeNativeMouseListener(listener) }
}

