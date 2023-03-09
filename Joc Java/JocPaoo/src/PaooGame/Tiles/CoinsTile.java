package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class CoinsTile
    \brief Abstractizeaza notiunea de dala de tip banut(e si element activ).
 */
public class CoinsTile extends Tile
{
    /*! \fn public CoinsTile(int id)
       \brief Constructorul de initializare al clasei

       \param id Id-ul dalei util in desenarea hartii.
    */
    public CoinsTile(int id)
    {
        super(Assets.coins, id);
    }

    /*! \fn public boolean IsSolid()
       \brief Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu .
    */
    @Override
    public boolean IsSolid()
    {
        return false ;
    }                               //il inchid pentru coliziune il i-au separat altfel probleme la deplasare
}
