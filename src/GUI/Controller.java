package GUI;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    private boolean wasUsed = false;

    @FXML
    public ImageView picture;

    @FXML
    private ImageView first;

    @FXML
    private ImageView second;

    @FXML
    private RadioButton originalImageTogle;

    @FXML
    private RadioButton modifiedImageTogle;

    private ImageView imgv;
    private CheckBox ch;
    private javafx.scene.control.Button b;
    private Slider s;
    private Stage stage;



    private Image originalImage;
    private Image modifiedImage;
    private Image tresholdEdited;

    private ToggleGroup tougleGroupImge = new ToggleGroup();

    public void tougleGroupInit() {
        if (!wasUsed) {
            tougleGroupImge.getToggles().add(originalImageTogle);
            tougleGroupImge.getToggles().add(modifiedImageTogle);
            originalImageTogle.setDisable(false);
            modifiedImageTogle.setDisable(false);
            tougleGroupImge.selectedToggleProperty().addListener((ob, o, n) -> {
                RadioButton rb = (RadioButton) tougleGroupImge.getSelectedToggle();
                if (rb != null) {
                    if (rb.getText().equals(originalImageTogle.getText())) {
                        picture.setImage(modifiedImage);
                    } else {
                        picture.setImage(modifiedImage);
                    }
                }

            });
        }
    }



    public void treshfilter(ActionEvent e) throws IOException {


        GridPane layout= new GridPane();
        imgv = new ImageView();
        imgv.setImage(modifiedImage);
        imgv.setFitHeight(423.0);
        imgv.setFitWidth(423.0);
        imgv.setX(100.0);
        imgv.setY(50);
        ch = new CheckBox("automatic treshold");
        ch.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                applyFilter();
            }
        });
        b = new Button("Apply");
        s = new Slider(0, 100, 1);
        s.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                applyFilter();
            }
                                              });
        b.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                picture.setImage(tresholdEdited);
                modifiedImage = tresholdEdited;
                second.setImage(tresholdEdited);
                originalImageTogle.setSelected(false);
                modifiedImageTogle.setSelected(true);
                stage.close();
            }
        });
        layout.add(imgv, 1, 1);
        layout.add(s, 1, 2);
        layout.add(ch, 1, 3);
        layout.add(b, 1, 4);
        layout.centerShapeProperty();
        Scene scene = new Scene(layout, 423, 500);
        stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("New Window");
        stage.setScene(scene);
        stage.show();
    }
    private int getAutomaticTreshold(){
        long outInt = 0;
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(originalImage, null);
        for (int x = 0; x < bufferedImage.getWidth(); x++){
            for (int y = 0; y < bufferedImage.getHeight(); y++){
                Color c = new Color(bufferedImage.getRGB(x, y));
                outInt = outInt + c.getRed() + c.getGreen() + c.getBlue();
            }
        }
        outInt = outInt / (bufferedImage.getHeight()*bufferedImage.getWidth()*3);
        return (int)outInt;
    }

    @FXML
    private void applyFilter(){
        int black = Color.BLACK.getRGB();
        int white = Color.WHITE.getRGB();
        double currentTreshold;
        if (ch.isSelected()){
            currentTreshold = getAutomaticTreshold();
        } else {
            currentTreshold = s.getValue();
        }
        BufferedImage img = SwingFXUtils.fromFXImage(originalImage, null);
        BufferedImage filteredImage = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        for (int x = 0; x < img.getWidth(); x++){
            for (int y = 0; y < img.getHeight(); y++){
                int rgb = img.getRGB(x, y);
                Color c = new Color(rgb);
                if (c.getRed() > currentTreshold ||
                        c.getGreen() > currentTreshold ||
                        c.getBlue() > currentTreshold){
                    filteredImage.setRGB(x, y, white);
                } else {
                    filteredImage.setRGB(x,y,black);
                }
            }
        }
        tresholdEdited = SwingFXUtils.toFXImage(filteredImage, null);
        imgv.setImage(tresholdEdited);
        System.out.println("Image change, treshold: " + currentTreshold);

    }




    @FXML
    public void exit() {
        System.exit(0);
    }

    @FXML
    public void loadImage() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open image file");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Images",
                "*.jpeg", "*.jpg", "*.png"));

        modifiedImageTogle.setSelected(false);
        originalImageTogle.setSelected(true);


        File file = fileChooser.showOpenDialog(new Stage());
        Image image = new Image(new FileInputStream(file));

        originalImage = image;
        modifiedImage = image;

        first.setImage(image);
        second.setImage(image);
        picture.setImage(image);

    }


    @FXML
    public void saveImage() throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");

        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Images",
                "*.jpeg", "*.jpg", "*.png"));

        Image image = picture.getImage();

        File file = fileChooser.showSaveDialog(new Stage());

        String formatName = file.toString();
        formatName = formatName.endsWith("jpeg") ? "jpeg"
                : formatName.endsWith("jpg") ? "jpg"
                : formatName.endsWith("png") ? "png" : "Nothing";

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ImageIO.write(bufferedImage, formatName, file);


    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        tougleGroupInit();
    }

    public void generateImage() {
        Image img = SwingFXUtils.toFXImage(makeColoredImage(), null);
        System.out.println(img);
        originalImage = img;
        modifiedImage = img;
        picture.setImage(modifiedImage);
        System.out.println(picture);
        first.setImage(modifiedImage);
        second.setImage(modifiedImage);

        modifiedImageTogle.setSelected(false);
        originalImageTogle.setSelected(true);
    }

    public BufferedImage makeColoredImage() {
        BufferedImage bImage = new BufferedImage(600, 600, BufferedImage.TYPE_3BYTE_BGR);
        for (int x = 0; x < bImage.getWidth(); x++) {
            for (int y = 0; y < bImage.getHeight(); y++) {
                bImage.setRGB(x, y, (new Color(x % 255, y % 255, (x + y) % 255).getRGB()));
            }
        }
        return bImage;
    }

    public void inversion() {
        BufferedImage image = SwingFXUtils.fromFXImage(picture.getImage(), null);
        BufferedImage filteredImage = new BufferedImage(image.getWidth(),
                image.getHeight(), image.getType());
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgbOrig = image.getRGB(x, y);
                Color c = new Color(rgbOrig);
                int r = 255 - c.getRed();
                int g = 255 - c.getGreen();
                int b = 255 - c.getBlue();
                Color nc = new Color(r, g, b);
                filteredImage.setRGB(x, y, nc.getRGB());
            }
            modifiedImageTogle.setSelected(true);
            originalImageTogle.setSelected(false);
        }
        modifiedImage = SwingFXUtils.toFXImage(filteredImage, null);
        picture.setImage(modifiedImage);
        first.setImage(originalImage);
        second.setImage(modifiedImage);
    }

    public void restoreImage() {
        picture.setImage(originalImage);
        modifiedImage = originalImage;
        first.setImage(originalImage);
        second.setImage(modifiedImage);
    }

    public void showOriginal() {
        picture.setImage(originalImage);
    }

    public void showModified() {
        picture.setImage(modifiedImage);
    }

    @FXML
    public void menuAboutAction() {
        try {
            about();
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public void about() throws Exception {
        Stage s = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("About.fxml"));
        s.setScene(new Scene(root, 800, 700));
        s.show();
    }
}
