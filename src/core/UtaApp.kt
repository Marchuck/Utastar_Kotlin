package core

import javafx.application.Application
import java.util.logging.Logger

/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 17 : 47
 */
class UtaApp : tornadofx.App() {

    companion object {
        var mlog = Logger.getLogger("Ulog")
    }

    override val primaryView = DynamicGraph::class


    fun main(args: Array<String>) {
        Application.launch(UtaApp::class.java, *args)
    }
}
