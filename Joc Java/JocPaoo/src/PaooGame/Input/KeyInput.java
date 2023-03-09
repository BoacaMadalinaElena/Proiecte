package PaooGame.Input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*! \class public class KeyInput implements KeyListener
    \brief Definire butoane taste
*/

public class KeyInput implements KeyListener {
    public boolean space, left, right, enter, esc, down, v,up,delete;                  /*!< Referinte catre statusul butoanelor.*/

    /*! \fn public KeyInput() initializeaza flag-urile
        \brief Constructor => initial nu am taste apasate
     */
    public KeyInput() {
        space = left = right = enter = esc = down = v =up = delete =  false;
    }

    /*! \fn public void keyPressed(KeyEvent e)
       \brief Supsrascrierea functiei din KeyListener -> eveniment apasare tasta

        \param Seteaza in consecinta flag-urile pentru taste
    */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();                              //codul tastei apasate
        switch (code) {
            case KeyEvent.VK_SPACE:
                space = true;
                break;
            case KeyEvent.VK_RIGHT:
                right = true;
                break;
            case KeyEvent.VK_LEFT:
                left = true;
                break;
            case KeyEvent.VK_ESCAPE:
                esc = true;
                break;
            case KeyEvent.VK_DOWN:
                down = true;
                break;
            case KeyEvent.VK_V:
                v = true;
                break;
            case KeyEvent.VK_ENTER:
                enter = true;
                break;
            case KeyEvent.VK_UP:
                up = true;
                break;
            case KeyEvent.VK_DELETE:
                delete = true;
                break;
            //nu ma intereseaza alte taste
        }
    }

    /*! \fn public void keyReleased(KeyEvent e)
       \brief Functie resetare flag-uri la eliberare taste
    */
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();                              //codul tastei care nu mai este apasata
        switch (code) {
            case KeyEvent.VK_SPACE:
                space = false;
                break;
            case KeyEvent.VK_RIGHT:
                right = false;
                break;
            case KeyEvent.VK_ENTER:
                enter = false;
                break;
            case KeyEvent.VK_LEFT:
                left = false;
                break;
            case KeyEvent.VK_ESCAPE:
                esc = false;
                break;
            case KeyEvent.VK_DOWN:
                down = false;
                break;
            case KeyEvent.VK_V:
                v = false;
                break;
            case KeyEvent.VK_UP:
                up = false;
                break;
            case KeyEvent.VK_DELETE:
                delete = false;
                break;
            //nu ma intereseaza alte taste
        }
    }

    /*! \fn public void keyTyped(KeyEvent e)
        \brief Functia nu are corp(nu e folosita) dar trebuie definita ca vida pentru a avea clasa concreta , altfel ramane abstracta !!!
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }
}
