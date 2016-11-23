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
class App : tornadofx.App() {

    override val primaryView = MainGui::class

    companion object {
        val mlog = Logger.getLogger("Ulog")

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(App::class.java, *args)
        }
    }
}
