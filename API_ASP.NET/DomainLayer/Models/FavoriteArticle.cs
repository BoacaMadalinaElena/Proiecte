namespace DomainLayer.Models
{
    public class FavoriteArticle : BaseEntity
    {
        public Article Article { get; set; }
        public User User { get; set; }

        //Nu reuseste sa faca la get si sa populeze automat Article - Framework-ul nu face mereu automat join
        public int ArticleId { get; set; }
        public FavoriteArticle() { }

        public FavoriteArticle(Article article, User user)
        {
            Article = article;
            User = user;
        }
    }
}
