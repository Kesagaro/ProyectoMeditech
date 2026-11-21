package co.edu.unbosque.view;

import javax.swing.*;
import java.awt.*;

public class View extends JFrame {
    private PanelInicio panelInicio;
    private PanelLogin panelLogin;
    private PanelMenu panelMenu;
    private PanelRegistro panelRegistro;
    private PanelInventario panelInventario;
    private PanelTurno panelTurno;
    private PanelReport panelReport;
    private PanelTop5 panelTop5;
    private PanelOrdenes panelOrdenes;
    private CardLayout cardLayout;
    private PanelFuncionarios panelFuncionarios;
    private PanelMedicina panelMedicina;
    private JPanel mainPanel;

    public View() {
        setTitle("BosqueFarma");
        setSize(960, 640);
        setMinimumSize(new Dimension(860, 580));
        java.net.URL logo = getClass().getResource("/BosqueFarma.png");
        if (logo != null) {
            setIconImage(new ImageIcon(logo).getImage());
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panelInicio = new PanelInicio();
        panelLogin = new PanelLogin();
        panelRegistro = new PanelRegistro();
        panelMenu = new PanelMenu();
        panelInventario = new PanelInventario();
        panelTurno = new PanelTurno();
        panelReport = new PanelReport();
        panelTop5 = new PanelTop5(); 
        panelOrdenes = new PanelOrdenes(); 
        panelFuncionarios = new PanelFuncionarios();
        panelMedicina = new PanelMedicina();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(panelInicio, "PanelInicio");
        mainPanel.add(panelLogin, "PanelLogin");
        mainPanel.add(panelRegistro, "PanelRegistro");
        mainPanel.add(panelMenu, "PanelMenu");
        mainPanel.add(panelInventario, "PanelInventario");
        mainPanel.add(panelTurno, "PanelTurno");
        mainPanel.add(panelReport, "PanelReport");
        mainPanel.add(panelTop5, "PanelTop5"); 
        mainPanel.add(panelOrdenes, "PanelOrdenes"); 
        mainPanel.add(panelFuncionarios, "PanelFuncionarios");
        mainPanel.add(panelMedicina, "PanelMedicina");

        add(mainPanel);
        setVisible(true);
    }

    public PanelInicio getPanelInicio() {
        return panelInicio;
    }

    public PanelLogin getPanelLogin() {
        return panelLogin;
    }

    public PanelRegistro getPanelRegistro() {
        return panelRegistro;
    }

    public PanelMenu getPanelMenu() {
        return panelMenu;
    }

    public PanelInventario getPanelInventario() {
        return panelInventario;
    }

    public PanelTurno getPanelTurno() {
        return panelTurno;
    }

    public PanelReport getPanelReport() {
        return panelReport;
    }

    public PanelTop5 getPanelTop5() {
        return panelTop5;
    }

    public PanelOrdenes getPanelOrdenes() {
        return panelOrdenes;
    }
    
    public PanelFuncionarios getPanelFuncionarios() {
    	return panelFuncionarios;
    }

    public PanelMedicina getPanelMedicina() {
        return panelMedicina;
    }

    public void showPanelMedicina() {
        cardLayout.show(mainPanel, "PanelMedicina");
    }


    public void showPanelLogin() {
        cardLayout.show(mainPanel, "PanelLogin");
    }
    
    public void showPanelFuncionarios() {
    	cardLayout.show(mainPanel, "PanelFuncionarios");
    }

    public void showPanelMenu() {
        cardLayout.show(mainPanel, "PanelMenu");
    }

    public void showPanelRegistro() {
        cardLayout.show(mainPanel, "PanelRegistro");
    }

    public void showPanelInicio() {
        cardLayout.show(mainPanel, "PanelInicio");
    }

    public void showPanelInventario() {
        cardLayout.show(mainPanel, "PanelInventario");
    }

    public void showPanelTurno() {
        cardLayout.show(mainPanel, "PanelTurno");
    }

    public void showPanelReport() {
        cardLayout.show(mainPanel, "PanelReport");
    }

    public void showPanelTop5() {
        cardLayout.show(mainPanel, "PanelTop5");
    }

    public void showPanelOrdenes() {
        cardLayout.show(mainPanel, "PanelOrdenes");
    }
}