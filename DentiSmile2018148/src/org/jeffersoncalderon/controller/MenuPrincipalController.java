package org.jeffersoncalderon.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.jeffersoncalderon.system.Principal;

public class MenuPrincipalController implements Initializable {
    private Principal escenarioPrincipal;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }

    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void programador() {
        escenarioPrincipal.programador();
    }
    
    public void paciente() {
        escenarioPrincipal.paciente();
    }
    
    public void especialidad() {
        escenarioPrincipal.especialidad();
    }
    
    public void medicamento() {
        escenarioPrincipal.medicamento();
    }
    
    public void doctor() {
        escenarioPrincipal.doctor();
    }
    
    public void receta() {
        escenarioPrincipal.receta();
    }
    
    public void detalleReceta() {
        escenarioPrincipal.detalleReceta();
    }
    
    public void cita() {
        escenarioPrincipal.cita();
    }
    
    public void login() {
        escenarioPrincipal.login();
    }
    
    public void usuario() {
        escenarioPrincipal.usuario();
    }
}
