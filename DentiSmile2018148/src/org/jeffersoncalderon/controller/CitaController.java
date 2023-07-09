
package org.jeffersoncalderon.controller;

import com.jfoenix.controls.JFXTimePicker;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.awt.Color;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.jeffersoncalderon.bean.Cita;
import org.jeffersoncalderon.bean.Doctor;
import org.jeffersoncalderon.bean.Paciente;
import org.jeffersoncalderon.db.Conexion;
import org.jeffersoncalderon.system.Principal;

public class CitaController implements Initializable {
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, NUEVO, GUARDAR, ELIMINAR, CANCELAR, EDITAR, ACTUALIZAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Cita> listaCitas;
    private ObservableList<Paciente> listaPacientes;
    private ObservableList<Doctor> listaDoctores;
    private DatePicker fCita;
    private UIManager UI;
    
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private TextField txtCodCita;
    @FXML private TextField txtTratamiento;
    @FXML private TextField txtDescripCondAct;
    @FXML private GridPane grpFechas;
    @FXML private ComboBox cmbNumColegiado;
    @FXML private ComboBox cmbCodPaciente;
    @FXML private TableView tblCitas;
    @FXML private TableColumn colCodCita;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colHoraCita;
    @FXML private TableColumn colTratamiento;
    @FXML private TableColumn colDescripCondAct;
    @FXML private TableColumn colCodPaciente;
    @FXML private TableColumn colNumColegiado;
    @FXML private JFXTimePicker jfxHora;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        
        fCita = new DatePicker(Locale.ENGLISH);
        fCita.setAlignment(Pos.CENTER);
        fCita.setTranslateX(5.0);
        fCita.setMaxSize(195, 20);
        fCita.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fCita.getCalendarView().todayButtonTextProperty().set("Today");
        fCita.getCalendarView().setShowWeeks(false);
        grpFechas.add(fCita, 3, 0);
        fCita.getStylesheets().add("/org/jeffersoncalderon/resource/DatePicker.css");

        cmbCodPaciente.setItems(getPaciente());
        cmbCodPaciente.setOpacity(1);
        cmbNumColegiado.setItems(getDoctor());
        cmbNumColegiado.setOpacity(1);
        
        jfxHora.setOpacity(1);
        
