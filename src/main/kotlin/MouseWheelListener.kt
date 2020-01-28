import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.jnativehook.mouse.NativeMouseWheelEvent
import org.jnativehook.mouse.NativeMouseWheelListener

class MouseWheelExtension(capacity: Int = Channel.BUFFERED): NativeMouseWheelListener {

    val mouseWheelChannel = Channel<NativeMouseWheelEvent>(capacity)

    override fun nativeMouseWheelMoved(e: NativeMouseWheelEvent) = runBlocking {
        mouseWheelChannel.send(e)
    }

}

class MouseWheelProvider(private val listener: MouseWheelExtension){

    suspend fun getMouseWheel(): NativeMouseWheelEvent = listener.mouseWheelChannel.receive()

}