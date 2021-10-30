package com.example.demo1

import javafx.event.Event
import javafx.event.EventHandler
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.scene.shape.Rectangle
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates

class ScreenshotPopup {
    lateinit var stage: Stage
    lateinit var layout: Pane
    var x = 0.0
    var y = 0.0
    var x_end = 0.0
    var y_end = 0.0
    var rectangle: Rectangle = Rectangle(0.0,0.0,0.0,0.0)
    fun start(img: WritableImage): Rectangle2D{
        rectangle.fill = Color.BLACK
        rectangle.opacity=0.55
        stage = Stage()
        stage.height=Screen.getPrimary().bounds.height
        stage.width=Screen.getPrimary().bounds.width
        stage.initStyle(StageStyle.UNDECORATED)
        stage.isAlwaysOnTop = false
        stage.isFullScreen = true

        layout = Pane()
        layout.children.add(ImageView(img))
        layout.children.add(rectangle)
        layout.prefWidth = Screen.getPrimary().bounds.width
        layout.prefHeight = Screen.getPrimary().bounds.height

        val scene = Scene(layout)
        stage.scene=scene
        layout.onMousePressed = EventHandler{
            event: MouseEvent ->
            x = event.screenX
            y = event.screenY
        }
        layout.onMouseReleased = EventHandler{
            event: MouseEvent ->
            x_end = event.screenX
            y_end = event.screenY
            print("($x;$y)\n($x_end;$y_end)")
            stage.hide()
        }
        layout.onMouseDragged = EventHandler{
            event: MouseEvent ->
            if(x>event.screenX || y>event.screenY){
                rectangle.x = min(event.screenX, x)
                rectangle.y = min(event.screenY, y)
                rectangle.height = (y-event.screenY).absoluteValue
                rectangle.width = (x-event.screenX).absoluteValue
            } else {
                rectangle.x = x
                rectangle.y = y
                rectangle.height = event.screenY - y
                rectangle.width = event.screenX - x
            }
        }
        stage.showAndWait()
        return Rectangle2D(min(x, x_end), min(y, y_end), max(x, x_end)-min(x, x_end), max(y, y_end)-min(y, y_end))
    }
}