        desactivarControles();
    }
    
    
    // ---------------------------- ELEMENTOS TABLA ----------------------------
    
    public void cargarDatos() {
        tblCitas.setItems(getCita());
        colCodCita.setCellValueFactory(new PropertyValueFactory<Cita, Integer>("codigoCita"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Cita, Date>("fechaCita"));
        colHoraCita.setCellValueFactory(new PropertyValueFactory<Cita, Date>("horaCita"));
        colTratamiento.setCellValueFactory(new PropertyValueFactory<Cita, String>("tratamiento"));
        colDescripCondAct.setCellValueFactory(new PropertyValueFactory<Cita, String>("descripCondActual"));
        colCodPaciente.setCellValueFactory(new PropertyValueFactory<Cita, Integer>("codigoPaciente"));
        colNumColegiado.setCellValueFactory(new PropertyValueFactory<Cita, Integer>("numeroColegiado"));
    }
    
    public void seleccionarElemento() {
        if (tblCitas.getSelectionModel().getSelectedItem() != null) {
            txtCodCita.setText(String.valueOf(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoCita()));
            txtTratamiento.setText(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getTratamiento());
            txtDescripCondAct.setText(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getDescripCondActual());
            fCita.selectedDateProperty().set(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getFechaCita());
            cmbCodPaciente.getSelectionModel().select(buscarPaciente(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
            cmbNumColegiado.getSelectionModel().select(buscarDoctor(((Cita)tblCitas.getSelectionModel().getSelectedItem()).getNumeroColegiado()));
            jfxHora.setValue((((Cita)tblCitas.getSelectionModel().getSelectedItem()).getHoraCita()).toLocalTime());
        }
    }
    
    
    // ---------------------------- METODOS TABLA ----------------------------
    
    public ObservableList<Cita> getCita() {
        ArrayList<Cita> lista = new ArrayList<Cita>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarCitas()}");
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                lista.add(new Cita(registro.getInt("codigoCita"),
                                    registro.getDate("fechaCita"),
                                    registro.getTime("horaCita"),
                                    registro.getString("tratamiento"),
                                    registro.getString("descripCondActual"),
                                    registro.getInt("codigoPaciente"),
                                    registro.getInt("numeroColegiado")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaCitas = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Paciente> getPaciente() {
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarPacientes()}");
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                lista.add(new Paciente(registro.getInt("codigoPaciente"),
                                        registro.getString("nombresPaciente"),
                                        registro.getString("apellidosPaciente"),
                                        registro.getString("sexo"),
                                        registro.getDate("fechaNacimiento"),
                                        registro.getString("direccionPaciente"),
                                        registro.getString("telefonoPersonal"),
                                        registro.getDate("fechaPrimeraVisita")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaPacientes = FXCollections.observableArrayList(lista);
    }
    
    public Paciente buscarPaciente(int codigoPaciente) {
        Paciente resultado = null;
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                resultado = new Paciente(registro.getInt("codigoPaciente"),
                                        registro.getString("nombresPaciente"),
                                        registro.getString("apellidosPaciente"),
                                        registro.getString("sexo"),
                                        registro.getDate("fechaNacimiento"),
                                        registro.getString("direccionPaciente"),
                                        registro.getString("telefonoPersonal"),
                                        registro.getDate("fechaPrimeraVisita"));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public ObservableList<Doctor> getDoctor() {
        ArrayList<Doctor> lista = new ArrayList<Doctor>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarDoctores()}");
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                lista.add(new Doctor(registro.getInt("numeroColegiado"),
                                    registro.getString("nombresDoctor"),
                                    registro.getString("apellidosDoctor"),
                                    registro.getString("telefonoContacto"),
                                    registro.getInt("codigoEspecialidad")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaDoctores = FXCollections.observableArrayList(lista);
    }
    
    public Doctor buscarDoctor(int numeroColegiado) {
        Doctor resultado = null;
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarDoctor(?)}");
            procedimiento.setInt(1, numeroColegiado);
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                resultado = new Doctor(registro.getInt("numeroColegiado"),
                                        registro.getString("nombresDoctor"),
                                        registro.getString("apellidosDoctor"),
                                        registro.getString("telefonoContacto"),
                                        registro.getInt("codigoEspecialidad"));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    
    // ---------------------------- BOTONES ----------------------------
    
    public void nuevo() {
        switch (tipoDeOperacion) {
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
                btnEditar.setOpacity(0.5);
                imgEditar.setOpacity(0.5);
                btnReporte.setDisable(true);
                btnReporte.setOpacity(0.5);
                imgReporte.setOpacity(0.5);
                
                tipoDeOperacion = operaciones.GUARDAR;
                
                break;
                 
            case GUARDAR:
                
                guardar();
                
                limpiarControles();
                desactivarControles();
                
                btnAgregar.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconMas.png"));
                btnEliminar.setText("Eliminar");
                btnEliminar.getStyleClass().clear();
                btnEliminar.getStyleClass().add("button2");
                imgEliminar.setImage(new Image("/org/jeffersoncalderon/image/IconMenos.png"));
                
                btnEditar.setDisable(false);
                btnEditar.setOpacity(1);
                imgEditar.setOpacity(1);
                btnReporte.setDisable(false);
                btnReporte.setOpacity(1);
                imgReporte.setOpacity(1);
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                cargarDatos();
                
                break;
        }
    }
    
    public void eliminar() {
        switch (tipoDeOperacion) {
            case GUARDAR:
                
                limpiarControles();
                desactivarControles();
                
                btnAgregar.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconMas.png"));
                btnEliminar.setText("Eliminar");
                btnEliminar.getStyleClass().clear();
                btnEliminar.getStyleClass().add("button2");
                imgEliminar.setImage(new Image("/org/jeffersoncalderon/image/IconMenos.png"));
                
                btnEditar.setDisable(false);
                btnEditar.setOpacity(1);
                imgEditar.setOpacity(1);
                btnReporte.setDisable(false);
                btnReporte.setOpacity(1);
                imgReporte.setOpacity(1);
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                cargarDatos();
                
                break;
                
            default:
                if (tblCitas.getSelectionModel().getSelectedItem() != null) {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    int respuesta = JOptionPane.showConfirmDialog(null, 
                                    new JLabel("<html><p style = \"color: white; font: 11px; \"><b> | ¿Estás seguro de eliminar el registro</b></p></html>", 
                                            icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25), 
                                            JLabel.CENTER), 
                                        "Eliminar cita", 
                                        JOptionPane.YES_NO_OPTION, 
                                        JOptionPane.PLAIN_MESSAGE);
                    
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarCita(?)}");
                            procedimiento.setInt(1, ((Cita)tblCitas.getSelectionModel().getSelectedItem()).getCodigoCita());
                            procedimiento.execute();
                            
                            listaCitas.remove(tblCitas.getSelectionModel().getSelectedIndex());
                            
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
                        "Eliminar Cita",
                        JOptionPane.PLAIN_MESSAGE);
                }

                break;
        }
    }
    
    public void editar() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                if (tblCitas.getSelectionModel().getSelectedItem() != null) {
                    
                    activarControles();
                    cmbNumColegiado.setDisable(true);
                    cmbCodPaciente.setDisable(true);
                    
                    btnAgregar.setDisable(true);
                    btnAgregar.setOpacity(0.5);
                    imgNuevo.setOpacity(0.5);
                    btnEliminar.setDisable(true);
                    btnEliminar.setOpacity(0.5);
                    imgEliminar.setOpacity(0.5);
                    
                    btnEditar.setText("Actualizar");
                    imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconGuardar.png"));
                    btnReporte.setText("Cancelar");
                    btnReporte.getStyleClass().clear();
                    btnReporte.getStyleClass().add("button1");
                    imgReporte.setImage(new Image("/org/jeffersoncalderon/image/Equis.png"));
                    
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                }else {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    JOptionPane.showMessageDialog(null, 
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b>Debe seleccionar un elemento</b></p></html>",
                                    icono("/org/jeffersoncalderon/image/IconEditar.png", 25, 25),
                                    JLabel.CENTER),
                                "Editar cita",
                                JOptionPane.PLAIN_MESSAGE);
                }    
                
                break;
                
            case ACTUALIZAR:
                
                actualizar();
                
                desactivarControles();
                limpiarControles();
                
                btnAgregar.setDisable(false);
                btnAgregar.setOpacity(1);
                imgNuevo.setOpacity(1);
                btnEliminar.setDisable(false);
                btnEliminar.setOpacity(1);
                imgEliminar.setOpacity(1);
                
                btnEditar.setText("Editar");
                imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconEditar.png"));
                btnReporte.setText("Reporte");
                btnReporte.getStyleClass().clear();
                btnReporte.getStyleClass().add("button2");
                imgReporte.setImage(new Image("/org/jeffersoncalderon/image/IconReporte.png"));
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                cargarDatos();

                break;
        }
    }
    
    public void reporte() {
        switch (tipoDeOperacion) {
            case ACTUALIZAR:
                
                desactivarControles();
                limpiarControles();
                
                btnAgregar.setDisable(false);
                btnAgregar.setOpacity(1);
                imgNuevo.setOpacity(1);
                btnEliminar.setDisable(false);
                btnEliminar.setOpacity(1);
                imgEliminar.setOpacity(1);
                
                btnEditar.setText("Editar");
                imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconEditar.png"));
                btnReporte.setText("Reporte");
                btnReporte.getStyleClass().clear();
                btnReporte.getStyleClass().add("button2");
                imgReporte.setImage(new Image("/org/jeffersoncalderon/image/IconReporte.png"));
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                break;
        }
    }
    
    
    // ---------------------------- PROCEDIMIENTOS ----------------------------
    
    public void guardar() {
        Cita registro = new Cita();
        registro.setFechaCita(fCita.getSelectedDate());
        registro.setHoraCita(java.sql.Time.valueOf(jfxHora.getValue()));
        registro.setTratamiento(txtTratamiento.getText());
        registro.setDescripCondActual(txtDescripCondAct.getText());
        registro.setCodigoPaciente(((Paciente)cmbCodPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        registro.setNumeroColegiado(((Doctor)cmbNumColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarCita(?, ?, ?, ?, ?, ?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setTime(2, registro.getHoraCita());
            procedimiento.setString(3, registro.getTratamiento());
            procedimiento.setString(4, registro.getDescripCondActual());
            procedimiento.setInt(5, registro.getCodigoPaciente());
            procedimiento.setInt(6, registro.getNumeroColegiado());
            
            procedimiento.execute();
            listaCitas.add(registro);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void actualizar() {
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarCita(?, ?, ?, ?, ?)}");
            Cita registro = (Cita)tblCitas.getSelectionModel().getSelectedItem();
            registro.setFechaCita(fCita.getSelectedDate());
            registro.setHoraCita(java.sql.Time.valueOf(jfxHora.getValue()));
            registro.setTratamiento(txtTratamiento.getText());
            registro.setDescripCondActual(txtDescripCondAct.getText());
            
            procedimiento.setInt(1, registro.getCodigoCita());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setTime(3, registro.getHoraCita());
            procedimiento.setString(4, registro.getTratamiento());
            procedimiento.setString(5, registro.getDescripCondActual());
            
            procedimiento.execute();
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Icon icono(String path, int width, int heigth) {
        Icon imagen = new ImageIcon(new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH));
        return imagen;
    }
    
    // ---------------------------- MANEJO DE CONTROLES ----------------------------
    
    public void limpiarControles() {
        txtCodCita.clear();
        txtTratamiento.clear();
        txtDescripCondAct.clear();
        fCita.setSelectedDate(null);
        cmbCodPaciente.getSelectionModel().select(null);
        cmbNumColegiado.getSelectionModel().select(null);
        tblCitas.getSelectionModel().clearSelection();
        jfxHora.setValue(null);
    }
    
    public void desactivarControles() {
        txtCodCita.setEditable(false);
        txtTratamiento.setEditable(false);
        txtDescripCondAct.setEditable(false);
        fCita.setDisable(true);
        cmbCodPaciente.setDisable(true);
        cmbNumColegiado.setDisable(true);
        jfxHora.setDisable(true);
    }
    
    public void activarControles() {
        txtTratamiento.setEditable(true);
        txtDescripCondAct.setEditable(true);
        fCita.setDisable(false);
        cmbCodPaciente.setDisable(false);
        cmbNumColegiado.setDisable(false);
        jfxHora.setDisable(false);
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
