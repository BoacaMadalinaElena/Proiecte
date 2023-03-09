package PaooGame.Graphics;

import java.awt.image.BufferedImage;

/*! \class public class SpriteSheet
    \brief Clasa retine o referinta catre o imagine formata din dale (sprite sheet)

    Clasa mai contine metode pentru a decupa dale din imagine -> toate fac acelasi lucru doar se schimba dimensiunile
 */
public class SpriteSheet
{
    private final BufferedImage spriteSheet;                    /*!< Referinta catre obiectul BufferedImage ce contine sprite sheet-ul.*/
    private static final int    tileWidth   = 48;               /*!< Latimea unei dale din sprite sheet.*/
    private static final int    tileHeight  = 48;               /*!< Inaltime unei dale din sprite sheet.*/
    private static final int    tileWidth1   = 200;             /*!< Latimea unui buton pentru meniu.*/
    private static final int    tileHeight1  = 60;              /*!< Inaltime unei buton pentru meniu.*/
    private static final int    tileWLife   = 180;              /*!< Latimea imagini pentru viata.*/
    private static final int    tileHLife  = 60;                /*!< Inaltime imagini pentru viata.*/

    /*! \fn public SpriteSheet(BufferedImage sheet)
        \brief Constructor, initializeaza spriteSheet.

        \param img Un obiect BufferedImage valid.
     */
    public SpriteSheet(BufferedImage buffImg)
    {
        /// Retine referinta catre BufferedImage object.
        spriteSheet = buffImg;
    }

    /*! \fn public BufferedImage crop(int x, int y)
        \brief Returneaza un obiect BufferedImage ce contine o subimage (dala).

        Subimaginea este localizata avand ca referinta punctul din stanga sus.

        \param x numarul dalei din sprite sheet pe axa x.
        \param y numarul dalei din sprite sheet pe axa y.
     */
    public BufferedImage crop(int x, int y)
    {
        /// Subimaginea (dala) este regasita in sprite sheet specificad coltul stanga sus
        /// al imaginii si apoi latimea si inaltimea (totul in pixeli). Coltul din stanga sus al imaginii
        /// se obtine inmultind numarul de ordine al dalei cu dimensiunea in pixeli a unei dale.
        return spriteSheet.getSubimage(x * tileWidth, y * tileHeight, tileWidth, tileHeight);
    }
    public BufferedImage cropButton(int x, int y)
    {
        return spriteSheet.getSubimage(x * tileWidth1, y * tileHeight1, tileWidth1, tileHeight1);
    }
    public BufferedImage cropLife(int x, int y)
    {
        return spriteSheet.getSubimage(x * tileWLife, y * tileHLife, tileWLife, tileHLife);
    }
    public BufferedImage cropCat(int x, int y)
    {
        return spriteSheet.getSubimage(x * 150, y *150, 150, 150);
    }
    public BufferedImage cropSnake(int x, int y)
    {
        return spriteSheet.getSubimage(x * 50, y *50, 50, 50);
    }
    public BufferedImage cropStartFinish(int x, int y)
    {
        return spriteSheet.getSubimage(0, 0, 200, 200);
    }
    public BufferedImage cropBullet(int x, int y)
    {
        return spriteSheet.getSubimage(x*100, y*100, 100, 100);
    }
    public BufferedImage cropRino(int x, int y)
    {
        return spriteSheet.getSubimage(x*150, y*150, 150, 150);
    }
    public BufferedImage cropSound(int x, int y)
    {
        return spriteSheet.getSubimage(x*130, y*60, 130, 60);
    }
}
