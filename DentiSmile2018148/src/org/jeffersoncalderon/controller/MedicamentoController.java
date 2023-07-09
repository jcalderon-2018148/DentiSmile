
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
import org.jeffersoncalderon.bean.Medicamento;
import org.jeffersoncalderon.db.Conexion;
import org.jeffersoncalderon.report.GenerarReporte;
import org.jeffersoncalderon.system.Principal;

public class MedicamentoController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NUEVO, ELIMINAR, EDITAR, REPORTE, GUARDAR, CANCELAR, ACTUALIZAR, NINGUNO};
    private operaciones tipoDeOperaciones = operaciones.NINGUNO;
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
    @FXML private TextField txtCodMedicamento;
    @FXML private TextField txtNombreMedicamento;
    @FXML private TableView tblMedicamentos;
    @FXML private TableColumn colCodMedicamento;
    @FXML private TableColumn colNombreMedicamento;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    
    // ---------------------------- ELEMENTOS TABLA ----------------------------
    
    public void cargarDatos() {
        tblMedicamentos.setItems(getMedicamentos());
        colCodMedicamento.setCellValueFactory(new PropertyValueFactory<Medicamento, Integer>("codigoMedicamento"));
        colNombreMedicamento.setCellValueFactory(new PropertyValueFactory<Medicamento, String>("nombreMedicamento"));
    }
    
    public void seleccionarElemento() {
        if (tblMedicamentos.getSelectionModel().getSelectedItem() != null) {
            txtCodMedicamento.setText(String.valueOf(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getCodigoMedicamento()));
            txtNombreMedicamento.setText(((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getNombreMedicamento());
        }
    }
    
    
    // ---------------------------- METODOS TABLA ----------------------------
    
    public ObservableList<Medicamento> getMedicamentos() {
        ArrayList<Medicamento> lista = new ArrayList<Medicamento>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_ListarMedicamentos}");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {
                lista.add(new Medicamento(resultado.getInt("codigoMedicamento"), 
                                           resultado.getString("nombreMedicamento")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaMedicamentos = FXCollections.observableArrayList(lista);
    }
    
    
    //---------------------------- BOTONES ----------------------------
    
    public void nuevo() {
        switch (tipoDeOperaciones) {
            case NINGUNO:
                
                limpiarControles();
                activarControles();
                
                btnAgregar.setText("Guardar");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconGuardar.png"));
                btnEliminar.setText("Cancelar");
                imgEliminar.setImage(new Image("/org/jeffersoncalderon/image/Equis.png"));
                btnEliminar.getStyleClass().clear();
                btnEliminar.getStyleClass().add("button1");
                
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                btnEditar.setOpacity(0.5);
                btnReporte.setOpacity(0.5);
                imgEditar.setOpacity(0.5);
                imgReporte.setOpacity(0.5);
                
                tipoDeOperaciones = operaciones.GUARDAR;
                
                break;
                
            case GUARDAR:
                
                guardar();
                
                limpiarControles();
                desactivarControles();
                
                btnAgregar.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconMas.png"));
                btnEliminar.setText("Eliminar");
                imgEliminar.setImage(new Image("/org/jeffersoncalderon/image/IconMenos.png"));
                btnEliminar.getStyleClass().clear();
                btnEliminar.getStyleClass().add("button2");
                
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                btnEditar.setOpacity(1);
                btnReporte.setOpacity(1);
                imgEditar.setOpacity(1);
                imgReporte.setOpacity(1);

                tipoDeOperaciones = operaciones.NINGUNO;
                
                cargarDatos();
                
                break;
        }
    }
    
    public void eliminar() {
        switch (tipoDeOperaciones) {
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
                btnReporte.setDisable(false);
                btnEditar.setOpacity(1);
                btnReporte.setOpacity(1);
                imgEditar.setOpacity(1);
                imgReporte.setOpacity(1);

                tipoDeOperaciones = operaciones.NINGUNO;
                
                break;
                
            default:
                if (tblMedicamentos.getSelectionModel().getSelectedItem() != null) {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    int respuesta = JOptionPane.showConfirmDialog(null, 
                                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | ¿Estás seguro de eliminar el registro?</b></p></html>",
                                                    icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25),
                                                    JLabel.CENTER), 
                                                "Eliminar Medicamento", 
                                                JOptionPane.YES_NO_OPTION, 
                                                JOptionPane.PLAIN_MESSAGE);
                    
                    if (respuesta == JOptionPane.YES_OPTION) {
                        try{
                            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EliminarMedicamento(?)}");
                            procedimiento.setInt(1, ((Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem()).getCodigoMedicamento());
                            procedimiento.execute();
                            listaMedicamentos.remove(tblMedicamentos.getSelectionModel().getSelectedIndex());
                            
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
                                new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Debe selecionar un elemento</b></p></html>",
                                    icono("/org/jeffersoncalderon/image/IconMenos.png", 25, 25),
                                    JLabel.CENTER),
                                "Eliminar Medicamento",
                                JOptionPane.PLAIN_MESSAGE);
                } 
                
                break;
        }
    }
    
    public void editar() {
        switch (tipoDeOperaciones) {
            case NINGUNO:
                if (tblMedicamentos.getSelectionModel().getSelectedItem() != null) {
                    activarControles();
                    
                    btnAgregar.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnAgregar.setOpacity(0.5);
                    btnEliminar.setOpacity(0.5);
                    imgNuevo.setOpacity(0.5);
                    imgEliminar.setOpacity(0.5);
                    
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnReporte.getStyleClass().clear();
                    btnReporte.getStyleClass().add("button1");
                    imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconGuardar.png"));
                    imgReporte.setImage(new Image("/org/jeffersoncalderon/image/Equis.png"));

                    tipoDeOperaciones = operaciones.ACTUALIZAR;
                    
                }else {
                    UI.put("OptionPane.background", new Color(44,68,84));
                    UI.put("Panel.background", new Color(44,68,84));
                    
                    JOptionPane.showMessageDialog(null, 
                                new JLabel("<html><p style = \" color: white; font: 11px \"><b> | Debe seleccionar un elemento</b></p></html>",
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
                btnReporte.setText("Reporte");
                btnReporte.getStyleClass().clear();
                btnReporte.getStyleClass().add("button2");
                imgEditar.setImage(new Image("/org/jeffersoncalderon/image/IconEditar.png"));
                imgReporte.setImage(new Image("/org/jeffersoncalderon/image/IconReporte.png"));

                tipoDeOperaciones = operaciones.NINGUNO;
                
                cargarDatos();
                
                break;
        }
    }
    
    public void reporte() {
        switch (tipoDeOperaciones) {
            case NINGUNO:
                
                UI.put("OptionPane.background", new Color(255, 255, 255));
                UI.put("Panel.background", new Color(255, 255, 255));
                imprimirReporte();
                
                break;
                
            case ACTUALIZAR:
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
                
                desactivarControles();
                limpiarControles();
                tipoDeOperaciones = operaciones.NINGUNO;
                
                break;
        }
    }
    
    
    // ---------------------------- PROCEDIMIENTOS ----------------------------
    
    public void guardar() {
        Medicamento registro = new Medicamento();
        registro.setNombreMedicamento(txtNombreMedicamento.getText());
        
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarMedicamento(?)}");
            procedimiento.setString(1, registro.getNombreMedicamento());
            procedimiento.execute();
            listaMedicamentos.add(registro);
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void actualizar() {
        try{
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_EditarMedicamento(?, ?)}");
            Medicamento registro = (Medicamento)tblMedicamentos.getSelectionModel().getSelectedItem();
            registro.setNombreMedicamento(txtNombreMedicamento.getText());
            
            procedimiento.setInt(1, registro.getCodigoMedicamento());
            procedimiento.setString(2, registro.getNombreMedicamento());
            procedimiento.execute();
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void imprimirReporte() {
        Map parametros = new HashMap();
        parametros.put("codImagen", "org/jeffersoncalderon/image/ReporteMedicamentos.png");
        parametros.put("codigoMedicamento", null);
        GenerarReporte.mostrarReporte("ReporteMedicamentos.jasper", "Reporte de Medicamentos", parametros);
    }
    
    public Icon icono(String path, int width, int heigth) {
        Icon imagen = new ImageIcon(new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH));
        return imagen;
    }

    
    // ---------------------------- MANEJO DE CONTROLES ----------------------------
    
    public void limpiarControles() {
        txtCodMedicamento.clear();
        txtNombreMedicamento.clear();
        tblMedicamentos.getSelectionModel().clearSelection();
    }
    
    public void activarControles(){
        txtNombreMedicamento.setEditable(true);
    }
    
    public void desactivarControles() {
        txtNombreMedicamento.setEditable(false);
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
