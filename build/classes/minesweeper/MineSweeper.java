package minesweeper;

/**
 *
 * @author Gianluca Notari Magnabosco da Silva - GRR20211621
 */
public class MineSweeper {
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new MineSweeperGUI().setVisible(true);
        });
    }
}
