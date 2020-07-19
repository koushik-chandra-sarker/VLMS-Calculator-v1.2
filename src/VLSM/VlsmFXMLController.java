package VLSM;

import com.gembox.spreadsheet.ExcelFile;
import com.gembox.spreadsheet.ExcelWorksheet;
import com.gembox.spreadsheet.SpreadsheetInfo;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.IIOException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import static java.lang.Integer.parseInt;

public class VlsmFXMLController implements Initializable {

    @FXML
    private TableColumn subnetName;

    @FXML
    private TableColumn needSize;

    @FXML
    private TableColumn allocatedSize;

    @FXML
    private TableColumn address;

    @FXML
    private TableColumn mask;

    @FXML
    private TableColumn assignableRange;

    @FXML
    private TableColumn broadcast;

    @FXML
    private Button subnetbtn;

    @FXML
    private TextField subnetFIeld;

    @FXML
    private Button hostbtn;

    @FXML
    private TextField hostfield;

    @FXML
    private Button ipbtn;

    @FXML
    private TextField maskField;

    @FXML
    private TextField ipField;

    @FXML
    private Button submitbtn;

    @FXML
    private Label hostLabel;

    @FXML
    private Label massageLabel;

    @FXML
    private TableView mytable;

    @FXML
    private MaterialDesignIconView Close;

    @FXML
    private MaterialDesignIconView minimize;

    @FXML
    private Button clearbtn;

    @FXML
    private Button resetbtn;

    @FXML
    private Button save;

    @FXML
    private Button routerbtn;

    @FXML
    private Label hostLabel1;

    @FXML
    private Label massageLabel1;

    @FXML
    private TextField totalRouterField;

    private String ip;

    private int subnetMask;

    private int needSubnet;

    private ArrayList<Integer> RequiredHost = new ArrayList();

    int index = 0;

    int a = 2;

    String initialBinary;

