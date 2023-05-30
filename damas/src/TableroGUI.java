

import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import javax.imageio.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;

import static java.lang.Thread.sleep;

public class TableroGUI {

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private Image[] imagenesTablero = new Image[6];
    private JPanel chessBoard;
    private final JLabel message = new JLabel("");

    private int[][] tablero;
    private int turno;
    private int pasosSinCaptura;
    private int estado;
    private Jugador[] jugadores = new Jugador[2];
    private boolean gameStarted = false;

    private TableroGUI() {
        inicializarGui();
    }

    public final void inicializarGui() {
        cargarImagenes();

        // set up the main GUI
        gui.setBorder(new EmptyBorder(0, 0, 0, 0));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        ImageIcon iconoNuevo = new ImageIcon("D:\\Ale U\\2023\\1er Semestre-2023\\Inteligencia Artificial\\IA_PP_Practica\\IA_1par\\damas\\src\\images\\add_icon.png");
        JButton botonNuevo = new JButton(iconoNuevo);
        botonNuevo.setToolTipText("Nuevo");
        botonNuevo.addActionListener(new AbstractAction() {
    @Override
    public void actionPerformed(ActionEvent e) {
        nuevoTablero();
    }
});

tools.add(botonNuevo);

        JToolBar tools2 = new JToolBar();
        tools2.setFloatable(false);
        gui.add(tools2, BorderLayout.AFTER_LAST_LINE);
       
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JComboBox m1 = new JComboBox();
        JComboBox m2 = new JComboBox();
        m1.setModel(new DefaultComboBoxModel(new String[] { "MinMax", "AlfaBeta", "Random"}));
        m2.setModel(new DefaultComboBoxModel(new String[] { "MinMax", "AlfaBeta", "Random"}));
        m1.setSelectedIndex(0);
        m2.setSelectedIndex(1);
//        contrincantes.append(opList.getSelectedItem().toString());
        m1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {}
        });
        m1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {}
        });

        JTextField prof1 = new JTextField();
        JTextField prof2 = new JTextField();
        prof1.setText("2");
        prof2.setText("2");

        ImageIcon iconoSig = new ImageIcon("D:\\Ale U\\2023\\1er Semestre-2023\\Inteligencia Artificial\\IA_PP_Practica\\IA_1par\\damas\\src\\images\\next_icon.png");
        JButton botonSig = new JButton(iconoSig);
        botonSig.setToolTipText("Siguiente Paso");

        botonSig.addActionListener(new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (!gameStarted)
                    if (!profIsOk(prof1.getText()) || !profIsOk(prof2.getText())) {
                        JOptionPane.showMessageDialog(null, "Debe especificar una profundidad mayor a 1", "Atencion", JOptionPane.ERROR_MESSAGE);
                    } else {

//                        if (!gameStarted) {
    switch (m1.getSelectedIndex()) {
        case 0:
            jugadores[0] = new Minimax(Tablero.JUGADOR_NEGRO, Integer.parseInt(prof1.getText()));
//                                jugadores[1] = new AlphaBeta(Tablero.JUGADOR_BLANCO, Integer.parseInt(prof.getText()));
            break;
        case 1:
            jugadores[0] = new AlphaBeta(Tablero.JUGADOR_NEGRO, Integer.parseInt(prof1.getText()));
//                                jugadores[1] = new RL(Tablero.JUGADOR_BLANCO);
            break;
        case 2:
            jugadores[0] = new Random(Tablero.JUGADOR_NEGRO);
            break;
            
    }

                        switch (m2.getSelectedIndex()) {
                            case 0:
                                jugadores[1] = new Minimax(Tablero.JUGADOR_BLANCO, Integer.parseInt(prof2.getText()));
//                                jugadores[1] = new AlphaBeta(Tablero.JUGADOR_BLANCO, Integer.parseInt(prof.getText()));
                                break;
                            case 1:
                                jugadores[1] = new AlphaBeta(Tablero.JUGADOR_BLANCO, Integer.parseInt(prof2.getText()));
//                                jugadores[1] = new RL(Tablero.JUGADOR_BLANCO);
                                break;
                            case 2:
                                jugadores[1] = new Random(Tablero.JUGADOR_BLANCO);
                                break;           
                        }
                        gameStarted = true;
//                        }
                    }

                if (gameStarted) {
                    boolean captura;

                    if (estado == Tablero.JUEGO_CONTINUA && pasosSinCaptura < 100) {
                        Object[] resultado = jugadores[turno].mover(tablero);
                        tablero = (int[][]) resultado[0];
                        estado = (int) resultado[1];
                        captura = (boolean) resultado[2];

                        desplegarTablero();
                        if (!captura) {
                            pasosSinCaptura++;
                        } else {
                            pasosSinCaptura = 0;
                        }

                        turno = Tablero.jugadorOpuesto(turno);
                        
                    }


                    if (estado == Tablero.JUGADOR_NEGRO) {
                        //                    victoriasJugador0++;
                        message.setText("Ganador: " + jugadores[0]);
                    } else if (estado == Tablero.jugadorOpuesto(Tablero.JUGADOR_NEGRO)) {
                        message.setText("Ganador: " + jugadores[1]);
                        //                    perdidasJugador0++;
                        // 50-moves rule
                    } else if (estado != Tablero.JUEGO_CONTINUA) {
                        message.setText("Empate");
                        //                    empatesJugador0++;
                    }

                    desplegarTablero();
                }
            }
        });
        tools.add(botonSig);
        tools.addSeparator();
        tools.add(message);

        tools.addSeparator();
        tools.add(m1);

        tools.addSeparator();
        tools.add(prof1);

        tools.addSeparator();
        tools.add(m2);

        tools.addSeparator();
        tools.add(prof2);

        int squareSize = 70; // Tamaño deseado de las casillas

