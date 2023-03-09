package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class WoodVerticalTile
    \brief Abstractizeaza notiunea de dala de tip lemn.
 */
public class WoodVerticalTile extends Tile
{
    /*! \fn  public WoodVerticalTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public WoodVerticalTile(int id)
    {
        super(Assets.woodVertical, id);
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
