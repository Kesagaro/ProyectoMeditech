package co.edu.unbosque.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

/** Colores, fuentes y piezas comunes de la interfaz, para que todas las pantallas se vean iguales. */
public final class Tema {
    public static final Color VERDE = new Color(0x4E7A3A);
    public static final Color VERDE_OSCURO = new Color(0x34532A);
    public static final Color FONDO_ARRIBA = new Color(0x6B8E4E);
    public static final Color FONDO_ABAJO = new Color(0xC9D8C5);
    public static final Color TEXTO = new Color(0x1F2A1C);
    public static final Color TEXTO_SUAVE = new Color(0x5E6B5A);
    /** Morado del logo, para resaltar datos importantes como el número de turno. */
    public static final Color ACENTO = new Color(0x6A2C70);

    public static final int MARGEN = 20;

    private Tema() {
    }

    /** Activa FlatLaf con el verde de la marca. Se llama una vez, antes de crear la ventana. */
    public static void instalar() {
        FlatLaf.setGlobalExtraDefaults(Map.of("@accentColor", "#4E7A3A"));
        FlatLightLaf.setup();
        UIManager.put("Button.arc", 14);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ScrollBar.showButtons", false);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.width", 10);
        UIManager.put("Table.rowHeight", 30);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.intercellSpacing", new java.awt.Dimension(0, 1));
        UIManager.put("Table.selectionBackground", new Color(0xC5DBB6));
        UIManager.put("Table.selectionForeground", TEXTO);
        UIManager.put("Table.selectionInactiveBackground", new Color(0xC5DBB6));
        UIManager.put("Table.selectionInactiveForeground", TEXTO);
        UIManager.put("Table.cellMargins", new Insets(0, 10, 0, 10));
        UIManager.put("Table.alternateRowColor", new Color(0xF6F9F4));
        UIManager.put("TableHeader.height", 32);
        UIManager.put("TableHeader.background", new Color(0xEEF3EB));
        UIManager.put("TableHeader.font", fuente(Font.BOLD, 0));
    }

    /** Fuente base de la interfaz con otro estilo y {@code extra} puntos más grande. */
    public static Font fuente(int estilo, float extra) {
        Font base = UIManager.getFont("Label.font");
        if (base == null) {
            base = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        }
        return base.deriveFont(estilo, base.getSize2D() + extra);
    }

    /** Fondo degradado verde de todas las pantallas. */
    public static void pintarFondo(Graphics g, JComponent panel) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(new GradientPaint(0, 0, FONDO_ARRIBA, 0, panel.getHeight(), FONDO_ABAJO));
        g2d.fillRect(0, 0, panel.getWidth(), panel.getHeight());
    }

    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(Font.BOLD, 9));
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    public static JLabel subtitulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(Font.PLAIN, 1));
        etiqueta.setForeground(TEXTO_SUAVE);
        return etiqueta;
    }

    /**
     * Barra superior de cada pantalla: botón para volver, título en blanco y, a la derecha,
     * un componente opcional (por ejemplo, quién inició sesión).
     */
    public static JPanel encabezado(PersonalButton atras, String titulo, Component derecha) {
        JPanel barra = new JPanel(new GridBagLayout());
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(14, MARGEN, 6, MARGEN));
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        if (atras != null) {
            barra.add(atras, c);
        }
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(fuente(Font.BOLD, 7));
        etiqueta.setForeground(Color.WHITE);
        c.weightx = 1;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, atras != null ? 16 : 0, 0, 0);
        barra.add(etiqueta, c);
        if (derecha != null) {
            c.weightx = 0;
            c.insets = new Insets(0, 0, 0, 0);
            barra.add(derecha, c);
        }
        return barra;
    }

    /** Tarjeta a todo el ancho de la pantalla, con margen alrededor. */
    public static JPanel contenido(JComponent tarjeta) {
        JPanel contenedor = new JPanel(new java.awt.BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(margen());
        contenedor.add(tarjeta);
        return contenedor;
    }

    /** Tarjeta de ancho fijo, centrada en la pantalla (formularios cortos). */
    public static JPanel centrado(JComponent tarjeta, int ancho) {
        tarjeta.setPreferredSize(new java.awt.Dimension(ancho, tarjeta.getPreferredSize().height));
        JPanel contenedor = new JPanel(new GridBagLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(margen());
        contenedor.add(tarjeta);
        return contenedor;
    }

    private static Border margen() {
        return BorderFactory.createEmptyBorder(6, MARGEN, MARGEN, MARGEN);
    }

    /**
     * Agrega una fila "etiqueta: campo" a un formulario con {@link GridBagLayout}.
     * El campo ocupa el ancho disponible.
     */
    public static void filaFormulario(JPanel formulario, int fila, String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila;
        c.insets = new Insets(5, 0, 5, 12);
        c.anchor = GridBagConstraints.WEST;
        JLabel texto = new JLabel(etiqueta);
        texto.setForeground(TEXTO);
        formulario.add(texto, c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 5, 0);
        formulario.add(campo, c);
    }
}
