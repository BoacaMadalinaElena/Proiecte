package PaooGame.Tiles;
import PaooGame.Graphics.Assets;

/*! \class public class CreateButtonTile extends Tile
    \brief Creaza un buton pentru load
 */
public class CreateButton1Tile extends Tile
{
    /*! \fn public CreateButtonTile(int id)
       \brief public Up1Tile(int id)

       \param id :  Nu este folosit(compatibilitate).
    */
    public CreateButton1Tile(int id)
    {
        super(Assets.create1, id);
    }

    /*! \fn public boolean IsSolid()
       \brief  Suprascrie metoda IsSolid() stabileste daca obiectul face coliziune sau nu.
    */
    @Override
    public boolean IsSolid()
    {
        return false;
    }
}
