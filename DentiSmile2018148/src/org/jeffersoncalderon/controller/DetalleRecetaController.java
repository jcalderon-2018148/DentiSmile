
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
import org.jeffersoncalderon.bean.DetalleReceta;
import org.jeffersoncalderon.bean.Medicamento;
import org.jeffersoncalderon.bean.Receta;
import org.jeffersoncalderon.db.Conexion;
import org.jeffersoncalderon.system.Principal;

public class DetalleRecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, NUEVO, GUARDAR, ACTUALIZAR, ELIMINAR, CANCELAR, EDITAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<DetalleReceta> listaDetallesReceta;
    private ObservableList<Receta> listaRecetas;
    private ObservableList<Medicamento> listaMedicamentos;
    private UIManager UI;
    
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private TableView tblDetallesReceta;
    @FXML private TableColumn colCodDetalleReceta;
    @FXML private TableColumn colDosis;
    @FXML private TableColumn colCodMedicamento;
    @FXML private TableColumn colCodReceta;
    @FXML private TextField txtCodDetalleReceta;
    @FXML private TextField txtDosis;
    @FXML private ComboBox cmbCodReceta;
    @FXML private ComboBox cmbCodMedicamento;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        desactivarControles();
        
        cmbCodReceta.setItems(getReceta());
        cmbCodReceta.setOpacity(1);
        cmbCodMedicamento.setItems(getMedicamento());
        cmbCodMedicamento.setOpacity(1);
    }
    
    
    // ---------------------------- ELEMENTOS TABLA ----------------------------
    
    public void cargarDatos() {
        tblDetallesReceta.setItems(getDetalleReceta());
        colCodDetalleReceta.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("codigoDetalleReceta"));
        colDosis.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("dosis"));
        colCodMedicamento.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("codigoMedicamento"));
        colCodReceta.setCellValueFactory(new PropertyValueFactory<DetalleReceta, Integer>("codigoReceta"));
    }
    
    public void seleccionarElemento() {
        if (tblDetallesReceta.getSelectionModel().getSelectedItem() != null) {
            txtCodDetalleReceta.setText(String.valueOf(((DetalleReceta)tblDetallesReceta.getSelectionModel().getSelectedItem()).getCodigoDetalleReceta()));
            txtDosis.setText(((DetalleReceta)tblDetallesReceta.getSelectionModel().getSelectedItem()).getDosis());
            cmbCodReceta.getSelectionModel().select(buscarReceta(((DetalleReceta)tblDetallesReceta.getSelectionModel().getSelectedItem()).getCodigoReceta()));
            cmbCodMedicamento.getSelectionModel().select(buscarMedicamento(((DetalleReceta)tblDetallesReceta.getSelectionModel().getSelectedItem()).getCodigoMedicamento()));
        }
    }
    
    
    // ---------------------------- METODOS TABLA ----------------------------
    
    public ObservableList<DetalleReceta> getDetalleReceta() {
        ArrayList<DetalleReceta> lista = new ArrayList<DetalleReceta>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarDetallesReceta()}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {
                lista.add(new DetalleReceta(resultado.getInt("codigoDetalleReceta"),
                                            resultado.getString("dosis"),
                                            resultado.getInt("codigoReceta"),
                                            resultado.getInt("codigoMedicamento")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaDetallesReceta = FXCollections.observableArrayList(lista);
    }
    
    public Receta buscarReceta(int codigoReceta) {
        Receta resultado = null;
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarReceta(?)}");
            procedimiento.setInt(1, codigoReceta);
            
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                resultado = new Receta(registro.getInt("codigoReceta"), registro.getDate("fechaReceta"), registro.getInt("numeroColegiado"));
                
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public Medicamento buscarMedicamento(int codigoMedicamento) {
        Medicamento resultado = null;
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_BuscarMedicamento(?)}");
            procedimiento.setInt(1, codigoMedicamento);
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                resultado = new Medicamento(registro.getInt("codigoMedicamento"), registro.getString("nombreMedicamento"));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return resultado;
    }
    
    public ObservableList<Receta> getReceta() {
        ArrayList<Receta> lista = new ArrayList<Receta>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarRecetas()}");
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                lista.add(new Receta(registro.getInt("codigoReceta"),
                                    registro.getDate("fechaReceta"),
                                    registro.getInt("numeroColegiado")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaRecetas = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Medicamento> getMedicamento() {
        ArrayList<Medicamento> lista = new ArrayList<Medicamento>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarMedicamentos()}");
            ResultSet registro = procedimiento.executeQuery();
            
            while (registro.next()) {
                lista.add(new Medicamento(registro.getInt("codigoMedicamento"),
                                        registro.getString("nombreMedicamento")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaMedicamentos = FXCollections.observableArrayList(lista);
    }
    
    
    // ---------------------------- BOTONES ----------------------------
    
    public void nuevo() {
        switch (tipoDeOperacion) {
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
                
                if (tblDetallesReceta.getSelectionModel().getSelectedItem() != null) {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    int respuesta = JOptionPane.showConfirmDialog(null, 
                                    new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | ¿Estás seguro de eliminar este registro?</b></p></html>",
                                        icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25),
                                        JLabel.CENTER), 
                                    "Eliminar detalle receta", 
                                    JOptionPane.YES_NO_OPTION, 
                                    JOptionPane.PLAIN_MESSAGE);
                    
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try {
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarDetalleReceta(?)}");
                            procedimiento.setInt(1, ((DetalleReceta)tblDetallesReceta.getSelectionModel().getSelectedItem()).getCodigoDetalleReceta());
                            
                            procedimiento.execute();
                            
                            listaDetallesReceta.remove(tblDetallesReceta.getSelectionModel().getSelectedIndex());
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
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debes seleccionar un elemento</b></p></html>",
                                    icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25),
                                    JLabel.CENTER),
                                "Eliminar detalle receta",
                                JOptionPane.PLAIN_MESSAGE);
                }  
                
                break;
        }
    }
    
    public void editar() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                
                if (tblDetallesReceta.getSelectionModel().getSelectedItem() != null) {
                    
                    activarControles();
                    
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
                    
                    cmbCodReceta.setDisable(true);
                    cmbCodMedicamento.setDisable(true);
                    
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    
                }else {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    JOptionPane.showMessageDialog(null, 
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debe seleccionar un elemento</b></p></html>",
                                    icono("/org/jeffersoncalderon/image/IconEditar.png", 25, 25),
                                    JLabel.CENTER),
                                "Editar detalle receta",
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
        DetalleReceta registro = new DetalleReceta();
        registro.setDosis(txtDosis.getText());
        registro.setCodigoReceta(((Receta)cmbCodReceta.getSelectionModel().getSelectedItem()).getCodigoReceta());
        registro.setCodigoMedicamento(((Medicamento)cmbCodMedicamento.getSelectionModel().getSelectedItem()).getCodigoMedicamento());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarDetalleReceta(?, ?, ?)}");
            procedimiento.setString(1, registro.getDosis());
            procedimiento.setInt(2, registro.getCodigoReceta());
            procedimiento.setInt(3, registro.getCodigoMedicamento());
            
            procedimiento.execute();
            
            listaDetallesReceta.add(registro);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void actualizar() {
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarDetalleReceta(?, ?)}");
            DetalleReceta registro = (DetalleReceta)tblDetallesReceta.getSelectionModel().getSelectedItem();
            registro.setDosis(txtDosis.getText());
            
            procedimiento.setInt(1, registro.getCodigoDetalleReceta());
            procedimiento.setString(2, registro.getDosis());
            procedimiento.execute();
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Icon icono(String path, int width, int heigth) {
        Icon image = new ImageIcon(new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH));
        return image;
    }
    
    
    // ---------------------------- MANEJO DE CONTROLES ----------------------------
    
    public void desactivarControles() {
        txtCodDetalleReceta.setEditable(false);
        txtDosis.setEditable(false);
        cmbCodReceta.setDisable(true);
        cmbCodMedicamento.setDisable(true);
    }
    
    public void activarControles() {
        txtDosis.setEditable(true);
        cmbCodReceta.setDisable(false);
        cmbCodMedicamento.setDisable(false);
    }
    
    public void limpiarControles() {
        txtCodDetalleReceta.clear();
        txtDosis.clear();
        cmbCodReceta.getSelectionModel().select(null);
        cmbCodMedicamento.getSelectionModel().select(null);
        tblDetallesReceta.getSelectionModel().clearSelection();
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
