package core

import tornadofx.UIComponent
import tornadofx.osgi.ViewProvider
import uta.utaModel.DataContainer

/**
 * @author Lukasz
 * @since 22.11.2016.
 */
class ResultProvider : ViewProvider {

    val scoring: DoubleArray
    val dataContainer: DataContainer

    constructor(scoring: DoubleArray, dataContainer: DataContainer) {
        this.scoring = scoring
        this.dataContainer = dataContainer
    }

    override val discriminator: Any?
        get() = "result"

    override fun getView(): UIComponent {
        return DynamicGraph(scoring, dataContainer)
    }
}