chessBoard = new JPanel(new GridLayout(0, 8));
chessBoard.setBorder(new CompoundBorder(
        new EmptyBorder(0, 0, 0, 0),
        new LineBorder(Color.BLACK)
));
Color ochre = new Color(248, 191, 60);
Color black = new Color(255, 255, 204);
Color white = new Color(81, 26, 11);
chessBoard.setBackground(ochre);
JPanel boardConstrain = new JPanel(new GridBagLayout());
boardConstrain.setBackground(ochre);
boardConstrain.add(chessBoard);
gui.add(boardConstrain);

Insets buttonMargin = new Insets(0, 0, 0, 0);
for (int i = 0; i < chessBoardSquares.length; i++) {
    for (int j = 0; j < chessBoardSquares[i].length; j++) {
        JButton b = new JButton();
        b.setMargin(buttonMargin);
        ImageIcon icon = new ImageIcon(new BufferedImage(squareSize, squareSize, BufferedImage.TYPE_INT_ARGB));
        b.setIcon(icon);
        if ((j % 2 == 1 && i % 2 == 1) || (j % 2 == 0 && i % 2 == 0)) {
            b.setBackground(white);
        } else {
            b.setBackground(black);
        }
        chessBoardSquares[i][j] = b;
        chessBoardSquares[i][j].setPreferredSize(new Dimension(squareSize, squareSize));
        chessBoard.add(chessBoardSquares[i][j]);
    }
}
        nuevoTablero();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                chessBoard.add(chessBoardSquares[i][j]);
            }
        }
    }

    boolean profIsOk(String s) {
        if (s.isEmpty()) return false;
        else {
            for (int i = 0; i < s.length(); i++)
                if (!Character.isDigit(s.charAt(i)))
                    return false;

            // Todos son digitos
            if (s.compareTo("0") == 0 || s.compareTo("1") == 0)
                return false;
        }
        return true;
    }


    private void cargarImagenes() {
        try {
            String[] image_names = {"peon_negro", "peon_blanco", "dama_negra", "dama_blanca", "fondo_negro", "fondo_blanco"};

            for (int i = 0; i < image_names.length; i++) {
                imagenesTablero[i] = ImageIO.read(new File("D:\\Ale U\\2023\\1er Semestre-2023\\Inteligencia Artificial\\IA_PP_Practica\\IA_1par\\damas\\src\\images\\" + image_names[i] + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void desplegarTablero() {

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                ImageIcon resizedIcon = new ImageIcon(imagenesTablero[tablero[i][j]].getScaledInstance(75, 75, Image.SCALE_SMOOTH));
        chessBoardSquares[i][j].setIcon(resizedIcon);
                // peon negro, peon_blanco, dama negra, dama blanca
                if (tablero[i][j] < 4) {
                    chessBoardSquares[i][j].setIcon(new ImageIcon(imagenesTablero[tablero[i][j]]));
                    // fondo blanco
                } else if ((j % 2 == 1 && i % 2 == 1) || (j % 2 == 0 && i % 2 == 0)) {
                    chessBoardSquares[i][j].setIcon(new ImageIcon(imagenesTablero[5]));
                    // fondo negro
                } else {
                    chessBoardSquares[i][j].setIcon(new ImageIcon(imagenesTablero[4]));
                }
            }
        }
    }

    private void nuevoTablero() {
        tablero = Tablero.crearTablero();
        pasosSinCaptura = 0;
        estado = Tablero.JUEGO_CONTINUA;
        message.setText("");
        desplegarTablero();
        gameStarted = false;
        turno = Tablero.JUGADOR_NEGRO;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Runnable r = () -> {
            TableroGUI cg = new TableroGUI();

            JFrame f = new JFrame("Damas");
            f.add(cg.gui);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setLocationByPlatform(true);

            f.pack();


            Dimension scDim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frmDim = new Dimension(720, 690);
            f.setBounds(scDim.width/2-frmDim.width/2,scDim.height/2-frmDim.height/2, frmDim.width, frmDim.height);
            f.setResizable(false);
            f.setMaximumSize(new Dimension(frmDim));
            f.setVisible(true);

        };
        SwingUtilities.invokeLater(r);
    }
}