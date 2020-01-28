import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import org.jnativehook.GlobalScreen
import org.jnativehook.mouse.NativeMouseAdapter
import org.jnativehook.mouse.NativeMouseEvent
import org.jnativehook.mouse.NativeMouseListener

class MouseExtension(capacity: Int = Channel.BUFFERED) : NativeMouseListener {

    val mousePressedChannel = Channel<NativeMouseEvent>(capacity)
    val mouseClickedChannel = Channel<NativeMouseEvent>(capacity)
    val mouseReleasedChannel = Channel<NativeMouseEvent>(capacity)

    override fun nativeMousePressed(e: NativeMouseEvent) = runBlocking {
        mousePressedChannel.send(e)
    }

    override fun nativeMouseClicked(e: NativeMouseEvent) = runBlocking {
        mouseClickedChannel.send(e)
    }

    override fun nativeMouseReleased(e: NativeMouseEvent) = runBlocking {
        mouseReleasedChannel.send(e)
    }

}

class MouseProvider(private val listener: MouseExtension) {

    suspend fun getMousePress() = listener.mousePressedChannel.receive()

    suspend fun getMouseClick() = listener.mouseClickedChannel.receive()

    suspend fun getMouseRelease() = listener.mouseReleasedChannel.receive()

}

@ExperimentalCoroutinesApi
fun mousePressesFlow(): Flow<NativeMouseEvent> = callbackFlow {
    val listener = object : NativeMouseAdapter() {
        override fun nativeMousePressed(e: NativeMouseEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeMouseListener(listener)
    awaitClose { GlobalScreen.removeNativeMouseListener(listener) }
}

@ExperimentalCoroutinesApi
fun mouseClicksFlow(): Flow<NativeMouseEvent> = callbackFlow {
    val listener = object : NativeMouseAdapter() {
        override fun nativeMouseClicked(e: NativeMouseEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeMouseListener(listener)
    awaitClose { GlobalScreen.removeNativeMouseListener(listener) }
}

@ExperimentalCoroutinesApi
fun mouseReleasesFlow(): Flow<NativeMouseEvent> = callbackFlow {
    val listener = object : NativeMouseAdapter() {
        override fun nativeMouseClicked(e: NativeMouseEvent) {
            offer(e)
        }
    }
    GlobalScreen.addNativeMouseListener(listener)
    awaitClose { GlobalScreen.removeNativeMouseListener(listener) }
}

