namespace DomainLayer.Models
{
    public class ArticleImage : BaseEntity
    {
        public string Content { get; set; }

        // Legatura
        public int ArticleId { get; set; }
        public Article Article { get; set; }

        public ArticleImage(string content)
        {
            Content = content;
        }
        public ArticleImage() { }
    }
}
