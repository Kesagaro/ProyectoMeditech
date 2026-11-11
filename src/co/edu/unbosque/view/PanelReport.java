package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class PanelReport extends JPanel {
    private JLabel titleLabel;
    private  PersonalButton atrasButton;
    private  PersonalButton Top5Button;
    private  PersonalButton UsuariosButton;
    private TransparentPanel glassPanel;

    public PanelReport() {
        setLayout(null); 
        

		glassPanel = new TransparentPanel();
        glassPanel.setLayout(null); 
        glassPanel.setBounds(100, 50, 300, 300);
       
        Top5Button = new PersonalButton(" Top ventas ");
        Top5Button.setBounds(60, 100, 180, 30); 
        glassPanel.add(Top5Button);
        
        UsuariosButton = new PersonalButton("Usuarios");
        UsuariosButton.setBounds(60, 150, 180, 30); 
        glassPanel.add(UsuariosButton);
        

        atrasButton = new PersonalButton("Atras");
		atrasButton.setBounds(5, 5, 100, 40);
		add(atrasButton);
        
        add(glassPanel);
        
      
    }
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GradientPaint gradient = new GradientPaint(0, 0, new Color(107, 142, 78), 0, getHeight(), new Color(178, 197, 178));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    

    public PersonalButton getAtrasButton() {
        return  atrasButton;
    }


	public PersonalButton getUsuariosButton() {
		
		return UsuariosButton;
	}

	public PersonalButton getTop5Button() {
		
		return Top5Button;
	}
}