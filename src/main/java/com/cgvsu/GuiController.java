package com.cgvsu;

import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.model.TransformedModel;
import com.cgvsu.render_engine.RenderEngine;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {

    final private float TRANSLATION = 0.5F;

    final private float SCALE = 0.125F;

    final private float ROTATE_PARAM = 0.5F;

    static final float EPS = 1e-6f;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    private Model mesh = null;

    private ArrayList<KeyCode> keyCodes = null;

    private Camera camera = new Camera(
            new Vector3f(new float[]{0, 00, 100}),
            new Vector3f(new float[]{0, 0, 0}),
            1.0F, 1, 0.01F, 100);

    private TransformedModel transformedModel = new TransformedModel(mesh);

    private Timeline timeline;

    @FXML
    private void initialize() {
        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);
        keyCodes = new ArrayList<KeyCode>();

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            if (mesh != null) {
                canvas.setOnScroll(this::handleMouseWheelMoved);
                canvas.setOnMousePressed(this::handleMousePressed);
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, transformedModel, mesh,
                        (int) width, (int) height);
            }
        });


        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    public void shoot(KeyEvent e) {
        KeyCode key = e.getCode();
        if (!keyCodes.contains(key)) {
            keyCodes.add(key);
        }
        handleKeys();
    }

    private void handleKeyPressed(KeyEvent e, int sign) {
        if (e == null) {
            camera.movePosition(new Vector3f(new float[]{0, 0, sign * TRANSLATION}));
        } else if (e.getCode() == KeyCode.D) {
            camera.movePosition(new Vector3f(new float[]{0, sign * TRANSLATION, 0}));
        } else if (e.getCode() == KeyCode.W) {
            camera.movePosition(new Vector3f(new float[]{sign * TRANSLATION, 0, 0}));
        }
    }

    private boolean handleMouseWheel(ScrollEvent event) {
        return event.getDeltaY() < 0;
    }

    private void handleMouseWheelMoved(ScrollEvent event) {
        double notches = event.getDeltaY();
        float x = camera.getPosition().get(0);
        float y = camera.getPosition().get(1);
        float z = camera.getPosition().get(2);
        float signX = 1;
        float signY = 1;
        float signZ = 1;
        if (x < 0) {
            signX = -1;
            x = Math.abs(x);
        }
        if (y < 0) {
            signY = -1;
            y = Math.abs(y);
        }
        if (z < 0) {
            signZ = -1;
            z = Math.abs(z);
        }
        if (notches < 0) {
            if (x - y >= EPS && x - z >= EPS) {
                camera.movePosition(new Vector3f(new float[]{signX * TRANSLATION, 0, 0}));
            } else if (y - x >= EPS && y - z >= EPS) {
                camera.movePosition(new Vector3f(new float[]{0, signY * TRANSLATION, 0}));
            } else {
                camera.movePosition(new Vector3f(new float[]{0, 0, signZ * TRANSLATION}));
            }
            //handleCameraBackward();
        } else {
            if (x - y >= EPS && x - z >= EPS) {
                camera.movePosition(new Vector3f(new float[]{-signX * TRANSLATION, 0, 0}));
            } else if (y - x >= EPS && y - z >= EPS) {
                camera.movePosition(new Vector3f(new float[]{0, -signY * TRANSLATION, 0}));
            } else {
                camera.movePosition(new Vector3f(new float[]{0, 0, -signZ * TRANSLATION}));
            }
            //handleCameraForward();
        }
    }

    private void handleMousePressed(javafx.scene.input.MouseEvent event) {
        var ref = new Object() {
            float prevX = (float) event.getX();
            float prevY = (float) event.getY();
        };
        canvas.setOnMouseDragged(mouseEvent -> {
            final float actualX = (float) mouseEvent.getX();
            final float actualY = (float) mouseEvent.getY();
            float dx = ref.prevX - actualX;
            final float dy = actualY - ref.prevY;
            final float dxy = Math.abs(dx) - Math.abs(dy);
            float dz = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

            if (dxy >= EPS && (camera.getPosition().get(0) <= EPS && dx < 0 ||
                    camera.getPosition().get(0) > EPS && dx > 0)) {
                dz *= -1;
            } else if (dxy < EPS) { //если больше перемещаем по y, то по z е перемещаем
                dz = 0;
            }
            if (camera.getPosition().get(2) <= EPS) {
                dx *= -1;
            }

            ref.prevX = actualX;
            ref.prevY = actualY;
            camera.movePosition(new Vector3f(new float[]{dx * 0.01f, dy * 0.01f, dz * 0.01f}));
        });
    }

    /*
    private void handleMousePressed1(javafx.scene.input.MouseEvent event) {
        var ref = new Object() {
            float prevX = (float) event.getX();
            float prevY = (float) event.getY();
        };
        canvas.setOnMouseDragged(mouseEvent -> {
            final float actualX = (float) mouseEvent.getX();
            final float actualY = (float) mouseEvent.getY();
            float dx = 0;
            float dy = 0;
            float dz = 0;

            if (keyCodes.contains(KeyCode.X)) {
                dx = ref.prevX - actualX;
                if (camera.getPosition().get(2) <= EPS) {
                    dx *= -1;
                }
            } else if (keyCodes.contains(KeyCode.Y)) {
                dy = actualY - ref.prevY;
            } else if (keyCodes.contains(KeyCode.Z)) {
                dz = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            }
            float dxy = Math.abs(dx) - Math.abs(dy);

            if (dxy >= EPS && (camera.getPosition().get(0) <= EPS && dx < 0 ||
                    camera.getPosition().get(0) > EPS && dx > 0)) {
                dz *= -1;
            } else if (dxy < EPS) { //если больше перемещаем по y, то по z е перемещаем
                dz = 0;
            }

            ref.prevX = actualX;
            ref.prevY = actualY;
            camera.moveTarget(new Vector3f(new float[]{dx * 0.01f, dy * 0.01f, dz * 0.01f}));
        });
    }

     */

    private void handleTranslate() {
        if (keyCodes.contains(KeyCode.D)) {
            translateX1();
        } else if (keyCodes.contains(KeyCode.A)) {
            translateX();
        }
        if (keyCodes.contains(KeyCode.W)) {
            translateY();
        } else if (keyCodes.contains(KeyCode.S)) {
            translateY1();
        }
        if (keyCodes.contains(KeyCode.UP)) {
            translateZ();
        } else if (keyCodes.contains(KeyCode.DOWN)) {
            translateZ1();
        }
    }

    private void handleScale() {
        if (keyCodes.contains(KeyCode.D)) {
            scaleObjectByX();
        } else if (keyCodes.contains(KeyCode.A)) {
            scaleObjectByX1();
        }
        if (keyCodes.contains(KeyCode.W)) {
            scaleObjectByY();
        } else if (keyCodes.contains(KeyCode.S)) {
            scaleObjectByY1();
        }
        if (keyCodes.contains(KeyCode.UP)) {
            scaleObjectByZ();
        } else if (keyCodes.contains(KeyCode.DOWN)) {
            scaleObjectByZ1();
        }
    }

    private void handleRotate() {
        if (keyCodes.contains(KeyCode.D) && keyCodes.contains(KeyCode.S)) {
            rotateAroundZ();
        } else if (keyCodes.contains(KeyCode.D)) {
            rotateAroundY();
        } else if (keyCodes.contains(KeyCode.S)) {
            rotateAroundX1();
        }
        if (keyCodes.contains(KeyCode.W) && (keyCodes.contains(KeyCode.A))) {
            rotateAroundZ1();
        } else if (keyCodes.contains(KeyCode.W)) {
            rotateAroundX();
        } else if (keyCodes.contains(KeyCode.A)) {
            rotateAroundY1();
        }
    }

    private void handleKeys() {
        if (keyCodes.contains(KeyCode.G)) {
            handleTranslate();
        } else if (keyCodes.contains(KeyCode.E)) {
            canvas.setOnScroll(this::handleMouseWheelMoved);
            handleScale();
        } else if (keyCodes.contains(KeyCode.R)) {
            handleRotate();
        }
    }

    public void handleKeyReleased(KeyEvent event) {
        keyCodes.remove(event.getCode());
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    // 9 параметров
    @FXML
    public void handleCameraForward() {
        if (keyCodes.contains(KeyCode.UP)) {
            camera.movePosition(new Vector3f(new float[]{0, 0, -TRANSLATION}));
        }
    }

    @FXML
    public void handleCameraBackward() {
        //<MenuItem mnemonicParsing="false" onAction="#handleCameraBackward" text="Backward" accelerator="DOWN"/>
        if (keyCodes.contains(KeyCode.DOWN)) {
            camera.movePosition(new Vector3f(new float[]{0, 0, TRANSLATION}));
        }
    }

    @FXML
    public void handleCameraLeft() {
        if (keyCodes.contains(KeyCode.A)) {
            camera.movePosition(new Vector3f(new float[]{TRANSLATION, 0, 0}));
        }
    }

    @FXML
    public void handleCameraRight() {
        if (keyCodes.contains(KeyCode.D)) {
            camera.movePosition(new Vector3f(new float[]{-TRANSLATION, 0, 0}));
        }
    }

    @FXML
    public void handleCameraUp() {
        if (keyCodes.contains(KeyCode.W)) {
            camera.movePosition(new Vector3f(new float[]{0, TRANSLATION, 0}));
        }
    }

    @FXML
    public void handleCameraDown() {
        if (keyCodes.contains(KeyCode.S)) {
            camera.movePosition(new Vector3f(new float[]{0, -TRANSLATION, 0}));
        }
    }

    @FXML
    public void scaleObjectByX() {
        transformedModel.scaleXParams(SCALE);
    }

    @FXML
    public void scaleObjectByX1() {
        transformedModel.scaleXParams(-SCALE);
    }

    @FXML
    public void scaleObjectByY() {
        transformedModel.scaleYParams(SCALE);
    }

    @FXML
    public void scaleObjectByY1() {
        transformedModel.scaleYParams(-SCALE);
    }

    @FXML
    public void scaleObjectByZ() {
        transformedModel.scaleZParams(SCALE);
    }

    @FXML
    public void scaleObjectByZ1() {
        transformedModel.scaleZParams(-SCALE);
    }

    @FXML
    public void rotateAroundX() {
        transformedModel.setRotateXParam(ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundX1() {
        transformedModel.setRotateXParam(-ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundY() {
        transformedModel.setRotateYParam(ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundY1() {
        transformedModel.setRotateYParam(-ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundZ() {
        transformedModel.setRotateZParam(ROTATE_PARAM);
    }

    @FXML
    public void rotateAroundZ1() {
        transformedModel.setRotateZParam(-ROTATE_PARAM);
    }

    @FXML
    public void translateX() {
        transformedModel.setTranslateXParam(TRANSLATION);
    }

    @FXML
    public void translateX1() {
        transformedModel.setTranslateXParam(-TRANSLATION);
    }

    @FXML
    public void translateY() {
        transformedModel.setTranslateYParam(TRANSLATION);
    }

    @FXML
    public void translateY1() {
        transformedModel.setTranslateYParam(-TRANSLATION);
    }

    @FXML
    public void translateZ() {
        transformedModel.setTranslateZParam(TRANSLATION);
    }

    @FXML
    public void translateZ1() {
        transformedModel.setTranslateZParam(-TRANSLATION);
    }
}