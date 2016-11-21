package core

import javafx.application.Application
import tornadofx.importStylesheet

/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 17 : 47
 */
class App : tornadofx.App() {
    override val primaryView = DynamicGraph::class
}

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}