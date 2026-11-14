package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelInicio extends JPanel {
	   private PersonalButton loginButton;
	    private PersonalButton registerButton;
	   
	    public PanelInicio() {
    setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Usar FlowLayout para centrar

    // Crear panel vidrio
    TransparentPanel glassPanel = new TransparentPanel();
    glassPanel.setLayout(new BoxLayout(glassPanel, BoxLayout.Y_AXIS));
    glassPanel.setPreferredSize(new Dimension(300, 300)); // Tamaño 

    // Agregar un espaciador en la parte superior
    glassPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Espacio de 20 píxeles

    // Logo cargado desde el classpath para no depender de la carpeta de ejecución
    java.net.URL logo = getClass().getResource("/BosqueFarma.png");
    if (logo != null) {
        Image img = new ImageIcon(logo).getImage().getScaledInstance(200, 100, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img), SwingConstants.CENTER);
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        glassPanel.add(imageLabel);
    }

    // Crear botones con estilos
    loginButton = new PersonalButton("Iniciar");
    registerButton = new PersonalButton("Registrar");
   

    // Agregar botones al panel de vidrio
    glassPanel.add(Box.createRigidArea(new Dimension(0, 15))); 
    loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); 
    glassPanel.add(loginButton);
    glassPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    registerButton.setAlignmentX(Component.CENTER_ALIGNMENT); 
    glassPanel.add(registerButton);
    glassPanel.add(Box.createRigidArea(new Dimension(0, 15))); 
    

    add(glassPanel);
}

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;

    // Crear el degradado
    GradientPaint gradient = new GradientPaint(0, 0, new Color(107, 142, 78), 0, getHeight(), new Color(178,197,178));
    g2d.setPaint(gradient);
    g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public PersonalButton getLoginButton() {
        return loginButton;
    }

    public PersonalButton getRegisterButton() {
        return registerButton;
    }
    
    

   
}