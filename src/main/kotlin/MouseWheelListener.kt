import events.NativeEvents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import org.jnativehook.GlobalScreen
import org.jnativehook.mouse.NativeMouseWheelEvent
import org.jnativehook.mouse.NativeMouseWheelListener

class MouseWheelExtension(capacity: Int = Channel.BUFFERED): NativeMouseWheelListener {

    val mouseWheelChannel = Channel<NativeMouseWheelEvent>(capacity)

    override fun nativeMouseWheelMoved(e: NativeMouseWheelEvent) = runBlocking {
        mouseWheelChannel.send(e)
    }

}

class MouseWheelProvider(private val listener: MouseWheelExtension) {

    suspend fun getMouseWheel(): NativeMouseWheelEvent = listener.mouseWheelChannel.receive()

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