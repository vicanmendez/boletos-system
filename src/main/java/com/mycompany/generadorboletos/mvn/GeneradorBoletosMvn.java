/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.generadorboletos.mvn;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GeneradorBoletosMvn extends JFrame {
    private JTextField campoTitulo;
    private JTextField campoFecha;
    private JTextField campoDescripcion;
    private JTextField campoInstitucion;
    private JTextField campoValor;
    private List<JTextField> camposPremios;
    private JSpinner numeroInicio;
    private JSpinner numeroFin;
    private JSpinner boletosPorPagina;
    private JSpinner numerosPorBoleto;
    
    // Tamaño predeterminado para todos los campos de texto
    private static final Dimension TAMANO_CAMPO = new Dimension(250, 25);
    // Número fijo de campos de premios
    private static final int NUMERO_PREMIOS = 6;

    public GeneradorBoletosMvn() {
        super("Generador de Boletos");
        camposPremios = new ArrayList<>();
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {
        // Panel principal con GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos básicos
        campoTitulo = new JTextField();
        campoTitulo.setPreferredSize(TAMANO_CAMPO);
        
        campoFecha = new JTextField();
        campoFecha.setPreferredSize(TAMANO_CAMPO);
        
        campoDescripcion = new JTextField();
        campoDescripcion.setPreferredSize(TAMANO_CAMPO);
        
        campoInstitucion = new JTextField();
        campoInstitucion.setPreferredSize(TAMANO_CAMPO);
        
        campoValor = new JTextField();
        campoValor.setPreferredSize(TAMANO_CAMPO);
        
        // Crear 10 campos de premios fijos
        for (int i = 0; i < NUMERO_PREMIOS; i++) {
            JTextField campoPremio = new JTextField();
            campoPremio.setPreferredSize(TAMANO_CAMPO);
            camposPremios.add(campoPremio);
        }

        // Spinners para números
        numeroInicio = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        numeroFin = new JSpinner(new SpinnerNumberModel(100, 0, 999999, 1));
        boletosPorPagina = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        numerosPorBoleto = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));

        // Agregar componentes al panel principal
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoTitulo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Fecha (DD/MM/YYYY):"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoFecha, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        mainPanel.add(new JLabel("Descripción (ej: Quiniela Nocturna):"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoDescripcion, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        mainPanel.add(new JLabel("Institución:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoInstitucion, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        mainPanel.add(new JLabel("Valor del boleto ($):"), gbc);
        gbc.gridx = 1;
        mainPanel.add(campoValor, gbc);

        // Agregar etiqueta para los premios
        gbc.gridx = 0; gbc.gridy = 5;
        mainPanel.add(new JLabel("Premios (dejar en blanco si no corresponde):"), gbc);
        
        // Agregar los campos de premios al panel principal
        for (int i = 0; i < NUMERO_PREMIOS; i++) {
            gbc.gridx = 0; gbc.gridy = 6 + i;
            mainPanel.add(new JLabel((i+1) + "° Premio:"), gbc);
            gbc.gridx = 1;
            mainPanel.add(camposPremios.get(i), gbc);
        }

        // Panel de números
        JPanel panelNumeros = new JPanel(new GridLayout(4, 2, 5, 5));
        panelNumeros.setBorder(BorderFactory.createTitledBorder("Configuración de números"));
        panelNumeros.add(new JLabel("Número inicial:"));
        panelNumeros.add(numeroInicio);
        panelNumeros.add(new JLabel("Número final:"));
        panelNumeros.add(numeroFin);
        panelNumeros.add(new JLabel("Boletos por página:"));
        panelNumeros.add(boletosPorPagina);
        panelNumeros.add(new JLabel("Números por boleto:"));
        panelNumeros.add(numerosPorBoleto);

        gbc.gridx = 0; gbc.gridy = 6 + NUMERO_PREMIOS;
        gbc.gridwidth = 2;
        mainPanel.add(panelNumeros, gbc);

        // Botón de generar
        JButton btnGenerar = new JButton("Generar PDF");
        btnGenerar.addActionListener(e -> generarPDF());
        gbc.gridy = 7 + NUMERO_PREMIOS;
        mainPanel.add(btnGenerar, gbc);

        // Agregar el panel principal al frame
        add(mainPanel);
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void generarPDF() {
        try {
            String titulo = campoTitulo.getText().trim();
            String fecha = campoFecha.getText().trim();
            String descripcion = campoDescripcion.getText().trim();
            String institucion = campoInstitucion.getText().trim();
            String valorBoleto = campoValor.getText().trim();

            if (titulo.isEmpty() || fecha.isEmpty() || institucion.isEmpty() || valorBoleto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.");
                return;
            }

            int inicio = (Integer) numeroInicio.getValue();
            int fin = (Integer) numeroFin.getValue();
            int boletosPorPagina = (Integer) this.boletosPorPagina.getValue();
            int numerosPorBoleto = (Integer) this.numerosPorBoleto.getValue();

            if (inicio > fin) {
                JOptionPane.showMessageDialog(this, "El número inicial no puede ser mayor que el número final.");
                return;
            }

            String rutaCarpeta = seleccionarCarpeta();
            if (rutaCarpeta == null) {
                JOptionPane.showMessageDialog(this, "No se seleccionó ninguna carpeta.");
                return;
            }

            String rutaArchivo = rutaCarpeta + "/boletos_rifa.pdf";
            int digitos = String.valueOf(fin).length();

            // Generar PDF con todos los premios
            generarPDF(rutaArchivo, inicio, fin, digitos, titulo, camposPremios, fecha, 
                      descripcion, institucion, valorBoleto, boletosPorPagina, numerosPorBoleto);

            JOptionPane.showMessageDialog(this, "PDF creado exitosamente en: " + rutaArchivo);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ocurrió un error: " + e.getMessage());
        }
    }

    private String seleccionarCarpeta() {
        JFileChooser selector = new JFileChooser();
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        selector.setDialogTitle("Selecciona una carpeta para guardar el PDF");

        int resultado = selector.showSaveDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            return selector.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    private void generarPDF(String ruta, int numeroInicio, int numeroFin, int digitos,
                          String titulo, List<JTextField> camposPremios, String fecha, 
                          String descripcion, String institucion, String valorBoleto, 
                          int boletosPorPagina, int numerosPorBoleto) throws Exception {
        // Crear un documento con tamaño personalizado para cada boleto
        Document doc = new Document(new com.itextpdf.text.Rectangle(800, 300));
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(ruta));
        
        // Configurar para mantener los elementos juntos
        writer.setStrictImageSequence(true);
        
        doc.open();
        
        // Reducir el tamaño de la fuente para que todo quepa en una página
        com.itextpdf.text.Font fuente = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Font fuenteTitulo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD, BaseColor.RED);
        com.itextpdf.text.Font fuenteNumeros = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 24, com.itextpdf.text.Font.BOLD);

        int contador = 0;
        // Modificar para generar boletos con múltiples números
        for (int i = numeroInicio; i <= numeroFin; i += numerosPorBoleto) {
            // Crear un array con los números para este boleto
            int[] numerosBoleto = new int[Math.min(numerosPorBoleto, numeroFin - i + 1)];
            for (int j = 0; j < numerosBoleto.length; j++) {
                numerosBoleto[j] = i + j;
            }
            
            // Crear el boleto como un elemento único
            PdfPTable boleto = crearBoleto(numerosBoleto, fuente, fuenteTitulo, fuenteNumeros, digitos, titulo, camposPremios, fecha, 
                                         descripcion, institucion, valorBoleto);
            
            // Marcar el boleto como un elemento que no debe dividirse
            boleto.setKeepTogether(true);
            
            // Agregar el boleto al documento
            doc.add(boleto);

            contador++;
            if (contador >= boletosPorPagina && i + numerosPorBoleto <= numeroFin) {
                doc.newPage();
                contador = 0;
            }
        }

        doc.close();
    }

    private PdfPTable crearBoleto(int[] numeros, com.itextpdf.text.Font fuente, com.itextpdf.text.Font fuenteTitulo, 
                                com.itextpdf.text.Font fuenteNumeros, int digitos, String titulo, 
                                List<JTextField> camposPremios, String fecha, String descripcion, 
                                String institucion, String valorBoleto) throws DocumentException {
        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new int[]{3, 1, 5});
        
        // Marcar la tabla como un elemento que no debe dividirse
        tabla.setKeepTogether(true);
        
        tabla.addCell(parteIzquierda(numeros, fuente, digitos, valorBoleto));
        tabla.addCell(lineaDivisoria());
        tabla.addCell(parteDerecha(numeros, fuente, fuenteTitulo, fuenteNumeros, digitos, titulo, camposPremios, fecha, 
                                 descripcion, institucion, valorBoleto));
        return tabla;
    }

    private PdfPCell parteIzquierda(int[] numeros, com.itextpdf.text.Font fuente, int digitos, String valorBoleto) {
        StringBuilder numerosStr = new StringBuilder();
        for (int i = 0; i < numeros.length; i++) {
            if (i > 0) numerosStr.append(", ");
            numerosStr.append(String.format("%0" + digitos + "d", numeros[i]));
        }
        
        String[] datos = {
                "Nombre: ____________________",
                "Dirección: __________________",
                "Teléfono: ___________________",
                "Valor: " + valorBoleto,
                " ",
                "N°: " + numerosStr.toString()
        };
        return crearCeldaDesdeTexto(datos, fuente);
    }

    private PdfPCell parteDerecha(int[] numeros, com.itextpdf.text.Font fuente, com.itextpdf.text.Font fuenteTitulo,
                                com.itextpdf.text.Font fuenteNumeros, int digitos, String titulo,
                                List<JTextField> camposPremios, String fecha, String descripcion, 
                                String institucion, String valorBoleto) throws DocumentException {
        StringBuilder numerosStr = new StringBuilder();
        for (int i = 0; i < numeros.length; i++) {
            if (i > 0) numerosStr.append(", ");
            numerosStr.append(String.format("%0" + digitos + "d", numeros[i]));
        }

        PdfPTable subTabla = new PdfPTable(1);
        subTabla.setSpacingBefore(2);
        subTabla.setSpacingAfter(2);
        
        // Marcar la subTabla como un elemento que no debe dividirse
        subTabla.setKeepTogether(true);

        PdfPCell celdaTitulo = new PdfPCell(new Phrase(titulo, fuenteTitulo));
        celdaTitulo.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        celdaTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
        celdaTitulo.setPaddingBottom(5);
        subTabla.addCell(celdaTitulo);

        // Agregar premios con saltos de línea
        for (int i = 0; i < camposPremios.size(); i++) {
            String premio = camposPremios.get(i).getText().trim();
            if (!premio.isEmpty()) {
                PdfPCell celdaPremio = new PdfPCell(new Phrase((i+1) + "° Premio: " + premio, fuente));
                celdaPremio.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                celdaPremio.setPaddingBottom(3); // Reducir el espacio entre premios
                subTabla.addCell(celdaPremio);
            }
        }

        String[] datos = {
                "A beneficio de " + institucion,
                "Fecha: " + fecha,
                "A sortearse con: " + descripcion,
                "Valor: " + valorBoleto
        };

        for (String texto : datos) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
            celda.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            celda.setPaddingBottom(2); // Reducir el espacio entre líneas
            subTabla.addCell(celda);
        }

        // Agregar los números en la parte derecha debajo de la descripción
        PdfPCell numeroCelda = new PdfPCell(new Phrase("N° " + numerosStr.toString(), fuenteNumeros));
        numeroCelda.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        numeroCelda.setHorizontalAlignment(Element.ALIGN_CENTER);
        numeroCelda.setPaddingTop(5);
        subTabla.addCell(numeroCelda);

        PdfPCell contenedor = new PdfPCell(subTabla);
        contenedor.setPadding(5); // Reducir el padding
        contenedor.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        return contenedor;
    }

    private PdfPCell crearCeldaDesdeTexto(String[] lineas, com.itextpdf.text.Font fuente) {
        PdfPTable subTabla = new PdfPTable(1);
        for (String texto : lineas) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
            celda.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            celda.setPaddingBottom(2); // Reducir el espacio entre líneas
            subTabla.addCell(celda);
        }
        PdfPCell contenedor = new PdfPCell(subTabla);
        contenedor.setPadding(5); // Reducir el padding
        contenedor.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        return contenedor;
    }

    private PdfPCell lineaDivisoria() {
        PdfPCell linea = new PdfPCell();
        linea.setBorder(com.itextpdf.text.Rectangle.LEFT);
        linea.setBorderWidthLeft(1f);
        linea.setBorderColor(BaseColor.BLACK);
        return linea;
    }

    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        new GeneradorBoletosMvn(); // Esto lanza la ventana
    });
}

}