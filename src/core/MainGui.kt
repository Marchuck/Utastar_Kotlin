package core

import gui.Utastar
import javafx.application.Platform
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import tornadofx.FX
import tornadofx.View
import tornadofx.button
import tornadofx.row
import uta.utaModel.DataContainer
import java.io.BufferedReader
import java.io.FileReader


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
                button("Wczytaj dane") {

                    setOnAction {

                        val fileChooser = FileChooser()
                        val file = fileChooser.showOpenDialog(primaryStage) ?: return@setOnAction

                        file.absolutePath
                                .map { FileReader(file.absolutePath) }
                                .map(::BufferedReader)
                                .map(::DataContainer)
                                .map { dataContainer -> Utastar.optimize(dataContainer) }
                                .map {
                                    scoring ->
                                    Platform.runLater {
                                        val resultsGraph = BarChart(scoring)
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