package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class OffTile
    \brief Butonul de oprire a sunetului
 */
public class OffTile extends Tile
{
    /*! \fn  public OnTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei nu este folosit
    */
    public OffTile(int id)
    {
        super(Assets.off, id);
    }

    /*! \fn public boolean IsSolid()
       \brief Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu .
    */
    @Override
    public boolean IsSolid()
    {
        return true;
    }
}
