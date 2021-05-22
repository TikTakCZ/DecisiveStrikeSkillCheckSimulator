package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


public class Main extends Application {

    // Parametry sipky na skillchecku
    private int sipka_pocet_stupnu_nahnuti = 0;

    // Soubor pro zvuk pri hitnuti skillchecku
    File soubor_hudba = new File("herni_zvuk.wav");

    // Počítání hitnutých skillchecku po sobě
    private int pocet_hitnutych_skillchecku_po_sobe = 0;

    // Zaklikavaci ctverecky na vybrani pozic spawnuti skillcheck ctverecku
    Rectangle zaklikavaci_policko_pozice1 = new Rectangle();
    Rectangle zaklikavaci_policko_pozice2 = new Rectangle();
    private boolean policko_1_zakliknuto = true;
    private boolean policko_2_zakliknuto = true;
    private boolean probehla_vymena_skillchecku = false;


    @Override
    public void start(Stage podium) throws Exception{
        Group pozadi_okna = new Group();
        Scene scena = new Scene(pozadi_okna);
        podium.setScene(scena);

        // Vlastnosti okna
        podium.setTitle("Decisive Strike Simulátor");
        podium.setWidth(600);
        podium.setHeight(400);
        podium.setResizable(false);
        // Pridani ikony okna do aplikace
        podium.getIcons().add(new Image("ikona_okna.png"));

        // Přidání fotky na scenu která bude představovat pozadí okna
        ImageView fotka_pridana_na_scenu_pro_pozadi_okna = new ImageView("fotka_na_pozadi_okna.jpg");
        // Vlastnosti
        fotka_pridana_na_scenu_pro_pozadi_okna.setX(scena.getX());
        fotka_pridana_na_scenu_pro_pozadi_okna.setY(scena.getY());
        // Pridani na scenu
        pozadi_okna.getChildren().add(fotka_pridana_na_scenu_pro_pozadi_okna);

        // VYROBENÍ SKILLCHECKU A PŘIDÁNÍ SKILLCHECKU NA SCENU

        // Kruh skillchecku
        Circle kruh_skillchecku = new Circle();
        // Pozice
        kruh_skillchecku.setCenterX(290);
        kruh_skillchecku.setCenterY(180);
        // Vlastnosti
        kruh_skillchecku.setRadius(120);
        kruh_skillchecku.setFill(Color.TRANSPARENT);
        kruh_skillchecku.setStroke(Color.WHITE);
        // Pridani na scenu
        pozadi_okna.getChildren().add(kruh_skillchecku);

        // Sipka skillchecku
        ImageView sipka_skillchecku = new ImageView("sipka350x350.png");
        // Pozice
        sipka_skillchecku.setX(110);
        sipka_skillchecku.setY(5);
        // Vlastnosti
        sipka_skillchecku.setRotate(0);
        // Pridani na scenu
        pozadi_okna.getChildren().add(sipka_skillchecku);

        // Prvni hitovaci ctverecek
        Rectangle prvni_hitovaci_ctverecek = new Rectangle();
        // Pozice
        prvni_hitovaci_ctverecek.setX(165);
        prvni_hitovaci_ctverecek.setY(165);
        // Vlastnosti
        prvni_hitovaci_ctverecek.setWidth(12);
        prvni_hitovaci_ctverecek.setHeight(30);
        prvni_hitovaci_ctverecek.setFill(Color.WHITE);
        prvni_hitovaci_ctverecek.setVisible(true);
        // Pridani na scenu
        pozadi_okna.getChildren().add(prvni_hitovaci_ctverecek);

        // Druhy hitovaci ctverecek
        Rectangle druhy_hitovaci_ctverecek = new Rectangle();
        // Pozice
        druhy_hitovaci_ctverecek.setX(205);
        druhy_hitovaci_ctverecek.setY(75);
        // Vlastnosti
        druhy_hitovaci_ctverecek.setWidth(12);
        druhy_hitovaci_ctverecek.setHeight(30);
        druhy_hitovaci_ctverecek.setRotate(45);
        druhy_hitovaci_ctverecek.setFill(Color.WHITE);
        druhy_hitovaci_ctverecek.setVisible(false);
        // Pridani na scenu
        pozadi_okna.getChildren().add(druhy_hitovaci_ctverecek);

        // VYROBENÍ ZAKLIKÁVACÍCH POLÍČEK NA VÝBĚR HITOVACICH CTVERECKU

        // POLÍČKO 1 CTVERECEK
        // Pozice
        zaklikavaci_policko_pozice1.setX(450);
        zaklikavaci_policko_pozice1.setY(20);
        // Vlastnosti
        zaklikavaci_policko_pozice1.setWidth(15);
        zaklikavaci_policko_pozice1.setHeight(15);
        zaklikavaci_policko_pozice1.setStrokeWidth(1);
        zaklikavaci_policko_pozice1.setStroke(Color.WHITE);
        // Zapnuti zaklikávacího políčka už při zapnutí aplikace
        zaklikavaci_policko_pozice1.setFill(Color.WHITE);
        // Pridani na scenu
        pozadi_okna.getChildren().add(zaklikavaci_policko_pozice1);

        // Kliknuti na policko 1
        zaklikavaci_policko_pozice1.setOnMouseClicked(mouseEvent -> {
            // Když první políčko neni zakliknutý a druhý políčko je tak se první políčko může zakliknout
            if (policko_1_zakliknuto == false && policko_2_zakliknuto == true){
                zaklikavaci_policko_pozice1.setFill(Color.WHITE);
                policko_1_zakliknuto = true;
            }
            // Když první políčko je zakliknutý a druhý políčko je taky zakliknutý tak se první políčko může odkliknout
            else if (policko_1_zakliknuto == true && policko_2_zakliknuto == true){
                zaklikavaci_policko_pozice1.setFill(Color.TRANSPARENT);
                policko_1_zakliknuto = false;
            }
        });

        // POLÍČKO 1 TEXT
        Text text_zaklikavaci_policko_pozice1 = new Text();
        // Pozice
        text_zaklikavaci_policko_pozice1.setX(470);
        text_zaklikavaci_policko_pozice1.setY(34);
        // Vlastnosti
        text_zaklikavaci_policko_pozice1.setText("Pozice 1");
        text_zaklikavaci_policko_pozice1.setFill(Color.WHITE);
        text_zaklikavaci_policko_pozice1.setFont(Font.font("Times New Roman", 20));
        // Pridani na scenu
        pozadi_okna.getChildren().add(text_zaklikavaci_policko_pozice1);
        
        // POLÍČKO 2 CTVERECEK
        // Pozice
        zaklikavaci_policko_pozice2.setX(450);
        zaklikavaci_policko_pozice2.setY(40);
        // Vlastnosti
        zaklikavaci_policko_pozice2.setWidth(15);
        zaklikavaci_policko_pozice2.setHeight(15);
        zaklikavaci_policko_pozice2.setStrokeWidth(1);
        zaklikavaci_policko_pozice2.setStroke(Color.WHITE);
        // Zapnuti políčka dva už při zapnutí aplikace
        zaklikavaci_policko_pozice2.setFill(Color.WHITE);
        // Pridani na scenu
        pozadi_okna.getChildren().add(zaklikavaci_policko_pozice2);

        // Kliknuti na policko 2
        zaklikavaci_policko_pozice2.setOnMouseClicked(mouseEvent -> {
            // Když druhý políčko neni zakliknutý a první políčko je tak se druhý políčko může zakliknout
            if (policko_2_zakliknuto == false && policko_1_zakliknuto == true){
                zaklikavaci_policko_pozice2.setFill(Color.WHITE);
                policko_2_zakliknuto = true;
            }
            // Když druhý políčko je zakliknutý a první políčko je taky zakliknutý tak se druhý políčko může odkliknout
            else if (policko_2_zakliknuto == true && policko_1_zakliknuto == true){
                zaklikavaci_policko_pozice2.setFill(Color.TRANSPARENT);
                policko_2_zakliknuto = false;
            }
        });

        // POLÍČKO 2 TEXT
        Text text_zaklikavaci_policko_pozice2 = new Text();
        // Pozice
        text_zaklikavaci_policko_pozice2.setX(470);
        text_zaklikavaci_policko_pozice2.setY(54);
        // Vlastnosti
        text_zaklikavaci_policko_pozice2.setText("Pozice 2");
        text_zaklikavaci_policko_pozice2.setFill(Color.WHITE);
        text_zaklikavaci_policko_pozice2.setFont(Font.font("Times New Roman", 20));
        // Pridani na scenu
        pozadi_okna.getChildren().add(text_zaklikavaci_policko_pozice2);

        // Přidání počtu hitnutých skillchecku po sobě
        Text text_pocet_hitnutych_skillchecku_po_sobe = new Text();
        // Pozice
        text_pocet_hitnutych_skillchecku_po_sobe.setX(5);
        text_pocet_hitnutych_skillchecku_po_sobe.setY(20);
        // Vlastnosti
        text_pocet_hitnutych_skillchecku_po_sobe.setText("" + pocet_hitnutych_skillchecku_po_sobe);
        text_pocet_hitnutych_skillchecku_po_sobe.setFill(Color.WHITE);
        text_pocet_hitnutych_skillchecku_po_sobe.setFont(Font.font("Times New Roman", 20));
        // Pridani na scenu
        pozadi_okna.getChildren().add(text_pocet_hitnutych_skillchecku_po_sobe);

        // Hitování skillchecků
        scena.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.SPACE){
                    // KDYŽ HITNU PRVNI HITBOX CTVERECEK A PRVNI HITBOX CTVERECEK JE VIDITELNEJ NA SCÉNĚ
                    if (sipka_pocet_stupnu_nahnuti >= 172 && sipka_pocet_stupnu_nahnuti <= 185 && prvni_hitovaci_ctverecek.isVisible()){
                        try {
                            AudioInputStream hudba = AudioSystem.getAudioInputStream(soubor_hudba);
                            Clip klip = AudioSystem.getClip();
                            klip.open(hudba);
                            klip.start();
                            pocet_hitnutych_skillchecku_po_sobe += 1;
                            text_pocet_hitnutych_skillchecku_po_sobe.setText("" + pocet_hitnutych_skillchecku_po_sobe);
                            sipka_pocet_stupnu_nahnuti = 0;

                            // Když je první políčko zakliknutý a druhý taky ale ještě stále se hituje první skillcheck
                            if (policko_1_zakliknuto == true && policko_2_zakliknuto == true && probehla_vymena_skillchecku == false) {
                                prvni_hitovaci_ctverecek.setVisible(false);
                                druhy_hitovaci_ctverecek.setVisible(true);
                                probehla_vymena_skillchecku = true;
                            }
                            // Když je první políčko zakliknutý a druhý taky a už se hituje druhej skillcheck
                            else if (policko_1_zakliknuto == true &&policko_2_zakliknuto == true && probehla_vymena_skillchecku == true){
                                prvni_hitovaci_ctverecek.setVisible(true);
                                druhy_hitovaci_ctverecek.setVisible(false);
                                probehla_vymena_skillchecku = false;
                            }
                            // Když je první políčko zakliknutý ale druhý ne
                            else if (policko_1_zakliknuto == true & policko_2_zakliknuto == false){
                                prvni_hitovaci_ctverecek.setVisible(true);
                                druhy_hitovaci_ctverecek.setVisible(false);
                            }
                            // Když první políčko neni zakliknutý ale druhý je
                            else if (policko_1_zakliknuto == false & policko_2_zakliknuto == true){
                                prvni_hitovaci_ctverecek.setVisible(false);
                                druhy_hitovaci_ctverecek.setVisible(true);
                            }
                        }

                        catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                    // Když nehitnu první hitbox ctverecek a první hitbox ctverecek je viditelnej na scéně
                    else if (sipka_pocet_stupnu_nahnuti < 172 || sipka_pocet_stupnu_nahnuti > 185 && prvni_hitovaci_ctverecek.isVisible()){
                        sipka_pocet_stupnu_nahnuti = 0;
                        pocet_hitnutych_skillchecku_po_sobe = 0;
                        text_pocet_hitnutych_skillchecku_po_sobe.setText("" + pocet_hitnutych_skillchecku_po_sobe);
                    }

                    // Když hitnu druhý hitbox ctverecek a druhý hitbox ctverecek je viditelný na scéně
                    else if (sipka_pocet_stupnu_nahnuti >= 222 && sipka_pocet_stupnu_nahnuti <= 239 && druhy_hitovaci_ctverecek.isVisible()){
                        try {
                            AudioInputStream hudba = AudioSystem.getAudioInputStream(soubor_hudba);
                            Clip klip = AudioSystem.getClip();
                            klip.open(hudba);
                            klip.start();
                            pocet_hitnutych_skillchecku_po_sobe += 1;
                            text_pocet_hitnutych_skillchecku_po_sobe.setText("" + pocet_hitnutych_skillchecku_po_sobe);
                            sipka_pocet_stupnu_nahnuti = 0;

                            // Když je první políčko zakliknutý a druhý taky ale ještě stále se hituje první skillcheck
                            if (policko_1_zakliknuto == true && policko_2_zakliknuto == true && probehla_vymena_skillchecku == false) {
                                prvni_hitovaci_ctverecek.setVisible(false);
                                druhy_hitovaci_ctverecek.setVisible(true);
                                probehla_vymena_skillchecku = true;
                            }
                            // Když je první políčko zakliknutý a druhý taky a už se hituje druhej skillcheck
                            else if (policko_1_zakliknuto == true &&policko_2_zakliknuto == true && probehla_vymena_skillchecku == true){
                                prvni_hitovaci_ctverecek.setVisible(true);
                                druhy_hitovaci_ctverecek.setVisible(false);
                                probehla_vymena_skillchecku = false;
                            }
                            // Když je první políčko zakliknutý ale druhý ne
                            else if (policko_1_zakliknuto == true & policko_2_zakliknuto == false){
                                prvni_hitovaci_ctverecek.setVisible(true);
                                druhy_hitovaci_ctverecek.setVisible(false);
                            }
                            // Když první políčko neni zakliknutý ale druhý je
                            else if (policko_1_zakliknuto == false & policko_2_zakliknuto == true){
                                prvni_hitovaci_ctverecek.setVisible(false);
                                druhy_hitovaci_ctverecek.setVisible(true);
                            }
                        }

                        catch (Exception e) {
                            System.out.println(e);
                        }
                    }

                    // Když nehitnu druhej hitbox ctverecek a druhej hitbox ctverecek je viditelnej na scéně
                    else if (sipka_pocet_stupnu_nahnuti <= 222 || sipka_pocet_stupnu_nahnuti >= 239 && druhy_hitovaci_ctverecek.isVisible()){
                        sipka_pocet_stupnu_nahnuti = 0;
                        pocet_hitnutych_skillchecku_po_sobe = 0;
                        text_pocet_hitnutych_skillchecku_po_sobe.setText("" + pocet_hitnutych_skillchecku_po_sobe);
                    }
                }
            }
        });

        // Hlavní animace skillchecku která se spustí na novým vláknu procesoru
        Timer casovac = new Timer();
        casovac.schedule(new TimerTask() {
            public void run() {
                // Spuštění nového vlákna procesoru
                Platform.runLater(() -> {
                    if (sipka_pocet_stupnu_nahnuti <= 270) {
                        sipka_skillchecku.setRotate(sipka_pocet_stupnu_nahnuti += 1);
                    }
                    else if (sipka_pocet_stupnu_nahnuti >= 270) {
                        sipka_pocet_stupnu_nahnuti = 0;
                        pocet_hitnutych_skillchecku_po_sobe = 0;
                        text_pocet_hitnutych_skillchecku_po_sobe.setText("" + pocet_hitnutych_skillchecku_po_sobe);
                    }
                });
            }
        }, 0, 6);

        // Na pódiu se zobrazí scéna
        podium.show();
    }
}
