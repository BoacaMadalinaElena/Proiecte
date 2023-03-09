package PaooGame;

import PaooGame.Characters.Elements;
import java.awt.*;
import java.util.Vector;

/*! \class public class Variable
    \brief Retine referinte statice catre obiecte utilizate des in joc
 */

public class Variable {
    public static Game game;                                    /*!< Referinta spre joc*/
    public static Graphics g;                                   /*!< Referinta catre contextul grafic.*/
    public static Vector<Elements> gloante;                     /*!< Referinta spre vectorul de gloante */
    public static int nrGloante;                                /*!< Referinta catre numarul de gloante */
}
