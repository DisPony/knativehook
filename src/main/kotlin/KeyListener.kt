import events.NativeEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyAdapter
import org.jnativehook.keyboard.NativeKeyEvent
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
For some reason doesn't work on my system
 */
suspend fun NativeEvents.Companion.getKeyType(): NativeKeyEvent = suspendCoroutine { cont ->
    val callback = object : NativeKeyAdapter() {
        override fun nativeKeyTyped(e: NativeKeyEvent) {
            cont.resume(e)
            GlobalScreen.removeNativeKeyListener(this)
        }
    }
    GlobalScreen.addNativeKeyListener(callback)
}

suspend fun NativeEvents.Companion.getKeyPress(): NativeKeyEvent = suspendCoroutine { cont ->
    val callback = object : NativeKeyAdapter() {
        override fun nativeKeyPressed(e: NativeKeyEvent) {
            cont.resume(e)
            GlobalScreen.removeNativeKeyListener(this)
        }
    }
    GlobalScreen.addNativeKeyListener(callback)
}

suspend fun NativeEvents.Companion.getKeyRelease(): NativeKeyEvent = suspendCoroutine { cont ->
    val callback = object : NativeKeyAdapter() {
        override fun nativeKeyReleased(e: NativeKeyEvent) {
            cont.resume(e)
            GlobalScreen.removeNativeKeyListener(this)
        }
    }
    GlobalScreen.addNativeKeyListener(callback)
}


/*
For some reason doesn't work on my system
 */
@ExperimentalCoroutinesApi
fun NativeEvents.Companion.keyTypes(): Flow<NativeKeyEvent> = callbackFlow {
    val listener = object : NativeKeyAdapter() {
        override fun nativeKeyTyped(e: NativeKeyEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeKeyListener(listener)
    awaitClose { GlobalScreen.removeNativeKeyListener(listener) }
}

@ExperimentalCoroutinesApi
fun NativeEvents.Companion.keyPresses(): Flow<NativeKeyEvent> = callbackFlow {
    val listener = object : NativeKeyAdapter() {
        override fun nativeKeyPressed(e: NativeKeyEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeKeyListener(listener)
    awaitClose { GlobalScreen.removeNativeKeyListener(listener) }
}

@ExperimentalCoroutinesApi
fun NativeEvents.Companion.keyReleases(): Flow<NativeKeyEvent> = callbackFlow {
    val listener = object : NativeKeyAdapter() {
        override fun nativeKeyReleased(e: NativeKeyEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeKeyListener(listener)
    awaitClose { GlobalScreen.removeNativeKeyListener(listener) }
}