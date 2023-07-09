
package org.jeffersoncalderon.controller;

import com.lowagie.text.Font;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.awt.Color;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.jeffersoncalderon.bean.Paciente;
import org.jeffersoncalderon.db.Conexion;
import org.jeffersoncalderon.report.GenerarReporte;
import org.jeffersoncalderon.system.Principal;

public class PacienteController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO, GUARDAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fNacimiento, fPV;
    private UIManager UI;

    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
    @FXML private TableView tblPacientes;
    @FXML private TableColumn colCodPaciente;
    @FXML private TableColumn colNombresPaciente;
    @FXML private TableColumn colApellidosPaciente;
    @FXML private TableColumn colSexo;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colFechaPrimeraVisita;
    @FXML private GridPane grpFechas;
    @FXML private TextField txtCodPaciente;
    @FXML private TextField txtSexo;
    @FXML private TextField txtNombresPaciente;
    @FXML private TextField txtApellidosPaciente;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private TextField txtDireccionPaciente;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fNacimiento = new DatePicker(Locale.ENGLISH);
        fNacimiento.setAlignment(Pos.CENTER);
        fNacimiento.setTranslateX(5.0);
        fNacimiento.setMaxSize(195, 20);
        fNacimiento.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fNacimiento.getCalendarView().todayButtonTextProperty().set("Today");
        fNacimiento.getCalendarView().setShowWeeks(false);
        grpFechas.add(fNacimiento, 3, 1);
        fNacimiento.getStylesheets().add("/org/jeffersoncalderon/resource/DatePicker.css");
        
        fPV = new DatePicker(Locale.ENGLISH);
        fPV.setAlignment(Pos.CENTER);
        fPV.setTranslateX(5.0);
        fPV.setMaxSize(195, 20);
        fPV.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fPV.getCalendarView().todayButtonTextProperty().set("Today");
        fPV.getCalendarView().setShowWeeks(false);
        grpFechas.add(fPV, 3, 3);
        fPV.getStylesheets().add("/org/jeffersoncalderon/resource/DatePicker.css");
        desactivarControles();
        
        
    }
    
    
    // ---------------------------- ELEMENTOS TABLA ----------------------------
    
    public void cargarDatos() {
        tblPacientes.setItems(getPaciente());
        colCodPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("codigoPaciente"));
        colNombresPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, String>("nombresPaciente"));
        colApellidosPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, String>("apellidosPaciente"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente, String>("sexo"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaNacimiento"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente, String>("direccionPaciente"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<Paciente, String>("telefonoPersonal"));
        colFechaPrimeraVisita.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaPrimeraVisita"));
    }
    
    public void seleccionarElemento() {
        if (tblPacientes.getSelectionModel().getSelectedItem() != null) {
            txtCodPaciente.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
            txtNombresPaciente.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getNombresPaciente());
            txtApellidosPaciente.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getApellidosPaciente());
            txtSexo.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getSexo());
            fNacimiento.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaNacimiento());
            txtDireccionPaciente.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDireccionPaciente());
            txtTelefonoPersonal.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            fPV.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaPrimeraVisita());
        }
    }
    
    
    // ---------------------------- METODOS TABLA ----------------------------
    
    public ObservableList<Paciente> getPaciente() {
        ArrayList<Paciente> lista = new ArrayList<Paciente>(); //<Tipo_de_estructura_de_datos>
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarPacientes}");
            // cuando un sp tenga un select se usa execute query
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()) { //en cada vuelta verifica si hay mas datos
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                        resultado.getString("nombresPaciente"),
                        resultado.getString("apellidosPaciente"),
                        resultado.getString("sexo"),
                        resultado.getDate("fechaNacimiento"),
                        resultado.getString("direccionPaciente"),
                        resultado.getString("telefonoPersonal"),
                        resultado.getDate("fechaPrimeraVisita")));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaPaciente = FXCollections.observableArrayList(lista);
    }
    
    
    // ---------------------------- BOTONES ----------------------------
    
    public void nuevo() {
        switch(tipoDeOperacion) {
            case NINGUNO:
                
                limpiarControles();
                activarControles();
                
                btnAgregar.setText("Guardar");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconGuardar.png"));
                btnEliminar.setText("Cancelar");
                btnEliminar.getStyleClass().clear();
                btnEliminar.getStyleClass().add("button1");
                imgEliminar.setImage(new Image("/org/jeffersoncalderon/image/Equis.png"));
                
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                btnEditar.setOpacity(0.5);
                btnReporte.setOpacity(0.5);
                imgEditar.setOpacity(0.5);
                imgReporte.setOpacity(0.5);

                tipoDeOperacion = operaciones.GUARDAR;
                
                break;
                
            case GUARDAR:
                
                guardar();
                
                desactivarControles();
                limpiarControles();
                
                btnAgregar.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconMas.png"));
                btnEliminar.setText("Eliminar");
                btnEliminar.getStyleClass().clear();
                btnEliminar.getStyleClass().add("button2");
                imgEliminar.setImage(new Image("/org/jeffersoncalderon/image/IconMenos.png"));
                
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                btnEditar.setOpacity(1);
                btnReporte.setOpacity(1);
                imgEditar.setOpacity(1);
                imgReporte.setOpacity(1);

                tipoDeOperacion = operaciones.NINGUNO;
                
                cargarDatos();
                
                break;
        }
    }
    
    public void eliminar() {
        switch(tipoDeOperacion) {
            case GUARDAR:
                
                desactivarControles();
                limpiarControles();
                
                btnAgregar.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconMas.png"));
                btnEliminar.setText("Eliminar");
                btnEliminar.getStyleClass().clear();
                btnEliminar.getStyleClass().add("button2");
                imgEliminar.setImage(new Image("/org/jeffersoncalderon/image/IconMenos.png"));
                
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                btnEditar.setOpacity(1);
                btnReporte.setOpacity(1);
                imgEditar.setOpacity(1);
                imgReporte.setOpacity(1);
  
                tipoDeOperacion = operaciones.NINGUNO;
                break;

            default:
                if (tblPacientes.getSelectionModel().getSelectedItem() != null) {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    int respuesta = JOptionPane.showConfirmDialog(null, 
                                    new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | ¿Estás seguro de eliminar el registro?</b></p></html>", 
                                        icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25), 
                                        JLabel.CENTER), 
                                    "Eliminar Paciente", 
                                    JOptionPane.YES_NO_OPTION, 
                                    JOptionPane.PLAIN_MESSAGE);
                    
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarPaciente(?)}");
                            procedimiento.setInt(1, ((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente()); //se castea porque necesito el metodo getCodPaciente de esa clase
                            procedimiento.execute(); //no hay select por eso execute
                            listaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else
                        limpiarControles();
                    
                }else {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    JOptionPane.showMessageDialog(null, 
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debe seleccionar un elemento</b></p></html>", 
                                    icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25), 
                                    JLabel.CENTER), 
                                "Eliminar Paciente", 
                                JOptionPane.PLAIN_MESSAGE);
                }
                
                break;
        }
    }
    
    public void editar() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                if (tblPacientes.getSelectionModel().getSelectedItem() != null){
                    btnAgregar.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnAgregar.setOpacity(0.5);
                    btnEliminar.setOpacity(0.5);
                    imgNuevo.setOpacity(0.5);
                    imgEliminar.setOpacity(0.5);
                    
                    btnEditar.setText("Actualizar");
                    imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconGuardar.png"));
                    btnReporte.setText("Cancelar");
                    btnReporte.getStyleClass().clear();
                    btnReporte.getStyleClass().add("button1");
                    imgReporte.setImage(new Image("/org/jeffersoncalderon/image/Equis.png"));
                    
                    activarControles();
                    
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                }else {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    JOptionPane.showMessageDialog(null, 
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debe seleccionar un elemento</b></p></html>", 
                                    icono("/org/jeffersoncalderon/image/IconEditar.png", 25, 25),
                                    JLabel.CENTER),
                                "Editar Paciente", 
                                JOptionPane.PLAIN_MESSAGE);
                }
                
                break;
                
            case ACTUALIZAR:
                
                actualizar();
                
                desactivarControles();
                limpiarControles();
                
                btnAgregar.setDisable(false);
                btnEliminar.setDisable(false);
                btnAgregar.setOpacity(1);
                btnEliminar.setOpacity(1);
                imgNuevo.setOpacity(1);
                imgEliminar.setOpacity(1);
                
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnReporte.getStyleClass().clear();
                btnReporte.getStyleClass().add("button2");
                imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/jeffersoncalderon/image/IconReporte.png"));

                tipoDeOperacion = operaciones.NINGUNO;
                
                cargarDatos();
                
                break;
        }
    }
    
    public void reporte() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                
                UI.put("OptionPane.background", new Color(255, 255, 255));
                UI.put("Panel.background", new Color(255, 255, 255));
                imprimirReporte();
                
                break;
                
            case ACTUALIZAR:
                
                desactivarControles();
                limpiarControles();

                btnAgregar.setDisable(false);
                btnEliminar.setDisable(false);
                btnAgregar.setOpacity(1);
                btnEliminar.setOpacity(1);
                imgNuevo.setOpacity(1);
                imgEliminar.setOpacity(1);
                
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnReporte.getStyleClass().clear();
                btnReporte.getStyleClass().add("button2");
                imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/jeffersoncalderon/image/IconReporte.png"));
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                break;
        }
    }
    
    
    // ---------------------------- PROCEDIMIENTOS ----------------------------
    
    public void actualizar() {
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarPaciente(?, ?, ?, ?, ?, ?, ?, ?)}");
            Paciente registro = (Paciente)tblPacientes.getSelectionModel().getSelectedItem();
            registro.setNombresPaciente(txtNombresPaciente.getText());
            registro.setApellidosPaciente(txtApellidosPaciente.getText());
            registro.setSexo(txtSexo.getText());
            registro.setFechaNacimiento(fNacimiento.getSelectedDate());
            registro.setDireccionPaciente(txtDireccionPaciente.getText());
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setFechaPrimeraVisita(fPV.getSelectedDate());
            
            procedimiento.setInt(1, registro.getCodigoPaciente());
            procedimiento.setString(2, registro.getNombresPaciente());
            procedimiento.setString(3, registro.getApellidosPaciente());
            procedimiento.setString(4, registro.getSexo());
            procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(6, registro.getDireccionPaciente());
            procedimiento.setString(7, registro.getTelefonoPersonal());
            procedimiento.setDate(8, new java.sql.Date(registro.getFechaPrimeraVisita().getTime()));
            procedimiento.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void guardar() {
        Paciente registro = new Paciente();
        registro.setCodigoPaciente(Integer.parseInt(txtCodPaciente.getText()));
        registro.setNombresPaciente(txtNombresPaciente.getText());
        registro.setApellidosPaciente(txtApellidosPaciente.getText());
        registro.setFechaNacimiento(fNacimiento.getSelectedDate());
        registro.setDireccionPaciente(txtDireccionPaciente.getText());
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setFechaPrimeraVisita(fPV.getSelectedDate());
        registro.setSexo(txtSexo.getText());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarPaciente(?, ?, ?, ?, ?, ?, ?, ?)}");
            
            procedimiento.setInt(1, registro.getCodigoPaciente());
            procedimiento.setString(2, registro.getNombresPaciente());
            procedimiento.setString(3, registro.getApellidosPaciente());
            procedimiento.setString(4, registro.getSexo());
            procedimiento.setDate(5, new java.sql.Date(registro.getFechaNacimiento().getTime()));
            procedimiento.setString(6, registro.getDireccionPaciente());
            procedimiento.setString(7, registro.getTelefonoPersonal());
            procedimiento.setDate(8, new java.sql.Date(registro.getFechaPrimeraVisita().getTime()));

            procedimiento.execute();
            
            listaPaciente.add(registro);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void imprimirReporte() {
        Map parametros = new HashMap();
        parametros.put("codigoPaciente", null);
        parametros.put("codImagen", "org/jeffersoncalderon/image/ReportePacientes.png");
        GenerarReporte.mostrarReporte("ReportePacientes.jasper", "Reporte de Pacientes", parametros); 
    }
    
    public Icon icono(String path, int width, int heigth){
        Icon imagen = new ImageIcon(new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH));
        return imagen;
    }
    
    
    // ---------------------------- MANEJO DE CONTROLES ----------------------------
    
    public void desactivarControles() {
        txtCodPaciente.setEditable(false);
        txtNombresPaciente.setEditable(false);
        txtApellidosPaciente.setEditable(false);
        txtSexo.setEditable(false);
        txtDireccionPaciente.setEditable(false);
        txtTelefonoPersonal.setEditable(false);
        fNacimiento.setDisable(true);
        fPV.setDisable(true);
    }
    
    public void activarControles() {
        txtCodPaciente.setEditable(true);
        txtNombresPaciente.setEditable(true);
        txtApellidosPaciente.setEditable(true);
        txtSexo.setEditable(true);
        txtDireccionPaciente.setEditable(true);
        txtTelefonoPersonal.setEditable(true);
        fNacimiento.setDisable(false);
        fPV.setDisable(false);
    }
    
    public void limpiarControles() {
        txtCodPaciente.clear();
        txtNombresPaciente.clear();
        txtApellidosPaciente.clear();
        txtSexo.clear();
        txtDireccionPaciente.clear();
        txtTelefonoPersonal.clear();
        tblPacientes.getSelectionModel().clearSelection();
        fNacimiento.setSelectedDate(null);
        fPV.setSelectedDate(null);
        fNacimiento.setDisable(true);
        fPV.setDisable(true);
    }
    
    
    // ---------------------------- ESCENARIO ----------------------------
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal() {
        escenarioPrincipal.menuPrincipal();
    }
}
