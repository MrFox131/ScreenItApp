package com.example.demo1

import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.Parent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.time.LocalDateTime
import java.util.*
import javax.swing.JFileChooser
import kotlin.io.path.Path
import kotlin.io.path.createTempDirectory

class HelloApplication : Application() {
    private val props: Properties = Properties()
    private lateinit var cfgFile: File

    override fun start(stage: Stage) {
        val fxmlLoader = FXMLLoader(HelloApplication::class.java.getResource("mainScreen.fxml"))
        val layout: Parent = fxmlLoader.load()
        stage.height=600.0
        stage.width=900.0
        fxmlLoader.getController<HelloController>().stage = stage
        fxmlLoader.getController<HelloController>().bindToSizes()
        val scene = Scene(layout)
        stage.title = "ScreenItApp"
        stage.scene = scene
        stage.show()
        val exitCombination = KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN)
        stage.scene.accelerators[exitCombination] = Runnable{
            Platform.exit()
        }
        val saveCombination = KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN)
        stage.scene.accelerators[saveCombination] = Runnable {
            fxmlLoader.getController<HelloController>().saveImage()
        }
        val fastSaveCombination = KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN)
        stage.scene.accelerators[fastSaveCombination] = Runnable {
            fxmlLoader.getController<HelloController>().saveImage(LocalDateTime.now().toString()+".png")
        }
        if (! Files.exists(Path(JFileChooser().fileSystemView.defaultDirectory.toString()+"/"+".ScreenItApp"))) {
            Files.createDirectory(Path(JFileChooser().fileSystemView.defaultDirectory.toString()+"/"+".ScreenItApp"))
        }
        cfgFile = File(JFileChooser().fileSystemView.defaultDirectory.toString()+"/"+".ScreenItApp/cfg.cfg")
        if (!cfgFile.exists()) {
            cfgFile.createNewFile()
        }

        FileInputStream(cfgFile).use {
            props.load(it)
        }
        fxmlLoader.getController<HelloController>().props = props
    }

    override fun stop() {
        FileOutputStream(cfgFile).use {
            props.store(it, "")
        }
        super.stop()

    }

    fun main() {
        launch()
    }
}

fun main() {
    Application.launch(HelloApplication::class.java)
}