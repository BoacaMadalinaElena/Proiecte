package PaooGame.Input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*! \public class MouseInput extends MouseAdapter
    \brief Trateaza evenimentele de mouse -> apasare click stang pentru selectie , coordonate mouse(trebuie sa stiu pe ce apas )

    Clasa retine daca a fost apasat click pe butonul stang si coordonatele actuale ale mouse-ului
 */
public class MouseInput extends MouseAdapter {
    private static int x, y;             /*!< Referinte catre cele doua coordonate ale mouse-ului.*/
    public boolean clickRight;           /*!< Referinta catre starea butonului stang.*/

    /*! \fn public void mousePressed(MouseEvent e)
        \brief modifica clickLeft il pune pe 1 daca am apasat butonul stang
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            clickRight = true;
    }

    /*! \fn public void mouseReleased(MouseEvent e)
        \brief brief modifica clickLeft il pune pe 1 daca daca butonul stang nu mai este apasat
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            clickRight = false;
    }

    /*! \fn public void mouseMoved(MouseEvent e)
        \brief seteaza noile coordonate ale mouse-ului
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }
    public int getX() {
        return  x;
    }
    public int getY() {
        return  y;
    }
}
