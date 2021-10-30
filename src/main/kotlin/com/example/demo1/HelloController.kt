package com.example.demo1

import javafx.application.Platform
import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Rectangle2D
import javafx.scene.SnapshotParameters
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.control.CheckBox
import javafx.scene.control.ColorPicker
import javafx.scene.control.ScrollPane
import javafx.scene.control.Slider
import javafx.scene.image.ImageView
import javafx.scene.image.PixelFormat
import javafx.scene.image.WritableImage
import javafx.scene.image.WritablePixelFormat
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.robot.Robot
import javafx.scene.shape.ArcType
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.stage.FileChooser
import javafx.stage.Screen
import javafx.stage.Stage
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import kotlin.concurrent.schedule
import kotlin.io.path.Path


class HelloController {
    var stage: Stage? = null
    private var delay: Int = 0
    private var minimizeOnScreenshot: Boolean = false
    private var robot: Robot = Robot()
    lateinit var props: Properties

    @FXML
    private lateinit var controlsAnchorPane: AnchorPane

    @FXML
    private lateinit var controlsHBox: HBox

    @FXML
    private lateinit var main: GridPane

    @FXML
    private lateinit var minimizeCheckbox: CheckBox

    @FXML
    private lateinit var delaySlider: Slider

    @FXML
    private lateinit var imageContainer: StackPane

    @FXML
    private lateinit var scroll: ScrollPane

    @FXML
    private lateinit var imageView: ImageView

    @FXML
    private lateinit var canvas: Canvas

    @FXML
    private lateinit var colorPicker: ColorPicker

    @FXML
    private lateinit var widthSlider: Slider

    @FXML
    private lateinit var clearCheckbox: CheckBox

    private lateinit var gc: GraphicsContext

