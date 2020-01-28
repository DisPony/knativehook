import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.jnativehook.mouse.NativeMouseEvent
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