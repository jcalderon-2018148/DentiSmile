
package org.jeffersoncalderon.controller;

import java.awt.Color;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
import org.jeffersoncalderon.bean.Especialidad;
import org.jeffersoncalderon.db.Conexion;
import org.jeffersoncalderon.system.Principal;

public class EspecialidadController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, ELIMINAR, EDITAR, ACTUALIZAR, CANCELAR, NINGUNO, GUARDAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    ObservableList<Especialidad> listaEspecialidad;
    private UIManager UI;
    
    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReporte;
    @FXML private TableView tblEspecialidades;
    @FXML private TableColumn colCodEspecialidad;
    @FXML private TableColumn colDescripEspecialidad;
    @FXML private TextField txtCodEspecialidad;
    @FXML private TextField txtDescripEspecialidad;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        desactivarControles();
    }
    
    
    // ---------------------------- ELEMENTOS TABLA ----------------------------

    public void cargarDatos() {
        tblEspecialidades.setItems(getEspecialidad());
        colCodEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, Integer>("codigoEspecialidad"));
        colDescripEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, String>("descripcion"));
    }
    
    public void seleccionarElemento() {
        if (tblEspecialidades.getSelectionModel().getSelectedItem() != null) {
            txtCodEspecialidad.setText(String.valueOf(((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
            txtDescripEspecialidad.setText(((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getDescripcion());
        }
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
        
        return listaEspecialidad = FXCollections.observableArrayList(lista);
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
                if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    int respuesta = JOptionPane.showConfirmDialog(null, 
                                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | ¿Estás seguro de eliminar el registro?</b></p></html>",
                                                    icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25),
                                                    JLabel.CENTER), 
                                                "Eliminar Especialidad", 
                                                JOptionPane.YES_NO_OPTION, 
                                                JOptionPane.PLAIN_MESSAGE);
                    
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarEspecialidad(?)}");
                            procedimiento.setInt(1, ((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
                            procedimiento.execute();
                            
                            listaEspecialidad.remove(tblEspecialidades.getSelectionModel().getSelectedIndex());
                            
                            limpiarControles();
                            
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else
                        limpiarControles();
                    
                } else {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    JOptionPane.showMessageDialog(null, 
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debe seleccionar un elemento</b></p></html>",
                                    icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25),
                                    JLabel.CENTER),
                                "Eliminar Especialidad",
                                JOptionPane.PLAIN_MESSAGE);
                }
                    
                break;
        }
    }
    
    public void editar() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                if (tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                    
                    activarControles();
                    
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
                    
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                }else {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    JOptionPane.showMessageDialog(null, 
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debe seleccionar un elemento</b></p></html>",
                                    icono("/org/jeffersoncalderon/image/IconEditar.png", 25, 25),
                                    JLabel.CENTER),
                                "Editar Medicamento",
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
                btnEliminar.setDisable(false);
                btnAgregar.setOpacity(1);
                btnEliminar.setOpacity(1);
                imgNuevo.setOpacity(1);
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
    
    public void actualizar() {
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarEspecialidad(?, ?)}");
            Especialidad registro = (Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem();
            registro.setDescripcion(txtDescripEspecialidad.getText());
            
            procedimiento.setInt(1, registro.getCodigoEspecialidad());
            procedimiento.setString(2, registro.getDescripcion());
            procedimiento.execute();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public void guardar() {
        Especialidad registro = new Especialidad();
        registro.setDescripcion(txtDescripEspecialidad.getText());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarEspecialidad(?)}");
            procedimiento.setString(1, registro.getDescripcion());
            procedimiento.execute();
            listaEspecialidad.add(registro);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Icon icono(String path, int width, int heigth) {
        Icon imagen = new ImageIcon(new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH));
        return imagen;
    }
    
    
    // ---------------------------- MANEJO DE CONTROLES ----------------------------
    
    public void activarControles() {
        txtDescripEspecialidad.setEditable(true);
    }
    
    public void desactivarControles() {
        txtCodEspecialidad.setEditable(false);
        txtDescripEspecialidad.setEditable(false);
    }
    
    public void limpiarControles() {
        txtCodEspecialidad.clear();
        txtDescripEspecialidad.clear();
        tblEspecialidades.getSelectionModel().clearSelection();
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
