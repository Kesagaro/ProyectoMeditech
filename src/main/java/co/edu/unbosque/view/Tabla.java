package co.edu.unbosque.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Tabla de solo lectura que se ordena con un clic en el encabezado. Da formato a los pesos
 * ($7.500) y a las fechas (19/11/2026 14:30), y muestra un mensaje cuando no hay filas.
 */
public class Tabla extends JScrollPane {
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat PESOS = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO"));

    private final DefaultTableModel modelo;
    private final JTable tabla;
    private String mensajeVacio = "Sin datos.";

    /**
     * @param columnas títulos de las columnas
     * @param tipos    clase de cada columna (para ordenar bien): String, Long, Integer, Double
     *                 (se muestra como pesos) o LocalDateTime
     */
    public Tabla(String[] columnas, Class<?>[] tipos) {
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                return tipos[columna];
            }
        };
        tabla = new JTable(modelo) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0) {
                    pintarMensajeVacio(g, this);
                }
            }
        };
        tabla.setAutoCreateRowSorter(true);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setDefaultRenderer(Double.class, new Formato(SwingConstants.RIGHT) {
            @Override
            String texto(Object valor) {
                return "$" + PESOS.format(Math.round((Double) valor));
            }
        });
        tabla.setDefaultRenderer(LocalDateTime.class, new Formato(SwingConstants.LEFT) {
            @Override
            String texto(Object valor) {
                return ((LocalDateTime) valor).format(FORMATO_FECHA);
            }
        });
        Formato numero = new Formato(SwingConstants.RIGHT) {
            @Override
            String texto(Object valor) {
                return valor.toString();
            }
        };
        tabla.setDefaultRenderer(Long.class, numero);
        tabla.setDefaultRenderer(Integer.class, numero);
        setViewportView(tabla);
        setBorder(BorderFactory.createLineBorder(new java.awt.Color(0xD5DFD0)));
    }

    /** Reemplaza las filas de la tabla. */
    public void mostrar(List<Object[]> filas) {
        modelo.setRowCount(0);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
    }

    /** Ancho preferido de cada columna, en píxeles; el espacio sobrante se reparte en proporción. */
    public void anchos(int... pixeles) {
        for (int i = 0; i < pixeles.length; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(pixeles[i]);
        }
    }

    public void setMensajeVacio(String mensaje) {
        this.mensajeVacio = mensaje;
    }

    public JTable getTabla() {
        return tabla;
    }

    public int getFilas() {
        return modelo.getRowCount();
    }

    /** Valor de la fila seleccionada (en el orden del modelo), o {@code null} si no hay selección. */
    public Object valorSeleccionado(int columna) {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : modelo.getValueAt(tabla.convertRowIndexToModel(fila), columna);
    }

    private void pintarMensajeVacio(Graphics g, JTable componente) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(Tema.fuente(Font.PLAIN, 1));
        g2d.setColor(Tema.TEXTO_SUAVE);
        int ancho = g2d.getFontMetrics().stringWidth(mensajeVacio);
        g2d.drawString(mensajeVacio, (componente.getWidth() - ancho) / 2, 40);
        g2d.dispose();
    }

    /** Renderizador que convierte el valor a texto; las celdas vacías muestran un guion. */
    private abstract static class Formato extends DefaultTableCellRenderer {
        Formato(int alineacion) {
            setHorizontalAlignment(alineacion);
        }

        abstract String texto(Object valor);

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            setText(valor == null ? "—" : texto(valor));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            return this;
        }
    }
}
