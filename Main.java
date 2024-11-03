import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ventanaCarrera();
            }
        });
    }
}

class Hilo implements Runnable {
    Thread t;
    String nombre;
    JLabel personaje;
    JLabel labelFinal;
    JLabel finalGif;
    public static int lugar;
    volatile boolean paused;
    private final Object pauseLock = new Object();

    public Hilo(String nombre, JLabel personaje, JLabel labelFinal, JLabel finalGif) {
        this.nombre = nombre;
        this.personaje = personaje;
        this.labelFinal = labelFinal;
        this.finalGif = finalGif;

        t = new Thread(this, nombre);
        t.start();
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll();
        }
    }

    @Override
    public void run() {
        int retardo;

        try {
            retardo = (int) (Math.random() * 15) + 1;
            personaje.setVisible(true);
            for (int i = 50; i <= 500; i++) {
                personaje.setLocation(i, personaje.getY());

          
                synchronized (pauseLock) {
                    while (paused) {
                        pauseLock.wait();
                    }
                }

                Thread.sleep(retardo);
            }

            personaje.setVisible(true);
            labelFinal.setText(nombre + " Ha llegado a la posicion: " + lugar);
            labelFinal.setVisible(true);
            lugar++;

            if (lugar == 4) {
                finalGif.setVisible(true);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

class ventanaCarrera extends JFrame {
    private JLabel finalGifLabel;
    private JButton botonIniciarCarrera;
    private JButton botonPausarCarrera;
    private Hilo tmario, tbart, tflash;

    public ventanaCarrera() {
        super("----CARRERAS----");
        JLabel mario, flash, bart, mario_pos, flash_pos, bart_pos;
        
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FondoPanel panel = new FondoPanel();
        panel.setLayout(null);

        // Cargar imágenes desde las rutas proporcionadas
        Image imagen_mario = new ImageIcon("C:\\Users\\rosy\\Documents\\CarreraL\\mario.gif").getImage();
        ImageIcon icon_mario = new ImageIcon(imagen_mario.getScaledInstance(50, 50, Image.SCALE_DEFAULT));
        mario = new JLabel();
        mario.setIcon(icon_mario);
        mario.setBounds(50, 50, 50, 50);

        Image imagen_flash = new ImageIcon("C:\\Users\\rosy\\Documents\\CarreraL\\fls.gif").getImage();
        ImageIcon icon_flash = new ImageIcon(imagen_flash.getScaledInstance(50, 50, Image.SCALE_DEFAULT));
        flash = new JLabel();
        flash.setIcon(icon_flash);
        flash.setBounds(50, 150, 50, 50);

        Image imagen_bart = new ImageIcon("C:\\Users\\rosy\\Documents\\CarreraL\\bart.gif").getImage();
        ImageIcon icon_bart = new ImageIcon(imagen_bart.getScaledInstance(50, 50, Image.SCALE_DEFAULT));
        bart = new JLabel();
        bart.setIcon(icon_bart);
        bart.setBounds(50, 250, 50, 50);

        mario_pos = new JLabel();
        mario_pos.setBounds(50, 100, 350, 50);

        flash_pos = new JLabel();
        flash_pos.setBounds(50, 200, 350, 50);

        bart_pos = new JLabel();
        bart_pos.setBounds(50, 300, 350, 50);

        // Cargar y configurar el GIF final
        ImageIcon finalGifIcon = new ImageIcon("C:\\Users\\rosy\\Documents\\CarreraL\\feli.gif");
        finalGifLabel = new JLabel();
        finalGifLabel.setIcon(finalGifIcon);
        finalGifLabel.setBounds(400, 100, 300, 300);
        finalGifLabel.setVisible(false);

        botonIniciarCarrera = new JButton("Iniciar Carrera");
        botonIniciarCarrera.setBounds(600, 450, 150, 50);
        botonPausarCarrera = new JButton("Pausar/Reanudar");
        botonPausarCarrera.setBounds(600, 500, 150, 50);

        botonIniciarCarrera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarCarrera(mario, mario_pos, bart, bart_pos, flash, flash_pos);
            }
        });

        botonPausarCarrera.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pausarReanudarCarrera();
            }
        });

        panel.add(mario);
        panel.add(mario_pos);
        panel.add(bart);
        panel.add(bart_pos);
        panel.add(flash);
        panel.add(flash_pos);
        panel.add(botonIniciarCarrera);
        panel.add(botonPausarCarrera);
        panel.add(finalGifLabel);

        add(panel);
        setVisible(true);
    }

    private void iniciarCarrera(JLabel mario, JLabel mario_pos, JLabel bart, JLabel bart_pos, JLabel flash, JLabel flash_pos) {
        Hilo.lugar = 1;  // Reiniciar la posición
        finalGifLabel.setVisible(false);  // Ocultar el GIF final

        tmario = new Hilo("Mario", mario, mario_pos, finalGifLabel);
        tbart = new Hilo("Bart", bart, bart_pos, finalGifLabel);
        tflash = new Hilo("Flash", flash, flash_pos, finalGifLabel);
    }

    private void pausarReanudarCarrera() {
        if (tmario != null && tbart != null && tflash != null) {
            if (tmario.paused) {
                tmario.resume();
                tbart.resume();
                tflash.resume();
            } else {
                tmario.pause();
                tbart.pause();
                tflash.pause();
            }
        }
    }

    class FondoPanel extends JPanel {
        private Image imagen;

        public FondoPanel() {
            imagen = new ImageIcon("C:\\Users\\rosy\\Documents\\CarreraL\\meta.jpg").getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
