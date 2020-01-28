import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.runBlocking
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