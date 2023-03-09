package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class WoodHorizontalTile
    \brief Abstractizeaza notiunea de dala de tip lemn.
 */
public class WoodHorizontalTile extends Tile
{
    /*! \fn public WoodHorizontalTile(int id)
       \brief Constructorul de initializare al clasei

       \param id : Id-ul dalei util in desenarea hartii.
    */
    public WoodHorizontalTile(int id)
    {
        super(Assets.woodHorizontal, id);
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
