namespace DomainLayer.Models
{
    public class Category : BaseEntity
    {
        public string Name { get; set; }
        public int ArticleId { get; set; }
        public Article Article { get; set; }
        
        public Category(string name)
        {
            Name = name;
        }
    }
}