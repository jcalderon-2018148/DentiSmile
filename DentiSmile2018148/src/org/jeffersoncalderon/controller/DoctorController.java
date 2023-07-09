
package org.jeffersoncalderon.controller;

import java.awt.Color;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.jeffersoncalderon.bean.Doctor;
import org.jeffersoncalderon.bean.Especialidad;
import org.jeffersoncalderon.db.Conexion;
import org.jeffersoncalderon.report.GenerarReporte;
import org.jeffersoncalderon.system.Principal;

public class DoctorController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, GUARDAR, ELIMINAR, EDITAR, ACTUALIZAR,CANCELAR, NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Doctor> listaDoctores;
    private ObservableList<Especialidad> listaEspecialidades;
    private UIManager UI;

    @FXML private TextField txtNumeroColegiado;
    @FXML private TextField txtNombresDoctor;
    @FXML private TextField txtApellidosDoctor;
    @FXML private TextField txtTelefonoContacto;
    @FXML private ComboBox cmbCodigoEspecialidad;
    @FXML private TableView tblDoctores;
    @FXML private TableColumn colNumeroColegiado;
    @FXML private TableColumn colNombresDoctor;
    @FXML private TableColumn colApellidosDoctor;
    @FXML private TableColumn colTelefonoContacto;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();

        cmbCodigoEspecialidad.setItems(getEspecialidad());
        cmbCodigoEspecialidad.setDisable(true);
        cmbCodigoEspecialidad.setOpacity(1);
    }
    
    
    // ---------------------------- ELEMENTOS TABLA ----------------------------
    
    public void cargarDatos() {
        tblDoctores.setItems(getDoctor());
        colNumeroColegiado.setCellValueFactory(new PropertyValueFactory<Doctor, Integer>("numeroColegiado"));
        colNombresDoctor.setCellValueFactory(new PropertyValueFactory<Doctor, String>("nombresDoctor"));
        colApellidosDoctor.setCellValueFactory(new PropertyValueFactory<Doctor, String>("apellidosDoctor"));
        colTelefonoContacto.setCellValueFactory(new PropertyValueFactory<Doctor, String>("telefonoContacto"));
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<Doctor, Integer>("codigoEspecialidad"));
    }

    public void seleccionarElemento() {
        if (tblDoctores.getSelectionModel().getSelectedItem() != null) {
            txtNumeroColegiado.setText(String.valueOf(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getNumeroColegiado()));
            txtNombresDoctor.setText(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getNombresDoctor());
            txtApellidosDoctor.setText(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getApellidosDoctor());
            txtTelefonoContacto.setText(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getTelefonoContacto());
            cmbCodigoEspecialidad.getSelectionModel().select(buscarEspecialidad(((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
        }
    }
    
    
    // ---------------------------- METODOS TABLA ----------------------------
    
    public ObservableList<Doctor> getDoctor() {
        ArrayList<Doctor> lista = new ArrayList<Doctor>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarDoctores}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()) {
                lista.add(new Doctor(resultado.getInt("numeroColegiado"),
                                        resultado.getString("nombresDoctor"),
                                        resultado.getString("apellidosDoctor"),
                                        resultado.getString("telefonoContacto"),
                                        resultado.getInt("codigoEspecialidad")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        return listaDoctores = FXCollections.observableArrayList(lista);
    }
    
    public Especialidad buscarEspecialidad(int codigoEspecialidad) {
        Especialidad resultado = null;
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarEspecialidad(?)}");
            procedimiento.setInt(1, codigoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                resultado = new Especialidad(registro.getInt("codigoEspecialidad"), registro.getString("descripcion"));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public ObservableList<Especialidad> getEspecialidad() {
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarEspecialidades}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while(resultado.next()) {
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),
                                            resultado.getString("descripcion")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaEspecialidades = FXCollections.observableArrayList(lista);
    }
    
    
    // ---------------------------- BOTONES ----------------------------
    
    public void nuevo() {
        switch(tipoDeOperacion) {
            case NINGUNO:
                
                activarControles();
                limpiarControles();
                
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
                
                desactivarControles();
                limpiarControles();
                
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
                
                cargarDatos();
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                break;
        }
    }
    
    public void eliminar() {
        switch (tipoDeOperacion) {
            case GUARDAR:
                
                desactivarControles();
                limpiarControles();
                
                btnAgregar.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconMas.png"));
                btnEliminar.setText("Eliminar");
                imgEliminar.setImage(new Image("/org/jeffersoncalderon/image/IconMenos.png"));
                btnEliminar.getStyleClass().clear();
                btnEliminar.getStyleClass().add("button2");
                
                btnEditar.setDisable(false);
                btnEditar.setOpacity(1);
                imgEditar.setOpacity(1);
                btnReporte.setDisable(false);
                btnReporte.setOpacity(1);
                imgReporte.setOpacity(1);
                
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                break;
                
            default:
                if (tblDoctores.getSelectionModel().getSelectedItem() != null) {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    int respuesta = JOptionPane.showConfirmDialog(null, 
                                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | ¿Estás seguro de eliminar el registro?</b></p></html>",
                                                    icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25),
                                                    JLabel.CENTER), 
                                                "Eliminar doctor", 
                                                JOptionPane.YES_NO_OPTION, 
                                                JOptionPane.PLAIN_MESSAGE);
                    
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarDoctor(?)}");
                            procedimiento.setInt(1, ((Doctor)tblDoctores.getSelectionModel().getSelectedItem()).getNumeroColegiado());
                            procedimiento.execute();
                            listaDoctores.remove(tblDoctores.getSelectionModel().getSelectedIndex());
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
                                "Eliminar Doctor",
                                JOptionPane.PLAIN_MESSAGE);
                }
                    
            break;
        }
    }
    
    public void editar() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                if (tblDoctores.getSelectionModel().getSelectedItem() != null) {
                    
                    activarControles();
                    
                    btnAgregar.setDisable(true);
                    btnAgregar.setOpacity(0.5);
                    imgNuevo.setOpacity(0.5);
                    btnEliminar.setDisable(true);
                    btnEliminar.setOpacity(0.5);
                    imgEliminar.setOpacity(0.5);
                    
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnReporte.getStyleClass().clear();
                    btnReporte.getStyleClass().add("button1");
                    imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconGuardar.png"));
                    imgReporte.setImage(new Image("/org/jeffersoncalderon/image/Equis.png"));
                    
                    txtNumeroColegiado.setEditable(false);
                    cmbCodigoEspecialidad.setDisable(true);
                    cmbCodigoEspecialidad.setOpacity(1);
                    
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                }else {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    JOptionPane.showMessageDialog(null, 
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debe seleccionar un elemento</b></p></html>",
                                    icono("/org/jeffersoncalderon/image/IconEditar.png", 25, 25),
                                    JLabel.CENTER),
                                "Editar Doctor",
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
                btnAgregar.setOpacity(1);
                imgNuevo.setOpacity(1);
                btnEliminar.setDisable(false);
                btnEliminar.setOpacity(1);
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
    
    public void generarReporte() {
        switch(tipoDeOperacion) {
            case NINGUNO:
                
                imprimirReporte();
                
                break;
        }
    }
    
    
    // ---------------------------- PROCEDIMIENTOS ----------------------------
    
    public void guardar() {
        Doctor registro = new Doctor();
        registro.setNumeroColegiado(Integer.parseInt(txtNumeroColegiado.getText()));
        registro.setNombresDoctor(txtNombresDoctor.getText());
        registro.setApellidosDoctor(txtApellidosDoctor.getText());
        registro.setTelefonoContacto(txtTelefonoContacto.getText());
        registro.setCodigoEspecialidad(((Especialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarDoctor(?, ?, ?, ?, ?)}");
            procedimiento.setInt(1, registro.getNumeroColegiado());
            procedimiento.setString(2, registro.getNombresDoctor());
            procedimiento.setString(3, registro.getApellidosDoctor());
            procedimiento.setString(4, registro.getTelefonoContacto());
            procedimiento.setInt(5, registro.getCodigoEspecialidad());
            
            procedimiento.execute();
            
            listaDoctores.add(registro);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void actualizar() {
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarDoctor(?, ?, ?, ?)}");
            Doctor registro = (Doctor) tblDoctores.getSelectionModel().getSelectedItem();
            registro.setNombresDoctor(txtNombresDoctor.getText());
            registro.setApellidosDoctor(txtApellidosDoctor.getText());
            registro.setTelefonoContacto(txtTelefonoContacto.getText());
            
            procedimiento.setInt(1, registro.getNumeroColegiado());
            procedimiento.setString(2, registro.getNombresDoctor());
            procedimiento.setString(3, registro.getApellidosDoctor());
            procedimiento.setString(4, registro.getTelefonoContacto());
            procedimiento.execute();
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void imprimirReporte() {
        Map parametros = new HashMap();
        parametros.put("codigoDoctor", null);
        parametros.put("codImagen", "org/jeffersoncalderon/image/ReporteDoctores.png");
        GenerarReporte.mostrarReporte("ReporteDoctores.jasper", "Reporte de Doctores", parametros);
    }
    
    public Icon icono(String path, int width, int heigth) {
        Icon imagen = new ImageIcon(new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH));
        return imagen;
    }
    

    // ---------------------------- MANEJO DE CONTROLES ----------------------------
    
    public void desactivarControles() {
        txtNumeroColegiado.setEditable(false);
        txtNombresDoctor.setEditable(false);
        txtApellidosDoctor.setEditable(false);
        txtTelefonoContacto.setEditable(false);
        cmbCodigoEspecialidad.setDisable(true);
    }
    
    public void activarControles() {
        txtNumeroColegiado.setEditable(true);
        txtNombresDoctor.setEditable(true);
        txtApellidosDoctor.setEditable(true);
        txtTelefonoContacto.setEditable(true);
        cmbCodigoEspecialidad.setDisable(false);
    }
    
    public void limpiarControles() {
        txtNumeroColegiado.clear();
        txtNombresDoctor.clear();
        txtApellidosDoctor.clear();
        txtTelefonoContacto.clear();
        tblDoctores.getSelectionModel().clearSelection();
        cmbCodigoEspecialidad.getSelectionModel().select(null);
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
