package com.example.appl1;

import javafx.application.Application;
//import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.CLAHE;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.LineSegmentDetector;
import org.w3c.dom.Text;

import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import java.awt.image.BufferedImage;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import javafx.geometry.Orientation;

public class HelloApplication extends Application implements Comparator<Double> {

    public static WritableImage MatToImageFX(Mat m) {
        if (m == null || m.empty()) return null;
        if (m.depth() == CvType.CV_8U) {}
        else if (m.depth() == CvType.CV_16U) {
            Mat m_16 = new Mat();
            m.convertTo(m_16, CvType.CV_8U, 255.0 / 65535);
            m = m_16;
        }
        else if (m.depth() == CvType.CV_32F) {
            Mat m_32 = new Mat();
            m.convertTo(m_32, CvType.CV_8U, 255);
            m = m_32;
        }
        else
            return null;
        if (m.channels() == 1) {
            Mat m_bgra = new Mat();
            Imgproc.cvtColor(m, m_bgra, Imgproc.COLOR_GRAY2BGRA);
            m = m_bgra;
        }
        else if (m.channels() == 3) {
            Mat m_bgra = new Mat();
            Imgproc.cvtColor(m, m_bgra, Imgproc.COLOR_BGR2BGRA);
            m = m_bgra;
        }
        else if (m.channels() == 4) { }
        else
            return null;
        byte[] buf = new byte[m.channels() * m.cols() * m.rows()];
        m.get(0, 0, buf);
        WritableImage wim = new WritableImage(m.cols(), m.rows());
        PixelWriter pw = wim.getPixelWriter();
        pw.setPixels(0, 0, m.cols(), m.rows(),
                WritablePixelFormat.getByteBgraInstance(),
                buf, 0, m.cols() * 4);
        return wim;
    }

    public static ImageView setImageView(Mat m, int x, int y, int h, int w){
        WritableImage wrImage = MatToImageFX(m);
        ImageView wrImageView = new ImageView(wrImage);
        wrImageView.setX(x);
        wrImageView.setY(y);
        wrImageView.setFitHeight(h);
        wrImageView.setFitWidth(w);
        return wrImageView;
    }


    @Override
    public int compare(Double o1, Double o2) {
        return o1.compareTo(o2);
    }

    void setMat(String str){
        mat = Imgcodecs.imread(str);

    }

