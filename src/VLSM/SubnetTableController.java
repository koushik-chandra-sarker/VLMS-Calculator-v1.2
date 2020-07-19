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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class SubnetTableController extends VlsmFXMLController implements Initializable {

    @FXML
    private TableView mytable;

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
    private Button save;

    @FXML
    private Button resetbtn;

    @FXML
    private MaterialDesignIconView minimize1;

    @FXML
    private MaterialDesignIconView Close1;

    private String ip1;

    private int needSubnet;

    private int subnetMask;

    private ArrayList<Integer> RequiredHost = new ArrayList();

    private int new_mask;

    private String new_ip;

    private int AllocatedSize;

    private int NeedSize;

    private String First_valid_Host;

    private String Last_valid_Host;

    private String BroadcastAddress;

    public ArrayList<String> AvailableSubnet = new ArrayList<String>();

    public ArrayList<Integer> SubnetMaskList = new ArrayList<Integer>();

    ObservableList<Person> list = FXCollections.observableArrayList();

    public void setValue(int needSubnet, int subnetMask, String ip, ArrayList<Integer> RequiredHost) {
        this.needSubnet = needSubnet;
        this.subnetMask = subnetMask;
        this.ip1 = ip;
        this.RequiredHost.addAll(RequiredHost);

    }

    @Override
    public void initialize(URL event, ResourceBundle rb) {
    }

    public void setTable() {
        subnetName.setCellValueFactory(new PropertyValueFactory<Person, String>("subnetName"));
        needSize.setCellValueFactory(new PropertyValueFactory<Person, Integer>("needSize"));
        allocatedSize.setCellValueFactory(new PropertyValueFactory<Person, Integer>("allocatedSize"));
        address.setCellValueFactory(new PropertyValueFactory<Person, String>("address"));
        mask.setCellValueFactory(new PropertyValueFactory<Person, String>("mask"));
        assignableRange.setCellValueFactory(new PropertyValueFactory<Person, String>("assignableRange"));
        broadcast.setCellValueFactory(new PropertyValueFactory<Person, String>("broadcast"));
        mytable.setItems(list);


        Collections.sort(RequiredHost, Collections.reverseOrder());

        char c = 'A';
        for (int i = 1; i <= needSubnet; i++) {
            int host = RequiredHost.get(0);
            SubnetCalculator(subnetMask, ip1, host);
            String s1 = Character.toString(c);
            Person p = new Person(s1, NeedSize, AllocatedSize, new_ip, "/" + new_mask, First_valid_Host + " to " + Last_valid_Host, BroadcastAddress + "/" + new_mask);
            list.add(p);
            RequiredHost.remove(0);
            c++;
            // massageLabel.setText("Subneting Successful.");
            boolean t = true;
            int x = 1;
            while (t != false) {
                subnetMask = this.SubnetMaskList.get(this.SubnetMaskList.size() - x);
                int y = (int) Math.pow(2, (32 - subnetMask));
                if (y < host) {
                    x++;
                    t = true;
                } else {
                    t = false;
                }
            }
            String Ip_binary = this.AvailableSubnet.get(this.AvailableSubnet.size() - x);
            Conversion con = new Conversion();
            con.binaryToIpConversion(Ip_binary);
            ip1 = con.getOutIP();
            if (i < needSubnet) {
                this.SubnetMaskList.remove(this.SubnetMaskList.size() - x);
                this.AvailableSubnet.remove(this.AvailableSubnet.size() - x);
            }
        }
        MakeFile m = new MakeFile();
        m.makeFile(AvailableSubnet, SubnetMaskList);
    }

    public void SubnetCalculator(int SubnetMask, String ip_in, int host) {
        String ip_In = ip_in;
        this.NeedSize = host;
        int subnetMask = SubnetMask;
        Conversion c = new Conversion();
        c.IpToBinaryConversion(ip_in);
        String initialBinary = c.getOutBinary();
        binaryGenerators b = new binaryGenerators(subnetMask, initialBinary, NeedSize);
        b.Generator();
        // Set Subnet to Database
        this.AvailableSubnet.addAll(b.subnetlist);
        // get subnet for required host
        String A = this.AvailableSubnet.get(this.AvailableSubnet.size() - 1);
        c.binaryToIpConversion(A);
        this.new_ip = c.getOutIP();
        this.AvailableSubnet.remove(this.AvailableSubnet.size() - 1);
        // Set Subnet mask to database
        this.SubnetMaskList.addAll(b.subnetMasklist);
        this.new_mask = this.SubnetMaskList.get(this.SubnetMaskList.size() - 1);
        this.SubnetMaskList.remove(this.SubnetMaskList.size() - 1);
        this.AllocatedSize = b.allocatedSize;
        HostCalculator h = new HostCalculator(new_ip, new_mask);
        this.First_valid_Host = h.firstAssignableHost;
        this.Last_valid_Host = h.lastAssignableHost;
        this.BroadcastAddress = h.BroadCast;
    }

    @FXML
    private void MouseClicked(MouseEvent event) {
        // Windows Close
        if (event.getSource() == Close1) {
            Stage stage = (Stage) ((MaterialDesignIconView) event.getSource()).getScene().getWindow();
            stage.close();
        }
        // Windows Minimize
        if (event.getSource() == minimize1) {
            Stage stage = (Stage) ((MaterialDesignIconView) event.getSource()).getScene().getWindow();
            // stage.toBack();
            // or
            stage.setIconified(true);
        }
    }

    @FXML
    private void OtherSubnetViewAction(ActionEvent event) throws IOException {
        Parent OtherSubnetView = FXMLLoader.load(getClass().getResource("OtherSubnetViewFXML.fxml"));
        Scene scene3 = new Scene(OtherSubnetView);
        Stage stage3 = new Stage();
        stage3.setScene(scene3);
        stage3.initStyle(StageStyle.TRANSPARENT);
        stage3.show();
        // stage2.centerOnScreen();
        OtherSubnetViewFXMLController c = new OtherSubnetViewFXMLController();
    }

    @FXML
    public void saveAction(ActionEvent event) throws IOException {
        SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
        subnetName.setCellValueFactory(new PropertyValueFactory<Person, String>("subnetName"));
        needSize.setCellValueFactory(new PropertyValueFactory<Person, Integer>("needSize"));
        allocatedSize.setCellValueFactory(new PropertyValueFactory<Person, Integer>("allocatedSize"));
        address.setCellValueFactory(new PropertyValueFactory<Person, String>("address"));
        mask.setCellValueFactory(new PropertyValueFactory<Person, String>("mask"));
        assignableRange.setCellValueFactory(new PropertyValueFactory<Person, String>("assignableRange"));
        broadcast.setCellValueFactory(new PropertyValueFactory<Person, String>("broadcast"));
        ExcelFile file = new ExcelFile();
        ExcelWorksheet worksheet = file.addWorksheet("sheet");
        for (int row = 0; row < mytable.getItems().size(); row++) {
            ObservableList cells = (ObservableList) mytable.getItems().get(row);
            for (int column = 0; column < cells.size(); column++) {
                if (cells.get(column) != null) {
                    worksheet.getCell(row, column).setValue(cells.get(column).toString());
                }
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("XLSX files(*.xlsx", "*.xlsx"), new FileChooser.ExtensionFilter("XLS files(*.xls", "*.xls"), new FileChooser.ExtensionFilter("ODS files(*.ods", "*.ods"), new FileChooser.ExtensionFilter("CSV files(*.csv", "*.csv"), new FileChooser.ExtensionFilter("HTML files(*.html", "*.html"));
        File saveFile = fileChooser.showSaveDialog(mytable.getScene().getWindow());
        file.save(saveFile.getAbsolutePath());
    }

    @FXML
    private void resetAction(ActionEvent event) {

        subnetMask = 0;
        needSubnet = 0;
        RequiredHost.clear();
        AvailableSubnet.clear();
        SubnetMaskList.clear();

        for (int i = 0; i < mytable.getItems().size(); i++) {
            mytable.getItems().clear();
        }
        // for clear file
        MakeFile m1 = new MakeFile();
        m1.makeFile(AvailableSubnet, SubnetMaskList);
    }
}
