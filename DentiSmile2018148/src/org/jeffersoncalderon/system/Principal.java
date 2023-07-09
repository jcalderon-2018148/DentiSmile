/*
    Nombre completo: Jefferson Alexander Calderón Martínez
    Código Técnico: IN5AV
    Carné: 2018148
    Fecha de creación: 5-4-2022
    Fecha de modificación: 5-10-2022
 */
package org.jeffersoncalderon.system;

import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.jeffersoncalderon.controller.CitaController;
import org.jeffersoncalderon.controller.DetalleRecetaController;
import org.jeffersoncalderon.controller.DoctorController;
import org.jeffersoncalderon.controller.EspecialidadController;
import org.jeffersoncalderon.controller.LoginController;
import org.jeffersoncalderon.controller.MedicamentoController;
import org.jeffersoncalderon.controller.MenuPrincipalController;
import org.jeffersoncalderon.controller.PacienteController;
import org.jeffersoncalderon.controller.ProgramadorController;
import org.jeffersoncalderon.controller.RecetaController;
import org.jeffersoncalderon.controller.UsuarioController;

public class Principal extends Application {
    private Stage escenarioPrincipal;
    private Scene escena;
    private final String PAQUETE_VISTA = "/org/jeffersoncalderon/view/";
    
    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        this.escenarioPrincipal = escenarioPrincipal;
        this.escenarioPrincipal.setTitle("DentiSmile");
        escenarioPrincipal.getIcons().add(new Image("/org/jeffersoncalderon/image/LogoClinicaPNG.png"));
//        Parent root = FXMLLoader.load(getClass().getResource("/org/jeffersoncalderon/view/MenuPrincipalView.fxml"));
//        Scene escena = new Scene(root);
//        escenarioPrincipal.setScene(escena);
        
        //login();
        menuPrincipal();
        
        escenarioPrincipal.show();
    }
    
    public void login() {
        try {
            LoginController ventanaLogin = (LoginController)cambiarEscena("LoginView.fxml", 820, 600);
            ventanaLogin.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void usuario() {
        try {
            UsuarioController ventanaUsuario = (UsuarioController)cambiarEscena("UsuarioView.fxml", 787, 464);
            ventanaUsuario.setEscenarioPrincipal(this);
        }catch  (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void menuPrincipal() {
        try {
            MenuPrincipalController ventanaMenu = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml", 530, 538);
            ventanaMenu.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public void programador() {
        try {
            ProgramadorController vistaProgramador = (ProgramadorController)cambiarEscena("ProgramadorView.fxml", 600, 400);
            vistaProgramador.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void paciente() {
        try {
            PacienteController vistaPaciente = (PacienteController)cambiarEscena("PacientesView.fxml", 1150, 610);
            vistaPaciente.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void especialidad() {
        try {
            EspecialidadController vistaEspecialidad = (EspecialidadController)cambiarEscena("EspecialidadesView.fxml", 825, 600);
            vistaEspecialidad.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void medicamento() {
        try{
            MedicamentoController vistaMedicamento = (MedicamentoController)cambiarEscena("MedicamentosView.fxml", 825, 600);
            vistaMedicamento.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void doctor() {
        try{
            DoctorController vistaDoctor = (DoctorController)cambiarEscena("DoctoresView.fxml", 1150, 610);
            vistaDoctor.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void receta() {
        try {
            RecetaController vistaReceta = (RecetaController)cambiarEscena("RecetasView.fxml", 825, 600);
            vistaReceta.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void detalleReceta() {
        try {
            DetalleRecetaController vistaDetalleReceta = (DetalleRecetaController)cambiarEscena("DetallesRecetaView.fxml", 1150, 610);
            vistaDetalleReceta.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void cita() {
        try {
            CitaController vistaCita = (CitaController)cambiarEscena("CitasView.fxml", 1150, 610);
            vistaCita.setEscenarioPrincipal(this);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    public Initializable cambiarEscena(String fxml, int width, int height) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA + fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA + fxml));
        
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo), width, height);
        
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
        
        return resultado;
    }
    
}
