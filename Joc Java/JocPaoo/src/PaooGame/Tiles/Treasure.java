package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class Treasure
    \brief Abstractizeaza notiunea de dala de tip comanda.
 */
public class Treasure extends Tile
{
    /*! \fn public Treasure(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public Treasure(int id)
    {
        super(Assets.cufar, id);
    }

    /*! \fn public boolean IsSolid()
       \brief  Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu.
    */
    @Override
    public boolean IsSolid()
    {
        return true;
    }
}
