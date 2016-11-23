package core

import javafx.application.Application
import tornadofx.App
import tornadofx.UIComponent
import uta.utaModel.DataContainer
import kotlin.reflect.KClass

/**
 * @author Lukasz
 * @since 22.11.2016.
 */
class UtaStarGraphApp : App {
    val scoring: DoubleArray
    val dataContainer: DataContainer

    fun main(args: Array<String>) {
        Application.launch(UtaStarGraphApp::class.java, *args)
    }

    constructor(scoring: DoubleArray, dataContainer: DataContainer) {
        this.scoring = scoring
        this.dataContainer = dataContainer
    }

    override val primaryView: KClass<out UIComponent>
        get() = DynamicGraph(scoring, dataContainer)
}