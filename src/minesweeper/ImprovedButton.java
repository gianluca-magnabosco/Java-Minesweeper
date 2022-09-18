package minesweeper;

import java.awt.Dimension;
import javax.swing.JButton;

/**
 *
 * @author Gianluca Notari Magnabosco da Silva - GRR20211621
 */
public class ImprovedButton extends JButton {
    
    public enum ButtonState {
        ACTIVE, PRESSED, FLAGGED
    };
    
    public enum ButtonType {
        EMPTY, NUMBER, BOMB
    };
    
    private int posX, posY, value = 0;
    private ButtonState state;
    private ButtonType type;
    
    public ImprovedButton(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        this.state = ButtonState.ACTIVE;
        this.type = ButtonType.EMPTY;
        
        this.setMinimumSize(new Dimension(3, 3));
    }
    
    public int getPosX() {
        return posX;
    }
    
    public int getPosY() {
        return posY;
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public ButtonState getButtonState() {
        return state;
    }
    
    public void setButtonState(ButtonState state) {
        this.state = state;
    }
    
    public ButtonType getButtonType() {
        return type;
    }
    
    public void setButtonType(ButtonType type) {
        this.type = type;
    }
}
