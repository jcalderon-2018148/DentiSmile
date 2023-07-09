
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
import javafx.scene.control.TextField;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.jeffersoncalderon.bean.Login;
import org.jeffersoncalderon.bean.Usuario;
import org.jeffersoncalderon.db.Conexion;
import org.jeffersoncalderon.system.Principal;

public class LoginController implements Initializable{
    private Principal escenarioPrincipal;
    private ObservableList<Usuario> listaUsuario;
    private UIManager UI;
    
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public ObservableList<Usuario> getUsuario() {
        ArrayList<Usuario> lista = new ArrayList<Usuario>();
        
        try {
            PreparedStatement procedimiento = Conexion.getInstance().getConexion().prepareCall("call sp_ListarUsuarios()");
            ResultSet resultado = procedimiento.executeQuery();
            
            while (resultado.next()) {
                lista.add(new Usuario(resultado.getInt("codigoUsuario"),
                                        resultado.getString("nombreUsuario"),
                                        resultado.getString("apellidoUsuario"),
                                        resultado.getString("usuarioLogin"),
                                        resultado.getString("contrasena")));
            }
            
        }catch (Exception e) {
            e.printStackTrace();
        }
        
        return listaUsuario = FXCollections.observableArrayList(lista);
    }
    
    @FXML
    private void login() {
        Login log = new Login();
        int x = 0;
        boolean bandera = false;
        log.setUsuarioMaster(txtUsername.getText());
        log.setPasswordLogin(txtPassword.getText());
        
        while (x < getUsuario().size()) {
            String user = getUsuario().get(x).getUsuarioLogin();
            String pass = getUsuario().get(x).getContrasena();
            
            if (user.equals(log.getUsuarioMaster()) && pass.equals(log.getPasswordLogin())) {
                UI.put("OptionPane.background", new Color(44,68,84));
                UI.put("Panel.background", new Color(44,68,84));
                
                JOptionPane.showMessageDialog(null, 
                            new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Sesión iniciada</b></p></html>" ,
                                icono("/org/jeffersoncalderon/image/IconUsuario.png", 25, 25),
                                JLabel.CENTER),
                            "Login",
                            JOptionPane.PLAIN_MESSAGE);
                
                escenarioPrincipal.menuPrincipal();
                
                x = getUsuario().size();
                bandera = true;
            }
            
            x++;
        }
        
        if (bandera == false) {
            UI.put("OptionPane.background", new Color(44,68,84));
            UI.put("Panel.background", new Color(44,68,84));
            
            JOptionPane.showMessageDialog(null, 
                        new JLabel("<html><p style = \" color: white; font: 11px; \"><b> | Usuario o contraseña incorrectos</b></p></html>",
                            icono("/org/jeffersoncalderon/image/IconUsuario.png", 25, 25),
                            JLabel.CENTER),
                        "Login",
                        JOptionPane.PLAIN_MESSAGE);
        }
        
    }
    
    public Icon icono(String path, int width, int heigth) {
        Icon imagen = new ImageIcon(new ImageIcon(getClass().getResource(path)).getImage().getScaledInstance(width, heigth, java.awt.Image.SCALE_SMOOTH));
        return imagen;
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void usuario() {
        escenarioPrincipal.usuario();
    }
}
