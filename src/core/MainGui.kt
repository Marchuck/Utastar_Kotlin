package core

import gui.Utastar
import javafx.application.Platform
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import tornadofx.*
import uta.utaModel.DataContainer
import java.io.BufferedReader
import java.io.FileReader
import java.util.logging.Level


/**
 * Utastar_Kotlin
 *
 * @author Lukasz Marczak
 *
 * @since 21 lis 2016.
 * 17 : 48
 */

class MainGui : View("Uta Star") {

    override val root = GridPane()

    init {
        Platform.setImplicitExit(false)

        with(root) {

            row {
                button("load data") {
                    isDefaultButton = true

                    setOnAction {

                        val fileChooser = FileChooser();
                        val file = fileChooser.showOpenDialog(primaryStage)

                        file.absolutePath
                                .map { FileReader(file.absolutePath) }
                                .map(::BufferedReader)
                                .map(::DataContainer)
                                .map { dataContainer -> Utastar.optimize(dataContainer) }
                                .map {
                                    scoring ->
                                    Platform.runLater {
                                        val resultsGraph = DynamicGraph(scoring)
                                        if (FX.primaryStage.scene.root != resultsGraph.root) {
                                            FX.primaryStage.scene.root = resultsGraph.root
                                            FX.primaryStage.sizeToScene()
                                            FX.primaryStage.centerOnScreen()
                                        }
                                    }
                                }
                    }
                }
            }
        }
    }
}