    private var clickDrawHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.fillArc(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value,
            0.0,
            360.0,
            ArcType.OPEN
        )
    }

    private var pressDrawHandler = EventHandler { event: MouseEvent ->
        canvas.graphicsContext2D.fillArc(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value,
            0.0,
            360.0,
            ArcType.OPEN
        )

        gc.moveTo(event.x, event.y)
        gc.beginPath()
    }

    private var dragDrawHandler = EventHandler { event: MouseEvent ->
        gc.stroke = colorPicker.value
        gc.lineWidth = widthSlider.value
        gc.lineTo(event.x, event.y)
        gc.stroke()
    }

    private var releaseDrawHandler = EventHandler { event: MouseEvent ->
        gc.stroke = colorPicker.value
        gc.lineWidth = widthSlider.value
        gc.lineTo(event.x, event.y)
        gc.stroke()
        gc.closePath()
    }

    private var clickEraseHandler = EventHandler { event: MouseEvent ->
        gc.clearRect(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value
        )
    }

    private var pressEraseHandler = EventHandler { event: MouseEvent ->

        gc.clearRect(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value
        )
    }

    private var dragEraseHandler = EventHandler { event: MouseEvent ->
        gc.clearRect(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value
        )
    }

    private var releaseEraseHandler = EventHandler { event: MouseEvent ->
        gc.clearRect(
            event.x - widthSlider.value / 2,
            event.y - widthSlider.value / 2,
            widthSlider.value,
            widthSlider.value
        )
    }


    fun initialize() {
        gc = canvas.graphicsContext2D
//        val boxblur = BoxBlur()
//        boxblur.height = 2.0
//        boxblur.width = 2.0
//        boxblur.iterations = 1
//        gc.setEffect(boxblur)
        gc.fill = colorPicker.value
        gc.lineJoin = StrokeLineJoin.ROUND
        gc.lineCap = StrokeLineCap.ROUND
        delaySlider.valueProperty().addListener { _, _, new ->
            delay = new.toInt()
        }

        canvas.onMouseClicked = clickDrawHandler
        canvas.onMousePressed = pressDrawHandler
        canvas.onMouseDragged = dragDrawHandler
        canvas.onMouseReleased = releaseDrawHandler
    }

    fun bindToSizes() {
        main.prefHeightProperty().bind(stage?.heightProperty())
        main.prefWidthProperty().bind(stage?.widthProperty())
        controlsAnchorPane.prefWidthProperty().bind(main.prefWidthProperty())
        controlsHBox.prefWidthProperty().bind(controlsAnchorPane.prefWidthProperty())
        imageContainer.minHeightProperty().bind(scroll.heightProperty())
        imageContainer.minWidthProperty().bind(scroll.widthProperty())
        imageView.isPreserveRatio = true

    }

    @FXML
    private fun minimizeCheckboxTouched() {
        minimizeOnScreenshot = minimizeCheckbox.isSelected
    }

    @FXML
    private fun screenshotButtonPressed() {

        if (minimizeOnScreenshot) {
            stage?.isIconified = true
        }
        Timer().schedule(300 + delay.toLong() * 1000) {
            Platform.runLater {
                val screen = Screen.getPrimary().bounds
                val imgOut: WritableImage =
                    robot.getScreenCapture(null, Rectangle2D(0.0, 0.0, screen.width, screen.height))

                val rect: Rectangle2D = ScreenshotPopup().start(imgOut)
                val newImg = WritableImage(rect.width.toInt(), rect.height.toInt())
                val buffer = ByteArray(rect.width.toInt() * rect.height.toInt() * 4)
                val format = WritablePixelFormat.getByteBgraInstance()
                imgOut.pixelReader.getPixels(
                    rect.minX.toInt(), rect.minY.toInt(), rect.width.toInt(),
                    rect.height.toInt(), format, buffer, 0, rect.width.toInt() * 4
                )
                newImg.pixelWriter.setPixels(
                    0, 0, rect.width.toInt(), rect.height.toInt(),
                    format, buffer, 0, rect.width.toInt() * 4
                )
                imageView.image = newImg
                imageView.fitWidth = newImg.width
                imageView.fitHeight = newImg.height
                canvas.widthProperty().bind(imageView.fitWidthProperty())
                canvas.heightProperty().bind(imageView.fitHeightProperty())
                imageContainer.prefHeightProperty().bind(imageView.fitHeightProperty())
                imageContainer.prefWidthProperty().bind(imageView.fitWidthProperty())
                gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
                stage?.isIconified = false
            }
        }
    }

    @FXML
    fun colorPicked() {
        canvas.graphicsContext2D.fill = colorPicker.value
    }

    @FXML
    fun clearCheckboxTouched() {
        if (clearCheckbox.isSelected) {
            canvas.onMouseClicked = clickEraseHandler
            canvas.onMousePressed = pressEraseHandler
            canvas.onMouseDragged = dragEraseHandler
            canvas.onMouseReleased = releaseEraseHandler
        } else {
            canvas.onMouseClicked = clickDrawHandler
            canvas.onMousePressed = pressDrawHandler
            canvas.onMouseDragged = dragDrawHandler
            canvas.onMouseReleased = releaseDrawHandler
        }
    }

    @FXML
    fun saveImage() {
        saveImage(null)
    }

    @FXML
    fun saveImage(name: String? = null) {
        val img = (imageView.image as WritableImage)
        val imageWriter = img.pixelWriter
        val canvasImage = WritableImage(canvas.width.toInt(), canvas.height.toInt())
        val params = SnapshotParameters()
        params.fill = Color.TRANSPARENT
        canvas.snapshot(params, canvasImage)
        val canvasReader = canvasImage.pixelReader
        val buffer = IntArray((canvasImage.height * canvasImage.width).toInt())
        canvasReader.getPixels(
            0,
            0,
            canvas.width.toInt(),
            canvas.height.toInt(),
            PixelFormat.getIntArgbInstance(),
            buffer,
            0,
            canvas.width.toInt()
        )
        for (pixelNumber in buffer.indices) {
            val row = (pixelNumber / canvas.width).toInt()
            val column = pixelNumber - (row * canvas.width).toInt()
            if ((buffer[pixelNumber] shr 24 and 0xff) > 0) {
                imageWriter.setArgb(column, row, buffer[pixelNumber])
            }
        }
        val file: File? = if (name == null) {
            val fileChooser = FileChooser()

            fileChooser.initialDirectory = if (props.keys.contains("saveFolder")){
                File(props.getValue("saveFolder") as String)
            } else {
                File(JFileChooser().fileSystemView.defaultDirectory.toString())
            }
            val extFilter = FileChooser.ExtensionFilter("PNG files (*.png)", "*.png")
            fileChooser.extensionFilters.add(extFilter)

            var file = fileChooser.showSaveDialog(stage)
            if (!FilenameUtils.getExtension(file.name).equals("png")){
                file = File(file.parentFile, FilenameUtils.getBaseName(file.name)+".png")
            }
            file
        } else {
            File(JFileChooser().fileSystemView.defaultDirectory.toString()+"/"+name)
        }

        if (name == null) {
            print(file?.parent)
            props["saveFolder"] = file?.parent
            print(props.keys.contains("saveFolder"))
        }

        if (file != null) {
            try {
                val bufferedImage = SwingFXUtils.fromFXImage(img, null)
                ImageIO.write(bufferedImage, "png", file)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    @FXML
    fun platformExit() {
        Platform.exit()
    }

    @FXML
    fun fastSave() {
        saveImage(LocalDateTime.now().toString()+".png")
    }

    @FXML
    fun openImage() {
        val fileChooser: FileChooser = FileChooser()
        fileChooser.title = "Open Image"
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("Png image *.png", "*.png"), FileChooser.ExtensionFilter("Jpg image *.jpg","*.jpg"), FileChooser.ExtensionFilter("Jpeg image *.jpeg","*.jpeg"))
        val file = fileChooser.showOpenDialog(stage)

        val newImg = SwingFXUtils.toFXImage(ImageIO.read(file.inputStream()), null)
        imageView.image = newImg
        imageView.fitWidth = newImg.width
        imageView.fitHeight = newImg.height
        canvas.widthProperty().bind(imageView.fitWidthProperty())
        canvas.heightProperty().bind(imageView.fitHeightProperty())
        imageContainer.prefHeightProperty().bind(imageView.fitHeightProperty())
        imageContainer.prefWidthProperty().bind(imageView.fitWidthProperty())
        gc.clearRect(0.0, 0.0, canvas.width, canvas.height)
    }

}