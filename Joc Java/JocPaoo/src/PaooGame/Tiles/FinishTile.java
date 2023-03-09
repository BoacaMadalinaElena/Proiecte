package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class FinishTile
    \brief Abstractizeaza notiunea de dala de tip finish(cea care se incarca la finalul unui nivel).
 */
public class FinishTile extends Tile
{
    /*! \fn public FinishTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public FinishTile(int id)
    {
        super(Assets.finish, id);
    }

    /*! \fn public boolean IsSolid()
       \brief Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu .
    */
    @Override
    public boolean IsSolid()
    {
        return true ;
    }                               //il inchid pentru coliziune il i-au separat altfel probleme la deplasare
}
