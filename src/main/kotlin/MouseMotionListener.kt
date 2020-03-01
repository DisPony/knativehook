import events.NativeEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import org.jnativehook.GlobalScreen
import org.jnativehook.mouse.NativeMouseEvent
import org.jnativehook.mouse.NativeMouseMotionAdapter
import org.jnativehook.mouse.NativeMouseMotionListener

class MouseMotionExtension(capacity: Int = Channel.BUFFERED): NativeMouseMotionListener {

    val mouseMovedChannel = Channel<NativeMouseEvent>(capacity)
    val mouseDraggedChannel = Channel<NativeMouseEvent>(capacity)

    override fun nativeMouseMoved(e: NativeMouseEvent) = runBlocking {
        mouseMovedChannel.send(e)
    }

    override fun nativeMouseDragged(e: NativeMouseEvent) = runBlocking {
        mouseDraggedChannel.send(e)
    }

}

class MouseMotionProvider(private val listener: MouseMotionExtension) {

    suspend fun getMouseMove(): NativeMouseEvent = listener.mouseMovedChannel.receive()

    suspend fun getMouseDrag(): NativeMouseEvent = listener.mouseDraggedChannel.receive()

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