    public Mat mat;
    //public Stage stage;

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }
    @Override
    public void start(Stage stage) throws Exception {
        int WID = 1200;
        int HIE = 400;
        Label lbl = new Label();
        TextField textField = new TextField();
        textField.setPrefColumnCount(11);
        TextField textField2 = new TextField();
        textField.setPrefColumnCount(10);
        Button btn = new Button("Enter path of image location\n(for contrast)");
        Button btn2 = new Button("Enter path of image location\n(for segmentation)");
        textField.setText("F:\\JVPr\\lab_3_pkg\\tiger-low-contrast.png");
        textField2.setText("F:\\Photo\\segm.png");
        //btn.setOnAction(event -> lbl.setText("Input: " + textField.getText()));
        FlowPane root1 = new FlowPane(Orientation.VERTICAL, 1000, 10, textField, btn, btn2, textField2,lbl);
//        btn2.setMinSize(100,100);
//        btn.setMinSize(100,100);
//        textField.setMinSize(100,200);
//        textField2.setMinSize(100,200);
//        textField.setLayoutY(50);
        root1.setLayoutX(1000);
        root1.setLayoutY(0);

        stage.setTitle("Reading image");


        Scene scene = new Scene(root1, WID, HIE);
        // Adding scene to the stage
        stage.setScene(scene);
        // Displaying the contents of the stage
        stage.show();
        btn.setOnAction(event -> {
            setMat(textField.getText());
            if (mat.empty()) {
                lbl.setText("Не удалось загрузить изображение");
                return;
            }
            System.out.println(CvType.typeToString(mat.depth()));

            Mat m = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
            Mat matB = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
            for (int i = 0, r = m.rows(); i < r; i++) {
                for (int j = 0, c = m.cols(); j < c; j++) {
                    matB.put(i, j, mat.get(i, j)[0] * 1 + 0);
                }
            }
            ArrayList<Double> smat = new ArrayList<>();
            ArrayList<Integer> smat2 = new ArrayList<>();
            int[] arr = new int[256];
            int[] arr2 = new int[256];
            double[] arr3 = new double[256];
            double sum = 0;
            for (int i = 0, r = m.rows(); i < r; i++) {
                for (int j = 0, c = m.cols(); j < c; j++) {
                    smat.add(mat.get(i, j)[0]);
                    sum += mat.get(i, j)[0];
                    int indx = (int) mat.get(i, j)[0];
                    int el = arr[indx] + 1;
                    arr[indx] = el;
                }
            }

//            System.out.println(Arrays.toString(arr3));
//            System.out.println(Arrays.toString(arr));
            Comparator<Double> comp = new Comparator<Double>() {
                @Override
                public int compare(Double o1, Double o2) {
                    return o1.compareTo(o2);
                }

                @Override
                public boolean equals(Object obj) {
                    return this == obj;
                }
            };
            smat.sort(comp);
            double min = smat.get(0);
            double max = smat.get(smat.size() - 1);


            for (int i = 0; i < m.rows(); i++) {
                for (int j = 0; j < m.cols(); j++) {
                    m.put(i, j, (mat.get(i, j)[0] - min) * 255 / (max - min)); // линейное контрастирование
                }
            }
            for (int i = 0; i < m.rows(); i++) {
                for (int j = 0; j < m.cols(); j++) {
                    int indx = (int) m.get(i, j)[0];
                    int el = arr2[indx] + 1;
                    arr2[indx] = el;
                }
            }

            Mat img2 = new Mat();
            Imgproc.cvtColor(mat, img2, Imgproc.COLOR_BGR2GRAY);   //выравнивание гистограмы
            Mat img3 = new Mat();
            CLAHE clane = Imgproc.createCLAHE();
            clane.setClipLimit(4);
            clane.apply(img2, img3);
            for (int i = 0, r = img3.rows(); i < r; i++) {
                for (int j = 0, c = img3.cols(); j < c; j++) {
                    int indx = (int) img3.get(i, j)[0];
                    int el = (int) arr3[indx] + 1;
                    arr3[indx] = el;
                }
            }


            CategoryAxis x = new CategoryAxis();    ///рисование гистограмм
            x.setLabel("Color\nИсходное изображение");
//y axis
            NumberAxis y = new NumberAxis();
            y.setLabel("Count");
//bar chart creation
            BarChart bc = new BarChart(x, y);
//add values
            XYChart.Series ds = new XYChart.Series();
            for (int i = 0; i < 256; i++) {
                ds.getData().add(new XYChart.Data(Integer.toString(i), arr[i]));
            }
            //ds.getData().addAll(col);
            bc.getData().add(ds);

            bc.setMaxHeight(200);
            bc.setMaxWidth(300);
            bc.setLayoutX(320);
            bc.setLayoutY(220);

            CategoryAxis x2 = new CategoryAxis();
            x2.setLabel("Color\nлинейное контрастирование");
//y axis
            NumberAxis y2 = new NumberAxis();
            y2.setLabel("Count");
//bar chart creation
            BarChart bc2 = new BarChart(x2, y2);
//add values
            XYChart.Series ds2 = new XYChart.Series();
            for (int i = 0; i < 256; i++) {
                ds2.getData().add(new XYChart.Data(Integer.toString(i), arr2[i]));
            }
            //ds.getData().addAll(col);
            bc2.getData().add(ds2);

            bc2.setMaxHeight(200);
            bc2.setMaxWidth(300);
            bc2.setLayoutX(0);
            bc2.setLayoutY(220);

            CategoryAxis x3 = new CategoryAxis();
            x3.setLabel("Color\nвыравнивание гистограммы");
//y axis
            NumberAxis y3 = new NumberAxis();
            y3.setLabel("Count");
//bar chart creation
            BarChart bc3 = new BarChart(x3, y3);
//add values
            XYChart.Series ds3 = new XYChart.Series();
            for (int i = 0; i < 256; i++) {
                ds3.getData().add(new XYChart.Data(Integer.toString(i), arr3[i]));
            }
            //ds.getData().addAll(col);
            bc3.getData().add(ds3);

            bc3.setMaxHeight(200);
            bc3.setMaxWidth(300);
            bc3.setLayoutX(640);
            bc3.setLayoutY(220);

            ImageView wrImageView = setImageView(m, 10, 20, 200, 300);
            ImageView defImageView = setImageView(mat, 320, 20, 200, 300);
            ImageView bfImageView = setImageView(img3, 630, 20, 200, 300);
            // Setting the preserve ratio of the image view
            //imageView.setPreserveRatio(true);

            // Creating a Group object

            // Creating a scene object

            // Setting title to the Stage
            Group root = new Group(wrImageView, defImageView, bfImageView, bc, bc2, bc3, root1);
            root.setLayoutX(0);
            root.setLayoutY(0);
            stage.setTitle("Reading image");
            //Label bt = new Label("Enter path of image location\n(with double slash\\)");

            Scene scene1 = new Scene(root, WID, HIE);
            // Adding scene to the stage
            stage.setScene(scene1);
                // Displaying the contents of the stage
            stage.show();
        });

        btn2.setOnAction(event -> {
            Mat img = Imgcodecs.imread(textField2.getText());
            ImageView wrImageViewS1 = setImageView(img, 10, 20, 300, 450);
            Mat imgGray = new Mat();
            Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
            Mat lines = new Mat();
            Mat width = new Mat();
            Mat prec = new Mat();
            Mat result = new Mat(img.size(), CvType.CV_8UC3);
            Mat result2 = new Mat(img.size(), CvType.CV_8UC3);
            LineSegmentDetector d = Imgproc.createLineSegmentDetector();
            d.detect(imgGray, lines, width, prec, new Mat());
//            System.out.println(CvType.typeToString(lines.type())); // CV_32FC4
//            System.out.println(lines.size()); // 1x20
//            System.out.println(CvType.typeToString(width.type())); // CV_64FC1
//            System.out.println(width.size()); // 1x20
//            System.out.println(width.dump());
//            System.out.println(CvType.typeToString(prec.type())); // CV_64FC1
//            System.out.println(prec.size()); // 1x20
//            System.out.println(prec.dump());
            d.drawSegments(result, lines);
            Imgproc.Sobel(imgGray, result2, -1, 1, 1);
            //CvUtilsFX.showImage(result, "Результат");
            ImageView wrImageViewS2 = setImageView(result, 520, 20, 300, 450);
            ImageView wrImageViewS3 = setImageView(result, 630, 20, 300, 300);
            Group root = new Group(wrImageViewS1, wrImageViewS2, root1);
            root.setLayoutX(0);
            root.setLayoutY(0);
            stage.setTitle("Reading image");
            //Label bt = new Label("Enter path of image location\n(with double slash\\)");

            Scene scene1 = new Scene(root, WID, HIE);
            // Adding scene to the stage
            stage.setScene(scene1);
            // Displaying the contents of the stage
            stage.show();
            img.release();
            imgGray.release();
            result.release();
        });


    }

    public static void main(String args[]) throws Exception {
        launch(args);
    }

}
