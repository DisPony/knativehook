import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import events.NativeEvents
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import org.jnativehook.GlobalScreen
import org.jnativehook.NativeInputEvent
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
enum class Event(private val flowFactory: () -> Flow<NativeInputEvent>) {
    KEY_PRESS(NativeEvents::keyPresses), KEY_TYPE(NativeEvents::keyTypes), KEY_RELEASE(NativeEvents::keyReleases),
    MOUSE_PRESS(NativeEvents::mousePresses), MOUSE_CLICK(NativeEvents::mouseClicks), MOUSE_RELEASE(NativeEvents::mouseReleases),
    MOUSE_MOVE(NativeEvents::mouseMoves), MOUSE_DRAG(NativeEvents::mouseDrags),
    MOUSE_WHEEL(NativeEvents::mouseWheels);

    fun flow(): Flow<NativeInputEvent> = flowFactory()
}

@ExperimentalCoroutinesApi
class CliLogger : CliktCommand(), CoroutineScope {
    private val printTime by option(
        "-t", "--time",
        help = "Display unix-time of event occurrence"
    ).flag()

    private val eventTypes by argument(
        help = "Possible event types: ${Event.values()
            .joinToString(prefix = "[", postfix = "]", transform = { it.toString().toLowerCase() })}"
    ).enum<Event>().multiple(required = true)

    override fun run() {
        Logger.getLogger(GlobalScreen::class.java.getPackage().name).apply {
            level = Level.OFF
            useParentHandlers = false
        }
        GlobalScreen.registerNativeHook()
        eventTypes.forEach { eventType ->
            launch {
                eventType.flow().collect {
                    val timePrefix = if (printTime) "${System.currentTimeMillis()} " else ""
                    TermUi.echo("$timePrefix$eventType ${it.paramString()}")
                }
            }
        }

        job.invokeOnCompletion { GlobalScreen.unregisterNativeHook() }
    }

    private val job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

}

@ExperimentalCoroutinesApi
fun main(args: Array<String>): Unit = CliLogger().main(args)