   // ObservableList<Person> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL event, ResourceBundle rb) {
        ipbtn.setDisable(true);
        subnetbtn.setDisable(true);
        hostbtn.setDisable(true);
        subnetFIeld.setDisable(true);
        submitbtn.setDisable(true);
        totalRouterField.setDisable(true);
        routerbtn.setDisable(true);
        hostfield.setDisable(true);

    }

    @FXML
    private void ipbuttonAction(ActionEvent event) {
        String dot = ipField.getText();
        int countDot = 0;
        for (int i = 0; i < dot.length(); i++) {
            if (dot.charAt(i) == '.') {
                countDot++;
            }
        }
        if (countDot > 3 || countDot < 3) {
            massageLabel.setText("Invalid Ip Address. Enter a valid Ip Address.");
        } else {
            String s = ipField.getText();
            String[] s1 = s.split("\\.");
            int a = Integer.parseInt(s1[0]);
            int b = Integer.parseInt(s1[1]);
            int c = Integer.parseInt(s1[2]);
            int d = Integer.parseInt(s1[3]);
            int m = Integer.parseInt(maskField.getText());
            if (s.length() > 15 || a > 255 || a < 0 || b > 255 || b < 0 || c > 255 || c < 0 || d > 255 || d < 0) {
                massageLabel.setText("Invalid Ip Address. Enter a valid Ip Address.");
            } else if (m > 32 || m < 0) {
                massageLabel.setText("Invalid Subnet Mask. Enter a valid Mask");
            } else {
                ip = ipField.getText();
                subnetMask = Integer.parseInt(maskField.getText());
                subnetFIeld.setDisable(false);
                ipField.setDisable(true);
                maskField.setDisable(true);
                ipbtn.setDisable(true);
            }
        }
        massageLabel.setText("How Many Subnets You Need To Create? ");
    }

    @FXML
    private void FieldKeyReleased(KeyEvent event) {
        boolean ipbtnDisable, subnetbtnDisable, hostBtnDisable;
        ipbtnDisable = ipField.getText().isEmpty() || maskField.getText().isEmpty();
        ipbtn.setDisable(ipbtnDisable);
        subnetbtnDisable = subnetFIeld.getText().isEmpty();
        subnetbtn.setDisable(subnetbtnDisable);
        if (subnetbtnDisable == false) {
            ipbtn.setDisable(true);
        }
        hostBtnDisable = hostfield.getText().isEmpty();
        hostbtn.setDisable(hostBtnDisable);
        if (hostBtnDisable == false) {
            subnetbtn.setDisable(true);
        }
    }

    @FXML
    private void subnetButtonAction(ActionEvent event) {
        needSubnet = Integer.parseInt(subnetFIeld.getText());
        hostfield.setDisable(false);
        subnetbtn.setDisable(true);
        subnetFIeld.setDisable(true);
        massageLabel.setText("How many Host need for Subnet number " + 1 + " ?");
    }

    @FXML
    private void hostButtonAction(ActionEvent event) {
        int n = Integer.parseInt(hostfield.getText());
        RequiredHost.add(n);
        hostfield.clear();
        hostLabel.setText("How many Host need for Subnet number " + a + " :");
        massageLabel.setText("How many Host need for Subnet number " + a + " ?");
        if ((a - 1) == needSubnet) {
            hostfield.setDisable(true);
            hostbtn.setDisable(true);
            massageLabel.setText("Click Submit Button");
            submitbtn.setDisable(false);
        }
        a++;
    }

    @FXML
    private void submitAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SubnetTable.fxml"));
        Parent root = (Parent) loader.load();
        SubnetTableController sub = loader.getController();
        sub.setValue(needSubnet,subnetMask, ip, RequiredHost);
        sub.setTable();

        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        submitbtn.setDisable(true);

    }

    private int new_mask;

    private String new_ip;

    private int AllocatedSize;

    private int NeedSize;

    private String First_valid_Host;

    private String Last_valid_Host;

    private String BroadcastAddress;







    @FXML
    private void MouseClicked(MouseEvent event) {
        // Windows Close
        if (event.getSource() == Close) {
            Stage stage = (Stage) ((MaterialDesignIconView) event.getSource()).getScene().getWindow();
            stage.close();
        }
        // Windows Minimize
        if (event.getSource() == minimize) {
            Stage stage = (Stage) ((MaterialDesignIconView) event.getSource()).getScene().getWindow();
            // stage.toBack();
            // or
            stage.setIconified(true);
        }
    }

    @FXML
    private void ClearAction(ActionEvent event) {
        ipField.setText("");
        ipField.setDisable(false);
        maskField.setText("");
        maskField.setDisable(false);
        subnetFIeld.setText("");
        subnetFIeld.setDisable(true);
        hostfield.setText("");
        hostfield.setDisable(true);
        massageLabel.setText("Clear all Content.");
        subnetMask = 0;
        needSubnet = 0;
        RequiredHost.clear();
        ip = "";
        a = 2;
        hostLabel.setText("How many Host need for Subnet number " + 1 + " :");
    }

    /*@FXML
    private void ResetAction(ActionEvent event) {
        ipField.setText("");
        ipField.setDisable(false);
        maskField.setText("");
        maskField.setDisable(false);
        subnetFIeld.setText("");
        subnetFIeld.setDisable(true);
        hostfield.setText("");
        hostfield.setDisable(true);
        massageLabel.setText("Clear all Content.");
        subnetMask = 0;
        needSubnet = 0;
        RequiredHost.clear();
        *//*AvailableSubnet.clear();
        SubnetMaskList.clear();*//*
        ip = "";
        a = 2;
        hostLabel.setText("How many Host need for Subnet number " + 1 + " :");
        for (int i = 0; i < mytable.getItems().size(); i++) {
            mytable.getItems().clear();
        }
        // for clear file
        MakeFile m1 = new MakeFile();
        m1.makeFile(AvailableSubnet, SubnetMaskList);
    }*/




}
