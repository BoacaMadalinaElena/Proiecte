package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class OnTile
    \brief Butonul de pornire a sunetului
 */
public class OnTile extends Tile
{
    /*! \fn  public OnTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei nu este folosit
    */
    public OnTile(int id)
    {
        super(Assets.on, id);
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
