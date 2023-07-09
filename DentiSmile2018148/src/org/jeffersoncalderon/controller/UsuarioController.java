
package org.jeffersoncalderon.controller;

import java.awt.Color;
import java.net.URL;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.jeffersoncalderon.bean.Usuario;
import org.jeffersoncalderon.db.Conexion;
import org.jeffersoncalderon.system.Principal;

public class UsuarioController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones{NINGUNO, GUARDAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private UIManager UI;
    
    @FXML private TextField txtCodUsuario;
    @FXML private TextField txtNombreUsuario;
    @FXML private TextField txtApellidoUsuario;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtPassword;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnEliminar.setDisable(true);
        imgEliminar.setOpacity(0.5);
    }
    
    public void nuevo() {
        switch (tipoDeOperacion) {
            case NINGUNO:
                
                limpiarControles();
                activarControles();
                
                btnAgregar.setText("Guardar");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconGuardar.png"));
                btnEliminar.setText("Cancelar");
                imgEliminar.setOpacity(1);
                
                tipoDeOperacion = operaciones.GUARDAR;
                
                break;
            
            case GUARDAR:
                
                guardar();
                limpiarControles();
                desactivarControles();
                
                btnAgregar.setText("Nuevo");
                imgNuevo.setImage(new Image("/org/jeffersoncalderon/image/IconMas.png"));
                btnEliminar.setText("Cancelar");
                imgEliminar.setOpacity(0.5);
                
                tipoDeOperacion = operaciones.NINGUNO;
                
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
                btnEliminar.setText("Cancelar");
                imgEliminar.setOpacity(0.5);
                
                tipoDeOperacion = operaciones.NINGUNO;
                
                login();
                
                break;
        }
    }
    
    
    
    public void guardar() {
        Usuario registro = new Usuario();
        registro.setNombreUsuario(txtNombreUsuario.getText());
        registro.setApellidoUsuario(txtApellidoUsuario.getText());
        registro.setUsuarioLogin(txtUsuario.getText());
        registro.setContrasena(txtPassword.getText());
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("{call sp_AgregarUsuario(?, ?, ?, ?)}");
            procedimiento.setString(1, registro.getNombreUsuario());
            procedimiento.setString(2, registro.getApellidoUsuario());
            procedimiento.setString(3, registro.getUsuarioLogin());
            procedimiento.setString(4, registro.getContrasena());
            
            procedimiento.execute();
            
            UI.put("OptionPane.background", new Color(44,68,84));
            UI.put("Panel.background", new Color(44,68,84));
            
            JOptionPane.showMessageDialog(null, 
                        new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Se ha registrado el usuario correctamente</b></p></html>",
                            icono("/org/jeffersoncalderon/image/IconMas.png", 25, 25),
                            JLabel.CENTER),
                        "Agregar usuario",
                        JOptionPane.PLAIN_MESSAGE);
            login();
            
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Icon icono(String path, int width, int heigth) {
        Icon imagen = new ImageIcon(new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH));
        return imagen;
    }
    
    public void desactivarControles() {
        txtCodUsuario.setEditable(false);
        txtNombreUsuario.setEditable(false);
        txtApellidoUsuario.setEditable(false);
        txtUsuario.setEditable(false);
        txtPassword.setEditable(false);
        btnEliminar.setDisable(true);
    }
    
    public void activarControles() {
        txtNombreUsuario.setEditable(true);
        txtApellidoUsuario.setEditable(true);
        txtUsuario.setEditable(true);
        txtPassword.setEditable(true);
        btnEliminar.setDisable(false);
    }
    
    public void limpiarControles() {
        txtCodUsuario.clear();
        txtNombreUsuario.clear();
        txtApellidoUsuario.clear();
        txtUsuario.clear();
        txtPassword.clear();
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void login() {
        escenarioPrincipal.login();
    }

}
