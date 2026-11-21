package co.edu.unbosque.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;

/**
 * Botón de la aplicación. El principal es verde con texto blanco; el secundario (por ejemplo,
 * "Atrás") es blanco con texto verde. FlatLaf redondea las esquinas y oscurece el color al pasar
 * el mouse y al presionar.
 */
public class PersonalButton extends JButton {

    public PersonalButton(String text) {
        this(text, true);
    }

    public PersonalButton(String text, boolean principal) {
        super(text);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Tema.fuente(Font.BOLD, 1));
        setBackground(principal ? Tema.VERDE : Color.WHITE);
        setForeground(principal ? Color.WHITE : Tema.VERDE_OSCURO);
        putClientProperty("JButton.borderColor", principal ? null : Tema.VERDE);
        Dimension tamano = getPreferredSize();
        setPreferredSize(new Dimension(Math.max(tamano.width + 24, 110), 38));
        // GridBagLayout usa el tamaño mínimo cuando falta espacio: así el botón no pierde altura
        setMinimumSize(new Dimension(80, 38));
    }
}
