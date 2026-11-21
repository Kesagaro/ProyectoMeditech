package co.edu.unbosque.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PanelInicio extends JPanel {
    private PersonalButton loginButton;
    private PersonalButton registerButton;

    public PanelInicio() {
        super(new GridBagLayout());

        TransparentPanel tarjeta = new TransparentPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));

        // Logo cargado desde el classpath para no depender de la carpeta de ejecución
        java.net.URL logo = getClass().getResource("/BosqueFarma.png");
        if (logo != null) {
            Image img = new ImageIcon(logo).getImage().getScaledInstance(280, 140, Image.SCALE_SMOOTH);
            centrar(tarjeta.add(new JLabel(new ImageIcon(img))));
        }
        tarjeta.add(Box.createVerticalStrut(18));
        centrar(tarjeta.add(Tema.titulo("Bienvenido")));
        tarjeta.add(Box.createVerticalStrut(4));
        centrar(tarjeta.add(Tema.subtitulo("Gestión de inventario, órdenes y turnos")));
        tarjeta.add(Box.createVerticalStrut(24));

        loginButton = new PersonalButton("Iniciar sesión");
        registerButton = new PersonalButton("Registrar funcionario", false);
        for (PersonalButton boton : new PersonalButton[] {loginButton, registerButton}) {
            boton.setMaximumSize(new Dimension(280, 42));
            boton.setPreferredSize(new Dimension(280, 42));
            centrar(tarjeta.add(boton));
            tarjeta.add(Box.createVerticalStrut(10));
        }

        add(tarjeta);
    }

    private static void centrar(Component componente) {
        ((javax.swing.JComponent) componente).setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Tema.pintarFondo(g, this);
    }

    public PersonalButton getLoginButton() {
        return loginButton;
    }

    public PersonalButton getRegisterButton() {
        return registerButton;
    }
}
