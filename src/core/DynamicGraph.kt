package core

import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.layout.GridPane
import tornadofx.*
import uta.utaModel.DataContainer
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction


/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 17 : 48
 */

class DynamicGraph(override val constructors: Collection<KFunction<UIComponent>>,
                   override val members: Collection<KCallable<*>>,
                   override val nestedClasses: Collection<KClass<*>>,
                   override val objectInstance: UIComponent?,
                   override val qualifiedName: String?,
                   override val simpleName: String?,
                   override val annotations: List<Annotation>) : View(""), KClass<UIComponent> {
    override fun equals(other: Any?): Boolean {
        return true
    }

    override fun hashCode(): Int {
        return 0
    }

    var scoring: DoubleArray = doubleArrayOf(0.0)
    var dataContainer: DataContainer? = null

    constructor(scoring: DoubleArray, dataContainer: DataContainer) : this(
            emptyList(), emptyList(), emptyList(), null, "hi", "", emptyList()) {

        this.scoring = scoring
        this.dataContainer = dataContainer
    }

    override val root = GridPane()

    init {
        with(root) {
            row {
                barchart("Utastar", CategoryAxis(), NumberAxis()) {
                    series("Utastar alternatives scoring") {
                        for (j in 0..scoring.size)
                            data("Property ", scoring[j])
                    }
                }

            }
        }
    }
}