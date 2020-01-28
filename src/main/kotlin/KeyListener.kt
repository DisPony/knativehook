import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener
import java.lang.annotation.Native

class KeyExtension(capacity: Int = Channel.BUFFERED) : NativeKeyListener {

    val keyTypedChannel = Channel<NativeKeyEvent>(capacity)
    val keyPressedChannel = Channel<NativeKeyEvent>(capacity)
    val keyReleasedChannel = Channel<NativeKeyEvent>(capacity)

    override fun nativeKeyTyped(e: NativeKeyEvent) = runBlocking {
        keyTypedChannel.send(e)
    }

    override fun nativeKeyPressed(e: NativeKeyEvent) = runBlocking {
        keyPressedChannel.send(e)
    }

    override fun nativeKeyReleased(e: NativeKeyEvent) = runBlocking {
        keyReleasedChannel.send(e)
    }

}

class KeyProvider(private val listener: KeyExtension) {

    suspend fun getKeyType(): NativeKeyEvent = listener.keyTypedChannel.receive()

    suspend fun getKeyPress(): NativeKeyEvent = listener.keyPressedChannel.receive()

    suspend fun getKeyRelease(): NativeKeyEvent = listener.keyReleasedChannel.receive()

}