
package org.jeffersoncalderon.controller;

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
import javafx.scene.control.ComboBox;
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
import org.jeffersoncalderon.bean.Doctor;
import org.jeffersoncalderon.bean.Receta;
import org.jeffersoncalderon.db.Conexion;
import org.jeffersoncalderon.report.GenerarReporte;
import org.jeffersoncalderon.system.Principal;

public class RecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, NUEVO, GUARDAR, ELIMINAR, CANCELAR, EDITAR, ACTUALIZAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Receta> listaRecetas;
    private ObservableList<Doctor> listaDoctores;
    private DatePicker fReceta;
    private UIManager UI;
    
    @FXML private TextField txtCodReceta;
    @FXML private TableView tblRecetas;
    @FXML private TableColumn colCodReceta;
    @FXML private TableColumn colFechaReceta;
    @FXML private TableColumn colNumeroColegiado;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private GridPane grpFecha;
    @FXML private ComboBox cmbNumeroColegiado;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        
        fReceta = new DatePicker(Locale.ENGLISH);
        fReceta.setAlignment(Pos.CENTER);
        fReceta.setTranslateX(5.0);
        fReceta.setMaxSize(260, 20);
        fReceta.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fReceta.getCalendarView().todayButtonTextProperty().set("Today");
        fReceta.getCalendarView().setShowWeeks(false);
        grpFecha.add(fReceta, 1, 2);
        fReceta.getStylesheets().add("/org/jeffersoncalderon/resource/DatePicker.css");
        
        cmbNumeroColegiado.setItems(getDoctor());
        cmbNumeroColegiado.setDisable(true);
        cmbNumeroColegiado.setOpacity(1);
        
        txtCodReceta.setEditable(false);
        
        desactivarControles();
    }
    
    
    // ---------------------------- ELEMENTOS TABLA ----------------------------
    
    public void cargarDatos() {
        tblRecetas.setItems(getReceta());
        colCodReceta.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoReceta"));
        colFechaReceta.setCellValueFactory(new PropertyValueFactory<Receta, Date>("fechaReceta"));
        colNumeroColegiado.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("numeroColegiado"));
    }
    
    public void seleccionarElemento() {
        if (tblRecetas.getSelectionModel().getSelectedItem() != null) {
            txtCodReceta.setText(String.valueOf(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta()));
            fReceta.selectedDateProperty().set(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getFechaReceta());
            cmbNumeroColegiado.getSelectionModel().select(buscarDoctor(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getNumeroColegiado()));
        }
    }
    
    
    // ---------------------------- METODOS TABLA ----------------------------
    
    public ObservableList<Receta> getReceta() {
        ArrayList<Receta> lista = new ArrayList<Receta>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarRecetas}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {
                lista.add(new Receta(resultado.getInt("codigoReceta"),
                                    resultado.getDate("fechaReceta"),
                                    resultado.getInt("numeroColegiado")));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaRecetas = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Doctor> getDoctor() {
        ArrayList<Doctor> lista = new ArrayList<Doctor>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarDoctores}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {
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
                
                break;
                
            default:
                if (tblRecetas.getSelectionModel().getSelectedItem() != null) {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    int respuesta = JOptionPane.showConfirmDialog(null, 
                                    new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | ¿Estás seguro de eliminar este registro?</b></p></html>",
                                        icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25),
                                        JLabel.CENTER),
                                    "Eliminar receta",
                                    JOptionPane.YES_NO_OPTION, 
                                    JOptionPane.PLAIN_MESSAGE);
                    
                    if (respuesta == JOptionPane.YES_OPTION) {
                        
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarReceta(?)}");
                            procedimiento.setInt(1, ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta());
                            procedimiento.execute();
                            
                            listaRecetas.remove(tblRecetas.getSelectionModel().getSelectedIndex());
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
                                "Eliminar receta",
                                JOptionPane.PLAIN_MESSAGE);
                }
                    
                    
                break;
        }
    }
    
    public void editar() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                
                if (tblRecetas.getSelectionModel().getSelectedItem() != null) {
                    
                    activarControles();
                    cmbNumeroColegiado.setDisable(true);
                
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnReporte.getStyleClass().clear();
                    btnReporte.getStyleClass().add("button1");
                    imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconGuardar.png"));
                    imgReporte.setImage(new Image("/org/jeffersoncalderon/image/Equis.png"));

                    btnAgregar.setDisable(true);
                    btnAgregar.setOpacity(0.5);
                    imgNuevo.setOpacity(0.5);
                    btnEliminar.setDisable(true);
                    btnEliminar.setOpacity(0.5);
                    imgEliminar.setOpacity(0.5);

                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                }else {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    JOptionPane.showMessageDialog(null, 
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debe seleccionar un elemento</b></p></html>",
                                    icono("/org/jeffersoncalderon/image/IconEditar.png", 25, 25),
                                    JLabel.CENTER),
                                "Editar receta",
                                JOptionPane.PLAIN_MESSAGE);
                }  

                break;
                
            case ACTUALIZAR:
                
                actualizar();
                
                limpiarControles();
                desactivarControles();
                
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnReporte.getStyleClass().clear();
                btnReporte.getStyleClass().add("button2");
                imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/jeffersoncalderon/image/IconReporte.png"));
                
                btnAgregar.setDisable(false);
                btnAgregar.setOpacity(1);
                imgNuevo.setOpacity(1);
                btnEliminar.setDisable(false);
                btnEliminar.setOpacity(1);
                imgEliminar.setOpacity(1);
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                cargarDatos();
                
                break;
        }
    }
    
    public void reporte() {
        switch(tipoDeOperacion) {
            case NINGUNO:
                
                imprimirReporte();
                
                break;
            
            case ACTUALIZAR:
                
                limpiarControles();
                desactivarControles();
                
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnReporte.getStyleClass().clear();
                btnReporte.getStyleClass().add("button2");
                imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/jeffersoncalderon/image/IconReporte.png"));
                
                btnAgregar.setDisable(false);
                btnAgregar.setOpacity(1);
                imgNuevo.setOpacity(1);
                btnEliminar.setDisable(false);
                btnEliminar.setOpacity(1);
                imgEliminar.setOpacity(1);
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                break;
        }
    }
    
    
    // ---------------------------- PROCEDIMIENTOS ----------------------------
    
    public void guardar() {
        Receta registro = new Receta();
        registro.setFechaReceta(fReceta.getSelectedDate());
        registro.setNumeroColegiado(((Doctor)cmbNumeroColegiado.getSelectionModel().getSelectedItem()).getNumeroColegiado());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarReceta(?, ?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaReceta().getTime()));
            procedimiento.setInt(2, registro.getNumeroColegiado());
            
            procedimiento.execute();
            listaRecetas.add(registro);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void actualizar() {
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarReceta(?, ?)}");
            
            Receta registro = (Receta) tblRecetas.getSelectionModel().getSelectedItem();
            registro.setFechaReceta(fReceta.getSelectedDate());
            
            procedimiento.setInt(1, registro.getCodigoReceta());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaReceta().getTime()));
            
            procedimiento.execute();
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void imprimirReporte() { // hacer metodo comprobante
        if (tblRecetas.getSelectionModel().getSelectedItem() != null) {
            UI.put("OptionPane.background", new Color(44,68,84));
            UI.put("Panel.background", new Color(44,68,84));

            Map parametros = new HashMap();
            
            int codReceta = ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta();

            parametros.put("imagenNombre", "org/jeffersoncalderon/image/NombreClinica.png");
            parametros.put("imagenLogo", "org/jeffersoncalderon/image/LogoClinicaPNG.png");
            parametros.put("imagenFirma", "org/jeffersoncalderon/image/firma.png");
            parametros.put("codReceta", codReceta);

            UI.put("OptionPane.background", new Color(255, 255, 255));
            UI.put("Panel.background", new Color(255, 255, 255));

            GenerarReporte.mostrarReporte("ReporteReceta.jasper", "Receta", parametros);  
            
        }else {
            UI.put("OptionPane.background", new Color(44,68,84));
            UI.put("Panel.background", new Color(44,68,84));
            
            JOptionPane.showMessageDialog(null, 
                        new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debe seleccionar un elemento</b></p></html>",
                            icono("/org/jeffersoncalderon/image/IconReporte.png", 25, 35),
                            JLabel.CENTER),
                        "Reporte receta",
                        JOptionPane.PLAIN_MESSAGE);
        }
            
    }
    
    public Icon icono(String path, int width, int heigth) {
        Icon imagen = new ImageIcon(new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH));
        return imagen;
    }
    
    
    // ---------------------------- MANEJO DE CONTROLES ----------------------------
    
    public void limpiarControles() {
        txtCodReceta.clear();
        cmbNumeroColegiado.getSelectionModel().select(null);
        fReceta.setSelectedDate(null);
        tblRecetas.getSelectionModel().clearSelection();
    }
    
    public void activarControles() {
        cmbNumeroColegiado.setDisable(false);
        fReceta.setDisable(false);
    }
    
    public void desactivarControles() {
        cmbNumeroColegiado.setDisable(true);
        fReceta.setDisable(true);
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
