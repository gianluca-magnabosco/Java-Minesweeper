package minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import minesweeper.ImprovedButton.ButtonState;
import minesweeper.ImprovedButton.ButtonType;

/**
 *
 * @author Gianluca Notari Magnabosco da Silva - GRR20211621
 */
public class MineSweeperGUI extends JFrame implements ActionListener {
    
    Font counterTextFont;
    
    private int height;
    private int width;
    private int bombs;
    private int flags;
    
    private int oldHeight;
    private int oldWidth;
    private int currentBombs;
    private int currentFlags;
    
    private boolean gameEnded;
    
    private ImprovedButton[][] buttonMatrix;
    private int[][] valueMatrix;
    
    
    public MineSweeperGUI() {
        initComponents();
        setUpEventListeners();
        setTextFieldFont();
        setGameDifficulty("Easy");
        startGame(false);
    }

    
    private void setBorderLayout(int borderLayoutHeight) {
        topPanel.setLayout(new BorderLayout(borderLayoutHeight, 0));
        topPanel.add(bombCounterTextField, BorderLayout.WEST);
        topPanel.add(startButton, BorderLayout.CENTER);
        topPanel.add(flagCounterTextField, BorderLayout.EAST);
        
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    
    private void setUpEventListeners() {
        startButton.addActionListener(this);
        easyMenuItem.addActionListener(this);
        mediumMenuItem.addActionListener(this);
        advancedMenuItem.addActionListener(this);
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!e.getActionCommand().equals("startButton")) {
            setGameDifficulty(e.getActionCommand());
        } 
        
        startGame(true);
    }
    
    
    private void setTextFieldFont() {
        try {
            counterTextFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/font/digital-7.ttf")).deriveFont(46f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/font/digital-7.ttf")));
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(MineSweeperGUI.class.getName()).log(Level.SEVERE, null, ex);
            try {
                counterTextFont = Font.createFont(Font.TRUETYPE_FONT, new File("digital-7.ttf")).deriveFont(46f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("digital-7.ttf")));
            } catch (FontFormatException | IOException ex2) {
                Logger.getLogger(MineSweeperGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        bombCounterTextField.setFont(counterTextFont);
        flagCounterTextField.setFont(counterTextFont);
    }
 
    
    private void setGameDifficulty(String difficulty) {
        switch (difficulty) {
            case "Easy" -> {
                this.width = 9;
                this.height = 9;
                this.flags = 10;
                this.bombs = 10;
                setSize(300, 400);
                setBorderLayout(31);
                this.setLocationRelativeTo(null);
            }
            
            case "Medium" -> {
                this.width = 16;
                this.height = 16;
                this.flags = 40;
                this.bombs = 40;
                setSize(500, 620);
                setBorderLayout(132);
                this.setLocationRelativeTo(null);
            }
            
            case "Advanced" -> {
                this.width = 30;
                this.height = 16;
                this.flags = 99;
                this.bombs = 99;
                setSize(950, 620);
                setBorderLayout(356);
                this.setLocationRelativeTo(null);
            }
            
            default -> {}
        }
    }
    
    
    private void startGame(boolean deleteButtons) {
        if (deleteButtons == true) {
            deleteCurrentButtons();
        }
        
        gameEnded = false;
        
        oldHeight = height;
        oldWidth = width;
        
        setGridLayout();
        startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/smiley_face.jpg")));
        setUpMatrices();
        setUpCounters();

        mineSweeperPanel.validate();
        mineSweeperPanel.repaint();
    }
    
    
    private void deleteCurrentButtons() {
        for (int i = 0; i < oldHeight; i++) {
            for (int j = 0; j < oldWidth; j++) {
                mineSweeperPanel.remove(buttonMatrix[i][j]);
                mineSweeperPanel.validate();
                mineSweeperPanel.repaint();
            }
        }
    }
    
    
    private void setGridLayout() {
        GridLayout grid = new GridLayout(height, width);
        mineSweeperPanel.setLayout(grid);
        mineSweeperPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
    }
    
    
    private void setUpMatrices() {
        initButtonMatrix();
        createValuesMatrix();
        shuffleMatrix();
        setUpBombs();
        setUpCellValues();
    }
    
    
    private void initButtonMatrix() {
        buttonMatrix = new ImprovedButton[height][width];
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ImprovedButton button = new ImprovedButton(i, j);
                buttonMatrix[i][j] = button;
                buttonMatrix[i][j].addMouseListener(new MouseHandler());         
                mineSweeperPanel.add(buttonMatrix[i][j]);
            }
        }  
    }
    
    
    public final void createValuesMatrix() {
        valueMatrix = new int[height][width];
        currentBombs = bombs;
        
        for (int i = 0; i < height - 1; i++) {
            for (int j = 0; j < width - 1; j++) {
                valueMatrix[i][j] = 0;
                if (currentBombs > 0) {
                    valueMatrix[i][j] = 99;
                    currentBombs--;
                }
            }
        }
    }
    
    
    private void shuffleMatrix() {
        Random random = new Random();
        
        for (int i = valueMatrix.length - 1; i >= 0; i--) {
            for (int j = valueMatrix[i].length - 1; j >= 0; j--) {
                int m = random.nextInt(i + 1);
                int n = random.nextInt(j + 1);
                
                int temp = valueMatrix[i][j];
                valueMatrix[i][j] = valueMatrix[m][n];
                valueMatrix[m][n] = temp;
            }
        }
    }
    
    
    private void setUpBombs() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (valueMatrix[i][j] == 99) {
                    buttonMatrix[i][j].setButtonType(ButtonType.BOMB);
                }
            }  
        }
    }
    
    
    private void setUpCellValues() {
        int count;
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                count = 0;
                
                if (buttonMatrix[row][col].getButtonType() == ButtonType.BOMB) {
                    continue;
                }
                
                // TOP LEFT
                if (cellIsValid(row - 1, col - 1) == true) {
                    if (cellHasBomb(row - 1, col - 1) == true) {
                        count++;
                    }
                }
                
                // TOP
                if (cellIsValid(row - 1, col) == true) {
                    if (cellHasBomb(row - 1, col) == true) {
                        count++;
                    }
                }
                
                // TOP RIGHT
                if (cellIsValid(row - 1, col + 1) == true) {
                    if (cellHasBomb(row - 1, col + 1) == true) {
                        count++;
                    }
                }
                
                // LEFT
                if (cellIsValid(row, col - 1) == true) {
                    if (cellHasBomb(row, col - 1) == true) {
                        count++;
                    }
                } 
                
                // RIGHT
                if (cellIsValid(row, col + 1) == true) {
                    if (cellHasBomb(row, col + 1) == true) {
                        count++;
                    }
                } 
                
                // BOTTOM LEFT
                if (cellIsValid(row + 1, col - 1) == true) {
                    if (cellHasBomb(row + 1, col - 1) == true) {
                        count++;
                    }
                }
                
                // BOTTOM
                if (cellIsValid(row + 1, col) == true) {
                    if (cellHasBomb(row + 1, col) == true) {
                        count++;
                    }
                }                
                
                // BOTTOM RIGHT
                if (cellIsValid(row + 1, col + 1) == true) {
                    if (cellHasBomb(row + 1, col + 1) == true) {
                        count++;
                    }
                } 
                

                if (count > 0) {
                    valueMatrix[row][col] = count;
                    buttonMatrix[row][col].setButtonType(ButtonType.NUMBER);
                }  
            }
        }
    }
    
    private boolean cellIsValid(int posX, int posY) {
        return (posX >= 0) && (posX < height) && (posY >= 0) && (posY < width);
    }
    
    
    private boolean cellHasBomb(int posX, int posY) {
        return (buttonMatrix[posX][posY].getButtonType() == ButtonType.BOMB);
    }   
    
    
    private void setUpCounters() {
        currentBombs = bombs;
        currentFlags = flags;
        bombCounterTextField.setText(String.format("%03d", bombs));
        flagCounterTextField.setText(String.format("%03d", flags));
    }
      
    
    private void revealEmptyCells(int posX, int posY) {
        if (!cellIsValid(posX, posY) || buttonMatrix[posX][posY].getButtonState() == ButtonState.PRESSED) {
            return;
        }
        
        if (buttonMatrix[posX][posY].getButtonState() == ButtonState.FLAGGED) { 
            currentFlags++;
            buttonMatrix[posX][posY].setIcon(null);
            flagCounterTextField.setText(String.format("%03d", currentFlags));
        }
        
        buttonMatrix[posX][posY].setEnabled(false);
        buttonMatrix[posX][posY].setBackground(new Color(0xB9B9B9));
        buttonMatrix[posX][posY].setButtonState(ButtonState.PRESSED);
        
        if (buttonMatrix[posX][posY].getButtonType() == ButtonType.NUMBER) {
            int valor = valueMatrix[posX][posY];
            buttonMatrix[posX][posY].setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/" + Integer.toString(valor) + ".png")));
            buttonMatrix[posX][posY].setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/image/" + Integer.toString(valor) + ".png")));
            return;
        }
        
        if (buttonMatrix[posX][posY].getButtonType() == ButtonType.EMPTY) {
            revealEmptyCells(posX - 1, posY - 1);
            revealEmptyCells(posX - 1, posY);
            revealEmptyCells(posX - 1, posY + 1);
            revealEmptyCells(posX, posY - 1);
            revealEmptyCells(posX, posY + 1);
            revealEmptyCells(posX + 1, posY + 1);
            revealEmptyCells(posX + 1, posY);
            revealEmptyCells(posX + 1, posY - 1);
        } 
    }
    
    
    private boolean wonTheGame() {
        int count = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (buttonMatrix[i][j].getButtonState() == ButtonState.FLAGGED && buttonMatrix[i][j].getButtonType() == ButtonType.BOMB) {
                    count++;
                }
            }
        }
        
        return count == currentBombs;
    }
    
    
    private void changeFlagsToGreen() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (buttonMatrix[i][j].getButtonState() == ButtonState.FLAGGED) {
                    buttonMatrix[i][j].setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/green_flag.png")));
                }
            }
        }
    }
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        bombCounterTextField = new javax.swing.JTextField();
        flagCounterTextField = new javax.swing.JTextField();
        startButton = new javax.swing.JButton();
        mineSweeperPanel = new javax.swing.JPanel();
        gameMenuBar = new javax.swing.JMenuBar();
        gameMenu = new javax.swing.JMenu();
        easyMenuItem = new javax.swing.JMenuItem();
        mediumMenuItem = new javax.swing.JMenuItem();
        advancedMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Campo Minado");
        setResizable(false);

        bombCounterTextField.setEditable(false);
        bombCounterTextField.setBackground(new java.awt.Color(51, 51, 51));
        bombCounterTextField.setFont(new java.awt.Font("Courier New", 1, 36)); // NOI18N
        bombCounterTextField.setForeground(new java.awt.Color(255, 0, 0));
        bombCounterTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        bombCounterTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(242, 242, 242), 3));
        bombCounterTextField.setMargin(new java.awt.Insets(5, 10, 5, 10));
        bombCounterTextField.setPreferredSize(new java.awt.Dimension(75, 50));

        flagCounterTextField.setEditable(false);
        flagCounterTextField.setBackground(new java.awt.Color(51, 51, 51));
        flagCounterTextField.setFont(new java.awt.Font("SansSerif", 1, 36)); // NOI18N
        flagCounterTextField.setForeground(new java.awt.Color(255, 0, 0));
        flagCounterTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        flagCounterTextField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(242, 242, 242), 3));
        flagCounterTextField.setPreferredSize(new java.awt.Dimension(75, 50));

        startButton.setActionCommand("startButton");
        startButton.setMaximumSize(new java.awt.Dimension(6, 6));
        startButton.setMinimumSize(new java.awt.Dimension(6, 6));
        startButton.setPreferredSize(new java.awt.Dimension(50, 50));

        javax.swing.GroupLayout topPanelLayout = new javax.swing.GroupLayout(topPanel);
        topPanel.setLayout(topPanelLayout);
        topPanelLayout.setHorizontalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topPanelLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(bombCounterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 308, Short.MAX_VALUE)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(250, 250, 250)
                .addComponent(flagCounterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );
        topPanelLayout.setVerticalGroup(
            topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(flagCounterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bombCounterTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(topPanelLayout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );

        getContentPane().add(topPanel, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout mineSweeperPanelLayout = new javax.swing.GroupLayout(mineSweeperPanel);
        mineSweeperPanel.setLayout(mineSweeperPanelLayout);
        mineSweeperPanelLayout.setHorizontalGroup(
            mineSweeperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 792, Short.MAX_VALUE)
        );
        mineSweeperPanelLayout.setVerticalGroup(
            mineSweeperPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 492, Short.MAX_VALUE)
        );

        getContentPane().add(mineSweeperPanel, java.awt.BorderLayout.CENTER);

        gameMenu.setText("Jogo");

        easyMenuItem.setText("Fácil");
        easyMenuItem.setActionCommand("Easy");
        gameMenu.add(easyMenuItem);

        mediumMenuItem.setText("Médio");
        mediumMenuItem.setActionCommand("Medium");
        gameMenu.add(mediumMenuItem);

        advancedMenuItem.setText("Avançado");
        advancedMenuItem.setActionCommand("Advanced");
        gameMenu.add(advancedMenuItem);

        gameMenuBar.add(gameMenu);

        setJMenuBar(gameMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem advancedMenuItem;
    private javax.swing.JTextField bombCounterTextField;
    private javax.swing.JMenuItem easyMenuItem;
    private javax.swing.JTextField flagCounterTextField;
    private javax.swing.JMenu gameMenu;
    private javax.swing.JMenuBar gameMenuBar;
    private javax.swing.JMenuItem mediumMenuItem;
    private javax.swing.JPanel mineSweeperPanel;
    private javax.swing.JButton startButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    
    
    private class MouseHandler extends MouseAdapter {
        private String mouseButtonPressed = "";
        
        @Override
        public void mouseClicked(MouseEvent e) {
            
            switch (e.getButton()) {
                case MouseEvent.BUTTON1 -> mouseButtonPressed = "LEFT";
                case MouseEvent.BUTTON3 -> mouseButtonPressed = "RIGHT";
                default -> {
                    return;
                }
            }
            
            ImprovedButton button = (ImprovedButton) e.getSource();
            
            if (button.getButtonState() == ButtonState.PRESSED || gameEnded == true) {
                return;
            }
            
            
            if (mouseButtonPressed.equals("RIGHT")) { 
                if (button.getButtonState() != null) switch (button.getButtonState()) {
                    case ACTIVE -> {
                        if (currentFlags == 0) {
                            return;
                        }
                        
                        currentFlags--;
                        flagCounterTextField.setText(String.format("%03d", currentFlags));
                        button.setButtonState(ButtonState.FLAGGED);
                        button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/flag.png")));
                        
                        if (wonTheGame()) {
                            changeFlagsToGreen();
                            gameEnded = true;
                            startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/sunglasses.png")));
                            JOptionPane.showMessageDialog(null, "Parabéns!\nVocê Venceu!!!", "Game Over", JOptionPane.INFORMATION_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/image/sunglasses.png")));
                        }
                    }
                    
                    case FLAGGED -> {
                        currentFlags++;
                        flagCounterTextField.setText(String.format("%03d", currentFlags));
                        button.setButtonState(ButtonState.ACTIVE);
                        button.setIcon(null);
                    }
                    
                    default -> {}
                }
            }
            
            
            if (mouseButtonPressed.equals("LEFT")) {
                if (button.getButtonState() == ButtonState.ACTIVE) {
                    
                    button.setEnabled(false);
                    button.setBackground(new Color(0xB9B9B9));
                    
                    if (button.getButtonType() != null) switch (button.getButtonType()) {
                        case BOMB -> {
                            gameOver();
                            gameEnded = true;
                            button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/exploded_mine.png")));
                            button.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/image/exploded_mine.png")));
                            JOptionPane.showMessageDialog(null, "Game Over!\nVocê Perdeu!", "Game Over", JOptionPane.INFORMATION_MESSAGE, new javax.swing.ImageIcon(getClass().getResource("/image/dead_face.jpg")));
                        }
                        
                        case NUMBER -> {
                            int valor = valueMatrix[button.getPosX()][button.getPosY()];
                            button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/" + Integer.toString(valor) + ".png")));
                            button.setDisabledIcon(new javax.swing.ImageIcon(getClass().getResource("/image/" + Integer.toString(valor) + ".png")));
                        }
                        
                        case EMPTY -> revealEmptyCells(button.getPosX(), button.getPosY());
                        
                        default -> {}
                    }
                    
                    button.setButtonState(ButtonState.PRESSED);
                    mineSweeperPanel.repaint();
                }
            }
        }
        
        
        private void gameOver() {
            startButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/dead_face.jpg")));
            
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (buttonMatrix[i][j].getButtonType() == ButtonType.BOMB) {
                        if (buttonMatrix[i][j].getButtonState() == ButtonState.FLAGGED) {
                            buttonMatrix[i][j].setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/green_flag.png")));
                            continue;
                        }
                        
                        buttonMatrix[i][j].setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/mine.png")));
                    }
                }
            }
        }
    }  
}
