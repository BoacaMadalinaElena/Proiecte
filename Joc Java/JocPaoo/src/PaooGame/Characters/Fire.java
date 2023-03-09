package PaooGame.Characters;

import PaooGame.Graphics.Assets;
import PaooGame.Variable;
import java.util.Random;

/*! \public class Fire extends Elements
    \brief Implementeaza notiunea de foc
 */
public class Fire extends Elements {

    /*! \fn public Fire(int x, int y, int width, int height, int widthRectangle, int heightRectanle,int sens)
        \brief Constructorul clasei
     */
    public Fire(int x, int y, int width, int height, int widthRectangle, int heightRectanle) {
        super(x, y, width, height, widthRectangle, heightRectanle);
    }

    /*! \fn public void Move()
        \brief Neutilizat
    */
    @Override
    public boolean Move() {
        return false;
    }

    /*! \fn public void Update()
        \brief Neutilizat
    */
    @Override
    public void Update() {
    }

    /*! \fn public void Draw()
        \brief Deseneaza focul
     */
    @Override
    public void Draw() {
        Random rand = new Random();
        Variable.g.drawImage(Assets.fire[rand.nextInt(4)], x, y, width, height, null);
    }
}


